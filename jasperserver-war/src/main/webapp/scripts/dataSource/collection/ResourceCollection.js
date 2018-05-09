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

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        JdbcDriverModel = require("dataSource/model/JdbcDriverModel");

    return Backbone.Collection.extend({
        model: JdbcDriverModel,

        initialize: function(models, options) {
            this.options = options;
        },

        getDefaultDriver: function() {
            var defaultDriver = this.find(function(driver) {
                return !_.isUndefined(driver.get("default")) && driver.get("default") !== false;
            });

            return defaultDriver ? defaultDriver : this.first();
        },

        set: function(models, options) {
            if (typeof options === "undefined") {
                options = {};
            }

            _.extend(options, this.options);

            return Backbone.Collection.prototype.set.call(this, models, options);
        },

        getDriverByClass: function(className) {
            var driver = this.findWhere({ jdbcDriverClass: className });

            return driver ? driver : this.findWhere({ jdbcDriverClass: JdbcDriverModel.OTHER_DRIVER });
        },

        getAllPossibleCustomAttributes: function() {
            return _.keys(JdbcDriverModel.VALIDATION_PATTERNS);
        }
    });
});