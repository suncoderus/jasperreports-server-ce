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
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for a service which is key point in dealing with input controls for different
 * kinds of resources which could contain input controls like
 *  {@link com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit}
 * etc.
 *
 * Implementations of this interface allows to get input controls DTO's (data transfer objects),
 * get values for input controls based on passed parameters,
 * get deserialized values for input controls based on passed serialized parameters,
 * get serialized to string values for given native Java values representation for input controls.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: InputControlsLogicService.java 22695 2012-03-21 13:55:32Z ykovalchyk $
 *
 * @see {@link ControlLogic}
 */
@JasperServerAPI
public interface InputControlsLogicService {

    /**
     * Gets structure for input controls, which ids were specified.
     *
     * @param containerUri       input controls container URI
     * @param inputControlIds Set of id of input controls, which structures should be returned.
     * @return List<ReportInputControl> data transfer objects which represents
     *                                  input controls for given controls container.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     *
     * @see {@link ControlLogic#getInputControlsStructure}
     * @see {@link ReportInputControl}
     */
    List<ReportInputControl> getInputControlsStructure(String containerUri, Set<String> inputControlIds)
            throws CascadeResourceNotFoundException;

    /**
     * Getting input controls structure with filled states.
     *
     * @param containerUri -  input controls container URI
     * @param inputControlIds - set of id of input controls, which structures should be returned.
     * @param rawParameters - map with control values where key is control name
     *                   and value is serialized to array of strings control value
     * @return input controls structure with filled states (values)
     * @throws CascadeResourceNotFoundException if required resource isn't found
     */
    List<ReportInputControl> getInputControlsWithValues(String containerUri, Set<String> inputControlIds,
            Map<String, String[]> rawParameters) throws CascadeResourceNotFoundException;

    /**
     * Reorders input controls of specified container.
     *
     *
     * @param containerUri  input controls container URI
     * @param newOrder  list of input controls ids, sorted in new order
     * @throws CascadeResourceNotFoundException
     */
    List<ReportInputControl> reorderInputControls(String containerUri, List<String> newOrder)
            throws CascadeResourceNotFoundException, InputControlsValidationException;

    /**
     * Resolve cascade values using request values for controls, which ids were specified.
     * It performs query and returns states only for specified control ids.
     * Missing request parameters are completed with default parameter values.
     * If inputControlIds is null or empty, states for all controls will be returned.
     *
     * @param containerUri         input controls container URI
     * @param inputControlIds   Set of id of input controls, which structures should be returned.
     * @param parameters map with control values where key is control name
     *                   and value is serialized to array of strings control value
     * @param freshData cache isn't used if freshData is true, otherwise cached data are used.
     * @return List<InputControlState> instance of {@link InputControlState} which represents state of input control
     *                                 with it's values, errors etc.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     *
     * @see {@link ControlLogic#getValuesForInputControlsFromRawData}
     */
    List<InputControlState> getValuesForInputControls(
            String containerUri, Set<String> inputControlIds, Map<String, String[]> parameters, boolean freshData)
                throws CascadeResourceNotFoundException;

    /**
     * Parse string parameter values and convert it to the type of {@link net.sf.jasperreports.engine.JRParameter}.
     *
     * @param containerUri         input controls container URI
     * @param requestParameters map with control values where key is control name
     *                   and value is serialized to array of strings control value
     * @return Map<String, Object> a map where key is control name and value is native Java representation of passed
     *                              serialized value.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     * @throws InputControlsValidationException
     *          Thrown when value for input control is invalid.
     */
    Map<String, Object> getTypedParameters(String containerUri, Map<String, String[]> requestParameters)
            throws CascadeResourceNotFoundException, InputControlsValidationException;

    /**
     * Parse string parameter values and convert it to the type of {@link net.sf.jasperreports.engine.JRParameter}.
     *
     * @param containerUri         input controls container URI
     * @param requestParameters map with control values where key is control name
     *                   and value is serialized to array of strings control value
     * @param skipValidation whether input controls validation should be skipped.
     *                       If true - {@link InputControlsValidationException} will never be thrown.
     * @return Map<String, Object> a map where key is control name and value is native Java representation of passed
     *                              serialized value.
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     * @throws InputControlsValidationException
     *          Thrown when value for input control is invalid.
     */
    Map<String, Object> getTypedParameters(
            String containerUri, Map<String, String[]> requestParameters, boolean skipValidation)
                throws CascadeResourceNotFoundException, InputControlsValidationException;

    /**
     * Formats typed parameters according to the types of input control
     * and {@link net.sf.jasperreports.engine.JRParameter}.
     *
     * @param containerUri Input controls container URI
     * @param typedParameters Typed parameters
     * @return Map<String, String[]> map where key is control name and
     *                              value is array of serialized to string control values
     * @throws CascadeResourceNotFoundException
     *          Thrown when resource is not found.
     * @throws InputControlsValidationException
     *          Thrown when value for input control is invalid.
     */
    Map<String, String[]> formatTypedParameters(String containerUri, Map<String, Object> typedParameters)
            throws CascadeResourceNotFoundException, InputControlsValidationException;

}
