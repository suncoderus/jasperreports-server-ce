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
 * Single select component which supports view port, and filtering via search.
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        listWithNavigationModelTrait = require("components/scalableList/model/listWithNavigationModelTrait"),
        SingleSelect = require("components/singleSelect/view/SingleSelect"),
        SingleSelectListModelNew = require("components/singleSelect/model/SingleSelectListModelNew");

    var _getSelectionIndex = function(selection) {

        //Try to get selection index only if it's already in buffer
        var bufferStartIndex = this.listViewModel.get("bufferStartIndex");
        var items = this.listViewModel.get("items");

        for (var i = 0; i < items.length; i++) {
            if (selection === items[i].value) {
                return bufferStartIndex + i;
            }
        }

        return undefined;
    };

    var SingleSelectNew = SingleSelect.extend({

        initialize: function(options) {
            _getSelectionIndex = _.bind(_getSelectionIndex, this);
            this.formatValue = options.formatValue;
            SingleSelect.prototype.initialize.call(this, options);
        },

        _createListViewModel: function(options) {
            this.getData = options.getData;

            var model = options.model || _.extend(new SingleSelectListModelNew(options), {
                _afterSelect: this._afterSelect
            });

            return model;
        },

        _getGetData: function() {
            return this.getData;
        },

        /* Event handlers */
        // None

        /* Key handlers for KeyboardManager */
        // None

        /* Internal helper methods */

        //Overrides model's afterSelect method
        _afterSelect: function (selection, options) {
            if (selection && typeof selection === "string") {
                if (!this.getActive()) {
                    var index = _getSelectionIndex(selection);

                    if (typeof index !== "undefined") {
                        listWithNavigationModelTrait.setActive.call(this, {
                            index: index,
                            item: {value: selection},
                            silent: true
                        });
                    }
                }
            }
        },

        _getActiveValueIndex: function(active) {
            return _getSelectionIndex(active.value);
        },

        changeFilter: function() {
            var self = this;
            this.listView.scrollTo(0);

            //Assuming we use cacheable dataprovider which does caching by some window
            //so there will not be two getData calls
            this._getGetData()({
                criteria: this.model.get("criteria"),
                offset: 0,
                limit: 1
            }).done(function() {
                self.listView.fetch(function () {
                    self.listView.resize();
                    self.setValueToList();
                });
            });
        },

        getControlLabelByValue: function (value) {
            return value.label == null
                ? (this.formatValue ? this.formatValue(value.value) : value.value)
                : value.label;
        },

        setValue: function(value, options) {
            var value = this.convertExternalValueToInternalFormat(value);

            if (options && options.silent) {
                this.silent = true;
            }

            this.model.set("value", value);
            this.setValueToList(options);
        },

        setValueToList: function(options) {
            var selection = this.model.get("value"),
                value = {};

            if (selection && selection.index) {
                value[selection.index] = selection.value;
            } else if (selection) {
                value = selection.value;
            }

            this.listView.setValue(value, options);
        }

        /* API */
        // None

    });

    return SingleSelectNew;
});
