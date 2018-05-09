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
 * @author Sergey Prilukin; modified by Ken Penn
 * @version: $Id: AvailableItemsList.js 812 2015-01-27 11:01:30Z psavushchik $
 */

/**
 * Utilities for DOM and CSS calculations
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        $ = require("jquery");

    return {

        /**
         * Makes copy of a passed DOM element right to a body
         * and thus makes it visible (left usually set to negative number to avoid blinks).
         * Then callback is called which could do any sizes calculation on visible dome element copy
         *
         * @param options
         */
        doCalcOnVisibleNodeClone: function(options) {
            if (!options || !options.el) {
                throw "Missing required option: el"
            }

            _.defaults(options, {
                css: {}, //addtitional styles for container element
                classes: "", //additional classes for container element
                container: "<div/>", //container text, element or selector
                appendTo: "body", //where to append container
                callback: function() {
                    throw "no callback was defined"
                },
                alwaysClone: false, //create clone even if passed element is visible
                cloneHandlers: false //whether to clone event handlers attached to passed element
            });

            _.defaults(options.css, {
                "position" : "absolute",
                "left" : "-9999px"
            });

            if (!options.alwaysClone && $(options.el).is(":visible")) {
                options.callback($(options.el));
            } else {
                var $el = $(options.el).clone();

                var $tmp = $(options.container)
                    .css(options.css)
                    .addClass(options.classes)
                    .appendTo($(options.appendTo))
                    .append($el);

                //user's code should do all measurements in callback
                options.callback($el);

                $tmp.remove();
            }
        }
    }
});

