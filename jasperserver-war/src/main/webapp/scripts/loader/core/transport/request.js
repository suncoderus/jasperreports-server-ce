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
 * @author: Andriy Godovanec, Igor Nesterenko
 * @version $Id$
 */

define(function (require) {
    "use strict";

    var $ = require("jquery"),
        xdm = require("xdm"),
        Deferred = $.Deferred,
        fakeXhrFactory = require("fakeXhrFactory"),
        logger = require("logger"),
        rpc;

    function getLogParams() {
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

    var makeRpc = function(providerUrl){
            rpc =  new xdm.Rpc({
                remote: providerUrl + "?" + getLogParams(),
                container: document.body,
                props: {
                    'style': { display: "none"}
                }
            }, {
                remote: {
                    request: {},
                    abort:{}
                }
            });
        };

    var request = function (params, callback) {
            var dfd =  new Deferred(),
                errorback = params.error;

            callback = callback || params.success;

            if (rpc){
                rpc.request(
                    params,
                    function(result) {
                        dfd.resolve(result.data, result.status, fakeXhrFactory(result.xhr));
                    },
                    function(error) {
                        dfd.reject(error.message, error.data);
                    }
                );

                if (callback){
                    dfd.done(callback);
                }

                if (errorback){
                    dfd.error(errorback);
                }
            }else{
                dfd.rejectWith(new Error("RPC object is not initialized"));
            }

            return dfd.promise();
     };

    request.rpc = makeRpc;

    return request;

});