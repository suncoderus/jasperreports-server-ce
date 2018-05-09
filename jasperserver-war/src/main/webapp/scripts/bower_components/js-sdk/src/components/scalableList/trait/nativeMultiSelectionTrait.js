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
 * @author: Taras Bidyuk
 */

define(function(require) {

    var _ = require("underscore");

    return {
        globalEvents: false,

        _multiSelect: function(event, value, index) {
            var model = this.model;

            if (event.shiftKey) {
                model.addRangeToSelection(value, index);
            } else if (event.ctrlKey || event.metaKey) {
                model.toggleSelection(value, index);
            } else {
                if(_.compact(model.getSelection()).length <= 1) {
                    this._singleSelect(event, value, index);
                    this._triggerSelectionChanged();
                }
            }
        },

        onMousedblclick: function(event) {
            if ((this.selection.allowed.left || this.selection.allowed.right) && !this.getDisabled()) {
                var itemData = this._getDomItemData(event.currentTarget);
                this._singleSelect(event, itemData.value, itemData.index);

                if (this.selectionChanged) {
                    this._triggerSelectionChanged();
                    this._triggerDblclicked();
                    this.selectionChanged = false;
                }
            }
        },

        onMousemove: function(event) {},

        onGlobalMouseup: function(event) {
            var itemData = this._getDomItemData(event.currentTarget);

            if(!event.ctrlKey && !event.shiftKey && !event.metaKey) {
                this._singleSelect(event, itemData.value, itemData.index);
            }

            if (this.selectionChanged) {
                this._triggerSelectionChanged();
                this.selectionChanged = false;
            }
        }
    }
});
