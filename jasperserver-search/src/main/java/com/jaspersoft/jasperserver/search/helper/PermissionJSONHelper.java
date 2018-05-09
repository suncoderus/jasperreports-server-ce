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

package com.jaspersoft.jasperserver.search.helper;

import com.jaspersoft.jasperserver.search.model.permission.Permission;
import com.jaspersoft.jasperserver.search.model.permission.PermissionToDisplay;
import com.jaspersoft.jasperserver.search.model.permission.RoleWithPermission;
import com.jaspersoft.jasperserver.search.model.permission.UserWithPermission;
import com.jaspersoft.jasperserver.war.helper.JSONHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Permission JSON helper.
 *
 * @author Yuriy Plakosh
 */
public class PermissionJSONHelper extends JSONHelper implements Serializable {
    protected static final String PERMISSION_TO_DISPLAY = "permissionToDisplay";
    protected static final String IS_DISABLED = "isDisabled";
    protected static final String PERMISSION = "permission";
    protected static final String INHERITED_PERMISSION = "inheritedPermission";
    protected static final String IS_INHERITED = "isInherited";
    protected static final String TYPE = "type";
    protected static final String NEW_PERMISSION = "newPermission";
    
    public JSONObject createUserWithPermissionsJson(List<UserWithPermission> userWithPermissionList, String objectType)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        for (UserWithPermission userWithPermission : userWithPermissionList) {
            jsonArray.put(createUserWithPermissionJson(userWithPermission));
        }
        jsonObject.put(ENTITIES, jsonArray);

        jsonObject.put(TYPE, objectType);

        return jsonObject;
    }

    private JSONObject createUserWithPermissionJson(UserWithPermission userWithPermission) throws JSONException {
        JSONObject jsonObject = convertUserToJson(userWithPermission.getUser(), null);

        JSONObject permissionToDisplay = convertPermissionToDisplayToJson(userWithPermission.getPermissionToDisplay());
        jsonObject.put(PERMISSION_TO_DISPLAY, permissionToDisplay);

        return jsonObject;
    }

    private JSONObject convertPermissionToDisplayToJson(PermissionToDisplay permissionToDisplay) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (permissionToDisplay.getPermission() != null) {
            jsonObject.put(PERMISSION, permissionToDisplay.getPermission().name());
        }
        if (permissionToDisplay.getInheritedPermission() != null) {
            jsonObject.put(INHERITED_PERMISSION, permissionToDisplay.getInheritedPermission().name());
        }
        jsonObject.put(IS_INHERITED, permissionToDisplay.isInherited());
        jsonObject.put(IS_DISABLED, permissionToDisplay.isDisabled());

        return jsonObject;
    }

    public JSONObject createRoleWithPermissionsJson(List<RoleWithPermission> roleWithPermissionList, String objectType)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        for (RoleWithPermission roleWithPermission : roleWithPermissionList) {
            jsonArray.put(createRoleWithPermissionJson(roleWithPermission));
        }
        jsonObject.put(ENTITIES, jsonArray);

        jsonObject.put(TYPE, objectType);

        return jsonObject;
    }

    private JSONObject createRoleWithPermissionJson(RoleWithPermission roleWithPermission) throws JSONException {
        JSONObject jsonObject = convertRoleToJson(roleWithPermission.getRole(), null);

        JSONObject permissionToDisplay = convertPermissionToDisplayToJson(roleWithPermission.getPermissionToDisplay());
        jsonObject.put(PERMISSION_TO_DISPLAY, permissionToDisplay);

        return jsonObject;
    }

    public Set<UserWithPermission> convertJsonArrayToUserWithPermissionSet(String json) throws JSONException {
        JSONArray array = new JSONArray(json);

        Set<UserWithPermission> userWithPermissionSet = new HashSet<UserWithPermission>(array.length());
        for(int i = 0; i < array.length(); i++) {
            UserWithPermission userWithPermission = new UserWithPermission();

            userWithPermission.setUser(convertJsonToUser(array.getJSONObject(i)));
            userWithPermission.setPermissionToDisplay(convertJsonToPermissionToDisplay(
                    getJsonObjectIfHas(array.getJSONObject(i), PERMISSION_TO_DISPLAY)));

            userWithPermissionSet.add(userWithPermission);
        }

        return userWithPermissionSet;
    }

    public Set<RoleWithPermission> convertJsonArrayToRoleWithPermissionSet(String json) throws JSONException {
        JSONArray array = new JSONArray(json);

        Set<RoleWithPermission> roleWithPermissionSet = new HashSet<RoleWithPermission>(array.length());
        for(int i = 0; i < array.length(); i++) {
            RoleWithPermission roleWithPermission = new RoleWithPermission();

            roleWithPermission.setRole(convertJsonToRole(array.getJSONObject(i)));
            roleWithPermission.setPermissionToDisplay(convertJsonToPermissionToDisplay(
                    getJsonObjectIfHas(array.getJSONObject(i), PERMISSION_TO_DISPLAY)));

            roleWithPermissionSet.add(roleWithPermission);
        }

        return roleWithPermissionSet;
    }

    private PermissionToDisplay convertJsonToPermissionToDisplay(JSONObject jsonObject) throws JSONException {
        PermissionToDisplay permissionToDisplay = new PermissionToDisplay();

        String permissionString  = getPropertyIfHas(jsonObject, PERMISSION);
        permissionToDisplay.setPermission(permissionString != null ? Permission.valueOf(permissionString) : null);

        String newPermissionString  = getPropertyIfHas(jsonObject, NEW_PERMISSION);
        permissionToDisplay.setNewPermission(newPermissionString != null ?
                Permission.valueOf(newPermissionString) : null);

        String inheritedPermissionString  = getPropertyIfHas(jsonObject, INHERITED_PERMISSION);
        permissionToDisplay.setInheritedPermission(inheritedPermissionString != null ?
                Permission.valueOf(inheritedPermissionString) : null);

        permissionToDisplay.setInherited(Boolean.parseBoolean(getPropertyIfHas(jsonObject, IS_INHERITED)));

        return permissionToDisplay;
    }
}
