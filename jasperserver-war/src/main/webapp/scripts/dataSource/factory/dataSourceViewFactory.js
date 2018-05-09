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

define(function(require) {
    "use strict";

    var CustomDataSourceView = require("dataSource/view/CustomDataSourceView"),
        dataSourceResourceTypes = require("dataSource/enum/dataSourceResourceTypes"),
        customDataSourceTypes = require("dataSource/enum/customDataSourceTypes"),
        JdbcDataSourceView = require("dataSource/view/JdbcDataSourceView"),
        JndiDataSourceView = require("dataSource/view/JndiDataSourceView"),
        AwsDataSourceView = require("dataSource/view/AwsDataSourceView"),
        AzureSqlDataSourceView = require("dataSource/view/AzureSqlDataSourceView"),
        VirtualDataSourceView = require("dataSource/view/VirtualDataSourceView"),
        BeanDataSourceView = require("dataSource/view/BeanDataSourceView"),
        MongoDbDataSourceView = require("dataSource/view/MongoDbDataSourceView"),
	    MongoDbJdbcDataSourceView = require("dataSource/view/MongoDbJdbcDataSourceView"),
        DiagnosticCustomDataSourceView = require("dataSource/view/DiagnosticCustomDataSourceView");
        //TextDataSourceView = require("dataSource/fileDataSource/TextDataSourceView");


    var registeredViews = {};
    registeredViews[dataSourceResourceTypes.JDBC.toLowerCase()] = JdbcDataSourceView;
    registeredViews[dataSourceResourceTypes.JNDI.toLowerCase()] = JndiDataSourceView;
    registeredViews[dataSourceResourceTypes.AWS.toLowerCase()] = AwsDataSourceView;
    registeredViews[dataSourceResourceTypes.AZURE_SQL.toLowerCase()] = AzureSqlDataSourceView;
    registeredViews[dataSourceResourceTypes.VIRTUAL.toLowerCase()] = VirtualDataSourceView;
    registeredViews[dataSourceResourceTypes.BEAN.toLowerCase()] = BeanDataSourceView;
    registeredViews[dataSourceResourceTypes.MONGODB] = MongoDbDataSourceView;
	registeredViews[dataSourceResourceTypes.MONGODBJDBC] = MongoDbJdbcDataSourceView;
	// next, we pre-define the custom views for some of the custom data sources
	// you also can define your own views here
    registeredViews[customDataSourceTypes.DIAGNOSTIC] = DiagnosticCustomDataSourceView;

    // disable file data sources for now
	//registeredViews[customDataSourceTypes.TEXT_FILE] = TextDataSourceView;

    return {
        getView: function(options) {
            var constructor;

            if (options.dataSourceType) {
                constructor = registeredViews[options.dataSourceType];
                if (!constructor) {
                    constructor = CustomDataSourceView;
                }
            } else {
                // by default
                constructor = JdbcDataSourceView;
            }

            return new constructor(options);
        },

        getViewType: function(contentType, dataSource) {
            var result = /application\/repository\.([^+]+)\+json/.exec(contentType);
            var viewType = result && result[1] ? result[1].toLowerCase() : contentType.toLowerCase();
            // if this is custom data source, then view type is a dataSourceName attribute of data source entity
            if (dataSourceResourceTypes.CUSTOM.toLowerCase() === viewType) {
                viewType = dataSource.dataSourceName;
            }
            return viewType;
        }
    }
});