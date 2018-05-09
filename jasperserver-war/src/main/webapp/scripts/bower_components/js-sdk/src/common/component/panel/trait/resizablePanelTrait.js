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
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function(require){
    "use strict";

    var _ = require("underscore"),
        abstractPanelTrait = require("./abstractPanelTrait");

    require("css!jquery-ui/jquery-ui");
    require("jquery-ui/jquery.ui.resizable");

    /**
     * @event resizablePanelTrait#resize
     * @description This event is constantly fired during resizing.
     */
    function onResize(e, ui) {
        this.trigger("resize", e, ui);
    }

    /**
     * @event resizablePanelTrait#resizeStart
     * @description This event is fired when resizing started.
     */
    function onStart(e, ui){
        this.trigger("resizeStart", e, ui);
    }

    /**
     * @event resizablePanelTrait#resizeStop
     * @description This event is fired when resizing stopped.
     */
    function onStop(e, ui){
        this.trigger("resizeStop", e, ui);
    }

    /**
     * @mixin resizablePanelTrait
     * @description Extend panel with resizable behavior.
     * @extends abstractPanelTrait
     */
    return _.extend({}, abstractPanelTrait, {
        /**
         * @description Initialize additional Panel options.
         * @memberof! resizablePanelTrait
         * @param {object} [options]
         * @param {string} [options.handles="e, s, se"] jQuery UI resizable handles.
         * @param {number} [options.minWidth=10] jQuery UI resizable minWidth option.
         * @param {number} [options.minHeight=10] jQuery UI resizable minHeight option.
         * @param {number} [options.maxWidth=null] jQuery UI resizable maxWidth option.
         * @param {number} [options.maxHeight=null] jQuery UI resizable maxHeight option.
         * @param {string|jQuery|HTMLElement} [options.alsoResize] jQuery UI resizable alsoResize option.
         * @param {string} [options.resizableEl] Selector of resizable element.
         */
        onConstructor: function(options) {
            options || (options = {});

            this.handles = options.handles || "e, s, se";
            this.minWidth = options.minWidth || 10;
            this.minHeight = options.minHeight || 10;
            this.maxWidth = options.maxWidth || null;
            this.maxHeight = options.maxHeight || null;
            this.alsoResize = options.alsoResize || false;
            this.resizableEl = options.resizableEl;
        },

        /**
         * @description Initialize jQuery UI Resizable for element.
         * @memberof! resizablePanelTrait
         * @fires resizablePanelTrait#resizeStart
         * @fires resizablePanelTrait#resize
         * @fires resizablePanelTrait#resizeStop
         */
        afterSetElement: function(){
            this.$resizableEl = this.$el.find(this.resizableEl).length ? this.$el.find(this.resizableEl) : this.$el;

            this.$resizableEl.resizable({
                handles: this.handles,
                minHeight: this.minHeight,
                minWidth: this.minWidth,
                maxWidth: this.maxWidth,
                maxHeight: this.maxHeight,
                alsoResize: this.alsoResize,
                resize: _.bind(onResize, this),
                start: _.bind(onStart, this),
                stop: _.bind(onStop, this)
            });
        },

        /**
         * @description Destroy jQuery UI Resizable on Panel remove.
         * @memberof! resizablePanelTrait
         */
        onRemove: function() {
            try {
                this.$el.resizable("destroy");
            } catch (e) {
                // destroyed already, skip
            }
        }
    });
});
