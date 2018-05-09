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
        $ = require("jquery"),
        Backbone = require("backbone"),
        request = require("request"),
        TemplateEngine = require("components.templateengine"),
        groupsEnum = require("serverSettingsCommon/enum/serverSettingGroupsEnum"),
        levelIdEnum = require("attributes/enum/levelIdEnum");

    var HOLDERS = {
        TENANT: "tenant:/",
        USER: "user:/"
    };

    var AttributesCollection = Backbone.Collection.extend({

        initialize: function(models, options) {
            options = options || {};
            var defaultUrlPUTTemplate = "rest_v2/attributes?_embedded=permission",
                defaultUrlGETTemplate = defaultUrlPUTTemplate + "&group=" + groupsEnum.CUSTOM,

                context = options.context || {};

            this.urlGETTemplate = context.urlGETTemplate || defaultUrlGETTemplate;
            this.urlPUTTemplate = context.urlPUTTemplate || defaultUrlPUTTemplate;
        },

        /**
         * Parses server responce. It is called by Backbone.
         * @instance
         * @memberof Attributes
         * @param data received JavaScript object
         * @return error code
         */
        parse: function(data) {
            return data && data.attribute ? data.attribute : [];
        },

        setContext: function(context, forceRefresh) {
            context = context || {
                urlGETTemplate: this.urlGETTemplate,
                urlPUTTemplate: this.urlPUTTemplate
            };

            var dfd = new $.Deferred();

            if (_.isEqual(this.context, context) && !forceRefresh) {
                dfd.resolve();
            } else {
                this.context = context;
                this.fetch({cache: false, reset: true, headers: {Accept: "application/attributes.collection.hal+json"}})
                    .done(dfd.resolve);
            }

            return dfd;
        },

        getContext: function() {
            return this.context;
        },

        /**
         * Returns url of collection.
         * @instance
         * @param {string} [type] - type of request
         * @return url of representing collection
         */
        url: function(type) {
            var data = _.extend({}, this.context),
                urlTemplate = type === "PUT" ? this.urlPUTTemplate : this.urlGETTemplate;

            _.each(levelIdEnum, function(id) {
                data[id] && (data[id] = this.escapeLevelId(data[id]));
            }, this);

            return TemplateEngine.renderUrl(urlTemplate, data, false);
        },

        escapeLevelId: function(id) {
            return encodeURIComponent(id).replace(/'/g, "%27");
        },

        save: function(allModels, updatedModels) {
            var type = "PUT",
                updatedModelsJSON = this._modelsToJSON(updatedModels, true),
                contentType = "application/hal+json";

            return request({
                url: this.url(type) + this._concatNames(allModels),
                type: "PUT",
                cache: false,
                contentType: contentType,
                headers: {
                    Accept: contentType
                },
                data: JSON.stringify({"attribute": updatedModelsJSON})
            });
        },

        validateSearch: function(model, newModels, omitId, groupsSearch) {
            var recursive = "&recursive=true",
                groups = groupsSearch && this._concatGroups();

            return this.search(model, newModels, recursive, omitId, groups)
                .done(_.bind(this._successSearchCallback, this, model, newModels));
        },

        filterInheritedAttributes: function(data) {
            var attributes = data && data.attribute;

            return data
                ? _.filter(attributes, function(attribute) {
                      return attribute.inherited && !this.findWhere({name: attribute.name, inherited: true});
                  }, this)
                : null;
        },

        search: function(models, newModels, recursive, omitId, groups) {
            recursive = recursive || "";
            groups = groups || "";

            var contentType = "application/attributes.collection.hal+json";

            return request({
                url: this.url() + this._concatNames(models, omitId) + recursive + groups,
                type: "GET",
                dataType: "json",
                cache: false,
                contentType: contentType,
                headers: {
                    Accept: contentType
                }
            });
        },

        getHolder: function() {
            return this.context.id
                ? HOLDERS.TENANT + this.context.id
                : this.context.tenantId
                ? HOLDERS.USER + this.context.tenantId + "/" + this.context.userName
                : HOLDERS.TENANT;
        },

        addItemsToCollection: function(attributes) {
            !_.isArray(attributes) && (attributes = [attributes]);

            _.each(attributes, function(attribute) {
                this.addNew(attribute);
            }, this);
        },

        addNew: function(attribute) {
            attribute = attribute || {};

            var model = new this.model(attribute);
            this.add(model);

            return model;
        },

        _modelsToJSON: function(models, omitValue) {
            return _.map(models, function(model) {
                return model.toJSON({omitValue: omitValue});
            });
        },

        _successSearchCallback: function(models, newModels, data) {
            models = _.isArray(models) ? models : [models];
            newModels = this._modelsToJSON(newModels);

            var holder = this.getHolder(),
                defaultModel,
                modelHolder;

            _.each(models, function(model) {
                modelHolder = holder ? holder : model.holder;
                model.holder = modelHolder;

                if (data && data.attribute.length) {
                    model.attr = data.attribute;
                }

                if (newModels && _.where(newModels, {name: model.get("name"), inherited: false}).length > 1) {
                    defaultModel = {holder: modelHolder};
                    model.attr ? model.attr.push(defaultModel) : (model.attr = [defaultModel])
                }
            }, this);
        },

        _concatGroups: function() {
            var groups = "";

            _.each(_.omit(groupsEnum, "CUSTOM"), function(group) {
                groups += "&group=" + group;
            });

            return groups;
        },

        _concatNames: function(models, omitId) {
            models = _.isArray(models) ? models : [models];

            var nameStr = "",
                self = this,
                getNameParameter = function(name) {
                    return "&name=" + self.escapeLevelId(name);
                },
                id;

            _.each(models, function(model) {
                id = model.get("id");

                !omitId && model.isRenamed() && id && (nameStr += getNameParameter(id));
                nameStr += getNameParameter(model.get("name"));
            }, this);

            return nameStr;
        }

    });

    return AttributesCollection;
});

