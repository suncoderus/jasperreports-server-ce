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
 tooltipModule, $break, baseList, orgModule, dialogs, buttonManager, ValidationModule, ResourcesUtils,
 accessibilityModule, confirm, fileSender, invokeClientAction, invokeServerAction, require
 */

orgModule.serverActionFactory = {
    'browse': function (options) {
        return this.createAction(orgModule.ActionMap.BROWSE, options);
    },

    'search': function (options) {
        return this.createAction(orgModule.ActionMap.SEARCH, options);
    },

    'next': function (options) {
        return this.createAction(orgModule.ActionMap.NEXT, options);
    },

    'selectAndGetDetails': function (options) {
        return this.createAction(orgModule.ActionMap.SELECT_AND_GET_DETAILS, options);
    },

    'searchAvailable': function(options) {
        return this.createAction(orgModule.ActionMap.SEARCH_AVAILABLE, options);
    },

    'searchAssigned': function(options) {
        return this.createAction(orgModule.ActionMap.SEARCH_ASSIGNED, options);
    },

    'nextAvailable': function(options) {
        return this.createAction(orgModule.ActionMap.NEXT_AVAILABLE, options);
    },

    'nextAssigned': function(options) {
        return this.createAction(orgModule.ActionMap.NEXT_ASSIGNED, options);
    },

    'create': function(options) {
        var entity = options.entity;
        var data = {
            'entity': Object.toJSON(entity)
        };

        return this.createAction(orgModule.ActionMap.CREATE, data);
    },

    'update': function(options) {
        var entityName = options.entityName;
        var data = {
            'entityName': entityName,
            'entity': Object.toJSON(options.entity),
            'assignedEntities': Object.toJSON(options.assigned),
            'unassignedEntities': Object.toJSON(options.unassigned)
        };

        return this.createAction(orgModule.ActionMap.UPDATE, data, options.unencryptedEntity);
    },

    'deleteAll': function(options) {
        var entities = options.entities;
        var data = {'entities' : Object.toJSON(entities.collect(function(entity) {
            return entity.getNameWithTenant();
        }))};

        return this.createAction(orgModule.ActionMap.DELETE_ALL, data);
    },

    'delete': function(options) {
        var entity = options.entity,
            entityEvent = options.entityEvent;

        var data = {'entity' : entity.getNameWithTenant(), 'entityEvent': entityEvent};

        return this.createAction(orgModule.ActionMap.DELETE, data);
    },

    'exist': function(options) {
        // TODO: refactor to use createAction method.
        var entity = options.entity;
        var data = {
            entityName: entity.getNameWithTenant()
        };

        var action = new orgModule.ServerAction(orgModule.ActionMap.EXIST, data);

        action.onSuccess = function(data) {
            data.exist ? options.onExist && options.onExist(data.uniqueId) : options.onNotExist && options.onNotExist();
        };

        action.onError = function(data) {
            orgModule.fire(orgModule.Event.SERVER_ERROR, {
                inputData: options,
                responseData: data
            });
        };

        return action;
    },

    createAction: function(actionName, options, unencryptedEntity) {
        var action = new orgModule.ServerAction(actionName, options);

        var event;
        if (actionName == orgModule.ActionMap.BROWSE) {
            event = orgModule.Event.RESULT_CHANGED;
        } else if (actionName == orgModule.ActionMap.SEARCH) {
            event = orgModule.Event.RESULT_CHANGED;
        } else if (actionName == orgModule.ActionMap.NEXT) {
            event = orgModule.Event.RESULT_NEXT;
        } else if (actionName == orgModule.ActionMap.SELECT_AND_GET_DETAILS) {
            event = orgModule.Event.ENTITY_DETAILS_LOADED;
        } else if (actionName == orgModule.ActionMap.SEARCH_AVAILABLE) {
            event = orgModule.Event.SEARCH_AVAILABLE_LOADED;
        } else if (actionName == orgModule.ActionMap.SEARCH_ASSIGNED) {
            event = orgModule.Event.SEARCH_ASSIGNED_LOADED;
        } else if (actionName == orgModule.ActionMap.NEXT_AVAILABLE) {
            event = orgModule.Event.NEXT_AVAILABLE_LOADED;
        } else if (actionName == orgModule.ActionMap.NEXT_ASSIGNED) {
            event = orgModule.Event.NEXT_ASSIGNED_LOADED;
        } else if (actionName == orgModule.ActionMap.CREATE) {
            event = orgModule.Event.ENTITY_CREATED;
        } else if (actionName == orgModule.ActionMap.UPDATE) {
            event = orgModule.Event.ENTITY_UPDATED;
        } else if (actionName == orgModule.ActionMap.DELETE_ALL) {
            event = orgModule.Event.ENTITIES_DELETED;
        } else if (actionName == orgModule.ActionMap.DELETE) {
            event = orgModule.Event.ENTITY_DELETED;
        } else {
            throw new Error("Unexpected action name '" + actionName + "'");
        }

        action.onSuccess = function(data) {
            orgModule.fire(event, {
                inputData: options,
                unencryptedEntity: unencryptedEntity,
                responseData: data
            });
        };

        action.onError = function(data) {
            orgModule.fire(orgModule.Event.SERVER_ERROR, {
                inputData: options,
                unencryptedEntity: unencryptedEntity,
                responseData: data
            });
        };

        return action;
    }
};

orgModule.clientActionFactory = {
    'create': function () {
        var org;
        if (orgModule.manager.tenantsTree) {
            org = orgModule.manager.tenantsTree.getTenant();
        }

        var entities = orgModule.entityList.getSelectedEntities();

        var cancelEditBeforeInvoke = function() {
            return invokeClientAction("cancelIfEdit", { entity: entities[0] });
        };
        
        return new orgModule.Action(function() {
            orgModule.entityList.deselectAll();
            orgModule.addDialog.show(org);
        }, cancelEditBeforeInvoke);
    },

    'deleteAll': function () {
        var entities = orgModule.entityList.getSelectedEntities();

        var cancelEditBeforeInvoke = function() {
            return invokeClientAction("cancelIfEdit", { entity: entities[0] });
        };

        return new orgModule.Action(function() {

	        var text = orgModule.getMessage("deleteAllMessage", { count: entities.length });

            // Has been changed from simple require("Module name") due to http://requirejs.org/docs/errors.html#notloaded error
	        require(["common/component/dialog/ConfirmationDialog"], function(ConfirmationDialog){
                var dialog = new ConfirmationDialog({text: text});

                dialog.on("button:yes", function() {
                    invokeServerAction(orgModule.ActionMap.DELETE_ALL, {
                        entities: entities
                    });
                });

                dialog.open();
            });

        }, cancelEditBeforeInvoke);
    },

    'delete': function (options) {
        var entity = options.entity,
            entityEvent = options.entityEvent;

        return new orgModule.Action(function() {

	        var text = orgModule.getMessage("deleteMessage", { entity: entity.getDisplayName() });

            // Has been changed from simple require("Module name") due to http://requirejs.org/docs/errors.html#notloaded error
            require(["common/component/dialog/ConfirmationDialog"], function(ConfirmationDialog){
                var dialog = new ConfirmationDialog({text: text});
                dialog.on("button:yes", function(){
                    invokeServerAction(orgModule.ActionMap.DELETE, {
                        entity: entity,
                        entityEvent: entityEvent
                    });
                });
                dialog.open();
            });

        });
    },

    'cancelIfEdit': function (options) {
        options = options || {};

        var entity = options.entity || orgModule.manager.tenantsTree && orgModule.manager.tenantsTree.getTenant(),
            showConfirm = typeof options.showConfirm === "undefined" ? true : options.showConfirm,
            entityEvent = typeof options.entityEvent === "undefined" ? true : options.entityEvent,
            properties = orgModule.properties;

        return new orgModule.Action(function() {
            if ((!properties.locked || !entityEvent) &&
                (!properties.isChanged() || (showConfirm && confirm(orgModule.getMessage("cancelEdit", { entity: entity.getDisplayName() }))))) {
                if (properties.isEditMode) {
                    properties.lock();
                    properties.cancel().done(function(){
                        var entity = properties.getDetailsLoadedEntity(),
                            showProperties = orgModule.userManager ? orgModule.entityList.findEntity(entity.fullName) : true;

                        properties.unlock();

                        if(showProperties){
                            orgModule.fire(orgModule.Event.ENTITY_SELECT_AND_GET_DETAILS, {
                                entityId: entity.id,
                                cancelIfEdit: true,
                                entityEvent: true
                            });
                        }else{
                            properties.hide();
                        }
                    });
                }
                return true;
            } else {
                !entityEvent && properties.lock();
                return false;
            }
        });
    }
};