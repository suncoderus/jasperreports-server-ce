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

import org.springframework.webflow.execution.RequestContext;

/**
 * Action for handling AWS Settings saving
 *
 * @author agodovanets@jaspersoft.com
 * @version $Id$
 */
public class AwsSettingsAction extends BaseSettingsAction {
    @Override
    public void init(RequestContext context) {
        context.getRequestScope().put("aws.db.security.group.changes.enabled", getPropertiesManagementService().getProperty("aws.db.security.group.changes.enabled"));
        context.getRequestScope().put("aws.db.security.group.name", getPropertiesManagementService().getProperty("aws.db.security.group.name"));
        context.getRequestScope().put("aws.db.security.group.description", getPropertiesManagementService().getProperty("aws.db.security.group.description"));
        context.getRequestScope().put("aws.db.security.group.ingressPublicIp", getPropertiesManagementService().getProperty("aws.db.security.group.ingressPublicIp"));
        context.getRequestScope().put("aws.db.security.group.suppressEc2CredentialsWarnings", getPropertiesManagementService().getProperty("aws.db.security.group.suppressEc2CredentialsWarnings"));
    }

    // TODO: implement URI validation
    @Override
    protected String validate(String option, String value) {
        return null;
    }

    @Override
    protected String getSettingsContextName() {
        return "awsConfigurationSettingsUpdate";
    }
}
