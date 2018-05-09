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

/* global orgModule, layoutModule, webHelpModule, localContext, isProVersion, invokeClientAction,
 dynamicList, RegExpRepresenter, dialogs, invokeServerAction, ValidationModule, matchAny, canDeleteRole, canEditRole,

  */

/////////////////////////////////
// Panel which shows roles list
/////////////////////////////////
orgModule.roleManager.roleList = {
    CE_LIST_TEMPLATE_ID: "tabular_oneColumn:roles",
    CE_ITEM_TEMPLATE_ID: "tabular_oneColumn:roles:leaf",
    PRO_LIST_TEMPLATE_ID: "tabular_twoColumn:roles",
    PRO_ITEM_TEMPLATE_ID: "tabular_twoColumn:roles:leaf",

    ROLE_ID_PATTERN: ".ID",
    ROLE_ORGANIZATION_PATTERN: ".organization",

    initialize: function(options) {
        orgModule.entityList.initialize({
            listTemplateId: (isProVersion()) ? this.PRO_LIST_TEMPLATE_ID : this.CE_LIST_TEMPLATE_ID,
            itemTemplateId: (isProVersion()) ? this.PRO_ITEM_TEMPLATE_ID : this.CE_ITEM_TEMPLATE_ID,
            toolbarModel: options.toolbarModel,
            text: options.text
        });

        orgModule.entityList._createEntityItem = function(value) {
            var item = new dynamicList.ListItem({
                label: value.roleName,
                value: value
            });

            item.processTemplate = function(element) {
                var id = element.select(orgModule.roleManager.roleList.ROLE_ID_PATTERN)[0];
                id.update(xssUtil.escape(this.getValue().roleName));

                var tenantId = this.getValue().tenantId;
                if (isProVersion() && tenantId) {
                    var org = element.select(orgModule.roleManager.roleList.ROLE_ORGANIZATION_PATTERN)[0];
                    org.update(xssUtil.escape(tenantId));
                }

                return element;
            };

            return item;
        }

    }
};

////////////////////////////////////////
// Panel which shows role's properties
////////////////////////////////////////
orgModule.roleManager.properties = {
    ROLE_NAME_PATTERN: "#roleName",
    EXTERNAL_ROLE_PATTERN: "#externalRole",

    role: null,

    initialize: function() {

        orgModule.properties.initialize({
            viewAssignedListTemplateDomId: "list_responsive_fields",
            viewAssignedItemTemplateDomId: "list_responsive_fields:leaf",
            searchAssigned: true,
            showAssigned: true
        });

        var panel = $(orgModule.properties._id);

        this.roleName = panel.select(this.ROLE_NAME_PATTERN)[0];
        this.external = panel.select(this.EXTERNAL_ROLE_PATTERN)[0];

        this.roleName.regExp = new RegExp(orgModule.Configuration.roleNameNotSupportedSymbols);
        this.roleName.unsupportedSymbols =
                new RegExpRepresenter(orgModule.Configuration.roleNameNotSupportedSymbols).getRepresentedString();

        this.roleName.inputValidator = orgModule.createInputRegExValidator(this.roleName);

        this._validators = [
            orgModule.createBlankValidator(this.roleName, "roleNameIsEmpty"),
            this.roleName.inputValidator
        ];

        this._initCustomEvents();

        orgModule.properties.setProperties = function(properties) {
            var rmProperties = orgModule.roleManager.properties;

            rmProperties.roleName.setValue(properties.roleName);
            rmProperties.external.checked = properties.external;

            if (properties.external) {
                rmProperties.external.up("fieldset").removeClassName(layoutModule.HIDDEN_CLASS);
            } else {
                rmProperties.external.up("fieldset").addClassName(layoutModule.HIDDEN_CLASS);
            }

            invokeServerAction(orgModule.ActionMap.SEARCH_ASSIGNED, {
                text: ""
            });
        };

        orgModule.properties._deleteEntity = function() {
            invokeClientAction(orgModule.ActionMap.DELETE, {entity: this._value});
        };

        orgModule.properties._editEntity = function() {
            var rmProperties = orgModule.roleManager.properties;

            this.resetValidation([rmProperties.ROLE_NAME_PATTERN]);
            this.changeReadonly(true, [rmProperties.ROLE_NAME_PATTERN]);
        };

        orgModule.properties._showEntity = function() {
            var rmProperties = orgModule.roleManager.properties;

            this.resetValidation([rmProperties.ROLE_NAME_PATTERN]);
            this.changeReadonly(false, [rmProperties.ROLE_NAME_PATTERN]);
        };

        orgModule.properties.validate = function() {
            var rmProperties = orgModule.roleManager.properties;

            var role = rmProperties._toRole(this._value.tenantId);

            if (role.roleName == this._value.roleName) {
                this.save(role);
            } else {
                return ValidationModule.validateLegacy(rmProperties._validators) && invokeServerAction(orgModule.ActionMap.EXIST, {
                    entity: role,
                    onExist: function() {
                        ValidationModule.showError(rmProperties.roleName, orgModule.messages['roleNameIsAlreadyInUse']);
                    }.bind(this),
                    onNotExist: function() {
                        ValidationModule.hideError(rmProperties.roleName);
                        this.save(role);
                    }.bind(this)
                });
            }
        };

        orgModule.properties.isChanged = function() {
            var rmProperties = orgModule.roleManager.properties;

            var oldRole = this._value;
            var role = rmProperties._toRole();

            return this.isEditMode && (oldRole.roleName != role.roleName ||
                    this.getAssignedEntities().length > 0 || this.getUnassignedEntities().length > 0);
        };

        orgModule.properties.save = function(role) {
            invokeServerAction("update", {
                entityName: this._value.getNameWithTenant(),
                entity: role,
                assigned: this.getAssignedEntities(),
                unassigned: this.getUnassignedEntities()
            });
        };

        orgModule.properties.cancel = function() {
            var dfd = new jQuery.Deferred();
            this.setProperties(this._value);
            return dfd.resolve();
        };

        orgModule.properties.canEdit = function() {
            if (this._value == null) {
                return true;
            } else {
                return canEditRole(this._value);
            }
        };

        orgModule.properties.canDelete = function() {
            if (this._value == null) {
                return true;
            } else {
                return canDeleteRole(this._value);
            }
        };

    },

    _initCustomEvents: function(roles) {
        var panel = $(orgModule.properties._id),
            $activeElements = jQuery("#moveButtons, #userEnable"),
            $availableAndAssignedEl = jQuery("#editUsers").find("#assigned, #available");


        panel.observe('keyup', function(event) {
            var input = event.element();

            if (input == this.roleName) {
                ValidationModule.validateLegacy([input.inputValidator]);
                event.stop();
            }

            orgModule.properties._toggleButton();

        }.bindAsEventListener(this));

        $activeElements.on('click', function(e) {
            orgModule.properties._toggleButton();
        });

        $availableAndAssignedEl.on('dblclick', function() {
            orgModule.properties._toggleButton();
        });
    },

    _toRole: function(tenantId) {
        var panel = $(orgModule.properties._id);

        var role = new orgModule.Role({
            roleName: panel.select(this.ROLE_NAME_PATTERN)[0].getValue()
        });

        if(tenantId) {
            role.tenantId = tenantId;
        }

        role.users = orgModule.properties.getAssignedEntities();

        return role;
    }
};

////////////////////////////////////////
// Create role dialog
////////////////////////////////////////
orgModule.roleManager.addDialog = {
    ADD_ROLE_ID: "addRole",
    ADD_ROLE_BUTTON_ID: "addRoleBtn",
    CANCEL_ROLE_BUTTON_ID: "cancelAddRoleBtn",
    ADD_ROLE_NAME_ID: "addRoleName",

    ADD_ROLE_BUTTON_TITLE_PATTERN: ".wrap",

    initialize: function() {
        this.addRole = $(this.ADD_ROLE_ID);
        this.addBtn = $(this.ADD_ROLE_BUTTON_ID);
        this.cancelBtn = $(this.CANCEL_ROLE_BUTTON_ID);
        this.roleName = $(this.ADD_ROLE_NAME_ID);

        this.roleName.regExp = new RegExp(orgModule.Configuration.roleNameNotSupportedSymbols);
        this.roleName.unsupportedSymbols =
                new RegExpRepresenter(orgModule.Configuration.roleNameNotSupportedSymbols).getRepresentedString();

        this.roleName.inputValidator = orgModule.createInputRegExValidator(this.roleName);

        this._validators = [
            orgModule.createBlankValidator(this.roleName, "roleNameIsEmpty"),
            this.roleName.inputValidator
        ];

        this.addRole.observe('keyup', function(event) {
            var input = event.element();

            if (input == this.roleName) {
                ValidationModule.validateLegacy([input.inputValidator]);
                event.stop();
            }
        }.bindAsEventListener(this));

        this.addRole.observe('click', function(event) {
            var button = matchAny(event.element(), [layoutModule.BUTTON_PATTERN], true);

            if (button == this.addBtn) {
                this._doAdd();
            } else if(button == this.cancelBtn) {
                this.hide();
            }
        }.bindAsEventListener(this));
    },

    show: function(organization) {
        this.organization = organization;

        ValidationModule.hideError(this.roleName);

        var title = this.addBtn.select(this.ADD_ROLE_BUTTON_TITLE_PATTERN)[0];
        if (title) {
            var msg = (this.organization && !this.organization.isRoot()) ?
                    orgModule.getMessage('addRoleTo', {
                        organizationName: orgModule.truncateOrgName(this.organization.id)
                    }) :
                    orgModule.getMessage('addRole');

            title.update(msg);
        }

        this.addBtn.title = (this.organization && !this.organization.isRoot()) ?
                    orgModule.getMessage('addRoleTo', { organizationName: this.organization.id }) :
                    orgModule.getMessage('addRole');

        dialogs.popup.show(this.addRole, true);

        try {
            this.roleName.focus();
        } catch(e) {}
    },

    hide: function() {
        dialogs.popup.hide(this.addRole);
        this.roleName.setValue("");
    },

    _validate: function() {
        return ValidationModule.validateLegacy(this._validators);
    },

    _doAdd:function() {
        if (this._validate()) {
            var role = new orgModule.Role({
                roleName: this.roleName.getValue()
            });

            if (this.organization && !this.organization.isRoot()) { role.tenantId = this.organization.id; }

            invokeServerAction(orgModule.ActionMap.EXIST, {
                entity: role,
                onExist: function() {
                    ValidationModule.showError(this.roleName, orgModule.messages['roleNameIsAlreadyInUse']);
                }.bind(this),
                onNotExist: function() {
                    ValidationModule.hideError(this.roleName);

                    invokeServerAction(orgModule.ActionMap.CREATE, {
                        entity: role
                    });
                }.bind(this)
            });
        }
    }
};

