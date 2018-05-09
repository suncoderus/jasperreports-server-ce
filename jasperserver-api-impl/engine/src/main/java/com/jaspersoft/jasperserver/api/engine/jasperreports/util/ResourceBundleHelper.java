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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Locale;

/**
 */
public class ResourceBundleHelper {
    public static final String BUNDLE_FILE_EXTENSION = ".properties";

    private static Pattern PATTERN_BUNDLE_FILE =
        Pattern.compile(".*(?:(?:[_])([a-z][a-z]))(?:[_]){0,1}([A-Z][A-Z]){0,1}(?:(?:[_])([^_]*)){0,1}\\.properties");

    public static Locale parseSuffixForLocale(String fileName) {
        Matcher m = PATTERN_BUNDLE_FILE.matcher(fileName);
        if (m.find()) {
            String language = m.group(1);
            String country = (m.group(2) != null) ? m.group(2) : "";
            String variant = (m.group(3) != null) ? m.group(3) : "";
            return new Locale(language, country, variant);
        }
        return new Locale("","","");
    }

    public static String getBaseName(String fileName) {
        Locale locale = parseSuffixForLocale(fileName);
        String localeStr = locale.toString();
        String searchStr = (localeStr.length() > 0) ? "_" + localeStr : "";
        int k = fileName.indexOf(searchStr + BUNDLE_FILE_EXTENSION);
        if (k > 0) {
            return fileName.substring(0, k);
        }
        return "";
    }

    public static boolean isBundle(String fileName) {
        return fileName.endsWith(BUNDLE_FILE_EXTENSION);
    }

}
