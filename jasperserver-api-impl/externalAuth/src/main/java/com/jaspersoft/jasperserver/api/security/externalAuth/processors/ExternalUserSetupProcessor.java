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
package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ExternalUserService;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalAuthProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_AUTHORITIES;
import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_AUTH_DETAILS;
import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_JRS_USER_TENANT_ID;


/**
 * User: dlitvak
 * Date: 8/22/12
 */
public class ExternalUserSetupProcessor extends AbstractExternalUserProcessor {
    private static final Logger logger = LogManager.getLogger(ExternalUserSetupProcessor.class);
	private static final String ROLE_SUFFIX = "|*";

    // roles that will be created automatically for each user once he is authenticated.
    private List defaultInternalRoles;
	private ExternalAuthProperties externalAuthProperties = new ExternalAuthProperties();
	private Map<String, String> organizationRoleMap = Collections.emptyMap();
	private String permittedExternalRoleNameRegex = "[A-Za-z0-9_]+";
	private List<String> adminUsernames;
	private List<String> defaultAdminRoles;
	private String conflictingExternalInternalRoleNameSuffix = "EXT";
	private Pattern  permittedExternalRoleNamePattern = null;

	private List<Pattern> permittedRolesRegex = new ArrayList<Pattern>();

	@Override
    public void afterPropertiesSet() throws Exception {
//        Assert.notNull(this.defaultInternalRoles, "Please specify non-null internal default role");
//        Assert.notEmpty(this.defaultInternalRoles, "Please specify at least one internal default role");
        super.afterPropertiesSet();

		permittedExternalRoleNamePattern = Pattern.compile(permittedExternalRoleNameRegex);

		//clean up organizationRoleMap of invalid internal role values
		if (!this.organizationRoleMap.isEmpty()) {
			Map<String, String> cleanedupOrganizationRoleMap = new HashMap<String, String>(organizationRoleMap.size());

			for (Map.Entry<String,String> rolePair : organizationRoleMap.entrySet()) {
				final String rolePairKey = rolePair.getKey();
				final String rolePairValue = rolePair.getValue();
				if (rolePairValue != null) {
					String roleNameToValidate = rolePairValue.trim();
					roleNameToValidate = roleNameToValidate.endsWith(ROLE_SUFFIX) ?
							roleNameToValidate.substring(0, roleNameToValidate.length() - ROLE_SUFFIX.length()) : roleNameToValidate;

					String validAuthorityName = purgeRoleNameOfInvalidChars(roleNameToValidate);
					if (validAuthorityName.length() == 0) {
						ProcessorData processorData = ProcessorData.getInstance();
						UserDetails userDetails = (UserDetails) processorData.getData(EXTERNAL_AUTH_DETAILS);

						logger.error("External role " + roleNameToValidate + " has forbidden characters only " +
								"according to permittedExternalRoleNameRegex: " + permittedExternalRoleNameRegex +
								". Skipping it for user: " +
								(userDetails != null && userDetails.getUsername() != null ? userDetails.getUsername() : ""));
						continue;
					}

					if (rolePairValue.trim().endsWith(ROLE_SUFFIX))
						validAuthorityName += ROLE_SUFFIX;

					cleanedupOrganizationRoleMap.put(rolePairKey.toUpperCase().trim(), validAuthorityName.toUpperCase().trim());
				}
			}

			this.organizationRoleMap = cleanedupOrganizationRoleMap;
		}
    }

    protected User getUser(){
        ProcessorData processorData = ProcessorData.getInstance();
        UserDetails userDetails = (UserDetails) processorData.getData(EXTERNAL_AUTH_DETAILS);

		if (logger.isDebugEnabled())
			logger.debug("Getting user obj for username " + (userDetails != null ? userDetails.getUsername() : ": userDetails is null."));

        return getUserAuthorityService().getUser(new ExecutionContextImpl(), userDetails.getUsername());
    }

    @Override
	public void process() {
		ProcessorData processorData = ProcessorData.getInstance();
		UserDetails userDetails = (UserDetails) processorData.getData(EXTERNAL_AUTH_DETAILS);

		try {
			String userName = userDetails.getUsername();

			if (logger.isDebugEnabled())
				logger.debug("Setting up external user: " + userName);

			User user = getUser();
			String logoutUrl = externalAuthProperties != null ? externalAuthProperties.getLogoutUrl() : null;
			if ( user==null ) {
				user = createNewExternalUser(userName);
			}
			else if (!user.isEnabled()) {
				throw new JSException("External user " + user.getUsername() + " was disabled on jasperserver. Please contact an admin user to re-enable. " +
						(logoutUrl != null && logoutUrl.length() > 0 ?  "Click <a href=\"" + logoutUrl + "\">logout</a> to exit from external system." : ""));
			}
			else if (!user.isExternallyDefined()) {
				throw new JSException("Internally defined user " + user.getUsername() + " already exists. Please contact an admin user to resolve the issue. " +
						(logoutUrl != null && logoutUrl.length() > 0 ?  "Click <a href=\"" + logoutUrl + "\">logout</a> to exit from external system." : ""));
			}

			List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) processorData.getData(EXTERNAL_AUTHORITIES);
			final String tenantId = (String) processorData.getData(EXTERNAL_JRS_USER_TENANT_ID);

			Set<Role> externalRoles = convertGrantedAuthoritiesToRoles(grantedAuthorities, tenantId);
			user.setTenantId(tenantId);
			alignInternalAndExternalUser(externalRoles, user);

			if (logger.isDebugEnabled())
				logger.debug("External user " + userName + " has been synchronized.");

            ((ExternalUserService)getUserAuthorityService()).makeUserLoggedIn(user);
		}
		catch (RuntimeException e) {
			String userName = (userDetails != null ? userDetails.getUsername() : "");
			logger.error("Error processing external user " + userName + ": " + e.getMessage());
			throw e;
		}
	}

    /**
     * New user created from given authentication details. No password is set or needed.
     * Roles are set elsewhere.
     *
     * @param userName
     * @return created User
     */
    protected User createNewExternalUser(String userName) {
        User user = getUserAuthorityService().newUser(new ExecutionContextImpl());
        user.setUsername(userName);
        // If it is externally authenticated, no save of password
        //user.setPassword(userDetails.getPassword());
        user.setFullName(userName); // We don't know the real name
        user.setExternallyDefined(true);
        user.setEnabled(true);
		logger.warn("Created new external user: " + user.getUsername());
        return user;
    }

    protected Set persistRoles(Set<Role> roles) {
        Set<Role> persistedRoles = new HashSet<Role>();
        for (Iterator<Role> iter = roles.iterator(); iter.hasNext(); ) {
            Role r = iter.next();
            persistedRoles.add(getOrCreateRole(r));
        }
        return persistedRoles;
    }

    protected void alignInternalAndExternalUser(Set remoteExternalUserRoles, User user) {
        Set<Role> jrsUserRoles = user.getRoles();
        logger.info("Starting align for user: "+user.getFullName()+" with remoteExternalUserRoles at size of " + remoteExternalUserRoles.size());

        Collection jrsInternalUserRoles = CollectionUtils.select(jrsUserRoles, new Predicate() {
            public boolean evaluate(Object input) {
                if (!(input instanceof Role)) {
                    return false;
                }
                return !((Role) input).isExternallyDefined();
            }
        });

        if (logger.isDebugEnabled()){
            logger.debug("jrsInternalUserRoles: " +roleCollectionToString(jrsInternalUserRoles));
        }

        Collection<Role> jrsInternalRolesNotInRoleMap = CollectionUtils.select(jrsInternalUserRoles, new Predicate() {
            public boolean evaluate(Object input) {
                return !getOrganizationRoleMap().containsValue(((Role) input).getRoleName())
						&& !getOrganizationRoleMap().containsValue(((Role) input).getRoleName() + ROLE_SUFFIX);
            }
        });

        if (logger.isDebugEnabled()){
            logger.debug("jrsInternalRolesNotInRoleMap: " +roleCollectionToString(jrsInternalRolesNotInRoleMap));
        }

       //assign default internal roles if needed
        Collection<Role> defaultInternalRolesToAdd = CollectionUtils.subtract(getNewDefaultInternalRoles(user.getUsername()), jrsInternalRolesNotInRoleMap);
        jrsInternalRolesNotInRoleMap.addAll(defaultInternalRolesToAdd);

        Collection<Role> newUserRoles = remoteExternalUserRoles;
        newUserRoles.addAll(jrsInternalRolesNotInRoleMap);

        if (logger.isDebugEnabled()){
            logger.debug("internal and external roles to persist: " +roleCollectionToString(newUserRoles));
        }

        persistRoles(new HashSet<Role>(newUserRoles));
        user.setRoles(new HashSet<Role>(newUserRoles));
		updateUserAttributes(user);
        getUserAuthorityService().putUser(new ExecutionContextImpl(), user);

    }

	/**
	 * A 'hook' method to extend when additional user columns need to be populated (e.g. emailAddress)
	 *
	 * @param user
	 */
	protected void updateUserAttributes(User user) {
	}

	/**
     * Get a set of roles based on the given GrantedAuthority[]. Roles are created
     * in the metadata if they do not exist.
     *
     *
	 * @param authorities from authenticated user
	 * @param tenantId
	 * @return Set of externally defined Roles
	 *
	 * protected scope for unit testing
     */
    protected Set<Role> convertGrantedAuthoritiesToRoles(List<GrantedAuthority> authorities, String tenantId) {
        Set<Role> set = new HashSet<Role>();

        if (authorities == null || authorities.isEmpty())
            return set;

		final UserAuthorityService userAuthorityService = getUserAuthorityService();
        for (GrantedAuthority auth : authorities) {
           	String authorityName = auth.getAuthority();
			if (authorityName == null || !isRoleSynchronizable(authorityName))
				continue;

			Role role = userAuthorityService.newRole(new ExecutionContextImpl());
			String internalRoleName = organizationRoleMap.get(authorityName.toUpperCase());
			if (internalRoleName != null) {
				if (internalRoleName.endsWith(ROLE_SUFFIX)) {
					internalRoleName = internalRoleName.substring(0, internalRoleName.length() - ROLE_SUFFIX.length());
					role.setTenantId(tenantId);
				}
				role.setRoleName(internalRoleName);
				role.setExternallyDefined(false);  //role is mapped to internal
			}
			else {
				String authorityNameValid = purgeRoleNameOfInvalidChars(authorityName);
				if (authorityNameValid.length() == 0) {
					if (Level.WARN.isGreaterOrEqual(logger.getEffectiveLevel())) {
						ProcessorData processorData = ProcessorData.getInstance();
						UserDetails userDetails = (UserDetails) processorData.getData(EXTERNAL_AUTH_DETAILS);

						logger.warn("External role " + authorityName + " has forbidden characters only " +
								"according to permittedExternalRoleNameRegex: " + permittedExternalRoleNameRegex +
								". Skipping it for user: " +
								(userDetails != null && userDetails.getUsername() != null ? userDetails.getUsername() : ""));
					}

					continue;
				}
				authorityName = authorityNameValid.toUpperCase().trim();

				role.setRoleName(authorityName);
				role.setExternallyDefined(true);
				role.setTenantId(tenantId);
			}

            set.add(role);
        }
        return set;
    }

	/**
	 * Extention point for deciding whether the role is synchronizable.  By default, the regex list is empty causing all
	 * the roles to be entered into db.
	 *
	 * @param roleName - role to test whether it matches any of regex's in #permittedRolesRegex
	 * @return whether the role matches any regex.
	 */
	protected boolean isRoleSynchronizable(String roleName) {
		if (this.permittedRolesRegex.isEmpty())
			return true;

		for (Pattern regex : this.permittedRolesRegex) {
			if (regex.matcher(roleName).matches())
				return true;
		}

		logger.warn("Role " + roleName + " did not match any regex in permittedRolesRegex list");
		return false;
	}

	private String roleCollectionToString(Collection coll) {
        Iterator it = coll.iterator();
        StringBuffer rolesPrint = new StringBuffer();
        while (it.hasNext()) {
            String s = ((Role) it.next()).getRoleName();
            rolesPrint.append(s).append("\n");
        }
        return rolesPrint.toString();
    }

    /**
     * Get a set of roles that are the defaults for a new external user. Roles are created
     * in the metadata if they do not exist.
     *
     * @return Set of internally defined Roles
     */
    private Set getNewDefaultInternalRoles(String username) {
        ExecutionContext executionContext = new ExecutionContextImpl();
        Set<Role> set = new HashSet<Role>();

		List<String> internalRoles = Collections.<String>emptyList();
        if (this.adminUsernames != null && this.adminUsernames.contains(username)
				&& this.defaultAdminRoles != null && this.defaultAdminRoles.size() > 0)
			internalRoles = this.defaultAdminRoles;
        else if (this.defaultInternalRoles != null && this.defaultInternalRoles.size() > 0)
			internalRoles = this.defaultInternalRoles;
		else
            return set;

        for (String roleName : internalRoles) {
            Role role = getUserAuthorityService().getRole(executionContext, roleName);
            if (role == null) {
                role = getUserAuthorityService().newRole(executionContext);
                role.setRoleName(roleName);
                role.setExternallyDefined(false);
                getUserAuthorityService().putRole(executionContext, role);
            }

            set.add(role);
        }
        return set;
    }

	private Role getOrCreateRole(Role role) {
		Role existingRole = null;
		UserAuthorityService userAuthorityService = getUserAuthorityService();

		if (userAuthorityService instanceof PersistentObjectResolver)
			existingRole = (Role) ((PersistentObjectResolver) userAuthorityService).getPersistentObject(role);

		//when internal role name&tenantId coincide with the external role, modify the external
		// role name in order to avoid overwriting the roles (bug 31324).
		if (existingRole != null && role.isExternallyDefined() && !existingRole.isExternallyDefined() ) {
			role.setRoleName(role.getRoleName() + "_" + this.conflictingExternalInternalRoleNameSuffix);
			existingRole = null;
		}

		//role does not exist.  Need to create it.
		if (existingRole == null)
			userAuthorityService.putRole(new ExecutionContextImpl(), role);
		return role;
    }

    public List getDefaultInternalRoles() {
        return defaultInternalRoles;
    }

    public void setDefaultInternalRoles(List defaultInternalRoles) {
        this.defaultInternalRoles = defaultInternalRoles;
    }

	public void setExternalAuthProperties(ExternalAuthProperties externalAuthProperties) {
		this.externalAuthProperties = externalAuthProperties;
	}

	public ExternalAuthProperties getExternalAuthProperties() {
		return externalAuthProperties;
	}

	public String getPermittedExternalRoleNameRegex() {
		return permittedExternalRoleNameRegex;
	}

	public void setPermittedExternalRoleNameRegex(String permittedExternalRoleNameRegex) {
		this.permittedExternalRoleNameRegex = permittedExternalRoleNameRegex;
	}

	public void setOrganizationRoleMap(Map<String, String> organizationRoleMapParam) {
		this.organizationRoleMap = organizationRoleMapParam;
	}

	/** Method replacing invalid role chars (according to permittedExternalRoleNamePattern) with _   */
	private String purgeRoleNameOfInvalidChars(String authorityName) {
		Matcher authorityMatcher = permittedExternalRoleNamePattern.matcher(authorityName);
		StringBuffer validAuthorityNameBuff = new StringBuffer();
		while (authorityMatcher.find()) {
			String matchedSubstr = authorityMatcher.group().trim();
			if (matchedSubstr.length() > 0) {
				if (validAuthorityNameBuff.length() > 0)
					validAuthorityNameBuff.append("_");
				validAuthorityNameBuff.append(matchedSubstr);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("External role " + authorityName + " has forbidden characters " +
					"according to permittedExternalRoleNameRegex: " + permittedExternalRoleNameRegex +
					". Replacing those character sequences with _.  Result: " + validAuthorityNameBuff);
		}

		return validAuthorityNameBuff.toString();
	}

	/**
	 * Names of external users that are converted into admins
	 * @param adminUsernames
	 */
	public void setAdminUsernames(List<String> adminUsernames) {
		this.adminUsernames = adminUsernames;
	}

	public List<String> getAdminUsernames() {
		return adminUsernames;
	}

	/**
	 * Default admin roles that are assigned to the users in {@link #adminUsernames}
	 * @param defaultAdminRoles
	 */
	public void setDefaultAdminRoles(List<String> defaultAdminRoles) {
		this.defaultAdminRoles = defaultAdminRoles;
	}

	public List<String> getDefaultAdminRoles() {
		return defaultAdminRoles;
	}

	public void setConflictingExternalInternalRoleNameSuffix(String conflictingExternalInternalRoleNameSuffix) {
		this.conflictingExternalInternalRoleNameSuffix = conflictingExternalInternalRoleNameSuffix;
	}

	public String getConflictingExternalInternalRoleNameSuffix() {
		return conflictingExternalInternalRoleNameSuffix;
	}

    public Map<String, String> getOrganizationRoleMap() {
        return organizationRoleMap;
    }

	public void setPermittedRolesRegex(List<String> permittedRoleRegexList) {
		for (String r : permittedRoleRegexList)
			this.permittedRolesRegex.add(Pattern.compile(r));
	}
}
