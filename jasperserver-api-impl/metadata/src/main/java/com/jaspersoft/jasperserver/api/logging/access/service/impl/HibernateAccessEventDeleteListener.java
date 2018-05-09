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

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateBeforeDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.logging.access.domain.hibernate.RepoAccessEvent;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.EventSource;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Iterator;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class HibernateAccessEventDeleteListener implements HibernateBeforeDeleteListener {

    public void beforeDelete(DeleteEvent event) {
        Object o = event.getObject();
        if (o instanceof RepoUser) {
            deleteAccessEventsByUser((RepoUser) o, event.getSession());
        }
    }

    protected void deleteAccessEventsByUser(RepoUser user, EventSource session) {
        Criteria criteria = session.createCriteria(RepoAccessEvent.class);
        criteria.add(Restrictions.eq("user", user));
        List accessEvents = criteria.list();
        if (accessEvents != null && !accessEvents.isEmpty()) {
            for (Iterator it = accessEvents.iterator(); it.hasNext();) {
                RepoAccessEvent accessEvent = (RepoAccessEvent) it.next();
                session.delete(accessEvent);
            }
        }
    }

}
