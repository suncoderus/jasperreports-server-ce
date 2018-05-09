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
 * @author: afomin, inesterenko
 * @version: $Id$
 */

/* global JRS, errorObject, __jrsConfigs__, Mustache, dialogs, _, statusText */

JRS.Controls = (function (JSON, jQuery, _, Controls) {

    //module:
    //
    //  controls.datatransfer
    //
    //summary:
    //
    //DataTransfer    - REST wrapper, returns normalized data
    //
    //dependencies:
    //
    //
    //  jQuery          - v1.7.1
    //  _,              - underscore.js 1.3.1
    //  Controls,       - controls.core module


    //Common error handing
    function commonErrorHandler(err) {
        var errorObject;
        try {
            try {
                errorObject = jQuery.parseJSON(err.responseText);
            } catch (e) {}

            if (errorObject && errorObject.error) {
                _.each(errorObject.error, function (error) {
                    //TODO: try to avoid it
                    var viewModel = Controls.getViewModel();
                    var controlUri = error.inputControlUri.replace("repo:", "");
                    var control = viewModel.find({uri:controlUri});
                    if (error.errorCode) {
                        control.set({error:error.errorCode});
                    } else if (error.defaultMessage) {
                        control.set({error:error.defaultMessage});
                    }
                });
            } else {
                if (err.getResponseHeader("LoginRequested") || err.status === 401) {
                    document.location = __jrsConfigs__.urlContext;
                } else if (err.status == 500 || (err.getResponseHeader("JasperServerError")
                    && !err.getResponseHeader("SuppressError"))) {

                    var message = "";
                    if (errorObject) {
                        message = Mustache.to_html("<div><b>{{message}}</b></div><p>{{#parameters}}<div>{{.}}</div>{{/parameters}}</p>", errorObject);
                    } else {
                        message = statusText;
                    }

                    dialogs.errorPopup.show(message);
                }
            }
        } catch (e) {
            // In this scenario security error is handled earlier, in errorHandler, so we can ignore exception here.
            // Comment this because it will not work in IE, but can be uncommented for debug purpose.
            // console.error("Can't parse server response: %s", "controls.core", err.responseText);
        }
    }

    return _.extend(Controls, {

        //REST wrapper, returns normalized data
        DataTransfer:Controls.Base.extend({
            CONTROLS_STRUCTURE_TEMPLATE_URI:__jrsConfigs__.contextPath + "/rest_v2/reports{{reportUri}}/inputControls/{{controlIds}}",

            INITIAL_CONTROLS_VALUES_TEMPLATE_URL:__jrsConfigs__.contextPath + "/rest_v2/reports{{reportUri}}/inputControls/values",

            CONTROLS_VALUES_TEMPLATE_URL:__jrsConfigs__.contextPath + "/rest_v2/reports{{reportUri}}/inputControls/{{controlIds}}/values",

            constructor : function(options) {
                this.dataConverter = options.dataConverter;
                this.initialize(options);
            },

            initialize : function(options) {},

            setReportUri:function (reportUri) {
                this.currentReportUri = reportUri;
            },
            buildUrl : function(reportPath, controlIds, urlTemplate) {
                controlIds = _.isArray(controlIds) ? controlIds.join(";") : controlIds || "";

                return Controls.TemplateEngine.renderUrl(urlTemplate, {
                    reportUri: reportPath,
                    controlIds: controlIds
                });

            },
            /**
             * Send request
             * @param url
             * @param data
             * @param settings
             * @returns {*}
             */
            sendRequest : function(url, data, settings) {
                var defaultSettings = {
                    url : url,
                    type : "GET",
                    dataType : "json",
                    cache : false
                };
                if (!_.isEmpty(data)) {
                    _.extend(defaultSettings, {
                        type : "POST",
                        processData : false,
                        data : JSON.stringify(data),
                        contentType : "application/json"
                    });
                }

                var requestSettings = _.defaults(settings || {}, defaultSettings);
                requestSettings.headers || (requestSettings.headers = {});

                _.defaults(requestSettings.headers, {
                    /**
                     *  Use custom HTTP header to prevent 401 response from server.
                     *  401 response makes browsers to show native login dialog.
                     *  We don't want that.
                     */
                    "X-Suppress-Basic" : "true",

                    /**
                     * Overwrite locale of browser, use locale of current user
                     */
                    "Accept-Language": __jrsConfigs__.userLocale.replace(/_/g, "-")
                });

                return jQuery.ajax(requestSettings)
                    .fail(commonErrorHandler);
            },

            fetchControlsStructure:function (uris, selectedData) {
                var url = this.buildUrl(this.currentReportUri, uris, this.CONTROLS_STRUCTURE_TEMPLATE_URI);

                var controlsStructure = this.sendRequest(url, selectedData).then(_.bind(this.dataConverter.structureFormater, this.dataConverter));

                Controls.Utils.showLoadingDialogOn(controlsStructure, null, true);
                return  controlsStructure;
            },

            updateControlsStructure: function(updatedStructure, uri){
                var url = this.buildUrl(uri || this.currentReportUri, null, this.CONTROLS_STRUCTURE_TEMPLATE_URI);

                return this.sendRequest(url, updatedStructure, {type : "PUT"}).then(_.bind(this.dataConverter.structureFormater, this.dataConverter));
            },

            fetchInitialControlValues:function (reportUri, selectedData) {
                var url = this.buildUrl(reportUri, null, this.INITIAL_CONTROLS_VALUES_TEMPLATE_URL);

                var initialControlsValues = this.sendRequest(url, selectedData).
                    then(this.dataConverter.convertResponseToControlsState);

                Controls.Utils.showLoadingDialogOn(initialControlsValues, null, true);
                return initialControlsValues;
            },

            fetchControlsUpdatedValues:function (ids, selectedData) {
                var url = this.buildUrl(this.currentReportUri, ids, this.CONTROLS_VALUES_TEMPLATE_URL);
                var dataConverter = this.dataConverter;
                if (this.fullUpdateDeferred && this.fullUpdateDeferred.state() == "pending") {
                    this.fullUpdateDeferred.reject();
                }

                var fullUpdateDeferred = new jQuery.Deferred();

                var triggerRequestDeferred = Controls.Utils.wait(Controls.DataTransfer.FREQUENT_CHANGES_MIN_DELAY);

                triggerRequestDeferred.done(_.bind(function () {
                    this.sendRequest(url, selectedData).
                        then(dataConverter.convertResponseToControlsState).done(
                            function (data) {
                                if (fullUpdateDeferred.state() == "pending") {
                                    fullUpdateDeferred.resolve(data);
                                }
                    }).
                        fail(function() {
                            fullUpdateDeferred.reject(this, arguments);
                        });
                }, this));

                fullUpdateDeferred.fail(function () {
                    triggerRequestDeferred.reject();
                });

                Controls.Utils.showLoadingDialogOn(fullUpdateDeferred, null, true);

                this.fullUpdateDeferred = fullUpdateDeferred;

                return fullUpdateDeferred;
            }

        }, {

            //Static props

            CONTROLS_STRUCTURE_TEMPLATE_URI:__jrsConfigs__.contextPath + "/rest_v2/reports{{reportUri}}/inputControls/{{controlIds}}",

            INITIAL_CONTROLS_VALUES_TEMPLATE_URL:__jrsConfigs__.contextPath + "/rest_v2/reports{{reportUri}}/inputControls/values",

            CONTROLS_VALUES_TEMPLATE_URL:__jrsConfigs__.contextPath + "/rest_v2/reports{{reportUri}}/inputControls/{{controlIds}}/values",

            FREQUENT_CHANGES_MIN_DELAY : 400
        })
    });

})(
    JSON,
    jQuery,
    _,
    JRS.Controls
);