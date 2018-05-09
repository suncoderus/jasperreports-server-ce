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
 * @author: Sergii Kylypko, Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function(require) {
    "use strict";

    var classUtil = require("common/util/classUtil");

    /*
     * Class to wrap i18n message.
     *
     * @param code Message bundle code
     * @param arguments Additional arguments for message
     */
    var i18nMessage = classUtil.extend({
        constructor: function (code){
            this.code = code;
            this.args = Array.prototype.slice.call(arguments, 1);
        },

        bundle: {}
    });

    /*
     * Override toString method.
     *
     * @return string If code was not found in bundle, just raw code is returned. If code was found in bundle,
     *     bundle from message is returned, optionally parts of message will be replaced with arguments.
     */

    i18nMessage.prototype.toString = function() {
        var text = this.bundle[this.code];

        if (!text) {
            return this.code;
        }

        for (var i = 0, l = this.args.length; i < l; i++) {
            text = text.replace("{" + i + "}", this.args[i]);
        }

        return text;
    };

    //Static i18n message factory
    i18nMessage.create = function(bundle) {
        return function(code) {
            var message = {
                bundle: bundle,
                code: code,
                args: Array.prototype.slice.call(arguments, 1)
            };

            return i18nMessage.prototype.toString.call(message);
        }
    };

    return i18nMessage;
});