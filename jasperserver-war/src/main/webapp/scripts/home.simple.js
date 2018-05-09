/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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

/* global webHelpModule, _ */

var home = {
    _locationMap: {},

    initialize: function(options) {
        webHelpModule.setCurrentContext("bi_overview");

        if (options.locationMap) {
            this._locationMap = options.locationMap;
        }

        this._initHandlers();
    },

    _initHandlers: function() {
        var buttons = $(document.body).select('.button.action.jumbo');

        buttons.each(function(button) {
            $(button).observe('click', function(e) {
                var buttonId = button.identify();

                if (this._locationMap[buttonId]) {
                    if(_.isFunction(this._locationMap[buttonId])) {
                        (this._locationMap[buttonId])();
                    } else {
                        document.location = this._locationMap[buttonId];
                    }
                }
            }.bindAsEventListener(home));
        });
    }
};
