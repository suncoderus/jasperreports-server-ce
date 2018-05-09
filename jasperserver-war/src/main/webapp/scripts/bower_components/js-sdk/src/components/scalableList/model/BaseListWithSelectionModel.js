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
 * @author: Sergey Prilukin
 * @version: $Id$
 */

/**
 * @class BaseListWithSelectionModel
 * @classdesc Model extended from ScalableListModel which allows to work with selection.
 * Base class for other selection implementations
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

/*global window, Backbone, _, jaspersoft.components */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        ScalableListModel = require("components/scalableList/model/ScalableListModel");

    var BaseListWithSelectionModel = ScalableListModel.extend(
        /** @lends BaseListWithSelectionModel.prototype */
        {

        initialize: function(options) {
            ScalableListModel.prototype.initialize.call(this, options);

            this.select(options.value, {silent: true});
        },

        /* Methods which supposed to be overridden */

        /**
         * Adding to selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _addToSelection: function(value, index) {
            throw "Not implemented";
        },

        /**
         * Removing from selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _removeFromSelection: function(value, index) {
            throw "Not implemented";
        },

        /**
         * Clearing selection should be done only through this method.
         * No direct change of this.selection object is allowed
         */
        _clearSelection: function() {
            throw "Not implemented";
        },

        /**
         * Selection contains should be checked only through this method
         * No direct change of this.selection object is allowed
         */
        _selectionContains: function(value, index) {
            throw "Not implemented";
        },

        /**
         * Returns current selection
         * No direct change of this.selection object is allowed
         */
        _getSelection: function() {
            throw "Not implemented";
        },

        /* Internal helper methods */

        _triggerSelectionChange: function(options) {
            if (!options || !options.silent) {
                this.trigger("change");
            }

            this.trigger("selection:change");
        },

        _addRangeToSelection: function(data, rangeStart, rangeEnd) {
            for (var i = 0; i < data.length; i++) {
                this._addToSelection(data[i].value, i + rangeStart);
            }

            this.trigger("selection:addRange", {start: rangeStart, end: rangeEnd, selection: this.getSelection()});
        },

        _fetchAllDataAndModifySelection: function(modifySelection, onDone) {
            this.getData().done(function(values) {
                for (var i = 0; i < values.data.length; i++) {
                    modifySelection(values.data[i].value, i);
                }

                onDone && onDone();
            }).fail(this.fetchFailed);

            return this;
        },

        /*-------------------------
         * API
         -------------------------*/

        /*
            Adds value selection and triggers add event
        */
        addValueToSelection: function(value, index) {
            this.selectionStartIndex = index;
            this._addToSelection(value, index);
            return this.trigger("selection:add", {value: value, index: index});
        },

        /*
            Adds value selection and triggers add event
        */
        addRangeToSelection: function(value, index) {

            //No range determined - use single item selection
            if (typeof this.selectionStartIndex === "undefined") {
                this.addValueToSelection(value, index);
                return;
            }

            //Same element selected no need to fire range selection
            if (this.selectionStartIndex === index) {
                return;
            }

            //Calculate range dimensions
            var rangeStart = Math.min(this.selectionStartIndex, index);
            var rangeEnd = Math.max(this.selectionStartIndex, index);
            this.selectionStartIndex = index;

            if (rangeStart >= this.get("bufferStartIndex") && rangeEnd <= this.get("bufferEndIndex")) {
                //All data from this range already fetched
                var data = Array.prototype.slice.call(this.get("items"),
                    rangeStart - this.get("bufferStartIndex"), rangeEnd - this.get("bufferStartIndex") + 1);
                this._addRangeToSelection(data, rangeStart, rangeEnd);
            } else {
                //Need to fetch data
                var that = this;
                this.getData({
                    offset: rangeStart,
                    limit: rangeEnd - rangeStart + 1}
                ).done(function(values) {
                    that._addRangeToSelection(values.data, rangeStart, rangeEnd);
                }).fail(this.fetchFailed);
            }
        },

        /*
             Removes value from selection and triggers remove event
         */
        removeValueFromSelection: function(value, index) {
            this._removeFromSelection(value, index);
            this.trigger("selection:remove", {value: value, index: index});
        },

        /*
            Does selection toggle for passed value
         */
        toggleSelection: function(value, index) {
            if (this.selectionContains(value, index)) {
                this.removeValueFromSelection(value, index);
            } else {
                this.addValueToSelection(value, index);
            }
        },

        /*
            Returns true if value present in selection
        */
        selectionContains: function(value, index) {
            return this._selectionContains(value, index);
        },

        /*
            Removes all items from selection
         */
        clearSelection: function() {
            this._clearSelection();
            return this.trigger("selection:clear");
        },

        /*
            Returns all selected values
        */
        getSelection: function() {
            return this._getSelection();
        },

        /*
            Selects passed selection
         */
        select: function(selection, options) {
            throw "Not implemented";
        },

        /*
            Selects all values
         */
        selectAll: function() {
            var self = this,
                firstIteration = true;

            this._fetchAllDataAndModifySelection(function(value, index) {

                //this is necessary when getData is slow operation
                //so model will not be empty too early
                if (firstIteration) {
                    self._clearSelection();
                    firstIteration = false;
                }

                self._addToSelection(value, index);
            }, _.bind(this._triggerSelectionChange, this));
        },

        /*
            Inverts selection
         */
        invertSelection: function() {
            var self = this;

            this._fetchAllDataAndModifySelection(function(value, index) {
                if (self._selectionContains(value, index)) {
                    self._removeFromSelection(value, index);
                } else {
                    self._addToSelection(value, index);
                }
            }, _.bind(this._triggerSelectionChange, this));
        }
    });

    return BaseListWithSelectionModel;
});
