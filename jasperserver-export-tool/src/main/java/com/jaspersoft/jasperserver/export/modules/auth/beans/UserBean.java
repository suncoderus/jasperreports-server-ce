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

import com.jaspersoft.jasperserver.api.common.crypto.Cipherer;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.export.modules.auth.AuthorityImportHandler;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.util.CommandOut;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean.ENCRYPTION_PREFIX;
import static com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean.ENCRYPTION_SUFFIX;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class UserBean {

	private static final CommandOut commandOut = CommandOut.getInstance();

	private String username;
	private String fullName;
	private String password;
	private String emailAddress;
	private boolean externallyDefined = false;
	private boolean enabled = false;
	private TenantQualifiedName[] roleNames;
	private ProfileAttributeBean[] attributes;
	private Date previousPasswordChangeTime;
	private String tenantId;

	private static final Cipherer importExportCipher =
			(Cipherer)StaticApplicationContext.getApplicationContext().getBean("importExportCipher");

	public void copyFrom(User user) {
		setUsername(user.getUsername());
		setFullName(user.getFullName());

		//encrypt for export
		//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
		setPassword(ENCRYPTION_PREFIX + importExportCipher.encode(user.getPassword()) + ENCRYPTION_SUFFIX);

		setEmailAddress(user.getEmailAddress());
		setExternallyDefined(user.isExternallyDefined());
		setEnabled(user.isEnabled());
		setPreviousPasswordChangeTime(user.getPreviousPasswordChangeTime());
		setTenantId(user.getTenantId());
		copyRolesFrom(user);
	}

	protected void copyRolesFrom(User user) {
		Set roles = user.getRoles();
		TenantQualifiedName[] names;
		if (roles != null && !roles.isEmpty()) {
			names = new TenantQualifiedName[roles.size()];
			int c = 0;
			for (Iterator iter = roles.iterator(); iter.hasNext(); ++c) {
				Role role = (Role) iter.next();
				names[c] = new TenantQualifiedName(
						role.getTenantId(), role.getRoleName());
			}
		} else {
			names = null;
		}
		setRoleNames(names);
	}

	public void copyTo(User user, AuthorityImportHandler importHandler) {
		user.setUsername(getUsername());
		user.setFullName(getFullName());
		user.setEmailAddress(getEmailAddress());
		user.setExternallyDefined(isExternallyDefined());
		user.setEnabled(isEnabled());
		user.setPreviousPasswordChangeTime(getPreviousPasswordChangeTime());
		user.setTenantId(getTenantId());

		//decrypt pwd for import. if decryption fails, set password as is; this is probably due to legacy import
		//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
		final String pwd = getPassword();
		user.setPassword((pwd != null && pwd.startsWith(ENCRYPTION_PREFIX) && pwd.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(pwd.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : pwd);

		if (!importHandler.getImportContext().getNewGeneratedTenantIds().isEmpty()) {
			user.setTenantId(TenantStrHolderPattern.TENANT_ID.replaceWithNewTenantIds(
					importHandler.getImportContext().getNewGeneratedTenantIds(), user.getTenantId()));
		}

		copyRolesTo(user, importHandler);
	}

	protected void copyRolesTo(User user, AuthorityImportHandler importHandler) {
		Set roles;
		if (roleNames == null) {
			roles = null;
		} else {
			roles = new HashSet();
			for (int i = 0; i < roleNames.length; i++) {
				TenantQualifiedName roleName = roleNames[i];

				if (!importHandler.getImportContext().getNewGeneratedTenantIds().isEmpty()) {
					roleName.setTenantId(TenantStrHolderPattern.TENANT_ID.replaceWithNewTenantIds(
							importHandler.getImportContext().getNewGeneratedTenantIds(), roleName.getTenantId()));
				}

				Role role = importHandler.resolveRole(roleName);
				if (role == null) {
					commandOut.warn("Role " + roleName.getName()
							+ (roleName.getTenantId() == null ? "" : (" of tenant " + roleName.getTenantId()))
							+ " not found while copying user " + getUsername() + ", skipping.");
				} else {
					roles.add(role);
				}
			}
		}
		user.setRoles(roles);
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isExternallyDefined() {
		return externallyDefined;
	}

	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public TenantQualifiedName[] getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(TenantQualifiedName[] roleNames) {
		this.roleNames = roleNames;
	}

	public ProfileAttributeBean[] getAttributes() {
		return attributes;
	}

	public void setAttributes(ProfileAttributeBean[] attributes) {
		this.attributes = attributes;
	}

	public Date getPreviousPasswordChangeTime() {
		return previousPasswordChangeTime;
	}

	public void setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
		this.previousPasswordChangeTime = previousPasswordChangeTime;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}


}
