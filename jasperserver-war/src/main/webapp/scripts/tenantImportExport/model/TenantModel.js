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
        primaryNavModule = require("actionModel.primaryNavigation");

    var TenantModel = Backbone.Model.extend({
        defaults: {
            id: undefined,
            name: undefined,
            alias: undefined,
            desc: "",
            uri: undefined,
            parentId: undefined
        },

        initialize: function(json) {
            if (!json) {
                throw new Error("Can't create Tenant from undefined json");
            }
            //TODO: Move this to model.attributes.
                this.name = json.label || json.tenantName;
                this.label = this.name;
                this.alias = json.alias || json.tenantAlias;
                this.desc = json.tenantDesc;
                this.uri = json.tenantUri || json.uri;
                this.parentId = json.parentId;
                this.subTenantCount = json.subTenantCount;

            this.set({
                id: json.id || json.tenantId,
                name: this.name,
                uri: json.tenantUri || json.uri,
                alias: json.alias || json.tenantAlias
            });
        },

        isRoot: function() {
            // TODO: load name of root organization from configuration.
            return this.get("id") == 'organizations';
        },

        //TODO: Move to parent class.
        getId: function() {
            return this.get("id");
        },

        getNameWithTenant: function() {
            return this.get("id");
        },

        getDisplayName: function() {
            return this.get("name");
        },

        equals: function(org) {
            return org && this.get("id") == org.get("id");
        },

        navigateToManager: function() {
            primaryNavModule.navigationPaths.tempNavigateToManager = _.cloneDeep(primaryNavModule.navigationPaths.organization);
            primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
                tenantId: this.get("id")
            });
            primaryNavModule.navigationOption("tempNavigateToManager");
        }
    });

    return TenantModel;
});

