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
 * Context menu.
 *
 * @author: Andriy Godovanets
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Menu = require("./Menu"),
        $ = require("jquery"),
        _ = require("underscore");

    return Menu.extend(/** @lends ContextMenu.prototype */{
        /**
         * @constructor ContextMenu
         * @classdesc Context menu that is shown at specified position.
         * @extends Menu
         * @param {array} options - Options for {@link Menu}.
         * @param {object} [additionalSettings] - Additional settings object. See {@link Menu} for more information.
         * @param {number} [additionalSettings.topPadding=5] - Top padding of context menu.
         * @param {number} [additionalSettings.leftPadding=5] - Left padding of context menu.
         * @example
         *      var contextMenu = new ContextMenu([ { label: "Save Dashboard", action: "save" } ]);
         */
        constructor: function(options, additionalSettings) {
            Menu.call(this, options, additionalSettings);
            _.bindAll(this, "_tryHide");

            this.topPadding = additionalSettings && additionalSettings.topPadding || 5;
            this.leftPadding = additionalSettings && additionalSettings.leftPadding || 5;

            this.hideOnMouseLeave = additionalSettings && additionalSettings.hideOnMouseLeave || false;
            this.hideOnMouseLeave && this.$el.on("mouseleave", this._tryHide);
        },

        /**
         * @description Hides context menu on another context menu event
         * @access protected
         */
        _tryHide: function() {
            this.hide();
            this._signOffTryHide();
        },

        _signOffTryHide: function() {
            $(document.body).off("click.contextMenu", this._tryHide);
        },

        /**
         * @description Shows context menu at specified position.
         * @param {object} position - Position object.
         * @param {number} position.left - Absolute position in px of context menu from left edge of the screen.
         * @param {number} position.top - Absolute position in px of context menu from top edge of the screen.
         * @param {number} [position.padding] - Padding with what context menu will be shown within container.
         * @param {jQuery} [container=$("body")] - containment of context menu.
         * @throws {Error} Required params (top, left) missing
         */
        show: function(position, container) {
            if (!position || !_.isNumber(position.top) || !_.isNumber(position.left)) {
                throw new Error("Required params (top, left) missing: " + JSON.stringify(position));
            }

            $(document.body).on("click.contextMenu", this._tryHide);

            var top = position.top,
                left = position.left,
                topPadding = this.topPadding,
                leftPadding = this.leftPadding,
                body = $("body"),
                menuElHeight = this.$el.height(),
                menuElWidth = this.$el.width(),
                containerHeight = container ?  container.height() : body.height(),
                containerWidth = container ? container.width() : body.width(),
                containerOffset = container ? container.offset() : body.offset(),
                fitByHeight = containerHeight - position.top,
                fitByWidth =  containerWidth - position.left;

            if(fitByHeight < menuElHeight){
                top = position.top - menuElHeight-topPadding;
                (top < containerOffset.top) && (top += containerOffset.top-top+topPadding);
                top = (top < 0) ? (containerHeight/2 - menuElHeight/2) : top
            }
            if(fitByWidth < menuElWidth){
                left = position.left - menuElWidth-leftPadding;
                (left < containerOffset.left) && (left += containerOffset.left-left+leftPadding);
                left = (left < 0) ? (containerWidth/2 - menuElWidth/2) : left
            }

            _.extend(this, {top: top, left: left});

            this.$el.css({ top: this.top, left: this.left });

            return Menu.prototype.show.apply(this, arguments);
        },



        /**
         * @description Removes ContextMenu from DOM and removes events handlers from BODY.
         */
        remove: function() {
            this._signOffTryHide();

            this.hideOnMouseLeave && this.$el.off("mouseleave", this._tryHide);

            Menu.prototype.remove.apply(this, arguments);
        }
    });
});
