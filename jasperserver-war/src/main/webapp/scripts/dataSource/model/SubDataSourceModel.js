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

    var Backbone = require("backbone"),
        _ = require("underscore"),
		ResourceModel = require("bi/repository/model/RepositoryResourceModel"),
        i18n = require("bundle!jasperserver_messages");

    return Backbone.Model.extend({
        defaults: {
            id: undefined,
            uri: "",
            name: "",
            readOnly: false
        },

		idAttribute: "uri",

        validation: {
            id: [
                {
                    required: true,
                    msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.name"]
                },
                {
                    maxLength: ResourceModel.settings.NAME_MAX_LENGTH,
                    msg: i18n["ReportDataSourceValidator.error.too.long.reportDataSource.name"]
                },
                {
                    startsWithLetter: true,
                    msg: i18n["ReportDataSourceValidator.error.invalid.chars.shouldStartWithLetter"]
                },
                {
                    containsOnlyWordCharacters: true,
                    msg: i18n["ReportDataSourceValidator.error.invalid.chars.wordCharsOnly"]
                }
            ]
        },

        initialize: function(attributes){
            this.set("name", /.*\/(.+)$/.exec(attributes.uri)[1]);
        }
    });
});