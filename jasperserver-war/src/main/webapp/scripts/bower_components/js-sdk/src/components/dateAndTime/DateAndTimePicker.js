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
 * @author: Igor Nesterenko
 * @version: $Id$
 */

define(function (require, config, module) {
    "use strict";

    var Backbone = require("backbone"),
        $ = require("jquery"),
        _ = require("underscore"),
        popupContainerTemplate = require("text!./template/dateTimePopupContainer.htm"),
        DATE_TIME_DEFAULTS = {
            stepHour: 1,
            stepMinute: 1,
            stepSecond: 1,
            showSecond: true,
            changeMonth: true,
            changeYear: true,
            showButtonPanel: true,
            onChangeMonthYear: null,
            constrainInput: false
        },
        TIME_DEFAULTS = {
            showSecond:true,
            constrainInput:false
        },
        log = require("logger").register(module);

    require("jquery-ui/jquery.ui.datepicker");
    require("jquery-ui/jquery.ui.timepicker");

    $.timepicker.log = function (err) {
        //redirect error messages to our logger
        log.warn(err);
    };

    return Backbone.View.extend({

        /**
         * @constructor DateAndTimePicker
         * @class DateAndTimePicker
         * @classdesc DateAndTimePicker decorator over jQuery datetime pickers
         * @extends Backbone.View
         * @param {object} options - mixin on Backbone standard properties and jQuery datetime pickers
         * @description - You have to pass 'el' property to point to element in DOM. We have next behaviour
         *  - by default it renders with  datetimepicker
         *  - if only `dateFormat` it renders with datepicker
         *  - if only `timeFormat` it renders with  timepicker
         *  - if `dateFormat` and `timeFormat` specified it renders with datetimepicker
         */

        constructor: function (options) {
            this.pickerOptions = _.clone(options);

            if (this.pickerOptions.hasOwnProperty("dateFormat") && !_.isString(this.pickerOptions.dateFormat)) {
                delete this.pickerOptions.dateFormat;
            }
            if (this.pickerOptions.hasOwnProperty("timeFormat") && !_.isString(this.pickerOptions.timeFormat)) {
                delete this.pickerOptions.timeFormat;
            }

            this.inline = !!this.pickerOptions.el || false;
            this.skipMoving = this.pickerOptions.skipMoving || false;
            //clean up from backbone props
            delete this.pickerOptions.el;
            delete this.pickerOptions.skipMoving;

            this.pickerType = discoverPickerType(this.pickerOptions);

            this.pickerOptions = provideDefaultPickerOptions(this.pickerOptions, this.pickerType);

            this.pickerOptions = fixPopupPositionAndStyling(this.pickerOptions, this.skipMoving);

            this.log = options.log ? options.log : log;

            Backbone.View.apply(this, arguments);
        },

        initialize: function () {
            var self = this;

            //initialize picker
            this.$el[this.pickerType](this.pickerOptions);

            this._callPickerAction = function (options) {
                self.$el[self.pickerType](options);
            }
        },

        /**
         * @return {Date} - standart date object
         * @description Return Date if components has it
         */

        getDate: function(){
            var date;

            if (this.pickerType !== "timepicker"){
                date = this.$el[this.pickerType].getDate();
            }else{
                date = this.$el[this.pickerType].getTime();
            }
            return date;
        },

        /**
         * @params {Date|String} date
         * @return this
         */

        setDate: function(date){

            if (!date) return this;

            try{
                //try to convert string to date object
                // we don't need to convert time because setTime method accepts string time format
                if (_.isString(date)){
                    if (this.pickerType === "datetimepicker") {
                        date = $.datepicker.parseDateTime(this.pickerOptions.dateFormat, this.pickerOptions.timeFormat,  date);
                    } else if (this.pickerType === "datepicker") {
                        date = $.datepicker.parseDate(this.pickerOptions.dateFormat, date);
                    }
                }

                if (this.pickerType === "datepicker"){
                    this.$el[this.pickerType]("setDate", date);
                }else if (this.pickerType === "timepicker") {
                    this.$el[this.pickerType]("setTime", date);
                }else if (this.pickerType === "datetimepicker"){
                    this.$el[this.pickerType]("setTime", date);
                    this.$el[this.pickerType]("setDate", date);
                }

            }catch(err){
                this.log.debug(err);
            }
            return this;
        },

        /**
         * @description Show picker
         */

        show: function () {
            this._callPickerAction("show");
            return this;
        },

        /**
         * @description Hide picker
         */

        hide: function () {
            this._callPickerAction("hide");
            return this;
        },

        remove: function () {
            this._callPickerAction("destroy");
            if (this.inline) {
                this.$el.empty().off();
                this.stopListening();
            } else {
                Backbone.View.prototype.remove.apply(this, arguments);
            }

        }

    }, {

        /**
         * @param {object} configs - which should contains 'locale', configs for 'date' amd configs for 'time'
         * @description Provide defaults for locale specific configuration
         */

        setDefaults : function(configs){
            $.datepicker.regional[configs.locale] = configs.date;
            $.datepicker.setDefaults(configs.date);
            $.timepicker.setDefaults(configs.time);
        },

        Helpers : {

            fixPopupPositionAndStyling: fixPopupPositionAndStyling,

            movePickerRelativelyToTriggerIcon: movePickerRelativelyToTriggerIcon,

            stylePopupContainer: stylePopupContainer,

            discoverPickerType: discoverPickerType,

            provideDefaultPickerOptions: provideDefaultPickerOptions
        }
    });

    function discoverPickerType(options) {
        var result = "datetimepicker";
        if (options.dateFormat && options.timeFormat) {
            return result;
        } else if (options.dateFormat) {
            result = "datepicker"
        } else if (options.timeFormat) {
            result = "timepicker"
        }
        return result;
    }

    function provideDefaultPickerOptions(options, type){

        var result = options;

        if ("datetimepicker" == type || "datepicker" == type){
            result = _.defaults(options, DATE_TIME_DEFAULTS);
        }else if("timepicker" == type){
            result = _.defaults(options, TIME_DEFAULTS)
        }

        return result;
    }

    function fixPopupPositionAndStyling(options, skipMoving) {

        var originalBeforeShowFn = options.beforeShow;

        options.beforeShow = function () {
            stylePopupContainer.apply(this, arguments);
            skipMoving || movePickerRelativelyToTriggerIcon.apply(this, arguments);
            originalBeforeShowFn && originalBeforeShowFn.apply(this, arguments);
        };

        return options;
    }

    function movePickerRelativelyToTriggerIcon(input, inst) {
        var offset = $(input).offset().left;
        var width = parseFloat(inst.dpDiv.css("width").replace("px", ""));
        var move = offset + input.offsetWidth + width < $(window).width();
        inst.dpDiv.css({
            marginLeft: move ? input.offsetWidth + "px" : 0
        });
    }

    function stylePopupContainer(input, inst) {
        var $popupContainer = inst.dpDiv,
            $parent = $popupContainer.parent(),
            $dpDiv;

        if ($parent && $parent.is('body')) {
            $dpDiv = $popupContainer.detach();
            $popupContainer = $(popupContainerTemplate);
            $popupContainer.append($dpDiv);
            $('body').append($popupContainer);
        }
    }

});
