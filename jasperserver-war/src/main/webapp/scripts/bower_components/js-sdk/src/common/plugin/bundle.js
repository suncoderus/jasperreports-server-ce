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

    var _ = require("underscore"),
        requestSettings = _.cloneDeep(require("requestSettings")),
        request = require("request"),
        javaPropertiesParser = require("common/util/parse/javaProperties");

    requestSettings.headers["Cache-Control"] = "private";
    delete requestSettings.headers["Pragma"];

    return {
        load: function (name, req, onLoad, config) {

            if (config.isBuild) {
                onLoad();
                return;
            }

            var i18nConf = config.config.i18n || {},
                path = config.baseUrl ? config.baseUrl + "../bundles" : "/bundles",
                module = name.split("/")[0],
                locale = i18nConf.locale ? ("_" + i18nConf.locale) : "",
                replaceTo = i18nConf.paths && i18nConf.paths[module] || "";

            name = name.replace(module, replaceTo);

            name[0] !== "/" && (name = "/" + name);

            var settings = _.extend({}, requestSettings, {
                type: "GET",
                url: path + name + locale + ".properties"
            });

            request(settings).then(function(resp) {
                onLoad(javaPropertiesParser(resp));
            });
        }
    };

});