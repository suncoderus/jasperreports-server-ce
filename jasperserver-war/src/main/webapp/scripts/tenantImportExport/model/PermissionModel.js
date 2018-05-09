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

    var Backbone = require("backbone");

    var PermissionModel = Backbone.Model.extend({

        initialize: function(options) {
            this.isInherited = !!options.isInherited;
            if (options.permission) {
                this.permission = options.permission;
            }
            if (options.inheritedPermission) {
                this.inheritedPermission = options.inheritedPermission;
            }
            this.isDisabled = !!options.isDisabled;
        },

        getResolvedPermission: function() {
            return this.isInherited ? this.inheritedPermission : this.permission;
        },

        toJSON: function() {
            return {
                permission: this.permission,
                isInherited: this.isInherited,
                inheritedPermission: this.inheritedPermission,
                newPermission: this.newPermission
            };
        },

        toData: function() {
            return {
                permission: this.permission,
                isInherited: this.isInherited,
                inheritedPermission: this.inheritedPermission,
                newPermission: this.newPermission
            };
        }

    });

    return PermissionModel;
});

