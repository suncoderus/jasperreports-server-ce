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

/* global orgModule, isProVersion, alert, _  */

function invokeServerAction(actionName, options) {
    var actionBuilder = orgModule.serverActionFactory[actionName];

    if (actionBuilder) {
        var action = actionBuilder.call(orgModule.serverActionFactory, options);
        action.invokeAction();
    } else {
        throw new Error("No server action found for action name '" + actionName + "'");
    }
}

function invokeClientAction(actionName, options) {
    var actionBuilder = orgModule.clientActionFactory[actionName];

    if (actionBuilder) {
        var action = actionBuilder.call(orgModule.clientActionFactory, options);
        return action.invokeAction();
    } else {
        throw new Error("No client action found for action name '" + actionName + "'");
    }
}

orgModule.confirmAndLeave = function() {
    var entities = orgModule.entityList.getSelectedEntities();

    if(!orgModule.properties.attributesFacade){
        return invokeClientAction("cancelIfEdit", { entity: entities[0], entityEvent: false});
    }

    return true;
};

orgModule.manager = {
    initialize: function(options) {
        this.options = options;
        this.state = options.state;

        _.extend(this.state, {id: this.state.tenantId});

        if (isProVersion()) {
            this.tenantsTree = new options.TenantsTreeView({
                container: "#folders .body",
                currentUser: options.currentUser,
                removeContextMenuTreePlugin: options.removeContextMenuTreePlugin,
                selectedTenant: this.state,
                comparator: function(t1, t2){
                    return orgModule._comparator(t1.tenantName, t2.tenantName)
                }
            });

            orgModule.initTenantsTreeEvents();
            this.tenantsTree.render();
        }

        // Request events listeners.
        orgModule.observe("org:browse", function(event) {
            var org;

            this.tenantsTree && (org = this.tenantsTree.getTenant());

            var entityEvent = event.memo.entityEvent,
                force = event.memo.force;

            if (force || !this.lastSelectedOrg || this.lastSelectedOrg.id != org.id) {
                var entities = orgModule.entityList.getSelectedEntities();

                if(invokeClientAction("cancelIfEdit", { entity: entities[0] || org, entityEvent: entityEvent})) {
                    invokeServerAction(orgModule.ActionMap.BROWSE, {
                        tenantId : (org) ? org.id : null
                    });
                    this.lastSelectedOrg = org;
                } else {
                    this.lastSelectedOrg && this.tenantsTree.selectTenant(this.lastSelectedOrg.id);
                }
            }
        }.bindAsEventListener(this));

        orgModule.observe("entity:search", function(event) {
            var entities = orgModule.entityList.getSelectedEntities(),
                text = event.memo.text, org;

            if (this.tenantsTree) {
                org = this.tenantsTree.getTenant();
            }

            if(invokeClientAction("cancelIfEdit", { entity: entities[0] || org })) {
                invokeServerAction(orgModule.ActionMap.SEARCH, {
                    text: text
                });
                this.lastSearchText = text;
            } else {
                if (text.length === 0 && this.lastSearchText && this.lastSearchText.length !== 0) {
                    orgModule.entityList.setSearchText(this.lastSearchText);
                }
            }
        }.bindAsEventListener(this));

        orgModule.observe("entity:next", function(event) {
            invokeServerAction(orgModule.ActionMap.NEXT, {});
        }.bindAsEventListener(this));

        orgModule.observe("entity:selectAndGetDetails", function(event) {
            var entityEvent = event.memo.entityEvent,
                isCtrlHeld = event.memo.isCtrlHeld,
                cancelIfEdit = event.memo.cancelIfEdit,
                entities = orgModule.entityList.getSelectedEntities(),
                refreshAttributes = Boolean(event.memo.refreshAttributes);

            if (entities.length > 1) {
                orgModule.properties.hide();
            } else {
                var firstEntity = entities[0],
                    entityName;

                if (firstEntity && entityEvent) {
                    entityName = firstEntity.getNameWithTenant()
                } else {
                    entityName = (!entityEvent || isCtrlHeld || cancelIfEdit) && event.memo.entityId;
                }

                if (!firstEntity && (orgModule.userManager || (!orgModule.userManager && orgModule.roleManager)) && isCtrlHeld) {
                    orgModule.properties.hide();
                    return;
                }

                entityName && invokeServerAction(orgModule.ActionMap.SELECT_AND_GET_DETAILS, {
                    refreshAttributes: refreshAttributes,
                    entity: entityName
                });
            }
        }.bindAsEventListener(this));

        // Response events listeners.
        orgModule.observe("result:changed", function(event) {
            var data = event.memo.responseData;
            orgModule.entityList.setEntities(data.entities.collect(this.entityJsonToObject));
            options.defaultEntity && orgModule.entityList.restoreSelectedEntity(options.defaultEntity);
        }.bindAsEventListener(this));

        orgModule.observe("result:next", function(event) {
            var data = event.memo.responseData;
            if (data.entities.length > 0) {
                orgModule.entityList.addEntities(data.entities.collect(this.entityJsonToObject));
            }
        }.bindAsEventListener(this));

        orgModule.observe("entity:detailsLoaded", function(event) {
            var entity = this.entityJsonToObject(event.memo.responseData),
                properties = orgModule.properties,
                refreshAttributes = event.memo.inputData.refreshAttributes;

            orgModule.entityList.update(entity.getNameWithTenant(), entity);

            properties.setDetailsLoadedEntity(entity);
            properties.show(entity, refreshAttributes);
            !properties.locked && properties.setProperties(entity);

            orgModule.entityList.toolbar.refresh();

            properties.unlock();

        }.bindAsEventListener(this));

        orgModule.observe("searchAvailable:loaded", function(event) {
            var data = event.memo.responseData;
            if (data && data.entities) {
                orgModule.properties.setAvailableEntities(data.entities.collect(this.relatedEntityJsonToObject));
            }
        }.bindAsEventListener(this));

        orgModule.observe("searchAssigned:loaded", function(event) {
            var data = event.memo.responseData;

            if(data && data.entities) {
                orgModule.properties.setAssignedEntities(data.entities.collect(this.relatedEntityJsonToObject));
            }
        }.bindAsEventListener(this));

        orgModule.observe("nextAvailable:loaded", function(event) {
            var data = event.memo.responseData;

            if (data && data.entities && data.entities.length > 0) {
                orgModule.properties.addAvailableEntities(data.entities.collect(this.relatedEntityJsonToObject));
            }
        }.bindAsEventListener(this));

        orgModule.observe("nextAssigned:loaded", function(event) {
            var data = event.memo.responseData;

            if (data && data.entities && data.entities.length > 0) {
                orgModule.properties.addAssignedEntities(data.entities.collect(this.relatedEntityJsonToObject));
            }
        }.bindAsEventListener(this));

        orgModule.observe("server:error", function(event) {
            var error = event.memo.responseData;
            if(error) {
                alert(error.message + "\n\n" + (error.description) ? error.description : "");
            }
        }) ;

        orgModule.observe("entity:created", function(event) {
            orgModule.addDialog.hide();

            var entityJson = event.memo.inputData.entity;
            if (entityJson) {
                var entity = this.entityJsonToObject(entityJson.evalJSON());

                orgModule.entityList.addEntities([entity]);
                orgModule.entityList.deselectAll();
                orgModule.entityList.selectEntity(entity.getNameWithTenant());
            }
        }.bindAsEventListener(this));

        orgModule.observe("entity:updated",function(event) {
            var self = this;
            var entityName = event.memo.inputData.entityName;


            ////////////////////////////////////////////////////
            //  2012-05-09  thorick chow
            //              http://bugzilla.jaspersoft.com/show_bug.cgi?id=27717
            //
            //              Special case for User Objects
            //              if
            //                  the update involves the server
            //                  sending us back an updated User Object
            //              then
            //                 use that as the entity
            //              else
            //                 fallback to whatever the pre-existing cases were using
            //

            //  if the Response *Object* contains 'roles' property
            //  then we'll assume that that's enough to qualify it as a User Object
            var entityJson = event.memo.responseData;
            if (!entityJson.roles) {
                entityJson = event.memo.unencryptedEntity;
            }
            if (!entityJson) {
                entityJson = event.memo.inputData.entity;
            }

            var attributesFacade = orgModule.properties.attributesFacade || {};
            var saveDfD = attributesFacade.designer && attributesFacade.designer.saveDfD;

            if (orgModule.properties.isEditMode) {
                saveDfD ? saveDfD.done(function(){
                    orgModule.properties.changeMode(false);
                }) : orgModule.properties.changeMode(false);
            }

            if (entityJson) {
                saveDfD ? saveDfD.done(function(){
                    self.updateEntityList(entityJson, entityName);
                }) : this.updateEntityList(entityJson, entityName);
            }
        }.bindAsEventListener(this));

        orgModule.observe("entity:deleted", function(event) {
            var entityName = event.memo.inputData.entity;

            orgModule.entityList.remove(entityName);
        }.bindAsEventListener(this));

        orgModule.observe("entities:deleted", function(event) {
            var entityNameSet = event.memo.inputData.entities;

            if (entityNameSet) {
                orgModule.entityList.remove(entityNameSet.evalJSON());
            }
        }.bindAsEventListener(this));
    },

    updateEntityList: function(entityJson, entityName){
        var entity = this.entityJsonToObject(entityJson.evalJSON ? entityJson.evalJSON() : entityJson);
        orgModule.entityList.update(entityName, entity);
        orgModule.entityList.selectEntity(entity.getNameWithTenant ? entity.getNameWithTenant() : entityName);
    },

    entityJsonToObject: function(json) {
        // Template method.
    },

    relatedEntityJsonToObject: function(json) {
        // Template method.
    },

    reloadEntities: function() {
        orgModule.fire(orgModule.Event.ORG_BROWSE, {});
    },

    isUserSuperuser: function () {
        var superuserRole = (isProVersion())
                ? orgModule.Configuration.superuserRole
                : orgModule.Configuration.adminRole;

        var roles = this.options.currentUserRoles;

        return roles.detect(function(r) { return r.roleName == superuserRole && !r.tenantId; }) != null;
    }
};
