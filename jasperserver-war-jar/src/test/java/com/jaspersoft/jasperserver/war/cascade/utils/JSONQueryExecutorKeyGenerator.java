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

package com.jaspersoft.jasperserver.war.cascade.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONQueryExecutorKeyGenerator implements QueryExecutorTestCaseKeyGenerator {
    public static final String NO_REPLACEMENT = "\"";

    private ObjectMapper jsonMapper = new ObjectMapper();
    private String replacementForDoubleQuotes = NO_REPLACEMENT;
    private Boolean quoteFieldNames = true;

    public void setReplacementForDoubleQuotes(String replacementForDoubleQuotes) {
        this.replacementForDoubleQuotes = replacementForDoubleQuotes;
    }

    public void setQuoteFieldNames(Boolean quoteFieldNames) {
        this.quoteFieldNames = quoteFieldNames;
    }

    @Override
    public String generateKey(String name, Map<String, Object> paramValues) throws Exception {
        jsonMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, quoteFieldNames);

        Map<String, Object> sortedParamValues = new LinkedHashMap<String, Object>(paramValues.size());
        List<String> paramNamesList = new ArrayList<String>(paramValues.keySet());
        Collections.sort(paramNamesList);

        for (String paramName: paramNamesList) {
            sortedParamValues.put(paramName, paramValues.get(paramName));
        }

        String json = jsonMapper.writeValueAsString(sortedParamValues);
        if (!NO_REPLACEMENT.equals(replacementForDoubleQuotes)) {
            json = json.replaceAll("\"", replacementForDoubleQuotes);
        }

        return json;
    }
}
