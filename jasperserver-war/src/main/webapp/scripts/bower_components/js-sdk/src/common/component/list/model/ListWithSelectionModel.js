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

/*global window, Backbone, _, jaspersoft.components */

/**
 * @class ListWithSelectionModel
 * @classdesc Model extended from ScalableListModel which allows to work with selection.
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
        BaseListWithSelectionModel = require("common/component/list/model/BaseListWithSelectionModel");

    var ListWithSelectionModel = BaseListWithSelectionModel.extend(
        /** @lends ListWithSelectionModel.prototype */
        {


        /* Methods which supposed to be overridden */

        /**
         * Adding to selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _addToSelection: function(value, index) {
            this.selection[index] = value;
        },

        /**
         * Removing from selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _removeFromSelection: function(value, index) {
            this.selection[index] = undefined;
        },

        /**
         * Clearing selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _clearSelection: function() {
            this.selection = [];
        },

        /**
         * Selection contains should be checked only through this method
         * No direct change of this.selection object is allowed
         */
        _selectionContains: function(value, index) {
            return this.selection[index] === value;
        },

        /**
         * Returns current selection
         * No direct change of this.selection object is allowed
         */
        _getSelection: function() {
            return this.selection;
        },

        _isSelectionEmpty: function(selection) {
            if (!selection) {
                return true;
            } else if (_.isArray(selection) && selection.length === 0) {
                return true;
            } else if (_.isArray(selection) || typeof selection === "string") {
                return false;
            }

            for (var index in selection) {
                if (selection.hasOwnProperty(index) && selection[index] !== undefined) {
                    return false;
                }
            }

            return true;
        },

        _selectPendingSelection: function(pendingSelection, options) {
            if (this.get("bufferStartIndex") === 0 && this.get("bufferEndIndex") == this.get("total") - 1) {
                //All data from this range already fetched
                this._convertInitialArrayToSelection(this.get("items"), pendingSelection, options);
            } else {
                //Need to fetch data
                var that = this;
                this.getData().done(function(values) {
                    that._convertInitialArrayToSelection(values.data, pendingSelection, options);
                }).fail(this.fetchFailed);
            }
        },

        _convertInitialArrayToSelection: function(data, pendingSelection, options) {
            for (var i = 0; i < data.length; i++) {
                if (pendingSelection[data[i].value]) {
                    this._addToSelection(data[i].value, i);
                }
            }

            this._triggerSelectionChange(options);
        },

        _triggerSelectionChange: function(options) {
            this._calcSelectionStartIndex && this._calcSelectionStartIndex();
            BaseListWithSelectionModel.prototype._triggerSelectionChange.call(this, options);
        },

        _calcSelectionStartIndex: function() {
            if (typeof this.selectionStartIndex !== "undefined") {
                return;
            }

            for (var index in this.selection) {
                if (this.selection.hasOwnProperty(index) && this.selection[index] !== undefined) {
                    this.selectionStartIndex = parseInt(index, 10);
                    break;
                }
            }
        },

        /*-------------------------
         * API
         -------------------------*/

        /*
         Selects passed selection
         */
        select: function(selection, options) {
            this._clearSelection();

            if (this._isSelectionEmpty(selection)){
                this._triggerSelectionChange(options);
                return;
            }

            if (_.isArray(selection) || typeof selection === "string") {
                //If selection is an array with not matched indexes or simply a string,
                //need to initialize selection after first data fetch

                var pendingSelection = {};

                if (typeof selection === "string") {
                    pendingSelection[selection] = true;
                } else {
                    //Creating hash with selected values optimized for constant search time
                    for (var i = 0; i < selection.length; i++) {
                        pendingSelection[selection[i]] = true;
                    }
                }

                this._selectPendingSelection(pendingSelection, options);
            } else {
                //Ok, selection is just usual hash with indexes as keys -
                //can init it immediately
                for (var index in selection) {
                    if (selection.hasOwnProperty(index)) {
                        var value = selection[index];
                        if (value !== undefined) {
                            this._addToSelection(value, index);
                        }
                    }
                }

                this._triggerSelectionChange(options);
            }
        }
    });

    return ListWithSelectionModel;
});
