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
 * @version: $Id: ListWithSelection.js 43947 2014-04-02 17:51:07Z sergey.prilukin $
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        $ = require("jquery");

    var ValueFormattingDataProvider = function(options) {
        _.bindAll(this, "getData");

        options = options || {};

        this.format = options.format;

        //original data provider
        this.request = options.request;
    };

    _.extend(ValueFormattingDataProvider.prototype, {


        /* API */

        getData: function(options) {
            var deferred = $.Deferred();

            this.request(options).done(_.bind(this._requestDone, this, deferred)).fail(deferred.reject);

            return deferred.promise();
        },

        //Called when external request is done
        _requestDone: function(deferred, data) {
            if (this.format && data && data.data) {
                var values = data.data;

                if (values.length > 0 && !values[0].label) {
                    for (var i = 0; i < values.length; i++) {
                        values[i].label = this.format(values[i].value);
                    }
                }
            }

            deferred.resolve(data);
        }
    });

    return ValueFormattingDataProvider;
});
