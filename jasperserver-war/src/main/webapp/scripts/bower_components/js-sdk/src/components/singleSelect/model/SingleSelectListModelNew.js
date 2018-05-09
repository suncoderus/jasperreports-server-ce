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
 * @version: $Id: SingleSelectListModelNew.js 172 2014-09-23 11:45:30Z sergey.prilukin $
 */

/**
 * Model for list which is used as part of Single Select component.
 * It extends ListWithSelectionModel and add possibility
 * to select element by it's index.
 */

define(function (require) {
    'use strict';

    var SingleSelectListModel = require("components/singleSelect/model/SingleSelectListModel"),
        ListWithSelectionAsObjectHashModel = require("components/scalableList/model/ListWithSelectionAsObjectHashModel");

    var listPrototype = ListWithSelectionAsObjectHashModel.prototype;

    //Adding navigation abilities
    var SingleSelectListModelNew = SingleSelectListModel.extend({
        _addToSelection: listPrototype._addToSelection,

        _removeFromSelection: listPrototype._removeFromSelection,

        _clearSelection: listPrototype._clearSelection,

        _selectionContains: listPrototype._selectionContains,

        _getSelection: listPrototype._getSelection,

        //just remove unnecessary method
        _calcSelectionStartIndex: undefined,

        _select: listPrototype.select
    });

    return SingleSelectListModelNew;
});

