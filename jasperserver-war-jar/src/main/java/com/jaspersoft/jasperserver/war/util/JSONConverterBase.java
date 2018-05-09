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

package com.jaspersoft.jasperserver.war.util;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author asokolnikov
 */
public class JSONConverterBase implements Serializable{

    public static String RESPONSE_STATUS = "status";
    public static String RESPONSE_DATA = "data";

    public enum Status {
        OK,
        ERROR
    }

    public JSONObject createOKJSONResponse(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(RESPONSE_STATUS, Status.OK);
        jsonObject.put(RESPONSE_DATA, message);

        return jsonObject;
    }

    public JSONObject createErrorJSONResponse(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(RESPONSE_STATUS, Status.ERROR);
        jsonObject.put(RESPONSE_DATA, message);

        return jsonObject;
    }

    public JSONObject createJSONResponse(Object json) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(RESPONSE_STATUS, Status.OK);
        jsonObject.put(RESPONSE_DATA, json);

        return jsonObject;
    }

}
