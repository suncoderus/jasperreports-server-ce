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

    var ColorPicker = require("common/component/colorPicker/SimpleColorPicker"),
        ClickComponent = require("common/component/base/ClickComponent"),
        $ = require("jquery"),
        colors = require("./enum/colors"),
        template = require("text!common/component/colorPicker/template/simpleColorPickerTemplate.htm");

    return ColorPicker.extend(ClickComponent.extend(/** @lends SimpleAttachableColorPicker.prototype */{
        /**
         * @constructor SimpleAttachableColorPicker
         * @class SimpleAttachableColorPicker
         * @classdesc SimpleAttachableColorPicker component.
         * @extends ColorPicker
         * @extends ClickComponent
         * @param {jQuery|string|HTMLElement} attachTo - HTML DOM element, selector or jQuery object to attach colorpicker to.
         * @param {object} [padding={top: 5, left: 5}] - attachable component padding
         * @param {object} options Options for {@link SimpleColorPicker}
         */
        constructor: function(attachTo, padding, options){
            ClickComponent.call(this, attachTo, padding);
            ColorPicker.call(this, options);
        },

        initialize: function(){
            ColorPicker.prototype.initialize.apply(this);
            this.hide();
            $('body').append(this.$el);
        },

        /**
         * @description Select color and hides attachable color picker
         * @access protected
         * @fires SimpleColorPicker#color:selected
         */
        _selectColor: function(event){
            this.hide();
            ColorPicker.prototype._selectColor.apply(this, arguments);
        },

        /**
         * @description Remove attachable color picker from DOM
         */
        remove: function() {
            ClickComponent.prototype.remove.apply(this, arguments);
            ColorPicker.prototype.remove.apply(this, arguments);
        }

    }).prototype);
});
