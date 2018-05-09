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

package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.*;

public abstract class InputControlHandlerBaseTest extends UnitilsJUnit4 {

    private ApplicationContext applicationContextForTestCases;

    protected String getTestCasesConfigContextPath() {
        return "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/sample-context-for-handlerTests.xml";
    }

    protected String getHandlersConfigContextPath() {
        return "classpath:/com/jaspersoft/jasperserver/war/cascade/applicationContext-cascade-test.xml";
    }

    protected void setApplicationContextForTestCase(String path) {
        applicationContextForTestCases = setUpApplicationContext(null, path);
    }

    private ApplicationContext getApplicationContextForTestCases() {
        if (applicationContextForTestCases == null) {
            applicationContextForTestCases = setUpApplicationContext(null, getTestCasesConfigContextPath());
        }

        return applicationContextForTestCases;
    }

    @SuppressWarnings("unchecked")
    protected InputControlHandler getHandler(String testCaseName, byte handlerType) throws Exception {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        mockedServices.put("cachedRepositoryService", setUpCachedRepositoryService(getApplicationContextForTestCases(), testCaseName));
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("cachedEngineService", setUpCachedEngineService(getApplicationContextForTestCases(), testCaseName));
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, getHandlersConfigContextPath());

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>) context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get(String.valueOf(handlerType)).get("handler");
    }

    protected InputControl getControl(String name, String testCaseName) {
        return getInputControl(name, testCaseName, getApplicationContextForTestCases());
    }

    protected ReportInputControlInformation getControlInfo(String name, String testCaseName) {
        return getInputControlInfo(name, testCaseName, getApplicationContextForTestCases());
    }

    protected Map<String, Class<?>> getParameterTypes(String testCaseName) {
        final Map<String, Object> testCaseMap = (Map<String, Object>) getApplicationContextForTestCases().getBean(testCaseName);
        return (Map<String, Class<?>>) testCaseMap.get(PARAMETER_TYPES);
    }

    protected Map<String, Object> getTypedParamsMap(String testCaseName) {
        ApplicationContext applicationContext = getApplicationContextForTestCases();
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        return (Map<String, Object>)testCaseMap.get(TYPED_PARAMS_MAP_KEY_NAME);
    }

    protected String[] getRawDataArray(String testCaseName) {
        ApplicationContext applicationContext = getApplicationContextForTestCases();
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        String rawData = (String) testCaseMap.get(PAW_PARAMS_ARRAY_KEY_NAME);
        return rawData != null ? rawData.split(",") : null;
    }

    protected void assertControlState(String testCaseName, InputControlState actualResult) {
        ApplicationContext applicationContext = getApplicationContextForTestCases();
        assertResult(testCaseName, actualResult, applicationContext);
    }

    protected void assertTypedValue(String testCaseName, Object actualResult) {
        ApplicationContext applicationContext = getApplicationContextForTestCases();
        assertResult(testCaseName, actualResult, applicationContext);
    }
}
