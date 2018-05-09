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
 * @version: $Id: DataProviderWithLabelHash.js 1160 2015-04-28 12:46:42Z spriluki $
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        CacheableDataProvider = require("components/singleSelect/dataprovider/CacheableDataProvider");

    var DataProviderWithLabelHash = function() {
        CacheableDataProvider.apply(this, arguments);
    };

    _.extend(DataProviderWithLabelHash.prototype, CacheableDataProvider.prototype);

    _.extend(DataProviderWithLabelHash.prototype, {

        /* API */

        setData: function(data) {
            CacheableDataProvider.prototype.setData.call(this, data);

            this.dataLabelsHash = {};

            for (var i = 0; i < this.data.length; i++) {
                this.dataLabelsHash[this.data[i].value] = this.data[i].label;
            }
        },

        getDataLabelHash: function() {
            return this.dataLabelsHash;
        }
    });

    return DataProviderWithLabelHash;
});
