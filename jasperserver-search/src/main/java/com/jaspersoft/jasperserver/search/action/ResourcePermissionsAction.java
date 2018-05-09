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
package com.jaspersoft.jasperserver.search.action;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.*;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.search.helper.PermissionJSONHelper;
import com.jaspersoft.jasperserver.search.model.permission.*;
import com.jaspersoft.jasperserver.war.action.EntitiesListManager;
import com.jaspersoft.jasperserver.war.action.EntitiesListState;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.io.Serializable;
import java.util.*;

/**
 * Resource permissions action class.
 *
 * @author Yuriy Plakosh
 * @author Stas Chubar
 */
public class ResourcePermissionsAction extends BaseSearchAction {
    /**
     * Object type enum.
     *
     * @author Yuriy Plakosh
     */
    enum ObjectType {
        USER,
        ROLE
    }

    /**
     * Resource permissions state.
     *
     * @author Yuriy Plakosh.
     */
    class ResourcePermissionsState {
        private String resourceUri;
        private boolean isFolder = false;
        private ObjectType objectType = ObjectType.USER;
        private EntitiesListState entitiesListState = new EntitiesListState();
        private Resource resource;

        public String getResourceUri() {
            return resourceUri;
        }

        public void setResourceUri(String resourceUri) {
            this.resourceUri = resourceUri;
        }

        public boolean isFolder() {
            return isFolder;
        }

        public void setFolder(boolean folder) {
            isFolder = folder;
        }

        public ObjectType getObjectType() {
            return objectType;
        }

        public void setObjectType(ObjectType objectType) {
            this.objectType = objectType;
        }

        public EntitiesListState getEntitiesListState() {
            return entitiesListState;
        }

        public Resource getResource() {
            return resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }
    }

    /**
     * State class.
     *
     * @author Yuriy Plakosh.
     */
    class State implements Serializable {
        private Map<String, ResourcePermissionsState> resourcePermissionsStateMap =
                new HashMap<String, ResourcePermissionsState>();

        public Map<String, ResourcePermissionsState> getResourcePermissionsStateMap() {
            return resourcePermissionsStateMap;
        }
    }

    // Request parameters.
    protected static final String PARAMETER_INITIALIZE = "initialize";
    protected static final String PARAMETER_RESOURCE_URI = "uri";
    protected static final String PARAMETER_IS_FOLDER = "isFolder";
    protected static final String PARAMETER_TYPE = "type";
    protected static final String PARAMETER_TEXT = "text";
    protected static final String PARAMETER_ENTITIES_WITH_PERMISSION = "entitiesWithPermission";

    // Resource URI prefix.
    protected static final String RESOURCE_URI_PREFIX = Resource.URI_PROTOCOL + ":";

    private PermissionJSONHelper permissionJSONHelper;
	private ObjectPermissionService objectPermissionService;
	private RepositoryService repository;
	private UserAuthorityService userService;
    private ConfigurationBean configuration;

    private List<String> rolesToDisablePermissionEditForEveryone; // Only root roles are supported.
    private List<String> rolesToDisablePermissionEditForNonSuperuser; // Only root roles are supported.
    private String roleSuperuser;
    private String defaultSuperuserUsername;

    public Event browse(RequestContext context) {
        State state = getState(context);

        String resourceUri = getResourceUri(context);

        ResourcePermissionsState resourcePermissionsState = state.getResourcePermissionsStateMap().get(resourceUri);
        if (resourcePermissionsState == null) {
            resourcePermissionsState = new ResourcePermissionsState();
            resourcePermissionsState.setResourceUri(resourceUri);
            resourcePermissionsState.setFolder(Boolean.parseBoolean(getParameter(context, PARAMETER_IS_FOLDER)));

            resourcePermissionsState.setResource(getResource(context, resourcePermissionsState.getResourceUri(),
                    resourcePermissionsState.isFolder()));

            state.getResourcePermissionsStateMap().put(resourceUri, resourcePermissionsState);
        } else {
            resourcePermissionsState.getEntitiesListState().updateResultState(0, 0);

            String initialize = getParameter(context, PARAMETER_INITIALIZE);
            if (Boolean.parseBoolean(initialize)) {
                resourcePermissionsState.getEntitiesListState().updateText(null);
            }
        }
        resourcePermissionsState.setObjectType(ObjectType.valueOf(getParameter(context, PARAMETER_TYPE)));

        return success();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public Event search(RequestContext context) {
        String resourceUri = getResourceUri(context);
        ResourcePermissionsState resourcePermissionsState = getState(context).getResourcePermissionsStateMap().
                get(resourceUri);

        if (resourcePermissionsState == null) {
            return error(new IllegalStateException("Search action performed before browse action (initialization) " +
                    "[resourceUri=\"" + resourceUri + "\"]."));
        }

        resourcePermissionsState.getEntitiesListState().updateText(getParameter(context, PARAMETER_TEXT));

        return success();
    }

    @SuppressWarnings({"unchecked", "ThrowableInstanceNeverThrown"})
    public Event next(RequestContext context) {
        String resourceUri = getResourceUri(context);
        ResourcePermissionsState resourcePermissionsState = getState(context).getResourcePermissionsStateMap().
                get(resourceUri);

        if (resourcePermissionsState == null) {
            return error(new IllegalStateException("Next action performed before browse action (initialization) " +
                    "[resourceUri=\"" + resourceUri + "\"]."));
        }

        JSONObject responseModel;
        try {
            final ExecutionContext executionContext = getExecutionContext(context);
            final Set<String> tenantIdSet = getEffectiveTenantIds(resourcePermissionsState, executionContext);
            final EntitiesListState entitiesListState = resourcePermissionsState.getEntitiesListState();

            JSONObject json;
            switch (resourcePermissionsState.getObjectType()) {
                case USER:
                    List<User> users = getEntitiesAndUpdateState(entitiesListState, configuration.getRoleItemsPerPage(),
                            new EntitiesListManager() {
                                public int getResultsCount() {
                                    return userService.getTenantUsersCount(executionContext, tenantIdSet,
                                            entitiesListState.getText());
                                }

                                public List getResults(int resultIndex, int maxResults) {
                                    return userService.getTenantUsers(executionContext, tenantIdSet,
                                            entitiesListState.getText(), resultIndex, maxResults);
                                }
                            });

                    List<UserWithPermission> userWithPermissionList = getUserWithPermissionList(context, users,
                            resourcePermissionsState);

                    json = permissionJSONHelper.createUserWithPermissionsJson(userWithPermissionList,
                            resourcePermissionsState.getObjectType().name());

                    break;
                case ROLE:
                    List<Role> roles = getEntitiesAndUpdateState(entitiesListState, configuration.getRoleItemsPerPage(),
                            new EntitiesListManager() {
                                public int getResultsCount() {
                                    return userService.getTenantVisibleRolesCount(executionContext, tenantIdSet,
                                            entitiesListState.getText());
                                }

                                public List getResults(int resultIndex, int maxResults) {
                                    return userService.getTenantVisibleRoles(executionContext, tenantIdSet,
                                            entitiesListState.getText(), resultIndex, maxResults);
                                }
                            });

                    List<RoleWithPermission> roleWithPermissionList = getRoleWithPermissionList(context, roles,
                            resourcePermissionsState);

                    json = permissionJSONHelper.createRoleWithPermissionsJson(roleWithPermissionList, 
                            resourcePermissionsState.getObjectType().name());

                    break;

                default:
                    return error(new IllegalArgumentException("Object type is not supported [type=\"" +
                            resourcePermissionsState.getObjectType() + "\"]"));
            }

            responseModel = getConverter(context).createJSONResponse(json);
        } catch (Exception e) {
            try {
                responseModel = getConverter(context).createErrorJSONResponse(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel.toString());

        return success();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public Event updatePermissions(RequestContext context) {
        String resourceUri = getResourceUri(context);

        ResourcePermissionsState resourcePermissionsState = getState(context).getResourcePermissionsStateMap().
                get(resourceUri);
        if (resourcePermissionsState == null) {
            return error(new IllegalStateException("UpdatePermissions action performed before browse action " +
                    "(initialization) [resourceUri=\"" + resourceUri + "\"]."));
        }

        String entitiesWithPermissionJson = getParameter(context, PARAMETER_ENTITIES_WITH_PERMISSION);
        if (entitiesWithPermissionJson == null) {
            return error(new IllegalArgumentException("Request parameter \"" + PARAMETER_ENTITIES_WITH_PERMISSION +
                    "\" is missed"));
        }

        JSONObject responseModel;
        try {
            JSONObject json;
            switch (resourcePermissionsState.getObjectType()) {
                case USER:
                    Set<UserWithPermission> userWithPermissionSet =
                            permissionJSONHelper.convertJsonArrayToUserWithPermissionSet(entitiesWithPermissionJson);

                    updateUserPermissions(context, resourcePermissionsState, userWithPermissionSet);

                    List<UserWithPermission> userWithPermissionList = getUserWithPermissionList(context,
                            extractUsers(userWithPermissionSet), resourcePermissionsState);

                    json = permissionJSONHelper.createUserWithPermissionsJson(userWithPermissionList,
                            resourcePermissionsState.getObjectType().name());
                    break;

                case ROLE:
                    Set<RoleWithPermission> roleWithPermissionSet =
                            permissionJSONHelper.convertJsonArrayToRoleWithPermissionSet(entitiesWithPermissionJson);
                    updateRolePermissions(context, resourcePermissionsState, roleWithPermissionSet);

                    List<RoleWithPermission> roleWithPermissionList = getRoleWithPermissionList(context,
                            extractRoles(roleWithPermissionSet), resourcePermissionsState);

                    json = permissionJSONHelper.createRoleWithPermissionsJson(roleWithPermissionList,
                            resourcePermissionsState.getObjectType().name());
                    break;

                default:
                    return error(new IllegalArgumentException("Object type is not supported [type=\"" +
                                resourcePermissionsState.getObjectType() + "\"]"));
            }

            responseModel = getConverter(context).createJSONResponse(json);
        } catch (Exception e) {
            try {
                responseModel = getConverter(context).createErrorJSONResponse(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel.toString());

        return success();
    }

    private void updateUserPermissions(RequestContext context, ResourcePermissionsState resourcePermissionsState,
            Set<UserWithPermission> userWithPermissionSet) {
        for (UserWithPermission userWithPermission : userWithPermissionSet) {
            updateRecipientPermission(context, resourcePermissionsState, userWithPermission.getUser(),
                    userWithPermission.getPermissionToDisplay());
        }
    }

    private void updateRolePermissions(RequestContext context, ResourcePermissionsState resourcePermissionsState,
            Set<RoleWithPermission> roleWithPermissionSet) {
        for (RoleWithPermission roleWithPermission : roleWithPermissionSet) {
            updateRecipientPermission(context, resourcePermissionsState, roleWithPermission.getRole(),
                    roleWithPermission.getPermissionToDisplay());
        }
    }

    private void updateRecipientPermission(RequestContext context, ResourcePermissionsState resourcePermissionsState,
            Object recipient, PermissionToDisplay permissionToDisplay) {
        Resource resource = resourcePermissionsState.getResource();
        ExecutionContext executionContext = getExecutionContext(context);

        if(permissionToDisplay.getNewPermission() != null) {
            if (permissionToDisplay.isInherited()) {
                if (permissionToDisplay.getNewPermission() != permissionToDisplay.getInheritedPermission()) {
                    performObjectPermissionSave(executionContext, resource, recipient,
                            permissionToDisplay.getNewPermission().getMask());
                }
            } else {
                if (permissionToDisplay.getNewPermission() != permissionToDisplay.getPermission()) {
                    if (permissionToDisplay.getNewPermission() == permissionToDisplay.getInheritedPermission()) {
                        performObjectPermissionDelete(executionContext, resource, recipient);
                    } else {
                        performObjectPermissionSave(executionContext, resource, recipient,
                            permissionToDisplay.getNewPermission().getMask());
                    }
                }
            }
        }
    }

    private Set<String> getEffectiveTenantIds(ResourcePermissionsState resourcePermissionsState,
            ExecutionContext executionContext) {
        Tenant resourceTenant = tenantService.getTenantBasedOnRepositoryUri(executionContext,
                resourcePermissionsState.getResourceUri());

        String resourceTenantId = resourceTenant == null ? TenantService.ORGANIZATIONS : resourceTenant.getId();

        if (resourcePermissionsState.getResourceUri().startsWith(configuration.getPublicFolderUri())) {
            String currentTenantId = getCurrentTenantId();

            return getSubTenantIdsSet(currentTenantId == null ? TenantService.ORGANIZATIONS : currentTenantId);
        } else {
            return getTenantIdsBetween(executionContext, getCurrentTenantId(), resourceTenantId);
        }
    }

    protected State getState(RequestContext context) {
        Object state = super.getState(context);
        if (state == null) {
            initState(context);
            state = super.getState(context);
        }

        return (State)state;
    }

    @Override
    protected void initState(RequestContext context) {
        State state = new State();
        putState(context, state);
    }

    private String getResourceUri(RequestContext context) {
        String resourceUri = getParameter(context, PARAMETER_RESOURCE_URI);

        if (resourceUri.startsWith(RESOURCE_URI_PREFIX)) {
            resourceUri = resourceUri.substring(RESOURCE_URI_PREFIX.length());
        }

        return resourceUri;
    }

    private List<UserWithPermission> getUserWithPermissionList(RequestContext context, List<User> users,
            ResourcePermissionsState resourcePermissionsState) {
        List<UserWithPermission> userWithPermissionList = new ArrayList<UserWithPermission>(users.size());
        for (User user : users) {
            UserWithPermission userWithPermission = new UserWithPermission();
            userWithPermission.setUser(user);
            userWithPermission.setPermissionToDisplay(
                    getPermission(getExecutionContext(context), resourcePermissionsState.getResource(), user));
            disablePermissionIfRequired(userWithPermission);

            userWithPermissionList.add(userWithPermission);
        }

        return userWithPermissionList;
    }

    private List<RoleWithPermission> getRoleWithPermissionList(RequestContext context, List<Role> roles,
            ResourcePermissionsState resourcePermissionsState) {
        List<RoleWithPermission> roleWithPermissionList = new ArrayList<RoleWithPermission>(roles.size());
        Set<Role> processedRoles = new HashSet<Role>();
        for (Role role : roles) {
        	if(processedRoles.contains(role)){
        		continue;
        	}
            RoleWithPermission roleWithPermission = new RoleWithPermission();
            roleWithPermission.setRole(role);
            roleWithPermission.setPermissionToDisplay(
                    getPermission(getExecutionContext(context), resourcePermissionsState.getResource(), role));
            disablePermissionIfRequired(roleWithPermission);

            roleWithPermissionList.add(roleWithPermission);
        	processedRoles.add(role);
        }

        return roleWithPermissionList;
    }

    private void disablePermissionIfRequired(UserWithPermission userWithPermission) {
        String currentUsername = getCurrentUsername();
        String currentTenantId = getCurrentTenantId();

        boolean isPermissionForDefaultSuperuser = currentTenantId == null
                && defaultSuperuserUsername.equals(currentUsername)
                && userWithPermission.getUser().getTenantId() == null
                && userWithPermission.getUser().getUsername().equals(defaultSuperuserUsername);
        
        boolean isPermissionForCurrentUser = userWithPermission.getUser().getUsername().equals(currentUsername)
                && ((currentTenantId == null && userWithPermission.getUser().getTenantId() == null)
                || (currentTenantId != null && userWithPermission.getUser().getTenantId().equals(currentTenantId)));

        if (isPermissionForDefaultSuperuser || isPermissionForCurrentUser) {
            userWithPermission.getPermissionToDisplay().setDisabled(true);
        }
    }

    private void disablePermissionIfRequired(RoleWithPermission roleWithPermission) {
        if (rolesToDisablePermissionEditForEveryone.contains(roleWithPermission.getRole().getRoleName())) {
            roleWithPermission.getPermissionToDisplay().setDisabled(true);
        }

        List<Role> roles = getCurrentUserRoles();
        boolean hasSuperuserRole = false;
        for (Role role : roles) {
            if (role.getRoleName().equals(roleSuperuser)) {
                hasSuperuserRole = true;
            }
        }

        if (!hasSuperuserRole) {
            if (rolesToDisablePermissionEditForNonSuperuser.contains(roleWithPermission.getRole().getRoleName())) {
                roleWithPermission.getPermissionToDisplay().setDisabled(true);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private PermissionToDisplay getPermission(ExecutionContext executionContext, Resource resource, Object recipient) {
        List<ObjectPermission> permissions =
                objectPermissionService.getObjectPermissionsForObjectAndRecipient(executionContext, resource,
                        recipient);

        PermissionToDisplay permissionToDisplay = new PermissionToDisplay();
        if (permissions != null && permissions.size() > 0) {
            ObjectPermission objectPermission = permissions.get(0);

            if (objectPermission != null && objectPermission.getPermissionRecipient() != null) {
                permissionToDisplay.setPermission(Permission.getByMask(objectPermission.getPermissionMask()));
                permissionToDisplay.setInheritedPermission(Permission.getByMask(
                        objectPermissionService.getInheritedObjectPermissionMask(executionContext, resource,
                                recipient)));
            }
        } else {
            permissionToDisplay.setInherited(true);
            permissionToDisplay.setInheritedPermission(Permission.getByMask(
                    objectPermissionService.getInheritedObjectPermissionMask(executionContext, resource, recipient)));
        }

        return permissionToDisplay;
    }

    private Resource getResource(RequestContext context, String resourceUri, boolean isFolder) {
        ExecutionContext executionContext = getExecutionContext(context);
        if (isFolder) {
            return repository.getFolder(executionContext, resourceUri);
        } else {
            return repository.getResource(executionContext, resourceUri);
        }
    }

    private Set<String> getTenantIdsBetween(ExecutionContext executionContext, String topTenantId,
            String bottomTenantId) {
        topTenantId = topTenantId == null ? TenantService.ORGANIZATIONS : topTenantId;

        if(topTenantId.equals(bottomTenantId)) {
            return Collections.singleton(topTenantId);
        }

        Tenant topTenant = tenantService.getTenant(executionContext, topTenantId);
        Tenant bottomTenant = tenantService.getTenant(executionContext, bottomTenantId);

        if (bottomTenant == null || topTenant == null) {
            return Collections.emptySet();
        }

        String topTenantUri = topTenant.getTenantUri();
        String bottomTenantUri = bottomTenant.getTenantUri();

        if (bottomTenantUri.startsWith(topTenantUri)) {
            String subUri = bottomTenantUri.substring(topTenantUri.length());

            String[] betweenTenantIds = subUri.split("/");

            Set<String> tenantIdsBetween = new HashSet<String>(Arrays.asList(betweenTenantIds));

            if (topTenantId.equals(TenantService.ORGANIZATIONS)) {
                tenantIdsBetween.add(TenantService.ORGANIZATIONS);
            }

            String topTenantParentId = topTenant.getParentId();
            if (topTenantUri == "/" && topTenantParentId != null && topTenantParentId.equals(TenantService.ORGANIZATIONS)) {
                tenantIdsBetween.add(topTenantId);
            }

            return tenantIdsBetween;
        } else {
            return Collections.emptySet();
        }
    }

	private void performObjectPermissionSave(ExecutionContext context, Resource resource, Object recipient,
            int permissionMask) {
		if (recipient == null) {
			throw new IllegalArgumentException("Recipient is null.");
		}

		if (resource == null) {
            throw new IllegalArgumentException("Resource is null.");
		}

        List lstObjPerms = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, resource,
                recipient);

        ObjectPermission objectPermission;
        String auditEventType;
        if (lstObjPerms != null && lstObjPerms.size() > 0) {
			objectPermission = (ObjectPermission) lstObjPerms.get(0);
            auditEventType = "updatePermission";
        } else {
            objectPermission = objectPermissionService.newObjectPermission(context);
            auditEventType = "createPermission";
        }
        createAuditEvent(auditEventType);

		// Because of default permissions, we could get something that has no recipient.
		objectPermission.setURI(resource.getProtocol() + ":" + resource.getURIString());
		objectPermission.setPermissionMask(permissionMask);
		objectPermission.setPermissionRecipient(recipient);

		objectPermissionService.putObjectPermission(context, objectPermission);
        closeAuditEvent(auditEventType);
	}

	private void performObjectPermissionDelete(ExecutionContext context, Resource resource, Object recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient is null.");
        }

        if (resource == null) {
            throw new IllegalArgumentException("Resource is null.");
        }

		List lstObjPerms = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, resource,
                recipient);
		if (lstObjPerms != null && lstObjPerms.size() > 0) {
            ObjectPermission objectPermission = (ObjectPermission) lstObjPerms.get(0);

            // Because of default permissions, we could get something that has no recipient.
            if (objectPermission == null || objectPermission.getPermissionRecipient() == null) {
                log.warn("No permission for target and recipient to delete.");
            } else {
                createAuditEvent("deletePermission");
                objectPermissionService.deleteObjectPermission(context, objectPermission);
                closeAuditEvent("deletePermission");
            }
        }
	}

    private List<User> extractUsers(Set<UserWithPermission> userWithPermissionSet) {
        List<User> users = new ArrayList<User>(userWithPermissionSet.size());

        for (UserWithPermission userWithPermission : userWithPermissionSet) {
            users.add(userWithPermission.getUser());
        }

        return users;
    }

    private List<Role> extractRoles(Set<RoleWithPermission> roleWithPermissionSet) {
        List<Role> roles = new ArrayList<Role>(roleWithPermissionSet.size());

        for (RoleWithPermission roleWithPermission : roleWithPermissionSet) {
            roles.add(roleWithPermission.getRole());
        }

        return roles;
    }

    public void setPermissionJSONHelper(PermissionJSONHelper permissionJSONHelper) {
        this.permissionJSONHelper = permissionJSONHelper;
    }

	public void setObjectPermissionService(ObjectPermissionService objectPermissionService) {
		this.objectPermissionService = objectPermissionService;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public void setUserService(UserAuthorityService userService) {
		this.userService = userService;
	}

	protected ExecutionContext getExecutionContext(RequestContext context) {
		return JasperServerUtil.getExecutionContext(context);
	}

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public void setRolesToDisablePermissionEditForEveryone(List<String> rolesToDisablePermissionEditForEveryone) {
        this.rolesToDisablePermissionEditForEveryone = rolesToDisablePermissionEditForEveryone;
    }

    public void setRolesToDisablePermissionEditForNonSuperuser(List<String> rolesToDisablePermissionEditForNonSuperuser) {
        this.rolesToDisablePermissionEditForNonSuperuser = rolesToDisablePermissionEditForNonSuperuser;
    }

    public void setRoleSuperuser(String roleSuperuser) {
        this.roleSuperuser = roleSuperuser;
    }

    public void setDefaultSuperuserUsername(String defaultSuperuserUsername) {
        this.defaultSuperuserUsername = defaultSuperuserUsername;
    }
}
