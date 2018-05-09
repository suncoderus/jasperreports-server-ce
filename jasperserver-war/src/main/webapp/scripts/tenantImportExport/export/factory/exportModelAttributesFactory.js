/**
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
 * @author: Olesya Bobruyko
 * @version:
 */


define(function(require) {

    var _ = require("underscore"),
        exportTypesEnum = require("tenantImportExport/export/enum/exportTypesEnum");

    var attributesByType = {},
        commonAttributes = {
            roles: null,
            users: null,
            everything: true,
            userForRoles: false,
            rolesForUser: false,
            includeReports: true,
            includeOtherResourceFiles: true,
            includeDataSources: true,
            includeDependentObjects: true,
            includeAttributes: true,
            includeAttributeValues: true
        },

        events = {
            includeAccessEvents: false,
            includeAuditEvents: false,
            includeMonitoringEvents: false
        },

        proAssets = {
            includeDomains: true,
            includeAdHocViews: true,
            includeDashboards: true
        },

        tenantAssets = {
            includeSubOrganizations: true
        },

        serverAssets = {
            includeServerSettings: true
        };


    attributesByType[exportTypesEnum.ROOT_TENANT] = _.extend({}, commonAttributes, events, proAssets, tenantAssets, serverAssets);

    attributesByType[exportTypesEnum.TENANT] = _.extend({}, commonAttributes, proAssets, tenantAssets);

    attributesByType[exportTypesEnum.SERVER_PRO] = _.extend({}, commonAttributes, events, proAssets, tenantAssets, serverAssets);

    attributesByType[exportTypesEnum.SERVER_CE] = _.extend({}, commonAttributes, serverAssets, {
        includeAccessEvents: true
    });

    return function(type) {
        return attributesByType[type];
    }
});
