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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.IlikeEscapeAwareExpression;
import com.jaspersoft.jasperserver.api.metadata.common.util.DatabaseCharactersEscapeResolver;
import com.jaspersoft.jasperserver.api.metadata.tenant.service.TenantPersistenceResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoTenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author swood
 * @version $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserAuthorityServiceImpl extends HibernateDaoImpl implements UserDetailsService, ExternalUserService,
        UserAuthorityPersistenceService, Diagnostic {

    private static final String HIBERNATE_ESCAPE_CHAR = "\\";
    protected static final Log log = LogFactory.getLog(UserAuthorityServiceImpl.class);
    private ResourceFactory objectFactory;
    private ResourceFactory persistentClassFactory;
    private ProfileAttributeService profileAttributeService;
    private TenantPersistenceResolver tenantPersistenceResolver;
    private AuditContext auditContext;

    private List defaultInternalRoles;
    private DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver;
    private boolean isUsernameCaseSensitive;
    private Pattern passwordPattern = Pattern.compile("^.*$");

    /**
     * Helper findByCriteria methods to cache all results
     *
     */
    private List findByCriteria(DetachedCriteria criteria){
    	HibernateTemplate template = getHibernateTemplate();
    	//template.setCacheQueries(true);
    	return template.findByCriteria(criteria);
    }
    
    private List findByCriteria(DetachedCriteria criteria, int firstResult, int maxResults){
    	HibernateTemplate template = getHibernateTemplate();
    	//template.setCacheQueries(true);
    	return template.findByCriteria(criteria, firstResult, maxResults);
    }

    
    
    public void setDatabaseCharactersEscapeResolver(DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver) {
        this.databaseCharactersEscapeResolver = databaseCharactersEscapeResolver;
    }

    public DatabaseCharactersEscapeResolver getDatabaseCharactersEscapeResolver() {
        return this.databaseCharactersEscapeResolver;
    }

    public ResourceFactory getObjectMappingFactory() {
        return objectFactory;
    }

    public void setObjectMappingFactory(ResourceFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ResourceFactory getPersistentClassFactory() {
        return persistentClassFactory;
    }

    public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
        this.persistentClassFactory = persistentClassFactory;
    }

    public ProfileAttributeService getProfileAttributeService() {
        return profileAttributeService;
    }

    public void setProfileAttributeService(ProfileAttributeService pas) {
        this.profileAttributeService = pas;
    }

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    public boolean isUsernameCaseSensitive() {
        return isUsernameCaseSensitive;
    }

    public void setUsernameCaseSensitive(boolean usernameCaseSensitive) {
        isUsernameCaseSensitive = usernameCaseSensitive;
    }

    protected RepoUser getRepoUser(ExecutionContext context, String username) {
        return getRepoUser(username, null);
    }

    protected RepoUser getRepoUser(String username, String tenantId) {
        RepoTenant tenant = getPersistentTenant(tenantId, false);
        if (tenant == null && !isNullTenant(tenantId)) {
            //if the requested tenant was not found, return null
            if (log.isDebugEnabled()) {
                log.debug("Tenant " + tenantId + " not found, returning null user.");
            }
            return null;
        }

        // Search with sensitivity according to the configuration.
        List userList = findByCriteria(createUserSearchCriteria(username, tenant,
                isUsernameCaseSensitive));

        // Before 5.0.1 we had case sensitive usernames. But there was a bug 24226 opened with at least 4 customer
        // cases that username should be case insensitive. Because of previous case sensitive logic existing customers
        // could have several users whose username is the same when case insensitive logic is applied but different
        // when case sensitive logic is applied. So, we decided to implement adaptive logic which will allow both worlds
        // live together.
        // It means that by default we are trying to find the user using case insensitive logic (this is the matter of
        // configuration). If more then 1 user was found we do one more search with case sensitive logic applied.
        if (userList.size() > 1) {
            log.warn(userList.size() + " users were found during case insensitive search for \"" + username +
                    "\". Retrying with case sensitive search.");

            // Case sensitive search.
            userList = findByCriteria(createUserSearchCriteria(username, tenant, true));
        }

        return extractUser(username, tenantId, userList);
    }

    private RepoUser extractUser(String username, String tenantId, List userList) {
        RepoUser user = null;

        if (userList.isEmpty()) {
            log.debug("User not found with username \"" + username
                    + "\" in tenant " + tenantId + ".");
        } else if (userList.size() == 1) {
            user = (RepoUser) userList.get(0);
        }

        return user;
    }

    private DetachedCriteria createUserSearchCriteria(String username, RepoTenant tenant,
            boolean isCaseSensitive) {
        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentUserClass());
        criteria.add(isCaseSensitive ? Restrictions.eq("username", username) :
                new IlikeEscapeAwareExpression("username", databaseCharactersEscapeResolver.getEscapedText(username.trim()), MatchMode.EXACT));
        criteria.add(Restrictions.eq("tenant", tenant));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);

        return criteria;
    }

    protected RepoUser getRepoUser(ExecutionContext context, User user) {
        return getRepoUser(user.getUsername(), user.getTenantId());
    }

    protected Class getPersistentUserClass() {
        return getPersistentClassFactory().getImplementationClass(User.class);
    }

    protected Class getPersistentTenantClass() {
        return getPersistentClassFactory().getImplementationClass(Tenant.class);
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
      */
    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser(ExecutionContext context, String username) {
        RepoUser user = getRepoUser(context, username);
        User userDTO = null;
        if (user != null) {
            userDTO = (User) user.toClient(getObjectMappingFactory());
            List attrs = getProfileAttributeService().
                    getProfileAttributesForPrincipal(null, user);
            userDTO.setAttributes(attrs);
        } else {
            log.debug("No such user as: " + username);
        }
        return userDTO;
    }

    protected RepoUser getRepoUser(ExecutionContext context, Long id) {
        RepoUser user = (RepoUser) getHibernateTemplate().load(getPersistentUserClass(), id);
        return user;
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
      */
    protected User getUser(ExecutionContext context, Long id) {
        RepoUser user = getRepoUser(context, id);
        User userDTO = null;
        if (user != null) {
            userDTO = (User) user.toClient(getObjectMappingFactory());
        }
        return userDTO;
    }

    /* (non-Javadoc)
      * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
      */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User u = getUser(null, username);

        if (u == null) {
            throw new UsernameNotFoundException("User not found with username \"" + username + "\"");
        } else {
            return makeUserDetails(u);
        }
    }

    protected MetadataUserDetails makeUserDetails(User user) {
        return new MetadataUserDetails(user);
    }

    /*
      * 11-11-08 bob
      * Modified to deal with bogus behavior allowed by a method signature that isn't specific enough.
      * This fixes bug 12382.
      * This method expects a client object (UserImpl),
      * but its signature just says User which has both RepoUser and UserImpl (client) implementations.
      * When you pass in a RepoUser, it does a copyFromClient() on it, which is wrong, but doesn't
      * burn anyone most of the time.
      * It DOES burn you when you have password encryption turned on, in which case it encrypts your already-encrypted password.
      * Guess what, you can't log in anymore!
      *
      * TODO Per Sherman, if there are methods calling putUser() with a RepoUser, they need to be fixed.
      * I looked at all the callers (about 20) and found three that do this: addRole(), removeRole(), and removeAllRoles().
      * We should probably change the interface so it can't be called with RepoUser, but we should probably look at other API's.
      *  (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#putUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.User)
      */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void putUser(ExecutionContext context, User aUser) {
        if (!aUser.isExternallyDefined() ? isPasswordStrongEnough(aUser.getPassword()) : true) {
            doPutUser(context, aUser);
        } else {
            throw new JSException("Weak password stored in the database does not conform the allowed pattern.");
        }
    }

    protected void doPutUser(ExecutionContext context, User aUser) {
        RepoUser existingUser;
        if (aUser instanceof RepoUser) {
            existingUser = (RepoUser) aUser;
        } else {
            existingUser = getRepoUser(context, aUser);
            if (existingUser == null) {
                existingUser = (RepoUser) getPersistentClassFactory().newObject(User.class);
            }
            updatePersistentUser(aUser, existingUser);
        }

        addPropertiesToUserEvent(new String[]{"createUser", "updateUser"}, existingUser);
        getHibernateTemplate().saveOrUpdate(existingUser);
    }

    protected boolean isPasswordStrongEnough(String password) {
        return passwordPattern.matcher(password).matches();
    }

    protected void updatePersistentUser(User user, RepoUser persistentUser) {
        persistentUser.copyFromClient(user, this);
    }

    /**
     * return everything for now
     *
     *  (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUsers(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<User> getUsers(ExecutionContext context, FilterCriteria filterCriteria) {
        // make User DTOs
        List results = getHibernateTemplate().loadAll(getPersistentUserClass());
        List userDTOs = null;

        if (results != null) {
            userDTOs = new ArrayList(results.size());
            Iterator it = results.iterator();
            while (it.hasNext()) {
                RepoUser u = (RepoUser) it.next();
                User newUser = (User) u.toClient(getObjectMappingFactory());
                userDTOs.add(newUser);
            }
        }
        return userDTOs;
    }

    /**
     * return everything for now
     *
     *  (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUsersByCriteria(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, org.hibernate.criterion.DetachedCriteria)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<User> getUsersByCriteria(ExecutionContext context, DetachedCriteria detachedCriteria) {
        // make User DTOs
        List results = findByCriteria(detachedCriteria);
        List userDTOs = null;

        if (results != null) {
            userDTOs = new ArrayList(results.size());
            Iterator it = results.iterator();
            while (it.hasNext()) {
                RepoUser u = (RepoUser) it.next();
                User newUser = (User) u.toClient(getObjectMappingFactory());
                userDTOs.add(newUser);
            }
        }
        return userDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getUsersCountExceptExcluded(ExecutionContext executionContext, final Set<String> excludedUserNames,
                                           final boolean excludeDisabledUsers) {
        return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getPersistentUserClass());
                if (excludedUserNames != null && excludedUserNames.size() > 0) {
                    criteria.add(Restrictions.not(Restrictions.in("username", excludedUserNames)));
                }
                if (excludeDisabledUsers) {
                    criteria.add(Restrictions.eq("enabled", true));
                }
                criteria.setProjection(Projections.count("id"));
                return criteria.uniqueResult();
            }
        });
    }

    /**
     * DTO for the User interface
     *
     * (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#newUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
     */
    public User newUser(ExecutionContext context) {
        return (User) getObjectMappingFactory().newObject(User.class);
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#disableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
      */
    protected boolean disableUser(ExecutionContext context, Long id) {
        RepoUser user = getRepoUser(context, id);
        if (user != null && user.isEnabled()) {
            user.setEnabled(false);
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#disableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
      */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public boolean disableUser(ExecutionContext context, String username) {
        RepoUser user = getRepoUser(context, username);
        if (user != null && user.isEnabled()) {
            user.setEnabled(false);
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#enableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
      */
    protected boolean enableUser(ExecutionContext context, Long id) {
        RepoUser user = getRepoUser(context, id);
        if (user != null && !user.isEnabled()) {
            user.setEnabled(true);
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#enableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
      */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public boolean enableUser(ExecutionContext context, String username) {
        RepoUser user = getRepoUser(context, username);
        if (user != null && !user.isEnabled()) {
            user.setEnabled(true);
            return true;
        } else {
            return false;
        }
    }

    protected void addPropertiesToUserEvent(final String[] auditEventTypes, final User user) {
        auditContext.doInAuditContext(auditEventTypes, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                if (user != null) {
                    auditContext.addPropertyToAuditEvent("username", user.getUsername(), auditEvent);
                    auditContext.addPropertyToAuditEvent("tenantId", user.getTenantId(), auditEvent);
                    auditContext.addPropertyToAuditEvent("email", user.getEmailAddress(), auditEvent);
                    auditContext.addPropertyToAuditEvent("fullName", user.getFullName(), auditEvent);
                    auditContext.addPropertyToAuditEvent("passwordChangeTime", user.getPreviousPasswordChangeTime(), auditEvent);
                    auditContext.addPropertyToAuditEvent("enabled", user.isEnabled(), auditEvent);
                    auditContext.addPropertyToAuditEvent("externallyDefined", user.isExternallyDefined(), auditEvent);

                    List attrs = getProfileAttributeService().
                            getProfileAttributesForPrincipal(null, user);

                    if (attrs != null && !attrs.isEmpty()) {
                        for (Object attribute: attrs) {
                            auditContext.addPropertyToAuditEvent("attribute", attribute, auditEvent);
                        }
                    }

                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        for (Object roleObject: user.getRoles()) {
                            Role role = (Role) roleObject;
                            auditContext.addPropertyToAuditEvent("roleName", role.getRoleName(), auditEvent);
                        }
                    }
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteUser (ExecutionContext context, String username) {
        RepoUser user = getRepoUser(context, username);
        if (user == null) {
            return;
        }

        addPropertiesToUserEvent(new String[] {"deleteUser"}, user);

        removeAllRoles(context, (User) user);

        getHibernateTemplate().delete(user);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void addRole(ExecutionContext context, User user, Role role) {
        if (user == null) {
            return;
        }

        RepoUser existingUser = getRepoUser(context, user);
        if (existingUser != null) {
            RepoRole existingRole = getRepoRole(role);
            existingUser.addRole(existingRole);
            doPutUser(null, existingUser);
        }
        user.addRole(role);
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void removeRole(ExecutionContext context, User user, Role role) {
        if (user == null || role == null) {
            return;
        }

        RepoUser existingUser = getRepoUser(context, user);

        if (existingUser != null) {
            RepoRole r = getRepoRole(role);
            if (r != null) {
                existingUser.removeRole(r);
                doPutUser(null, existingUser);
            } else {
                log.debug("removeRole: No role such as " + role.getRoleName());
            }
        } else {
            log.debug("removeRole: No user such as " + user.getUsername());
        }
        user.removeRole(role);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void removeAllRoles(ExecutionContext context, User user) {
        if (user == null) {
            return;
        }

        RepoUser existingUser = getRepoUser(context, user);
        if (existingUser == null) {
            return;
        }

        /*
              for (Iterator it = existingUser.getRoles().iterator(); it.hasNext(); ) {
                  Role role = (Role) it.next();
                  existingUser.removeRole(role);
                  user.removeRole(role);
              }
          */

        existingUser.getRoles().clear(); //to avoid ConcurrentModificationException
        doPutUser(null, existingUser);

    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
      */
    @Transactional(propagation = Propagation.REQUIRED)
    public Role getRole(ExecutionContext context, String roleName) {
        RepoRole repoRole = getRepoRole(context, roleName);
        Role role = null;
        if (repoRole != null) {
            role = (Role) repoRole.toClient(getObjectMappingFactory());
        }
        return role;
    }

    protected Class getPersistentRoleClass() {
        return getPersistentClassFactory().getImplementationClass(Role.class);
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
      */
    protected RepoRole getRepoRole(ExecutionContext context, String roleName) {
        return getRepoRole(roleName, (String) null);
    }

    protected RepoRole getRepoRole(Role role) {
        return getRepoRole(role.getRoleName(), role.getTenantId());
    }

    protected RepoRole getRepoRole(String roleName, String tenantId) {
        RepoTenant tenant = getPersistentTenant(tenantId, false);
        if (tenant == null && !isNullTenant(tenantId)) {
            //if the requested tenant was not found, return null
            if (log.isDebugEnabled()) {
                log.debug("Tenant " + tenantId + " not found, returning null role");
            }
            return null;
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentRoleClass());
        if (tenant == null) {
            criteria.
                    add(Restrictions.isNull("tenant")).
                    add(Restrictions.eq("roleName", roleName));
        } else {
            criteria.add(Restrictions.naturalId().
                    set("tenant", tenant).
                    set("roleName", roleName));
        }
        List roleList = findByCriteria(criteria);
        RepoRole role = null;
        if (roleList.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Role not found with role name \"" + roleName + "\""
                        + (tenantId == null ? "" : (", tenant \"" + tenantId + "\"")));
            }
        } else {
            role = (RepoRole) roleList.get(0);
        }
        return role;
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#putRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
      */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void putRole(ExecutionContext context, Role aRole) {
        RepoRole existingRole = getRepoRole(aRole);
        log.debug("putRole: " + aRole.getRoleName() + ", " + existingRole);
        if (existingRole == null) {
            existingRole = (RepoRole) getPersistentClassFactory().newObject(Role.class);
            log.debug("New Object");
        }
        Set existingRoleUserIds = getIdsFromUserSet(existingRole.getUsers());

        existingRole.copyFromClient(aRole, this);

        Set newRoleUserIds = getIdsFromUserSet(existingRole.getUsers());
        addParametersToRoleManagementAuditEvent(new String[] {"createRole", "updateRole"}, existingRole, false);
        addUserIdsToRoleManagementAuditEvent(existingRoleUserIds, newRoleUserIds);

        getHibernateTemplate().saveOrUpdate(existingRole);

        updateRoleUsers(context, existingRole, aRole);
    }

    private void updateRoleUsers(ExecutionContext context, RepoRole existingRole, Role aRole) {

        Set repoUsers = existingRole.getUsers();
        for (Iterator it = repoUsers.iterator(); it.hasNext();) {
            RepoUser repoUser = (RepoUser) it.next();
            repoUser.getRoles().remove(getPersistentObject(aRole));
        }

        Set users = aRole.getUsers();
        for (Iterator it = users.iterator(); it.hasNext();) {
            User user = (User) it.next();
            addRole(context, user, aRole);
        }
    }

    /**
     * Return everything for now
     *
     *  (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getRoles(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List getRoles(ExecutionContext context, FilterCriteria filterCriteria) {
        List results = getHibernateTemplate().loadAll(getPersistentRoleClass());
        List roleDTOs = null;

        if (results != null) {
            roleDTOs = new ArrayList(results.size());
            Iterator it = results.iterator();
            while (it.hasNext()) {
                RepoRole r = (RepoRole) it.next();
                Role newRole = (Role) r.toClient((ResourceFactory) getObjectMappingFactory());
                roleDTOs.add(newRole);
            }
        }
        return roleDTOs;
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#newRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
      */
    public Role newRole(ExecutionContext context) {
        // return a Role DTO
        return (Role) getObjectMappingFactory().newObject(Role.class);
    }

    protected void addParametersToRoleManagementAuditEvent(final String[] auditEventTypes, final RepoRole role, final boolean logUsers) {
        auditContext.doInAuditContext(auditEventTypes, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent("roleName", role.getRoleName(), auditEvent);
                auditContext.addPropertyToAuditEvent("tenantId", role.getTenantId(), auditEvent);
                auditContext.addPropertyToAuditEvent("externallyDefined", role.isExternallyDefined(), auditEvent);
                if (role.getAttributes() != null && !role.getAttributes().isEmpty()) {
                    for (Object attribute: role.getAttributes()) {
                        auditContext.addPropertyToAuditEvent("attribute", attribute, auditEvent);
                    }
                }
                if (logUsers && role.getUsers() != null && !role.getUsers().isEmpty()) {
                    for (Object userObject: role.getUsers()) {
                        RepoUser user = (RepoUser)userObject;
                        auditContext.addPropertyToAuditEvent("userId", user.getId(), auditEvent);
                    }
                }
            }
        });
    }

    private Set getIdsFromUserSet(Set userSet) {
        if (userSet != null && !userSet.isEmpty()) {
            Set userIds = new HashSet(userSet.size());
            for (Object user: userSet) {
                userIds.add(((RepoUser)user).getId());
            }
            return userIds;
        } else {
            return null;
        }
    }

    private void addUserIdsToRoleManagementAuditEvent(final Set usersIdsBeforeUpdate, final Set usersIdsAfterUpdate) {
        auditContext.doInAuditContext(new String[] {"createRole", "updateRole"},
                new AuditContext.AuditContextCallbackWithEvent() {
                    public void execute(AuditEvent auditEvent) {
                        if (usersIdsAfterUpdate != null) {
                            for (Iterator i = usersIdsAfterUpdate.iterator(); i.hasNext(); ) {
                                Object id = i.next();
                                if (usersIdsBeforeUpdate!=null && usersIdsBeforeUpdate.remove(id)){
                                    i.remove();
                                } else {
                                	auditContext.addPropertyToAuditEvent("addedUserId", id, auditEvent);
                                }
                            }
                        }

                        if (usersIdsBeforeUpdate != null) {
                            for (Object removedId: usersIdsBeforeUpdate) {
                                auditContext.addPropertyToAuditEvent("removedUserId", removedId, auditEvent);
                            }
                        }
                    }
                });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteRole(ExecutionContext context, String roleName) {
        RepoRole role = getRepoRole(context, roleName);
        if (role == null) {
            return;
        }

        addParametersToRoleManagementAuditEvent(new String[] {"deleteRole"}, role, true);

        // Get all users that have this role and remove the role from them
        Set userList = role.getUsers();
        for (Iterator it = userList.iterator(); it.hasNext(); ) {
            RepoUser u = (RepoUser) it.next();
            u.removeRole(role);
        }

//		role.getUsers().clear();

        // then delete the role
        getHibernateTemplate().delete(role);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getUsersNotInRole(ExecutionContext context, String roleName)
    {
        List allUsers = getUsers(context, null);
        List usersInRole = getUsersInRole(context, roleName);
        allUsers.removeAll(usersInRole);

        return allUsers;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getUsersInRole(ExecutionContext context, String roleName)
    {
        RepoRole repoRole = getRepoRole(context, roleName);
        Set repoUsers = repoRole.getUsers();
        List users = new ArrayList();

        for (Iterator it = repoUsers.iterator(); it.hasNext();)
        {
            RepoUser repoUser = (RepoUser) it.next();
            User user = (User) repoUser.toClient(getObjectMappingFactory());
            users.add(user);
        }

        return users;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List getAssignedRoles(ExecutionContext context, String userName)
    {
        RepoUser repoUser = getRepoUser(context, userName);
        Set repoRoles = repoUser.getRoles();

        List roles = new ArrayList();

        for (Iterator it = repoRoles.iterator(); it.hasNext();) {
            RepoRole repoRole = (RepoRole) it.next();
            Role role = (Role) repoRole.toClient(getObjectMappingFactory());
            roles.add(role);
        }

        return roles;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List getAvailableRoles(ExecutionContext context, String userName)
    {
        List allRoles = getRoles(context, null);
        List assignedRoles = getAssignedRoles(null, userName);
        allRoles.removeAll(assignedRoles);
        return allRoles;
    }

    public boolean roleExists(ExecutionContext context, String roleName)
    {
        return (getRole(context, roleName) != null);
    }

    /*
      * TODO this should be generalized. Maybe get the Repo* objects to return a
      * DetachedCriteria filled with the key from the client object?
      *
      *  (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver#getPersistentObject(java.lang.Object)
      */
    @Transactional(propagation = Propagation.REQUIRED)
    public Object getPersistentObject(Object clientObject) {
        if (clientObject instanceof Role) {
            Role r = (Role) clientObject;
            return getRepoRole(r);
        } else if (clientObject instanceof User) {
            User u = (User) clientObject;
            return getRepoUser(null, u);
        } else if (clientObject instanceof Tenant) {
            return getPersistentTenant(((Tenant)clientObject).getId(), true);
        }
        return null;
    }

    /**
     * From an external UserDetails + GrantedAuthority[], maintain the shadow internal user
     *
     * @param externalUserDetails
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public User maintainInternalUser(UserDetails externalUserDetails, Collection<? extends GrantedAuthority> authorities) {

        log.debug("External user: " + externalUserDetails.getUsername());

        User user = getUser(new ExecutionContextImpl(), externalUserDetails.getUsername());

        if (user == null) {
            user = createNewExternalUser(externalUserDetails.getUsername());
        }

        Set roles = persistRoles(getRolesFromGrantedAuthorities(authorities));
        alignInternalAndExternalUser(roles, user);

        return user;
    }

    /**
     * From an external user (string user name) + GrantedAuthority[], maintain the shadow internal user
     *
     * @param userName
     * @param authorities
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public User maintainInternalUser(String userName, Collection<? extends GrantedAuthority> authorities) {

        log.debug("External user(String): " + userName);

        User user = getUser(new ExecutionContextImpl(), userName);

        if (user == null) {
            user = createNewExternalUser(userName);
        }

        Set roles = persistRoles(getRolesFromGrantedAuthorities(authorities));
        alignInternalAndExternalUser(roles, user);

        return user;
    }
    /**
     * New user created from given authentication details. No password is set or needed.
     * Roles are set elsewhere.
     *
     * @param userName
     * @return created User
     */
    protected User createNewExternalUser(String userName) {
        User user = newUser(new ExecutionContextImpl());
        user.setUsername(userName);
        // If it is externally authenticated, no save of password
        //user.setPassword(userDetails.getPassword());
        user.setFullName(userName); // We don't know the real name
        user.setExternallyDefined(true);
        user.setEnabled(true);
        log.warn("Created new external user: " + user.getUsername());
        return user;
    }

    /**
     * Ensure the external user has the right roles. Roles attached to the userDetails are the definitive list
     * of externally defined roles.
     *
     * @param externalRoles
     * @param user
     */
    protected void alignInternalAndExternalUser(Set externalRoles, User user) {

        final Predicate externallyDefinedRoles = new Predicate() {
            public boolean evaluate(Object input) {
                if (!(input instanceof Role)) {
                    return false;
                }
                return ((Role) input).isExternallyDefined();
            }
        };

        Set currentRoles = user.getRoles();

        // we may have a new user, so always persist them
        boolean persistUserNeeded = (currentRoles.size() == 0);
/*
    	// If it is externally authenticated, no save of password
    	if (!user.getPassword().equals(userDetails.getPassword())) {
    		user.setPassword(userDetails.getPassword());
    		persistUserNeeded = true;
    	}

*/    	Collection currentExternalRoles = CollectionUtils.select(user.getRoles(), externallyDefinedRoles);
        if (log.isDebugEnabled()) {
            log.debug("Login of external User: " + user.getUsername() );
            log.debug("Roles from authentication:\n" + roleCollectionToString(externalRoles));
            log.debug("Current roles from metadata:\n" + roleCollectionToString(user.getRoles()));
            log.debug("Current external roles for user from metadata: " + user.getUsername() + "\n" + roleCollectionToString(currentExternalRoles));
        }

        /*
           * If we have new external roles, we want to add them
           */
        Collection newExternalRoles = CollectionUtils.subtract(externalRoles, currentExternalRoles);

        if (newExternalRoles.size() > 0) {
            currentRoles.addAll(newExternalRoles);
            if (log.isWarnEnabled()) {
                log.warn("Added following external roles to: " + user.getUsername() + "\n" + roleCollectionToString(newExternalRoles));
            }
            persistUserNeeded = true;
        }

        /*
           * If external roles have been removed, we need to remove them
           */
        Collection rolesNeedingRemoval = CollectionUtils.subtract(currentExternalRoles, externalRoles);

        if (rolesNeedingRemoval.size() > 0) {
            currentRoles.removeAll(rolesNeedingRemoval);
            if (log.isWarnEnabled()) {
                log.warn("Removed following external roles from: " + user.getUsername() + "\n" + roleCollectionToString(rolesNeedingRemoval));
            }
            persistUserNeeded = true;
        }

        /*
           * If we have new default internal roles, we want to add them
           */
        Collection defaultInternalRolesToAdd = CollectionUtils.subtract(getNewDefaultInternalRoles(), currentRoles);

        if (defaultInternalRolesToAdd.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Default internal roles: " + roleCollectionToString(getNewDefaultInternalRoles()));
            }
            currentRoles.addAll(defaultInternalRolesToAdd);
            if (log.isWarnEnabled()) {
                log.warn("Added following new default internal roles to: " + user.getUsername() + "\n" + roleCollectionToString(defaultInternalRolesToAdd));
            }
            persistUserNeeded = true;
        }

        if (persistUserNeeded) {
            if (log.isWarnEnabled()) {
                log.warn("Updated user: " + user.getUsername() + ". Roles are now:\n" + roleCollectionToString(currentRoles));
            }
            user.setRoles(currentRoles);
            // persist user and roles
            doPutUser(new ExecutionContextImpl(), user);
            if (log.isWarnEnabled()) {
                log.warn("Updated user: " + user.getUsername() + ". Roles are now:\n" + roleCollectionToString(currentRoles));
            }
        }

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
     * Get a set of roles based on the given GrantedAuthority[]. Roles are created
     * in the metadata if they do not exist.
     *
     * @param authorities from authenticated user
     * @return Set of externally defined Roles
     */
    protected Set getRolesFromGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set set = new HashSet();

        if (authorities == null || authorities.isEmpty())
            return set;

        for (GrantedAuthority authority : authorities) {
            String authorityName = authority.getAuthority();

            // Make spaces in the authority name be underscores

            authorityName = authorityName.replace(' ', '_');

            if (!authorityName.startsWith("ROLE_")) {
                authorityName = "ROLE_" + authorityName;
            }

            Role r = newRole(new ExecutionContextImpl());
            r.setRoleName(authorityName);
            r.setExternallyDefined(true);
            set.add(r);
        }
        return set;
    }

    /*
    *
    */
    protected Set persistRoles(Set roles) {
        Set persistedRoles = new HashSet();
        for (Iterator iter = roles.iterator(); iter.hasNext(); ) {
            Role r = (Role) iter.next();
            persistedRoles.add(getOrCreateRole(r.getRoleName(), r.isExternallyDefined()));
        }
        return persistedRoles;
    }

    /**
     * @return the Authentication corresponding to the principal who used
     * the "Switch User" feature to login as the current principal if any,
     * or null, if the current principal is not a switched user.
     */
    public static Authentication getSourceAuthentication() {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        Authentication original = null;

        // iterate over granted authorities and find the 'switch user' authority
        Collection<? extends GrantedAuthority> authorities = current.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            // check for switch user type of authority
            if (authority instanceof SwitchUserGrantedAuthority) {
                original = ((SwitchUserGrantedAuthority) authority).getSource();
                log.debug("Found original switch user granted authority [" + original + "]");
            }
        }

        return original;
    }

    public static boolean isUserSwitched() {
        return (getSourceAuthentication() != null);
    }

    /**
     * Get a set of roles that are the defaults for a new external user. Roles are created
     * in the metadata if they do not exist.
     *
     * @return Set of internally defined Roles
     */
    private Set getNewDefaultInternalRoles() {
        Set set = new HashSet();

        if (getDefaultInternalRoles() == null || getDefaultInternalRoles().size() == 0)
            return set;

        for (int i = 0; i < getDefaultInternalRoles().size(); i++) {
            String roleName = (String) getDefaultInternalRoles().get(i);

            set.add(getOrCreateRole(roleName, false));
        }
        return set;
    }

    private Role getOrCreateRole(String roleName, boolean externallyDefined) {
        Role r = getRole(new ExecutionContextImpl(), roleName);
        if (r == null) {
            r = newRole(new ExecutionContextImpl());
            r.setRoleName(roleName);
            r.setExternallyDefined(externallyDefined);
            putRole(new ExecutionContextImpl(), r);
            log.warn("Created new " + (externallyDefined ? "external" : "internal") + " role: " + roleName);
        }

        return r;
    }

    /**
     * From an external UserDetails + GrantedAuthority[], maintain the shadow internal user
     * which is used only in integration tests (applicationContext-testProviders.xml)
     *
     * @deprecated deprecated per emerald SSO work
     */
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public User maintainInternalUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Object authPrincipal = auth.getPrincipal();
        String userName = null;

        if (authPrincipal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authPrincipal;
            userName = userDetails.getUsername();
        }
        else if (authPrincipal instanceof String) {
            userName = String.valueOf(authPrincipal);
        }
        else {
            throw new JSException("Cannot synchronize user details. Unknown principal class: " +
                    authPrincipal.getClass().getName());
        }

        log.debug("Processing external user: " + userName);

        User user = getUser(new ExecutionContextImpl(), userName);

        if (user == null) {
            user = createNewExternalUser(userName);
        }

        Set roles = persistRoles(getRolesFromGrantedAuthorities(auth.getAuthorities()));
        alignInternalAndExternalUser(roles, user);

        return user;
    }

    public void makeUserLoggedIn(User user) {

        try {
            // Make our user the Authentication!

            UserDetails ourUserDetails = makeUserDetails(user);

            // Don't set the authentication if we have no roles

            if (!ourUserDetails.getAuthorities().isEmpty()) {
                UsernamePasswordAuthenticationToken ourAuthentication = new UsernamePasswordAuthenticationToken(ourUserDetails,
                        ourUserDetails.getPassword(), ourUserDetails.getAuthorities());

                if (log.isDebugEnabled()) {
                    log.debug("Setting Authentication to: " + ourAuthentication);
                }
                SecurityContextHolder.getContext().setAuthentication(ourAuthentication);
            } else {

                // There was some error - maybe no roles?
                // Remove authentication to allow anonymous access to catch things
                // later in the filter chain
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } catch (UsernameNotFoundException e) {
            log.warn("User: " + user.getUsername() + " was not found to make them logged in");
        }
    }

    /**
     * @return Returns the defaultInternalRoles.
     */
    public List getDefaultInternalRoles() {
        return defaultInternalRoles;
    }

    /**
     * @param defaultInternalRoles The defaultInternalRoles to set.
     */
    public void setDefaultInternalRoles(List defaultInternalRoles) {
        this.defaultInternalRoles = defaultInternalRoles;
    }

    public boolean userExists(ExecutionContext context, String username)
    {
        return (getUser(context, username) != null);
    }

    protected boolean isDateExpired(int nDate, Date previousExpirationDate) {
        long during = nDate*3600*24;
        return ((previousExpirationDate.getTime() / 1000) + during) <= ((new Date()).getTime() / 1000);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public boolean isPasswordExpired(ExecutionContext context, String username, int nDate) {
        Date previousExpirationDate = getUser(context, username).getPreviousPasswordChangeTime();
        // TO-DO what if previousExpirationDate is empty
        if ((previousExpirationDate == null) || ("".equals(previousExpirationDate))) {
            resetPasswordExpiration(context, username);
            return false;
        }

        return isDateExpired(nDate, previousExpirationDate);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void resetPasswordExpiration(ExecutionContext context, String username) {
        User user = getUser(context, username);
        if (user != null) {
            user.setPreviousPasswordChangeTime(new Date());
            doPutUser(context, user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String getTenantId(ExecutionContext context, String userName) {
        User user = getUser(context, userName);
        return user.getTenantId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getTenantUsers(ExecutionContext context,
                               final Set tenantIds, final String name) {
        List userList = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = createTenantUsersCriteria(session, tenantIds, name);
                return criteria.list();
            }
        });

        List userDTOs = convertUserListToDtoList(userList);

        return userDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getTenantUsers(ExecutionContext context,
                               final Set tenantIds, final String name,
                               final int firstResult, final int maxResults) {
        List userList = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = createTenantUsersCriteria(session, tenantIds, name);
                if (firstResult >= 0) {
                    criteria.setFirstResult(firstResult);
                }
                if (maxResults > 0) {
                    criteria.setMaxResults(maxResults);
                }
                return criteria.list();
            }
        });

        List userDTOs = convertUserListToDtoList(userList);

        return userDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getTenantRoles(ExecutionContext context,
                               final Set tenantIds, final String name) {
        List roleList = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = createTenantRolesCriteria(session, tenantIds, name);
                return criteria.list();
            }
        });
        return convertRoleListToDtoList(roleList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getTenantRoles(ExecutionContext context,
                               final Set tenantIds, final String name,
                               final int firstResult, final int maxResults) {
        List roleList = (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = createTenantRolesCriteria(session, tenantIds, name);
                if (firstResult >= 0) {
                    criteria.setFirstResult(firstResult);
                }
                if (maxResults > 0) {
                    criteria.setMaxResults(maxResults);
                }
                return criteria.list();
            }
        });
        return convertRoleListToDtoList(roleList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getTenantVisibleRoles(ExecutionContext context, Set tenantIds, String name, int firstResult, int maxResults) {
        DetachedCriteria criteria = createTenantVisibleRolesCriteria(tenantIds, name);

        List results = findByCriteria(criteria, firstResult, maxResults);

        List roleDTOs = convertRoleListToDtoList(results);

        return roleDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getTenantVisibleRolesCount(ExecutionContext context, Set tenantIds, String name) {
        DetachedCriteria criteria = createTenantVisibleRolesCriteria(tenantIds, name, false);

        criteria.setProjection(Projections.rowCount());

        List results = findByCriteria(criteria);

        Integer rowCount = new Integer(0) ;
        if (results != null && !results.isEmpty()) {
            rowCount = (Integer) results.get(0);
        }

        return rowCount.intValue();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getTenantUsersCount(ExecutionContext context,
                                   final Set tenantIds, final String name) {
        Integer rowCount = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = createTenantUsersCriteria(session, tenantIds, name, false);
                criteria.setProjection(Projections.rowCount());
                return criteria.uniqueResult();
            }
        });
        return rowCount.intValue();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getTenantRolesCount(ExecutionContext context,
                                   final Set tenantIds, final String name) {
        Integer rowCount = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = createTenantRolesCriteria(session, tenantIds, name, false);
                criteria.setProjection(Projections.rowCount());
                return criteria.uniqueResult();
            }
        });
        return rowCount.intValue();
    }

    /**
     * The number of all roles
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected int getTotalRolesCount(ExecutionContext context) {
        Integer rowCount = (Integer) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getPersistentRoleClass());
                criteria.setProjection(Projections.rowCount());
                return criteria.uniqueResult();
            }
        });
        return rowCount;
    }

    private Criteria createTenantUsersCriteria(Session session, Set tenantIds, String name) {
        return  createTenantUsersCriteria(session, tenantIds, name, true);
    }

    private Criteria createTenantUsersCriteria(Session session, Set tenantIds, String name, boolean order) {
        Set internalTenantIds = null;
        if (tenantIds != null) {
            internalTenantIds = new HashSet();
            internalTenantIds.addAll(tenantIds);
            if (internalTenantIds.contains(null)) {
                internalTenantIds.remove(null);
                internalTenantIds.add(TenantService.ORGANIZATIONS);
            }
        }
        Criteria criteria = session.createCriteria(getPersistentUserClass());
        criteria.createAlias("tenant", "tenant", Criteria.LEFT_JOIN);

        if (internalTenantIds == null) {
            criteria.add(Restrictions.eq("tenant.tenantId", TenantService.ORGANIZATIONS));
        } else {
            if (!internalTenantIds.isEmpty()) {
                if (internalTenantIds.size() < 1000) {
                    criteria.add(Restrictions.in("tenant.tenantId", internalTenantIds));
                } else {
                    // chunk the ID set by 1000 because Oracle cannot process more than 1000 elements in IN(?,?...?) condition
                    HashSet subset = new HashSet(1000);
                    Disjunction or = Restrictions.disjunction();
                    for (Iterator iter = internalTenantIds.iterator(); iter.hasNext(); ) {
                        subset.add(iter.next());
                        if (subset.size() == 1000) {
                            or.add(Restrictions.in("tenant.tenantId", subset));
                            subset.clear();
                        }
                    }
                    if (subset.size() > 0) {
                        or.add(Restrictions.in("tenant.tenantId", subset));
                    }
                    criteria.add(or);
                }
            }
        }

        if (name != null) {
            name = databaseCharactersEscapeResolver.getEscapedText(name.trim());
            if (name.length() > 0) {
//                Criterion userNameCriterion = Restrictions.ilike("username", "%" + name + "%");
//                Criterion fullNameCriterion = Restrictions.ilike("fullName", "%" + name + "%");

                Criterion userNameCriterion = new IlikeEscapeAwareExpression("username", name, MatchMode.ANYWHERE);
                Criterion fullNameCriterion = new IlikeEscapeAwareExpression("fullName", name, MatchMode.ANYWHERE);

                criteria.add(Restrictions.or(userNameCriterion, fullNameCriterion));
            }
        }

        if (order) {
            criteria.addOrder(Order.asc("username"));
            criteria.addOrder(Order.asc("tenant.tenantId"));
        }

        return criteria;
    }

    protected Criteria createTenantRolesCriteria(Session session,
                                                 Set tenantIds, String name){
        return createTenantRolesCriteria(session, tenantIds, name, true);
    }
    protected Criteria createTenantRolesCriteria(Session session, Set tenantIds, String name, boolean order){

        Criteria criteria = session.createCriteria(getPersistentRoleClass());
        String roleNameField = "roleName";

        addTenantCriteria(criteria, tenantIds);

        if (name != null && name.trim().length() > 0) {
            Criterion roleNameCriterion = Restrictions.ilike(roleNameField, "%"
                    + name.trim() + "%");
            criteria.add(roleNameCriterion);
        }

        if (order) {
            criteria.addOrder(Order.asc(roleNameField));
        }

        return criteria;
    }
    protected DetachedCriteria createTenantVisibleRolesCriteria(Set tenantIds, String name) {
        return createTenantVisibleRolesCriteria(tenantIds, name, true);
    }

    protected DetachedCriteria createTenantVisibleRolesCriteria(Set tenantIds, String name, boolean order) {
        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentRoleClass());
        String roleNameField = "roleName";

        addVisibleTenantCriteria(criteria, tenantIds);

        if (name != null) {
            name = databaseCharactersEscapeResolver.getEscapedText(name.trim());

            if(name.length() > 0){
//                Criterion roleNameCriterion = Restrictions.ilike(roleNameField, "%"
//                        + name.trim() + "%");
//                criteria.add(roleNameCriterion);
                criteria.add(new IlikeEscapeAwareExpression(roleNameField, name, MatchMode.ANYWHERE));
            }
        }

        if (order) {
            criteria.addOrder(Order.asc(roleNameField));
        }

        return criteria;
    }

    private List convertUserListToDtoList(List userList) {

        List userDTOs = null;
        if (userList != null) {

            userDTOs = new ArrayList(userList.size());

            Iterator it = userList.iterator();
            while(it.hasNext()) {

                RepoUser u = (RepoUser) it.next();

                User newUser = (User) u.toClient(getObjectMappingFactory());
                userDTOs.add(newUser);
            }
        }

        return userDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getAvailableRoles(ExecutionContext context, String roleName, Set userRoles, String userName,
                                  int firstResult, int maxResults){

        DetachedCriteria criteria = createAvailableRolesCriteria(roleName, userRoles, userName);

        List results = findByCriteria(criteria, firstResult, maxResults);

        List roleDTOs = convertRoleListToDtoList(results);

        return roleDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getAvailableRolesCount(ExecutionContext context, String roleName, Set userRoles, String userName){

        DetachedCriteria criteria = createAvailableRolesCriteria(roleName, userRoles, userName, false);
        criteria.setProjection(Projections.rowCount());

        List results = findByCriteria(criteria);

        Integer rowCount = new Integer(0) ;
        if (results != null && !results.isEmpty()) {
            rowCount = (Integer) results.get(0);
        }

        return rowCount.intValue();
    }

    protected DetachedCriteria createAvailableRolesCriteria(String roleName, Set userRoles, String userName){
        return createAvailableRolesCriteria(roleName, userRoles, userName, true);
    }

    protected DetachedCriteria createAvailableRolesCriteria(String roleName, Set userRoles, String userName, boolean order){

        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentRoleClass());
        String roleNameField = "roleName";
        String externallyDefinedField = "externallyDefined";

        if (userRoles != null && userRoles.size() > 0) {

            List userRoleIdList = getRoleIdList(userRoles);
            criteria.add(Restrictions.not(Restrictions.in("id", userRoleIdList)));
        }

        Criterion roleNameCriterion = Restrictions.ilike(roleNameField, "%" + roleName.trim() + "%");
        criteria.add(roleNameCriterion);

        criteria.add(Restrictions.eq(externallyDefinedField, Boolean.FALSE));

        if (order) {
            criteria.addOrder(Order.asc(roleNameField));
        }

        return criteria;
    }

    protected List getRoleIdList(Set roleNames) {
        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentRoleClass());

        if (roleNames != null && roleNames.size() > 0) {

            criteria.add(Restrictions.in("roleName", roleNames));
        } else {

            return new ArrayList();
        }

        criteria.setProjection(Projections.id());

        return findByCriteria(criteria);
    }

    private List<Role> convertRoleListToDtoList(List roleList) {
        List<Role> roleDTOs = null;

        if (roleList != null) {
            roleDTOs = new ArrayList<Role>(roleList.size());
            for (Object aRoleList : roleList) {
                RepoRole r = (RepoRole) aRoleList;
                Role newRole = (Role) r.toClient((ResourceFactory) getObjectMappingFactory());
                roleDTOs.add(newRole);
            }
        }

        return roleDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Role> getAvailableRoles(ExecutionContext context, String userName, String text, int firstResult,
                                        int maxResults) {
        DetachedCriteria criteria = createAvailableRolesCriteria(context, userName, text, true);

        List results = findByCriteria(criteria, firstResult, maxResults);

        return convertRoleListToDtoList(results);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getAvailableRolesCount(ExecutionContext context, String userName, String text) {
        DetachedCriteria criteria = createAvailableRolesCriteria(context, userName, text, false);
        criteria.setProjection(Projections.rowCount());

        List results = findByCriteria(criteria);

        int rowCount = 0 ;
        if (results != null && !results.isEmpty()) {
            rowCount = (Integer) results.get(0);
        }

        return rowCount;
    }

    protected DetachedCriteria createAvailableRolesCriteria(ExecutionContext context, String userName, String text,
                                                            boolean order) {
        final String roleNameField = "roleName";
        final String externallyDefinedField = "externallyDefined";

        RepoUser user = getRepoUser(context, userName);

        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentRoleClass());

        List<Long> assignedRolesIds = getUserRolesIds(user);
        if (assignedRolesIds!= null && assignedRolesIds.size() > 0) {
            criteria.add(Restrictions.not(Restrictions.in("id", assignedRolesIds)));
        }

        final String roleNameLikeValue = text == null ? "" : text;
        Criterion roleNameCriterion = Restrictions.ilike(roleNameField, "%" + roleNameLikeValue + "%");
        criteria.add(roleNameCriterion);

        criteria.add(Restrictions.eq(externallyDefinedField, Boolean.FALSE));

        if (order) {
            criteria.addOrder(Order.asc(roleNameField));
        }

        return criteria;
    }

    @SuppressWarnings({"unchecked"})
    protected List<Long> getUserRolesIds(RepoUser user) {
        DetachedCriteria criteria = createAssignedRolesCriteria(null, user, null, true);
        criteria.setProjection(Projections.id());

        return (List<Long>)findByCriteria(criteria);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getAssignedRoles(ExecutionContext context, String userName, String text, int firstResult,
                                 int maxResults) {
        RepoUser user = getRepoUser(context, userName);

        DetachedCriteria criteria = createAssignedRolesCriteria(context, user, text, true);

        List results = findByCriteria(criteria, firstResult, maxResults);

        return convertRoleListToDtoList(results);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getAssignedRolesCount(ExecutionContext context, String userName, String text) {
        RepoUser user = getRepoUser(context, userName);

        DetachedCriteria criteria = createAssignedRolesCriteria(context, user, text, false);
        criteria.setProjection(Projections.rowCount());

        List results = findByCriteria(criteria);

        int rowCount = 0 ;
        if (results != null && !results.isEmpty()) {
            rowCount = (Integer) results.get(0);
        }

        return rowCount;
    }

    public String getAllowedPasswordPattern() {
        return passwordPattern.pattern();
    }

    private DetachedCriteria createAssignedRolesCriteria(ExecutionContext context, RepoUser user, String text, boolean order) {
        final String roleNameField = "roleName";
        final String externallyDefinedField = "externallyDefined";

        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentRoleClass());

        DetachedCriteria usersCriteria = criteria.createCriteria("users");
        usersCriteria.add(Restrictions.idEq(user.getId()));

        final String roleNameLikeValue = text == null ? "" : text;
        Criterion roleNameCriterion = Restrictions.ilike(roleNameField, "%" + roleNameLikeValue + "%");
        criteria.add(roleNameCriterion);

        criteria.add(Restrictions.eq(externallyDefinedField, Boolean.FALSE));

        if (order) {
            criteria.addOrder(Order.asc(roleNameField));
        }

        return criteria;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void updateUser(ExecutionContext context, String userName, User aUser) {
        RepoUser existingUser = getRepoUser(context, userName);

        log.debug("updateUser: " + userName + ", " + existingUser);
        if (existingUser == null) {

            log.debug("User not found");
            throw new IllegalArgumentException("Cannot find user with name : " + userName);
        }
        updatePersistentUser(aUser, existingUser);

        addPropertiesToUserEvent(new String[] {"updateUser"}, existingUser);
        getHibernateTemplate().saveOrUpdate(existingUser);
    }

    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#updateRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void updateRole(ExecutionContext context, String roleName, Role roleDetails) throws IllegalArgumentException{
        RepoRole existingRole = getRepoRole(context, roleName);

        log.debug("updateRole: " + roleName + ", " + existingRole);
        if (existingRole == null) {

            log.debug("Role not found");
            throw new IllegalArgumentException("Cannot find role with name : " + roleName);
        }
        final String newName = roleDetails.getRoleName();
        final String tenantId = roleDetails.getTenantId();
        if(!existingRole.getRoleName().equals(newName) && getRepoRole(newName, tenantId) != null){
            throw new IllegalArgumentException(newName + " must be unique within the organization.");
        }
        existingRole.copyFromClient(roleDetails, this);

        addParametersToRoleManagementAuditEvent(new String[] {"updateRole"}, existingRole, false);
        getHibernateTemplate().saveOrUpdate(existingRole);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getUsersWithoutRole(ExecutionContext context, String roleName, String userName,
                                    int firstResult, int maxResults) {

        DetachedCriteria criteria = createUsersWithoutRoleCriteria(roleName, userName);

        List userList = findByCriteria(criteria, firstResult, maxResults);

        return convertUserListToDtoList(userList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getUsersCountWithoutRole(ExecutionContext context, String roleName, String userName) {

        DetachedCriteria criteria = createUsersWithoutRoleCriteria(roleName, userName, false);
        criteria.setProjection(Projections.rowCount());

        List results = findByCriteria(criteria);

        return getRowCountFromResult(results);
    }

    protected DetachedCriteria createUsersWithoutRoleCriteria(String roleName, String userName){
        return createUsersWithoutRoleCriteria(roleName, userName, true);
    }

    protected DetachedCriteria createUsersWithoutRoleCriteria(String roleName, String userName, boolean order){

        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentUserClass());

        DetachedCriteria usersWithRoleCriteria = createUsersWithRoleCriteria(roleName, "");
        usersWithRoleCriteria.setProjection(Projections.id());

        List usersWithRole = findByCriteria(usersWithRoleCriteria);

        String userNameField = "username";

//        addTenantCriteria(criteria, tenantIds);
        createSearchByUserNameCriteria(criteria, userName);

        if (usersWithRole != null && usersWithRole.size() > 0) {
            criteria.add(Restrictions.not(Restrictions.in("id", usersWithRole)));
        }

        if (order) {
            criteria.addOrder(Order.asc(userNameField));
        }

        return criteria;
    }

    private void addTenantCriteria(Criteria criteria, Set tenantIds){
        Set internalTenantIds = null;
        if (tenantIds != null) {
            internalTenantIds = new HashSet();
            internalTenantIds.addAll(tenantIds);
            if (internalTenantIds.contains(null)) {
                internalTenantIds.remove(null);
                internalTenantIds.add(TenantService.ORGANIZATIONS);
            }
        }
        criteria.createAlias("tenant", "tenant", Criteria.LEFT_JOIN);
        if (internalTenantIds == null) {
            criteria.add(Restrictions.eq("tenant.tenantId", TenantService.ORGANIZATIONS));
        } else {
            if (!internalTenantIds.isEmpty()) {
                Criterion idInCriterion = Restrictions.in("tenant.tenantId", internalTenantIds);
                criteria.add(idInCriterion);
            }
        }
    }

    protected List getIdByTenantIdSet(Set tenantIds) {
        DetachedCriteria idCriteria =
                DetachedCriteria.forClass(getPersistentTenantClass());
        idCriteria.add(Restrictions.in("tenantId", tenantIds));
        idCriteria.setProjection(Projections.id());
        return findByCriteria(idCriteria);
    }

    protected void addVisibleTenantCriteria(DetachedCriteria criteria, Set tenantIds) {
        Set internalTenantIds = null;
        if (tenantIds != null) {
            internalTenantIds = new HashSet();
            internalTenantIds.addAll(tenantIds);
            if (internalTenantIds.contains(null)) {
                internalTenantIds.remove(null);
                internalTenantIds.add(TenantService.ORGANIZATIONS);
            }
        }
        if (internalTenantIds == null) {
            RepoTenant tenant = tenantPersistenceResolver.getPersistentTenant(TenantService.ORGANIZATIONS, true);
            criteria.add(Restrictions.eq("tenant.id", tenant.getId()));
        } else {
            if (!internalTenantIds.isEmpty()) {
                Criterion idInCriterion = Restrictions.in("tenant.id", getIdByTenantIdSet(internalTenantIds));
                criteria.add(idInCriterion);
            }
        }
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public List getUsersWithRole(ExecutionContext context, String roleName, String userName,
                                 int firstResult, int maxResults) {

        DetachedCriteria criteria = createUsersWithRoleCriteria(roleName, userName);
        List userList = findByCriteria(criteria, firstResult, maxResults);

        return convertUserListToDtoList(userList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getUsersCountWithRole(ExecutionContext context, String roleName, String userName) {

        DetachedCriteria criteria = createUsersWithRoleCriteria(roleName, userName, false);
        criteria.setProjection(Projections.rowCount());
        List results = findByCriteria(criteria);

        return getRowCountFromResult(results);
    }

    protected DetachedCriteria createUsersWithRoleCriteria(String roleName, String userName){
        return  createUsersWithRoleCriteria(roleName, userName, true);
    }

    protected DetachedCriteria createUsersWithRoleCriteria(String roleName, String userName, boolean order){

        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentUserClass());

        String userNameField = "username";
        String roleNameField = "roleName";

//        addTenantCriteria(criteria, tenantIds);
        createSearchByUserNameCriteria(criteria, userName);

        if (roleName != null && roleName.trim().length() > 0) {

            Criterion roleNameCriterion = Restrictions.eq(roleNameField, roleName.trim());

            criteria.createCriteria("roles").add(roleNameCriterion);
        }

        if (order) {
            criteria.addOrder(Order.asc(userNameField));
        }

        return criteria;
    }

    protected void createSearchByUserNameCriteria(DetachedCriteria criteria, String userName){

        if (userName != null && userName.trim().length() > 0) {

            Criterion userNameCriterion = Restrictions.ilike("username", "%" + userName.trim() + "%");
            Criterion fullNameCriterion = Restrictions.ilike("fullName", "%" + userName.trim() + "%");

            criteria.add(Restrictions.or(userNameCriterion, fullNameCriterion));
        }
    }

    protected int getRowCountFromResult(List results) {

        Integer rowCount = new Integer(0) ;
        if (results != null && !results.isEmpty()) {
            rowCount = (Integer) results.get(0);
        }

        return rowCount.intValue();
    }

    private void addUserParamsToUpdateRoleAuditEvent(final String actionPrefix, final List users) {
        auditContext.doInAuditContext("updateRole", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                if (users != null && !users.isEmpty()) {
                    for (Object userObject: users) {
                        RepoUser user = (RepoUser)userObject;
                        auditContext.addPropertyToAuditEvent(actionPrefix + "UserId", user.getId(), auditEvent);
                    }
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void assignUsers(ExecutionContext context, String roleName, Set userNames) {

        if (userNames != null && !userNames.isEmpty()) {

            List users = getUsersByUserNames(context, userNames);
            addUserParamsToUpdateRoleAuditEvent("added", users);
            RepoRole role = getRepoRole(context, roleName);

            for (Iterator it = users.iterator(); it.hasNext(); ) {
                RepoUser user = (RepoUser) it.next();

                user.addRole(role);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void unassignUsers(ExecutionContext context, String roleName, Set userNames) {

        if (userNames != null && !userNames.isEmpty()) {

            List users = getUsersByUserNames(context, userNames);
            addUserParamsToUpdateRoleAuditEvent("removed", users);

            RepoRole role = getRepoRole(context, roleName);

            for (Iterator it = users.iterator(); it.hasNext(); ) {
                RepoUser user = (RepoUser) it.next();

                user.removeRole(role);
            }
        }
    }

    protected List getUsersByUserNames(ExecutionContext context, Set userNames) {
        DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentUserClass());

        if (userNames != null && !userNames.isEmpty()) {

            criteria.add(Restrictions.in("username", userNames));
        }

        return findByCriteria(criteria);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RepoUser getPersistentUser(String username) {
        return getRepoUser((ExecutionContext) null, username);
    }

    protected boolean isNullTenant(String tenantId) {
        return tenantId == null || tenantId.length() == 0 || TenantService.ORGANIZATIONS.equals(tenantId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RepoTenant getPersistentTenant(String tenantId, boolean required) {
        if (isNullTenant(tenantId)) {
            return getTenantPersistenceResolver().getPersistentTenant(TenantService.ORGANIZATIONS, true);
        }

        throw new IllegalArgumentException("This implementation does not support tenants");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RepoTenant getPersistentTenantByAlias(String tenantAlias, boolean required) {
        if (isNullTenant(tenantAlias)) {
            return getTenantPersistenceResolver().getPersistentTenant(TenantService.ORGANIZATIONS, true);
        }
        throw new IllegalArgumentException("This implementation does not support tenants");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<RepoTenant> getPersistentTenants(List<String> tenantIds) {
        return getTenantPersistenceResolver().getPersistentTenants(tenantIds);
    }

    public TenantPersistenceResolver getTenantPersistenceResolver() {
        return tenantPersistenceResolver;
    }

    public void setTenantPersistenceResolver(
            TenantPersistenceResolver tenantPersistenceResolver) {
        this.tenantPersistenceResolver = tenantPersistenceResolver;
    }

    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_USERS_COUNT, new DiagnosticCallback<Integer>() {
                    @Override
                    public Integer getDiagnosticAttributeValue() {
                        return getUsersCountExceptExcluded(ExecutionContextImpl.getRuntimeExecutionContext(), null, false);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_ENABLED_USERS_COUNT, new DiagnosticCallback<Integer>() {
                    @Override
                    public Integer getDiagnosticAttributeValue() {
                        return getUsersCountExceptExcluded(ExecutionContextImpl.getRuntimeExecutionContext(), null, true);
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_ROLES_COUNT, new DiagnosticCallback<Integer>() {
                    @Override
                    public Integer getDiagnosticAttributeValue() {
                        return getTotalRolesCount(ExecutionContextImpl.getRuntimeExecutionContext());
                    }
                }).build();
    }

    public void setAllowedPasswordPattern(String passwordPattern) {
        this.passwordPattern = Pattern.compile(passwordPattern);
    }
}
