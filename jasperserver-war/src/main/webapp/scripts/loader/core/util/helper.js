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
 * @author: Igor Nesterenko
 * @version $Id$
 */

define(function () {
    "use strict";

    return {

        serverSettings : function(htmlContent){
            var jsContent = htmlContent.match(/<script[^>]*>([^<]*)<\/script>/)[1],
                /* jshint evil: true */
                //run 'safe' eval
                func = new Function(jsContent + "return __jrsConfigs__;");
                /* jshint evil: false */
            return func();
        },

        loaderConfig: function(javascript){
            /* jshint evil: true */
            return (new Function("requirejs", "return " + javascript))({ config: function(opts) { return opts; } });
            /* jshint evil: false */
        }

    };

});