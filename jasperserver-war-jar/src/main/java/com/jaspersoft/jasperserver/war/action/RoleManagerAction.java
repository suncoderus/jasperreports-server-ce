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

import com.jaspersoft.jasperserver.api.metadata.common.service.PaginatedOperationResult;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.RoleManagerService;
import com.jaspersoft.jasperserver.war.common.UsersOperationResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Role manager action class.
 *
 * @author schubar
 * @author Yuriy Plakosh
 */
public class RoleManagerAction extends BaseManagerAction {
    protected static final String RM_DEFAULT_ROLE = "defaultRole";
    protected static final String UM_ROLE_DETAILS = "roleDetails";
    protected static final String UM_USER_NAME = "userName";
    protected static final String UM_ROLE_NAME = "roleName";
    protected static final String UM_ROLE_NAMES = "roleNames";
    protected static final String UM_FIRST_RESULT = "firstResult";

    protected static final String RM_ASSIGNED_USER_LIST = "assignedUserList";
    protected static final String RM_AVAILABLE_USER_LIST = "availableUserList";
    protected static final String RM_ASSIGN_USERS = "assignUsers";
    protected static final String RM_UNASSIGN_USERS = "unassignUsers";

    protected static final String RM_ROLE_USERS = "rmRoleUsers";
    protected static final String RM_ROLE_USERS_RESTORE = "rmRoleUsersRestore";

    // JSON attributes.
    protected static final String JSON_ATTRIBUTE_USER_NAME_SEPARATOR = "userNameSeparator";
    protected static final String JSON_ATTRIBUTE_ROLE_NAME_NOT_SUPPORTED_SYMBOLS = "roleNameNotSupportedSymbols";
    protected static final String JSON_ATTRIBUTE_USER_DEFAULT_ROLE = "userDefaultRole";
    protected static final String JSON_ATTRIBUTE_USER_PASSWORD_MASK = "passwordMask";

    private RoleManagerService roleManagerService;

    public void setRoleManagerService(RoleManagerService roleManagerService) {
        this.roleManagerService = roleManagerService;
    }

    public Event initEvent(RequestContext context) throws Exception {
        initState(context);

        JSONObject jsonConfiguration = new JSONObject();
        jsonConfiguration.put(JSON_ATTRIBUTE_ROLE_NAME_NOT_SUPPORTED_SYMBOLS, configuration.getRoleNameNotSupportedSymbols());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_NAME_SEPARATOR, configuration.getUserNameSeparator());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_DEFAULT_ROLE, configuration.getDefaultRole());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_PASSWORD_MASK, configuration.getPasswordMask());
        jsonConfiguration.put(JSON_ATTRIBUTE_SUPERUSER_ROLE, ROLE_SUPERUSER);
        jsonConfiguration.put(JSON_ATTRIBUTE_ADMIN_ROLE, ROLE_ADMINISTRATOR);

        context.getFlowScope().put(FLOW_ATTRIBUTE_CONFIGURATION, jsonConfiguration.toString());

        context.getFlowScope().put(RM_DEFAULT_ROLE, getDefaultEntity(context));
        context.getFlowScope().put(FLOW_ATTRIBUTE_DEFAULT_ENTITY, getDefaultEntity(context));
        context.getFlowScope().put(FLOW_ATTRIBUTE_CURRENT_USER, getCurrentUser());
        context.getFlowScope().put(FLOW_ATTRIBUTE_CURRENT_USER_ROLES,
                jsonHelper.convertRoleListToJson(getCurrentUserRoles(), null).toString());

        return success();
    }

    public Event next(RequestContext context) {
        final State state = getState(context);
        final EntitiesListState entitiesState = state.getEntitiesState();

        String responseModel;
        try {
            final Set tenantIdSet = getSubTenantIdsSet(state.getTenantId());

            List roles = getEntitiesAndUpdateState(entitiesState, configuration.getRoleItemsPerPage(),
                    new EntitiesListManager() {
                        public int getResultsCount() {
                            return userService.getTenantVisibleRolesCount(null, tenantIdSet, entitiesState.getText());
                        }

                        public List getResults(int resultIndex, int maxResults) {
                            return userService.getTenantVisibleRoles(null, tenantIdSet, entitiesState.getText(),
                                resultIndex, maxResults);
                        }
                    });

            JSONObject rolesJson = jsonHelper.createRolesResponseJson(roles);

            responseModel = jsonHelper.createDataResponseModel(rolesJson);
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event getDetails(RequestContext context) {
        State state = getState(context);
        String roleName = state.getSelectedEntity();

        String responseModel;
        try {
            Role role = this.userService.getRole(null, roleName);

            if (role != null) {
                JSONObject usersJson = jsonHelper.convertRoleToJson(role, null);
                responseModel = jsonHelper.createDataResponseModel(usersJson);
            } else {
                throw new IllegalArgumentException("Cannot find role with name : " + roleName);
            }
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    @SuppressWarnings({"unchecked"})
    public Event nextAssigned(RequestContext context) {
        final State state = getState(context);
        final EntitiesListState entitiesState = state.getAssignedEntitiesState();

        String responseModel;
        try {
            List users = getEntitiesAndUpdateState(entitiesState, configuration.getEntitiesPerPage(),
                    new EntitiesListManager() {
                        public int getResultsCount() {
                            return userService.getUsersCountWithRole(null, state.getSelectedEntity(),
                                entitiesState.getText());
                        }

                        public List getResults(int resultIndex, int maxResults) {
                            return userService.getUsersWithRole(null, state.getSelectedEntity(),
                                    entitiesState.getText(), resultIndex, maxResults);
                        }
                    });

            JSONObject usersJson = jsonHelper.createUsersResponseJson(users);
            responseModel = jsonHelper.createDataResponseModel(usersJson);
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    @SuppressWarnings({"unchecked"})
    public Event nextAvailable(RequestContext context) {
        final State state = getState(context);
        final EntitiesListState entitiesState = state.getAvailableEntitiesState();

        String responseModel;
        try {
            List users = getEntitiesAndUpdateState(entitiesState, configuration.getEntitiesPerPage(),
                    new EntitiesListManager() {
                        public int getResultsCount() {
                            return userService.getUsersCountWithoutRole(null, state.getSelectedEntity(),
                                entitiesState.getText());
                        }

                        public List getResults(int resultIndex, int maxResults) {
                            return userService.getUsersWithoutRole(null, state.getSelectedEntity(),
                                    entitiesState.getText(), resultIndex, maxResults);
                        }
                    });

            JSONObject usersJson = jsonHelper.createUsersResponseJson(users);
            responseModel = jsonHelper.createDataResponseModel(usersJson);
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event exist(RequestContext context) {
        String roleName = getParameter(context, PARAMETER_ENTITY_NAME);

        String responseModel;
        try {
            Role role = this.userService.getRole(null, roleName);

            JSONObject existJson = jsonHelper.createExistJson((role != null));
            responseModel = jsonHelper.createDataResponseModel(existJson);
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event delete(RequestContext context) {
        String roleName = getParameter(context, PARAMETER_ENTITY);

        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }

        try {
            if (roleName.length() > 0) {
                createAuditEvent("deleteRole");
                userService.deleteRole(null, roleName);
                closeAuditEvent("deleteRole");
                setUsersOperationResult(context, new UsersOperationResult(roleName));                
            } else {
                throw new IllegalArgumentException("Role name is empty.");
            }
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event deleteAll(RequestContext context) {
        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }

        try {
            List<String> roleNames = getEntities(context);

            //Bug 40143
            if (roleNames.size() == 1) {
                createAuditEvent("deleteRole");
                roleManagerService.deleteAll(null, roleNames);
                closeAuditEvent("deleteRole");
            }
            else if (roleNames.size() > 0) {
                createAuditEvent("deleteRoles");
                roleManagerService.deleteAll(null, roleNames);
                closeAuditEvent("deleteRoles");

                for (String roleName : roleNames) {
                    setUsersOperationResult(context, new UsersOperationResult(roleName));
                }
            } else {
                throw new IllegalArgumentException("Role name is empty.");
            }
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event create(RequestContext context) {
        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }

        try {
            String roleJson = getParameter(context, PARAMETER_ENTITY);

            if (roleJson != null && roleJson.length() > 0) {
                Role role = jsonHelper.convertJsonToRole(roleJson);

                if (role.getTenantId() != null) {
                    Tenant tenant = tenantService.getTenant(null, role.getTenantId());

                    if (tenant == null) {
                        throw new IllegalArgumentException("Cannot find organization with id : " + role.getTenantId());
                    }
                }

                createAuditEvent("createRole");
                userService.putRole(null, role);
                closeAuditEvent("createRole");

                setUsersOperationResult(context, new UsersOperationResult(role.getRoleName()));                                
            } else {
                throw new IllegalAccessException("Error when creating role");
            }
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event initEdit(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);

        setUsersOperationResult(context, new UsersOperationResult(roleName));

        log.info("Init edit for role : " + roleName);
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, jsonHelper.createSuccessResponseModel());

        return success();
    }

    public Event updateRole(RequestContext context) throws Exception {
        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }

        try {
            String roleJson = getParameter(context, PARAMETER_ENTITY);
            String roleName = getParameter(context, PARAMETER_ENTITY_NAME);
            String assignedJson = getParameter(context, PARAMETER_ASSIGNED_ENTITIES);
            String unassignedJson = getParameter(context, PARAMETER_UNASSIGNED_ENTITIES);

            if (roleJson != null && roleJson.length() > 0) {
                Role role = jsonHelper.convertJsonToRole(roleJson);
                Set assigned = jsonHelper.convertJsonArrayToUsers(assignedJson);
                Set unassigned = jsonHelper.convertJsonArrayToUsers(unassignedJson);

                if (role != null && role.getRoleName().trim().length() > 0) {

                    createAuditEvent("updateRole");
                    roleManagerService.updateRole(null, roleName, role, assigned, unassigned);
                    closeAuditEvent("updateRole");

//                    this.userService.assignUsers(null, roleName, assignedUserNames);
//                    this.userService.unassignUsers(null, roleName, unassignedUserNames);
//
//                    this.userService.updateRole(null, roleName, role);
                } else {
                    throw new IllegalArgumentException("Error when updating role details");
                }
            }
        } catch (Exception e) {
            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }
    
    public Event cancelEdit(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);

        setUsersOperationResult(context, new UsersOperationResult(roleName));

        log.info("Cancel edit role : " + roleName);
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, jsonHelper.createSuccessResponseModel());

        return success();
    }

    public Event initChangeUsers(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);

        UsersOperationResult usersOperationResult = getUsersOperationResult(context, roleName);
        getSession(context).put(RM_ROLE_USERS_RESTORE, usersOperationResult.clone());

        log.info("Init change users for role : " + roleName);
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, jsonHelper.createSuccessResponseModel());

        return success();
    }

    public Event revertUsersChanges(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);

        UsersOperationResult usersOperationResultRestore =
                (UsersOperationResult) getSession(context).get(RM_ROLE_USERS_RESTORE);
        setUsersOperationResult(context, usersOperationResultRestore);

        log.info("Revert user changes for role : " + roleName);
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, jsonHelper.createSuccessResponseModel());

        return success();
    }

    public Event loadAvailableUsers(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);
        final String userName = getUserName(context);
        int firstResult = getFirstResult(context);

        String responseModel;

        try {
//            final Set tenantIdSet = getAllSubTenantsIdSet(tenantId);

            JSONObject availableUsers =
                    getUnassignedUsers(context, roleName, userName, firstResult);

            responseModel = jsonHelper.createDataResponseModel(availableUsers);
        } catch (Exception e) {

            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event loadAssignedUsers(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);
        final String userName = getUserName(context);
        int firstResult = getFirstResult(context);

        String responseModel;

        try {
//            final Set tenantIdSet = getAllSubTenantsIdSet(tenantId);

            JSONObject assignedUsers =
                    getAssignedUsers(context, roleName, userName, firstResult);

            responseModel = jsonHelper.createDataResponseModel(assignedUsers);
        } catch (Exception e) {

            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event updateRoleUsers(RequestContext context) throws Exception {
        final String roleName = getRoleName(context, false);

        String responseModel;

        try {
            final Set tenantIdSet = null; //getAllSubTenantsIdSet(tenantId);

            assignUsers(context, roleName, getAssignUsers(context));
            unassignUsers(context, roleName, getUnassignUsers(context));

            final String availableListUserName = getUserName(getAvailableUserList(context));
            final int availableListFirstResult = getFirstResult(getAvailableUserList(context));

            final String assignedListUserName = getUserName(getAssignedUserList(context));
            final int assignedListFirstResult = getFirstResult(getAssignedUserList(context));

            JSONObject userChanges = new JSONObject();

            userChanges.put(RM_AVAILABLE_USER_LIST,
                   getUnassignedUsers(context, roleName, availableListUserName, availableListFirstResult));

            userChanges.put(RM_ASSIGNED_USER_LIST,
                    getAssignedUsers(context, roleName, assignedListUserName, assignedListFirstResult));

            responseModel = jsonHelper.createDataResponseModel(userChanges);
        } catch (Exception e) {

            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    private JSONObject getUnassignedUsers(RequestContext context, final String roleName, final String userName,
                                         final int firstResult) throws Exception {

        int maxResults = 10;

//        final String userName = getUserName(getAssignedUserList(context));
//        final int firstResult = getFirstResult(getAssignedUserList(context));
        final Set assignedUserNames = getUsersOperationResult(context, roleName).getAssignedUsers();
        final Set unassignedUserNames = getUsersOperationResult(context, roleName).getUnassignedUsers();

        PaginatedOperationResult result = roleManagerService.getUsersWithoutRole(null, roleName, userName,
                        assignedUserNames, unassignedUserNames,
                        firstResult, maxResults);

        JSONObject availableUserListJson;
        if (result.getResult() != null && !result.getResult().isEmpty()) {

            availableUserListJson = jsonHelper.createUsersResponseJson(result.getResult());
        } else {

            availableUserListJson = jsonHelper.createEmptyUserListResponseJson();
        }

        return availableUserListJson;
    }

    private JSONObject getAssignedUsers(RequestContext context, final String roleName, final String userName,
                                        final int firstResult) throws Exception {

        int maxResults = 10;

//        final String userName = getUserName(getAssignedUserList(context));
//        final int firstResult = getFirstResult(getAssignedUserList(context));
        final Set assignedUserNames = getUsersOperationResult(context, roleName).getAssignedUsers();
        final Set unassignedUserNames = getUsersOperationResult(context, roleName).getUnassignedUsers();

        PaginatedOperationResult result = roleManagerService.getUsersWithRole(null, roleName, userName,
                        assignedUserNames, unassignedUserNames,
                        firstResult, maxResults);

        JSONObject assignedUserListJson;
        if (result.getResult() != null && !result.getResult().isEmpty()) {

            assignedUserListJson = jsonHelper.createUsersResponseJson(result.getResult());
        } else {

            assignedUserListJson = jsonHelper.createEmptyUserListResponseJson();
        }

        return assignedUserListJson;
    }

    private int getFirstResult(JSONObject json) throws Exception{
        int firstResult = 0;

        if (json != null && json.has(UM_FIRST_RESULT)) {

            firstResult = json.getInt(UM_FIRST_RESULT);
        }

        return firstResult;
    }

    private String getUserName(JSONObject json) throws Exception{
        String userName = "";

        if (json != null && json.has(UM_USER_NAME)) {

            userName = json.getString(UM_USER_NAME);
        }

        return userName;
    }

    private void assignUsers(RequestContext context, String roleName, Set userNames) throws Exception{

        UsersOperationResult result = getUsersOperationResult(context, roleName);

        Set assigned = result.getAssignedUsers();

        assigned.addAll(userNames);

        Set unassigned = result.getUnassignedUsers();

        unassigned.removeAll(userNames);
    }

    private void unassignUsers(RequestContext context, String roleName, Set userNames) throws Exception{

        UsersOperationResult result = getUsersOperationResult(context, roleName);

        Set unassigned = result.getUnassignedUsers();

        unassigned.addAll(userNames);

        Set assigned = result.getAssignedUsers();

        assigned.removeAll(userNames);

    }

    private String getRoleJson(RequestContext context) {
        return context.getRequestParameters().get(UM_ROLE_DETAILS);
    }

    private String getUserName(RequestContext context) {
        String name = getDecodedRequestParameter(context, UM_USER_NAME);

        return (name != null) ? name : "";
    }

    private String getRoleName(RequestContext context, boolean decode) {
        String name;
        if (decode) {
            name = getDecodedRequestParameter(context, UM_ROLE_NAME);
        } else {
            name = context.getRequestParameters().get(UM_ROLE_NAME);
        }

        return (name != null) ? name : "";
    }

    private List<String> getRoleNames(RequestContext context) throws JSONException {
        List<String> roleNames = new ArrayList<String>();

        String json = context.getRequestParameters().get(UM_ROLE_NAMES);
        if (json != null) {
            JSONArray array = new JSONArray(json);

            for(int i = 0; i < array.length(); i ++) {

                roleNames.add(array.getString(i));
            }
        }

        return roleNames;
    }

    private Set getAssignUsers(RequestContext context) throws Exception {
        String assignedUsersParam = context.getRequestParameters().get(RM_ASSIGN_USERS);

        Set assignedUsers = new HashSet();

        if (assignedUsersParam != null && assignedUsersParam.length() > 0) {

            JSONArray assignedUsersArray = new JSONArray(assignedUsersParam);

            for (int i = 0; i < assignedUsersArray.length(); i ++) {

                assignedUsers.add(assignedUsersArray.getString(i));
            }
        }

        return assignedUsers;
    }

    private Set getUnassignUsers(RequestContext context) throws Exception {
        String unassignUsersParam = context.getRequestParameters().get(RM_UNASSIGN_USERS);

        Set unassignUsers = new HashSet();

        if (unassignUsersParam != null && unassignUsersParam.length() > 0) {

            JSONArray assignedUsersArray = new JSONArray(unassignUsersParam);

            for (int i = 0; i < assignedUsersArray.length(); i ++) {

                unassignUsers.add(assignedUsersArray.getString(i));
            }
        }

        return unassignUsers;
    }

    private JSONObject getAvailableUserList(RequestContext context) throws Exception {
        String availableUserListParam = context.getRequestParameters().get(RM_AVAILABLE_USER_LIST);

        JSONObject availableUserList;
        if (availableUserListParam != null && availableUserListParam.length() > 0) {

            availableUserList = new JSONObject(availableUserListParam);
        } else {
            availableUserList = new JSONObject();

            availableUserList.put(UM_FIRST_RESULT, 0);
            availableUserList.put(UM_USER_NAME, "");
        }

        return availableUserList;
    }

    private JSONObject getAssignedUserList(RequestContext context) throws Exception {
        String assignedUserListParam = context.getRequestParameters().get(RM_ASSIGNED_USER_LIST);

        JSONObject assignedUserList;
        if (assignedUserListParam != null && assignedUserListParam.length() > 0) {

            assignedUserList = new JSONObject(assignedUserListParam);
        } else {
            assignedUserList = new JSONObject();

            assignedUserList.put(UM_FIRST_RESULT, 0);
            assignedUserList.put(UM_USER_NAME, "");
        }

        return assignedUserList;
    }

    private UsersOperationResult getUsersOperationResult(RequestContext context, String roleName) throws Exception {
        UsersOperationResult usersOperationResult = (UsersOperationResult) getSession(context).get(RM_ROLE_USERS);

        if (usersOperationResult == null || !usersOperationResult.getRoleName().equals(roleName)) {
            usersOperationResult = new UsersOperationResult(roleName);
            setUsersOperationResult(context, usersOperationResult);
        }

        return usersOperationResult;
    }

    private void setUsersOperationResult(RequestContext context, UsersOperationResult usersOperationResult) throws Exception {
        if (usersOperationResult != null) {
            getSession(context).put(RM_ROLE_USERS, usersOperationResult);
        }
    }

    private int getFirstResult(RequestContext context) {
        String firstResult = getDecodedRequestParameter(context, UM_FIRST_RESULT);

        return (firstResult != null) ? Integer.valueOf(firstResult).intValue() : 0;
    }
}