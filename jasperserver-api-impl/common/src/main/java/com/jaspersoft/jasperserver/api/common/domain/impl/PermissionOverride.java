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

package com.jaspersoft.jasperserver.api.common.domain.impl;

/**
 * This is an object added to the attributeList of the ExecutionContext passed to RepositoryService methods.
 * If a ContextSensitiveAclEntryVoter applies to the call, it will replace the requiredPermissions with 
 * the value of newPermissions.
 * @author bob
 *
 */
public class PermissionOverride {
    private String overrideId;
    
    public PermissionOverride(String id) {
    	this.overrideId = id;
    }

	public void setOverrideId(String overrideId) {
		this.overrideId = overrideId;
	}

	public String getOverrideId() {
		return overrideId;
	}

}
