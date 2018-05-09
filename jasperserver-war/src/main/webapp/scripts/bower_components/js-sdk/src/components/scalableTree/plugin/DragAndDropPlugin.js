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
 * @author: Zakhar Tomchenko, Taras Bidyuk
 * @version: $Id$
 */

define(function(require){
    "use strict";

    var _ = require('underscore'),
        $ = require('jquery'),
        TreePlugin = require("./TreePlugin");

    require("jquery-ui/jquery.ui.draggable");

    return TreePlugin.extend({

        initialize: function(options) {
            this.tree = options.tree;

            // TODO: i18n
            this.itemsSelectedMessage = options.itemsSelectedMessage || "items selected";
            this.appendTo = options.appendTo || $("body");

            this.dragInProcess = false;

            this.onDragStart = options.onDragStart;
            this.onDrag = options.onDrag;
            this.onDragStop = options.onDragStop;

            this.test = options.test || function() {
                return true;
            };

            this._initEvents();

            TreePlugin.prototype.initialize.apply(this, arguments);
        },

        beforeItemsRendered: function() {
            this._destroyDraggable();
        },

        _initEvents: function() {
            this.listenTo(this.tree, "selection:change", this._onTreeSelectionChange);
            this.listenTo(this.tree, "item:mouseover", this._onTreeItemMouseOver);
            this.listenTo(this.tree, "item:mouseout", this._onTreeItemMouseOut);
        },

        _initDraggable: function($el) {
            var self = this;

            $el.draggable({
                cursor: "move",
                appendTo: this.appendTo,
                cursorAt: {
                    top: 0,
                    left: 0
                },
                helper: function( event ) {
                    return $('<div class="wrap button draggable dragging dragHelper"></div>');
                },
                start: function(e, ui) {
                    // prevent original event
                    // if not to do this, text selection event replaces cursor with pointer
                    e.originalEvent.preventDefault();

                    self.dragInProcess = true;

                    var data,
                        $item = $(this),
                        index = $item.attr("data-index");

                    while (index === undefined){
                        $item = $item.parent();
                        index = $item.attr("data-index");
                    }

                    var selection = self.tree.getSelection(),
                        selectionLength = selection.length;

                    if(!self.tree.selection.multiple || selectionLength <= 1) {
                        data = {
                            items: selection[0],
                            label: selection[0].label
                        }
                    } else {
                        data = {
                            label: selectionLength + " " + self.itemsSelectedMessage,
                            items: selection
                        }
                    }

                    ui.helper.text(data.label).data("data", data.items);

                    self.onDragStart && self.onDragStart(data.items, e);
                },
                drag: function(e, ui) {
                    self.onDrag && self.onDrag(ui.helper.data("data"), e);
                },
                stop: function(e, ui) {
                    self.dragInProcess = false;
                    self.onDragStop && self.onDragStop(ui.helper.data("data"), e);
                }
            });

            self.lastDraggableItem = $el;
        },

        _onTreeSelectionChange: function(selection) {
            if (!selection.length) {
                this._destroyDraggable();
            }
        },

        _onTreeItemMouseOver: function(item, event) {
            var id,
                $item = $(event.target),
                index = $item.attr("data-index");

            if (!this.dragInProcess) {
                $item = this._getItem($item, index);
                id = $item.data("id");
                // lazy initialization
                this.test(this.tree.getItem(id)) && this._initDraggable($item);
            }
        },

        _onTreeItemMouseOut: function(item, event) {
            var $item = $(event.target),
                index = $item.attr("data-index");

            if (!this.dragInProcess) {
                $item = this._getItem($item, index);
                // lazy initialization
                this._destroyDraggable($item);
            }
        },

        _getItem: function($item, index) {
            while (index === undefined){
                $item = $item.parent();
                index = $item.attr("data-index");
            }

            return $item;
        },

        _destroyDraggable: function($el) {
            $el = $el || this.lastDraggableItem;

            try {
                $el && $el.draggable("destroy");
            } catch (e) {
                // destroyed already, skip
            }

            this.lastDraggableItem = null;
        }
    });
});


