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
package com.jaspersoft.jasperserver.api.logging.audit.domain;

import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;

import java.util.Date;
import java.util.Set;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public interface AuditEvent extends LoggableEvent {

    public String getUsername();

    public void setUsername(String username);

    public String getTenantId();

    public void setTenantId(String tenantId);

    public Date getEventDate();

    public void setEventDate(Date eventDate);

    public String getEventType();

    public void setEventType(String eventType);

    public String getRequestType();

    public void setRequestType(String name);

    public String getResourceUri();

    public void setResourceUri(String uri);

    public String getResourceType();

    public void setResourceType(String resourceType);

    public Set<AuditEventProperty> getAuditEventProperties();

    public void setAuditEventProperties(Set<AuditEventProperty> auditEventProperties);
}
