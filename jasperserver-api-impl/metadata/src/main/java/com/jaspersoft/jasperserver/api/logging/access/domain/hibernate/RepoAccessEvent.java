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
package com.jaspersoft.jasperserver.api.logging.access.domain.hibernate;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEventImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class RepoAccessEvent implements IdedObject {
    private long id;
    private RepoUser user;
    private Date eventDate;
    private RepoResource resource;
    private boolean updating;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RepoUser getUser() {
        return user;
    }

    public void setUser(RepoUser user) {
        this.user = user;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public RepoResource getResource() {
        return resource;
    }

    public void setResource(RepoResource resource) {
        this.resource = resource;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public Object toClient(ResourceFactory clientMappingFactory) {
        AccessEvent accessEvent = (AccessEvent)clientMappingFactory.newObject(AccessEvent.class);
        accessEvent.setUser((User)getUser().toClient(clientMappingFactory));
        accessEvent.setEventDate(getEventDate());
        try {
            accessEvent.setResource((Resource)(getResource()).toClient(clientMappingFactory));
        } catch (Exception ex) {
            throw new AccessEventImpl.TranslateException(accessEvent, ((Resource)getResource()), clientMappingFactory, ex);
        }
        accessEvent.setUpdating(isUpdating());
        return accessEvent;
    }



    public void copyFromClient(Object objIdent, PersistentObjectResolver resolver) {
        AccessEvent accessEvent = (AccessEvent)objIdent;
        RepoUser repoUser = (RepoUser)resolver.getPersistentObject(accessEvent.getUser());
        setUser(repoUser);
        setEventDate(accessEvent.getEventDate());
        RepoResource repoResource = (RepoResource)resolver.getPersistentObject(accessEvent.getResource()); 
        setResource(repoResource);
        setUpdating(accessEvent.isUpdating());
    }
}
