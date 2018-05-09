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
 * @author: Dima Gorbenko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        i18n = require("bundle!jasperserver_messages"),
        jrsConfigs = require('jrs.configs'),
        request = require("request"),
        characterEncodings = require("dataSource/fileDataSource/enum/characterEncodings"),
        delimitersTextDataSource = require("dataSource/fileDataSource/enum/delimitersTextDataSource"),
        FileDataSourceModel = require("dataSource/fileDataSource/FileDataSourceModel");

    return FileDataSourceModel.extend({
        fileTypes:["txt", "csv"],

        validation: (function() {
            var validation = {};

            _.extend(validation, FileDataSourceModel.prototype.validation, {
                fieldDelimiterOther: [
                    {
                        fn: function(value, attr, computedState) {
                            if (computedState.fieldDelimiter === "other" && (_.isNull(value) || _.isUndefined(value) || (_.isString(value) && value === ''))) {
                                return i18n["fillParameters.error.enter.field.delimiter"];
                            }
                            return null;
                        }
                    }
                ],
                rowDelimiterOther: [
                    {
                        fn: function(value, attr, computedState) {
                            if (computedState.rowDelimiter === "other" && (_.isNull(value) || _.isUndefined(value) || (_.isString(value) && value === ''))) {
                                return i18n["fillParameters.error.enter.row.delimiter"];
                            }
                            return null;
                        }
                    }
                ]
            });

            return validation;
        })(),

		constructor: function(attributes, options) {
			this.defaults = _.extend({}, this.defaults, {
				fileSourceType: "repository",
				ftpsPort: "990",
				fieldDelimiter: "comma",
				rowDelimiter: "newLineWin",
				encodingType: "utf8",
                useFirstRowAsHeader: true,
				prepareDataForReporting: true
			});

            FileDataSourceModel.prototype.constructor.apply(this, [attributes, options]);
		},

        parse: function() {
            var model = FileDataSourceModel.prototype.parse.apply(this, arguments);
            var tmp;

            // converting column delimiter from database format into UI
            model.fieldDelimiterRegex = "";
            model.fieldDelimiterPlugin = "";
            model.fieldDelimiterOther = "";
            tmp = _.find(delimitersTextDataSource, function (d) { return d.dbValue === model.fieldDelimiter; });
            if (!_.isUndefined(tmp)) {
                model.fieldDelimiter = tmp.value;
            } else {
                if (!!model.fieldDelimiter) {
                    model.fieldDelimiterOther = model.fieldDelimiter;
                    model.fieldDelimiter = "other";
                } else {
                    model.fieldDelimiter = this.defaults.fieldDelimiter;
                }
            }

            // converting row delimiter from database format into UI
            model.rowDelimiterRegex = "";
            model.rowDelimiterPlugin = "";
            model.rowDelimiterOther = "";
            tmp = _.find(delimitersTextDataSource, function(d){ return d.dbValue === model.recordDelimiter; });
            if (!_.isUndefined(tmp)) {
                model.rowDelimiter = tmp.value;
            } else {
                if (!!model.fieldDelimiter) {
                    model.rowDelimiterOther = model.recordDelimiter;
                    model.rowDelimiter = "other";
                } else {
                    model.rowDelimiter = this.defaults.rowDelimiter;
                }
            }
            delete model.recordDelimiter;


            // converting encoding
            tmp = _.find(characterEncodings, function(d){ return d.dbValue === model.encoding; });
            model.encodingType = tmp ? tmp.value : this.defaults.encodingType;
            delete model.encoding;

            return model;
        },

        customFieldsToJSON: function(data, customFields) {

            // converting column delimiter from UI format into database
            if (data.fieldDelimiter === "other") {
                data.fieldDelimiter = data.fieldDelimiterOther;
            } else {
                data.fieldDelimiter = _.find(delimitersTextDataSource, function (d) {
                    return d.value === data.fieldDelimiter;
                }).dbValue;
            }
            // remove them if any exists
            delete data.fieldDelimiterRegex;
            delete data.fieldDelimiterPlugin;
            delete data.fieldDelimiterOther;


            // converting row delimiter from UI format into database
            if (data.rowDelimiter === "other") {
                data.recordDelimiter = data.rowDelimiterOther;
            } else {
                data.recordDelimiter = _.find(delimitersTextDataSource, function (d) {
                    return d.value === data.rowDelimiter;
                }).dbValue;
            }
            // remove them if any exists
            delete data.rowDelimiter;
            delete data.rowDelimiterRegex;
            delete data.rowDelimiterPlugin;
            delete data.rowDelimiterOther;


            // converting encoding
            data.encoding = _.find(characterEncodings, function(d){ return d.value === data.encodingType; }).dbValue;
            delete data.encodingType;


            return FileDataSourceModel.prototype.customFieldsToJSON.call(this, data, customFields);

        },

        validationMethodOnSaveClick: function(callback) {
            var self = this,
                data = JSON.stringify(_.extend({}, this.toJSON(), {
                    uri: "/ignore"
                }));

            request({
                type: "POST",
                url: jrsConfigs.contextPath + "/rest_v2/connections",
                dataType: "json",
                data: data,
                headers: {
                    "Content-Type" : "application/repository.customDataSource+json",
                    "Accept": "application/table.metadata+json"
                }
            }).done(function(){
                self.trigger("sourceFileIsOK");
                if (_.isFunction(callback)) {
                    callback();
                }

            }).fail(function(){
                self.trigger("sourceFileCantBeParsed");
            });
        }
    });
});