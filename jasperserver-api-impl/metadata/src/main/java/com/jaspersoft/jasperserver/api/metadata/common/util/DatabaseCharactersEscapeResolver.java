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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import org.apache.commons.lang3.text.translate.LookupTranslator;

import java.util.Map;

/**
 * Database characters escape resolver. This class help escape text which is used for database queries.
 *
 * @author Yuriy Plakosh
 * @author Roman Kuziv
 * @version $Id$
 */
public class DatabaseCharactersEscapeResolver {
    private Map<String, Map<String, String>> charactersEscapeMaps;
    private String dialect;
    private LookupTranslator translator;

    public void setCharactersEscapeMaps(Map<String, Map<String, String>> charactersEscapeMaps) {
        this.charactersEscapeMaps = charactersEscapeMaps;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getEscapedText(String text) {
        return getTranslator().translate(text);
    }

    protected LookupTranslator getTranslator() {
        // lazy initialization of the translator
        if (translator == null) {
            Map<String, String> charactersEscapeMapForDialect;
            if (charactersEscapeMaps.containsKey(dialect)) {
                charactersEscapeMapForDialect = charactersEscapeMaps.get(dialect);
            } else {
                charactersEscapeMapForDialect = charactersEscapeMaps.get("default");
            }
            if (charactersEscapeMapForDialect != null) {
                String[][] lookups = new String[charactersEscapeMapForDialect.size()][2];
                int i = 0;
                for (Map.Entry<String, String> e : charactersEscapeMapForDialect.entrySet()) {
                    lookups[i] = new String[]{e.getKey(), e.getValue()};
                    i++;
                }
                translator = new LookupTranslator(lookups);
            } else {
                // empty translator, which doesn't escape any thing
                translator = new LookupTranslator();
            }
        }
        return translator;
    }
}
