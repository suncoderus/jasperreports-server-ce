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
package com.jaspersoft.jasperserver.api.logging.audit.context;

import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;

import java.util.List;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public interface AuditContext {

    public static final String SKIP_AUDIT_REPORT_PARAMETER = "skipAudit";

    /**
     * Return all auditEvents which are present in current context
     *
     * @return list of all audit events
     */
    public List<AuditEvent> getAllAuditEvents();

    /**
     * Return audit events of specified type
     *
     * @param auditEventTypeName name of event type
     * @return list of audit events of specified type
     */
    public List<AuditEvent> getAuditEvents(String auditEventTypeName);

    /**
     * Return opened audit event of specified type.
     * We suppose that only one auditEvent of
     * specified type could be opened at one time
     *
     * @param auditEventTypeName name of auditEvent type
     * @return return opened audit event of specified type
     */
    public AuditEvent getOpenedAuditEvent(String auditEventTypeName);

    /**
     * Return all opened audit events
     *
     * @return list of all opened audit events
     */
    public List<AuditEvent> getOpenedAuditEvents();

    /**
     * Close audit event to indicate that it will not be updated more.
     * This method should be used for every auditEvent which
     * will not be updated more.
     * 
     * @param auditEvent auditEvent which will be marked as closed
     */
    public void closeAuditEvent(AuditEvent auditEvent);

    /**
     * Create auditEvent of specified type and add it to
     * audit context
     *
     * @param auditEventTypeName name of event type
     * @return audit event of specified type
     */
    public AuditEvent createAuditEvent(String auditEventTypeName);

    /**
     * Create auditEvent of specified type with
     * presetted resource and add it to context
     *
     * @param auditEventTypeName name of event type
     * @param resourceUri uri of resource which is associated with this audit event
     * @return audit event of specified type
     */
    public AuditEvent createAuditEvent(String auditEventTypeName, String resourceUri, String resourceType);

    /**
     * Return opened event of specified event type
     * or create one if it does not exists
     *
     * @param auditEventTypeName name of event type
     * @return existing opened event or created one
     */
    public AuditEvent getOrCreateAuditEvent(String auditEventTypeName);
    
    /**
     * Create auditEventProperty of specified property type from passed object
     * and append it to specified audit event
     *
     * @param propertyTypeName name of proprty type
     * @param param object which will be converted to auditEventProperty value
     * @param auditEvent audit event to which this audit event property will be
     * created
     */
    public void addPropertyToAuditEvent(String propertyTypeName, Object param, AuditEvent auditEvent);

    public void setResourceTypeToAuditEvent(String resourceType, AuditEvent auditEvent);

    public void doInAuditContext(AuditContextCallback callback);

    public void doInAuditContext(String auditEventType, AuditContextCallbackWithEvent callback);

    public void doInAuditContext(String[] auditEventTypes, AuditContextCallbackWithEvent callback);

    public interface AuditContextCallback {
        public void execute();
    }

    public interface AuditContextCallbackWithEvent {
        public void execute(AuditEvent auditEvent);
    }

}
