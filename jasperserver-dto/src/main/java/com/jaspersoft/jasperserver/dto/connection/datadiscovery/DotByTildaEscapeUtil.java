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
package com.jaspersoft.jasperserver.dto.connection.datadiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DotByTildaEscapeUtil {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?<!~)(?:~~)*\\.");
    private static final Pattern VALIDATE_PATTERN = Pattern.compile("(?<!~)(?:~~)*~(?!(~|\\.))");
    public String escapeDot(String toEscape){
        return toEscape.replace("~", "~~").replace(".", "~.");
    }
    
    public String unEscapeDot(String toUnEscape){
        validateBeforeUnEscape(toUnEscape);
        return toUnEscape.replace("~~", "~").replace("~.", ".");
    }
    
    public String[] splitByDotUnEscapeTokens(String dotQualifiedString){
        validateBeforeUnEscape(dotQualifiedString);
        if(dotQualifiedString == null) return null;
        final Matcher matcher = SPLIT_PATTERN.matcher(dotQualifiedString);
        int start = 0;
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()){
            final int end = matcher.end();
            tokens.add(dotQualifiedString.substring(start, end - 1));
            start = end;
        }
        tokens.add(dotQualifiedString.substring(start, dotQualifiedString.length()));
        final String[] split = tokens.toArray(new String[tokens.size()]);
        for(int i = 0; i < split.length; i++){
            split[i] = unEscapeDot(split[i]);
        }
        return split;
    }

    protected void validateBeforeUnEscape(String string){
        List<Integer> indexes = new ArrayList<Integer>();
        final Matcher matcher = VALIDATE_PATTERN.matcher(string);
        while (matcher.find()){
            indexes.add(matcher.end() - 1);
        }
        if(!indexes.isEmpty()){
            throw new UnexpectedEscapeCharacterException(string, indexes);
        }

    }
    
    public String toDotQualifiedString(String[] tokens){
        if(tokens == null || tokens.length == 0) return null;
        StringBuilder builder = new StringBuilder();
        for (String token : tokens) {
            if(builder.length() != 0){
                builder.append(".");
            }
            builder.append(escapeDot(token));
        }
        return builder.toString();
    }
}
