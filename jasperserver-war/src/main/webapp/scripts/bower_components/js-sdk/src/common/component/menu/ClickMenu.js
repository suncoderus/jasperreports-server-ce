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
 * Context menu that is invoked when user clicks on an element.
 *
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Menu = require("common/component/menu/Menu"),
        ClickComponent = require("common/component/base/ClickComponent");

    return Menu.extend(ClickComponent.extend(/** @lends ClickMenu.prototype */{
        /**
         * @constructor ClickMenu
         * @classdesc Context menu that is invoked when user clicks on an element.
         * @extends Menu
         * @extends ClickComponent
         * @param {array} options - Options for {@link Menu}.
         * @param {jQuery|HTMLElement|string} attachTo - Element to attach menu to.
         * @param {object} [additionalSettings] - Additional settings object. See {@link Menu} for more details.
         * @example
         *  var clickMenu = new ClickMenu([ { label: "Save Dashboard", action: "save" } ], "#someElement", { toggle: true });
         */
        constructor: function(options, attachTo, additionalSettings) {
            ClickComponent.call(this, attachTo);
            // prevent memory leaks
            try{
                Menu.call(this, options, additionalSettings);
            }catch(e){
                ClickComponent.prototype.remove.apply(this, arguments);
                throw e;
            }
        },

        /**
         * @description Shows menu.
         */
        show: function(){
            ClickComponent.prototype.show.apply(this, arguments);
            return Menu.prototype.show.apply(this, arguments);
        },

        /**
         * @description Removes menu from DOM and unsubscribes from all event handlers.
         */
        remove: function() {
            ClickComponent.prototype.remove.apply(this, arguments);
            Menu.prototype.remove.apply(this, arguments);
        }
    }).prototype);
});