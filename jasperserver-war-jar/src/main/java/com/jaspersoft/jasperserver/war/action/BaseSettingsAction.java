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
package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Abstract action that holds common logic for saving properties in DB
 *
 * @author agodovanets@jaspersoft.com
 * @version $Id$
 */
public abstract class BaseSettingsAction extends MultiAction {
    protected static final Log log = LogFactory.getLog(BaseSettingsAction.class);
    private static final String AJAX_RESPONSE_MODEL = "ajaxResponseModel";
    private static final String OPTION_NAME = "name";
    private static final String OPTION_VALUE = "value";


    private PropertiesManagementService propertiesManagementService;
    private AuditContext auditContext;

    public void init(RequestContext context) {
        // No op
    }

    public Event saveSingleProperty(RequestContext context) throws Exception {
        log.info("Saving Server Property");
        String name = context.getRequestParameters().get(OPTION_NAME);
        String value = context.getRequestParameters().get(OPTION_VALUE);

        String res;
        String error = validate(name, value);

        if (error == null) {
            createAuditEvent();
            saveSingleProperty(name, value);
            addParamToAuditEvent(name, value);
            closeAuditEvent();
            res = "{\"result\":\"JAM_056_UPDATED\",\"optionName\":\"" + name + "\"}";
        } else {
            res = "{\"error\":\"" + error + "\",\"optionName\":\"" + name + "\"}";
        }
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, res);
        return success();
    }

    protected void saveSingleProperty(String key, String value) {
        getPropertiesManagementService().setProperty(key, value);
    }

    protected abstract String validate(String option, String value);

    protected abstract String getSettingsContextName();

    private void createAuditEvent() {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent(getSettingsContextName());
            }
        });
    }

    private void addParamToAuditEvent(final String name, final Object value) {
        auditContext.doInAuditContext(getSettingsContextName(), new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent(name, value, auditEvent);
            }
        });
    }

    private void closeAuditEvent() {
        auditContext.doInAuditContext(getSettingsContextName(), new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

    public PropertiesManagementService getPropertiesManagementService() {
        return propertiesManagementService;
    }

    public void setPropertiesManagementService(PropertiesManagementService propertiesManagementService) {
        this.propertiesManagementService = propertiesManagementService;
    }

    public AuditContext getAuditContext() {
        return auditContext;
    }

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }
}
