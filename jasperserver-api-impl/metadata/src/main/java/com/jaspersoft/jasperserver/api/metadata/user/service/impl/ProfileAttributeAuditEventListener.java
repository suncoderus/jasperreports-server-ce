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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeEventListener;

/**
 * @author askorodumov
 * @version $Id: Id $
 */
public class ProfileAttributeAuditEventListener implements ProfileAttributeEventListener {
    private static final String AUDIT_RESOURCE_TYPE = ProfileAttribute.class.getName();

    private AuditContext auditContext;

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    @Override
    public void onCreate(ProfileAttribute attribute) {
        String eventType = "createAttribute";
        createAuditEvent(eventType, attribute);
        closeAuditEvent(eventType);
    }

    @Override
    public void onUpdate(ProfileAttribute attribute, String previousAttributeValue) {
        String eventType = "updateAttribute";
        createAuditEvent(eventType, attribute);
        if (!attribute.isSecure() && !PasswordCipherer.getInstance().isEncrypted(previousAttributeValue)) {
            addPropertyToAuditEvent(eventType, "previousAttributeValue", previousAttributeValue);
        }
        closeAuditEvent(eventType);
    }

    @Override
    public void onDelete(ProfileAttribute attribute) {
        String eventType = "deleteAttribute";
        createAuditEvent(eventType, attribute);
        closeAuditEvent(eventType);
    }

    private void createAuditEvent(final String eventType, final ProfileAttribute attribute) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            @Override
            public void execute() {
                AuditEvent auditEvent = auditContext.createAuditEvent(eventType, attribute.getURI(), AUDIT_RESOURCE_TYPE);

                auditContext.addPropertyToAuditEvent("attributeName", attribute.getAttrName(), auditEvent);
                auditContext.addPropertyToAuditEvent("isSecure", attribute.isSecure(), auditEvent);
                if (!attribute.isSecure()) {
                    auditContext.addPropertyToAuditEvent("attributeValue", attribute.getAttrValue(), auditEvent);
                }
                addRecipientProperties(auditEvent, attribute.getPrincipal());


            }
        });
    }

    private void addPropertyToAuditEvent(String eventType, final String propertyName, final Object propertyValue) {
        auditContext.doInAuditContext(eventType, new AuditContext.AuditContextCallbackWithEvent() {
            @Override
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent(propertyName, propertyValue, auditEvent);
            }
        });
    }

    private void closeAuditEvent(String eventType) {
        auditContext.doInAuditContext(eventType, new AuditContext.AuditContextCallbackWithEvent() {
            @Override
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

    private void addRecipientProperties(AuditEvent auditEvent, Object recipient) {
        if (recipient instanceof Tenant) {
            auditContext.addPropertyToAuditEvent("recipientType", "TENANT", auditEvent);
            auditContext.addPropertyToAuditEvent("recipientName", ((Tenant) recipient).getId(), auditEvent);
        } else if (recipient instanceof User) {
            auditContext.addPropertyToAuditEvent("recipientType", "USER", auditEvent);
            auditContext.addPropertyToAuditEvent("recipientName", ((User) recipient).getUsername(), auditEvent);
            auditContext.addPropertyToAuditEvent("recipientTenantId", ((User) recipient).getTenantId(), auditEvent);
        } else if (recipient instanceof Role) {
            auditContext.addPropertyToAuditEvent("recipientType", "ROLE", auditEvent);
            auditContext.addPropertyToAuditEvent("recipientName", ((Role) recipient).getRoleName(), auditEvent);
            auditContext.addPropertyToAuditEvent("recipientTenantId", ((Role) recipient).getTenantId(), auditEvent);
        } else {
            auditContext.addPropertyToAuditEvent("recipientType", "UNKNOWN", auditEvent);
        }
    }
}
