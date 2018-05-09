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
 * @author: Olesya Bobruyko
 * @version: $Id: addToSelectionModelTrait.js
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        ScalableListModel = require("common/component/list/model/ScalableListModel");

    return {
        afterFetchComplete:function(items, total) {
            ScalableListModel.prototype.afterFetchComplete.apply(this, arguments);

            var value,
                index;

            _.each(items, function(item, i) {
                if (item.addToSelection) {
                    item.selected = true;

                    value = item.value;
                    index = this.get("bufferStartIndex") + i;

                    this.selectionContains
                        && !this.selectionContains(value, index)
                        && this.addValueToSelection(value, index);

                    delete item.addToSelection;
                }
            }, this);
        }
    };

});
