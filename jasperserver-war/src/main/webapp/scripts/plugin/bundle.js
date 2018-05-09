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
 * @author: Zakhar Tomchenko, Igor Nesterenko, Andrew Godovanec, Sergey Prilukin
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var request = require("request"),
        requestSettings = require("requestSettings"),
        _ = require("underscore"),
        configs = require("jrs.configs"),
        urlRoot = configs.runtimeContextPath + "/rest_v2/bundles";

    // use bundle name "all" to get all available bundles merged to single bundle
    var MERGED_BUNDLES_NAME = "all";

    var bundlePluginFn = function(bundleName, callback) {
        var urlSuffix;

        if (MERGED_BUNDLES_NAME === bundleName) {
            urlSuffix = "?expanded=true";
        } else {
            bundleName = bundleName.split("/");
            urlSuffix = "/" + bundleName[bundleName.length-1];
        }

        var settings = _.extend({}, requestSettings, {
            type: "GET",
            dataType: "json",
            url: urlRoot + urlSuffix
        });

        // Changing default Cache Control directiove to have posebility decide caching on the server
        // Default value was 'no-cache'
        settings.headers["Cache-Control"] = "private";
        delete settings.headers["Pragma"];

        request(settings).then(function(resp) {
            callback(MERGED_BUNDLES_NAME !== bundleName ? resp : _(resp).reduce(function(memo, bundle) {
                return _.extend(memo, bundle);
            }, {}));
        });
    };

    bundlePluginFn.load = function (name, req, onLoad, config) {

        if (config.isBuild) {
            onLoad();
            return;
        }

        bundlePluginFn(name, onLoad);
    };

    return bundlePluginFn;
});
