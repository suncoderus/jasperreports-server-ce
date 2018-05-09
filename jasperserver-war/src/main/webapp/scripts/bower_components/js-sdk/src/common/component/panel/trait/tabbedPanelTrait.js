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

    var _ = require('underscore'),
        $ = require('jquery'),
        Backbone = require("backbone"),
        abstractPanelTrait = require("./abstractPanelTrait"),
        OptionContainer = require("common/component/base/OptionContainer"),
        panelButtonTemplate = require("text!../template/tabbedPanelButtonTemplate.htm");

    /**
     * @event tabbedPanelTrait#tab:ID
     * @description This event is fired when tab with corresponding ID is selected.
     */
    function onTabSelect(tabView, tabModel) {
        var tabAction = tabModel.get("action");

        if (!this._contentRendered[tabAction]) {
            this._contentRendered[tabAction] = true;
            this.tabs[tabAction].render && this.tabs[tabAction].render();
        }

        this.$tabs.hide();
        this.$tabs.filter(function() { return $(this).data("tab") === tabAction; }).show();
        this.selectedTab = tabAction;
        this.trigger("tab:" + tabAction, tabView, tabModel);
    }

    /**
     * @mixin tabbedPanelTrait
     * @description Extend panel with tabs.
     * @extends abstractPanelTrait
     */
    return _.extend({}, abstractPanelTrait, {
        /**
         * @description Initialize tab options.
         * @memberof! tabbedPanelTrait
         * @param {object} options
         * @param {array} options.tabs Data for tabs.
         * @param {string} [options.tabContainerClass="tabContainer"] CSS class for tabs container in DOM.
         * @param {string} [options.tabHeaderContainerSelector="> .header > .tabHeaderContainer"] CSS selector for tabs header in DOM.
         * @param {string} [options.tabHeaderContainerClass="tabHeaderContainer"] CSS class for tabs header in DOM.
         * @param {string} [options.tabbedPanelClass="tabbedPanel"] CSS class for Panel.
         * @throws {Error} Error if no data for tabs was provided
         */
        onConstructor: function(options) {
            options || (options = {});

            if (!options.tabs || !_.isArray(options.tabs) || options.tabs.length === 0) {
                throw new Error("Tabbed panel should have at least one tab");
            }

            this.tabContainerClass = options.tabContainerClass || "tabContainer";
            this.tabHeaderContainerSelector = options.tabHeaderContainerSelector || "> .header > .tabHeaderContainer";
            this.tabHeaderContainerClass = options.tabHeaderContainerClass || "tabHeaderContainer";
            this.tabbedPanelClass = options.tabbedPanelClass || "tabbedPanel";

            this.tabs = {};

            _.each(options.tabs, _.bind(function(tab) {
                this.tabs[tab.action] = tab.content;
                delete tab.content;
            }, this));
        },

        /**
         * @description Build DOM for tabs.
         * @memberof! tabbedPanelTrait
         */
        afterSetElement: function() {
            this.$el.addClass(this.tabbedPanelClass);

            this.$tabHeaderContainer = this.$(this.tabHeaderContainerSelector);

            if (!this.$tabHeaderContainer.length) {
                this.$tabHeaderContainer = $("<div></div>").addClass(this.tabHeaderContainerClass);
                this.$("> .header").append(this.$tabHeaderContainer);
            }

            this.$contentContainer.empty();

            _.each(this.tabs, _.bind(function(content, tabAction) {
                var tabContainer = $("<div></div>").addClass(this.tabContainerClass);
                tabContainer.data("tab", tabAction);
                tabContainer.html(content instanceof Backbone.View ? content.$el : content);

                this.$contentContainer.append(tabContainer);
            }, this));

            var classes = this.tabContainerClass.split(" ");
            this.$tabs = this.$('.' + classes[0]);
        },

        /**
         * @description Initialize tabs.
         * @memberof! tabbedPanelTrait
         * @fires tabbedPanelTrait#tab:ID
         */
        afterInitialize: function(options){
            this.tabHeaderContainer = new OptionContainer({
                options: options.tabs,
                el: this.$tabHeaderContainer,
                contextName: "tab",
                toggleClass: options.toggleClass || "active",
                toggle: true,
                optionTemplate: options.optionTemplate || panelButtonTemplate
            });

            this._contentRendered = {};

            this.listenTo(
                this.tabHeaderContainer,
                _.map(options.tabs, function(tab) { return "tab:" + tab.action; }).join(" "),
                _.bind(onTabSelect, this));

            for (var i = 0; i < options.tabs.length; i++) {
                if (options.tabs[i].primary) {
                    this.openTab(options.tabs[i].action);
                    break;
                }
            }
        },

        /**
         * @description Destroy tabs.
         * @memberof! tabbedPanelTrait
         */
        onRemove: function(){
            _.each(this.tabs, function(content) {
                content.remove && content.remove();
            });

            this.tabHeaderContainer.remove();
        },

        /**
         * @description Additional methods to expose through Panel's API.
         * @memberof! tabbedPanelTrait
         */
        extension: {
            /**
             * @description Open tab by it's ID.
             * @memberof! tabbedPanelTrait
             * @param {string} tabId ID of tab to open
             */
            openTab: function(tabId) {
                var optionView = this.tabHeaderContainer.getOptionView(tabId);

                optionView && optionView.select();
            },

            showTab: function(tabId) {
                var optionView = this.tabHeaderContainer.getOptionView(tabId);

                optionView && optionView.show();
            },

            hideTab: function(tabId) {
                var optionView = this.tabHeaderContainer.getOptionView(tabId);

                optionView && optionView.hide();
            }
        }
    });
});
