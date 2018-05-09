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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.auth.beans.RoleBean;
import com.jaspersoft.jasperserver.export.modules.auth.beans.UserBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class AuthorityExporter extends BaseExporterModule {

	private String usersArgument;
	private String rolesArgument;
	private String includeRoleUsersArgument;
	private String includeUsersRolesArgument;

	private Map users;
	private Map roles;

	protected AuthorityModuleConfiguration configuration;
	protected boolean includeRoleUsers;
	protected boolean includeUsersRoles;

	public String getIncludeRoleUsersArgument() {
		return includeRoleUsersArgument;
	}

	public void setIncludeRoleUsersArgument(String includeRoleUserArgument) {
		this.includeRoleUsersArgument = includeRoleUserArgument;
	}

	public String getRolesArgument() {
		return rolesArgument;
	}

	public void setRolesArgument(String rolesArgument) {
		this.rolesArgument = rolesArgument;
	}

	public String getUsersArgument() {
		return usersArgument;
	}

	public void setUsersArgument(String usersArgument) {
		this.usersArgument = usersArgument;
	}

	public AuthorityModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AuthorityModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public String getIncludeUsersRolesArgument() {
        return includeUsersRolesArgument;
    }

    public void setIncludeUsersRolesArgument(String includeUsersRolesArgument) {
        this.includeUsersRolesArgument = includeUsersRolesArgument;
    }

    public void init(ExporterModuleContext moduleContext) {
		super.init(moduleContext);

        roles = new LinkedHashMap();
        users = new LinkedHashMap();

		includeUsersRoles = hasParameter(getIncludeUsersRolesArgument());
		includeRoleUsers = hasParameter(getIncludeRoleUsersArgument());

		initUsersAndRoles();
	}

	protected void initUsersAndRoles() {
		initUsers();
		initRoles();
	}

	protected void initUsers() {
		if (isExportEverything() || hasParameter(getUsersArgument())) {
			String[] userNames = getParameterValues(getUsersArgument());
			if (isExportEverything() || userNames == null) {
				List<User> usersList = getUsers();
				for (User user : usersList) {
					addUser(getUser(user));
				}
			} else {
				for (int i = 0; i < userNames.length; i++) {
					addUser(getUser(userNames[i]));
				}
			}
		}
	}

	protected List<User> getUsers () {
		return configuration.getAuthorityService().getUsers(executionContext, null);
	}

	protected void addUser(User user) {
		if (isNotVisibleAuthority(user)) return;

		users.put(getUserName(user), user);
		if (includeUsersRoles){
			addUsersRoles(user);
		}
	}

	protected void addUsersRoles(User user) {
		for (Object roleObject : user.getRoles()) {
			Role role = (Role) roleObject;
			if (isNotVisibleAuthority(role)) continue;

			roles.put(getRoleName(role), role);
		}
	}

	protected User getUser(User user) {
		return getUser(getUserName(user));
	}

	protected User getUser(String userName) {
		User user = configuration.getAuthorityService().getUser(executionContext, userName);
		if (user == null) {
			throw new JSException("jsexception.no.such.user", new Object[] {userName});
		}
		return user;
	}

	protected void initRoles() {
		if (isExportEverything() || hasParameter(getRolesArgument())) {
			String[] roleNames = getParameterValues(getRolesArgument());
			if (isExportEverything() || roleNames == null) {
				List<Role> rolesList = getRoles();
				for (Role role : rolesList) {
					addRole(role);
				}
			} else {
				for (int i = 0; i < roleNames.length; i++) {
					addRole(getRole(roleNames[i]));
				}
			}
		}
	}

	protected List<Role> getRoles () {
		return configuration.getAuthorityService().getRoles(executionContext, null);
	}

	protected void addRole(Role role) {
		if (isNotVisibleAuthority(role)) return;

		if (roles.put(getRoleName(role), role) == null) {
			addRoleUsers(role);
		}
	}

	protected String getRoleName(Role role) {
		return role.getRoleName();
	}

	protected String getUserName(User user) {
		return user.getUsername();
	}

	protected Role getRole(String name) {
		Role role = configuration.getAuthorityService().getRole(executionContext, name);
		if (role == null) {
			throw new JSException("jsexception.no.such.role", new Object[] {name});
		}
		return role;
	}

	protected void addRoleUsers(Role role) {
		if (includeRoleUsers) {
			String roleName = getRoleName(role);
			List usersInRole = configuration.getAuthorityService().getUsersInRole(executionContext, roleName);
			addUsers(usersInRole);
		}		
	}

	protected void addUsers(List<User> usersInRole) {
		if (usersInRole != null) {
			for (User user : usersInRole) {
				//fetch again the user so that its attributes are set
				addUser(getUser(user));
			}
		}
	}

	protected boolean hasUsers() {
		return !users.isEmpty();
	}
	
	protected boolean hasRoles() {
		return !roles.isEmpty();
	}

	protected boolean isToProcess() {
		return hasRoles() || hasUsers();
	}
	
	public void process() {
		if (hasUsers()) {
			exportUsers();
		}
		
		if (hasRoles()) {
			exportRoles();
		}
	}

	protected void exportRoles() {
		mkdir(configuration.getRolesDirName());
		
		for (Iterator it = roles.values().iterator(); it.hasNext();) {
			Role role = (Role) it.next();
			exportRole(role);
		}
	}

	protected void exportRole(Role role) {
		commandOut.info("Exporting role " + role.getRoleName());

		RoleBean roleBean = new RoleBean();
		roleBean.copyFrom(role);
		
		serialize(roleBean, configuration.getRolesDirName(), getRoleFile(role), configuration.getSerializer());
		
		addIndexElement(role);
	}

	protected Element addIndexElement(Role role) {
		Element roleElement = getIndexElement().addElement(configuration.getRoleIndexElementName());
		roleElement.setText(role.getRoleName());
		return roleElement;
	}

	protected void exportUsers() {
		mkdir(configuration.getUsersDirName());
		
		for (Iterator it = users.values().iterator(); it.hasNext();) {
			User user = (User) it.next();
			export(user);
		}
	}

	protected void export(User user) {
		commandOut.info("Exporting user " + user.getUsername());

		UserBean userBean = new UserBean();
		userBean.copyFrom(user);
		userBean.setAttributes(prepareAttributesBeans(user.getAttributes()));
		
		serialize(userBean, configuration.getUsersDirName(), getUserFile(user), configuration.getSerializer());
		
		addIndexElement(user);
	}

	protected Element addIndexElement(User user) {
		Element userElement = getIndexElement().addElement(configuration.getUserIndexElementName());
		userElement.setText(user.getUsername());
		return userElement;
	}

	protected String getUserFile(User user) {
		return user.getUsername() + ".xml";
	}

	protected String getRoleFile(Role role) {
		return role.getRoleName() + ".xml";
	}

	protected boolean isNotVisibleAuthority(TenantQualified qualified) {
		return false;
	}
}
