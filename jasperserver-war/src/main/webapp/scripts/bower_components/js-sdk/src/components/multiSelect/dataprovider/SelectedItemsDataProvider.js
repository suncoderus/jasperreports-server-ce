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
 * @version: $Id: SelectedItemsDataProvider.js 1160 2015-04-28 12:46:42Z spriluki $
 */

/**
 * DataProvider proxy for SelectedItemsList
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        CacheableDataProvider = require("components/singleSelect/dataprovider/CacheableDataProvider");

    var SelectedItemsDataProvider = function(options) {
        options = options || {};

        _.defaults(options, {
            sortFunc: this._sortFunc,
            formatLabel: this._formatLabel
        });

        this.sortFunc = options.sortFunc;
        this.formatLabel = options.formatLabel;

        CacheableDataProvider.call(this, options);
    };

    _.extend(SelectedItemsDataProvider.prototype, CacheableDataProvider.prototype);

    _.extend(SelectedItemsDataProvider.prototype, {

        /* API */
        setData: function(data) {
            data = this._convertToStandardData(data);
            data = this._sortData(data);

            CacheableDataProvider.prototype.setData.call(this, data);
        },

        /* internal methods */
        _sortFunc: function(el) {
            return el.label;
        },

        _formatLabel: function(value) {
            return value;
        },

        _sortData: function(data) {
            return _.sortBy(data, this.sortFunc);
        },

        _convertToStandardData: function(data) {
            var self = this;

            return _.map(data, function(value) {
                return {
                    value: value,
                    label: self.formatLabel(value)
                }
            });
        }
    });

    return SelectedItemsDataProvider;
});
