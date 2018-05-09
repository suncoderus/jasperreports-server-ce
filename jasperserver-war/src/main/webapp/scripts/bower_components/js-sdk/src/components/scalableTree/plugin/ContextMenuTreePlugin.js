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

        ContextMenu = require("common/component/menu/ContextMenu");

    return TreePlugin.extend({
        initialize: function(options){
            this.tree = options.tree;

            this.actionsMap = {};

            if(!options){
                throw new Error("Initialization error. Options required.");
            }

            this.onTreeContextMenuEvent = options.onTreeContextMenuEvent || function() {};
            this.getOptionsToSelect = options.getOptionsToSelect || function() {return [];};
            this.contextMenuEvents = options.contextMenuEvents || function() {return {};};
            this.getContextMenuOptions = options.getContextMenuOptions || function() {return [];};
            this.showContextMenuCondition = options.showContextMenuCondition || function() {return true;};

            this._initEvents();

            TreePlugin.prototype.initialize.apply(this, arguments);
        },

        _initEvents: function() {
            this.listenTo(this.tree, "item:contextmenu", this._onContextMenuEvent);
        },

        _onContextMenuEvent: function(item, event) {
            this.onTreeContextMenuEvent(item);

            this._removeLastContextMenu();

            if (this.showContextMenuCondition(item)) {

                this.contextMenu = this._getContextMenu(item);

                this._initContextMenuEvents(item, event);
                this._preselectOptions(item);

                this.contextMenu.show({
                    left: event.pageX,
                    top: event.pageY
                });

                this.lastContextMenu = this.contextMenu;
            }
        },

        _getContextMenu: function(item) {
            var constructorOptions = this.getContextMenuOptions(item);

            return constructorOptions.contextMenu
                || new ContextMenu(constructorOptions.options, constructorOptions.additionalSettings);
        },

        _initContextMenuEvents: function(item, event) {
            var events = this.contextMenuEvents(item);

            if (events) {
                _.each(events, function(callback, contextMenuEvent) {
                    this.listenTo(this.contextMenu, contextMenuEvent, _.partial(callback, item, event));
                }, this);
            }
        },

        _preselectOptions: function(item) {
            var optionsToSelect = this.getOptionsToSelect(item) || [];

            if (optionsToSelect.length) {
                _.each(optionsToSelect, function(option) {
                    this.contextMenu.select(option);
                }, this);
            }
        },

        _removeLastContextMenu: function() {
            if (this.lastContextMenu) {
                this.stopListening(this.lastContextMenu);
                this.lastContextMenu.remove();
            }
        },

        remove: function(){
            this.stopListening();
            this.contextMenu && this.contextMenu.remove();
        }
    });
});
