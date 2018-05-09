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

/* global orgModule, _, webHelpModule, layoutModule, isProVersion, invokeClientAction, localContext, require */

function invokeUserAction(actionName, options) {
    var action = orgModule.userActionFactory[actionName](options);
    action.invokeAction();
}

function invokeUserManagerAction(actionName, options) {
    var action = orgModule.userManager.actionFactory[actionName](options);
    action.invokeAction();
}

function isLoggedInUserSelected() {
    var selected = orgModule.entityList.getSelectedEntities();
    var currentUser = orgModule.userManager.options.currentUser;

    return selected.length > 0 && !!selected.detect(function(item) {
        return item.getNameWithTenant() == currentUser;
    });
}

function canAddUser() {
    if (orgModule.manager.tenantsTree) {
        return orgModule.manager.tenantsTree.getTenant() != null;
    } else {
        return true;
    }
}

function canEnableAll() {
    return orgModule.entityList.getSelectedEntities().length > 0;
}

function canDisableAll() {
    return orgModule.entityList.getSelectedEntities().length > 0 && !isLoggedInUserSelected();
}

function canDeleteAll() {
    return orgModule.entityList.getSelectedEntities().length > 0 && !isLoggedInUserSelected();
}

function canDeleteUser() {
    return orgModule.entityList.getSelectedEntities().length > 0;
}

    orgModule.userManager = {
//    EMAIL_REG_EXP: XRegExp("^[\\p{L}\\p{M}\\p{N}._%'-\\@\\,\\;\\s]+$"),

        Event: {
            USERS_ENABLED: 'users:enabled',
            USERS_DISABLED: 'users:disabled'
        },

        Action: {
            ENABLE_ALL: 'enableAll',
            DISABLE_ALL: 'disableAll'
        },

        initialize: function(opt) {
            webHelpModule.setCurrentContext("admin");

            layoutModule.resizeOnClient('folders', 'users', 'properties');

            var options = _.extend({}, opt, localContext.userMngInitOptions, {removeContextMenuTreePlugin: true});
            orgModule.userManager.options = options;

            // Manager customization.
            orgModule.manager.initialize(options);
            orgModule.manager.entityJsonToObject = function(json) {
                return new orgModule.User(json);
            };
            orgModule.manager.relatedEntityJsonToObject = function(json) {
                return new orgModule.Role(json);
            };

            this.userList.initialize({
                toolbarModel: this.actionModel,
                text: orgModule.manager.state.text
            });

            // Dialogs customization.
            orgModule.addDialog.show = function(org) {
                this.addDialog.show(org);
            }.bind(this);
            // Dialogs customization.
            orgModule.addDialog.hide = function(org) {
                this.addDialog.hide(org);
            }.bind(this);

            this.properties.initialize(options);
            this.addDialog.initialize();

            function enabledOrDisabledHandler(event) {
                var users = event.memo.inputData.users;

                if (orgModule.properties.isEditMode) {
                    orgModule.properties.changeMode(false);
                }

                users.length == 1 && orgModule.entityList.selectEntity(users[0].getNameWithTenant());
            }

            orgModule.observe("users:enabled", enabledOrDisabledHandler.bindAsEventListener(this));
            orgModule.observe("users:disabled", enabledOrDisabledHandler.bindAsEventListener(this));
            orgModule.observe("entity:deleted", function() {
                orgModule.properties.hide();
            });

            orgModule.observe("entities:deleted", function() {
                orgModule.properties.hide();
            });

            orgModule.observe("server:unavailable", function(event) {
                var tree = orgModule.manager.tenantsTree;

                var id = tree ? tree.getOrganization().id : null;
                new orgModule.User({userName: "", tenantId: id}).navigateToManager();
            }.bindAsEventListener(this));

            if (!isProVersion()) {
                orgModule.manager.reloadEntities();
            }
        },

        actionModel: {
            ADD: {
                buttonId: "addNewUserBtn",
                action: invokeClientAction,
                actionArgs: "create",
                test: canAddUser
            },

            ENABLE: {
                buttonId: "enableAllUsersBtn",
                action: invokeUserManagerAction,
                actionArgs: "enableAllUsers",
                test: canEnableAll
            },

            DISABLE: {
                buttonId: "disableAllUsersBtn",
                action: invokeUserManagerAction,
                actionArgs: "disableAllUsers",
                test: canDisableAll
            },

            DELETE: {
                buttonId: "deleteAllUsersBtn",
                action: invokeClientAction,
                actionArgs: "deleteAll",
                test: canDeleteAll
            }
        },

        validators: {
        }
    };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (typeof require === "undefined") {
        // prevent conflict with domReady plugin in RequireJS environment
        document.observe('dom:loaded', orgModule.userManager.initialize.bind(orgModule.userManager));
    }
