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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        $ = require("jquery"),
        browserDetection = require("common/util/browserDetection");

    require("css!webPageView");

    /**
     * @description Set "src" attribute for iframe and start timeout to show error message.
     * @private
     * @memberof! WebPageView
     */
    function setIframeSrc() {
        /*jshint -W107 */
        if ((this.url || "").toLowerCase().indexOf("javascript:") === -1){
            /*jshint +W107 */
            this.$iframe.attr("src", this.url);

            this._iframeSrcSet = true;

            this.$el.addClass("loading");

            this._loadingTimeoutId && clearTimeout(this._loadingTimeoutId);

            this._loadingTimeoutId = setTimeout(_.bind(this.$el.removeClass, this.$el, "loading"), this.timeout);
        }
    }

    var WebPageView = Backbone.View.extend(/** @lends WebPageView.prototype */{
        tagName: "div",
        className: "webPageView",

        /**
         * @constructor WebPageView
         * @class WebPageView
         * @classdesc Embeddable WebPageView component
         * @param {object} options Object that holds various options for component.
         * @param {string|HTMLElement|jQuery} [options.renderTo] If specified, component will be rendered to this container automatically
         * @param {string} [options.url] Url to open
         * @param {boolean} [options.scrolling=true] Should iframe element have scroll-bars
         * @param {number} [options.timeout=20000] Timeout in milliseconds after which error message will be shown
         * @throws {Error} WebPageView cannot be rendered to specified container
         */
        constructor: function(options) {
            options || (options = {});

            if (options.renderTo && (!$(options.renderTo)[0] || !$(options.renderTo)[0].tagName)) {
                throw new Error("WebPageView cannot be rendered to specified container");
            }

            this.renderTo = options.renderTo;
            this.url = options.url;
            this.scrolling = _.isUndefined(options.scrolling) ? WebPageView.SCROLLING : options.scrolling;
            this.timeout = _.isUndefined(options.timeout) ? WebPageView.TIMEOUT : options.timeout;

            Backbone.View.prototype.constructor.apply(this, arguments);
        },

        initialize: function() {
            this.$iframe = $("<iframe></iframe>")
                .addClass("externalUrlIframe");

            this.$el.html(this.$iframe);

            this.$el.addClass("invisible");

            $("body").append(this.$el);

            this.listenToReadyStateCompete();

            this.setScrolling(this.scrolling, {silent: true});

            if (this.url && _.isString(this.url)) {
                setIframeSrc.call(this);

                if (this.renderTo) {
                    this.render(this.renderTo);
                }
            }
        },

        listenToReadyStateCompete: function () {
            var doc = this.$iframe[0].contentDocument || this.$iframe[0].contentWindow,
                self = this;

            this.$iframe.on("load", function(){
                clearInterval(self._loadingTimerId);

                $(this).blur();
                $(this).parent().focus();
                self.trigger("load");
            });

            if (doc.document) {
                doc = doc.document;
            }

            // Some browsers like Safari does not emit "load" event, se we need this as fallback.
            this._loadingTimerId = setInterval(function() {
                if (doc.readyState == 'complete') {
                    clearInterval(self._loadingTimerId);

                    self.$iframe.blur();
                    self.$iframe.parent().focus();
                    self.$el.removeClass("loading");
                    self.trigger("load");
                }
            }, 300);
        },

        /**
         * @description Render component to specified container
         * @param {string|HTMLElement|jQuery} container Where to render
         * @throws {Error} WebPageView URL is not specified
         * @throws {Error} WebPageView cannot be rendered to specified container
         * @fires WebPageView#render
         * @return {WebPageView}
         */
        render: function(container) {
            if (!this.url || !_.isString(this.url)) {
                throw new Error("WebPageView URL is not specified");
            }

            if (!container || !$(container)[0] || !$(container)[0].tagName) {
                throw new Error("WebPageView cannot be rendered to specified container");
            }

            if (!this._iframeSrcSet) {
                setIframeSrc.call(this);
            }

            var $el = this.$el.detach();

            $el.removeClass("invisible");

            $(container).html($el);

            this._rendered = true;

            /**
             * @event WebPageView#render
             */
            this.trigger("render", this, $(container));

            return this;
        },

        /**
         * @description Set URL property
         * @param {string} url String representing URL of web page.
         * @param {boolean} [noRefresh] If equals to true, component will not be automatically refreshed.
         * @fires WebPageView#change:url
         */
        setUrl: function(url, noRefresh) {
            this.url = url;

            /**
             * @event WebPageView#change:url
             */
            this.trigger("change:url", this, this.url);

            if (!noRefresh) {
                this.refresh();
            }
        },

        /**
         * @description Set timeout property. Triggers "change:timeout" event.
         * @param {number} timeout Number in milliseconds for load timeout.
         * @fires WebPageView#change:timeout
         */
        setTimeout: function(timeout) {
            this.timeout = timeout;

            /**
             * @event WebPageView#change:timeout
             */
            this.trigger("change:timeout", this, this.timeout);
        },

        /**
         * @description Set scrolling property. Will automatically update scroll-bars of the iframe element.
         * @param {boolean} scrolling If scroll-bars should be shown or not.
         * @param {object} [options]
         * @param {boolean} [options.silent] if set to 'true' then no event will be triggered
         * @fires WebPageView#change:scrolling
         */
        setScrolling: function(scrolling, options) {
            this.scrolling = scrolling;

            this.$iframe
                .attr("scrolling", this.scrolling ? "yes" : "no");

            //if this code will be executed on desktop browser
            //additional unnecessary scroll will be shown.
            //TODO: Need to think how to replace with feature detection.
            if (browserDetection.isIPad()) {
                this.scrolling
                    ? this.$el.addClass("touchScroll")
                    : this.$el.removeClass("touchScroll");
            }

            if (!options || !options.silent) {
                /**
                 * @event WebPageView#change:scrolling
                 */
                this.trigger("change:scrolling", this, this.scrolling);
            }
        },

        /**
         * @description Refresh iframe content.
         * @throws Error If component was not yet rendered to container.
         * @throws Error If URL is not specified.
         * @fires WebPageView#change:refresh
         */
        refresh: function() {
            if (!this._rendered) {
                throw new Error("WebPageView must be rendered to a specific container first");
            }

            if (!this.url || !_.isString(this.url)) {
                throw new Error("WebPageView URL is not specified");
            }

            setIframeSrc.call(this);

            /**
             * @event WebPageView#refresh
             */
            this.trigger("refresh", this, this.url);
        },

        /**
         * @description Remove component from DOM.
         * @fires WebPageView#remove
         */
        remove: function() {
            this._loadingTimeoutId && clearTimeout(this._loadingTimeoutId);
            this._loadingTimerId && clearTimeout(this._loadingTimerId);
            this.$iframe.off("load");

            /**
             * @event WebPageView#remove
             */
            this.trigger("remove", this);

            Backbone.View.prototype.remove.apply(this, arguments);
        }
    }, {
        /**
         * @static
         * @memberof! WebPageView
         * @type {number}
         * @description Number property representing default timeout in milliseconds after which error message will be shown while loading web page in iframe.
         */
        TIMEOUT: 20000,

        /**
         * @static
         * @memberof! WebPageView
         * @type {boolean}
         * @description Default state of scroll-bars of iframe element.
         */
        SCROLLING: true,

        /**
         * @static
         * @memberof! WebPageView
         * @description Static method to create new WebPageView component instance.
         * @param {(string|object)} settings If String, then it's treated as url parameter. For object case see {@link WebPageView#constructor}.
         * @param {function} [callback] Optional function to call after component initialization. If error occurred, callback
         *      will be called with Error as first argument. If component instance was successfully created, then
         *      callback is called with undefined as first argument and component instance as second.
         * @returns {WebPageView} New component instance.
         * @throws {Error} If callback was not specified and error occurred while initializing new instance of component.
         */
        open: function(settings, callback) {
            var view, err;

            try {
                view = new WebPageView(_.isObject(settings) ? settings : { url: settings });
            } catch(ex) {
                err = ex;

                if (!callback || !_.isFunction(callback)) {
                    throw ex;
                }
            }

            callback && _.isFunction(callback) && callback(err, view);

            return view;
        }
    });

    return WebPageView;
});
