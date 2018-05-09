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

define(function (require){

    "use strict";

    var
        $ = require('jquery'),
        _ = require('underscore'),
        request = require("request"),
        jrsConfigs = require("jrs.configs"),
        ResourceModel = require("bi/repository/model/RepositoryResourceModel");

    return ResourceModel.extend({

        defaults: {
            uri: undefined,
            label: undefined,
            columns:[],
            dataSourceUri: undefined
        },

        validation: {
            columns: function(columns) {
                var errors = [];
                _.each(columns, function(column, rowId) {
                    if (!column.label) {
                        errors.push({
                            rowId: rowId,
                            name: "label"
                        });
                    }
                });
                if (errors.length !== 0) {
                    this.trigger("validationFailed", errors);
                } else {
                    this.trigger("validationPassed");
                }

                return null;
            }
        },

        type: "simpleDomain",

        url: function(){ return jrsConfigs.contextPath + "/rest_v2/domains";},

        // isNew == true to force POST method
        isNew: function(){return true;},

        constructor: function(attributes, options) {
            ResourceModel.prototype.constructor.apply(this, arguments);
            this.dataSource = options.dataSource;
            if(options.dataSource && options.dataSource.uri){
                this.set("dataSourceUri", options.dataSource.uri);
            }
        },

        save: function(attributes, options){
            _.defaults(options || (options = {}), {
                headers:  {
                    "Accept": "application/json",
                    "Content-Type": "application/simpleDomain+json; charset=UTF-8"
                }
            });
            return ResourceModel.prototype.save.call(this, attributes, options);
        },

        parseMetadata: function(data) {
            _.each(data.columns, function(column){
                column.show = true;
            });

            return data;
        },

        parse: function(input){
            var data = ResourceModel.prototype.parse.apply(this, arguments);
            if(data.dataSource){
                data.dataSourceUri = data.dataSource.dataSourceReference;
                delete data.dataSource;
            }
            if(data.metadata){
                data.columns = data.metadata.columns;
                delete data.metadata;
            }
            return data;
        },

        toJSON: function(){
            var data = ResourceModel.prototype.toJSON.apply(this, arguments);
            data.dataSource = {dataSourceReference: data.dataSourceUri};
            data.metadata = {
                columns: _.reduce(data.columns, function(memo, column){
                    if(column.show){
                        // add only columns to show
                        memo.push(column);
                    }
                    return memo;
                },[]),
                queryLanguage:"csv"
            };
            delete data.dataSourceUri;
            delete data.columns;
            return data;
        },

        fetchMetadata: function() {
            var self = this, dfr = $.Deferred();
            request({
                type: "POST",
                url: jrsConfigs.contextPath + "/rest_v2/connections",
                dataType: "json",
                data: JSON.stringify(this.dataSource),
                headers: {
                    "Content-Type" : "application/repository.customDataSource+json",
                    "Accept": "application/table.metadata+json"
                }
            }).done(function(response) {

                response = self.parseMetadata(response);

                self.set("columns", response.columns);

                dfr.resolve(response);

            }).fail(function(xhr){
                dfr.reject(xhr);
            });
            return dfr;
        }
    });
});
