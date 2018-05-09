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

package com.jaspersoft.jasperserver.war.helper;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * 
 *
 */
public class JSONHelper implements Serializable{
    protected static final String AJAX_RESPONSE_SUCCESS = "success";

    protected static final String ERROR_TITLE = "title";
    protected static final String ERROR_MESSAGE = "message";
    protected static final String ERROR_DESC = "description";
    
    protected static final String USER_USER_NAME = "userName";
    protected static final String USER_FULL_NAME = "fullName";
    public static final String USER_PASSWORD = "password";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_ENABLED = "enabled";
    protected static final String USER_EXTERNAL = "external";
    protected static final String USER_ROLES = "roles";
    protected static final String USER_ATTRIBUTES = "attributes";
    protected static final String USER_TENANT_ID = "tenantId";

    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";

    protected static final String ROLE_ROLE_NAME = "roleName";
    protected static final String ROLE_EXTERNAL = "external";
    protected static final String ROLE_TENANT_ID = "tenantId";

    public static final String EXIST = "exist";

    protected static final String ENTITIES = "entities";

    protected String getPropertyIfHas(JSONObject userJson, String property) throws JSONException {
        if (userJson.has(property)) {
            return userJson.getString(property);
        } else {
            return null;
        }
    }

    protected JSONObject getJsonObjectIfHas(JSONObject userJson, String property) throws JSONException {
        if (userJson.has(property)) {
            return userJson.getJSONObject(property);
        } else {
            return null;
        }
    }

    public JSONObject createUnexpectedExceptionJson(String title, String message, String errorDescription)
            throws JSONException {
        JSONObject errorObject = new JSONObject();

        errorObject.put(ERROR_TITLE, title);
        errorObject.put(ERROR_MESSAGE, message);
        errorObject.put(ERROR_DESC, errorDescription);

        return errorObject;
    }

    public String createDataResponseModel(JSONObject data) throws JSONException {
        JSONObject responseModel = new JSONObject();

        responseModel.put("data", data);

        return responseModel.toString();
    }

    public String createErrorResponseModel(JSONObject error) throws JSONException {
        JSONObject responseModel = new JSONObject();

        responseModel.put("data", new JSONObject());
        if (error != null) {
            responseModel.put("error", error);
        }

        return responseModel.toString();
    }

    public String createSuccessResponseModel() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("status", AJAX_RESPONSE_SUCCESS);

        return createDataResponseModel(json);
    }

    public JSONObject createRolesResponseJson(List roles) throws JSONException {
        JSONObject data = new JSONObject();

        data.put(ENTITIES, convertRoleListToJson(roles, new String[]{ROLE_ROLE_NAME, ROLE_TENANT_ID}));

        return data;
    }

    public JSONObject createExistJson(boolean isRoleExist) throws JSONException {
        JSONObject isUserExistJson = new JSONObject();

        isUserExistJson.put(EXIST, isRoleExist);

        return isUserExistJson;
    }

    public JSONObject convertRoleToJson(Role user, String[] fields) throws JSONException {
        String roleName = user.getRoleName();
        Boolean external = user.isExternallyDefined();
        String tenantId = user.getTenantId();

        String[] allUserFields = {ROLE_ROLE_NAME, ROLE_EXTERNAL, ROLE_TENANT_ID};
        Object[] allFieldValues = {roleName, external, tenantId};
        List convertFields = (fields != null) ? Arrays.asList(fields) : Arrays.asList(allUserFields);

        JSONObject roleJson = new JSONObject();

        for (int i = 0; i < allUserFields.length; i++) {
            String userField = allUserFields[i];
            Object fieldValue = allFieldValues[i];

            if (convertFields.contains(userField)) {
                roleJson.put(userField, fieldValue);
            }
        }

        return roleJson;
    }

    public JSONArray convertRoleListToJson(List roleList, String[] fields) throws JSONException {
        JSONArray userListJson = new JSONArray();

        for (Object aRoleList : roleList) {
            Role role = (Role) aRoleList;

            userListJson.put(convertRoleToJson(role, fields));
        }

        return userListJson;
    }

    public Role convertJsonToRole(JSONObject roleJson) throws JSONException {
        Role role = new RoleImpl();
        role.setRoleName(getPropertyIfHas(roleJson, ROLE_ROLE_NAME));
        role.setTenantId(getPropertyIfHas(roleJson, ROLE_TENANT_ID));

        String external = getPropertyIfHas(roleJson, ROLE_EXTERNAL);
        role.setExternallyDefined((external != null) ? Boolean.valueOf(external) : false);

        if (roleJson.has(ENTITIES)) {
            JSONArray userJson =  roleJson.getJSONArray(ENTITIES);

            if (userJson != null && userJson.length() > 0) {
                for (int i = 0; i < userJson.length(); i++) {
                    User user = convertJsonToUser(userJson.getJSONObject(i));
                    role.addUser(user);
                }
            }
        }

        return role;
    }

    public Role convertJsonToRole(String json) throws JSONException {
        return convertJsonToRole(new JSONObject(json));
    }

    public Set<Role> convertJsonArrayToRoles(String json) throws JSONException {
        JSONArray array = new JSONArray(json);

        Set<Role> roles = new HashSet<Role>();
        for(int i = 0; i < array.length(); i++) {
            roles.add(convertJsonToRole(array.getJSONObject(i)));
        }

        return roles;
    }

    public Set<User> convertJsonArrayToUsers(String json) throws JSONException {
        JSONArray array = new JSONArray(json);

        Set<User> users = new HashSet<User>();
        for(int i = 0; i < array.length(); i++) {
            users.add(convertJsonToUser(array.getJSONObject(i)));
        }

        return users;
    }

    public JSONObject createUsersResponseJson(List users) throws JSONException {
        JSONObject data = new JSONObject();

        data.put(ENTITIES, convertUserListToJson(users,
                new String[]{
                        USER_USER_NAME,
                        USER_FULL_NAME,
                        USER_TENANT_ID,
                        USER_ENABLED}));

        return data;
    }

    public JSONObject createEmptyUserListResponseJson() throws JSONException {
        List<User> tempUserList = new ArrayList<User>();

        User tempUser = new UserImpl();

        tempUser.setFullName("");
        tempUser.setEmailAddress("");
        tempUser.setPassword("");
        tempUser.setEnabled(false);
        tempUser.setExternallyDefined(false);

        tempUserList.add(tempUser);

        return createUsersResponseJson(tempUserList);
    }

    @SuppressWarnings({"unchecked"})
    public JSONObject convertUserToJson(User user, String[] fields) throws JSONException {
        String userName = user.getUsername();
        String fullName = (user.getFullName() != null) ? user.getFullName() : "";
        String emailAddress = (user.getEmailAddress() != null) ? user.getEmailAddress() : "";
        //Bug 25942 - do not return password to the browser.
        //String password = (user.getPassword() != null) ? user.getPassword() : "";
        Boolean enabled = user.isEnabled();
        Boolean external = user.isExternallyDefined();
        String tenantId = user.getTenantId();
        List roles = (user.getRoles() != null) ? new ArrayList(user.getRoles()) : Collections.emptyList();
        JSONArray attributes = convertUserAttributesToJson(user.getAttributes());

        JSONArray rolesJson = convertRoleListToJson(roles,
                new String[]{ROLE_ROLE_NAME, ROLE_EXTERNAL, ROLE_TENANT_ID});

        String[] allUserFields =
                {USER_USER_NAME, USER_FULL_NAME, USER_EMAIL, /*USER_PASSWORD,*/ USER_ENABLED, USER_ROLES, USER_TENANT_ID, USER_EXTERNAL, USER_ATTRIBUTES};
        Object[] allFieldValues =
                {userName, fullName, emailAddress, /*password, */ enabled, rolesJson, tenantId, external, attributes};
        List convertFields = (fields != null) ? Arrays.asList(fields) : Arrays.asList(allUserFields);

        JSONObject userJson = new JSONObject();

        for (int i = 0; i < allUserFields.length; i++) {
            String userField = allUserFields[i];
            Object fieldValue = allFieldValues[i];

            if (convertFields.contains(userField)) {
                userJson.put(userField, fieldValue);
            }
        }

        return userJson;
    }

    public JSONArray convertUserAttributesToJson(List userAttributes) throws JSONException {
        List attributes = (userAttributes != null) ? userAttributes : new ArrayList();

        JSONArray attributesJson = new JSONArray();

        for (Object attribute1 : attributes) {
            ProfileAttribute attribute = (ProfileAttribute) attribute1;

            JSONObject attributeJson = new JSONObject();
            attributeJson.put(ATTRIBUTE_NAME, attribute.getAttrName());
            attributeJson.put(ATTRIBUTE_VALUE, attribute.getAttrValue());

            attributesJson.put(attributeJson);
        }

        return attributesJson;
    }

    public JSONArray convertUserListToJson(List userList, String[] fields) throws JSONException {
        JSONArray userListJson = new JSONArray();

        for (Object anUserList : userList) {
            User user = (User) anUserList;

            userListJson.put(convertUserToJson(user, fields));
        }

        return userListJson;
    }

    public User convertJsonToUser(String json) throws JSONException {
        return convertJsonToUser(new JSONObject(json));
    }

    public User convertJsonToUser(JSONObject userJson) throws JSONException {
        User user = new UserImpl();

        user.setUsername(getPropertyIfHas(userJson, USER_USER_NAME));
        user.setFullName(getPropertyIfHas(userJson, USER_FULL_NAME));
        user.setEmailAddress(getPropertyIfHas(userJson, USER_EMAIL));

        String enabled = getPropertyIfHas(userJson, USER_ENABLED);
        user.setEnabled((enabled != null) ? Boolean.valueOf(enabled) : false);

        String external = getPropertyIfHas(userJson, USER_EXTERNAL);
        user.setExternallyDefined((external != null) ? Boolean.valueOf(external) : false);

        user.setPassword(getPropertyIfHas(userJson, USER_PASSWORD));
        user.setTenantId(getPropertyIfHas(userJson, USER_TENANT_ID));

        if (userJson.has(USER_ROLES)) {
            JSONArray rolesJson = userJson.getJSONArray(USER_ROLES);

            if (rolesJson != null && rolesJson.length() > 0) {
                for (int i = 0; i < rolesJson.length(); i++) {
                    Role role = convertJsonToRole(rolesJson.getJSONObject(i));
                    user.addRole(role);
                }
            }
        }

        return user;
    }
}
