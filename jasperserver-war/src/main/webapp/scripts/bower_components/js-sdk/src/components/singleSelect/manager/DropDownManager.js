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
 * @version: $Id: DropDownManager.js 2376 2016-03-09 15:34:44Z psavushc $
 */

/**
 * DropDownManager to handle dropdown parts of scalable components
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        $ = require("jquery");

    var RECALC_DROP_DOWN_POS_INTERVAL = 500;

	var $body = $("body");

    var DropDownManager = function(options) {
        this.initialize(options);
        return this;
    };

    _.extend(DropDownManager.prototype, {

        initialize: function(options) {
            _.bindAll(this, "checkOffset", "setNewOffset");
            this.$dropDownEl = options.dropDownEl;
            this.calcOffset = options.calcOffset;
            this.isOffsetChanged = options.isOffsetChanged || this._isOffsetChanged;
            this.recalcInterval = options.recalcInterval || RECALC_DROP_DOWN_POS_INTERVAL;
            this.onOffsetChanged = options.onOffsetChanged;
        },

        /* API */

        startCalc: function() {
            this.checkOffset(this.setNewOffset);
            clearInterval(this.recalcTimer);
            this.recalcTimer = setInterval(
                _.bind(this.checkOffset, this, this.onOffsetChanged),
                this.recalcInterval);
        },

        stopCalc: function() {
            clearInterval(this.recalcTimer);
        },

        /* Internal methods */
	    checkOffset: function(callback) {
            var whereItNeedsToBe = this.calcOffset();
            var currentOffset = this.$dropDownEl.position();
            var bodyOffset = $body.offset();

            // hardfix http://jira.jaspersoft.com/browse/JRS-3086
            if (bodyOffset.top || bodyOffset.left) {
                currentOffset.top += bodyOffset.top;
                currentOffset.left += bodyOffset.left;
            }

            if (this.$dropDownEl.width() !== whereItNeedsToBe.width) {
                this.setNewOffset(whereItNeedsToBe);
            }

            if (this.isOffsetChanged(currentOffset, whereItNeedsToBe)) {
                callback && callback(whereItNeedsToBe);
            }
        },

        setNewOffset: function(newDimensions) {
            this.$dropDownEl.css("top", newDimensions.top - $body.offset().top)
                .css("left", newDimensions.left - $body.offset().left)
                .css("width", newDimensions.width);
        },

        _isOffsetChanged: function(currentDimensions, newDimensions) {
            //Math.floor is necessary for IE10 which can use float numbers for position
            return (Math.floor(currentDimensions.top) !== Math.floor(newDimensions.top)
                || Math.floor(currentDimensions.left) !== Math.floor(newDimensions.left));
        }
    });

    return DropDownManager;
});
