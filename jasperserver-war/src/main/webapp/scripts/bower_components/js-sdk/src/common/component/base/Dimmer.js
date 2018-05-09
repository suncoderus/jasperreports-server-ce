/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * Licensed under commercial Jaspersoft Subscription License Agreement
 */

/**
 * @author Andriy Godovanets
 */

define(function(require) {
    var $ = require("jquery"),
        classUtil = require("common/util/classUtil");

    var $dimmer, counter;

    return classUtil.extend(/** @lends Dimmer.prototype */{
        /**
         * @constructor Dimmer
         * @class Dimmer
         * @classdesc Overlay component
         * @param {object} [options] - CSS options for dimmer
         */
        constructor: function(options) {
            if (!$dimmer) {
                counter = 0;
                $dimmer = $("<div id='dialogDimmer' class='dimmer'></div>").css(options);
                $(document.body).append($dimmer);

                $dimmer.hide();
            }
            counter++;
        },

        /**
         * @description Set CSS for dimmer
         * @param {object} [options] - CSS options for dimmer
         * @returns {Dimmer}
         */
        css: function(options) {
            $dimmer.css(options);
            return this;
        },

        /**
         * @description Show dimmer
         * @returns {Dimmer}
         */
        show: function() {
            var dimmerCount = this.getCount() || 0;
            this.setCount(++dimmerCount);
            $dimmer.show();
            return this;
        },

        /**
         * @description Hide dimmer
         * @returns {Dimmer}
         */
        hide: function() {
            if (this.isVisible()) {
                var dimmerCount = this.getCount();
                this.setCount(--dimmerCount);
                !dimmerCount && $dimmer.hide();
                return this;
            }
        },

        setCount: function(value) {
            $dimmer.data({"count": value});
        },

        getCount: function() {
            return parseInt($dimmer.data("count"), 10);
        },

        /**
         * @description Check if dimmer is visible
         * @returns {boolean}
         */
        isVisible: function() {
            return $dimmer.is(':visible');
        },

        /**
         * @description Remove dimmer from DOM
         */
        remove: function() {
            if (this._removed) {
                return;
            }

            this._removed = true;
            if (!$dimmer) {
                return;
            }
            counter--;
            if (!counter) {
                $dimmer.remove();
                $dimmer = null;
            }
        }
});

});