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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserManagerService;
import com.jaspersoft.jasperserver.api.security.SecurityConfiguration;
import com.jaspersoft.jasperserver.api.security.encryption.EncryptionFilter;
import com.jaspersoft.jasperserver.war.helper.JSONHelper;
import org.json.JSONException;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.json.JSONObject;
import org.json.JSONArray;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.common.service.PaginatedOperationResult;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.PaginationHelper;

import java.util.*;

/**
 * User manager action class.
 *
 * @author schubar
 * @author Yuriy Plakosh
 */
public class UserManagerAction extends BaseManagerAction {
    protected static final String UM_DEFAULT_USER = "defaultUser";
    protected static final String UM_USER_DETAILS = "userDetails";
    protected static final String UM_USER_NAME = "userName";
    protected static final String UM_USER_NAMES = "userNames";
    protected static final String UM_ROLE_NAME = "roleName";
    protected static final String UM_USER_ROLES = "userRoles";
    protected static final String UM_FIRST_RESULT = "firstResult";
    protected static final String UM_EMAIL_REG_EXP_PATTERN = "emailRegExpPattern";

    // JSON attributes.
    protected static final String JSON_ATTRIBUTE_USER_NAME_SEPARATOR = "userNameSeparator";
    protected static final String JSON_ATTRIBUTE_USER_NAME_NOT_SUPPORTED_SYMBOLS = "userNameNotSupportedSymbols";
    protected static final String JSON_ATTRIBUTE_USER_DEFAULT_ROLE = "userDefaultRole";
    protected static final String JSON_ATTRIBUTE_USER_PASSWORD_MASK = "passwordMask";
    protected static final String JSON_ATTRIBUTE_USER_PASSWORD_PATTERN = "passwordPattern";

    private UserManagerService managerService;

    protected MessageSource getMessages() {
        return messages;
    }

    protected JSONHelper getJsonHelper() {
        return this.jsonHelper;
    }

    public void setManagerService(UserManagerService managerService) {
        this.managerService = managerService;
    }

    public Event initEvent(RequestContext context) throws Exception {
        initState(context);

        JSONObject jsonConfiguration = new JSONObject();
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_NAME_NOT_SUPPORTED_SYMBOLS,
                configuration.getUserNameNotSupportedSymbols());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_NAME_SEPARATOR, configuration.getUserNameSeparator());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_DEFAULT_ROLE, configuration.getDefaultRole());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_PASSWORD_MASK, configuration.getPasswordMask());
        jsonConfiguration.put(JSON_ATTRIBUTE_USER_PASSWORD_PATTERN, userService.getAllowedPasswordPattern());
        jsonConfiguration.put(JSON_ATTRIBUTE_SUPERUSER_ROLE, ROLE_SUPERUSER);
        jsonConfiguration.put(JSON_ATTRIBUTE_ADMIN_ROLE, ROLE_ADMINISTRATOR);
        jsonConfiguration.put(UM_EMAIL_REG_EXP_PATTERN, configuration.getEmailRegExpPattern());

        context.getFlowScope().put(FLOW_ATTRIBUTE_CONFIGURATION, jsonConfiguration.toString());

        context.getFlowScope().put(UM_DEFAULT_USER, getDefaultEntity(context));
        context.getFlowScope().put(FLOW_ATTRIBUTE_DEFAULT_ENTITY, getDefaultEntity(context));
        context.getFlowScope().put(FLOW_ATTRIBUTE_CURRENT_USER, getCurrentUser());
        context.getFlowScope().put(FLOW_ATTRIBUTE_CURRENT_USER_ROLES,
                jsonHelper.convertRoleListToJson(getCurrentUserRoles(), null).toString());

        return success();
    }

    public Event next(RequestContext context) {
        State state = getState(context);
        final EntitiesListState entitiesState = state.getEntitiesState();

        String responseModel;
        try {
            final Set tenantIdSet = getSubTenantIdsSet(state.getTenantId());

            List users = getEntitiesAndUpdateState(entitiesState, configuration.getRoleItemsPerPage(),
                    new EntitiesListManager() {
                        public int getResultsCount() {
                            return userService.getTenantUsersCount(null, tenantIdSet, entitiesState.getText());
                        }

                        public List getResults(int resultIndex, int maxResults) {
                            return userService.getTenantUsers(null, tenantIdSet, entitiesState.getText(),
                                resultIndex, maxResults);
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

    public Event getDetails(RequestContext context) {
        State state = getState(context);
        String userName = state.getSelectedEntity();

        String responseModel;
        try {
            User user = this.userService.getUser(null, userName);

            if (user != null) {
                // TODO: do we need make password hidden?
                //user.setPassword(hidePassword(user.getPassword()));
                JSONObject usersJson = jsonHelper.convertUserToJson(user, null);
                responseModel = jsonHelper.createDataResponseModel(usersJson);
            } else {
                throw new IllegalArgumentException("Cannot find user with username : " + userName);
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

    public Event nextAssigned(RequestContext context) {
        final State state = getState(context);
        final EntitiesListState entitiesState = state.getAssignedEntitiesState();

        String responseModel;
        try {
            List roles = getEntitiesAndUpdateState(entitiesState, configuration.getEntitiesPerPage(),
                    new EntitiesListManager() {
                        @SuppressWarnings({"unchecked"})
                        public int getResultsCount() {
                            return userService.getAssignedRolesCount(null, state.getSelectedEntity(),
                                    entitiesState.getText());
                        }

                        public List getResults(int resultIndex, int maxResults) {
                            return userService.getAssignedRoles(null, state.getSelectedEntity(),
                                entitiesState.getText(), resultIndex, maxResults);
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

    public Event nextAvailable(RequestContext context) {
        final State state = getState(context);
        final EntitiesListState entitiesState = state.getAvailableEntitiesState();

        String responseModel;
        try {
            List roles = getEntitiesAndUpdateState(entitiesState, configuration.getEntitiesPerPage(),
                    new EntitiesListManager() {
                        @SuppressWarnings({"unchecked"})
                        public int getResultsCount() {
                            return userService.getAvailableRolesCount(null, state.getSelectedEntity(),
                                    entitiesState.getText());
                        }

                        public List getResults(int resultIndex, int maxResults) {
                            return userService.getAvailableRoles(null, state.getSelectedEntity(),
                                entitiesState.getText(), resultIndex, maxResults);
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

    public Event exist(RequestContext context) {
        String userName = getParameter(context, PARAMETER_ENTITY_NAME);

        String responseModel;
        try {
            User user = this.userService.getUser(null, userName);

            JSONObject existJson = jsonHelper.createExistJson((user != null));
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
        String userName = getParameter(context, PARAMETER_ENTITY);

        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }
        
        try {
            if (userName.length() > 0) {
                createAuditEvent("deleteUser");
                userService.deleteUser(null, userName);
                closeAuditEvent("deleteUser");
            } else {
                throw new IllegalArgumentException("Username is empty.");
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
            List<String> userNames = getEntities(context);

            //Bug 37601
            if (userNames.size() == 1) {
                createAuditEvent("deleteUser");
                managerService.deleteAll(null, userNames);
                closeAuditEvent("deleteUser");
            }
            else if (userNames.size() > 0) {
                createAuditEvent("deleteUsers");
                managerService.deleteAll(null, userNames);
                closeAuditEvent("deleteUsers");
            } else {
                throw new IllegalArgumentException("Username is empty.");
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

    public Event create(RequestContext context) {
        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }

        try {
            String userJson = getParameter(context, PARAMETER_ENTITY);
            if (SecurityConfiguration.isEncryptionOn()) {
                //user json is expected in a decrypted form here
                MutableAttributeMap attributeMap = context.getExternalContext().getRequestMap();
                List decryptedJsonList = (List) attributeMap.get(EncryptionFilter.DECRYPTED_PREFIX + PARAMETER_ENTITY);
                if (decryptedJsonList != null  && decryptedJsonList.size() > 0)
                    userJson = decryptedJsonList.get(0).toString();
                else
                    throw new Exception("Expected a decrypted password in request attribute, but found none");
            }

            if (userJson != null && userJson.length() > 0) {
                User user = jsonHelper.convertJsonToUser(userJson);

                if (user.getTenantId() != null) {
                    Tenant tenant = tenantService.getTenant(null, user.getTenantId());

                    if (tenant == null) {
                        throw new IllegalArgumentException("Cannot find organization with id : " + user.getTenantId());
                    }
                }

                user.setPreviousPasswordChangeTime(new Date());

                createAuditEvent("createUser");
                userService.putUser(null, user);
                closeAuditEvent("createUser");
            } else {
                throw new IllegalAccessException("Error when creating user");
            }
        } catch (Exception e) {
            try {
                responseModel = createUnexpectedExceptionResponseModel(e);
            } catch (JSONException e1) {
                return error(e1);
            }
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event enableAll(RequestContext context) throws Exception {
        List<String> userNames = getUserNames(context);

        String responseModel = jsonHelper.createSuccessResponseModel();
        try {
            if (userNames.size() > 0) {
                createAuditEvent("enableAllUsers");
                managerService.enableAll(null, userNames);
                closeAuditEvent("enableAllUsers");
            } else {
                throw new IllegalArgumentException("Username is empty.");
            }
        } catch (Exception e) {
            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event disableAll(RequestContext context) throws Exception {
        List<String> userNames = getUserNames(context);

        String responseModel = jsonHelper.createSuccessResponseModel();
        try {
            if (userNames.size() > 0) {
                createAuditEvent("disableAllUsers");
                managerService.disableAll(null, userNames);
                closeAuditEvent("disableAllUsers");
            } else {
                throw new IllegalArgumentException("Username is empty.");
            }
        } catch (Exception e) {
            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public Event updateUser(RequestContext context) throws Exception {
        String responseModel;
        try {
            responseModel = jsonHelper.createSuccessResponseModel();
        } catch (JSONException e) {
            return error(e);
        }

        try {
            String userJson = getParameter(context, PARAMETER_ENTITY);
            if (SecurityConfiguration.isEncryptionOn()) {
                //user json is expected in a decrypted form here
                MutableAttributeMap attributeMap = context.getExternalContext().getRequestMap();
                List decryptedJsonList = (List) attributeMap.get(EncryptionFilter.DECRYPTED_PREFIX + PARAMETER_ENTITY);
                if (decryptedJsonList != null  && decryptedJsonList.size() > 0)
                    userJson = decryptedJsonList.get(0).toString();
                else
                    throw new Exception("Expected a decrypted password in request attribute, but found none");
            }

            String userName = getParameter(context, PARAMETER_ENTITY_NAME);
            String assignedJson = getParameter(context, PARAMETER_ASSIGNED_ENTITIES);
            String unassignedJson = getParameter(context, PARAMETER_UNASSIGNED_ENTITIES);

            if (userJson != null && userJson.length() > 0) {
                User user = jsonHelper.convertJsonToUser(userJson);
                Set<Role> assigned = jsonHelper.convertJsonArrayToRoles(assignedJson);
                Set<Role> unassigned = jsonHelper.convertJsonArrayToRoles(unassignedJson);

                if (user != null && StringUtils.hasText(user.getUsername())) {
                    // When password is empty, lets set it to null.
                    // User manager service will leave the old one without changes.
                    if (!StringUtils.hasText(user.getPassword())) {
                        user.setPassword(null);
                    }

                    createAuditEvent("updateUser");
                    managerService.updateUser(null, userName, user, assigned, unassigned);
                    closeAuditEvent("updateUser");

                  //
                  //  2012-05-09  thorick chow
                  //              send back complete User details for UI screen to draw
                  //
                  User userLookedup = this.userService.getUser(null, userName);
                  JSONObject usersJson = jsonHelper.convertUserToJson(userLookedup, null);
                  responseModel = jsonHelper.createDataResponseModel(usersJson);

                } else {
                    throw new IllegalArgumentException("Error when updating user details.");
                }
            }
        } catch (Exception e) {
            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    private String getOldUserPassword(String userName) {
        User user = this.userService.getUser(null, userName);

        return user.getPassword();
    }

    public Event getAvailableRoles(RequestContext context) throws Exception {
        final String userName = getUserName(context, false);
        final String roleName = getRoleName(context);
        final Set userRoles = getUserRoles(context);
        final int firstResult = getFirstResult(context);
        int maxResults = 10;

        String responseModel/* = createResponseModel("[]", null)*/;

        try {

            PaginatedOperationResult result = PaginationHelper.paginatedGetOperationResult(firstResult, maxResults,
                    new PaginationHelper.JasperService() {
                            public List getResultList(int firstResult, int maxResults) {
                                return userService.getAvailableRoles(null, roleName, userRoles, userName, firstResult, maxResults);
                            }

                            public int getResultCount() {
                                return userService.getAvailableRolesCount(null, roleName, userRoles, userName);
                            }
            });

            List roles = result.getResult();
            int rolesCount = result.getTotalResults();

//            if (roles != null && !roles.isEmpty()) {
//
                JSONObject rolesJson =
                        jsonHelper.createRolesResponseJson(roles);//, result.getFirstResult(), maxResults, rolesCount);
                responseModel = jsonHelper.createDataResponseModel(rolesJson);
//            } else {
//                responseModel = jsonHelper.createDataResponseModel(jsonHelper.createEmptyRoleListResponseJson());
//            }
        } catch (Exception e) {

            responseModel = createUnexpectedExceptionResponseModel(e.getMessage());
        }

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, responseModel);

        return success();
    }

    public String hidePassword(String password) {

        return (password == null) ? password : password.replaceAll(".", configuration.getPasswordMask());
    }

    private String getUserJson(RequestContext context) {
        return context.getRequestParameters().get(UM_USER_DETAILS);
    }

    private List<String> getUserNames(RequestContext context) throws JSONException {
        List<String> userNames = new ArrayList<String>();

        String json = context.getRequestParameters().get(UM_USER_NAMES);
        if (json != null) {
            JSONArray array = new JSONArray(json);

            for(int i = 0; i < array.length(); i ++) {

                userNames.add(array.getString(i));
            }
        }

        return userNames;
    }

    private String getUserName(RequestContext context, boolean decode) {
        String name;
        if (decode) {
            name = getDecodedRequestParameter(context, UM_USER_NAME);
        } else {
            name = context.getRequestParameters().get(UM_USER_NAME);
        }

        return (name != null) ? name : "";
    }

    private String getRoleName(RequestContext context) {
        String name = getDecodedRequestParameter(context, UM_ROLE_NAME);

        return (name != null) ? name : "";
    }

    private Set getUserRoles(RequestContext context) throws Exception {
        String userRolesParam = getDecodedRequestParameter(context, UM_USER_ROLES);

        Set userRoles = new HashSet();

        if (userRolesParam != null && userRolesParam.length() > 0) {

            JSONArray userRolesArray = new JSONArray(userRolesParam);

            for (int i = 0; i < userRolesArray.length(); i ++) {

                userRoles.add(userRolesArray.getString(i));
            }
        }

        return userRoles;
    }

    private int getFirstResult(RequestContext context) {
        String firstResult = getDecodedRequestParameter(context, UM_FIRST_RESULT);

        return (firstResult != null) ? Integer.valueOf(firstResult).intValue() : 0;
    }

}
