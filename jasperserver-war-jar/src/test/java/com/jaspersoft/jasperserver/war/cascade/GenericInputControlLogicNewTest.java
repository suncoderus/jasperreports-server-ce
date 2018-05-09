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

package com.jaspersoft.jasperserver.war.cascade;

import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.SrcSets;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.ReportUnitImpl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.war.action.EngineServiceCascadeTestQueryExecutor;
import com.jaspersoft.jasperserver.war.cascade.cache.ControlLogicCacheManager;
import com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler;
import com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper;
import com.jaspersoft.jasperserver.war.cascade.token.FilterResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets.checkAllSources;
import static com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets.newSet;
import static com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets.resolveAll;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.CASCADE_ACCOUNT_TYPE;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.CASCADE_COUNTRY;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.CASCADE_INDUSTRY;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.CASCADE_NAME;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.CASCADE_STATE;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.entry;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.list;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.map;
import static com.jaspersoft.jasperserver.war.cascade.handlers.ParametersHelper.set;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createEngineService;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createFilterResolver;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.createMessageSource;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.initFilterResolver;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.injectDependencyToPrivateField;
import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Tests for {@link com.jaspersoft.jasperserver.war.cascade.GenericInputControlLogic}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */

public class GenericInputControlLogicNewTest extends UnitilsJUnit4 {
    private static final Log log = LogFactory.getLog(GenericInputControlLogicNewTest.class);

    @TestedObject
    private GenericInputControlLogic genericInputControlLogic;

    @InjectInto(property = "cachedRepositoryService")
    private Mock<CachedRepositoryService> cachedRepositoryServiceMock;

    @InjectInto(property = "cachedEngineService")
    private Mock<CachedEngineService> cachedEngineServiceMock;

    @InjectInto(property = "controlLogicCacheManager")
    private Mock<ControlLogicCacheManager> controlLogicCacheManagerMock;

    @InjectInto(property = "filterResolver")
    protected Mock<FilterResolver> filterResolver;


    private ParametersHelper ph;

    @Test
    public void testSimpleMasterAndSlaveDependencies() throws Exception {
        Map<String, String> controls = new LinkedHashMap<String, String>();
        controls.put("A", null);
        controls.put("B", "A");
        controls.put("C", "A, B");
        createParametersForDependencyTest(controls);
        setupDependencyTest();

        // Actual call of method to test
        List<ReportInputControl> list = genericInputControlLogic.getInputControlsStructure("/testUri", set("A", "B", "C"));

        assertEquals(3, list.size());
        assertTrue(list.get(0).getMasterDependencies().isEmpty());
        assertReflectionEquals(Arrays.asList("B", "C"), list.get(0).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(Arrays.asList("A"), list.get(1).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(Arrays.asList("C"), list.get(1).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(Arrays.asList("A", "B"), list.get(2).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertTrue(list.get(2).getSlaveDependencies().isEmpty());
    }

    @Test
    public void testNotFullSetOfControlsMasterAndSlaveDependenciesOfExcludedInputControlIgnored() throws Exception {
        Map<String, String> controls = new LinkedHashMap<String, String>();
        controls.put("A", null);
        controls.put("B", "A");
        controls.put("C", "A, B");
        createParametersForDependencyTest(controls);
        setupDependencyTest();

        // Actual call of method to test
        List<ReportInputControl> list = genericInputControlLogic.getInputControlsStructure("/testUri", set("A", "C"));

        assertEquals(2, list.size());
        assertTrue(list.get(0).getMasterDependencies().isEmpty());
        assertReflectionEquals(Arrays.asList("C"), list.get(0).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(Arrays.asList("A"), list.get(1).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertTrue(list.get(1).getSlaveDependencies().isEmpty());
    }

    @Test
    public void testComplexMasterAndSlaveDependencies() throws Exception {
        Map<String, String> controls = new LinkedHashMap<String, String>();
        controls.put("A", null);
        controls.put("B", "A, F");
        controls.put("C", "A, B");
        controls.put("D", "A, B, C");
        controls.put("E", "C, F");
        controls.put("F", null);
        createParametersForDependencyTest(controls);
        setupDependencyTest();

        // Actual call of method to test
        List<ReportInputControl> list = genericInputControlLogic.getInputControlsStructure("/testUri", set("A", "B", "C", "D", "E", "F"));

        assertEquals(6, list.size());

        //A
        assertTrue(list.get(0).getMasterDependencies().isEmpty());
        assertReflectionEquals(Arrays.asList("B", "C", "D"), list.get(0).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);

        //B
        assertReflectionEquals(Arrays.asList("A", "F"),list.get(1).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(Arrays.asList("D", "C"), list.get(1).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);

        //C
        assertReflectionEquals(Arrays.asList("A", "B"),list.get(2).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertReflectionEquals(Arrays.asList("E", "D"), list.get(2).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);

        //D
        assertReflectionEquals(Arrays.asList("A", "B", "C"),list.get(3).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertTrue(list.get(3).getSlaveDependencies().isEmpty());

        //E
        assertReflectionEquals(Arrays.asList("C", "F"),list.get(4).getMasterDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
        assertTrue(list.get(4).getSlaveDependencies().isEmpty());

        //F
        assertTrue(list.get(5).getMasterDependencies().isEmpty());
        assertReflectionEquals(Arrays.asList("B", "E"), list.get(5).getSlaveDependencies(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    /*
    *  Should resolve default values for input controls which are not exist in a request parameters
    */
    @Test
    public void getTypedParametersWithNotCompleteOrMessyReportParams() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnitMock(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

//      fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("_flowId", new String[]{"er53234"});
        requestParameters.put("reportOptionURI", new String[]{"/ba/ba/ba"});
        requestParameters.put("customerUriParameter", new String[]{"cup32341"});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();
        Map<String, Object> defaultValues = ph.getDefaultParameterValues();

        Map actualResult  = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        assertReflectionEquals(defaultValues, actualResult);

        actualResult  = genericInputControlLogic.getTypedParameters(inputControls, null, infos, new ValidationErrorsImpl());

        assertReflectionEquals(defaultValues, actualResult);
    }

   /**
    *  Should resolve default values for input controls which are not exist in a request parameters
    */
    @Test
    public void getTypedParametersNothing() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnitMock(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

//      fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("_flowId", new String[]{"er53234"});
        requestParameters.put("reportOptionURI", new String[]{"/ba/ba/ba"});
        requestParameters.put("customerUriParameter", new String[]{"cup32341"});
        requestParameters.put(CASCADE_ACCOUNT_TYPE, new String[]{"~NOTHING~"});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();

        Map actualResult  = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list("USA")), entry(CASCADE_STATE, list("CA")),
                entry(CASCADE_INDUSTRY, "Engineering"), entry(CASCADE_NAME, "EngBureau, Ltd"));

        assertLenientEquals(expected, actualResult);
    }

   /**
    *  Ensure that empty array ([]) sent from UI for single select control doesn't treated as nothing selection.
    *  And instead it is treated like no raw value for this control sent from UI
    */
    @Test
    public void getTypedParametersUsingEmptyArrayForSingleSelectControl() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnitMock(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

//      fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put(CASCADE_ACCOUNT_TYPE, new String[]{});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();

        //Actuall call of test method
        Map actualResult  = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list("USA")), entry(CASCADE_STATE, list("CA")),
                entry(CASCADE_INDUSTRY, "Engineering"), entry(CASCADE_NAME, "EngBureau, Ltd"), entry(CASCADE_ACCOUNT_TYPE, null));

        assertLenientEquals(expected, actualResult);
    }

   /**
    *  Ensure that nothing substitution array ([~NOTHING~]) sent from UI for multiselect select control is threated same as
    *  empty array ([]) and means nothing selection.
    */
    @Test
    public void getTypedParametersUsingNothingSubstitusionForMultiSelectControl() throws CascadeResourceNotFoundException {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnitMock(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

//      fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put(CASCADE_COUNTRY, new String[]{InputControlHandler.NOTHING_SUBSTITUTION_VALUE});
        requestParameters.put(CASCADE_STATE, new String[]{});

        List<InputControl> inputControls = ph.getInputControls();
        ReportInputControlsInformationImpl infos = ph.getInputControlInfo();

        //Actuall call of test method
        Map actualResult  = genericInputControlLogic.getTypedParameters(inputControls, requestParameters, infos, new ValidationErrorsImpl());

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list()), entry(CASCADE_STATE, list()),
                entry(CASCADE_INDUSTRY, "Engineering"), entry(CASCADE_NAME, "EngBureau, Ltd"), entry(CASCADE_ACCOUNT_TYPE, null));

        assertLenientEquals(expected, actualResult);
    }

    /**
     *  Should resolve to NULL values for input controls which are not exist in a request parameters
     */
    @Test
    public void ensureNothingIsCompletedToNullInPublicMethod() throws Exception {

        createParameters();
        String reportUri = "/testUri";
        ReportUnit reportUnit = createReportUnitMock(reportUri);
        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        //fill request parameters
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("_flowId", new String[]{"er53234"});
        requestParameters.put("reportOptionURI", new String[]{"/ba/ba/ba"});
        requestParameters.put("customerUriParameter", new String[]{"cup32341"});
        requestParameters.put(CASCADE_ACCOUNT_TYPE, new String[]{InputControlHandler.NOTHING_SUBSTITUTION_VALUE});
        requestParameters.put(CASCADE_NAME, new String[]{InputControlHandler.NOTHING_SUBSTITUTION_VALUE});

        //Actual call of tested method
        Map<String, Object> actualResult  = genericInputControlLogic.getTypedParameters((InputControlsContainer)null, requestParameters, false);

        Map<String, Object> expected = map(entry(CASCADE_COUNTRY, list("USA")), entry(CASCADE_STATE, list("CA")),
                entry(CASCADE_INDUSTRY, "Engineering"),
                //NOTE - this is the difference beetween this test and "getTypedParametersNothing" test:
                //in previous test nothing means just removing key from typedParams, while here nothing is just replacement with null.
                entry(CASCADE_NAME, null),
                entry(CASCADE_ACCOUNT_TYPE, null));

        assertLenientEquals(expected, actualResult);
    }

    /**
     * Test cases:
     *
     * Report opened with default values
     *
     * Cascade fired when report run using parameters from url:
     *
     *      Not all parameters passed, those which are not present should be replaced by defaults and most relevant ones should be resolved.
     *
     * Cascade fired by change in control:
     *
     * Cascade fired by selecting report options:
     *
     */


    /**
     * Simplest case, all by default.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsSimplestCase() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("CA|true", "OR", "WA"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Engineering|true", "Machinery"));
        expectedOptions.add(list("EngBureau, Ltd|true", "BestEngineering, Inc", "SuperSoft, LLC"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Incoming values are not correct: country does not have specified state.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsWrongSlaveControlValue() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("CA"), "Distribution", "Communications", "B & X Rybolt Electronics, Inc");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("Zakarpatska|true", "Kyivska", "Kharkivska"));
        expectedOptions.add(list("Consulting", "Distribution|true", "Manufactoring"));
        expectedOptions.add(list("Machinery|true", "Telecommunications"));
        expectedOptions.add(list("B & X Rybolt Electronics, Inc|true", "H & G Van Antwerp Machinery Corp"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Ensure that query executor receives Null for single select when Nothing is selected.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsWithNothing() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("CA"), "~NOTHING~", "Communications", "B & X Rybolt Electronics, Inc");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, false, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        Map<String, Class<?>> countryTypes = ph.getParameterTypes();
        countryTypes.keySet().retainAll(list(CASCADE_COUNTRY));
        cachedEngineServiceMock.assertInvokedInSequence().executeQuery(null, null, null, null, null, null, countryTypes, CASCADE_STATE);
        Map<String, Class<?>> accountTypes = ph.getParameterTypes();
        accountTypes.keySet().retainAll(list(CASCADE_COUNTRY, CASCADE_STATE));
        Map<String, Object> expectedParametersForAccountQuery = map(entry(CASCADE_COUNTRY, list("Ukraine")), entry(CASCADE_STATE, list("Zakarpatska")));
        cachedEngineServiceMock.assertInvokedInSequence().executeQuery(null, null, null, null, null, expectedParametersForAccountQuery, accountTypes, CASCADE_ACCOUNT_TYPE);
        Map<String, Class<?>> industryTypes = ph.getParameterTypes();
        industryTypes.keySet().retainAll(list(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE));
        Map<String, Object> expectedParametersForIndustryQuery = map(entry(CASCADE_COUNTRY, list("Ukraine")), entry(CASCADE_STATE, list("Zakarpatska")), entry(CASCADE_ACCOUNT_TYPE, null));
        cachedEngineServiceMock.assertInvokedInSequence().executeQuery(null, null, null, null, null, expectedParametersForIndustryQuery, industryTypes, CASCADE_INDUSTRY);
        Map<String, Class<?>> nameTypes = ph.getParameterTypes();
        nameTypes.keySet().retainAll(list(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY));
        cachedEngineServiceMock.assertInvokedInSequence().executeQuery(null, null, null, null, null, null, nameTypes, CASCADE_NAME);

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("Zakarpatska|true", "Kyivska", "Kharkivska"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true", "Consulting", "Distribution", "Manufactoring"));
        expectedOptions.add(list());
        expectedOptions.add(list());

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Incoming values empty selection, mandatory.
     * Second control in cascade was clicked.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsEmptyNotMandatory() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(null, list(), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(list("USA"), list("CA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, false, true, true, true);

        List<InputControlState> actualStates =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentParameterValueAsArray(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME));

        List<InputControlState> expectedStates = prepareStatesForEmptySelectionTest();

        assertReflectionEquals(expectedStates, actualStates);
    }

    private List<InputControlState> prepareStatesForEmptySelectionTest() {
        List<InputControlState> expectedStates = new ArrayList<InputControlState>();

        InputControlState accountType = new InputControlState();
        List<InputControlOption> accountTypeOptions = new ArrayList<InputControlOption>();
        accountTypeOptions.add(new InputControlOption("Consulting", "Consulting", true));
        accountTypeOptions.add(new InputControlOption("Distribution", "Distribution"));
        accountTypeOptions.add(new InputControlOption("Manufactoring", "Manufactoring"));
        accountType.setOptions(accountTypeOptions);
        accountType.setId(CASCADE_ACCOUNT_TYPE);
        accountType.setUri(ph.getInputControlUriString(CASCADE_ACCOUNT_TYPE));
        expectedStates.add(accountType);

        InputControlState industry = new InputControlState();
        List<InputControlOption> industryOptions = new ArrayList<InputControlOption>();
        industryOptions.add(new InputControlOption("Engineering", "Engineering", true));
        industryOptions.add(new InputControlOption("Machinery", "Machinery"));
        industryOptions.add(new InputControlOption("Construction", "Construction"));
        industryOptions.add(new InputControlOption("Communications", "Communications"));
        industryOptions.add(new InputControlOption("Telecommunications", "Telecommunications"));
        industry.setOptions(industryOptions);
        industry.setId(CASCADE_INDUSTRY);
        industry.setUri(ph.getInputControlUriString(CASCADE_INDUSTRY));
        expectedStates.add(industry);

        InputControlState name = new InputControlState();
        List<InputControlOption> nameOptions = new ArrayList<InputControlOption>();
        nameOptions.add(new InputControlOption("EngBureau, Ltd", "EngBureau, Ltd", true));
        nameOptions.add(new InputControlOption("BestEngineering, Inc", "BestEngineering, Inc"));
        nameOptions.add(new InputControlOption("SuperSoft, LLC", "SuperSoft, LLC"));
        nameOptions.add(new InputControlOption("Detwiler-Biltoft Transportation Corp", "Detwiler-Biltoft Transportation Corp"));
        nameOptions.add(new InputControlOption("F & M Detwiler Transportation Corp", "F & M Detwiler Transportation Corp"));
        nameOptions.add(new InputControlOption("D & D Barrera Transportation, Ltd", "D & D Barrera Transportation, Ltd"));
        nameOptions.add(new InputControlOption("Infinity Communication Calls, Ltd", "Infinity Communication Calls, Ltd"));
        name.setOptions(nameOptions);
        name.setId(CASCADE_NAME);
        name.setUri(ph.getInputControlUriString(CASCADE_NAME));
        expectedStates.add(name);

        return expectedStates;
    }

    /**
     * Wrong state, no default, mandatory.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsNoDefaultMandatory() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Canada"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(null, null, null, null, null);

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("BC|true"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Construction|true", "Machinery", "Telecommunications"));
        expectedOptions.add(list("Q & Q Lopez Telecommunications Associates|true", "X & N Eichorn Construction Corp"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Wrong state, no default, mandatory. Onw of two selections is incorrect.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getUpdatedValuesForInputControlsNoDefaultMandatoryOneValueWrong() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("Ukraine"), list("CA", "Kyivska"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setDefaultParameterValues(null, null, null, null, null);

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri",
                        set(CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME),
                        ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("Zakarpatska", "Kyivska|true", "Kharkivska"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Construction", "Engineering|true", "Machinery", "Telecommunications"));
        expectedOptions.add(list("EngBureau, Ltd|true", "Infinity Communication Calls, Ltd", "SuperSoft, LLC"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Run report without request parameters. Default for state has two values.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsDefaultTwoValues() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(null, null, null, null, null);

        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(), ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA|true", "Ukraine", "Canada"));
        expectedOptions.add(list("CA|true", "OR", "WA|true"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Engineering|true", "Machinery", "Communications"));
        expectedOptions.add(list("EngBureau, Ltd|true", "BestEngineering, Inc", "SuperSoft, LLC",
                "D & D Barrera Transportation, Ltd", "Detwiler-Biltoft Transportation Corp", "F & M Detwiler Transportation Corp"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     *
     * Run report without request parameters. Three of five parameters passed through request.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsThreeRequestParameters() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("USA", "Ukraine"), list("Kyivska"), "Distribution", "Engineering", null);

        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, false, false, false);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(), ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA|true", "Ukraine|true", "Canada"));
        expectedOptions.add(list("CA", "OR", "WA", "Zakarpatska", "Kyivska|true", "Kharkivska"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE, "Consulting", "Distribution|true", "Manufactoring"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE, "Engineering|true", "Telecommunications"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true", "C & Y Difatta Machinery Partners", "Mills-Reed Electronics Associates"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     *
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsNotMandatory() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setArgumentCascadeParameterValues(list("USA", "Ukraine"), list("Kyivska"), null, "Machinery", null);

        ph.setDefaultParameterValues(list("USA"), list("CA", "WA"), "Consulting", "Engineering", "EngBureau, Ltd");

        ph.setMandatoryCascade(true, true, true, true, true);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(), ph.getArgumentCascadeParameterValuesAsArray());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA|true", "Ukraine|true", "Canada"));
        expectedOptions.add(list("CA", "OR", "WA", "Zakarpatska", "Kyivska|true", "Kharkivska"));
        expectedOptions.add(list("Consulting|true", "Distribution", "Manufactoring"));
        expectedOptions.add(list("Construction", "Engineering", "Machinery|true", "Telecommunications"));
        expectedOptions.add(list("SmartMachines, LLC|true", "Rage Against The Machines, LLC"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    /**
     * Run report without request parameters. All null.
     * @throws com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException
     * @throws com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException
     */
    @Test
    public void getInitialValuesForInputControlsAllNull() throws CascadeResourceNotFoundException, InputControlsValidationException {
        setupGetValuesTest();

        ph.setDefaultParameterValues(null, null, null, null, null);

        ph.setMandatoryCascade(false, false, false, false, false);

        List<InputControlState> envelopes =
                genericInputControlLogic.getValuesForInputControlsFromRawData("/testUri", ph.getInputControlsSet(),
                        Collections.<String, String[]>emptyMap());

        List<List<String>> expectedOptions = new ArrayList<List<String>>();
        expectedOptions.add(list("USA", "Ukraine", "Canada"));
        expectedOptions.add(list("CA", "OR", "WA", "Zakarpatska", "Kyivska", "Kharkivska", "BC"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true", "Consulting", "Distribution", "Manufactoring"));
        // TODO not sure how control logic and query executor should behave here.
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true"));
        expectedOptions.add(list(InputControlHandler.NOTHING_SUBSTITUTION_VALUE + "|true"));

        assertQueryEnvelopes(expectedOptions, envelopes);
    }

    private void assertQueryEnvelopes(List<List<String>> expectedEnvelopeOptions, List<InputControlState> actualEnvelopes) {
        assertEquals(expectedEnvelopeOptions.size(), actualEnvelopes.size());

        for (int i = 0; i < expectedEnvelopeOptions.size(); i++) {
            assertQueryEnvelope(expectedEnvelopeOptions.get(i), actualEnvelopes.get(i));
        }
    }

    private void assertQueryEnvelope(List<String> expectedOptions, InputControlState actualEnvelope) {
        List<String> actualOptions = convertOptionsList(actualEnvelope.getOptions());

        assertEquals(expectedOptions, actualOptions);
    }

    private List<String> convertOptionsList(List<InputControlOption> optionsList) {
        List<String> options = new ArrayList<String>();
        for (InputControlOption option : optionsList) {
            if (option.isSelected()) {
                options.add(option.getValue() + "|true");
            } else {
                options.add(option.getValue());
            }
        }
        return options;
    }

    private void setupGetValuesTest() throws CascadeResourceNotFoundException {
        createParameters();

        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnitMock(reportUri);

        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
        setUpFilterResolver();

        for (InputControl ic : ph.getInputControls()) {
            SrcSets.resolveAll(ic.getSources());
        }
    }

    private void setupGetValuesTestReorderedControls() throws CascadeResourceNotFoundException {
        createParameters();

        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnitMock(reportUri);

        setUpCachedEngineServiceReorderedControls();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
        setUpICHandlers();
        setUpFilterResolver();
    }

    private void setupGetValuesTestDirectCurcularDepedencies() throws CascadeResourceNotFoundException {
        createParametersWithDirectCircularDepedency();

        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnitMock(reportUri);

        setUpCachedEngineServiceReorderedControls();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
        setUpICHandlers();
    }

    private void setupGetValuesTestInDirectCurcularDepedencies() throws CascadeResourceNotFoundException {
        createParametersWithInDirectCircularDepedency();

        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnitMock(reportUri);

        setUpCachedEngineServiceReorderedControls();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
        setUpICHandlers();
    }

    private void setupDependencyTest() throws CascadeResourceNotFoundException {
        String reportUri = "/testUri";

        ReportUnit reportUnit = createReportUnitMock(reportUri);

        setUpCachedEngineService();
        setUpCachedRepositoryService(reportUnit);
        setUpICHandlers();
    }

    private ReportUnit createReportUnitMock(String uri) {
        Mock<ReportUnit> reportUnitMock = MockUnitils.createMock(ReportUnit.class);
        reportUnitMock.returns(uri).getURI();

        Mock<ResourceReference> dataSource = MockUnitils.createMock(ResourceReference.class);
        reportUnitMock.returns(dataSource.getMock()).getDataSource();
        return reportUnitMock.getMock();
    }

    /**
     * Create Parameters helper object
     */
    private void createParameters() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "test_report/country", "country");
        ph.setInputControlQuery(CASCADE_COUNTRY, "");

        ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "test_report/state", "state");
        ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

        ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "test_report/accountType", "account_type");
        ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).toString());

        ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "test_report/industry", "industry");
        ph.setInputControlQuery(CASCADE_INDUSTRY, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).toString());

        ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "test_report/name", "name");
        ph.setInputControlQuery(CASCADE_NAME, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).append(",").append(CASCADE_INDUSTRY).toString());
/*
        ph.addParameterAndControlInfo(TEXT_DATE, "Order Date", InputControl.TYPE_SINGLE_VALUE, Date.class, DataType.TYPE_DATE, new GregorianCalendar(2011, 6, 28).getTime(), true);
        ph.addParameterAndControlInfo(TEXT_DATE_TIME, "Order DateTime", InputControl.TYPE_SINGLE_VALUE, Date.class, DataType.TYPE_DATE_TIME, new GregorianCalendar(2011, 6, 28, 18, 22).getTime(), true);

        ph.addParameterAndControlInfo(STRING, "String Parameter", InputControl.TYPE_SINGLE_VALUE, String.class, DataType.TYPE_TEXT, "defaultValue", true);
*/
    }

    /**
     * Create Parameters helper object with circular deps
     */
    private void createParametersWithDirectCircularDepedency() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "test_report/country", "country");
        ph.setInputControlQuery(CASCADE_COUNTRY, CASCADE_STATE);

        ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "test_report/state", "state");
        ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

        ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "test_report/accountType", "account_type");
        ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).toString());

        ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "test_report/industry", "industry");
        ph.setInputControlQuery(CASCADE_INDUSTRY, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).toString());

        ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "test_report/name", "name");
        ph.setInputControlQuery(CASCADE_NAME, new StringBuilder(CASCADE_COUNTRY).append(",").append(CASCADE_STATE).append(",").append(CASCADE_ACCOUNT_TYPE).append(",").append(CASCADE_INDUSTRY).toString());
    }

    private void createParametersWithInDirectCircularDepedency() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo(CASCADE_COUNTRY, "Billing Country",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("USA"), true, "test_report/country", "country");
        ph.setInputControlQuery(CASCADE_COUNTRY, CASCADE_NAME);

        ph.addParameterAndControlInfo(CASCADE_STATE, "Billing State",
                InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, list("CA"), true, "test_report/state", "state");
        ph.setInputControlQuery(CASCADE_STATE, CASCADE_COUNTRY);

        ph.addParameterAndControlInfo(CASCADE_ACCOUNT_TYPE, "Account Type",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Consulting", true, "test_report/accountType", "account_type");
        ph.setInputControlQuery(CASCADE_ACCOUNT_TYPE, CASCADE_STATE);

        ph.addParameterAndControlInfo(CASCADE_INDUSTRY, "Industry",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "Engineering", true, "test_report/industry", "industry");
        ph.setInputControlQuery(CASCADE_INDUSTRY, CASCADE_ACCOUNT_TYPE);

        ph.addParameterAndControlInfo(CASCADE_NAME, "Company Name",
                InputControl.TYPE_SINGLE_SELECT_QUERY, String.class, null, "EngBureau, Ltd", true, "test_report/name", "name");
        ph.setInputControlQuery(CASCADE_NAME, CASCADE_INDUSTRY);
    }

    private void createParametersForDependencyTest(Map<String, String> nameAndQuery) {
        ph = new ParametersHelper();
        for (Map.Entry<String, String> entry : nameAndQuery.entrySet()) {
            ph.addParameterAndControlInfo(entry.getKey(), entry.getKey(),
                    InputControl.TYPE_MULTI_SELECT_QUERY, Collection.class, null, null, false, "/" + entry.getKey(), entry.getKey());
            ph.setInputControlQuery(entry.getKey(), entry.getValue());
        }
    }

    private void setUpCachedEngineService() throws CascadeResourceNotFoundException {
        cachedEngineServiceMock.resetBehavior();
        cachedEngineServiceMock.returns(ph.getInputControlInfo()).getReportInputControlsInformation(null, null, null);
        cachedEngineServiceMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                InputControl control = ph.getInputControlByQueryValueColumn((String) args.get(2));
                return EngineServiceCascadeTestQueryExecutor.executeOrderedMap(control.getName(), (Map<String, Object>) args.get(5));
            }
        }).executeQuery(null, null, null, null, null, null, null, null);
        cachedEngineServiceMock.returns(ph.getInputControls()).getInputControls(null);
    }

    private void setUpCachedEngineServiceReorderedControls() throws CascadeResourceNotFoundException {
        cachedEngineServiceMock.resetBehavior();
        cachedEngineServiceMock.returns(ph.getInputControlInfo()).getReportInputControlsInformation(null, null, null);
        cachedEngineServiceMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                InputControl control = ph.getInputControlByQueryValueColumn((String) args.get(2));
                return EngineServiceCascadeTestQueryExecutor.executeOrderedMap(control.getName(), (Map<String, Object>) args.get(5));
            }
        }).executeQuery(null, null, null, null, null, null, null, null);
        List<InputControl> controls = ph.getInputControls();
        Collections.shuffle(controls);
        cachedEngineServiceMock.returns(controls).getInputControls(null);
    }

    private void setUpCachedRepositoryService(final ReportUnit reportUnit) throws CascadeResourceNotFoundException {
        cachedRepositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                String uri = (String) args.get(1);
                if (uri.equals(reportUnit.getURI())) {
                    return reportUnit;
                } else {
                    return ph.getInputControlByUri(uri);
                }
            }
        }).getResource(null, (String) null);
        cachedRepositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                List<Object> args = proxyInvocation.getArguments();
                if (Query.class.isAssignableFrom((Class) args.get(0))) {
                    return ((ResourceReference) args.get(1)).getLocalResource();
                } else {
                    return null;
                }
            }
        }).getResource(null, (ResourceReference) null);
    }

    private void setUpFilterResolver() {
        initFilterResolver(filterResolver);
    }

    private void setUpICHandlers() throws CascadeResourceNotFoundException {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        mockedServices.put("cachedRepositoryService", cachedRepositoryServiceMock.getMock());
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("cachedEngineService", cachedEngineServiceMock.getMock());
        mockedServices.put("cachedEngine", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/war/cascade/applicationContext-cascade-test.xml");

        injectDependencyToPrivateField(genericInputControlLogic, "inputControlTypeConfiguration", context.getBean("inputControlTypeNewConfiguration"));
    }

}
