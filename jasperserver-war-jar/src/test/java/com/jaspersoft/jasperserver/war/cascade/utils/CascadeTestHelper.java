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

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.war.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.token.FilterResolver;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Helper class for cascade tests
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class CascadeTestHelper {
    public static final String QUERY_EXECUTOR_KEY_NAME = "queryExecutor";
    public static final String QUERY_EXECUTOR_KEY_GENERATOR_KEY_NAME = "keyGenerator";
    public static final String CONTROLS_KEY_NAME = "controls";
    public static final String PARAMETER_TYPES = "parameterTypes";
    public static final String INPUT_CONTROL_KEY_NAME = "inputControl";
    public static final String REPORT_INPUT_CONTROL_INFORMATION_KEY_NAME = "controlsInformation";
    public static final String TYPED_PARAMS_MAP_KEY_NAME = "typedParametersMap";
    public static final String PAW_PARAMS_ARRAY_KEY_NAME = "rawParametersArray";
    public static final String EXPECTED_RESULT_KEY_NAME = "expectedResult";

    public static void injectDependencyToPrivateField(Object holder, String name, Object value) {
        Field field  = ReflectionUtils.getFieldWithName(holder.getClass(), name, false);
        ReflectionUtils.setFieldValue(holder, field, value);
    }

    /**
     * Define mock for filterresolver so we can use following string instead of correct SQL:
     * "Country_multi_select, Cascading_state_multi_select" and mock ordering cascade dependency.
     */
    public static Mock<FilterResolver> initFilterResolver(Mock<FilterResolver> filterResolverMock) {
        filterResolverMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                String params = (String) proxyInvocation.getArguments().get(0);
                return (params == null || params.isEmpty()) ? Collections.emptySet() : new HashSet<String>(Arrays.asList(params.split("[,;\\s]+")));
            }
        }).getParameterNames(null, null);
        filterResolverMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                // We know the signature of resolveCascadingOrder method
                @SuppressWarnings("unchecked")
                HashMap<String, Set<String>> unorderedMasterDependencies = (HashMap<String, Set<String>>) proxyInvocation.getArguments().get(0);
                return new LinkedHashSet<String>(unorderedMasterDependencies.keySet());
            }
        }).resolveCascadingOrder(null);
        return filterResolverMock;
    }

    public static FilterResolver createFilterResolver() {
        return initFilterResolver(MockUnitils.createMock(FilterResolver.class)).getMock();
    }

    public static CalendarFormatProvider createCalendarFormatProvider() {
        Mock<CalendarFormatProvider> calendarFormatProviderMock = MockUnitils.createMock(CalendarFormatProvider.class);
        calendarFormatProviderMock.returns("%m-%d-%Y").getCalendarDatePattern();
        calendarFormatProviderMock.returns("%m-%d-%Y %H:%M").getCalendarDatetimePattern();
        calendarFormatProviderMock.returns("%H:%M").getCalendarTimePattern();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendarFormatProviderMock.returns(simpleDateFormat).getDateFormat();

        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        calendarFormatProviderMock.returns(simpleDateTimeFormat).getDatetimeFormat();

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        calendarFormatProviderMock.returns(simpleTimeFormat).getTimeFormat();

        return calendarFormatProviderMock.getMock();
    }

    public static MessageSource createMessageSource() {
        return new MessageSource() {
            @Override
            public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
                return code;
            }

            @Override
            public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
                return code;
            }

            @Override
            public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
                return resolvable.getDefaultMessage();
            }
        };
    }

    public static ApplicationContext setUpApplicationContext(Map<String, Object> mockedServices, String... contextPath) {
        GenericApplicationContext parentApplicationContext = new GenericApplicationContext();

        if (mockedServices != null && !mockedServices.isEmpty()) {
            if(!mockedServices.containsKey("dataTypeResourceConverter")){
                final Mock<ToClientConverter> mock = MockUnitils.createMock(ToClientConverter.class);
                mockedServices.put("dataTypeResourceConverter", mock.getMock());
            }
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) parentApplicationContext.getBeanFactory();
            for (Map.Entry<String, Object> entry: mockedServices.entrySet()) {
                beanFactory.registerSingleton(entry.getKey(), entry.getValue());
            }
            parentApplicationContext.refresh();

            return new ClassPathXmlApplicationContext(contextPath, true, parentApplicationContext);
        } else {
            return new ClassPathXmlApplicationContext(contextPath, true);
        }
    }

    /**
     * Set up CachedEngineService with properly mocked methods "executeQuery" and "getReportInputControlsInformation".
     * It get its values from configured sprin g context using testCaseName
     * @param cachedEngineServiceMock
     * @param applicationContext
     * @param testCaseName
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void setUpCachedEngineService(Mock<CachedEngineService> cachedEngineServiceMock, final ApplicationContext applicationContext, final String testCaseName) throws Exception {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        final Map<String, Map<String, OrderedMap>> queryExecutorMap = (Map<String, Map<String, OrderedMap>>)testCaseMap.get(QUERY_EXECUTOR_KEY_NAME);
        final QueryExecutorTestCaseKeyGenerator keyGenerator = (QueryExecutorTestCaseKeyGenerator)testCaseMap.get(QUERY_EXECUTOR_KEY_GENERATOR_KEY_NAME);
        Map<String, Map<String, Object>> controls = (Map<String, Map<String, Object>>)testCaseMap.get(CONTROLS_KEY_NAME);

        cachedEngineServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                String controlName = (String)proxyInvocation.getArguments().get(7);
                Map<String, Object> paramValues = (Map<String, Object>)proxyInvocation.getArguments().get(5);

                Map<String, OrderedMap> queryResultsMapForControlName = queryExecutorMap.get(controlName);
                String key = keyGenerator.generateKey(controlName, paramValues);
                OrderedMap result = queryResultsMapForControlName.get(key);
                if (result == null) {
                    result = new LinkedMap(1);
                }

                return result;
            }
        }).executeQuery(null, null, null, null, null, null, null, null);

        ReportInputControlsInformationImpl rici = new ReportInputControlsInformationImpl();
        for (Map.Entry<String, Map<String, Object>> entry: controls.entrySet()) {
            String controlName = entry.getKey();
            rici.setInputControlInformation(controlName, (ReportInputControlInformation)entry.getValue().get(REPORT_INPUT_CONTROL_INFORMATION_KEY_NAME));
        }

        cachedEngineServiceMock.returns(rici).getReportInputControlsInformation(null, null, null);

        List<InputControl> inputControls = new ArrayList<InputControl>();
        for (Map.Entry<String, Map<String, Object>> entry: controls.entrySet()) {
            inputControls.add((InputControl) entry.getValue().get("inputControl"));
        }

        cachedEngineServiceMock.returns(inputControls).getInputControls(null);
    }

    public static CachedEngineService setUpCachedEngineService(ApplicationContext applicationContext, String testCaseName) throws Exception {
        Mock<CachedEngineService> cachedEngineServiceMock = MockUnitils.createMock(CachedEngineService.class);
        setUpCachedEngineService(cachedEngineServiceMock, applicationContext, testCaseName);
        return cachedEngineServiceMock.getMock();
    }

    public static EngineService createEngineService() {
        Mock<EngineService> EngineServiceMock = MockUnitils.createMock(EngineService.class);
        return EngineServiceMock.getMock();
    }

    public static void setUpCachedRepositoryService(Mock<CachedRepositoryService> cachedRepositoryServiceMock, ApplicationContext applicationContext, String testCaseName) throws Exception {
        cachedRepositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                /*List<Object> args = proxyInvocation.getArguments();
                String uri = (String) args.get(1);
                if (uri.equals(reportUnit.getURI())) {
                    return reportUnit;
                } else {
                    return ph.getInputControlByUri(uri);
                }*/

                return null;
            }
        }).getResource(null, (String) null);

        cachedRepositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                if (Resource.class.isAssignableFrom((Class) args.get(0))) {
                    return args.get(1) != null ? ((ResourceReference) args.get(1)).getLocalResource() : null;
                } else {
                    return null;
                }
            }
        }).getResource(null, (ResourceReference) null);
    }

    public static CachedRepositoryService setUpCachedRepositoryService(ApplicationContext applicationContext, String testCaseName) throws Exception {
        Mock<CachedRepositoryService> cachedRepositoryServiceMock = MockUnitils.createMock(CachedRepositoryService.class);
        setUpCachedRepositoryService(cachedRepositoryServiceMock, applicationContext, testCaseName);
        return cachedRepositoryServiceMock.getMock();
    }

    @SuppressWarnings("unchecked")
    public static InputControl getInputControl(String name, String testCaseName, ApplicationContext applicationContext) {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        Map<String, Map<String, Object>> controls = (Map<String, Map<String, Object>>)testCaseMap.get(CONTROLS_KEY_NAME);
        return (InputControl)controls.get(name).get(INPUT_CONTROL_KEY_NAME);
    }

    @SuppressWarnings("unchecked")
    public static ReportInputControlInformation getInputControlInfo(String name, String testCaseName, ApplicationContext applicationContext) {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        Map<String, Map<String, Object>> controls = (Map<String, Map<String, Object>>)testCaseMap.get(CONTROLS_KEY_NAME);
        return (ReportInputControlInformation)controls.get(name).get(REPORT_INPUT_CONTROL_INFORMATION_KEY_NAME);
    }

    public static void assertResult(String testCaseName, Object actualResult, ApplicationContext applicationContext) {
        final Map<String, Object> testCaseMap = (Map<String, Object>)applicationContext.getBean(testCaseName);
        Object expectedResult = testCaseMap.get(EXPECTED_RESULT_KEY_NAME);

        assertReflectionEquals(expectedResult, actualResult);
    }
}
