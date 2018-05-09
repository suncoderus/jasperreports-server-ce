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
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var $ = require("jquery"),
        _ = require("underscore"),
        xdm = require("xdm"),
        fakeXhrFactory = require("fakeXhrFactory"),
        config = require("jrs.configs"),
        logger = require("logger");

    function sendRequest(rpc, params) {
        var innerDfd = $.Deferred(),
            outerDfd = $.Deferred();
        var requestId = rpc.request(
            params,
            function(result) {
                innerDfd.resolve(result.data, result.status, fakeXhrFactory(result.xhr));
            },
            function(error) {
                innerDfd.reject(error.message, error.data);
            }
        );

        // run $.ajax callbacks through Promise interface
        params.success && innerDfd.done(params.success);
        params.error && innerDfd.fail(params.error);
        params.complete && innerDfd.always(params.complete);

        innerDfd.done(outerDfd.resolve).fail(outerDfd.reject).progress(outerDfd.notify);
        outerDfd.fail(function(){
            innerDfd.state() == 'pending' && rpc.abort(requestId);
        });
        return outerDfd;
    }

    var request = function(params) {
        if(arguments.length === 0){
            throw new Error("Not enough arguments to perform the request.");
        }

        var rpc = request.create(params.baseUrl ? params.baseUrl : config.contextPath);
        return sendRequest(rpc, params);
    };

    function getLogParams() {
        //Pass logging settings to xdm through URL params
        var logParams = [];
        logParams.push({name: "logEnabled", value: logger.get("enabled")});

        if (logger.get("level")) {
            logParams.push({
                name: "logLevel",
                value: logger.get("level")
            });
        }

        return $.param(logParams);
    }

    request.create = _.memoize(function(baseUrl) {
        // xdm rpc object responsible for Cross Origin communication
        return new xdm.Rpc({
            remote: baseUrl + "/xdm.html?" + getLogParams(),
            container: document.body,
            props: {
                'style': { display: "none"}
            }
        }, {
            remote: {
                request: {},
                abort: {}
            }
        });

    });

    return request;
});