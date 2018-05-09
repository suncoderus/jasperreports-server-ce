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

package com.jaspersoft.jasperserver.core.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaspersoft.jasperserver.api.JSException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 3/6/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONUtil {
    private JSONUtil() {/* This util class.  Not to be instantiated.*/}

    private static ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static interface Functor {
        /**
         *  Functor method that will operate on jsonValue.  To be implemented by the user.
         *
         * @param jsonKey  if jsonValue's parent is JSONObject, jsonKey points to jsonValue in the object.
         *                 if jsonValue's parent is JSONArray, jsonKey points to jsonElem AND arrIndex points to jsonValue.
         * @param jsonValue  value on which the functor should operate.
         *
         * @return modified jsonValue if you want to modify the json to which this functor is applied.
         * Return null, if you do not want to modify the json.
         *
         */
        public Object call(String jsonKey, String jsonValue);
    }

    /**
     * Calls applyFunctorToJsonRecursively(Object jsonElem, String jsonElemKey, Functor<T> functor) with init jsonElemKey equal null
     *
     * @param jsonElem
     * @param functor
     * @throws JSONException
     */
    public static void applyFunctorToJson(Object jsonElem, Functor functor)  {
        applyFunctorToJsonRecursively(jsonElem, null, functor);
    }

    /**
     *
     * @param jsonElem
     * @param jsonElemKey
     * @param functor
     * @throws JSONException
     */
    private static void applyFunctorToJsonRecursively(Object jsonElem, String jsonElemKey, Functor functor)  {
        try {
            if (jsonElem == null)
                return;

            if (jsonElem instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) jsonElem;
                for (int j = 0; j < jsonArray.length(); ++j) {
                    Object elem = jsonArray.get(j);
                    if (elem instanceof JSONObject)
                        applyFunctorToJsonRecursively(elem, null, functor);
                    else if (elem instanceof JSONArray)
                        applyFunctorToJsonRecursively(elem, jsonElemKey, functor);
                    else {
                        Object procVal = functor.call(jsonElemKey, elem.toString());        //case: key:[val1, val2]
                        if (procVal != null)
                            jsonArray.put(j, procVal);
                    }
                }
            }
            else if (jsonElem instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) jsonElem;
                for (Iterator<String> iter = jsonObject.keys(); iter.hasNext(); ) {
                    String childElemKey = iter.next();
                    Object childElemVal = jsonObject.get(childElemKey);
                    if (childElemVal instanceof JSONObject || childElemVal instanceof JSONArray)
                        applyFunctorToJsonRecursively(childElemVal, childElemKey, functor);
                    else {
                        Object procVal = functor.call(childElemKey, childElemVal.toString());
                        if (procVal != null)
                            ((JSONObject) jsonElem).put(childElemKey, procVal);
                    }
                }
            }
            else
                return;
        }
        catch (JSONException je) {
            throw new RuntimeException("Exception during applying functor to JSON.", je);
        }
    }

    /**
     * Test whether maybeJsonStr is json object.
     * @param maybeJsonStr
     * @return true if it maybeJsonStr can be converted to a json entity
     */
    public static JSONObject getJSONObject(String maybeJsonStr) {
        try {
            return new JSONObject(maybeJsonStr);
        }
        catch (Exception e1) {
            return null;
        }
    }
    
    /**
     * Test whether maybeJsonStr is json array.
     * @param maybeJsonStr
     * @return true if it maybeJsonStr can be converted to a json entity
     */
    public static JSONArray getJSONArray(String maybeJsonStr) {
        try {
            return new JSONArray(maybeJsonStr);
        }
        catch (Exception e1) {
            return null;
        }
    }

    public static String toJSON(Object obj) {
        //TODO find common place for these utils
        if(obj == null) {
            return "{}";
        }
        try {
            return JSON_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new JSException("Failed to serialize to JSON", e);
        }
    }

    public static String toJSON(Collection col) {
        if(col == null) {
            return "[]";
        }
        return toJSON((Object)col);
    }
}
