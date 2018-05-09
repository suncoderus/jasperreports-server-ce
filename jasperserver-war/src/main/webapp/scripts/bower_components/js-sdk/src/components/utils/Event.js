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

    var _ = require("underscore");

    var Event = function (options) {

        options = options || {};

        this.name = options.name;
        this.data = options.data || {};
        this._isDefaultPrevented = false;
    };

    Event.prototype = Object.create({

        // Provide Setter/Getters

        get name(){
           return this._name;
        },

        set name(value){
           if (_.isString(value)){
               if (value.length){
                   this._name = value;
               }else {
                   throw new Error("'name' should't be an empty string");
               }
           }else{
               throw new TypeError("'name' must be a 'string'");
           }
        },

        get data(){
            return this._data;
        },

        set data(value){
            if (_.isObject(value)){
                this._data = value;
            }else{
                throw new TypeError("'data' must be an 'object'");
            }
        },

        // Provide `Prevent Default` mechanics to looks like in W3C

        isDefaultPrevented : function () {
            return this._isDefaultPrevented;
        },

        preventDefault : function () {
            this._isDefaultPrevented = true;
        }

    });

    return Event;


});
