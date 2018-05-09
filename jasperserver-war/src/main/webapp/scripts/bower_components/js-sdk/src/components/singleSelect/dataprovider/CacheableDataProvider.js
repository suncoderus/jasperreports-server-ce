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
 * @version: $Id: CacheableDataProvider.js 1160 2015-04-28 12:46:42Z spriluki $
 */

/**
 * Proxy over getData function which should fetch data.
 * It emulates pagination on client side
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        _ = require("underscore");

    var CacheableDataProvider = function(data) {
        _.bindAll(this, "getData");

        this.data = data || [];
    };

    _.extend(CacheableDataProvider.prototype, {

        /* API */
        setData: function(data) {
            this.data = data || [];
        },

        getData: function(options) {

            var deferred = new $.Deferred();

            var first = options && options.offset ? options.offset : 0;
            var last = options && options.limit ? first + options.limit : this.data.length;

            var dataSegment = this._getDataSegment(this.data, first, last);

            deferred.resolve({data: dataSegment, total: this.data.length});

            return deferred.promise();
        },

        /* Internal methods */

        _getDataSegment: function(values, first, last) {
            last = Math.min(last, values.length);

            var result = [];

            for (var i = first; i < last; i++) {
                result.push({label: values[i].label, value: values[i].value})
            }

            return result;
        }
    });

    return CacheableDataProvider;
});
