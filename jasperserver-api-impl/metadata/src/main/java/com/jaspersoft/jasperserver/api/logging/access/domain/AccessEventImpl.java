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
package com.jaspersoft.jasperserver.api.logging.access.domain;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessEventImpl implements AccessEvent, Cloneable {
    private User user;
    private Date eventDate;
    private boolean updating;
    private Resource resource;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public static class TranslateException extends IllegalArgumentException {

        private AccessEvent accessEvent;
        private Resource resource;
        private ResourceFactory resourceFactory;
        private Exception exception;

        public TranslateException(AccessEvent accessEvent, Resource resource, ResourceFactory resourceFactory, Exception exception) {
            super(exception);
            this.accessEvent = accessEvent;
            this.resource = resource;
            this.resourceFactory = resourceFactory;
            this.exception = exception;
        }

        public AccessEvent getAccessEvent() {
            return accessEvent;
        }

        public ResourceFactory getResourceFactory() {
            return resourceFactory;
        }

        public Resource getResource() {
            return resource;
        }

        public Exception getOriginalException() {
            return exception;
        }
    }

    public AccessEventImpl clone() { 
    	try {
    		return (AccessEventImpl)super.clone();
    	} catch (CloneNotSupportedException e) {        
            throw new RuntimeException("Cant clone AccessEventImpl",e);
    	}
    }
    
    public String toString() {
        return "AccessEventImpl["+user+","+eventDate+","+updating+","+resource+"]";
    }
}
