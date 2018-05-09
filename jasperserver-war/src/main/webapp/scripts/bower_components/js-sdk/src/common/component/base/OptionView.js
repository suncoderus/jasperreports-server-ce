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
 * Basic option view for buttons, menu options etc.
 *
 * @author: Kostiantyn Tsaregradskyi, Zakhar Tomchenko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore");

    return Backbone.View.extend({
            events: {
                "click": "select",
                "mouseover": "mouseover",
                "mouseout": "mouseout"
            },

            /**
             * @constructor OptionView
             * @classdesc Basic option view for buttons, menu options etc.
             * @param {object} options
             * @param {string} options.template - HTML template for option.
             * @param {Backbone.Model} options.model - model representing option.
         * @param {string} [options.disabledClass=[]] - CSS class to add to disabled option.
         * @param {string} [options.hiddenClass=[]] - CSS class to add to hidden option.
             * @throws "Option should have defined template" exception if options.template is not defined.
             * @throws "Option should have associated Backbone.Model" exception if options.model is not defined or is not a Backbone.Model instance.
         * @fires OptionView#mouseover
         * @fires OptionView#mouseout
             */
            constructor: function(options) {
                if (!options || !options.template) {
                    throw new Error ("Option should have defined template");
                }

                if (!options.model || !(options.model instanceof Backbone.Model)) {
                    throw new Error ("Option should have associated Backbone.Model");
                }

                this.template = _.template(options.template);

                this.disabledClass = options.disabledClass;
                this.hiddenClass = options.hiddenClass;
                this.toggleClass = options.toggleClass;
                this.overClass = options.overClass || "over";

                Backbone.View.apply(this, arguments);
            },

            initialize: function() {
                Backbone.View.prototype.initialize.apply(this, arguments);

                var self = this;

                if (this.model.get("disabled") === true) {
                    this.disable();
                }

                if (this.model.get("hidden") === true) {
                    this.hide();
                }

                if (this.model.get("selected") === true) {
                    this.addSelection();
                }

                this.listenTo(this.model, "change:over", function(model, value){
                    if (value){
                        self.$("> p").addClass(self.overClass);
                    } else {
                        self.$("> p").removeClass(self.overClass);
                    }
                });
            },

            el: function() {
                return this.template(this.model.toJSON());
            },

            /**
             * @description Enables option.
             */
            enable: function() {
                this.$el.removeAttr("disabled").removeClass(this.disabledClass);
            },

            /**
             * @description Disables option.
             */
            disable: function() {
                this.$el.attr("disabled", "disabled").addClass(this.disabledClass);
            },

        /*
         * @method over
         * @description adds over class option.
         */
        over: function() {
            this.model.set({over: true});
        },

        /*
         * @method leave
         * @description removes over class
         */
        leave: function() {
            this.model.set({over: false});
        },

        /*
         * @method show
             * @description Shows option.
             */
            show: function() {
                this.$el.show().removeClass(this.hiddenClass);
            },

            /**
             * @description Hides option.
             */
            hide: function() {
                this.$el.hide().addClass(this.hiddenClass);
            },

            /**
             * @description Check if option is visible.
             * @returns {boolean}
             */
            isVisible: function() {
                return this.$el.is(":visible");
            },

            /**
             * @description Check if option is disabled.
             * @returns {boolean}
             */
            isDisabled: function() {
                return this.$el.is(":disabled");
            },

            /**
             * @param {jQuery.Event}
             * @description Click on option event handler.
             * @fires OptionView#click
             */
            select: function(ev) {
                this.model.trigger("select", this, this.model, ev);
                /**
                 * @event optionView#click
                 * @description This event is fired when option is click.
                 */
                this.trigger("click", this, this.model, ev);
            },

            /**
             * @description Adds class to option element
             */
            addSelection: function() {
                this.$el.addClass(this.toggleClass);
                this.model.set("selected", true);
            },

            /**
             * @description Removes class from option element
             */
            removeSelection: function() {
                this.$el.removeClass(this.toggleClass);
                this.model.unset("selected");
            },

            /**
             * @param {jQuery.Event}
             * @description Mouseover on option event handler.
             * @fires OptionView#mouseover
             */
            mouseover: function(ev) {
                /**
                 * @event optionView#mouseover
                 * @description This event is fired when option is mouseovered.
                 */
                this.trigger("mouseover", this, this.model, ev);
            },

            /**
             * @param {jQuery.Event}
             * @description Mouseout on option event handler.
             * @fires OptionView#mouseout
             */
            mouseout: function(ev) {
                /**
                 * @event optionView#mouseout
                 * @description This event is fired when mouse is out of option.
                 */
                this.trigger("mouseout", this, this.model, ev);
            }
        });
});