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
        XRegExp = require("xregexp"),
        dataSourcePatterns = require("settings!dataSourcePatterns"),
        settingsUtility = require("dataSource/util/settingsUtility");

    var JdbcDriverModel = Backbone.Model.extend({
        idAttribute: "jdbcDriverClass",

        defaults: {
            defaultValues: {},
            jdbcDriverClass: "",
            label: "",
            available: false,
            isDefault: false,
            jdbcUrl: "",
            uploaded: false
        },

        initialize: function(attributes, options) {
            var mergedOptions = settingsUtility.deepDefaults(options, {
                dataSourcePatterns: dataSourcePatterns
            });
            JdbcDriverModel.DYNAMIC_URL_PART_PATTERN = mergedOptions.dataSourcePatterns.dynamicUrlPartPattern;
            JdbcDriverModel.VALIDATION_PATTERNS = _.reduce(mergedOptions.dataSourcePatterns, function(obj, value, propName) {
                obj[propName] = XRegExp(value);
                return obj;
            }, {});
        },

        isOtherDriver: function() {
            return this.get("jdbcDriverClass") === JdbcDriverModel.OTHER_DRIVER;
        },

        isUploadedDriver: function() {
            return this.get("uploaded") === true;
        },

        getCustomAttributes: function() {
            if (this.isOtherDriver()) {
                return [];
            }

            var groups = this._getRegExpFieldGroupsFromConnectionUrlTemplate(),
                fields = [];

            _.each(groups, function(group) {
                return fields.push(group[1]);
            });

            return fields;
        },

        // Convert url template to regexp template
        convertUrlTemplateToRegex: function() {
            var
                placeholderPattern,
                patternTemplate = this.get("jdbcUrl");
            // escaping ? otherwise regexp will not match to url "jdbc:sybase:Tds:localhost:5433?ServiceName=name"
            patternTemplate = patternTemplate.replace(/\?/g, "\\?");
            patternTemplate = patternTemplate.replace(/\[TMPDIR\/\]/g, "\\[TMPDIR/\\]");

            // replacing dynamic parts
            for (var patternName in JdbcDriverModel.VALIDATION_PATTERNS) {
                placeholderPattern = new RegExp("\\$\\[" + patternName + "\\]","g");
                patternTemplate = patternTemplate.replace(placeholderPattern, JdbcDriverModel.DYNAMIC_URL_PART_PATTERN);
            }

            return "^" + patternTemplate;
        },

        parse: function(response) {
            response.isDefault = response.isDefault ? true : false;
            if(response.defaultValues){
                response.defaultValues = _.reduce(response.defaultValues, function(memo, element){
                    memo[element.key] = element.value;
                    return memo;
                }, {});
            }
            return response;
        },

        // Evaluate regexp on urlTemplate and return all found groups.
        _getRegExpFieldGroupsFromConnectionUrlTemplate: function() {
            var groups = [], group;
            var groupSet = [];
            while (!_.isNull(group = JdbcDriverModel.FIELD_TEMPLATE_REGEXP.exec(this.get("jdbcUrl")))) {
                if (_.isArray(group) && group.length === 2) {
                    if (groupSet.indexOf(group[0]) === -1) {
                        groups.push(group);
                        groupSet.push(group[0]);
                    }
                }
            }

            return groups;
        }
    }, {
        FIELD_TEMPLATE_REGEXP: /\$\[([^\]]+)\]/g,
        OTHER_DRIVER: "other"
    });

    return JdbcDriverModel;
});