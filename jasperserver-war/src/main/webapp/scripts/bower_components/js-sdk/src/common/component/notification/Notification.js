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

define(function(require) {
    "use strict";

    var _ = require('underscore'),
        $ = require('jquery'),
        Backbone = require('backbone'),
        i18n = require("bundle!js-sdk/CommonBundle"),
        notificationTemplate = require('text!./template/notificationTemplate.htm'),
        instance, Notification;

    require("css!notifications");

    var NOTIFICATION_TYPES = {
            SUCCESS: "success",
            WARNING: "warning"
        },
        NOTIFICATION_DEFAULT_DELAY = 2000,
        notificationTypeToCssClassMap = {};

    notificationTypeToCssClassMap[NOTIFICATION_TYPES.WARNING] = NOTIFICATION_TYPES.WARNING;

    Notification = Backbone.View.extend(/** @lends Notification.prototype */{
        template: _.template(notificationTemplate),

        events: {
            "click .close a": "hide"
        },

        el: function() {
            return this.template({ message: this.message, i18n: i18n });
        },

        /**
         * @constructor Notification
         * @classdesc Notification component.
         * @extends Backbone.View
         */
        initialize: function() {
            this.render();
        },

        /**
         * @description Render notification.
         * @returns {Notification}
         * @private
         */
        render: function() {
            $("body").append(this.$el);
            this.$el.hide();
            this.$messageContainer = this.$(".notificationMessage > span:first-child");

            return this;
        },

        /**
         * @description Show notification.
         * @param {object} options
         * @param {string} [options.type="warning"] Type of notification (success, warning).
         * @param {number} [options.delay=2000] Delay before notification is hidden.
         * @param {string} options.message Notification text.
         * @returns {Notification}
         */
        show: function(options) {
            options = _.extend({
                type: NOTIFICATION_TYPES.WARNING,
                delay: NOTIFICATION_DEFAULT_DELAY
            }, options);

            this.$messageContainer.text(options.message);

            this.$messageContainer.removeClass().attr({"class": notificationTypeToCssClassMap[options.type]});

            this.$el.slideDown();

            options.delay && _.delay(_.bind(this.hide, this), options.delay);

            return this;
        },

        /**
         * @description Hide notification.
         * @returns {Notification}
         */
        hide: function() {
            arguments.length && arguments[0].preventDefault && arguments[0].preventDefault();

            this.$el.slideUp();

            return this;
        },

        /**
         * @description Remove component from DOM.
         */
        remove: function() {
            Backbone.View.prototype.remove.apply(this, arguments);
        }
    }, {
        show: function(){
            instance || (instance = new Notification());
            return instance.show.apply(instance, arguments);
        },

        hide: function(){
            instance || (instance = new Notification());
            return instance.hide.apply(instance, arguments);
        }
    });

    return Notification;
});