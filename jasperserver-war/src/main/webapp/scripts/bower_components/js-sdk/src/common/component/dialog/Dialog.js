/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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
        Dimmer = require("common/component/base/Dimmer"),
        Panel = require("common/component/panel/Panel"),
        OptionContainer = require("common/component/base/OptionContainer"),

        resizablePanelTrait = require('common/component/panel/trait/resizablePanelTrait'),

        dialogTemplate = require('text!./template/dialogTemplate.htm'),
        dialogButtonTemplate = require("text!./template/dialogButtonTemplate.htm");

    require("jquery-ui/jquery.ui.draggable");

	var _savedOptions = {}; // object used to save options which came to us

    var Dialog = Panel.extend(/** @lends Dialog.prototype */{
        defaultTemplate: dialogTemplate,

        events: {
          "mousedown": "_focus",
          "touchstart": "_focus",
          "keydown": "_onKeyDown"
        },

        /**
         * @constructor Dialog
         * @classdesc Base Dialog component
         * @extends Panel
         * @param {object} [options] Options for Dialog. For panel-specfic options see {@link Panel#constructor}
         * @param {boolean} [options.resizable=false] If dialog is resizable
         * @param {boolean} [options.modal=false] If dialog is modal
         * @param {object[]} [options.buttons] Buttons for dialog
         * @param {string} options.buttons[].action Button action
         * @param {string} options.buttons[].label Button label
         * @param {boolean} options.buttons[].primary If button is primary
         * @fires Dialog#button:ACTION
         */
        constructor: function(options) {
            options || (options = {});
            options.traits || (options.traits = []);

            this.resizable = options.resizable || false;
            this.modal = options.modal || false;
            this.additionalBodyCssClasses = options.additionalBodyCssClasses || "";

            if (this.resizable && _.indexOf(options.traits, resizablePanelTrait) === -1) {
                options.traits.push(resizablePanelTrait);
            }

            Panel.call(this, options);
        },

        initialize: function(options){
	        _savedOptions = _.extend({}, options);
            this.collapsed = !this.collapsed;

            if(!_.isEmpty(options.buttons)) {
                this.buttons = new OptionContainer({
                    options:options.buttons,
                    el: this.$(".jr-mDialog-footer")[0] || this.$(".footer")[0],
                    contextName:"button",
                    optionTemplate: dialogButtonTemplate
                });
            }

            Panel.prototype.initialize.apply(this, arguments);

            /**
             * @event Dialog#button:ACTION
             */
            // re-trigger button events on dialog itself
            this.buttons && this.listenTo(
                this.buttons,
                _.map(options.buttons, function(button) { return "button:" + button.action; }).join(" "),
                _.bind(function(buttonView, buttonModel) { this.trigger("button:" + buttonModel.get("action"), buttonView, buttonModel); }, this));

            this.render();
        },

        getTemplateArguments: function () {
            var
                templateArguments = Panel.prototype.getTemplateArguments.apply(this, arguments);

            return _.extend(templateArguments, {
                additionalBodyCssClasses: this.additionalBodyCssClasses
            });
        },

        setElement: function(el){
            var res = Panel.prototype.setElement.apply(this, arguments);

            this.buttons && this.buttons.setElement(this.$(".jr-mDialog-footer")[0] || this.$(".footer")[0]);

            return res;
        },

        /**
         * @description Set panel title.
         * @param {string} title Panel title
         */
        setTitle: function(title) {
            this.$(".jr-mDialog-header > .jr-mDialog-header-title").text(title);
        },

        render: function(){
            this.$el.hide();

            if (this.modal) {
                this.dimmer = new Dimmer({zIndex: 900});
            }

            $("body").append(this.$el);

            this.$el.draggable({ handle: ".mover", addClasses: false, containment: "document"});

            return this;
        },

        /**
         * @description Open dialog.
         * @param {object} [coordinates] Position to open dialog.
         * @param {number} [coordinates.top] Top position to open dialog.
         * @param {number} [coordinates.left] Left position to open dialog.
         * @fires Dialog#dialog:visible
         * @returns {Dialog}
         */
        open: function(coordinates){
            if (!this.isVisible()) {
                Panel.prototype.open.apply(this, arguments);

                this.modal && this.dimmer.css({zIndex: ++Dialog.highestIndex}).show();

	            if (_savedOptions.additionalCssClasses && _savedOptions.additionalCssClasses.indexOf("newResizeableDialog") !== -1) {
		            this._setMinSize();
	            }

                this._position(coordinates)._increaseZIndex();

                // Class "over" is added to buttons on mouseover by some old prototype code.
                // Button may remain overed when dialog is closed and it will be displayed as overed when we will open
                // dialog again. That's why we need to remove class "over" when dialog is opened.
                this.buttons && this.buttons.$(".over").removeClass("over");

                this.$el.show();

            /**
             * @event Dialog#dialog:visible
             */
            this.trigger("dialog:visible");

                return this;
            }
        },

        /**
         * @description Close dialog.
         * @returns {Dialog|undefined}
         */
        close: function(){
            if (this.isVisible()) {
                this.$el.css({ zIndex: --Dialog.highestIndex });

                this.modal && this.dimmer.css({ zIndex: --Dialog.highestIndex }).hide();

                this.$el.hide();

                Panel.prototype.close.apply(this, arguments);

                return this;
            }
        },

        /**
         * @description Adds additional css classes to Panel
         * @param {string} classNames Panel css class names
         */
        addCssClasses: function(classNames) {
            this.$el.addClass(classNames);
        },

        /**
         * @description Overrides {@link Panel#toggleCollapsedState} to do nothing.
         * @returns {Dialog}
         */
        toggleCollapsedState: function() {
            return this;
        },

        /**
         * @description Enable specific dialog button.
         * @param {string} action Button action
         */
        enableButton: function(action){
            this.buttons.enable(action);
        },

        /**
         * @description Disable specific dialog button.
         * @param {string} action Button action
         */
        disableButton: function(action){
            this.buttons.disable(action);
        },

        /**
         * @description Check if dialog is visible.
         * @returns {boolean}
         */
        isVisible: function() {
            return this.$el.is(":visible");
        },

	    _setMinSize: function() {

		    if (_savedOptions.minWidth) {
			    this.$el.css({
				    width: _savedOptions.minWidth,
				    minWidth: _savedOptions.minWidth
			    });
		    }

		    if (_savedOptions.minHeight) {
			    this.$el.css({
				    height: _savedOptions.minHeight,
				    minHeight: _savedOptions.minHeight
			    });
		    }
	    },

        _position: function(coordinates){

            var top, left;
            var body = $("body"),
                elHeight = this.$el.height(),
                elWidth = this.$el.width();

            if(coordinates && typeof coordinates.top != "undefined" && typeof coordinates.left != "undefined"){
                top = coordinates.top;
                left = coordinates.left;

                var bodyHeight = body.height();
                var bodyWidth = body.width();

                var fitByHeight = bodyHeight - coordinates.top;
                var fitByWidth =  bodyWidth - coordinates.left;

                if(fitByHeight < elHeight){
                    top = coordinates.top - elHeight;
                    top = (top < 0) ? (bodyHeight/2 - elHeight/2) : top
                }
                if(fitByWidth < elWidth){
                    left = coordinates.left - elWidth;
                    left = (left < 0) ? (bodyWidth/2 - elWidth/2) : left
                }

            }else{
                top = $(window).height() / 2 - elHeight / 2;
                left = $(window).width() / 2 - elWidth / 2;
            }

            this.$el.css({
                top: top,
                left: left,
                position: "absolute"
            });

            return this;
        },

        _focus: function(){
           !this.modal && this._increaseZIndex();
        },

        _increaseZIndex: function() {
            this.$el.css({ zIndex : ++Dialog.highestIndex });
        },

        _onKeyDown: function(e){
            this.buttons._onKeyDown(e);
        },

        /**
         * @description Remove dialog from DOM
         */
        remove: function(){
            this.buttons && this.buttons.remove();

            this.dimmer && this.dimmer.remove();

            try {
                this.$el.draggable("destroy");
            } catch (e) {
                // destroyed already, skip
            }

            Panel.prototype.remove.call(this);
        }
    }, {
        highestIndex: 1000,
        resetHighestIndex: function(index){
            Dialog.highestIndex = index || 1000;
        }
    });

    return Dialog;

});
