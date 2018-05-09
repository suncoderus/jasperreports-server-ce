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
        SubDataSourceCollection = require("dataSource/collection/SubDataSourceCollection"),
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),
        _ = require("underscore"),
        i18n = require("bundle!jasperserver_messages");

    return BaseDataSourceModel.extend({
        type: repositoryResourceTypes.VIRTUAL_DATA_SOURCE,
        defaults: (function (){
            var defaults = {};

            _.extend(defaults, BaseDataSourceModel.prototype.defaults, {
                subDataSources: []
            });

            return defaults;
        })(),

        validation: (function() {
            var validation = {};

            _.extend(validation, BaseDataSourceModel.prototype.validation, {
                subDataSources: [
					{
						arrayMinLength: 1,
						msg: i18n["ReportDataSourceValidator.error.sub.datasources.needed"]
					},
                    {
                        fn: function(value, attr, computedState) {
                            var subDataSourceIds = _.map(value, function(subDataSource) { return subDataSource.id.toLowerCase(); }),
                                countMap = {},
                                duplicateIds = [];

                            _.each(subDataSourceIds, function(id) {
                                if (id in countMap) {
                                    countMap[id]++;
                                } else {
                                    countMap[id] = 1;
                                }
                            });

                            for (var id in countMap) {
                                if (countMap[id] > 1) {
                                    duplicateIds.push(id);
                                }
                            }

                            if (duplicateIds.length > 0) {
                                return i18n["ReportDataSourceValidator.error.sub.datasources.id.duplicates"].replace("{0}", duplicateIds.join(", "));
                            }
                        }
                    },
					{

						fn: function() {
							var res = null;
							for (var i = 0; i < this.subDataSources.models.length; i++) {
								if (!this.subDataSources.models[i].isValid(true)) {
									res = true;
								}
							}
							return res;
						}
					}
                ]
            });

            return validation;
        })(),

        initialize: function(attributes, options) {
            BaseDataSourceModel.prototype.initialize.apply(this, arguments);

            this.subDataSources = new SubDataSourceCollection(this.get("subDataSources"));

            this.listenTo(this.subDataSources, "change reset", this.updateSubDataSourcesArray);

            if (options.dependentResources && options.dependentResources.length > 0) {
                this.subDataSources.forEach(function(model) {
                    model.set("readOnly", true);
                });
            }
        },

        updateSubDataSourcesArray: function() {
            this.set("subDataSources", this.subDataSources.toJSON());
        }
    });

});