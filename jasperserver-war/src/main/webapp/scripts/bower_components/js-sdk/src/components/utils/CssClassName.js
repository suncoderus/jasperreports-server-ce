/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        _s = require("underscore.string");


    var CssClassName = function (options) {

        options = options || {};

        this.type = options.type;
        this.name = options.name;
    };

    CssClassName.prototype = Object.create({

        get name(){
            return this._name;
        },

        set name(value){

            if (!_.isString(value)){
                throw new TypeError("'name' should be a string");
            }else if (value.length === 0){
                throw new Error("'name' shouldn't be an empty string");
            }

            var chars = _s.chars(value);
            //it should be upper case by css guidelines
            chars[0] = chars[0].toUpperCase();

            this._name = chars.join('');
        },

        get type(){
            return this._type;
        },

        set type(value){

            if (!_.isString(value)){
                throw new TypeError("'type' should be a string");
            }else if (value.length === 0){
                throw new Error("'type' shouldn't be an empty string");
            }

            var isOneOfAvailableTypes = Object.keys(CssClassName.TYPES).some(function (key) {
                return CssClassName.TYPES[key] === value;
            });

            if (!isOneOfAvailableTypes){
                throw new Error("'type' should be one of available types")
            }

            this._type = value;
        },

        toString: function () {
            return CssClassName.MAIN_PREFIX + "-" + CssClassName.TYPE_PEFIXES[this.type] + this.name;
        }

    });


    CssClassName.TYPES = {
        MODULE : "module",
        LAYOUT : "layout",
        UTIL   : "util",
        STATE  : "state",
        JSHOOK : "jshook"
    };

    CssClassName.TYPE_PEFIXES = {
        "module" : "m",
        "layout" : "l",
        "state"  : "is",
        "util"   : "u",
        "jshook": "j"
    };

    CssClassName.MAIN_PREFIX = "jr";

    return CssClassName;


});
