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

    require("commons.main");

    var domReady = require("!domReady"),
        $ = require("jquery"),
        jrsConfigs = require("jrs.configs"),
        RepositoryResourceModel = require("bi/repository/model/RepositoryResourceModel"),
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),
        DataSourceController = require("dataSource/DataSourceController");

    domReady(function () {
        var options = jrsConfigs.addDataSource.initOptions;
        var array = /ParentFolderUri=([^&]+)/.exec(location.href);
        var dfd = $.Deferred().done(function () {
            var dataSourceController = new DataSourceController(options);
            $("#display").append(dataSourceController.$el);
            dataSourceController.render();
        });
        if (array && array[1]) {
            var parentFolderUri = decodeURIComponent(array[1]);
            if ("/" === parentFolderUri) {
                // it's root folder, it's always exists and it is always a folder ;)
                // So, don't need to check any thing
                options.parentFolderUri = parentFolderUri;
                dfd.resolve();
            } else {
                var repositoryModel = new RepositoryResourceModel(
                    {uri: parentFolderUri}, {contextPath: jrsConfigs.contextPath});

                repositoryModel.fetch().always(function () {
                    if (repositoryModel.type && repositoryResourceTypes.FOLDER.toLowerCase() === repositoryModel.type.toLowerCase()) {
                        options.parentFolderUri = parentFolderUri;
                    }
                    dfd.resolve();
                });
            }
        } else {
            dfd.resolve();
        }
    });
});
