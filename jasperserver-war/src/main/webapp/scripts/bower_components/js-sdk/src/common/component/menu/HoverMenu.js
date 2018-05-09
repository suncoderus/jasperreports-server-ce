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
 * Menu that is shown near element when element is mouseovered.
 *
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var AttachableMenu = require("./AttachableMenu"),
        _ = require("underscore");

    return AttachableMenu.extend(
        /** @lends HoverMenu.prototype */
        {

        _isVisible: false,
        _elementHovered: false,
        _menuHovered: false,
        TIME_BETWEEN_MOUSE_OVERS: 200,

        /**
        * @constructor HoverMenu
        * @classdesc Menu that is shown near element when element is mouseovered.
        * @extends AttachableMenu
        * @param {array} options - Array containing objects with "label" and "action" properties, e.g. [ { label: "Save Dashboard", action: "save" } ].
        * @param {jQuery|HTMLElement|selector} attachTo - Element to attach menu to.
        * @param {object} [padding={top: 5, left: 5}] - padding of attachable menu.
        * @param {object} [additionalSettings] - Additional settings object. For more details on this see Menu.js
        * @throws "Menu should have options" error if no menu options were passed to constructor.
        * @throws "AttachableComponent should be attached to an element" error if attachTo is missing.
        * @fires option:action events when menu option is selected.
        * @example
        *  var hoverMenu = new HoverMenu([ { label: "Save Dashboard", action: "save" } ], $("#someElement"), { toggle: true });
        */
        constructor: function(options, attachTo, padding, additionalSettings) {
            _.bindAll(this, "_onAttachToMouseOver", "_onAttachToMouseOut");

            this.padding = padding || {top: 0, left: 0};
            AttachableMenu.call(this, options, attachTo, this.padding, additionalSettings);

	        // listen to synthetic events from OptionContainer which we inherit from
            this.on("mouseover", this._onMenuItemMouseOver);
            this.on("mouseout", this._onMenuItemMouseOut);
	        this.on("selectionMade", this._hide);
        },

        /**
         * Replace element to which component will be attached
         * @param attachTo component to which HoverMenu will be attached
         */
        setAttachTo: function(attachTo) {
            this._removeEventListeners();

            AttachableMenu.prototype.setAttachTo.call(this, attachTo);

            this._addEventListeners();
        },

        /**
         * @description on hover menu mouse over event handler.
         * @access protected
         */
        _onMenuItemMouseOver: function() {
            this._menuHovered = true;
        },

        /**
         * @description on hover menu mouse out event handler.
         * @access protected
         */
        _onMenuItemMouseOut: function() {
            this._menuHovered = false;

            this._hideByTimeout();
        },

        /**
         * @description on attached element mouse over event handler.
         * @access protected
         */
        _onAttachToMouseOver: function() {

            if (this.$attachTo.is(":disabled")) {
                return;
            }

            this._elementHovered = true;

            if (!this._isVisible) {
	            this.show();
	            this._isVisible = true;
            }
        },

        /**
         * @description on attached element mouse out event handler.
         * @access protected
         */
        _onAttachToMouseOut: function() {
            this._elementHovered = false;

            this._hideByTimeout();
        },


        /**
         * @description tried to hide menu after some timeout
         * @access protected
         */
        _hideByTimeout: function() {

	        if (this._elementHovered || this._menuHovered) {
		        return;
	        }

            setTimeout(_.bind(this._tryHide, this), this.TIME_BETWEEN_MOUSE_OVERS);
        },

        /**
         * @description tries to hide menu.
         * @access protected
         */
        _tryHide: function() {

	        if (this._elementHovered || this._menuHovered) {
		        return;
	        }

	        this._hide();
        },

        /**
         * @description hides menu.
         * @access protected
         */
        _hide: function() {

	        this.hide();
	        this._isVisible = false;

	        this.trigger("hidden");
        },

        _addEventListeners: function() {
            this.$attachTo && this.$attachTo.on("mouseover", this._onAttachToMouseOver);
            this.$attachTo && this.$attachTo.on("mouseout", this._onAttachToMouseOut);
        },

        _removeEventListeners: function() {
            this.$attachTo && this.$attachTo.off("mouseover", this._onAttachToMouseOver);
            this.$attachTo && this.$attachTo.off("mouseout", this._onAttachToMouseOut);
        },

        /**
         * @description Removes HoverMenu from DOM and remove event handlers from element that menu is attached to.
         */
        remove: function() {

            this._removeEventListeners();

            AttachableMenu.prototype.remove.apply(this, arguments);
        }
    });
});