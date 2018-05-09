/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: agodovanets, inesterenko
 * @version: $Id$
 */

/* global JRS, _ */

;(function (exp, _, Controls) {
    var DataConverter = Controls.Base.extend({
        //Clean up input structure from mess
        structureFormater : function(response) {
            if (!response) {
                return response;
            }

            var formatedResponse = _.map(response.inputControl, function (res) {
                res.readOnly = String(res.readOnly) == "true";
                res.uri = res.uri.replace("repo:", "");
                return res;
            });
            var state = _.map(formatedResponse, function (controlStructure) {
                return controlStructure.state;
            });
            state = _.compact(state);
            _.each(formatedResponse, function(structure){
                delete structure["state"];
            });
            return _.extend({ structure:formatedResponse }, this.convertResponseToControlsState(state));

        },

        //Convert controls update response to data structure compatible with controls
        convertResponseToControlsState : function(rawState) {
            var result = {};
            var requestStates = rawState.inputControlState ? rawState.inputControlState : rawState;

            var convertOptionFunc = function (option) {
                var opt = {
                    value : option.value,
                    label : option.label
                };
                if (String(option.selected) == "true"){
                    opt.selected  = true;
                }
                return opt;
            };

            var convertOptionsFunc = function(options){
                var values;
                if (_.isArray(options)) {
                    values = _.map(options, convertOptionFunc);
                } else {
                    values = [];
                }
                return values;
            };

            var convertToValues = function(requestState) {
                if (!_.isUndefined(requestState.value) && !_.isNull(requestState.value)) {
                    return requestState.value;
                }
                return convertOptionsFunc(requestState.options);
            };

            _.each(requestStates, function (requestState) {
                if (requestState && requestState.uri) {
                    result[requestState.id] = {
						error: _.isUndefined(requestState.error) ? null : requestState.error,
                        values : convertToValues(requestState)
                    };
                }
            });
            return {
                state : result
            };
        }
    });


    exp.DataConverter = DataConverter;
    return exp;
})(JRS.Controls, _, JRS.Controls);