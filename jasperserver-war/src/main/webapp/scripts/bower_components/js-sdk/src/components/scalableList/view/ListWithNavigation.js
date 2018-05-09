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
 * @author Sergey Prilukin
 * @version: $Id: ListWithNavigation.js 1178 2015-05-06 20:40:12Z yplakosh $
 */

define(function (require) {
    'use strict';

    var ListWithSelection = require("components/scalableList/view/ListWithSelection"),
        ListWithSelectionModel = require("components/scalableList/model/ListWithSelectionModel"),
        listWithNavigationModelTrait = require("components/scalableList/model/listWithNavigationModelTrait");

    var ListWithNavigation = ListWithSelection.extend({

        canActivate: true,

        initialize: function(options) {
            options = options || {};

            var ListWithNavigationModel = ListWithSelectionModel.extend(listWithNavigationModelTrait);
            options.model = options.model || new ListWithNavigationModel(options);
            ListWithSelection.prototype.initialize.call(this, options);
        },

        /* Event handlers */

        onMousemove: function(event) {
            ListWithSelection.prototype.onMousemove.call(this, event);

            if (!this.mouseMovePos || this._mouseMoved(event, this.mouseMovePos)) {
                this.mouseMovePos = this._getMousePos(event);

                var itemData = this._getDomItemData(event.currentTarget);

                this.activate(itemData.index);
            }
        },

        /* API */

        setCanActivate: function(canActivate) {
            this.canActivate = canActivate;
        },

        getCanActivate: function() {
            return this.canActivate && !this.getDisabled();
        },

        activateFirst: function() {
            this.activate(0);
        },

        activateLast: function() {
            this.activate(Math.max(0, this.model.get("total") - 1));
        },

        activateNext: function() {
            this.activate(Math.min(this.model.get("total") - 1, this._getActiveIndex() + 1));
        },

        activatePrevious: function() {
            this.activate(Math.max(0, this._getActiveIndex() - 1));
        },

        pageDown: function() {
            this.activate(Math.min(this.model.get("total") - 1, this._getActiveIndex() + this._getItemsPerView()));
        },

        pageUp: function() {
            this.activate(Math.max(0, this._getActiveIndex() - this._getItemsPerView()));
        },

        getActiveValue: function() {
            return this.model.getActive && this.model.getActive();
        },

        activate: function(index, options) {
            if (this.getCanActivate()) {
                var active = this.getActiveValue();

                if (active && active.index === index) {
                    return;
                }

                if (!options || !options.silent) {
                    var that = this;
                    this.model.once("active:changed", function() {
                        that.trigger("active:changed", that.getActiveValue());
                    });
                }

                this.model.activate(index, options);
            }
        },

        /* Methods which overrides ones from base class */

        //Add active attribute to model
        postProcessChunkModelItem: function(item, i) {
            ListWithSelection.prototype.postProcessChunkModelItem.call(this, item, i);

            var activeIndex = this._getActiveIndex && this._getActiveIndex();

            item.active = (typeof activeIndex !== "undefined" && activeIndex === this.model.get("bufferStartIndex") + i);
        },

        /* Internal helper methods */

        _getActiveIndex: function() {
            var active = this.getActiveValue();

            return active && active.index;
        }
    });

    return ListWithNavigation;
});
