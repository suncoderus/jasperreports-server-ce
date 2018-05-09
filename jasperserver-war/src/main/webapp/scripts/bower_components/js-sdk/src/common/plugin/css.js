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

    var originalCssPlugin = require("requirejs.plugin.css"),
        _ = require("underscore");

    var customizedCssPlugin = _.clone(originalCssPlugin);

    customizedCssPlugin.load = function(cssId, req, load, config) {

        var themeConfig = config.config ? config.config.theme : false;

        if (!themeConfig || !themeConfig.href) {
            // skipp CSS on-demand loading
            // we assume that CSS loads in some other way
            load();
            return;
        }

        cssId = [themeConfig.href, cssId].join("/");

        var customReq = _.extend(_.clone(req),{
             toUrl : function () {
                 return cssId + ".css";
             }
        });

        originalCssPlugin.load.call(this, cssId, customReq, load, config);
    };

    /*
    * provided function allows to load css in case when current strategy is to not load css from this plugin
    *
    * */
    customizedCssPlugin.manualLoad = function(cssId, theme) {
        var callback  =  function() {};
        customizedCssPlugin.load(cssId, require, callback, {
            config: {
                theme: theme
            }
        }, true);
    };





    return customizedCssPlugin;
});
