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
package com.jaspersoft.jasperserver.export.modules.auth.beans;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class RoleBean {

	private String roleName;
	private String tenantId;
	private boolean externallyDefined = false;

	public void copyFrom(Role role) {
		setRoleName(role.getRoleName());
		setTenantId(role.getTenantId());
		setExternallyDefined(role.isExternallyDefined());
	}

	public void copyTo(Role role, ImporterModuleContext context) {
		role.setRoleName(getRoleName());
		role.setTenantId(getTenantId());
		role.setExternallyDefined(isExternallyDefined());

		if (!context.getNewGeneratedTenantIds().isEmpty()) {
			role.setTenantId(TenantStrHolderPattern.TENANT_ID
					.replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), role.getTenantId()));
		}
	}
	
	public boolean isExternallyDefined() {
		return externallyDefined;
	}
	
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}
