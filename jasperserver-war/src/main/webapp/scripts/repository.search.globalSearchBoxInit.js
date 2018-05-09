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

/* global SearchBox, primaryNavModule, encodeUriParameter */

var globalSearchBox = {
    _searchBox: null,
    _containerId: "globalSearch",
    _searchInputId: "searchInput",

    initialize: function() {
        this._searchBox = new SearchBox({
            id: this._containerId
        });

        this._searchBox.onSearch = function(text) {
            $(this._searchInputId).setValue(text);
            primaryNavModule.navigationPaths.search.params += '&' + "searchText=" + encodeUriParameter(text);
            primaryNavModule.navigationOption("search");
//            $(this._containerId).submit();
        }.bind(this);
    },

    setText: function(text) {
        this._searchBox.setText(text);
    }
};