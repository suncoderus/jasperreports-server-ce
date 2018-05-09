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
package com.jaspersoft.jasperserver.api.logging.audit.context.impl;

import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;

import java.util.List;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DummyAuditContext implements AuditContext {

    public List<AuditEvent> getAllAuditEvents() {
        return null;
    }

    public List<AuditEvent> getAuditEvents(String auditEventTypeName) {
        return null;
    }

    public AuditEvent getOpenedAuditEvent(String auditEventTypeName) {
        return null;
    }

    public List<AuditEvent> getOpenedAuditEvents() {
        return null;
    }

    public void closeAuditEvent(AuditEvent auditEvent) {
    }

    public AuditEvent createAuditEvent(String auditEventTypeName) {
        return null;
    }

    public AuditEvent createAuditEvent(String auditEventTypeName, String resourceUri, String resourceType) {
        return null;
    }

    public AuditEvent getOrCreateAuditEvent(String auditEventTypeName) {
        return null;
    }

    public void addPropertyToAuditEvent(String propertyTypeName, Object param, AuditEvent auditEvent) {
    }

    public void setResourceTypeToAuditEvent(String resourceType, AuditEvent auditEvent) {       
    }

    public void doInAuditContext(AuditContextCallback callback) {
    }

    public void doInAuditContext(String auditEventType, AuditContextCallbackWithEvent callback) {        
    }

    public void doInAuditContext(String[] auditEventTypes, AuditContextCallbackWithEvent callback) {
    }

    public void flushContext() {
    }
}
