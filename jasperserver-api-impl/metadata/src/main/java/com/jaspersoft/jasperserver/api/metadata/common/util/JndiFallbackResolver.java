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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.properties.DecryptingPropertyPlaceholderConfigurer;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Resolves JNDI names to JDBC properties.</p>
 *
 * @author Bob
 * @author Yuriy Plakosh
 */
public class JndiFallbackResolver implements Serializable {
    public static final String JDBC_DRIVER_CLASS_NAME = "driverClassName";
    public static final String JDBC_URL = "url";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";


    /**
     * Returns the map of JDBC properties which were resolved from fallback properties file using specified JNDI name.
     *
     * @param jndiName the JNDI name.
     *
     * @return the map of JDBC properties.
     */
    public Map<String, String> getJdbcPropertiesMap(String jndiName) {
		Map<String, String> springProps = DecryptingPropertyPlaceholderConfigurer.getSpringImportedProperties();
        if (springProps == null) {
            throw new JSException("No JNDI fallback properties available.");
        }

        // look for prop matching jndi name
        String propPrefix = null;
        for (Map.Entry<String, String> entry : springProps.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(".jndi") && entry.getValue().equals(jndiName)) {
                propPrefix = key.replace(".jndi", "");
                break;
            }
        }
        if (propPrefix == null) {
            throw new JSException("No matching JNDI property found for '" + jndiName + "' in fallback properties.");
        }

        Map<String, String> jdbcPropertiesMap = new HashMap<String, String>(4);

        // Pull everything out of props and put it to the map.
        jdbcPropertiesMap.put(JDBC_DRIVER_CLASS_NAME, getFallbackProperty(propPrefix, JDBC_DRIVER_CLASS_NAME));
        jdbcPropertiesMap.put(JDBC_URL, StringEscapeUtils.unescapeXml(getFallbackProperty(propPrefix, JDBC_URL)));
        jdbcPropertiesMap.put(JDBC_USERNAME, getFallbackProperty(propPrefix, JDBC_USERNAME));
        jdbcPropertiesMap.put(JDBC_PASSWORD, getFallbackProperty(propPrefix, JDBC_PASSWORD));

        return jdbcPropertiesMap;
    }

    private String getFallbackProperty(String prefix, String suffix) {
		Map<String, String> springProps = DecryptingPropertyPlaceholderConfigurer.getSpringImportedProperties();
        String value = springProps.get(prefix + ".jdbc." + suffix);
        if (value == null) {
            throw new JSException("No value for '" + suffix + "' in fallback properties.");
        }
        return value;
    }

}
