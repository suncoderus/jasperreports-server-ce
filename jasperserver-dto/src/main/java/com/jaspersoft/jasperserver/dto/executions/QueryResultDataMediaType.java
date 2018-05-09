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

package com.jaspersoft.jasperserver.dto.executions;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 19.05.2016
 */
public class QueryResultDataMediaType {
    public final static String FLAT_DATA = "flatData";
    public final static String MULTI_LEVEL_DATA = "multiLevelData";
    public final static String MULTI_AXES_DATA = "multiAxesData";

    public final static String APPLICATION = "application";
    public final static String JSON = "+json";
    public final static String XML = "+xml";

    public final static String FLAT_DATA_JSON = APPLICATION + "/" + FLAT_DATA + JSON;
    public final static String MULTI_LEVEL_DATA_JSON = APPLICATION + "/" + MULTI_LEVEL_DATA + JSON;
    public final static String MULTI_AXES_DATA_JSON = APPLICATION + "/" + MULTI_AXES_DATA + JSON;

    public final static String FLAT_DATA_XML = APPLICATION + "/" + FLAT_DATA + XML;
    public final static String MULTI_LEVEL_DATA_XML = APPLICATION + "/" + MULTI_LEVEL_DATA + XML;
    public final static String MULTI_AXES_DATA_XML = APPLICATION + "/" + MULTI_AXES_DATA + XML;

    private static Map<String, Class<? extends ClientQueryResultData>> mediaTypeWithResultDataClassInfo =
            new HashMap<String, Class<? extends ClientQueryResultData>>(){{
                put(FLAT_DATA_JSON, ClientFlatQueryResultData.class);
                put(MULTI_LEVEL_DATA_JSON, ClientMultiLevelQueryResultData.class);
                put(MULTI_AXES_DATA_JSON, ClientMultiAxesQueryResultData.class);
            }};

    public static Class<? extends ClientQueryResultData> getResultDataType(String access) {
        for (Map.Entry<String, Class<? extends ClientQueryResultData>> entry : mediaTypeWithResultDataClassInfo.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(access)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
