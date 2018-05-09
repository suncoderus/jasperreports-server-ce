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
 * @author: Andrew Godovanec, Igor Nesterenko, Kostiantyn Tsaregradskyi
 * @version: $Id: text.js 270 2014-10-13 19:58:03Z agodovanets $
 */

define(function (require) {
    "use strict";

    var textPlugin = require("requirejs.plugin.text"),
        _ = require("underscore"),
        request = require("request");

    var textPluginXhrFactory = textPlugin.createXhr,
        load = textPlugin.load;

    return _.extend(textPlugin, {
        useXhr: function () {
            return true;
        },

        createXhr: function () {
            var tmpXhr = textPluginXhrFactory.apply(textPlugin, arguments),
                requestParams = { "headers":{"Cache-Control":"private", "Pragma":""} },
                xhr = {
                    response: undefined,
                    responseBody: undefined,
                    responseText: undefined,
                    responseType: undefined,
                    responseXML: undefined,
                    status: undefined,
                    statusText: undefined
                };

            // Clone properties to new object to be able to override some methods.
            // IE8 for some reason throws errors when we try to access "response*" and "status*" properties,
            // that's why we ignore them when copying propertiess to new object.
            for (var prop in tmpXhr) {
                if (prop.indexOf("response") == -1 && prop.indexOf("status") == -1) {
                    xhr[prop] = tmpXhr[prop];
                }
            }

            return _.extend(xhr, {
                open: function (method, url, async) {
                    requestParams.url = url;
                    xhr.status = 1;
                },

                send: function () {
                    request(requestParams).
                        done(function (resp, status, jqXhr) {
                            xhr.readyState = jqXhr.readyState;
                            xhr.status = jqXhr.status;
                            xhr.responseText = jqXhr.responseText;
                            xhr.onreadystatechange();
                        }).
                        fail(function (error) {
                            xhr.onerror(error);
                        });
                }
            });
        },

        load: function (name, req, onLoad, config) {

            //Skip optimization of the resource if it's explicitly excluded
            if (config.isBuild && config.excludeText && _.indexOf(config.excludeText, name) >= 0) {
                onLoad();
                return;
            }

            return load(name, req, onLoad, config);
        }
    });
});