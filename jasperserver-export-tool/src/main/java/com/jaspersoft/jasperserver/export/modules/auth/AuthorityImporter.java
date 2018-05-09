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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.auth.beans.RoleBean;
import com.jaspersoft.jasperserver.export.modules.auth.beans.UserBean;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class AuthorityImporter extends BaseImporterModule {
	
	private AuthorityModuleConfiguration configuration;
	private String updateArg;
    private String updateCoreUsersArg;
	private String skipUpdateArg;
	private boolean update;
    private boolean updateCoreUsers;

	protected class ImportHandler implements AuthorityImportHandler {

		private final Map roles;
		
		protected ImportHandler(List roles) {
			this.roles = new HashMap();

			for (Iterator it = roles.iterator(); it.hasNext();) {
				Role role = (Role) it.next();
				TenantQualifiedName roleQName = new TenantQualifiedName(
						role.getTenantId(), role.getRoleName());
				this.roles.put(roleQName, role);
			}
		}
		
		public Role resolveRole(TenantQualifiedName rolename) {
			Role role = (Role) roles.get(rolename);
			return role;
		}

		@Override
		public ImporterModuleContext getImportContext() {
			return importContext;
		}
	}
	
	private ImportHandler importHandler;
	
	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		update = getUpdateFlag();
        updateCoreUsers = getUpdateCoreUsersFlag();
	}

	public List<String> process() {
		initProcess();
		importRoles();
		
		// init the import handler after the roles are imported
		initImportHandler();
		
		importUsers();
        return null;
	}

	protected void importRoles() {
		for (Iterator it = indexElement.elementIterator(configuration.getRoleIndexElementName()); it.hasNext(); ) {
			Element roleElement = (Element) it.next();
			processRole(roleElement);
		}
	}

	protected void processRole(Element roleElement) {
		String roleName = roleElement.getText();
		String file = getRoleFile(roleName);
		RoleBean roleBean = (RoleBean) deserialize(
				configuration.getRolesDirName(), file, 
				configuration.getSerializer());

		Role role = createRole(roleBean);
		if (alreadyExists(role)) {
			commandOut.warn("Role " + role.getRoleName() + " already exists, skipping.");
		} else {
			saveRole(role);
			
			commandOut.info("Created role " + role.getRoleName());
		}
	}
	
	protected boolean alreadyExists(Role role) {
		return configuration.getAuthorityService().getRole(executionContext, role.getRoleName()) != null;
	}

	protected Role createRole(RoleBean roleBean) {
		Role role = configuration.getAuthorityService().newRole(executionContext);
		roleBean.copyTo(role, importContext);
		return role;
	}

	protected void saveRole(Role role) {
		configuration.getAuthorityService().putRole(executionContext, role);
	}
	
	protected void initImportHandler() {
		List allRoles = configuration.getAuthorityService().getRoles(executionContext, null);
		importHandler = new ImportHandler(allRoles);
	}

	protected void importUsers() {
		for (Iterator it = indexElement.elementIterator(configuration.getUserIndexElementName()); it.hasNext(); ) {
			Element userElement = (Element) it.next();
			processUser(userElement);
		}
	}

    protected void processUser(Element userElement) {
		String username = userElement.getText();
		String file = getUserFile(username);
		UserBean userBean = (UserBean) deserialize(configuration.getUsersDirName(), file, 
				configuration.getSerializer());

		User user = createUser(userBean);
		if (!alreadyExists(user)) {
            saveUser(user);
            saveProfileAttributes(configuration.getAttributeService(), user, userBean.getAttributes());

            commandOut.info("Created user " + user.getUsername());
        } else if (update || (updateCoreUsers && isCoreUser(user))) {
			updateUser(user.getUsername(),user);
			saveProfileAttributes(configuration.getAttributeService(), user, userBean.getAttributes());

            commandOut.info("Updated user " + user.getUsername());

        } else {
            commandOut.warn("User " + user.getUsername() + " already exists, skipping.");
		}
	}

    protected boolean isCoreUser(User user) {
        if (user.getUsername().equals("jasperadmin")) return true;
        return false;
    }


	protected boolean alreadyExists(User user) {
		return configuration.getAuthorityService().getUser(executionContext, user.getUsername()) != null;
	}

	protected User createUser(UserBean userBean) {
		User user = configuration.getAuthorityService().newUser(executionContext);
		userBean.copyTo(user, importHandler);		
		return user;
	}
	
	protected void saveUser(User user) {
		configuration.getAuthorityService().putUser(executionContext, user);
	}
	
	protected void updateUser(String userName, User aUser) {
		configuration.getAuthorityService().updateUser(executionContext, userName, aUser);
	}

	protected boolean getUpdateFlag() {
		return hasParameter(getUpdateArg()) && !hasParameter((getSkipUpdateArg()));
	}

    protected boolean getUpdateCoreUsersFlag() {
        return hasParameter(getUpdateCoreUsersArg());
    }

	protected boolean isUpdate() {
		return update;
	}

	protected boolean isUpdateCoreUsers() {
		return updateCoreUsers;
	}


	protected String getUserFile(String username) {
		return username + ".xml";
	}

	protected String getRoleFile(String roleName) {
		return roleName + ".xml";
	}

	public AuthorityModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AuthorityModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public String getUpdateArg() {
        return updateArg;
    }

    public void setUpdateArg(String updateArg) {
        this.updateArg = updateArg;
    }

    public String getSkipUpdateArg() {
        return skipUpdateArg;
    }

    public void setSkipUpdateArg(String skipUpdateArg) {
        this.skipUpdateArg = skipUpdateArg;
    }

    public String getUpdateCoreUsersArg() {
        return updateCoreUsersArg;
    }

    public void setUpdateCoreUsersArg(String updateCoreUsersArg) {
        this.updateCoreUsersArg = updateCoreUsersArg;
    }
}
