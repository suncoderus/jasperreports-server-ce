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
        abstractPanelTrait = require("./abstractPanelTrait");

    function collapseControlPressed(evt, allowPropagation) {
        !allowPropagation && evt.stopPropagation();
        this.onCollapseControlPressed ? this.onCollapseControlPressed(evt) : this.toggleCollapsedState();
    }

    function stopListeningToCollapser() {
        this.$collapser && this.$collapser.off("mousedown");

        this.$el && this.$el.find(this.expandOnDblClickSelector).off("dblclick");
    }

    /**
     * @mixin collapsiblePanelTrait
     * @extends abstractPanelTrait
     * @description Extend panel with additional collapse/expand button.
     */
    return _.extend({}, abstractPanelTrait, {
        /**
         * @description Initialize additional Panel options.
         * @memberof! collapsiblePanelTrait
         * @param {object} [options]
         * @param {string} [options.collapserClass="buttonIconToggle"] CSS class for expand/collapse button.
         * @param {string} [options.collapserSelector=".buttonIconToggle"] CSS selector for expand/collapse button.
         * @param {string} [options.collapsiblePanelClass="collapsiblePanel"] Additional CSS class for Panel.
         * @param {string} [options.expandOnDblClickSelector="> p:first"] CSS selector for double-click event to expand panel.
         * @param {function} [options.onCollapseControlPressed] Callback to call when collapse/expand button is pressed. Replaces default collapse/expand functionality.
         */
        onConstructor: function(options) {
            this.collapserClass = options.collapserClass || "buttonIconToggle";
            this.collapserSelector = options.collapserSelector || ".buttonIconToggle";
            this.collapsiblePanelClass = options.collapsiblePanelClass || "collapsiblePanel";
            this.expandOnDblClickSelector = options.expandOnDblClickSelector || "> p:first";
            this.allowEventPropagation = !!options.allowMouseDownEventPropagation;

            this.onCollapseControlPressed = options.onCollapseControlPressed;
        },

        /**
         * @description Unbind event listener before Panel element is set.
         * @memberof! collapsiblePanelTrait
         */
        beforeSetElement: function(){
            stopListeningToCollapser.call(this);
        },

        /**
         * @description Insert expand/collapse button and attach events to it.
         * @memberof! collapsiblePanelTrait
         */
        afterSetElement: function(){
            this.$el.addClass(this.collapsiblePanelClass);

            this.$collapser = this.$(this.collapserSelector);

            if (!this.$collapser.length) {
                this.$collapser = $("<button></button>").addClass(this.collapserClass);
                this.$("> .header").prepend(this.$collapser);
            }

            this.$collapser.on("mousedown", _.bind(collapseControlPressed, this, this.allowEventPropagation));

            this.$el.find(this.expandOnDblClickSelector).on("dblclick", _.bind(collapseControlPressed, this));
        },

        /**
         * @description Unbind event listener when Panel is removed.
         * @memberof! collapsiblePanelTrait
         */
        onRemove: function() {
            stopListeningToCollapser.call(this);
        }
    });
});
