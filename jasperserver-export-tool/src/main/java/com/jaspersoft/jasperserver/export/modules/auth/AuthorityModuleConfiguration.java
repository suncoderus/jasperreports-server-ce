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

package com.jaspersoft.jasperserver.export.modules.auth;

import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class AuthorityModuleConfiguration {
	
	private UserAuthorityService authorityService;
	private ProfileAttributeService attributeService;
	private String roleIndexElementName;
	private String userIndexElementName;
	private String usersDirName;
	private String rolesDirName;
	private ObjectSerializer serializer;
	
	public UserAuthorityService getAuthorityService() {
		return authorityService;
	}
	
	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}
	
	public String getRolesDirName() {
		return rolesDirName;
	}
	
	public void setRolesDirName(String rolesDirName) {
		this.rolesDirName = rolesDirName;
	}
	
	public ObjectSerializer getSerializer() {
		return serializer;
	}
	
	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}
	
	public String getUsersDirName() {
		return usersDirName;
	}
	
	public void setUsersDirName(String usersDirName) {
		this.usersDirName = usersDirName;
	}

	public String getRoleIndexElementName() {
		return roleIndexElementName;
	}

	public void setRoleIndexElementName(String roleIndexElementName) {
		this.roleIndexElementName = roleIndexElementName;
	}

	public String getUserIndexElementName() {
		return userIndexElementName;
	}

	public void setUserIndexElementName(String userIndexElementName) {
		this.userIndexElementName = userIndexElementName;
	}

	public ProfileAttributeService getAttributeService() {
		return attributeService;
	}

	public void setAttributeService(ProfileAttributeService attributeService) {
		this.attributeService = attributeService;
	}

}
