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
 * Simple accordion component. Works with Array of Panel objects.
 *
 * @author Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        Backbone = require("backbone"),
        $ = require("jquery");

    function doPanelAction(action, panel) {
        var self = this;

        if (_.indexOf(this.panels, panel) < 0) {
            return;
        }

        if (action === "toggle" || action === "open") {
            if (!self.allowMultiplePanelsOpen) {
                _.each(this.panels, function (panelView) {
                    panelView !== panel && panelView.close();
                });
            }
        }

        if (action === "toggle") {
            panel.collapsed ? panel.open() : panel.close();
        } else {
            panel[action]();
        }

        this.fit();
    }

    /**
     * @constructor Accordion
     * @class Accordion
     * @mixes Backbone.Events
     * @classdesc Simple accordion component. Works with Array of Panel objects.
     * @param {object} options
     * @param {jQuery|HTMLElement|string} options.container - element to which accordion will be attached.
     * @param {Panel[]} [options.panels=[]] - array of Panel objects.
     * @param {boolean} [options.allowMultiplePanelsOpen=false] - if multiple panels in accordion can be opened at the same time.
     * @throws {Error} Accordion should have specified container
     */
    function Accordion(options) {
        options || (options = {});

        if (!options.container || !$(options.container).length) {
            throw new Error("Accordion should have specified container");
        }

        this.container = options.container;
        this.panels = options.panels || [];
        this.allowMultiplePanelsOpen = options.allowMultiplePanelsOpen || false;

        $(this.container).css("overflow", "hidden");

        _.each(this.panels, function(panel){
            panel.$resizableEl && this.listenTo(panel, "resizeStart", _.bind(this.calcMaxHeight, this, panel));
        }, this);
    }

    /**
     * @description Toggle collapsed/expanded state of a panel in accordion
     * @memberof! Accordion
     * @param {Panel} panel - instance of Panel class
     */
    Accordion.prototype.toggle = function(panel) {
        doPanelAction.call(this, "toggle", panel);
    };

    /**
     * @description Expand a panel in accordion
     * @memberof! Accordion
     * @param {Panel} panel - instance of Panel class
     */
    Accordion.prototype.expand = function(panel) {
        doPanelAction.call(this, "open", panel);
    };

    /**
     * @description Collapse a panel in accordion
     * @memberof! Accordion
     * @param {Panel} panel - instance of Panel class
     */
    Accordion.prototype.collapse = function(panel) {
        doPanelAction.call(this, "close", panel);
    };

    /**
     * @description Fit all panels inside accordion
     * @memberof! Accordion
     * @fires Accordion#fit
     */
    Accordion.prototype.fit = function() {
        var collapsedPanels = [],
            nonCollapsedPanels = [];

        _.each(this.panels, function(panel) {
            panel.collapsed ? collapsedPanels.push(panel) : nonCollapsedPanels.push(panel);
        });

        var collapsedPanelsTotalHeight = 0,
            containerHeight = $(this.container).height(),
            freeHeightToShare = 0,
            fixedHeightPanelsNumber = 0;

        _.each(collapsedPanels, function (panel) {
            collapsedPanelsTotalHeight += panel.$el.outerHeight(true);
        });

        freeHeightToShare = containerHeight - collapsedPanelsTotalHeight;

        _.each(nonCollapsedPanels, function(panel) {
            if (panel.fixedHeight) {
                freeHeightToShare -= panel.$el.outerHeight(true);
                fixedHeightPanelsNumber++;
            }
        });

        _.each(nonCollapsedPanels, function(panel) {
            if (panel.fixedHeight) {
                panel.setHeight(panel.$el.outerHeight(true));
            } else {
                panel.setHeight(Math.floor(freeHeightToShare / (nonCollapsedPanels.length - fixedHeightPanelsNumber)));
            }
        });

        /**
         * @event Accordion#fit
         */
        this.trigger("fit", this);
    };

    Accordion.prototype.calcMaxHeight = function(resizablePanel) {
        var containerHeight = $(this.container).height(),
            activePanel = this.panels[this.panels.indexOf(resizablePanel) + 1];

        _.each(this.panels, function(panel) {
            if (!_.isEqual(resizablePanel, panel)) {
                containerHeight -= panel.$el.find(">.header").outerHeight(true);

                if (_.isEqual(activePanel, panel)) {
                    containerHeight -= panel.collapsed ? 0
                        : parseInt(panel.$el.find(">.subcontainer").css("minHeight"));
                } else {
                    containerHeight -= panel.$el.outerHeight(true);
                }
            }
        }, this);

        resizablePanel.$resizableEl.resizable("option", "maxHeight", containerHeight);
    };

    _.extend(Accordion.prototype, Backbone.Events);

    return Accordion;
});