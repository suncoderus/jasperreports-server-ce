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


define(function (require) {
    "use strict";

    var $ = require("jquery"),
        _ = require("underscore"),
        ClassUtil = require('common/util/classUtil');

    return ClassUtil.extend(
        /** @lends AttachableComponent.prototype */
        {

        /**
         * @constructor AttachableComponent
         * @classdesc Component that can be attached to any DOM elemenet
         * @param {(HTMLElement|jQuery)} attachTo - DOM element to attach to
         * @param {object} [padding={top: 5, left: 5}] Padding for component
         * @throw {Error} AttachableComponent should be attached to an element
         */
        constructor: function(attachTo, padding){

            this.padding = padding ? padding : {top: 5, left: 0};

            this.setAttachTo(attachTo);
        },

        /**
         * Replace element to which component will be attached
         * @param attachTo
         */
        setAttachTo: function(attachTo) {
            if (attachTo && $(attachTo).length > 0) {
                this.$attachTo = $(attachTo);
            } else {
                this.$attachTo = $("<div></div>");
            }
        },

        /**
         * @description Shows component near element.
         */
        show: function(){
            var attachOffset = this.$attachTo.offset(),
                attachHeight = this.$attachTo[0].tagName && this.$attachTo[0].tagName.toLowerCase() === "input" ? this.$attachTo.outerHeight() : this.$attachTo.height(),
                attachWidth  = this.$attachTo.width();

            var body = $("body"),
                bodyHeight = body.height(),
                bodyWidth = body.width(),
                elementWidth = this.$el.innerWidth(),
                elementHeight = this.$el.innerHeight(),
                fitByHeight = attachOffset.top + attachHeight + this.padding.top,
                fitByWidth =  attachOffset.left;

            var top = attachOffset.top + attachHeight + this.padding.top;
            var left = attachOffset.left + attachWidth;

            if(bodyHeight < elementHeight+fitByHeight){
                top = attachOffset.top - elementHeight - this.padding.top;
            }
            if(top < 0) {
                top = attachOffset.top - elementHeight/2 - this.padding.top;
            }
            if(top < 0){
                top = 0;
            }
            if(bodyWidth < elementWidth+fitByWidth){
                left = attachOffset.left - elementWidth;
            }
            if(left < 0){
                left = attachOffset.left + attachWidth/2  - elementWidth/2;
            }
            if(left < 0){
                left = 0;
            }
            if(top > attachOffset.top && bodyWidth > elementWidth+fitByWidth) {
                left= left - attachWidth;
            }

            _.extend(this, {top: top, left: left});

            this.$el.css({ top: this.top, left: this.left });
            this.$el.show();

            this.trigger("show", this);
        },

        /**
         * @description hides component
         * @returns {AttachableComponent}
         */
        hide: function() {
            this.$el.hide();

            this.trigger("hide", this);

            return this;
        }
    });
});
