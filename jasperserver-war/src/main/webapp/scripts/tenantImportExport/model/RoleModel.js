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
        Backbone = require("backbone"),
        primaryNavModule = require("actionModel.primaryNavigation"),
        PermissionModel = require("tenantImportExport/model/PermissionModel");

    var FLOW_ID = "roleListFlow",
        USER_NAME_SEPARATOR = "|";

    var RoleModel = Backbone.Model.extend({

        initialize: function(options) {

            if (options) {
                this.roleName = options.roleName;
                this.external = options.external;
                this.tenantId = options.tenantId;
            }

            if (options.permissionToDisplay) {
                this.permission = new PermissionModel(options.permissionToDisplay);
            }
        },

        getDisplayName: function() {
            return this.roleName;
        },

        getNameWithTenant: function() {
            if (this.tenantId && !this.tenantId.blank()) {
                return this.roleName + USER_NAME_SEPARATOR + this.tenantId;
            } else {
                return this.roleName;
            }
        },

        getManagerURL: function() {
            return 'flow.html?' + Object.toQueryString({
                _flowId: FLOW_ID,
                // Object.toQueryString already does encodeURIComponent() once, so we have double encoding here
                text: typeof(this.roleName) !== 'undefined' ? encodeURIComponent(this.roleName) : this.roleName,
                tenantId: typeof(this.tenantId) !== 'undefined' ? encodeURIComponent(this.tenantId) : this.tenantId
            });
        },

        navigateToManager: function() {
            primaryNavModule.navigationPaths.tempNavigateToManager = _.cloneDeep(primaryNavModule.navigationPaths.role);
            primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
                text: this.roleName,
                tenantId: this.tenantId
            });
            primaryNavModule.navigationOption("tempNavigateToManager");
        },

        equals: function(role) {
            return role && this.roleName == role.roleName && this.tenantId == role.tenantId;
        },

        toPermissionData: function(user) {
            return {
                roleName: this.roleName,
                tenantId: this.tenantId,
                permissionToDisplay: this.permission.toData()
            };
        }
    });

    return RoleModel;
});

