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

/* global cancelEventBubbling */

define(function(require) {
    "use strict";

    var jQuery = require("jquery"),
        _ = require("underscore"),
        dateTimeSettings = require("settings!dateTimeSettings");

    require("utils.common");
    require("config/dateAndTimeSettings");

    var CalendarInputComponent = function(options) {
        this.container = null;
        if (typeof options.container !== "undefined") {
            this.container = typeof options.container.jquery === "undefined" ? jQuery(options.container) : options.container;
        }
        this.name = options.name;
        this.id = options.name.replace(".", "_");
        this.value = options.value;
        this.onChange = options.onchange || null;
        this.isReadOnly = typeof options.readOnly === "undefined" ? false : options.readOnly;
        this.hasDate = typeof options.date !== "undefined" && options.date !== "" && options.date === "true";
        this.hasTime = typeof options.time !== "undefined" && options.time !== "" && options.time === "true";

        this.pickerOptions = _.extend({}, this.defaultPickerOptions);

        this.hasDate && _.extend(this.pickerOptions, dateTimeSettings.datepicker);
        this.hasTime && _.extend(this.pickerOptions, dateTimeSettings.timepicker);

        if (typeof options.picker !== "undefined" && _.isObject(options.picker)) {
            _.extend(this.pickerOptions, options.picker);
        }

        this.field = null;
    };

    CalendarInputComponent.prototype.defaultPickerOptions = {
        showOn: "button",
        buttonText: "",
        changeYear: true,
        changeMonth: true,
        showButtonPanel: true,
        onChangeMonthYear: null,
        beforeShow: jQuery.datepicker.movePickerRelativelyToTriggerIcon
    };

    CalendarInputComponent.prototype.create = function() {
        var inputField = jQuery("<input type='text'/>").attr({
            name: this.name,
            id: this.id,
            value: this.value
        });

        inputField.on("mousedown", cancelEventBubbling);

        if (this.onChange) {
            inputField.on("change", this.onChange);
        }

        if (this.isReadOnly) {
            inputField.attr("disabled", "disabled");
        }

        this.field = inputField;

        this.container.append(this.field);

        if (!this.isReadOnly) {
            var pickerType = this.hasDate ? "date" : "";
            pickerType += this.hasTime ? "time" : "";
            pickerType += "picker";

            jQuery.fn[pickerType].call(inputField, this.pickerOptions).next().addClass('button').addClass('picker');

            // Prototype.js compatibility
            inputField[0].getValue = function () {
                return jQuery(this).val();
            };
        }
    };

    return CalendarInputComponent;
});
