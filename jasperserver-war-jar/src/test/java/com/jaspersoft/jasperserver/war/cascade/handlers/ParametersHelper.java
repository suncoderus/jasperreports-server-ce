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

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JasperReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import net.sf.jasperreports.engine.JRParameter;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This helper class is encapsulation for entities and arguments needed for createWrappers test, it holds:
 * - JRParameter
 * - InputControl
 * - ReportInputControlsInformationImpl
 *
 * - argument parameter values
 * - default parameter values
 * - request URL parameter values
 *
 * @author Anton Fomin
 * @version $Id:$
 */
public final class ParametersHelper {
    /* Cascade query controls controls */
    public static final String CASCADE_COUNTRY = "country";
    public static final String CASCADE_STATE = "state";
    public static final String CASCADE_ACCOUNT_TYPE = "accountType";
    public static final String CASCADE_INDUSTRY = "industry";
    public static final String CASCADE_NAME = "name";

    /* Number controls */
    public static final String NUMBER = "number";


    /* Date single value (non-query) controls */
    public static final String DATE = "date";
    public static final String DATE_TIME = "dateTime";
    public static final String TIME = "time";

    /* String single value (non-query) controls */
    public static final String STRING = "stringParameter";

    private Map<String, Mock<JRParameter>> parameters;
    private Map<String, String> controlLabels;
    private Map<String, InputControl> inputControls;
    private ReportInputControlsInformationImpl infos;
    private Map<String, Object> argumentParameterValues;
    private Map<String, Object> requestParameterValues;

    public ParametersHelper() {
        parameters = new LinkedHashMap<String, Mock<JRParameter>>();
        controlLabels = new LinkedHashMap<String, String>();
        inputControls = new LinkedHashMap<String, InputControl>();
        infos = new ReportInputControlsInformationImpl();
        argumentParameterValues = new HashMap<String, Object>();
        requestParameterValues = new HashMap<String, Object>();
    }

    public void addParameterAndControlInfo(String name, String label, byte controlType, Class clazz, Byte controlDataType, Object defaultValue, boolean mandatory) {
        parameters.put(name, createJRParameter(name, clazz, null));
        controlLabels.put(name, label);
        inputControls.put(name, createInputControl(name, label, mandatory, controlType, controlDataType, null));
        infos.setInputControlInformation(name, createCI(parameters.get(name).getMock(), label, defaultValue));
    }

    public void addParameterAndControlInfo(String name, String label, byte controlType, Class clazz, Byte controlDataType,
                                           Object defaultValue, boolean mandatory, String uri, String queryValueColumn) {
        addParameterAndControlInfo(name, label, controlType, clazz, controlDataType, defaultValue, mandatory);
        getInputControl(name).setURIString(uri);
        getInputControl(name).setQueryValueColumn(queryValueColumn);
    }

    public void addListOfValues(String controlName, Map<String, Object> values) {
        InputControl c = getInputControl(controlName);

        ListOfValues lov = new ListOfValuesImpl();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            ListOfValuesItem lovi = new ListOfValuesItemImpl();
            lovi.setLabel(entry.getKey());
            lovi.setValue(entry.getValue());
            lov.addValue(lovi);
        }
        c.setListOfValues(lov);
    }

    public JRParameter getParameter(String name) {
        return parameters.get(name).getMock();
    }

    public Mock<JRParameter> getParameterMock(String name) {
        return parameters.get(name);
    }

    public InputControl getInputControl(String name) {
        return inputControls.get(name);
    }

    public void setInputControlQuery(String name, String sql) {
        Mock<Query> queryMock = MockUnitils.createMock(Query.class);
        queryMock.returns(sql).getSql();
        getInputControl(name).setQuery(queryMock.getMock());
    }

    public String getInputControlUriString(String name) {
        return getInputControl(name).getURIString();
    }

    public ResourceReference getInputControlReference(String name) {
        return new ResourceReference(inputControls.get(name));
    }

    public List<ResourceReference> getInputControlReferences() {
        List<ResourceReference> list = new ArrayList<ResourceReference>();
        for (InputControl control : inputControls.values()) {
            list.add(new ResourceReference(control));
        }
        return list;
    }

    public List<InputControl> getInputControls() {
        List<InputControl> ic = new ArrayList<InputControl>();
        for (Map.Entry<String, InputControl> entry : inputControls.entrySet()) {
            ic.add(entry.getValue());
        }
        return ic;
    }

    public InputControl getInputControlByQueryValueColumn(String queryValueColumn) {
        for (InputControl control : inputControls.values()) {
            if (queryValueColumn.equals(control.getQueryValueColumn())) {
                return control;
            }
        }
        return null;
    }

    public String getInputControLabel(String name) {
        return controlLabels.get(name);
    }

    public ReportInputControlsInformationImpl getInputControlInfo() {
        return infos;
    }

    public Map<String, Class<?>> getParameterTypes() {
        Map<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
        for (String name : getInputControlInfo().getControlNames()) {
            parameterTypes.put(name, getInputControlInfo().getInputControlInformation(name).getValueType());
        }
        return parameterTypes;
    }

    public Map<String, Object> getDefaultParameterValues() {
        Map<String, Object> defaults = new HashMap<String, Object>();
        for (String name : infos.getControlNames()) {
            defaults.put(name, getDefaultParameterValue(name));
        }
        return defaults;
    }

    public Object getDefaultParameterValue(String name) {
        return infos.getInputControlInformation(name).getDefaultValue();
    }

    public void setParameterType(String name, Class type, Class nestedType) {
        parameters.put(name, createJRParameter(name, type, nestedType));
        ((JasperReportInputControlInformation) getInputControlInfo().getInputControlInformation(name))
                .setReportParameter(parameters.get(name).getMock());
    }

    public void setDefaultParameterValues(List<String> country, List<String> state, String accountType, String industry, String name) {
        setDefaultParameterValue(CASCADE_COUNTRY, country);
        setDefaultParameterValue(CASCADE_STATE, state);
        setDefaultParameterValue(CASCADE_ACCOUNT_TYPE, accountType);
        setDefaultParameterValue(CASCADE_INDUSTRY, industry);
        setDefaultParameterValue(CASCADE_NAME, name);
    }

    public void setDefaultParameterValue(String parameterName, Object defaultValue) {
        infos.getInputControlInformation(parameterName).setDefaultValue(defaultValue);
    }

    public void setArgumentCascadeParameterValues(List<String> country, List<String> state, String accountType, String industry, String name) {
        argumentParameterValues.put(CASCADE_COUNTRY, country);
        argumentParameterValues.put(CASCADE_STATE, state);
        argumentParameterValues.put(CASCADE_ACCOUNT_TYPE, accountType);
        argumentParameterValues.put(CASCADE_INDUSTRY, industry);
        argumentParameterValues.put(CASCADE_NAME, name);
    }

    public void setArgumentParameterValue(String name, Object value) {
        argumentParameterValues.put(name, value);
    }

    public Map<String, Object> getAllArgumentParameterValues() {
        return argumentParameterValues;
    }

    public Map<String, Object> getArgumentCascadeParameterValues() {
        return getArgumentParameterValues(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME);
    }

    public Map<String, String[]> getArgumentCascadeParameterValuesAsArray() {
        return getArgumentParameterValueAsArray(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME);
    }

    public List<String> getInputControlsList(){
        return list(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME);
    }

    public Set<String> getInputControlsSet(){
        return set(CASCADE_COUNTRY, CASCADE_STATE, CASCADE_ACCOUNT_TYPE, CASCADE_INDUSTRY, CASCADE_NAME);
    }

    public Map<String, Object> getArgumentParameterValues(String... names) {
        Map<String, Object> argumentsMap = new HashMap<String, Object>();
        for (String name : names) {
            argumentsMap.put(name, argumentParameterValues.get(name));
        }
        return argumentsMap;
    }

    public Map<String, String[]> getArgumentParameterValueAsArray(String... names) {
        Map<String, String[]> argumentsMap = new HashMap<String, String[]>();
        for (String name : names) {
            Object value = argumentParameterValues.get(name);
            if (value == null) {
                argumentsMap.put(name, null);
            } else if (value instanceof List) {
                List valueList = (List) value;
                String[] array = (String[]) valueList.toArray(new String[valueList.size()]);
                argumentsMap.put(name, array);
            } else {
                argumentsMap.put(name, new String[]{String.valueOf(value)});
            }
        }
        return argumentsMap;
    }

    public InputControl getInputControlByUri(String uri) {
        for (InputControl control : inputControls.values()) {
            if (uri.equals(control.getURI())) {
                return control;
            }
        }
        return null;
    }

    public void setRequestParameterValues(List<String> country, List<String> state, String accountType, String industry, String name) {
        requestParameterValues = new HashMap<String, Object>();
        requestParameterValues.put(CASCADE_COUNTRY, country);
        requestParameterValues.put(CASCADE_STATE, state);
        requestParameterValues.put(CASCADE_ACCOUNT_TYPE, accountType);
        requestParameterValues.put(CASCADE_INDUSTRY, industry);
        requestParameterValues.put(CASCADE_NAME, name);
    }

    public void setRequestParameterValue(String name, Object value) {
        requestParameterValues.put(name, value);
    }

    public Map<String, Object> getRequestParameterValues() {
        return requestParameterValues;
    }

    /**
     * See whether parameter with given name is a Collection
     * @param parameterName
     *              Parameter name
     * @return boolean
     *              Multi or Single value
     */
    public boolean isMulti(String parameterName) {
        return getParameter(parameterName).getValueClass().equals(Collection.class);
    }

    public void setMandatory(String name, boolean mandatory) {
        inputControls.get(name).setMandatory(mandatory);
    }

    public void setMandatoryCascade(boolean country, boolean state, boolean accountType, boolean industry, boolean name) {
        setMandatory(CASCADE_COUNTRY, country);
        setMandatory(CASCADE_STATE, state);
        setMandatory(CASCADE_ACCOUNT_TYPE, accountType);
        setMandatory(CASCADE_INDUSTRY, industry);
        setMandatory(CASCADE_NAME, name);
    }

    public void setQueryForControl(String name, QueryImpl query) {
        inputControls.get(name).setQuery(query);
    }

    /**
     * Create JRParameter entity
     * @param name
     *          Parameter name
     * @param valueClass
     *          Class of values: Collection, String or ohther type
     * @return
     *          Mocked JRParameter
     */
    private Mock<JRParameter> createJRParameter(String name, Class valueClass, Class nestedType) {
        Mock<JRParameter> pMock = MockUnitils.createMock(JRParameter.class);
        pMock.returns(name).getName();
        pMock.returns(valueClass != null ? valueClass.getName() : null).getValueClassName();
        pMock.returns(valueClass).getValueClass();
        pMock.returns(nestedType).getNestedType();
        pMock.returns(nestedType != null ? nestedType.getName() : null).getNestedTypeName();
        return pMock;
    }

    /**
     * Create InputControl entity
     * @param name
     *          Parameter name
     * @param label
     *          Control label
     * @param mandatory
     *          Indicates whether control is mandatory
     * @param type
     *          Type of InputControl, see InputControl interface for possible types
     * @param controlDataType
     *          Data type of control
     * @return
     *          Mocked InputControl
     */
    private InputControl createInputControl(String name, String label, boolean mandatory, byte type, Byte controlDataType, String queryValueColumn) {
        InputControl control = new InputControlImpl();
        control.setURIString("/" + name);
        control.setName(name);
        control.setLabel(label);
        control.setMandatory(mandatory);
        control.setInputControlType(type);
        control.setQueryValueColumn(queryValueColumn);
        control.addQueryVisibleColumn(queryValueColumn);

        // Control is query type
        if (type == InputControl.TYPE_MULTI_SELECT_QUERY
         || type == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX
         || type == InputControl.TYPE_SINGLE_SELECT_QUERY
         || type == InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO) {
            // Set Query later

        // Experimentally I have found that only TYPE_SINGLE_VALUE controls have dataType
        } else if (type == InputControl.TYPE_SINGLE_VALUE) {

            if (controlDataType != null) {
                DataType dataType = new DataTypeImpl();
                dataType.setDataTypeType(controlDataType);
                control.setDataType(dataType);
            }
        }
        return control;
    }

    public void setDataType(String controlName, Byte dataType) {
        if (dataType != null) {
            DataType type = new DataTypeImpl();
            type.setDataTypeType(dataType);
            getInputControl(controlName).setDataType(type);
        } else {
            getInputControl(controlName).setDataType((ResourceReference) null);
        }
    }

    public void setDataType(String controlName, DataType type) {
        if (type != null) {
            getInputControl(controlName).setDataType(type);
        } else {
            getInputControl(controlName).setDataType((ResourceReference) null);
        }
    }

    /**
     * Create JasperReportInputControlInformation entity
     * @param parameter
     *          JRParameter entity
     * @param label
     *          The same label as for InputControl
     * @param defaultValue
     *          Default parameter value, it's handy to set it in this place
     * @return
     *          JasperReportInputControlInformation instance
     */
    private JasperReportInputControlInformation createCI(JRParameter parameter, String label, Object defaultValue) {
        JasperReportInputControlInformation info = new JasperReportInputControlInformation();
        info.setReportParameter(parameter);
        info.setPromptLabel(label);
        info.setDefaultValue(defaultValue);
        return info;
    }

    /**
     * Simple wrapper for Arrays.asList()
     * @param values Array
     * @param <T> Any type
     * @return List
     */
    public static <T> List<T> list(T... values) {
        return new ArrayList<T>(Arrays.asList(values));
    }

    /**
     * Simple wrapper for Arrays.asList()
     * @param values Array of arrays
     * @param <T> Array of any type
     * @return List
     */
    public static <T> List<T[]> listOfArrays(T[]... values) {
        return new ArrayList<T[]>(Arrays.asList(values));
    }

    /**
     * Wrapper for aray
     * @param values Array
     * @param <T> Any type
     * @return Array
     */
    public static <T> T[] array(T... values) {
        return values;
    }

    /**
     * Simple wrapper for Set
     * @param values Array
     * @return List
     */
    public static <T> Set<T> set(T... values) {
        return new HashSet<T>(Arrays.asList(values));
    }

    /**
     * Return empty list
     * @return List
     */
    public static List<String> list() {
        return new ArrayList<String>();
    }

    /**
     * Returns list with one element, which is null.
     * @return List
     */
    public static List nullList() {
        List list = new ArrayList();
        list.add(null);
        return list;
    }

    public static Map.Entry<String, Object> entry(String key, Object value) {
        return new AbstractMap.SimpleEntry<java.lang.String, java.lang.Object>(key, value);
    }

    public static Map<String, Object> map(Map.Entry<String, Object>... entries) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry: entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static List<ListOfValuesItem> listOfValues(Object... values) {
        List<ListOfValuesItem> items = new ArrayList<ListOfValuesItem>();
        if (values != null) {
            for (int i = 0; i < values.length; i++){
                final ListOfValuesItemImpl listOfValuesItem = new ListOfValuesItemImpl();
                listOfValuesItem.setLabel("item" + i);
                listOfValuesItem.setValue(values[i]);
                items.add(listOfValuesItem);
            }
        }
        return items;
    }

    public static List<InputControlOption> listOfOptions(List<String> items, boolean isSelected) {
        List<InputControlOption> options = new ArrayList<InputControlOption>();
        for (String item : items) {
            final InputControlOption option = new InputControlOption();
            option.setLabel(item);
            option.setValue(item);
            option.setSelected(isSelected);
            options.add(option);
        }
        return options;
    }

}
