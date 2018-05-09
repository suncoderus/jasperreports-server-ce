/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.logging.access.service.impl;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEventImpl;
import com.jaspersoft.jasperserver.api.logging.access.domain.hibernate.RepoAccessEvent;
import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AccessServiceImpl extends HibernateDaoImpl implements AccessService, PersistentObjectResolver {

    private HibernateRepositoryService hibernateRepositoryService;
    private PersistentObjectResolver persistentUserResolver;
    private int maxAccessEventAge;
    private ResourceFactory persistentClassFactory;
    private ResourceFactory clientClassFactory;
    public static final String COMMAND_OUT_LOGGER = "com.jaspersoft.jasperserver.export.command";

    public void setMaxAccessEventAge(int maxAccessEventAge) {
        this.maxAccessEventAge = maxAccessEventAge;
    }

    public void setHibernateRepositoryService(HibernateRepositoryService hibernateRepositoryService) {
        this.hibernateRepositoryService = hibernateRepositoryService;
    }

    public void setPersistentUserResolver(PersistentObjectResolver persistentUserResolver) {
        this.persistentUserResolver = persistentUserResolver;
    }

    public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
        this.persistentClassFactory = persistentClassFactory;
    }

    public void setClientClassFactory(ResourceFactory clientClassFactory) {
        this.clientClassFactory = clientClassFactory;
    }

    public Object getPersistentObject(Object clientObject) {
        if (clientObject == null) {
            return null;
        } else if (clientObject instanceof Resource) {
            return hibernateRepositoryService.getRepoResource((Resource)clientObject);
        } else if (clientObject instanceof User) {
            return persistentUserResolver.getPersistentObject(clientObject);
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveEvent(LoggableEvent loggableEvent) {
        AccessEvent accessEvent = (AccessEvent) loggableEvent;
        if (hibernateRepositoryService.resourceExists(null, accessEvent.getResource().getURI(), accessEvent.getResource().getClass())) {
            RepoAccessEvent repoAccessEvent = new RepoAccessEvent();
            repoAccessEvent.copyFromClient(loggableEvent, this);
            getHibernateTemplate().save(repoAccessEvent);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveEvents(List<LoggableEvent> loggableEvents) {
        if (loggableEvents == null || loggableEvents.isEmpty()) {
            return;
        }
        
        //This code will collapse all events for a specific user and uri into one event
        //to reduce the number of database calls that are caused by multiple events for
        //same resource in the same http request
        //see Bug 35570 - [case 42185] Hibernate generating thousands of queries from dashboard 
        Map<List<String>,AccessEventImpl> map = new HashMap<List<String>,AccessEventImpl>();
        for (LoggableEvent loggableEvent: loggableEvents) {
        	if (loggableEvent instanceof AccessEventImpl) {
        		AccessEventImpl e = (AccessEventImpl)loggableEvent;
        		List<String> key = new ArrayList<String>(2);
        		key.add(e.getUser().getUsername()+"|"+e.getUser().getTenantId());
        		key.add(e.getResource().getURI());
        		AccessEventImpl v = map.get(key);
        		if (v==null) {
        			map.put(key, e.clone());
        		} else {
        			if (e.isUpdating())
        				v.setUpdating(true);
        		}
        	} else {
        		saveEvent(loggableEvent);
        	}
        }
        
        for (AccessEventImpl e : map.values()) {
        	saveEvent(e);
        }
    }

    public List<AccessEvent> getAllEvents(int firstResult, int maxResults) {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(persistentClassFactory.getImplementationClass(AccessEvent.class));
        criteria.addOrder(Order.asc("eventDate"));

        List results = getHibernateTemplate().findByCriteria(criteria, firstResult, maxResults);

        if (results != null && !results.isEmpty()) {
            List<AccessEvent> clientEventsList = new ArrayList<AccessEvent>(results.size());
            Log logger = LogFactory.getLog(COMMAND_OUT_LOGGER);
            for (Object event: results) {
                try {
                AccessEvent accessEvent = (AccessEvent) ((IdedObject) event).toClient(clientClassFactory);
                clientEventsList.add(accessEvent);
                } catch (AccessEventImpl.TranslateException aex) {
                    logger.debug("Unable to translate access event:  resource = " + aex.getResource().getClass() + ", factory = " + aex.getResourceFactory().getClass(), aex.getOriginalException());
                } catch (Exception ex) {
                    logger.debug("Unable to translate access event", ex);
                }
            }
            return clientEventsList;
        } else {
            return Collections.emptyList();
        }
    }

    public int getAccessEventsCount() {
        DetachedCriteria criteria =
                DetachedCriteria.forClass(persistentClassFactory.getImplementationClass(AccessEvent.class));
        criteria.setProjection(Projections.rowCount());
        return (Integer) getHibernateTemplate().findByCriteria(criteria).get(0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void purgeAccessEvents() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -maxAccessEventAge);
        Date last = cal.getTime();
        
        getHibernateTemplate().bulkUpdate("delete RepoAccessEvent e where e.eventDate < ?", last);
    }
}
