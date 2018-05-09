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
 * @author: Zakhar Tomchenko, Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function(require){
    "use strict";

    var _ = require("underscore"),
        $ = require("jquery"),
        abstractPanelTrait = require("./abstractPanelTrait"),
        ClickMenu = require('common/component/menu/ClickMenu'),
        groupMenuTrait = require('common/component/menu/groupMenuTrait'),
        menuPanelMarkup = require("text!../template/menuPanelTemplate.htm");

    var GroupMenu = ClickMenu.extend(groupMenuTrait);

    /**
     * @mixin menuPanelTrait
     * @description Add context menu to panel.
     * @extends abstractPanelTrait
     */
    return _.extend({}, abstractPanelTrait, {
        /**
         * @description Initialize additional Panel options.
         * @memberof! menuPanelTrait
         * @param {object} [options]
         * @param {object} [options.menuOptions] Options for menu. See {@link ClickMenu}, {@link groupMenuTrait}
         * @param {boolean} [options.menuOptionSelectable] If option can be selected.
         */
        onConstructor: function(options) {
            options || (options = {});

            this.menuOptions = options.menuOptions;
            this.menuOptionSelectable = options.menuOptionSelectable;
        },

        /**
         * @description Initialize menu.
         * @memberof! menuPanelTrait
         * @fires menuPanelTrait#option:ID
         */
        afterSetElement: function(){
            this.$menuEl = $(menuPanelMarkup);
            this.$el.find(".title").after(this.$menuEl);

            this.filterMenu = new GroupMenu(this.menuOptions, this.$menuEl, { toggle: this.menuOptionSelectable, toggleClass: "active" });

            this.listenTo(this.filterMenu, "all", function(name, view, model){
                if (name.indexOf(this.filterMenu.contextName) >= 0) {
                    this.filterMenu.hide();
                    /**
                     * @event menuPanelTrait#option:ID
                     * @description This event is fired when option with corresponding ID is selected
                     */
                    this.trigger(name, view, model);
                }
            }, this);
        },

        /**
         * @description Destroy menu.
         * @memberof! menuPanelTrait
         */
        onRemove: function() {
            this.filterMenu.remove();
        }
    });
});
