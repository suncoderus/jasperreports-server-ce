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

/*
* ---------------------------------------------------------------------------
* OTHER PARTIES' CODE:
*
* function inherit taken from soma.js framework which available
* under MIT License
* Copyright (c) | 2013 | soma-events | Romuald Quantin | www.soundstep.com
* For more information visit https://github.com/somajs/somajs
* Inherit function code located: https://github.com/somajs/somajs/blob/master/src/utils.js
*/

 /**
 * @version: $Id$
 */

// TODO: rename this file to something like classExtensionUtil

define(function(require){
    "use strict";

    var _ = require("underscore");

    var classUtil = {
        mixin : function () {
            var arg, prop, arr = Array.prototype, to = arr.shift.call(arguments);
            for (arg = 0; arg < arguments.length; arg += 1) {
                if (!arguments[arg]) {
                    continue;
                }
                for (prop in arguments[arg]) {
                    if (arguments[arg].hasOwnProperty(prop)) {
                        to[prop] = arguments[arg][prop]; }
                }
            }
            return to;
        },
        /**
         * Copyright (c) | 2013 | soma-events | Romuald Quantin | www.soundstep.com
         */
        inherit : function(parent, obj) {
            var Subclass;
            if (obj && obj.hasOwnProperty('constructor')) {
                // use constructor if defined
                Subclass = obj.constructor;
            } else {
                // call the super constructor
                Subclass = function () {
                    return parent.apply(this, arguments);
                };
            }
            // set the prototype chain to inherit from the parent without calling parent's constructor
            var Chain = function(){};
            Chain.prototype = parent.prototype;
            Subclass.prototype = new Chain();
            // add obj properties
            if (obj) {
                _.extend(Subclass.prototype, obj);
            }
            // point constructor to the Subclass
            Subclass.prototype.constructor = Subclass;
            // set super class reference
            Subclass.parent = parent.prototype;
            // add extend shortcut
            Subclass.extend = function (obj) {
                return classUtil.inherit(Subclass, obj);
            };
            return Subclass;
        },

        extend : function(obj) {
            return classUtil.inherit(function() {}, obj);
        }
    };

    return classUtil;
});
