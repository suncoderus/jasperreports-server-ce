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

/* global repositorySearch, SearchBox, toolbarButtonModule, toFunction, getAsFunction, localContext, isArray, JSCookie,
 dynamicTree, disableSelectionWithoutCursorStyle, getBoxOffsets, actionModel, Folder, isMetaHeld, canFolderBeCopied,
 invokeFolderAction, layoutModule, canFolderBeMoved, Droppables, canFolderBeCopiedOrMovedToFolder,
 canAllBeCopiedOrMovedToFolder, Draggables, alert, dynamicList, isIPad, isSupportsTouch, TouchController, InfiniteScroll,
 canBeRun, canBeOpened, JSTooltip, invokeBulkAction, invokeRedirectAction, canBeScheduled, matchAny, centerElement,
 tooltipModule, $break, baseList, dialogs, buttonManager, ValidationModule, ResourcesUtils,
 accessibilityModule, confirm, fileSender, Template, primaryNavModule, deepClone, invokeClientAction, invokeServerAction,
 jaspersoft, $$, doNothing, ajaxTargettedUpdate, baseErrorHandler, AjaxRequester, XRegExp, $A
 */

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Main module for manege organizations, users and roles
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var orgModule = {
    TREE_ID: "orgTree",
    COOKIE_NAME: 'selectedTenant',
    messages: [],

    ActionMap: {
        SEARCH: 'search',
        BROWSE: 'browse',
        NEXT: 'next',

        SELECT_AND_GET_DETAILS: "selectAndGetDetails",
        SEARCH_AVAILABLE: "searchAvailable",
        SEARCH_ASSIGNED: "searchAssigned",
        NEXT_AVAILABLE: "nextAvailable",
        NEXT_ASSIGNED: "nextAssigned",

        CREATE: 'create',
        UPDATE: 'update',
        DELETE: 'delete',
        DELETE_ALL: 'deleteAll',

        EXIST: 'exist'
    },

    Event: {
        /** Request events. */
        ORG_BROWSE: 'org:browse',
        ENTITY_SEARCH: 'entity:search',
        ENTITY_NEXT: 'entity:next',
        ENTITY_SELECT_AND_GET_DETAILS: 'entity:selectAndGetDetails',

        /** Response events. */
        RESULT_CHANGED: 'result:changed',
        RESULT_NEXT: 'result:next',

        ENTITY_DETAILS_LOADED: 'entity:detailsLoaded',
        SEARCH_AVAILABLE_LOADED: 'searchAvailable:loaded',
        SEARCH_ASSIGNED_LOADED: 'searchAssigned:loaded',
        NEXT_AVAILABLE_LOADED: 'nextAvailable:loaded',
        NEXT_ASSIGNED_LOADED: 'nextAssigned:loaded',

        SERVER_ERROR: 'server:error',
        SERVER_UNAVAILABLE: 'server:unavailable',

        ENTITY_CREATED: "entity:created",
        ENTITY_UPDATED: "entity:updated",
        ENTITY_DELETED: "entity:deleted",
        ENTITIES_DELETED: "entities:deleted"
    },

    observe: function(eventName, handler) {
        this._getContainer().observe(eventName, handler);
    },

    stopObserving: function(eventName, handler) {
        this._getContainer().stopObserving(eventName, handler);
    },

    fire: function(eventName, memo) {
        this._getContainer().fire(eventName, memo);
    },

    getMessage: function(messageId, options) {
        var message = orgModule.messages[messageId];
        return message ? new Template(message).evaluate(options ? options : {}) : "";
    },

    _comparator: function(l1, l2) {
        return l1 > l2 ? 1 : (l1 < l2 ? -1 : 0);
    },

    _getContainer: function() {
        if (!this._container) {
            this._container = document.body;
        }
        return this._container;
    },

    Configuration: {
        nameSeparator: "|",
        userDefaultRole: "ROLE_USER",
        userNameNotSupportedSymbols: "[\|]", // jshint ignore: line
        roleNameNotSupportedSymbols: "[\|]", // jshint ignore: line
        emailRegExpPattern: "^[\\p{L}\\p{M}\\p{N}._%'-\\@\\,\\;\\s]+$",
        superuserRole: "ROLE_SUPERUSER",
        adminRole: "ROLE_ADMINISTRATOR",
        anonymousRole: "ROLE_ANONYMOUS"
    }
};

orgModule.systemRoles = [
    orgModule.Configuration.userDefaultRole,
    orgModule.Configuration.superuserRole,
    orgModule.Configuration.adminRole,
    orgModule.Configuration.anonymousRole
];

/**
 * This Class represents tenant in JS
 *
 * @param node {@link dynamicTree.TreeNode}
 */
orgModule.Organization = function(jsonOrNode) {
    if (!jsonOrNode) {
        throw new Error("Can't create Organization from undefined json or node");
    }
    if (jsonOrNode.param) {
        this.id = jsonOrNode.param.id;
        this.name = jsonOrNode.name;
        this.uri = jsonOrNode.param.uri;
        this.treeNode = jsonOrNode;
    } else {
        this.id = jsonOrNode.tenantId || jsonOrNode.id;
        this.name = jsonOrNode.tenantName;
        this.alias = jsonOrNode.tenantAlias || jsonOrNode.alias;
        this.desc = jsonOrNode.tenantDesc;
        this.uri = jsonOrNode.tenantUri;
        this.parentId = jsonOrNode.parentId;
        this.subTenantCount = jsonOrNode.subTenantCount;
    }
};

orgModule.Organization.addMethod('isRoot', function(level) {
    // TODO: load name of root organization from configuration.
    return this.id == 'organizations';
});

orgModule.Organization.addMethod('getNameWithTenant', function() {
    return this.id;
});

orgModule.Organization.addMethod('getDisplayName', function() {
    return this.name;
});

orgModule.Organization.addMethod('equals', function(org) {
    return org && this.id == org.id;
});

orgModule.Organization.addMethod('toJSON', function(org) {
    return {
        tenantId: this.id,
        tenantName: this.name,
        tenantAlias: this.alias,
        tenantDesc: this.desc,
        parentId: this.parentId,
        tenantUri: this.uri
    };
});

orgModule.Organization.addMethod('navigateToManager', function() {
    primaryNavModule.navigationPaths.tempNavigateToManager = deepClone(primaryNavModule.navigationPaths.organization);
    primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
        tenantId: this.id
    });
    primaryNavModule.navigationOption("tempNavigateToManager");
});

orgModule.Permission = function(options) {
    this.isInherited = !!options.isInherited;
    if (options.permission) {
        this.permission = options.permission;
    }
    if (options.inheritedPermission) {
        this.inheritedPermission = options.inheritedPermission;
    }
    this.isDisabled = !!options.isDisabled;
};

orgModule.Permission.addMethod("getResolvedPermission", function() {
    return this.isInherited ? this.inheritedPermission : this.permission;
});

orgModule.Permission.addMethod("toJSON", function() {
    return {
        permission: this.permission,
        isInherited: this.isInherited,
        inheritedPermission: this.inheritedPermission,
        newPermission: this.newPermission
    };
});

orgModule.Permission.addMethod("toData", function() {
    return {
        permission: this.permission,
        isInherited: this.isInherited,
        inheritedPermission: this.inheritedPermission,
        newPermission: this.newPermission
    };
});

orgModule.User = function(options) {
    if (options) {
        this.userName = options.userName;
        this.fullName = options.fullName;
        this.password = options.password || "";
        this.confirmPassword = options.confirmPassword || "";
        this.tenantId = options.tenantId;
        this.email = options.email;
        this.enabled = options.enabled;
        this.external = options.external;

        this.roles = [];

        if (options.roles) {
            options.roles.each(function(role) {
                this.roles.push(new orgModule.Role(role));
            }.bind(this));
        }

        if (options.permissionToDisplay) {
            this.permission = new orgModule.Permission(options.permissionToDisplay);
        }
    }
};

orgModule.User.addVar('FLOW_ID', "userListFlow");

orgModule.User.addMethod('getDisplayName', function() {
    return this.userName;
});

orgModule.User.addMethod('getNameWithTenant', function() {
    if (this.tenantId && !this.tenantId.blank()) {
        return this.userName + orgModule.Configuration.userNameSeparator + this.tenantId;
    } else {
        return this.userName;
    }
});

orgModule.User.addMethod('getManagerURL', function() {
    return 'flow.html?' + Object.toQueryString({
        _flowId: this.FLOW_ID,
        text: typeof(this.userName) !== 'undefined' ? encodeURIComponent(this.userName) : this.userName,
        tenantId: typeof(this.tenantId) !== 'undefined' ? encodeURIComponent(this.tenantId) : this.tenantId
    });
});

orgModule.User.addMethod('navigateToManager', function() {
    primaryNavModule.navigationPaths.tempNavigateToManager = deepClone(primaryNavModule.navigationPaths.user);
    primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
        text: this.userName,
        tenantId: this.tenantId
    });
    primaryNavModule.navigationOption("tempNavigateToManager");
});

orgModule.User.addMethod('equals', function(user) {
    return user && this.userName == user.userName && this.tenantId == user.tenantId;
});

orgModule.User.addMethod('toPermissionData', function(user) {
    return {
        userName: this.userName,
        tenantId: this.tenantId,
        permissionToDisplay: this.permission.toData()
    };
});

orgModule.Role = function(options) {
    if (options) {
        this.roleName = options.roleName;
        this.external = options.external;
        this.tenantId = options.tenantId;
    }

    if (options.permissionToDisplay) {
        this.permission = new orgModule.Permission(options.permissionToDisplay);
    }
};

orgModule.Role.addVar('FLOW_ID', "roleListFlow");

orgModule.Role.addMethod('getDisplayName', function() {
    return this.roleName;
});

orgModule.Role.addMethod('getNameWithTenant', function() {
    if (this.tenantId && !this.tenantId.blank()) {
        return this.roleName + orgModule.Configuration.userNameSeparator + this.tenantId;
    } else {
        return this.roleName;
    }
});

orgModule.Role.addMethod('getManagerURL', function() {
    return 'flow.html?' + Object.toQueryString({
        _flowId: this.FLOW_ID,
        // Object.toQueryString already does encodeURIComponent() once, so we have double encoding here
        text: typeof(this.roleName) !== 'undefined' ? encodeURIComponent(this.roleName) : this.roleName,
        tenantId: typeof(this.tenantId) !== 'undefined' ? encodeURIComponent(this.tenantId) : this.tenantId
    });
});

orgModule.Role.addMethod('navigateToManager', function() {
    primaryNavModule.navigationPaths.tempNavigateToManager = deepClone(primaryNavModule.navigationPaths.role);
    primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
        text: this.roleName,
        tenantId: this.tenantId
    });
    primaryNavModule.navigationOption("tempNavigateToManager");
});

orgModule.Role.addMethod('equals', function(role) {
    return role && this.roleName == role.roleName && this.tenantId == role.tenantId;
});

orgModule.Role.addMethod('toPermissionData', function(user) {
    return {
        roleName: this.roleName,
        tenantId: this.tenantId,
        permissionToDisplay: this.permission.toData()
    };
});

orgModule.initTenantsTreeEvents = function() {
    orgModule.manager.tenantsTree.on("selection:change", function(selectedItem) {
        if (!orgModule.manager.lastSelectedOrg || orgModule.manager.lastSelectedOrg.id !== selectedItem.id) {
            var selectedTenant = new orgModule.Organization(selectedItem),
                properties = orgModule.properties;

            orgModule.manager.tenantsTree.setTenant(selectedTenant);

            window.localStorage && localStorage.setItem(orgModule.COOKIE_NAME, JSON.stringify(selectedItem));

            orgModule.fire(orgModule.Event.ORG_BROWSE, {
                organization: selectedTenant,
                entityEvent: false
            });

            if (orgModule.orgManager) {
                properties.changeDisable(false, ['#' + properties._EDIT_BUTTON_ID]);

                orgModule.fire(orgModule.Event.ENTITY_SELECT_AND_GET_DETAILS, {
                    entityId: selectedTenant.id,
                    entityEvent: false
                });
            }

            if (orgModule.userManager || orgModule.roleManager) {
                properties.hide();
                properties.unlock();
            }
        }
    });

    orgModule.manager.tenantsTree.on("import:finished", function(tenantId) {
        var  properties = orgModule.properties;

        orgModule.fire(orgModule.Event.ORG_BROWSE, {
            force: true,
            entityEvent: false
        });

        if (orgModule.orgManager) {
            properties.changeDisable(false, ['#' + properties._EDIT_BUTTON_ID]);

            orgModule.fire(orgModule.Event.ENTITY_SELECT_AND_GET_DETAILS, {
                refreshAttributes: true,
                entityId: tenantId,
                entityEvent: false
            });
        }
    });
};

orgModule.entityList = {
    ID_PATTERN: ".ID > a",

    _listId: "entitiesList",
    _containerId: "listContainer",
    _searchBoxId: "secondarySearchBox",

    initialize: function(options) {
        this.list = new dynamicList.List(this._listId, {
            listTemplateDomId: options.listTemplateId,
            itemTemplateDomId: options.itemTemplateId,
            multiSelect: true,
            selectionDefaultsToCursor: true,
            comparator: function(item1, item2) {
                return orgModule._comparator(item1.getLabel(), item2.getLabel());
            }
        });

        this._initEvents();

        // Infinite scroll setup.
        this._infiniteScroll = new InfiniteScroll({
            id: this._containerId,
            contentId: this._listId
        });

        this._searchBox = new SearchBox({
            id: this._searchBoxId
        });
        if (options.text) {
            this._searchBox.setText(options.text);
        }

        this._infiniteScroll.onLoad = function() {
            orgModule.fire(orgModule.Event.ENTITY_NEXT, {});
        };
        this._searchBox.onSearch = function(text) {
            orgModule.fire(orgModule.Event.ENTITY_SEARCH, {text: text});
        };

        orgModule.toolbar.initialize(options.toolbarModel);
        this.toolbar = orgModule.toolbar;

        this.list.show();
    },

    _initEvents: function() {
        this.list.observe('item:selected', function(event) {
            var item = event.memo.item,
                properties = orgModule.properties;

            this.lastSelectedName = item.getValue().getNameWithTenant();

            orgModule.fire(orgModule.Event.ENTITY_SELECT_AND_GET_DETAILS, {entityId: item.getValue().id, entityEvent: true});

            properties.changeDisable(false, ['#' + properties._EDIT_BUTTON_ID]);
        }.bindAsEventListener(this));

        this.list.observe('item:unselected', function(event) {
            var tenantsTree = orgModule.manager.tenantsTree,
                properties = orgModule.properties;

            properties.changeDisable(false, ['#' + properties._EDIT_BUTTON_ID]);
            // Due to complexity of code in mng.common (event flow) there is need to handle some cases in such inappropriate manner
            // This need to by refactored once mng.common will be refactored in new AMD way + new tree.
            if (!properties.locked && tenantsTree) {
                orgModule.fire(orgModule.Event.ENTITY_SELECT_AND_GET_DETAILS, {
                    entityId: tenantsTree.getTenant().id,
                    entityEvent: true,
                    isCtrlHeld: event.memo.isCtrlHeld
                });
            } else {
                properties.hide();
                this.toolbar.refresh();
            }

        }.bindAsEventListener(this));

        this.list.observe('item:beforeSelectOrUnselect', function(event) {
            var item = event.memo.item;
            event.stopSelectOrUnselect = !invokeClientAction("cancelIfEdit", {entity: item.getValue(), showConfirm: event.memo.showConfirm});
        }.bindAsEventListener(this));
    },

//    _refreshProperties: function() {
//        if (this.list.getSelectedItems().length == 1) {
//            orgModule.properties.show(this.list.getSelectedItems()[0].getValue());
//        } else {
//            orgModule.properties.hide();
//        }
//    },

    _createEntityItem: function(value) {
        var item = new dynamicList.ListItem({
            label: value.getDisplayName(),
            value: value
        });

        var entityList = this;
        item.processTemplate = function(element) {
            var id = element.select(entityList.ID_PATTERN)[0];
            id.update(xssUtil.escape(this.getValue().getDisplayName()));
            return element;
        };

        return item;
    },

    getSearchText: function() {
        return this._searchBox.getText();
    },

    setSearchText: function(text) {
        return this._searchBox.setText(text);
    },

    getSelectedEntities: function() {
        if (this.list) {
            return this.list.getSelectedItems().collect(function(item) {
                return item.getValue();
            });
        } else {
            return [];
        }
    },

    setEntities: function(entities) {
        var items = entities.collect(this._createEntityItem.bind(this));

        this._infiniteScroll.reset();

        this.list.setItems(items);
        this.list.show();

        this.toolbar.refresh();
    },

    findEntity: function(name) {
        var items = this.list.getItems(),
            options = orgModule.properties.options || orgModule.userManager.options;

        return options._.find(items, function(item) {
            return item.getValue().fullName === name;
        });
    },

    addEntities: function(entities) {
        var items = entities.collect(this._createEntityItem.bind(this));

        this.list.addItems(items);
        this.list.refresh();

        this.toolbar.refresh();
    },

    selectEntity: function(entityName) {
        var all = this.list.getItems();

        var matched = all.detect(function(item) {
            return item.getValue().getNameWithTenant() == entityName;
        });

        if (matched) {
            matched.isSelected() && matched.deselect();
            matched.select();
            this.lastSelectedName = matched.getValue().getNameWithTenant();
        }
    },

    restoreSelectedEntity: function(entityName) {
        this.selectEntity(this.lastSelectedName ? this.lastSelectedName : entityName);
    },

    deselectAll: function() {
        this.list.resetSelected();
    },

    update: function(entityName, newEntity) {
        var all = this.list.getItems();
        var matched = all.findAll(function(item) {
            return item.getValue().getNameWithTenant() == entityName;
        });

        matched.each(function(item) {
            item.setValue(newEntity);
            item.refresh();
        });
    },

    remove: function(entityNameOrNameSet) {
        var all = this.list.getItems();

        var matched;

        if (isArray(entityNameOrNameSet)) {
            matched = all.findAll(function(item) {
                return entityNameOrNameSet.include(item.getValue().getNameWithTenant());
            });
        } else {
            matched = all.findAll(function(item) {
                return item.getValue().getNameWithTenant() == entityNameOrNameSet;
            });
        }

        this.list.resetSelected();
        this.list.removeItems(matched);
    }
};

orgModule.properties = {
    _EDIT_MODE_CLASS: "editMode",
    _moveButtonsId: "moveButtons",

    _id: "properties",
    _value: null,
    isEditMode: false,

    _NOTHING_TO_DISPLAY_ID: 'nothingToDisplay',

    _ASSIGNED_VIEW_ID: "assignedView",
    _ASSIGNED_VIEW_LIST_ID: "assignedViewList",
    _ASSIGNED_ID: "assigned",
    _AVAILABLE_ID: "available",
    _ASSIGNED_LIST_ID: "assignedList",
    _AVAILABLE_LIST_ID: "availableList",
    _ATTRIBUTES_LIST_ID: "attributesTab",

    _EDIT_BUTTON_ID: "edit",
    _SAVE_BUTTON_ID: "save",
    _CANCEL_BUTTON_ID: "cancel",
    _DELETE_BUTTON_ID: "delete",
    _REMOVE_FROM_ASSIGNED_BUTTON_ID: "removeFromAssigned",
    _ADD_TO_ASSIGNED_BUTTON_ID: "addToAssigned",

    _BUTTONS_CONTAINER_ID: "propertiesButtons",
    _MOVE_BUTTONS_CONTAINER_ID: "moveButtons",

    _DND_CLASS: ".draggable",
    _DROP_CLASS: "wrap",

    buttonsFunctions: {},

    initialize: function(options) {
        var attributesTypesEnum = options.attributesTypesEnum;

        this.options = options;

        this.locked = false;

        this.hide();
        this.processTemplate(options);
        this._toggleButton();

        if (options.attributes) {
            var attributesType = !orgModule.userManager && orgModule.orgManager ? attributesTypesEnum.TENANT : attributesTypesEnum.USER,
                optionsFactory = options.attributesViewOptionsFactory,
                scrollEventTrait = options.scrollEventTrait,
                AttributesViewFacade = options.AttributesViewFacade;

            this.options._.extend(this, scrollEventTrait);

            this.attributesFacade = new AttributesViewFacade(optionsFactory({context: options.attributes.context,
                container: jQuery("#attributesTab"), type: attributesType}));
        }

        if (this.options.showAssigned) {
            this.initDnD();
        }
        this.initEvents();
        this.initButtonsFunctions();
        this.options.ConfirmationDialog && this._initConfirmationDialog();
    },

    _initConfirmationDialog: function() {
        var self = this,
            i18n = this.options.i18n;

        this.confirmationDialog = new this.options.ConfirmationDialog({
            title: i18n["attributes.confirm.dialog.title"],
            text: i18n["attributes.confirm.cancel.dialog.text"]
        });

        this.confirmationDialog.on("button:yes", function() {
            self.cancelOnEdit();
        });
    },

    _toggleButton: function() {
        var propertiesIsChanged = this.isChanged(),
            attributesAndPropertiesChanged = this.attributesFacade && this.attributesFacade.containsUnsavedItems() || propertiesIsChanged;

        attributesAndPropertiesChanged
            ? this.saveButton.removeAttribute("disabled")
            : this.saveButton.removeClassName("over").setAttribute("disabled", "disabled");
    },

    show: function(value, refreshAttributes) {
        if (!this.locked) {
            if (this.attributesFacade) {
                var self = this,
                    isRoot = value && value.isRoot && value.isRoot(),
                    context = !isRoot ? value : this.options._.extend({}, value, {id: null});

                this.attributesFacade.getCurrentView().setContext(context, refreshAttributes)
                    .done(function() {
                        self.attributesFacade.render(isRoot);
                    });
            }

            var nothingToDisplay = $(this._NOTHING_TO_DISPLAY_ID);
            nothingToDisplay.addClassName(layoutModule.HIDDEN_CLASS);
            document.body.removeClassName(layoutModule.NOTHING_TO_DISPLAY_CLASS);

            this._value = value;
            this.changeMode(false);
        }
    },

    hide: function() {
        if (!this.locked) {
            var nothingToDisplay = $(this._NOTHING_TO_DISPLAY_ID);
            nothingToDisplay.removeClassName(layoutModule.HIDDEN_CLASS);
            centerElement(nothingToDisplay, {horz: true, vert: true});
            document.body.addClassName(layoutModule.NOTHING_TO_DISPLAY_CLASS);

            this._value = null;
            this.changeMode(false);
        }
    },

    lock: function() {
        this.locked = true;
    },

    unlock: function() {
        this.locked = false;
    },

    setDetailsLoadedEntity: function(entity) {
        this.detailsLoadedEntity = entity;
    },

    getDetailsLoadedEntity: function() {
        return this.detailsLoadedEntity;
    },

    processTemplate: function(options) {
        this.editButton = $(this._EDIT_BUTTON_ID);
        this.saveButton = $(this._SAVE_BUTTON_ID);
        this.cancelButton = $(this._CANCEL_BUTTON_ID);
        this.deleteButton = $(this._DELETE_BUTTON_ID);

        if (this.options.showAssigned) {
            this.removeFromAssigned = $(this._REMOVE_FROM_ASSIGNED_BUTTON_ID);
            this.addToAssigned = $(this._ADD_TO_ASSIGNED_BUTTON_ID);

            this.assignedViewList = new dynamicList.List(this._ASSIGNED_VIEW_LIST_ID, {
                listTemplateDomId: options.viewAssignedListTemplateDomId,
                itemTemplateDomId: options.viewAssignedItemTemplateDomId,
                comparator: this.assignedComparator,
                allowSelections: false,
                selectionDefaultsToCursor: false
            });

            var commonOptions = {
                listTemplateDomId: "list_responsive_collapsible_fields",
                itemTemplateDomId: "list_responsive_collapsible_fields:fields",
                comparator: this.assignedComparator,
                dragPattern: this._DND_CLASS,
                allowSelections: true,
                selectionDefaultsToCursor: true,
                multiSelect: true,
                selectOnMousedown: true,
                setCursorOnMouseDown: true
            };

            this.assignedList = new dynamicList.List(this._ASSIGNED_LIST_ID,
                Object.extend({}, commonOptions,
                    {
                        allowSelections: false,
                        selectionDefaultsToCursor: false
                        //selectOnMouseDown: false,
                        //setCursorOnMouseDown: false
                    }));
            this.availableList = new dynamicList.List(this._AVAILABLE_LIST_ID, commonOptions);

//            this.assignedList.DND_WRAPPER_TEMPLATE = "column_two";
//            this.assignedList.DND_ITEM_TEMPLATE = "column_two:resourceName";
//            this.availableList.DND_WRAPPER_TEMPLATE = "column_two";
//            this.availableList.DND_ITEM_TEMPLATE = "column_two:resourceName";

            if (options.searchAssigned) {
                this.assignedViewSearchBox = new SearchBox({
                    id: $(this._ASSIGNED_VIEW_ID).select(layoutModule.SEARCH_LOCKUP_PATTERN)[0].identify()
                });
                this.assignedViewSearchBox.onSearch = function(text) {
                    invokeServerAction(orgModule.ActionMap.SEARCH_ASSIGNED, {
                        text: text
                    });
                };
            }

            this.availableSearchBox = new SearchBox({
                id: $(this._AVAILABLE_ID).select(layoutModule.SEARCH_LOCKUP_PATTERN)[0].identify()
            });
            this.availableSearchBox.onSearch = function(text) {
                invokeServerAction(orgModule.ActionMap.SEARCH_AVAILABLE, {
                    text: text
                });
            };

            this.assignedSearchBox = new SearchBox({
                id: $(this._ASSIGNED_ID).select(layoutModule.SEARCH_LOCKUP_PATTERN)[0].identify()
            });
            this.assignedSearchBox.onSearch = function(text) {
                invokeServerAction(orgModule.ActionMap.SEARCH_ASSIGNED, {
                    text: text
                });
            };

            if (options.searchAssigned) {
                this.assignedViewInfiniteScroll = new InfiniteScroll({
                    id: $($(this._ASSIGNED_VIEW_LIST_ID).parentNode).identify(),
                    contentId: this._ASSIGNED_VIEW_LIST_ID
                });
                this.assignedViewInfiniteScroll.onLoad = function() {
                    invokeServerAction(orgModule.ActionMap.NEXT_ASSIGNED, {});
                };
            }

            this.availableInfiniteScroll = new InfiniteScroll({
                id: $($(this._AVAILABLE_LIST_ID).parentNode).identify(),
                contentId: this._AVAILABLE_LIST_ID
            });
            this.availableInfiniteScroll.onLoad = function() {
                invokeServerAction(orgModule.ActionMap.NEXT_AVAILABLE, {});
            };

            this.assignedInfiniteScroll = new InfiniteScroll({
                id: $($(this._ASSIGNED_LIST_ID).parentNode).identify(),
                contentId: this._ASSIGNED_LIST_ID
            });
            this.assignedInfiniteScroll.onLoad = function() {
                invokeServerAction(orgModule.ActionMap.NEXT_ASSIGNED, {});
            };
        }
    },

    initDnD: function() {
        Droppables.add($(this._ASSIGNED_LIST_ID).up(), {
            accept: [this._DROP_CLASS],
            hoverclass: layoutModule.DROP_TARGET_CLASS,
            onDrop: (function(dragged, dropped, event) {
                if (dragged.items) {
                    this._addToAssigned();
                }
            }).bind(this)
        });
        Droppables.add($(this._AVAILABLE_LIST_ID).up(), {
            accept: [this._DROP_CLASS],
            hoverclass: layoutModule.DROP_TARGET_CLASS,
            onDrop: (function(dragged, dropped, event) {
                if (dragged.items) {
                    this._removeFromAssigned();
                }
            }).bind(this)
        });
    },

    initEvents: function() {

        function assignedSelectionHandler() {
            ((this.assignedList.getSelectedItems().length > 0) ?
                buttonManager.enable : buttonManager.disable)(this.removeFromAssigned);
        }

        function availableSelectionHandler() {
            ((this.availableList.getSelectedItems().length > 0) ?
                buttonManager.enable : buttonManager.disable)(this.addToAssigned);
        }

        $(this._BUTTONS_CONTAINER_ID).observe('click', function(event) {
            var element = event.element();

            var button = matchAny(element, [layoutModule.BUTTON_PATTERN], true);
            if (button) {
                if (this.buttonsFunctions[button.identify()]) {
                    this.buttonsFunctions[button.identify()].call(this);
                }
            }

            event.stop();
        }.bindAsEventListener(this));

        if (this.options.showAssigned) {
            $(this._MOVE_BUTTONS_CONTAINER_ID).observe('click', function(event) {
                var element = event.element();

                var button = matchAny(element, [layoutModule.BUTTON_PATTERN], true);
                if (button) {
                    if (button == this.removeFromAssigned) {
                        this._removeFromAssigned();
                    } else if (button == this.addToAssigned) {
                        this._addToAssigned();
                    }
                }

                event.stop();
            }.bindAsEventListener(this));


            this.assignedList.observe("item:selected", assignedSelectionHandler.bindAsEventListener(this));
            this.assignedList.observe("item:unselected", assignedSelectionHandler.bindAsEventListener(this));
            this.assignedList.observe("item:dblclick", function(event) {
                this._removeFromAssigned();
            }.bindAsEventListener(this));


            this.availableList.observe("item:selected", availableSelectionHandler.bindAsEventListener(this));
            this.availableList.observe("item:unselected", availableSelectionHandler.bindAsEventListener(this));
            this.availableList.observe("item:dblclick", function(event) {
                this._addToAssigned();
            }.bindAsEventListener(this));
        }

        this.attributesFacade &&
        this.attributesFacade.on("change", this.options._.bind(this._toggleButton, this));

    },

    initButtonsFunctions: function() {
        this.buttonsFunctions[this._EDIT_BUTTON_ID] = function() {
            this.changeMode(true);
        };

        this.buttonsFunctions[this._SAVE_BUTTON_ID] = function() {
            var extObj = this;
            var dfd = new jQuery.Deferred();
            if (this.validate()) {
                this.attributesFacade ? this.attributesFacade.getCurrentView().saveChildren().done(function() {
                    dfd.resolve()
                }).fail(function() {
                    dfd.reject();
                    jQuery(".tab[tabid='#attributesTab']").trigger("mouseup");
                }) : dfd.resolve();

                dfd.done(function() {
                    extObj.save(extObj._value);
                });
            }
        };

        this.buttonsFunctions[this._CANCEL_BUTTON_ID] = function() {
            if (this.attributesFacade) {
                this.isChanged()
                    ? this.confirmationDialog.open()
                    : this.cancelOnEdit();
            } else {
                invokeClientAction("cancelIfEdit", {entity: this.getValue()});
            }
        };

        this.buttonsFunctions[this._DELETE_BUTTON_ID] = function() {
            this._deleteEntity();
        };

        this.buttonsFunctions[this._REMOVE_FROM_ASSIGNED_BUTTON_ID] = function() {
            this._removeFromAssigned();
        };

        this.buttonsFunctions[this._ADD_TO_ASSIGNED_BUTTON_ID] = function() {
            this._addFromAssigned();
        };
    },

    cancelOnEdit: function() {
        var self = this;

        if (this.isEditMode) {
            this.cancel().done(function() {
                self.unlock();
                self.changeMode(false);
                self._toggleButton();
            });
        }
    },

    changeMode: function(edit) {
        this.isEditMode = edit;

        var tabs, attributeTab, propertiesTab, $anchor;
        if (this.attributesFacade) {
            attributeTab = $("attributesTab");

            var hideFilters = !!this._value && this._value.id === "organizations";

            this.detachScrollEvent(this.attributesFacade.getCurrentView().$container.parent());

            this.attributesFacade.toggleMode(edit, hideFilters);

            edit && this.initScrollEvent(this.attributesFacade.getCurrentView());

            tabs = $$(".tertiary")[0];
            propertiesTab = $("propertiesTab");
            $anchor = jQuery(".control.tabSet.anchor");
        }

        if (edit) {
            $(this._id).addClassName(this._EDIT_MODE_CLASS);

            if (tabs) {
                tabs.down(".selected").removeClassName("selected");
                tabs.down("li", 0).addClassName("selected");
                tabs.addClassName("tabbed");
                attributeTab.addClassName("hidden");
                propertiesTab.removeClassName("hidden");
                $anchor.removeClass("attributesAnchor")
            }

            this._assigned = [];
            this._unassigned = [];

            if (this.options.showAssigned) {
                centerElement($(this._moveButtonsId), {horz: true});
                $(this._moveButtonsId).style.zIndex = 1000; //fix for bug 26831

                this.assignedList.setItems([]);
                this.assignedList.show();

                this.availableList.setItems([]);
                this.availableList.show();

                buttonManager.disable(this.removeFromAssigned);
                buttonManager.disable(this.addToAssigned);

                this.assignedSearchBox.setText("");
                this.availableSearchBox.setText("");

                invokeServerAction(orgModule.ActionMap.SEARCH_ASSIGNED, {
                    text: this.assignedSearchBox.getText()
                });
                invokeServerAction(orgModule.ActionMap.SEARCH_AVAILABLE, {
                    text: this.availableSearchBox.getText()
                });
            }
            this._editEntity();

            this._toggleButton();
        } else {
            $(this._id).removeClassName(this._EDIT_MODE_CLASS);

            if (tabs) {
                attributeTab.removeClassName("hidden");
                propertiesTab.removeClassName("hidden");
                tabs.removeClassName("tabbed");
            }

            if (this.options.searchAssigned && this.assignedViewSearchBox) {
                this.assignedViewSearchBox.setText("");
            }

            this.changeDisable(this.canEdit(), ['#' + this._EDIT_BUTTON_ID]);
            this.changeDisable(this.canDelete(), ['#' + this._DELETE_BUTTON_ID]);
            this._showEntity();
            this._assigned = [];
            this._unassigned = [];
        }
    },

    canEdit: function() {
        return true;
    },

    canDelete: function() {
        var org = orgModule.manager.tree && orgModule.manager.tree.getOrganization();

        if (org && org.uri === "/" && !orgModule.entityList.getSelectedEntities().length) {
            return false;
        }

        return true;
    },

    setValuesProperty: function(name, value) {
        this._value[name] = value;
    },

    getValue: function() {
        return this._value;
    },

    setProperties: function(properties) {
        // Template method.
    },

    isChanged: function(properties) {
        // Template method.
    },

    save: function(properties) {
        // Template method.
    },

    validate: function() {
        // Template method.
        return true;
    },

    assignedComparator: function(item1, item2) {
        var l1 = item1.getLabel();
        var l2 = item2.getLabel();

        return l1 > l2 ? 1 : (l1 < l2 ? -1 : 0);
    },

    _moveEntities: function(bToAssigned) {

        var from, to, fromList, toList;

        if (bToAssigned) {
            from = this._unassigned;
            fromList = this.availableList;
            to = this._assigned;
            toList = this.assignedList
        } else {
            from = this._assigned;
            fromList = this.assignedList;
            to = this._unassigned;
            toList = this.availableList;
        }

        var items = fromList.getSelectedItems();
        var entities = items.collect(function(item) {
            return item.getValue();
        });

        to = to.concat(entities.collect(function(entity) {
            return (entity.available == bToAssigned) ? entity : null;
        })).compact();

        from = from.reject(function(fromItem) {
            return entities.detect(function(entity) {
                return fromItem.equals(entity);
            });
        });

        fromList.removeItems(items);

        toList.addItems(items, true);
        toList.refresh();

        if (bToAssigned) {
            this._unassigned = from;
            this._assigned = to;
        } else {
            this._assigned = from;
            this._unassigned = to;
        }

    },

    _addToAssigned: function() {
        this._moveEntities(true);
    },

    _removeFromAssigned: function() {
        this._moveEntities(false);
    },

    _entitiesToItems: function(entities, isAvailable) {
        entities = entities ? entities : [];

        return entities.collect(function(entity) {
            var label = "";
            var tooltipText = [], template = "orgTooltip";

            if (entity instanceof orgModule.User) {
                label = entity.userName;

                if (entity.fullName && entity.fullName.length > 0) {
                    tooltipText = [entity.fullName];
                }

                template = (entity.tenantId) ? "fullNameAndOrgTooltip" : "fullNameTooltip";
            } else if (entity instanceof orgModule.Role) {
                label = entity.roleName;

                template = (entity.tenantId) ? "orgTooltip" : undefined;
            }

            if (entity.tenantId && entity.tenantId.length > 0) {
                tooltipText.push(entity.tenantId);
            }

            entity.available = !!isAvailable;

            var item = new dynamicList.ListItem({
                label: label,
                value: entity
            });

            if (this.isEditMode) {
                item.processTemplate = function(element) {
                    if (tooltipText.length > 0) {
                        new JSTooltip(element, {
                            text: xssUtil.escape(tooltipText),
                            templateId: template
                        });
                    }

                    return dynamicList.ListItem.prototype.processTemplate.call(this, element);
                };
            } else {
                item.processTemplate = function(element) {
                    var nameAnchor = element.select("a")[0];

                    nameAnchor.insert(xssUtil.escape(this.getLabel()));
                    nameAnchor.writeAttribute('href', this.getValue().getManagerURL());

                    if (tooltipText.length > 0) {
                        new JSTooltip(element, {
                            text: xssUtil.escape(tooltipText),
                            templateId: template
                        });
                    }
                    return element;
                };
            }

            return item;
        }.bind(this));
    },

    getAssignedEntities: function() {
        return this._assigned;
    },

    getUnassignedEntities: function() {
        return this._unassigned;
    },

    setAssignedEntities: function(entities) {
        this.assignedViewInfiniteScroll && this.assignedViewInfiniteScroll.reset();
        this.assignedInfiniteScroll && this.assignedInfiniteScroll.reset();

        var list = (this.isEditMode) ? this.assignedList : this.assignedViewList;

        var filteredEntities = this._filterEntities(entities, this._unassigned, this._assigned,
            this.assignedSearchBox.getText());
        list.setItems(this._entitiesToItems(filteredEntities));

        list.refresh();
    },

    addAssignedEntities: function(entities) {
        var list = (this.isEditMode) ? this.assignedList : this.assignedViewList;

        var filteredEntities = this._filterEntities(entities, this._unassigned, this._assigned,
            this.assignedSearchBox.getText());
        list.addItems(this._entitiesToItems(filteredEntities));

        list.refresh();
    },

    setAvailableEntities: function(entities) {
        this.availableInfiniteScroll.reset();

        var filteredEntities = this._filterEntities(entities, this._assigned, this._unassigned,
            this.availableSearchBox.getText());
        this.availableList.setItems(this._entitiesToItems(filteredEntities, true));

        this.availableList.refresh();
    },

    addAvailableEntities: function(entities) {
        var filteredEntities = this._filterEntities(entities, this._assigned, this._unassigned,
            this.availableSearchBox.getText());
        this.availableList.addItems(this._entitiesToItems(filteredEntities, true));

        this.availableList.refresh();
    },

    _filterEntities: function(entities, removeSet, addSet, text) {
        var allEntities = entities.reject(function(entity) {
            return removeSet.detect(function(removeEntity) {
                return removeEntity.equals(entity);
            });
        });

        text = text.toLowerCase();
        allEntities = allEntities.concat(addSet.findAll(function(entity) {
            return entity.getDisplayName().toLowerCase().include(text);
        }));

        return allEntities;
    },

    changeReadonly: function(edit, elementsPatterns) {
        elementsPatterns.each(function(elementPattern) {
            $(this._id).select(elementPattern)[0].writeAttribute(layoutModule.READONLY_ATTR_NAME,
                edit ? null : layoutModule.READONLY_ATTR_NAME);
        }.bind(this));
    },

    resetValidation: function(elementsPatterns) {
        var panel = $(this._id);
        elementsPatterns.each(function(elementPattern) {
            ValidationModule.hideError(panel.select(elementPattern)[0]);
        }.bind(this));
    },

    changeDisable: function(edit, elementsPatterns) {
        elementsPatterns.each(function(elementPattern) {
            if (edit) {
                buttonManager.enable($(this._id).select(elementPattern)[0]);
            } else {
                buttonManager.disable($(this._id).select(elementPattern)[0]);
            }
        }.bind(this));
    },

    _deleteEntity: function() {
        // Template method.
    },

    _editEntity: function() {
        // Template method.
    },

    _showEntity: function() {
        // Template method.
    }
};

orgModule.addDialog = {
    show: function(organization) {
        // Template method.
    },

    hide: function(organization) {
        // Template method.
    }
};

orgModule.Action = function(invokeFn, beforeInvoke) {
    if (Object.isFunction(invokeFn)) {

        beforeInvoke = beforeInvoke && Object.isFunction(beforeInvoke) ? beforeInvoke : this.beforeInvoke;

        this.invokeAction = function() {
            if (beforeInvoke()) {
                return invokeFn.apply(this, arguments);
            }
        };
    }
};

/**
 * Invokes action.
 */
orgModule.Action.addMethod('invokeAction', doNothing);

/**
 * Invokes function before action is invoked..
 *
 * @return {Boolean} if false if true the action will not occur
 */
orgModule.Action.addMethod('beforeInvoke', function() {
    return true;
});

orgModule.ServerAction = function(eventId, options) {
    this.actionURL = 'flow.html?_flowExecutionKey=' + localContext.flowExecutionKey + '&_eventId=' + eventId;

    this.data = Object.toQueryString(options);

    this.onSuccess = doNothing();
    this.onError = doNothing();
};

/**
 * Invokes server action.
 */
orgModule.ServerAction.addMethod('invokeAction', function() {
    if (this.beforeInvoke()) {
        ajaxTargettedUpdate(this.actionURL, {
            postData: this.data,
            callback: function(response) {
                if (response.error) {
                    this.onError(response.error);
                } else {
                    this.onSuccess(response.data);
                }
            }.bind(this),
            errorHandler: function(ajaxAgent) {
                if (ajaxAgent.getResponseHeader("LoginRequested")) {
                    orgModule.fire(orgModule.Event.SERVER_UNAVAILABLE, {});
                    return true;
                }

                return baseErrorHandler(ajaxAgent);
            },
            mode: AjaxRequester.prototype.EVAL_JSON
        });
    }
});

/**
 * Invokes function before server action is invoked..
 *
 * @return {Boolean} if false if true the action will not occur
 */
orgModule.ServerAction.addMethod('beforeInvoke', function() {
    return true;
});

orgModule.toolbar = {
    _actionModel: {},

    initialize: function(actionModel) {
        this._actionModel = actionModel;
        toolbarButtonModule.initialize(this._toActionMap(this._actionModel));
        this.refresh();
    },

    refresh: function() {
        for (var name in this._actionModel) {
            var id = this._actionModel[name].buttonId;
            var testFn = toFunction(this._actionModel[name].test);

            toolbarButtonModule.setButtonState($(id), testFn());
        }
    },

    _toActionMap: function(bulkActionModel) {
        var actionMap = {};

        for (var name in bulkActionModel) {
            var bulkAction = bulkActionModel[name];
            var id = bulkAction.buttonId;

            actionMap[id] = function(action, actionArgs) {
                return function() {
                    var myAction = getAsFunction(action);
                    var args = actionArgs;

                    var belongsToLocalContext = localContext && localContext[action];

                    if (args && isArray(args)) {
                        myAction.apply(belongsToLocalContext ? localContext : null, args);
                    } else {
                        myAction.call(belongsToLocalContext ? localContext : null, args);
                    }
                }
            }(bulkAction.action, bulkAction.actionArgs)
        }

        return actionMap;
    }
};


////////////////////////////////////////////////////////////////////////////////////////
// Input validator
////////////////////////////////////////////////////////////////////////////////////////
/**
 * Parameter should have two properties:
 * <ul>
 * <li>regExp {RegExp} - expression must contains list of unsupported symbols</li>
 * <li>unsupportedSymbols {String} -  list of unsupported symbols which will be displayed in error message</li>
 * </ul>
 * @param input {DOMElement}
 */
orgModule.createInputRegExValidator = function(input) {
    return {
        validator: function(value) {
            var matches = input.regExp.test(value);
            var isValid = !matches;

            var errorMessage = (isValid) ? "" :
                orgModule.getMessage('unsupportedSymbols', {symbols: input.unsupportedSymbols});

            input.isValid = isValid;

            return { isValid: isValid, errorMessage: errorMessage };
        },
        element: input
    }
};

/**
 *  Check element value isn't blank
 *
 * @param element {DOMElement}
 * @param messageKey {String}
 */
orgModule.createBlankValidator = function(element, messageKey, onValid, onInvalid) {
    return {
        validator: function(value) {
            var isValid = !value.blank();
            var errorMessage = (isValid) ? "" : orgModule.getMessage(messageKey);

            return { isValid: isValid, errorMessage: errorMessage };
        },
        element: element,
        onValid: onValid,
        onInvalid: onInvalid
    }
};

/**
 *  Check max length of value
 *
 * @param element {DOMElement}
 * @param messageKey {String}
 */
orgModule.createMaxLengthValidator = function(element, messageKey, replaceKey) {
    return {
        validator: function(value) {
            var max = element.hasAttribute("maxlength") ? element.readAttribute("maxlength") : element.maxlength;
            var isValid = value.length < parseInt(max);
            var errorMessage = (isValid) ? "" : orgModule.getMessage(messageKey);

            replaceKey && (errorMessage = errorMessage.replace(replaceKey, max));

            return { isValid: isValid, errorMessage: errorMessage };
        },
        element: element
    }
};

/**
 *
 *
 * @param element {DOMElement} Validated element
 * @param asElement {DOMElement} Element to compare with
 * @param messageKey {String}
 */
orgModule.createSameValidator = function(element, asElement, messageKey) {
    return {
        validator: function(value) {
            var isValid = (value === asElement.getValue());
            var errorMessage = (isValid) ? "" : orgModule.getMessage(messageKey);

            return { isValid: isValid, errorMessage: errorMessage };
        },
        element: element
    }
};

/**
 *
 *
 * @param element {DOMElement} Validated element
 * @param messageKey {String}
 */
orgModule.createRegExpValidator = function(element, messageKey, regExp) {
    if (typeof regExp === "string") {
        if (window.XRegExp) {
            regExp = XRegExp(regExp);
        } else {
            regExp = new RegExp(regExp);
        }
    }
    return {
        validator: function(value) {
            var matches = regExp.exec(value);

            var isValid = (!matches && value.length === 0) || (matches && value == matches[0]);
            var errorMessage = (isValid) ? "" : orgModule.getMessage(messageKey);

            return { isValid: isValid, errorMessage: errorMessage };
        },
        element: element
    }
};

orgModule.truncateOrgName = function(name) {
    return name && name.length > 0 ? name.truncate(15) : "";
};

////////////////////////////////////////////////////////////////////////////////////////
// Used to convert regular expression with unsupported symbols into string separated by comas.
////////////////////////////////////////////////////////////////////////////////////////
function RegExpRepresenter(expression) {

    this.charMap = {'\\s': ' '};
    this.expressionTokens = this.__parse(expression);
}

RegExpRepresenter.addMethod("__parse", function(expression) {
    var result = $A();

    for (var i = 1; i < expression.length - 1; i++) {

        var ch = expression.charAt(i);

        if (ch == '\\') {

            ch += expression.charAt(++i);
        }

        if (ch == '\\u') {

            ch += expression.substring(++i, i + 4);
            i += 3;
        }

        result.push(ch);
    }

    return result;
});

RegExpRepresenter.addMethod("getCharacters", function() {
    var result = $A();

    this.expressionTokens.each(function(token) {

        if (token.startsWith('\\u')) {

            result.push(eval('"' + token + '"')); // jshint ignore: line
        } else if (token.startsWith('\\')) {

            var ch = this.charMap[token];
            result.push((ch) ? ch : token.substring(1, token.length));
        } else {

            result.push(token);
        }
    }.bind(this));

    return result;
});

RegExpRepresenter.addMethod("getRepresentedString", function() {
    var result = "";

    var chars = this.getCharacters();

    var len = chars.length;
    for (var i = 0; i < len; i++) {
        var ch = chars[i];

        if (ch == ' ' || ch == '.' || ch == ',') {
            result += '[' + ch + ']';
        } else {
            result += ch;
        }


        if (i < len - 1) {
            result += ', ';
        }
    }

    return result;
});