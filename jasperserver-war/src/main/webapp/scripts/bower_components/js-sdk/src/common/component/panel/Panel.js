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

define(function(require) {
    "use strict";

    require("css!panel");

    var _ = require('underscore'),
        Backbone = require('backbone'),
        panelTemplate = require('text!./template/panelTemplate.htm');

    return Backbone.View.extend(
        /** @lends Panel.prototype */
        {

            defaultTemplate: panelTemplate,

            el: function() {
                var
                    templateArguments = this.getTemplateArguments();

                return this.template(templateArguments);
            },

            getTemplateArguments: function () {
                return {
                    title: this.title,
                    additionalCssClasses: this.additionalCssClasses
                };
            },

            /**
             * @constructor Panel
             * @extends Backbone.View
             * @classdesc Panel component.
             * @param {object} [options]
             * @param {string} [options.template] Template for panel. If is not provided, default template is used.
             * @param {string} [options.title=""] Panel title.
             * @param {string} [options.additionalCssClasses=""] Additional CSS classes to be appended to default template. Split several classes with space.
             * @param {string|Backbone.View|HTMLElement} [options.content=""] Panel content. Can be plain string, HTMLElement, jQuery object or a Backbone.View.
             * @param {boolean} [options.collapsed=true] Collapsed state.
             * @param {string} [options.openClass="open"] CSS class that is added to Panel's DOM element when it is not collapsed.
             * @param {string} [options.closedClass="closed"] CSS class that is added to Panel's DOM element when it is collapsed.
             * @param {string} [options.loadingClass="loading"] CSS class that is added to Panel's DOM element when it is loading.
             * @param {boolean} [options.fixedHeight=false] If Panel's can be changed or it has fixed height.
             * @param {string} [options.contentContainer=".subcontainer"] CSS selector to put content into Panel's DOM.
             * @param {string} [options.overflow="auto"] CSS overflow property.
             * @param {array} [options.traits=[]] Additional traits for Panel.
             */
            constructor: function(options) {
                options || (options = {});

                this.template = _.template(options.template || this.defaultTemplate);
                this.title = options.title || "";
                this.additionalCssClasses = options.additionalCssClasses || "";
                this.content = options.content || "";
                this.collapsed = !options.collapsed;
                this.openClass = options.openClass || "open";
                this.closedClass = options.closedClass || "closed";
                this.loadingClass = options.loadingClass || "loading";
                this.fixedHeight = options.fixedHeight || false;
                this.contentContainer = options.contentContainer || ".subcontainer";
                this.overflow = options.overflow || "auto";

                this.traits = options.traits || [];

                // extend panel with additional stuff from traits
                _.each(this.traits, _.bind(function(trait) {
                    trait.extension && _.extend(this, trait.extension);
                }, this));

                this.invokeTraits("onConstructor", options);

                Backbone.View.apply(this, arguments);
            },

            invokeTraits: function(method) {
                var args = Array.prototype.slice.call(arguments, 1),
                    self = this;

                _.each(this.traits, function(trait) {
                    trait[method] && trait[method].apply(self, args);
                });
            },

            initialize: function(options) {
                this.invokeTraits("beforeInitialize", options);

                if (this.content) {
                    this.setContent(this.content);
                }

                this.toggleCollapsedState();

                this.invokeTraits("afterInitialize", options);
            },

            setElement: function(el) {
                this.invokeTraits("beforeSetElement", el);

                var res = Backbone.View.prototype.setElement.apply(this, arguments);
                this.$contentContainer = this.$(this.contentContainer);

                //update set DOM element content according to required state
                this.$contentContainer[this.collapsed ? "hide" : "show"]();

                this.invokeTraits("afterSetElement", el);

                return res;
            },

            /**
             * @desciption Open panel.
             * @param {object} [options] Additional options may be required for traits.
             * @param {boolean} [options.renderContent=false] If content should be rendered before opening the panel.
             * @param {boolean} [options.silent=false] Suppress firing 'open' event if called with true.
             * @fires Panel#open
             * @returns {Panel}
             */
            open: function(options) {
                this.invokeTraits("beforeOpen", options);

                this.$contentContainer.show();
                this.$el.removeClass(this.closedClass).addClass(this.openClass);
                this.collapsed = false;
                options && options.renderContent && this.renderContent();

                this.invokeTraits("afterOpen", options);

                /**
                 * @event Panel#open
                 * @description This event fires when panel is opened.
                 */
                    (options && options.silent) || this.trigger('open', this);

                return this;
            },

            /**
             * @desciption Close panel.
             * @param {object} [options] Additional options may be required for traits.
             * @param {boolean} [options.silent=false] Suppress firing 'close' event if called with true.
             * @fires Panel#close
             * @returns {Panel}
             */
            close: function(options) {
                this.invokeTraits("beforeClose", options);

                this.$contentContainer.hide();
                this.$el.removeClass(this.openClass).addClass(this.closedClass);
                this.collapsed = true;

                this.invokeTraits("afterClose", options);

                /**
                 * @event Panel#close
                 * @description This event fires when panel is closed.
                 */
                    (options && options.silent) || this.trigger('close', this);

                return this;
            },

            /**
             * @description Set panel content.
             * @param {string|Backbone.View|HTMLElement} content Panel content. Can be plain string, HTMLElement, jQuery object or a Backbone.View.
             * @param {boolean} [append] If content should be appended to container, not replace it's content.
             */
            setContent: function(content, append) {
                this.content = content;

                var rendered = this.renderContent();

                this.$contentContainer[append === true ? "append" : "html"](rendered);
            },

            /**
             * @description Render panel content.
             * @private
             */
            renderContent: function() {
                return this.content instanceof Backbone.View
                    ? this.content.render().$el
                    : this.content;
            },

            /**
             * @description Set panel height.
             * @param {number} height Panel height
             */
            setHeight: function(height) {
                var headerHeight = this.$("> .header").outerHeight();

                this.$contentContainer.css({
                    "height": _.isNumber(height) ? ((height - headerHeight) + "px") : "auto",
                    "overflow": this.overflow
                });
            },

            /**
             * @description Toggle panel collapsed state.
             */
            toggleCollapsedState: function() {
                this.collapsed ? this.open() : this.close();

                return this;
            },

            /**
             * @description Remove component from DOM.
             */
            remove: function() {
                this.invokeTraits("onRemove");

                this.content && this.content.remove && this.content.remove();

                Backbone.View.prototype.remove.call(this);
            }
        }
    );
});
