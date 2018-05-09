/**
 * Copyright (C) 2005 - 2015 Jaspersoft Corporation. All rights reserved.
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
 * This program is distributed in the hope self it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: Zakhar Tomchenko
 * @version:
 */


define(function (require) {

    var _ = require("underscore"),
        $ = require("jquery"),
        importModelAttributesFactory = require("tenantImportExport/import/factory/importModelAttributesFactory"),
        BrokenDependencyStrategyEnum = require("../enum/brokenDependencyStrategyEnum"),
        AjaxFormSubmitter = require("common/transport/AjaxFormSubmitter"),
        BackboneValidation = require("backbone.validation"),
        BaseModel = require("common/model/BaseModel"),

        ImportModel = BaseModel.extend({

            defaults: {
                "fileName": "",
                "update": true,
                "skipUserUpdate": false,
                "mergeOrganization": false,
                "skipThemes": true,
                "brokenDependencies": BrokenDependencyStrategyEnum.FAIL
            },

            url: "rest_v2/import",

            validation: {
                fileName: [
                    {
                        fn: function(fileName) {
                            return !/\.zip$/.test(fileName);
                        }
                    }
                ]
            },

            initialize: function (attributes, options) {
                this.form = new AjaxFormSubmitter(options.form, this.url, "post", "multipart/form-data");
            },

            parse: function(){
                return this.attributes;
            },

            save: function () {
                var self = this,
                    result,
                    parameters;

                if (this.isNew()) {
                    result = new $.Deferred();
                    this.form.submit().done(function (responce) {
                        self.set("id",responce.id);
                        if (_.isUndefined(responce.errorCode) && _.isUndefined(responce.error)) {
                            result.resolve(responce);
                        } else {
                            result.reject(responce);
                        }
                    }).fail(function(responce) {
                        self.trigger("error", responce);
                        result.reject(responce);
                    });
                } else {
                    parameters = this._convertParameters();

                    result = BaseModel.prototype.save.call(this, {parameters: parameters}, {url: this.url + "/" + this.id}).fail(_.bind(this.trigger, "error"));
                }

                return result;
            },

            cancel: function() {
                var url = this.url + "/" + this.id;

                this.destroy({url: url});
            },

            reset: function(type, options) {
                var defaults = _.extend({}, this.defaults, importModelAttributesFactory(type), options);

                this.clear().set(_.extend({}, defaults));

                this.id = undefined;
            },

            _convertParameters: function() {
                var parameters = [];

                this.get("skipUserUpdate") && parameters.push("skip-user-update");
                this.get("includeAccessEvents") && parameters.push("include-access-events");
                this.get("includeAuditEvents") && parameters.push("include-audit-events");
                this.get("includeMonitoringEvents") && parameters.push("include-monitoring-events");
                this.get("includeServerSettings") && parameters.push("include-server-setting");
                this.get("mergeOrganization") && parameters.push("merge-organization");
                this.get("skipThemes") && parameters.push("skip-themes");
                this.get("update") && parameters.push("update");

                return parameters;
            }
        });

    _.extend(ImportModel.prototype, BackboneValidation.mixin);

    return ImportModel;

});