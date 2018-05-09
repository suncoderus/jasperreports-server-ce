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
 * @version: $Id$
 */

define(function(require) {
    var _ = require("underscore"),
        Backbone = require("backbone"),
        request = require("request"),
        groupsEnum = require("serverSettingsCommon/enum/serverSettingGroupsEnum");


    var ResetSettingsCollection = Backbone.Collection.extend({

        initialize: function(models, options) {
            this.urlPUTTemplate = "rest_v2/attributes?_embedded=permission" ;
            this.urlGETTemplate = this.urlPUTTemplate + "&group=" + groupsEnum.CUSTOM_SERVER_SETTINGS;
        },

        parse: function(data) {
            return data && data.attribute ? data.attribute : [];
        },

        url: function(type) {
            return type === "PUT" ? this.urlPUTTemplate : this.urlGETTemplate;
        },

        escapeLevelId: function(id) {
            return encodeURIComponent(id).replace("'", "%27");
        },

        save: function(allModels, updatedModels) {
            var type = "PUT",
                updatedModelsJSON = this._modelsToJSON(updatedModels);

            return request({
                url: this.url(type) + this._concatNames(allModels),
                type: "PUT",
                contentType: "application/hal+json",
                headers: {
                    Accept: "application/hal+json"
                },
                data: JSON.stringify({"attribute": updatedModelsJSON})
            });
        },

        _modelsToJSON: function(models) {
            return _.map(models, function(model) {
                return model.toJSON();
            });
        },

        _concatNames: function(models) {
            models = _.isArray(models) ? models : [models];

            var nameStr = "";

            _.each(models, function(model) {
                nameStr += "&name=" + this.escapeLevelId(model.get("name"));
            }, this);

            return nameStr;
        }

    });

    return ResetSettingsCollection;
});

