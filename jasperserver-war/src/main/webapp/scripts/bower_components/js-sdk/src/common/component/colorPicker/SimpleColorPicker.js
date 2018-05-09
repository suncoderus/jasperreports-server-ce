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


define(function(require){
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        $ = require("jquery"),
        i18n = require("bundle!js-sdk/CommonBundle"),
        colors = require("./enum/colors"),
        template = require("text!common/component/colorPicker/template/simpleColorPickerTemplate.htm");

    require("css!simpleColorPicker");

    return Backbone.View.extend(/** @lends SimpleColorPicker.prototype */{
        events: {
            "click .color": "_selectColor"
        },

        /**
         * @constructor SimpleColorPicker
         * @class SimpleColorPicker
         * @classdesc SimpleColorPicker component.
         * @extends Backbone.View
         * @param {object} options
         * @param {string} [options.label] Color picker label
         * @param {boolean} [options.showTransparentInput] Show transparent (to set transparent property) input field.
        */
        constructor: function(options){
            options || (options = {});

            this.label = options && options.label;
            this.showTransparentInput = options && options.showTransparentInput;
            this.showNoneInput = options && options.showNoneInput;

            Backbone.View.apply(this, arguments);
        },

        el: function() {
            return _.template(template)({
                colors: colors, i18n: i18n,
                label: this.label,
                showTransparentInput: this.showTransparentInput,
                showNoneInput: this.showNoneInput
            });
        },

        /**
         * @description Highlights color in color picker.
         * @param {string} color - hex color value
        */
        highlightColor: function(color){
            var colorBox, index;

            if (color === null) {
                colorBox = this.$el.find(".color.none");
            } else if (color === "rgba(0, 0, 0, 0)" || color === "transparent") {
                colorBox = this.$el.find(".color.transparent");
            } else {
                index = _.indexOf(colors, color);
                index >= 0 && (colorBox =  this.$el.find("div[data-index='" + index + "']"));
            }

            this.$el.find(".color.transparent.selected, .color.none.selected, .colorWrapper.selected").removeClass("selected");
            colorBox && colorBox.addClass("selected");
        },

        /**
         * @description Selects color. Triggers event.
         * @access protected
         * @fires SimpleColorPicker#color:selected
         */
        _selectColor: function(event){
            var colorEl = $(event.target),
                color;

            if (colorEl.is(".none")) {
                color = null;
            } else {
                color = colorEl.css("background-color");
            }
            this.highlightColor(color);

            /**
             * @event SimpleColorPicker#color:selected
             */
            this.trigger("color:selected", color);
        },

        /**
         * @description Show color picker.
         */
        show: function(){
            this.$el.show();
        },

        /**
         * @description Hide color picker.
         */
        hide: function(){
            this.$el.hide();
        }
    });
});
