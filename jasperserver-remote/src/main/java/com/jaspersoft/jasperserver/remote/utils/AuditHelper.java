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
package com.jaspersoft.jasperserver.remote.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.remote.helpers.JacksonMapperProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author gtoffoli
 * @version $Id$
 */
@Component
public class AuditHelper {
    @Resource(name = "concreteAuditContext")
    private AuditContext auditContext;

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    public void createAuditEvent(final String auditEventType) {
        auditContext.doInAuditContext(
                new AuditContext.AuditContextCallback() {
                    public void execute() {
                        auditContext.createAuditEvent(auditEventType);
                    }
                });
    }

    public void closeAuditEvent(String jobType) {
        auditContext.doInAuditContext(jobType, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

    public void addPropertyToAuditEvent(final String auditEventType, final String propertyName, final Object property) {
        auditContext.doInAuditContext(auditEventType,
                new AuditContext.AuditContextCallbackWithEvent() {
                    public void execute(AuditEvent auditEvent) {
                        auditContext.addPropertyToAuditEvent(propertyName, property, auditEvent);
                    }
                });
    }

    public void addExceptionToAllAuditEvents(final Throwable exception) {
        auditContext.doInAuditContext(
                new String[]{"saveResource", "updateResource", "deleteResource", "copyResource", "moveResource",
                        "createFolder", "updateFolder", "deleteFolder", "copyFolder", "moveFolder",
                        "runReport"},
                new AuditContext.AuditContextCallbackWithEvent() {
                    public void execute(AuditEvent auditEvent) {
                        auditContext.addPropertyToAuditEvent("exception", exception, auditEvent);
                    }
                });
    }



    public void createAuditEvent(String operation, String wsType, boolean isNew) {
        if ("copy".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("copyFolder");
            } else {
                createAuditEvent("copyResource");
            }
        } else if ("move".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("moveFolder");
            } else {
                createAuditEvent("moveResource");
            }
        } else if ("delete".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("deleteFolder");
            } else {
                createAuditEvent("deleteResource");
            }
        } else if ("put".equals(operation)) {
            if ("folder".equals(wsType)) {
                if (isNew) {
                    createAuditEvent("createFolder");
                } else {
                    createAuditEvent("updateFolder");
                }
            } else {
                if (isNew) {
                    createAuditEvent("saveResource");
                } else {
                    createAuditEvent("updateResource");
                }
            }
        } else if ("get".equals(operation)) {
            if (!"folder".equals(wsType)) {
                createAuditEvent("accessResource");
            }
        }
    }

    public static String convertObjectToJSON(Object obj) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectMapper mapper = JacksonMapperProvider.getObjectMapper();
        mapper.writeValue(stream, obj);
        return stream.toString(StandardCharsets.UTF_8.name());
    }
}
