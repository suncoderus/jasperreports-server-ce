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


/**
 * @version: $Id$
 */

/* global orgModule, _, layoutModule, webHelpModule, localContext, isProVersion, invokeClientAction */

function invokeRoleAction(actionName, options) {
    var action = orgModule.roleActionFactory[actionName](options);
    action.invokeAction();
}

function invokeRoleManagerAction(actionName, options) {
    var action = orgModule.roleManager.actionFactory[actionName](options);
    action.invokeAction();
}

function canAddRole() {
    if (orgModule.roleManager.tenantsTree) {
        return orgModule.roleManager.tenantsTree.getTenant() != null;
    } else {
        return true;
    }
}

function canEditRole(role) {
    var role = role ? role : orgModule.properties.getValue();
    return (role.tenantId || orgModule.manager.isUserSuperuser());
}

function canDeleteRole(role) {
    var role = role ? role : orgModule.properties.getValue();
    return (role.tenantId || (orgModule.manager.isUserSuperuser() && !_.contains(orgModule.systemRoles, role.roleName)));
}

function canDeleteAll() {
    var roles = orgModule.entityList.getSelectedEntities();
    return roles.length > 0 && !roles.detect(function(role) {
        return !canDeleteRole(role);
    });
}

orgModule.roleManager = {
    Event: {
    },

    Action: {
    },

    initialize: function(opt) {
        layoutModule.resizeOnClient('folders', 'roles', 'properties');
        webHelpModule.setCurrentContext("admin");

        var options = _.extend(opt, localContext.roleMngInitOptions, {removeContextMenuTreePlugin: true});

        // Manager customization.
        orgModule.manager.initialize(options);
        orgModule.manager.entityJsonToObject = function(json) {
            return new orgModule.Role(json);
        };
        orgModule.manager.relatedEntityJsonToObject = function(json) {
            return new orgModule.User(json);
        };

        this.roleList.initialize({
            toolbarModel: this.actionModel,
            text: orgModule.manager.state.text
        });

        // Dialogs customization.
        orgModule.addDialog.show = function(org) {
            this.addDialog.show(org);
        }.bind(this);
        orgModule.addDialog.hide = function(org) {
            this.addDialog.hide(org);
        }.bind(this);

        this.properties.initialize();
        this.addDialog.initialize();

        orgModule.observe("server:unavailable", function(event) {
            var tree = orgModule.manager.tree;

            var id = tree ? tree.getOrganization().id : null;
            new orgModule.Role({roleName: "", tenantId: id}).navigateToManager();
        }.bindAsEventListener(this));

        orgModule.observe("entity:deleted", function(){
            orgModule.properties.hide();
        });

        orgModule.observe("entities:deleted", function(){
            orgModule.properties.hide();
        });

        if(!isProVersion()) {
            orgModule.manager.reloadEntities();
        }
    },

    actionModel: {
        ADD: {
            buttonId: "addNewRoleBtn",
            action: invokeClientAction,
            actionArgs: "create",
            test: canAddRole
        },
        DELETE: {
            buttonId: "deleteAllRolesBtn",
            action: invokeClientAction,
            actionArgs: "deleteAll",
            test: canDeleteAll
        }
    }
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
if (typeof require === "undefined") {
    // prevent conflict with domReady plugin in RequireJS environment
    document.observe('dom:loaded', orgModule.roleManager.initialize.bind(orgModule.roleManager));
}
