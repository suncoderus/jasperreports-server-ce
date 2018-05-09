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

/*global window, Backbone, _, jaspersoft.components */

/**
 * @author: Sergey Prilukin
 * @version: $Id$
 */

/**
 * @class ListWithSelectionAsObjectHashModel
 * @classdesc Model extended from ScalableListModel which allows to work with selection.
 * It holds selection as an object hash
 *
 * Model API (in addition to ScalableListModel API):
 *      addValueToSelection         - adds one value to selection and triggers "selection:add" event
 *      addRangeToSelection         - adds range from last adding of single selection to passed
 *                                    index and triggers "selection:addRange"
 *      removeValueFromSelection    - removes value from selection and triggers "selection:remove"
 *      toggleSelection             - toggles selection and triggers appropriate event
 *      selectionContains           - return true if value is selected and false otherwise
 *      clearSelection              - clear selection and trigger "selection:clear" event
 *      getSelection                - returns current selection as sparse array
 *      select                      - select passed values
 *      selectAll                   - select all values
 *      invertSelection             - inverts selection
 *
 * this Model implementation looks for following additional options in hash provided to constructor:
 *      selection       - initial selection
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        BaseListWithSelectionModel = require("components/scalableList/model/BaseListWithSelectionModel");


    var ListWithSelectionAsObjectHashModel = BaseListWithSelectionModel.extend(
        /** @lends ListWithSelectionAsObjectHashModel.prototype */
        {

        /* Methods which supposed to be overridden */

        /**
         * Adding to selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _addToSelection: function(value, index) {
            this.selection[value] = true;
        },

        /**
         * Removing from selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _removeFromSelection: function(value, index) {
            delete this.selection[value];
        },

        /**
         * Clearing selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _clearSelection: function() {
            this.selection = {};
        },

        /**
         * Selection contains should be checked only through this method
         * No direct change of this.selection object is allowed
         */
        _selectionContains: function(value, index) {
            return this.selection[value];
        },

        /**
         * Returns current selection
         * No direct change of this.selection object is allowed
         */
        _getSelection: function() {
            return _.keys(this.selection);
        },

        /* Internal helper methods */

        /*-------------------------
         * API
         -------------------------*/


        /*
            Selects passed selection
         */
        select: function(selection, options) {
            this._clearSelection();

            if (typeof selection === "string") {
                this._addToSelection(selection);
            } else if (_.isArray(selection)) {
                for (var i = 0; i < selection.length; i++) {
                    this._addToSelection(selection[i]);
                }
            } else if (typeof selection !== "undefined") {
                //Ok, selection is just usual hash with indexes as keys -
                for (var key in selection) {
                    if (selection.hasOwnProperty(key)) {
                        var value = selection[key];
                        if (value !== undefined) {
                            this._addToSelection(value, key);
                        }
                    }
                }
            }

            this._afterSelect && this._afterSelect(selection, options);

            this._triggerSelectionChange(options);
        }
    });

    return ListWithSelectionAsObjectHashModel;
});
