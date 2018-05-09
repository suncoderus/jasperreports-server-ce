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
package com.jaspersoft.jasperserver.jaxrs.jdbcdrivers;

import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.dto.jdbcdrivers.JdbcDriverInfo;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class JdbcDriversJaxrsServiceTest {
    @InjectMocks
    private JdbcDriversJaxrsService service = new JdbcDriversJaxrsService();
    @Mock
    private JdbcDriverService jdbcDriverService;
    private Map<String, Map<String, Object>> jdbcConnectionMap = new HashMap<String, Map<String, Object>>() {{
        put("mysql", new HashMap<String, Object>() {{
            put("label", "MySQL");
            put("jdbcUrl", "jdbc:mysql://$[dbHost]:$[dbPort]/$[dbName]");
            put("jdbcDriverClass", "org.mariadb.jdbc.Driver");
            put("defaultValues", new HashMap<String, Object>() {{
                put("dbHost", "localhost");
                put("dbPort", "3306");
                put("dbName", "dbname");
            }});
        }});
        put("postgresql", new HashMap<String, Object>() {{
            put("label", "PostgreSQL");
            put("jdbcUrl", "jdbc:postgresql://$[dbHost]:$[dbPort]/$[dbName]");
            put("jdbcDriverClass", "org.postgresql.Driver");
            put("default", "true");
            put("defaultValues", new HashMap<String, Object>() {{
                put("dbHost", "localhost");
                put("dbPort", "5432");
                put("dbName", "dbname");
            }});
        }});
    }};

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        service.jdbcConnectionMap = jdbcConnectionMap;
    }

    public void refresh() {
        reset(jdbcDriverService);
    }

    @Test
    public void getJdbcDrivers() {
        when(jdbcDriverService.isRegistered("postgresql")).thenReturn(false);
        when(jdbcDriverService.isRegistered("mysql")).thenReturn(true);
        when(jdbcDriverService.getRegisteredDriverClassNames()).thenReturn(new HashSet<String>() {{
            add("org.mariadb.jdbc.Driver");
            add("org.postgresql.Driver");
        }});
        final List<JdbcDriverInfo> jdbcDrivers = service.getJdbcDrivers();
        assertNotNull(jdbcDrivers);
        assertEquals(jdbcDrivers.size(), jdbcConnectionMap.size());
        for (JdbcDriverInfo currentInfo : jdbcDrivers) {
            final Map<String, Object> currentInfoMap = jdbcConnectionMap.get(currentInfo.getName());
            assertNotNull(currentInfoMap);
            final String jdbcDriverClass = (String) currentInfoMap.get("jdbcDriverClass");
            assertEquals(currentInfo.getJdbcDriverClass(), jdbcDriverClass);
            final Boolean available = currentInfo.getAvailable();
            assertNotNull(available);
            assertEquals(available.booleanValue(), jdbcDriverService.isRegistered(jdbcDriverClass));
            assertEquals(currentInfo.isDefault(), currentInfoMap.get("default") != null ? Boolean.valueOf((String) currentInfoMap.get("default")) : null);
            assertEquals(currentInfo.getJdbcUrl(), currentInfoMap.get("jdbcUrl"));
            assertEquals(currentInfo.getLabel(), currentInfoMap.get("label"));
            final Map<String, String> defaultValuesMap = (Map<String, String>) currentInfoMap.get("defaultValues");
            final List<ClientProperty> defaultValues = currentInfo.getDefaultValues();
            assertNotNull(defaultValues);
            for (ClientProperty property : defaultValues) {
                assertTrue(defaultValuesMap.containsKey(property.getKey()));
                assertEquals(property.getValue(), defaultValuesMap.get(property.getKey()));
            }
        }
    }

    @Test
    public void getJdbcDrivers_notConfiguredDriverClass() {
        final String customDriverClass = "some.manually.added.Driver";
        when(jdbcDriverService.isRegistered(any(String.class))).thenReturn(true);
        when(jdbcDriverService.getRegisteredDriverClassNames()).thenReturn(new HashSet<String>() {{
            add("org.mariadb.jdbc.Driver");
            add("org.postgresql.Driver");
            add(customDriverClass);
        }});
        final List<JdbcDriverInfo> jdbcDrivers = service.getJdbcDrivers();
        assertNotNull(jdbcDrivers);
        assertEquals(jdbcDrivers.size(), 3);
        JdbcDriverInfo customDriver = null;
        for (JdbcDriverInfo currentInfo : jdbcDrivers) {
            if(customDriverClass.equals(currentInfo.getJdbcDriverClass())){
                customDriver = currentInfo;
                break;
            }
        }
        assertNotNull(customDriver);
        assertEquals(customDriver.getLabel(), customDriverClass);
        assertEquals(customDriver.getName(), customDriverClass);
    }
}
