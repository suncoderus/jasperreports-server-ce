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
package com.jaspersoft.jasperserver.api.common.util;

import com.jaspersoft.jasperserver.api.common.properties.PropertyChanger;
import com.jaspersoft.jasperserver.api.common.properties.PropertyChangerAdapter;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link PropertyChanger} for JdbcDriverService
 *
 * @author Sergey Prilukin (sprilukin@jaspersoft.com)
 * @version $Id$
 */
public class JdbcDriverServicePropertyChanger extends PropertyChangerAdapter {

    final public static String PROPERTY_PREFIX = "jdbc:";

    public static final Log log = LogFactory.getLog(JdbcDriverServicePropertyChanger.class);

    private JdbcDriverService jdbcDriverService;

    public void setJdbcDriverService(JdbcDriverService jdbcDriverService) {
        this.jdbcDriverService = jdbcDriverService;
    }

    @Override
    public void setProperty(String key, String val) {
        checkKey(key);

        try {
            jdbcDriverService.setDriverMapping(getDriverClassName(key), val);
        } catch (Exception e) {
            log.warn(e);
        }
    }

    @Override
    public String getProperty(String key) {
        checkKey(key);

        return jdbcDriverService.getDriverMappings().get(getDriverClassName(key));
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : jdbcDriverService.getDriverMappings().keySet()) {
            map.put(PROPERTY_PREFIX + key, jdbcDriverService.getDriverMappings().get(key));
        }
        return map;
    }

    @Override
    public void removeProperty(String key, String val) {
        checkKey(key);

        try {
            jdbcDriverService.removeDriverMapping(getDriverClassName(key));
        } catch (Exception e) {
            log.warn(e);
        }
    }

    private void checkKey(String key) {
        if (!key.startsWith(PROPERTY_PREFIX)) {
            throw new IllegalArgumentException(String.format("Key should starts with %s", PROPERTY_PREFIX));
        }
    }

    private String getDriverClassName(String key) {
        return key.substring(PROPERTY_PREFIX.length(), key.length());
    }
}
