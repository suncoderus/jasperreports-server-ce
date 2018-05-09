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

package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.common.properties.Log4jSettingsService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link LogSettingsDiagnosticService}
 *
 * @author vsabadosh
 */
public class LogSettingsDiagnosticServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private  LogSettingsDiagnosticService logSettingsDiagnosticService;

    @InjectInto(property = "propertiesManagementService")
    private Mock<PropertiesManagementService> propertiesManagementService;

    @InjectInto(property = "messageSource")
    private Mock<MessageSource> messageSource;

    @InjectInto(property = "log4jSettingsService")
    private Mock<Log4jSettingsService> log4jSettingsService;


    private Map<String, String> loggers = new HashMap <String, String>();

    @Before
    public void setUp() throws Exception {
        loggers.put("Log1", "Value1");
        loggers.put("Log2", "Value2");
        loggers.put("Log3", "Value3");
        Set<Map.Entry<String,String>> globalPropertiesList = new HashSet<Map.Entry<String,String>>();
        globalPropertiesList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "log4j.Log1";
            }

            @Override
            public String getValue() {
                return "Value1Updated";
            }

            @Override
            public String setValue(String s) {
                return "Value1Updated";
            }
        });
        globalPropertiesList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "log4j.Log4";
            }

            @Override
            public String getValue() {
                return "NewValue";
            }

            @Override
            public String setValue(String s) {
                return "NewValue";
            }
        });
        globalPropertiesList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "NotLog4JProperty";
            }

            @Override
            public String getValue() {
                return "";
            }

            @Override
            public String setValue(String s) {
                return "";
            }
        });
        propertiesManagementService.returns(globalPropertiesList).entrySet();
        log4jSettingsService.returns(loggers).getLoggers();

    }

    @Test
    public void testGetAllEventsNotModifiable() throws Exception {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = logSettingsDiagnosticService.getDiagnosticData();
        //Test total size of diagnostic attributes
        assertEquals(2, resultDiagnosticData.size());
        assertEquals(4, ((Map)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.LOG_SETTINGS, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Value3", ((Map)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.LOG_SETTINGS, null, null)).getDiagnosticAttributeValue()).get("Log3"));
        assertEquals("Value1Updated", ((Map)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.LOG_SETTINGS, null, null)).getDiagnosticAttributeValue()).get("Log1"));
    }

}
