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
 * @version: $Id: SingleSelectListModel.js 172 2014-09-23 11:45:30Z sergey.prilukin $
 */

/**
 * Model for list which is used as part of Single Select component.
 * It extends ListWithSelectionModel and add possibility
 * to select element by it's index.
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        ListWithSelectionModel = require("components/scalableList/model/ListWithSelectionModel"),
        listWithNavigationModelTrait = require("components/scalableList/model/listWithNavigationModelTrait");

    //Adding navigation abilities
    var SingleSelectListModel = ListWithSelectionModel.extend(listWithNavigationModelTrait);

    // Adding specific abilities
    SingleSelectListModel = SingleSelectListModel.extend({

        /* Methods which supposed to be overridden */

        //Override setActive From listWithNavigationModelTrait
        setActive: function(options) {
            listWithNavigationModelTrait.setActive.call(this, _.extend(options, {silent: true}));

            var selection = {};

            if (typeof options.index === "number") {
                selection[options.index] = options.item.value;
            }

            this.select(selection);
        },

        _convertInitialArrayToSelection: function(data, pendingSelection, options) {
            for (var i = 0; i < data.length; i++) {
                if (pendingSelection[data[i].value]) {
                    this._addToSelection(data[i].value, i);

                    //If active element was not set yet we have to set it here
                    if (!this.getActive()) {
                        listWithNavigationModelTrait.setActive.call(this, {
                            index: i, item: data[i], silent: true
                        });
                    }
                }
            }

            this._triggerSelectionChange(options);
        },

        //Method which converts {"1": "value"} to this {index: 1, value: "value}
        _convertSelectionObject: function(selection) {
            for (var index in selection) {
                if (selection.hasOwnProperty(index) && selection[index] !== undefined) {
                    var value = selection[index];

                    return {
                        index: parseInt(index, 10),
                        value: value
                    }
                }
            }
        },

        //Original select method
        _select: ListWithSelectionModel.prototype.select,

        /* API */

        select: function(selection, options) {
            var convertedSelection;
            var shouldFetchLabel = false;

            //We have to reset active value to match selected value if it wasn't done already
            if (!selection || typeof selection === "string" || _.isArray(selection)) {
                listWithNavigationModelTrait.setActive.call(this, {silent: true});
            } else {
                convertedSelection = this._convertSelectionObject(selection);

                if (!convertedSelection) {
                    //If selection is empty object - clear active value
                    listWithNavigationModelTrait.setActive.call(this, {silent: true});
                } else {
                    var active = this.getActive();
                    if (!active || convertedSelection.value !== active.value || convertedSelection.index !== active.index) {
                        shouldFetchLabel = true;
                    }
                }
            }

            if (shouldFetchLabel) {
                //selection is different from current active value
                //We should set correct active value, but we have no label to do this.
                //Since selection is an object there will no be getData call
                //so we have to fetch label by our self.
                this.activate(convertedSelection.index);
            } else {
                this._select(selection, options);
            }

            return this;
        }
    });

    return SingleSelectListModel;
});

