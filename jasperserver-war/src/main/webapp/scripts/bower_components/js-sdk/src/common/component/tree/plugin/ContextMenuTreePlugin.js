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


define(function(require){
    "use strict";

    var TreePlugin = require("./TreePlugin"),
        _ = require('underscore'),
        $ = require("jquery");

    var returnContextMenuOptions = _.memoize(function(contextMenu){
        var actions = _.map(contextMenu.options, function(option){
            return option.model.get("action");
        });
        return actions;
    }, function(contextMenu){
        return contextMenu.cid;
    });

    return TreePlugin.extend({
        initialize: function(options){

            if(!options){
                throw new Error("Initialization error. Options required.");
            }

            this.contextMenus = options.contextMenus;
            this.contextMenu = options.contextMenu;
            this.defaultSelectedItems = options.defaultSelectedItems || function(){};

            if(!this.contextMenus && !this.contextMenu){
                throw new Error("contextMenu or contextMenus must be specified.");
            }

            this.showContextMenuCondition = options.showContextMenuCondition;

            if(!this.showContextMenuCondition && !this.contextMenu){
                throw new Error("contextMenu must be specified for default behaviour");
            }

            !this.showContextMenuCondition && (this.showContextMenuCondition = function(){return this.contextMenu});

            this.model = options.model;

            TreePlugin.prototype.initialize.apply(this, arguments);
        },

        itemAction: function(optionModel){
            var action = optionModel.get("action");
            this.treeItem.action = action;
        },

        itemsRendered: function(model, list){
            var self = this;

            this.listenTo(list, "list:item:contextmenu", function(item, event){
                this.currentContextMenu = self.showContextMenuCondition(item);

                this.lastContextMenu && this.lastContextMenu.hide();
                this.lastContextMenu = this.currentContextMenu;

                if(this.currentContextMenu){
                    this.currentContextMenu.treeItem = item;
                    var action = this.currentContextMenu.treeItem.action;

                    action || (action = self.defaultSelectedItems.call(this.currentContextMenu, item, returnContextMenuOptions(this.currentContextMenu))) ?
                        this.currentContextMenu.resetSelection([action]) :
                        this.currentContextMenu.resetSelection();

                    this.currentContextMenu.show({
                        left: event.pageX,
                        top: event.pageY
                    });

                    this.listenTo(this.currentContextMenu.collection, "select", function(optionView, optionModel){
                        this.itemAction.call(this.currentContextMenu, optionModel);
                    }, this);
                }
            }, this);

        },

        remove: function(){
            this.stopListening();
            this.contextMenu && this.contextMenu.remove();
        }
    });
});
