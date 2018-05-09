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
 * Pagination model.
 *
 * @author: Taras Bidyuk
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        BackboneValidation = require("backbone.validation"),
        i18n = require("bundle!js-sdk/CommonBundle"),
        i18nMessage = require("common/util/i18nMessage").extend({bundle: i18n}),
        _ = require("underscore");

    var PaginationModel = Backbone.Model.extend({
        validation: {
            step: [{
                min: 1,
                msg: new i18nMessage("error.pagination.property.min.value", "step", 1)
            }],
            current: [{
                integerNumber: true,
                msg: new i18nMessage("error.pagination.property.integer.value", "current", 1)
            },
            {
                min: 1,
                msg: new i18nMessage("error.pagination.property.min.value", "current", 1)
            },
            {
                fn: function(value){
                    if(value > this.get("total")){
                        return new i18nMessage("error.pagination.property.max.value", "current", value);
                    }
                }
            }],
            total: [{
                min: 1,
                msg: new i18nMessage("error.pagination.property.min.value", "total", 1)
            }]
        },

        defaults: {
            step: 1,
            current: 1,
            total: 1
        }
    });

    // Add validation to model (https://github.com/thedersen/backbone.validation#validation-mix-in)
    _.extend(PaginationModel.prototype, BackboneValidation.mixin);

    return PaginationModel;
});