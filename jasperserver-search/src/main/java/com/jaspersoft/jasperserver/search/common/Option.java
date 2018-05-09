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

package com.jaspersoft.jasperserver.search.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Option to be used for filters and sorters.
 *
 * @author Stas Chubar
 * @version $Id$
 */
public class Option implements Serializable {
    private static final String JSON_ID = "id";
    private static final String JSON_LABEL_ID = "labelId";

    private String id;
    private String labelId;
    private List<RoleAccess> roleAccessList;
    private boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public List<RoleAccess> getRoleAccessList() {
        return roleAccessList;
    }

    public void setRoleAccessList(List<RoleAccess> roleAccessList) {
        this.roleAccessList = roleAccessList;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_ID, getId());
        jsonObject.put(JSON_LABEL_ID, getLabelId());

        return jsonObject;
    }
}
