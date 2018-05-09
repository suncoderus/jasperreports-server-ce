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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var request = require("request"),
        requestSettings = require("requestSettings"),
        _ = require("underscore");

    var VISUALIZE_NS = "__visualize__.";

    return {
        load : function(name, req, onLoad, config) {
            if (config.isBuild) {
                onLoad();
                return;
            }

            var settings = _.extend({}, requestSettings, {
                type: "GET",
                dataType: "script",
                cache: true,
                url: req.toUrl(name + ".js")
            });

            request(settings).then(function(resp) {
                if (window.__visualize__) {
                    var indexOfDefine = resp.indexOf("define(");

                    if (indexOfDefine > -1) {
                        onLoad.fromText(_.str.splice(resp, indexOfDefine, 0, VISUALIZE_NS));
                        return;
                    }
                }

                onLoad.fromText(resp);
            }, onLoad.error);
        }
    };
});