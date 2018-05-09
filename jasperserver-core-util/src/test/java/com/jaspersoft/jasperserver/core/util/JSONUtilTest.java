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

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 3/6/12
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONUtilTest {
    @Test
    public void testApplyFunctorToJsonRecursively() throws Exception {
        String testJsonStr = "{\"test\": [{\"Misha\": \"Ch\", \"Andrew\": \"SF\", \"Norm\": \"SF\"}, {\"SF\": [\"Norm\", \"Andrew\", \"Chaim\"], \"Ch\": \"Misha\"}]}";
        JSONObject testJson = new JSONObject(testJsonStr);

        String expectedPostProcJsonStr = "{\"test\": [{\"Misha\": \"Ch\", \"Andrew\": \"SF\", \"Norm\": \"SF\"}, {\"SF\": [\"NORM\", \"ANDREW\", \"CHAIM\"], \"Ch\": \"Misha\"}]}";
        JSONObject expectedPostProcJson = new JSONObject(expectedPostProcJsonStr);
        expectedPostProcJsonStr = expectedPostProcJson.toString();

        Assert.assertFalse("Before processing the json strings  should not be equal.", expectedPostProcJsonStr.equals(testJson.toString()));

        JSONUtil.applyFunctorToJson(testJson, new JSONUtil.Functor() {
            public String call(String jsonKey, String jsonValue) {
                if (jsonKey.equalsIgnoreCase("SF"))
                    return jsonValue.toUpperCase();

                return jsonValue;
            }
        });

        Assert.assertEquals("Processing did not make the json's equal like it should have", expectedPostProcJsonStr, testJson.toString());
    }
}
