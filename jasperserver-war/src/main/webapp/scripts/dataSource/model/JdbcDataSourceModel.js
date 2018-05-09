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

    var BaseDataSourceModel = require("dataSource/model/BaseDataSourceModel"),
        JdbcDriverModel = require("dataSource/model/JdbcDriverModel"),
        JdbcDriverCollection = require("dataSource/collection/JdbcDriverCollection"),
        dataSourcePatterns = require("settings!dataSourcePatterns"),
        connectionTypes = require("dataSource/enum/connectionTypes"),
        _ = require("underscore"),
        $ = require("jquery"),
        XRegExp = require("xregexp"),
        forbidWhitespacesPattern = XRegExp(dataSourcePatterns.forbidWhitespacesPattern),
        i18n = require("bundle!jasperserver_messages"),
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),
        jasperserverConfig = require("bundle!jasperserver_config");

    var BASE_VALIDATION_OBJECT = (function() {
        var validation = {};

        _.extend(validation, BaseDataSourceModel.prototype.validation, {
            connectionUrl: [
                {
                    required: true,
                    msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.connectionUrl"]
                },
                {
                    fn: function(value, attr, state){
                        var allowSpaces = this.drivers.getDriverByClass(state.selectedDriverClass).get("allowSpacesInDbName");
                        if(!allowSpaces && !forbidWhitespacesPattern.test(value)){
                            return i18n["ReportDataSourceValidator.error.invalid.chars.reportDataSource.connectionUrl"];
                        }
                    }
                }
            ]
        });

        return validation;
    })();

    return BaseDataSourceModel.extend({
        JDBC_BUNDLE_PREFIX: "resource.dataSource.jdbc.",

        otherDriverIsPresent: true,
        type: repositoryResourceTypes.JDBC_DATA_SOURCE,

        defaults: (function (){
            var defaults = {};

            _.extend(defaults, BaseDataSourceModel.prototype.defaults, {
                driverClass: "", // this is the value of the selected driver class. Keep in mind, what "other" driver has (and should have) empty driver class name (i.e. "")
                selectedDriverClass: "", // this is the pointer to the "selected" element in the <select> html tag on the page
                username: "",
                password: "",
                timezone: "",
                connectionUrl: "",
                isOtherDriver: false,
                connectionType: connectionTypes.JDBC
            });

            return defaults;
        })(),

        validation: (function() {
            return _.extend({}, BASE_VALIDATION_OBJECT);
        })(),

        initialize: function(attributes, options) {

            BaseDataSourceModel.prototype.initialize.apply(this, arguments);

            this.initialization = $.Deferred();
            this.drivers = new JdbcDriverCollection([], this.options);

            var self = this;

            this.fetchDrivers().then(function() {
                if (self.isNew()) {
                    self.setCustomAttributesDefaultValues(self.drivers.getDefaultDriver());
                } else {
                    self.set("selectedDriverClass", self.get("driverClass"));
                    self.set(self.getCustomAttributeValuesFromConnectionUrl());
                    // use password substitution
                    self.set("password", jasperserverConfig["input.password.substitution"]);
                    self.extendValidation();
                }

                var customAttributesChangeEventString = _.map(self.drivers.getAllPossibleCustomAttributes(),
                    function(attr) { return "change:" + attr; }).join(" ");

                self.on(customAttributesChangeEventString, self.setConnectionUrlFromCustomAttributes);
                self.on("change:connectionUrl", self.setCustomAttributesFromConnectionUrl);
                self.on("change:selectedDriverClass", self.changeSelectedDriver);

                self.initialization.resolve();
            });
        },

        fetchDrivers: function() {
            var self = this;

            return this.drivers.fetch({reset: true}).done(function() {
                // set default driver

                // implementing issue JRS-9120: sorting of Driver List:
                // let's group drivers by their "installed" state and then sort them by alphabet
                var driversModels = _.groupBy(self.drivers.models, function(model) {
                    return model.attributes.available;
                });

                // from now the 'driversModels' is grouped like this:
                //      {true: Array[16], false: Array[4]}
                // Let's sort these two groups
                _.each(driversModels, function(groupSet, index) {
                    driversModels[index] = _.sortBy(groupSet, function(model) {
                        return model.attributes.label;
                    });
                });

                var finalModelsSet = [];
                // first, add installed drivers
                if (driversModels["true"]) {
                    finalModelsSet = finalModelsSet.concat(driversModels["true"]);
                }
                // then add not installed
                if (driversModels["false"]) {
                    finalModelsSet = finalModelsSet.concat(driversModels["false"]);
                }

                // store it back to original variable
                self.drivers.models = finalModelsSet;

                // not, let's add "other" and item if we need it
                if (self.drivers.driverUploadEnabled && self.otherDriverIsPresent) {
                    self.drivers.add({
                        defaultValues: {},
                        jdbcDriverClass: JdbcDriverModel.OTHER_DRIVER,
                        label: i18n["resource.dataSource.jdbc.otherDriver"],
                        available: false,
                        "default": false,
                        jdbcUrl: "",
                        uploaded: false
                    });
                }
            });
        },

        getCurrentDriver: function() {
            return this.drivers.getDriverByClass(this.get("selectedDriverClass"));
        },

        changeSelectedDriver: function() {
            var driver = this.drivers.getDriverByClass(this.get("selectedDriverClass"));

            if (driver) {
                this.setCustomAttributesDefaultValues(driver);

                this.setConnectionUrlFromCustomAttributes();

                this.trigger("driverClassChange", this);
            }
        },

        setCustomAttributesFromConnectionUrl: function() {
            var customAttributesHash = this.getCustomAttributeValuesFromConnectionUrl();

            this.set(customAttributesHash, { silent: true });

            // trigger custom event in order to prevent event loop
            this.trigger("customAttributesUpdate", this);
        },

        setConnectionUrlFromCustomAttributes: function() {
            var currentDriver = this.getCurrentDriver(),
                driverCustomAttributes = currentDriver.getCustomAttributes(),
                currentCustomAttributeValues = this.pick(driverCustomAttributes),
                connectionUrl = this.replaceConnectionUrlTemplatePlaceholdersWithValues(
                    currentDriver.get("jdbcUrl"), currentCustomAttributeValues);

            this.set("connectionUrl", connectionUrl, { silent: true });

            // trigger custom event in order to prevent event loop
            this.trigger("connectionUrlUpdate", this);
        },

        // Extract jdbc field values from url accordingly to template.
        getAttributeValueFromUrl: function(url, regExp) {
            var groups = regExp.exec(url);
            return [].slice.call(groups || [], 1);
        },

        getCustomAttributeValuesFromConnectionUrl: function() {
            var
                foundParts,
                currentDriver = this.getCurrentDriver(),
                url = this.get("connectionUrl"),
                regExp = XRegExp(currentDriver.convertUrlTemplateToRegex()),
                jdbcFields = currentDriver.getCustomAttributes(),
                fieldsWithValues = {};

            foundParts = this.getAttributeValueFromUrl(url, regExp);

            _.each(foundParts, function(group, i) {
                fieldsWithValues[jdbcFields[i]] = group;
            });

            return fieldsWithValues;
        },

        setCustomAttributesDefaultValues: function(driver) {
            this.unsetCustomAttributes();

            var defaultValues = {};

            if (!driver.isOtherDriver()) {
                _.extend(defaultValues, driver.get("defaultValues"));
                defaultValues["selectedDriverClass"] = driver.get("jdbcDriverClass");
                defaultValues["driverClass"] = driver.get("jdbcDriverClass");
                defaultValues["isOtherDriver"] = false;
                defaultValues["connectionUrl"] = this.replaceConnectionUrlTemplatePlaceholdersWithValues(
                    driver.get("jdbcUrl"), driver.get("defaultValues")
                );
            } else {
                defaultValues["selectedDriverClass"] = driver.get("jdbcDriverClass");
                defaultValues["driverClass"] = "";
                defaultValues["isOtherDriver"] = true;
            }

            this.set(defaultValues, { silent: true });
            this.extendValidation();
        },

        unsetCustomAttributes: function() {
            var self = this;

            _.each(this.drivers.getAllPossibleCustomAttributes(), function(attr) {
                self.unset(attr, { silent: true });
            });
        },

        // Replace placeholders with values, if value is not present, set empty string.
        replaceConnectionUrlTemplatePlaceholdersWithValues: function(urlTemplate, valuesMap) {
            var groups = this.getRegExpFieldGroupsFromConnectionUrlTemplate(urlTemplate);

            _.each(groups, function(group) {
                urlTemplate = urlTemplate.replace(group[0], !_.isUndefined(valuesMap[group[1]]) ? valuesMap[group[1]] : "" );
            });

            return urlTemplate;
        },

        // Evaluate regexp on urlTemplate and return all found groups.
        getRegExpFieldGroupsFromConnectionUrlTemplate: function(urlTemplate) {
            var groups = [], group;

            while (!_.isNull(group = JdbcDriverModel.FIELD_TEMPLATE_REGEXP.exec(urlTemplate))) {
                if (_.isArray(group) && group.length === 2) {
                    groups.push(group);
                }
            }

            return groups;
        },

        extendValidation: function() {
            var self = this,
                extendedValidationObject = {},
                customDriverFields = this.getCurrentDriver().getCustomAttributes();

            _.extend(extendedValidationObject, BASE_VALIDATION_OBJECT);

            _.each(customDriverFields, function(field) {
                extendedValidationObject[field] = [
                    {
                        xRegExpPattern: JdbcDriverModel.VALIDATION_PATTERNS[field],
                        msg: (i18n[self.JDBC_BUNDLE_PREFIX + "invalidField"]).replace("{0}", i18n[self.JDBC_BUNDLE_PREFIX + field])
                    }
                ];
            });

            this.validation = extendedValidationObject;
        },

        toJSON: function() {
            var data = BaseDataSourceModel.prototype.toJSON.apply(this, arguments);

            if (this.options.isEditMode && data.password === jasperserverConfig["input.password.substitution"]) {
                data.password = null;
            }

            return data;
        }
    });
});