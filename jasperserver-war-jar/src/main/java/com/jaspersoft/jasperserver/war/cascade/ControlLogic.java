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

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Interface which implements startegy of how input controls will be handled.
 *
 * @author jwhang
 * @version $Id$
 *          Interface implemented for use by anyone creating custom business rules.
 *
 * @see {@link com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler}
 * @see {@link GenericInputControlLogic}
 */
@JasperServerAPI
public interface ControlLogic<T extends InputControlsContainer> {

    /**
     * Gets structure for input controls, which ids were specified.
     *
     * @param containerUri       input controls container URI
     * @param inputControlIds Set of id of input controls, which structures should be returned.
     * @return List<ReportInputControl> data transfer objects which represents
     *                                  input controls for given controls container.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     */
    public List<ReportInputControl> getInputControlsStructure(String containerUri, Set<String> inputControlIds)
            throws CascadeResourceNotFoundException;

    /**
     * Gets structure for input controls, which ids were specified.
     *
     * @param container       input controls container
     * @param inputControlIds Set of id of input controls, which structures should be returned.
     * @return List<ReportInputControl> data transfer objects which represents
     *                                  input controls for given controls container.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     */
    public List<ReportInputControl> getInputControlsStructure(T container, Set<String> inputControlIds)
            throws CascadeResourceNotFoundException;

    /**
     * Resolve cascade values using request values for controls, which ids were specified.
     * It performs query and returns states only for specified control ids.
     * Missing request parameters are completed with default parameter values.
     * If inputControlIds is null or empty, states for all controls will be returned.
     *
     * @param containerUri         input controls container URI
     * @param inputControlIds   Set of id of input controls, which structures should be returned.
     * @param requestParameters map with control values where key is control name
     *                   and value is serialized to array of strings control values
     * @return List<InputControlState> instance of {@link InputControlState} which represents state of input control
     *                                 with it's values, errors etc.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     */
    public List<InputControlState> getValuesForInputControlsFromRawData(
            String containerUri, Set<String> inputControlIds, Map<String, String[]> requestParameters)
                throws CascadeResourceNotFoundException;

    /**
     * Resolve cascade values using request values for controls, which ids were specified.
     * It performs query and returns states only for specified control ids.
     * Missing request parameters are completed with default parameter values.
     * If inputControlIds is null or empty, states for all controls will be returned.
     *
     * @param container         input controls container
     * @param inputControlIds   Set of id of input controls, which structures should be returned.
     * @param requestParameters map with control values where key is control name
     *                   and value is serialized to array of strings control values
     * @return List<InputControlState> instance of {@link InputControlState} which represents state of input control
     *                                 with it's values, errors etc.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     */
    public List<InputControlState> getValuesForInputControlsFromRawData(
            T container, Set<String> inputControlIds, Map<String, String[]> requestParameters)
                throws CascadeResourceNotFoundException;

    /**
     * Parse string parameter values and convert it to the type of JRParameter.
     *
     * @param containerUri         input controls container URI
     * @param requestParameters Parameters from servlet request.
     * @param skipValidation whether input controls validation should be skipped.
     *                       If true - {@link InputControlsValidationException} will never be thrown.
     * @return Map<String, Object> a map where key is control name and value is native Java representation of passed
     *                              serialized value.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     * @throws InputControlsValidationException
     *          Thrown when value for input control is invalid.
     */
    public Map<String, Object> getTypedParameters(
            String containerUri, Map<String, String[]> requestParameters, boolean skipValidation)
                throws CascadeResourceNotFoundException, InputControlsValidationException;

    /**
     * Parse string parameter values and convert it to the type of JRParameter.
     *
     * @param container         input controls container
     * @param requestParameters Parameters from servlet request.
     * @param skipValidation whether input controls validation should be skipped.
     *                       If true - {@link InputControlsValidationException} will never be thrown.
     * @return Map<String, Object> a map where key is control name and value is native Java representation of passed
     *                              serialized value.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     * @throws InputControlsValidationException
     *          Thrown when value for input control is invalid.
     */
    public Map<String, Object> getTypedParameters(
            T container, Map<String, String[]> requestParameters, boolean skipValidation)
                throws CascadeResourceNotFoundException, InputControlsValidationException;

    /**
     * Formats typed parameters according to the types of input control and JRParameter.
     *
     * @param containerUri Input controls container URI
     * @param typedParameters Typed paraterers
     * @return Map<String, String[]> map where key is control name and
     *                              value is array of serialized to string control values
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     */
    public Map<String, String[]> formatTypedParameters(String containerUri, Map<String, Object> typedParameters)
            throws CascadeResourceNotFoundException;

    /**
     * Formats typed parameters according to the types of input control and JRParameter.
     *
     * @param container Input controls container
     * @param typedParameters Typed paraterers
     * @return Map<String, String[]> map where key is control name and
     *                              value is array of serialized to string control values
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     */
    public Map<String, String[]> formatTypedParameters(T container, Map<String, Object> typedParameters)
            throws CascadeResourceNotFoundException;
}
