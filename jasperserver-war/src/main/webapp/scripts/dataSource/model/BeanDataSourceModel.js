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
		connectionTypes = require("dataSource/enum/connectionTypes"),
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),
        _ = require("underscore"),
        i18n = require("bundle!jasperserver_messages");

    return BaseDataSourceModel.extend({
        type: repositoryResourceTypes.BEAN_DATA_SOURCE,
        defaults: (function (){
            var defaults = {};

            _.extend(defaults, BaseDataSourceModel.prototype.defaults, {
                beanName: "",
                beanMethod: "",
				connectionType: connectionTypes.BEAN
            });

            return defaults;
        })(),

        validation: (function() {
            var validation = {};

            _.extend(validation, BaseDataSourceModel.prototype.validation, {
                beanName: [
                    {
                        required: true,
                        msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.beanName"]
                    }
                ],
                beanMethod: [
                    {
                        required: true,
                        msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.beanMethod"]
                    }
                ]
            });

            return validation;
        })()
    });

});