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

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationException;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;

import java.util.Map;
import java.util.Set;

/**
 * This interface describes API for handling basic operations with single input control
 * such as building presentation object for input control, fill it with data from data source,
 * and provide serialization of value to string.
 *
 * @see {@link com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService}
 * @see {@link com.jaspersoft.jasperserver.war.cascade.ControlLogic}
 *
 * @version $Id$
 */
@JasperServerAPI
public interface InputControlHandler {

    /**
     * Value substitution for null values passed from or to UI for controls of string type.
     *
     * @see {@link com.jaspersoft.jasperserver.war.cascade.handlers.converters.StringDataConverter}
     */
    public static final String NULL_SUBSTITUTION_VALUE = "~NULL~";

    /**
     * Label which will be displayed for null value on UI.
     *
     * @see {@link QueryValuesLoader}
     */
    public static final String NULL_SUBSTITUTION_LABEL = "[Null]";

    /**
     * Value substitution for empty selection in multiselect controls and for empty value in single-value controls
     * which sent from or to UI.
     *
     * @see {@link #isNothingSelected(String[])}
     * @see {@link BasicInputControlHandler#fillStateValue}
     */
    public static final String NOTHING_SUBSTITUTION_VALUE = "~NOTHING~";

    /**
     * Label substitution for empty selection in multiselect controls and for empty value in single-value controls
     * which sent from or to UI.
     *
     * @see {@link BasicInputControlHandler#fillStateValue}
     */
    public static final String NOTHING_SUBSTITUTION_LABEL = "---";

    /**
     * Label substitution for empty string in filters which sent to UI.
     * @see {@link QueryValuesLoader#substituteSpecialLabel}
     */
    public static final String BLANK_SUBSTITUTION_LABEL = "[Blank]";

    /**
     * Used to build instance of {@link ReportInputControl} which is data transfer object (DTO) for input control
     * together with it's state (values and validation errors etc.).
     *
     * @param inputControl instance of {@link InputControl} which state will be used to build DTO.
     * @param uiType type of control see bean {@code inputControlTypeConfiguration} in {@code applicationContext-cascade.xml}
     * @return instance of {@link ReportInputControl} which represents given input control but doesn't have filled state yet.
     * @throws CascadeResourceNotFoundException in case if some necessary resource is not found.
     *
     * @see {@link com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService#getInputControlsStructure}
     */
    public ReportInputControl buildReportInputControl(InputControl inputControl, String uiType, ResourceReference dataSource)
            throws CascadeResourceNotFoundException;

    /**
     * Returns instance of {@link InputControlState} for given input control together with other control values.
     * Possibly could execute query to fill values (for controls of query types).
     *
     *
     *
     * @param inputControl input control for which state going top be received.
     * @param dataSource data source from which values for state will be loaded (if necessary)
     * @param parameters map of other parameters with their values which possibly could take effect for getting state for given control.
     * @param parameterTypes
     * @param info {@link com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation} for given input control
     * @return instance of {@link InputControlState} for given input control together with other control values.
     * @throws CascadeResourceNotFoundException in case if some necessary resource is not found.
     *
     * @see {@link com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService#getValuesForInputControls}
     */
    public InputControlState getState(InputControl inputControl, ResourceReference dataSource,
                                      Map<String, Object> parameters, Map<String, Class<?>> parameterTypes, ReportInputControlInformation info) throws CascadeResourceNotFoundException;


    /**
     * Is used to convert input control values which serialized to string,
     * back their's native Java representation.
     *
     * @param rawData array of serialized values (for single value controls only first value is matter)
     * @param control given input control
     * @param info {@link ReportInputControlInformation} for given control.
     * @return native Java representation of passed serialized control values.
     * @throws CascadeResourceNotFoundException in case if some necessary resource is not found.
     *
     * @see {@link com.jaspersoft.jasperserver.war.cascade.ControlLogic#getTypedParameters}
     * @see {@link com.jaspersoft.jasperserver.war.cascade.ControlLogic#getValuesForInputControlsFromRawData}
     */
    public Object convertParameterValueFromRawData(String[] rawData, InputControl control, ReportInputControlInformation info)
            throws CascadeResourceNotFoundException, InputControlValidationException;

    /**
     * Helper method which will decide what to do if {@link InputControlHandler#NOTHING_SUBSTITUTION_VALUE} was passed as
     * a value for control.
     *
     * @param controlName name of control for which empty selection was received.
     * @param parameters map of typed parameters which could be modified in order to correctly handle empty selection
     */
    public void applyNothingSelected(String controlName, Map<String, Object> parameters);

    /**
     * Used to serialize values of control to array of string.
     *
     * @param typedValue value in it's native Java representation.
     * @param control given input control
     * @param info instance of {@link ReportInputControlInformation} for this control.
     * @return String[] Formatted value, array with single element for single select controls,
     *                                   array with multiple elements for multi select controls.
     * @throws CascadeResourceNotFoundException in case if some necessary resource is not found.
     */
    public String[] formatValue(Object typedValue, InputControl control, ReportInputControlInformation info) throws CascadeResourceNotFoundException;

    /**
     * Returns {@code true} if raw data represents empty selection in input control.
     *
     * @param rawData raw data from request.
     * @return {@code true} if nothing (or empty) selection is sent from client or {@code false} in other case.
     */
    public boolean isNothingSelected(String[] rawData);

    /**
     * Returns master dependencies of handled input control

     * @param control control to resolve its dependencies
     * @return {@code Set} of ids of master dependencies of control
     */
    public Set<String> getMasterDependencies(InputControl control, ResourceReference dataSource) throws CascadeResourceNotFoundException;
}
