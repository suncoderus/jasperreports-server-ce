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
 * Menu attached to an element which can be shown near this element
 *
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Menu = require("./Menu"),
        AttachableComponent = require("common/component/base/AttachableComponent");

    return Menu.extend(AttachableComponent.extend(/** @lends AttachableMenu.prototype */{
        /**
         * @constructor AttachableMenu
         * @classdesc Menu attached to an element which can be shown near this element.
         * @extends Menu
         * @extends AttachableComponent
         * @param {object[]} options - Options for menu. See {@link Menu} for more details.
         * @param {jQuery|HTMLElement|string} attachTo - Element to attach menu to.
         * @param {object} [padding={top: 5, left: 5}] - padding of attachable menu.
         * @param {object} [additionalSettings] - Additional settings object. See {@link Menu}.
         * @example
         *  var attachableMenu = new AttachableMenu([ { label: "Save Dashboard", action: "save" } ], "#someElement");
         */
        constructor: function(options, attachTo, padding, additionalSettings) {
            this.padding = padding || {top: 0, left: 0};
            AttachableComponent.call(this, attachTo, this.padding);
            Menu.call(this, options, additionalSettings);
        },

        /**
         * @description Shows menu near element.
         */
        show: function() {
            AttachableComponent.prototype.show.apply(this, arguments);
            return Menu.prototype.show.apply(this, arguments);
        }
    }).prototype);
});