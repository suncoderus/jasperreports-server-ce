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
 * Basic Menu component.
 *
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var $ = require("jquery"),
        _ = require("underscore"),
        OptionContainer = require("../base/OptionContainer"),
        menuOptionTemplate = require("text!./template/menuOptionTemplate.htm"),
        menuContainerTemplate = require("text!./template/menuContainerTemplate.htm");

    require("css!menu");

    return OptionContainer.extend(
        /** @lends Menu.prototype */
        {
        /**
         * @constructor Menu
         * @classdesc Basic Menu component. Works as a base for all other menu types.
         * @extends OptionContainer
         * @param {object[]} options - Menu options
         * @param {string} options[].label - Option label
         * @param {string} options[].action - Option action
         * @param {object} [additionalSettings] - Additional settings object.
         * @param {string} [additionalSettings.menuContainerTemplate] - HTML template for a menu container.
         * @param {string} [additionalSettings.menuOptionTemplate] - HTML template for a single menu option.
         * @param {boolean} [additionalSettings.toggle] - Flag indicates that menu options can be in active/inactive states.
         * @param {string} [additionalSettings.toggleClass] - CSS class that is added to menu option when option is active.
         * @throws {Error} Menu should have options
         * @example
         *  var menu = new Menu([ { label: "Save Dashboard", action: "save" } ], { toggle: true });
         */
        constructor: function(options, additionalSettings) {
            if (!options || !_.isArray(options) || options.length === 0) {
                throw new Error("Menu should have options");
            }

            additionalSettings || (additionalSettings = {});

            OptionContainer.call(this, {
                options: options,
                collection: additionalSettings.collection,
                mainTemplate: additionalSettings.menuContainerTemplate || menuContainerTemplate,
                optionTemplate: additionalSettings.menuOptionTemplate || menuOptionTemplate,
                toggle: additionalSettings.toggle,
                toggleClass: additionalSettings.toggleClass
            });

            this.maxSize = additionalSettings.maxSize;

            // extension point for traits
            this._onConstructor && this._onConstructor(options, additionalSettings);
        },

        initialize: function() {
            OptionContainer.prototype.initialize.apply(this, arguments);

            $("body").append(this.$el);

            // extension point for traits
            this._onInitialize && this._onInitialize();
        },

        show: function(){
            var res = OptionContainer.prototype.show.apply(this, arguments);
            this.applyMaxSize();
            return res;

        },
        /**
         * @description Applies max size according to property if set. Menu gets trincated ns scrollbar appears.
         */
        applyMaxSize: function () {
            if (this.maxSize && this.options.length > this.maxSize) {
                this.$el.css({
                    "overflow-y": "auto",
                    "max-height": this.maxSize * this.options[0].$el.height() + "px"
                });
            }
        }
    });
});