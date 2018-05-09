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

define(function (require) {

    "use strict";

    var $ = require('jquery'),
	    _ = require("underscore"),
	    i18n = require('bundle!all'),
	    Backbone = require('backbone'),
	    holidayCalendarTemplate = require("text!scheduler/template/editor/holidayCalendarTemplate.htm");

    return Backbone.View.extend({

        initialize: function () {
			// initialize view app
            this.collection.on('reset', this.onReset, this);
        },

        onReset: function() {

	        this.$el.empty().append($(_.template(holidayCalendarTemplate, {i18n: i18n})));

            var calendarBlock = this.$el.find(".calendarBlock");
            var calendarSelect = this.$el.find('[name=calendarSelect]');

            // who knows, maybe it has the 'disabled' class, so remove it.
            calendarBlock.removeClass("disabled").find("select").attr("disabled", false);
            calendarSelect.empty("");

            if (this.collection.size() === 0) {
                calendarSelect.append($("<option>").attr("value", "").text(i18n["report.scheduling.job.edit.trigger.calendars.nocalendars"]));
                // mark the whole block with class name as disabled
                calendarBlock.addClass("disabled").find("select").attr("disabled", "disabled");
                return;
            }

            calendarSelect.append($("<option>").attr("value", "").text(i18n["label.none"]));
            this.collection.forEach(function(calendar){
                calendarSelect.append($("<option>").attr("value", calendar.id).text(calendar.id));
            }, this );
        }
    });
});