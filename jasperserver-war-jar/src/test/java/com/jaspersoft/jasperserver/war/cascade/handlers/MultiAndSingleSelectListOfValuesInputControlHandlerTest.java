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
import com.jaspersoft.jasperserver.war.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createEngineService;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createFilterResolver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MultiAndSingleSelectListOfValuesInputControlHandlerTest extends UnitilsJUnit4 {
    private ApplicationContext context;

    @Before
    public void setupApplicationContext() {
        final HashMap<String, Object> mockedServices = new HashMap<String, Object>();
        Mock<CachedRepositoryService> cachedRepositoryService = MockUnitils.createMock(CachedRepositoryService.class);
        mockedServices.put("cachedRepositoryService", cachedRepositoryService.getMock());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("cachedEngineService", MockUnitils.createMock(CachedEngineService.class).getMock());
        mockedServices.put("engineService", createEngineService());
        context = CascadeTestHelper.setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/war/cascade/handlers/multiAndSingleSelectListOfValuesInputControlHandlerTest-config.xml");
    }

    /**
     * Test target: MultiSelectListInputControlHandler.getState()
     * Case: List of values contains "~NULL~" and input parameters contains list with the only null element.
     * Expected result: Null substitution option is present and it is the only selected option
     *
     * @throws CascadeResourceNotFoundException
     */
    @Test
    public void getStateMultiSelectInputNullListOfValuesNullSelected() throws CascadeResourceNotFoundException {
        final InputControl inputControl = context.getBean("shipreg", InputControl.class);
        final ReportInputControlInformation info = context.getBean("shipreg_controlInfo", ReportInputControlInformation.class);
        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>) context.getBean("inputControlTypeNewConfiguration");
        final Map<String, Class<?>> parameterTypes = (Map<String, Class<?>>) context.getBean("parameterTypes");
        final InputControlHandler handler = (InputControlHandler) config.get("6").get("handler");
        Map<String, Object> inputParameters = new HashMap<String, Object>();
        final List<Object> list = new ArrayList<Object>();
        list.add(null);
        inputParameters.put("shipreg", list);
        final InputControlState state = handler.getState(inputControl, null, inputParameters, parameterTypes, info);
        final List<InputControlOption> options = state.getOptions();
        final List<Object> shipreg = (List<Object>) inputParameters.get("shipreg");
        assertTrue(shipreg != null && shipreg.size() == 1 && shipreg.get(0) == null);
        assertFalse(options == null || options.isEmpty());
        InputControlOption nullSubstituteOption = null;
        for (InputControlOption currentOption : options)
            if (InputControlHandler.NULL_SUBSTITUTION_VALUE.equals(currentOption.getValue())){
                assertTrue(currentOption.isSelected());
                nullSubstituteOption = currentOption;
            }
            else
                assertFalse(currentOption.isSelected());
        assertFalse(nullSubstituteOption == null);
    }

    /**
     * Test target: MultiSelectListInputControlHandler.getState()
     * Case: List of values contains "~NULL~" and input parameters contains empty list (empty list means nothing selected).
     * Expected result: no one option of state is selected
     *
     * @throws CascadeResourceNotFoundException
     */
    @Test
    public void getStateMultiSelectInputNullListOfValuesNothingSelected() throws CascadeResourceNotFoundException {
        final InputControl inputControl = context.getBean("shipreg", InputControl.class);
        final ReportInputControlInformation info = context.getBean("shipreg_controlInfo", ReportInputControlInformation.class);
        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>) context.getBean("inputControlTypeNewConfiguration");
        final Map<String, Class<?>> parameterTypes = (Map<String, Class<?>>) context.getBean("parameterTypes");
        final InputControlHandler handler = (InputControlHandler) config.get("6").get("handler");
        Map<String, Object> inputParameters = new HashMap<String, Object>();
        final List<Object> list = new ArrayList<Object>();
        inputParameters.put("shipreg", list);
        final InputControlState state = handler.getState(inputControl, null, inputParameters, parameterTypes, info);
        final List<InputControlOption> options = state.getOptions();
        final List<Object> shipreg = (List<Object>) inputParameters.get("shipreg");
        assertTrue(shipreg != null && shipreg.isEmpty());
        assertFalse(options == null || options.isEmpty());
        InputControlOption nullSubstituteOption = null;
        for (InputControlOption currentOption : options){
            if (InputControlHandler.NULL_SUBSTITUTION_VALUE.equals(currentOption.getValue())){
                nullSubstituteOption = currentOption;
            }
            assertFalse(currentOption.isSelected());
        }
        assertFalse(nullSubstituteOption == null);
    }

    /**
     * Test target: ListOfValuesInputControlHandler.getState()
     * Case: List of values contains "~NULL~" and input parameters contains null.
     * Expected result: Null substitution option is present and it is the only selected option,
     *                  nothing substitution option is also present and not selected.
     *
     * @throws CascadeResourceNotFoundException
     */
    @Test
    public void getStateSingleSelectInputNullListOfValuesNullSelectedNothingUnselected() throws CascadeResourceNotFoundException {
        final InputControl inputControl = context.getBean("shipregSingleSelect", InputControl.class);
        final ReportInputControlInformation info = context.getBean("shipreg_controlInfo", ReportInputControlInformation.class);
        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>) context.getBean("inputControlTypeNewConfiguration");
        final Map<String, Class<?>> parameterTypes = (Map<String, Class<?>>) context.getBean("parameterTypes");
        final InputControlHandler handler = (InputControlHandler) config.get("3").get("handler");
        Map<String, Object> inputParameters = new HashMap<String, Object>();
        inputParameters.put("shipreg", null);
        final InputControlState state = handler.getState(inputControl, null, inputParameters, parameterTypes, info);
        final List<InputControlOption> options = state.getOptions();
        assertTrue(inputParameters.get("shipreg") == null);
        assertFalse(options == null || options.isEmpty());
        InputControlOption nullSubstituteOption = null;
        InputControlOption nothingSubstitutionOption = null;
        for (InputControlOption currentOption : options)
            if (InputControlHandler.NOTHING_SUBSTITUTION_VALUE.equals(currentOption.getValue())){
                assertFalse(currentOption.isSelected());
                nothingSubstitutionOption = currentOption;
            }else if(InputControlHandler.NULL_SUBSTITUTION_VALUE.equals(currentOption.getValue())){
                assertTrue(currentOption.isSelected());
                nullSubstituteOption = currentOption;
            }
            else
                assertFalse(currentOption.isSelected());
        assertFalse(nullSubstituteOption == null);
        assertFalse(nothingSubstitutionOption == null);
    }

    /**
     * Test target: ListOfValuesInputControlHandler.getState()
     * Case: List of values contains "~NULL~" and input parameters doesn't contain value for this input control (i.e. nothing selected).
     * Expected result: Nothing substitution option is the only selected option, null substitution option is present and not selected.
     *
     * @throws CascadeResourceNotFoundException
     */
    @Test
    public void getStateSingleSelectInputNullListOfValuesNothingSelected() throws CascadeResourceNotFoundException {
        final InputControl inputControl = context.getBean("shipregSingleSelect", InputControl.class);
        final ReportInputControlInformation info = context.getBean("shipreg_controlInfo", ReportInputControlInformation.class);
        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>) context.getBean("inputControlTypeNewConfiguration");
        final Map<String, Class<?>> parameterTypes = (Map<String, Class<?>>) context.getBean("parameterTypes");
        final InputControlHandler handler = (InputControlHandler) config.get("3").get("handler");
        Map<String, Object> inputParameters = new HashMap<String, Object>();
        final InputControlState state = handler.getState(inputControl, null, inputParameters, parameterTypes, info);
        final List<InputControlOption> options = state.getOptions();
        assertTrue(inputParameters.get("shipreg") == null);
        assertFalse(options == null || options.isEmpty());
        InputControlOption nullSubstituteOption = null;
        InputControlOption nothingSubstitutionOption = null;
        for (InputControlOption currentOption : options)
            if (InputControlHandler.NOTHING_SUBSTITUTION_VALUE.equals(currentOption.getValue())){
                assertTrue(currentOption.isSelected());
                nothingSubstitutionOption = currentOption;
            }else if(InputControlHandler.NULL_SUBSTITUTION_VALUE.equals(currentOption.getValue())){
                assertFalse(currentOption.isSelected());
                nullSubstituteOption = currentOption;
            }
            else
                assertFalse(currentOption.isSelected());
        assertFalse(nullSubstituteOption == null);
        assertFalse(nothingSubstitutionOption == null);
    }
}
