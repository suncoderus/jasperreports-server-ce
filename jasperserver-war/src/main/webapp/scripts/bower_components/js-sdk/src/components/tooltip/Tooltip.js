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
 * @author: Igor Nesterenko
 * @version: $Id$
 */

define(function (require, module) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        TooltipPopupView = require("./view/TooltipPopupView"),
        TooltipPopupModel = require("./model/TooltipPopupModel"),
        placements = require("./enum/tooltipPlacements"),
        log = require("logger").register(module),
        $ = Backbone.$,
        Event = require("components/utils/Event");

    var Tooltip = Backbone.View.extend({

        events : {
            "mouseenter" : "_onShow",
            "mouseleave" : "_onHide"
        },

        initialize: function (options) {
            options = options || {};

            this.log = options.log || log;

            var dataOptions = Tooltip.readTooltipDataFromDomElement(this.el);

            if (!_.isEmpty(dataOptions)){
                if (Tooltip.areSomeKeysEqual(dataOptions, options)){
                    this.log.warn(
                        "The same options found both in constructor and in 'data-' atts. Don't use both"
                    );
                }
                options = _.extend(options, dataOptions);
            }

            this.popup = new TooltipPopupView({
                placement: options.placement,
                model: new TooltipPopupModel({
                    content: Tooltip.convertContentToObject({
                        value: options.content,
                        log: this.log
                    })
                })
            });

            this.$container = $("body");
        },

        remove: function () {
            this.stopListening();
            this.popup.remove();
            return this;
        },

        show: function () {

            var attrData = Tooltip.readTooltipDataFromDomElement(this.el);

            if (!_.isEmpty(attrData)){
                attrData.placement && (this.placement = attrData.placement);
                attrData.content && (this.content = attrData.content);
            }

            if (!this.content){
                //don't show empty tooltip
                return;
            }

            //fill tooltip popup with content and insert to container
            //it gives us proper bounding rects
            this.popup.model.set({
                content : this.content
            });

            if (!$.contains(this.$container[0], this.popup.el)){
                this.$container.append(this.popup.$el);
            }

            var position = Tooltip.calculateTooltipPopupPosition(
                this.el.getBoundingClientRect(),
                this.popup.el.getBoundingClientRect(),
                this.placement
            );

            this.popup.model.set({
                visible: this.content ? true : false,
                placement: this.placement,
                position: position
            });

            return this;
        },

        hide: function () {
            this.popup.model.set({
                visible : false
            });
            this.popup.$el.detach();

            return this;
        },

        _onShow: function () {
            var event = new Event({ name: "show:tooltip"});
            this.trigger(event.name, event);
            if (!event.isDefaultPrevented()){
                this.show();
            }
        },

        _onHide: function () {
            var event = new Event({ name: "hide:tooltip"});
            this.trigger(event.name, event);
            if (!event.isDefaultPrevented()){
                this.hide();
            }
        }


    },{

        PLACEMENTS : placements,

        calculateTooltipPopupPosition : function(targetRect, popupRect, placement) {

            var sign, left, top, height, width, offset = {
                left : 6,
                top : 6
            };

            placement || ( placement = placements.BOTTOM);

            if (placements.BOTTOM == placement || placements.TOP == placement){
                left = targetRect.left + Math.round(targetRect.width / 2)  - Math.round(popupRect.width / 2);

                sign = placements.BOTTOM == placement ? 1 : -1;
                height = placements.BOTTOM == placement ? targetRect.height : popupRect.height;

                top = targetRect.top + sign * height + sign * offset.top;

            }else if (placements.LEFT == placement || placements.RIGHT == placement){

                top = targetRect.top + Math.round(targetRect.height / 2) - Math.round(popupRect.height / 2);

                sign = placements.RIGHT == placement ? 1 : -1;
                width = placements.RIGHT == placement ? targetRect.width : popupRect.width;

                left = targetRect.left + sign * width + sign * offset.left;
            }

            return {
                left : left,
                top : top
            }

        },

        readTooltipDataFromDomElement: function (elem) {

            var $element = $(elem),
                content = $element.data("jrContent"),
                placement = $element.data("jrPlacement"),
                result;

            if (content || placement) {
                result = {
                    placement: placement,
                    content: content
                }
            } else {
                result = {}
            }
            return result;
        },

        areSomeKeysEqual : function (obj1, obj2) {
           return _.intersection(Object.keys(obj1), Object.keys(obj2)).length > 0;
        },

        convertContentToObject : function (options) {

            options = options || {};

            var value = options.value || {},
                log = options.log,
                isEmptyString = _.isString(value) && value.length === 0,
                hasOneOfNessesaryProperties = !_.isUndefined(value.label) || !_.isUndefined(value.text),
                isObjectWithoutNessesaryProperties = _.isObject(value) && !hasOneOfNessesaryProperties;

            if (_.isUndefined(value) || isEmptyString || isObjectWithoutNessesaryProperties){
                log && log.warn("Can't find anything to display in 'content', tooltip won't be shown");
            }else{

                if (!_.isObject(value) && _.isString(value)){
                    value =  {
                        text: value
                    }
                }
            }

            return value;
        }

    });

    Tooltip.prototype = _.extend({

        get placement(){
            return this.popup.placement;
        },

        set placement(value){
            this.popup.placement = value;
        },

        get content(){
            return this.popup.model.get("content");
        },

        set content(value){
            this.popup.model.set("content",
                Tooltip.convertContentToObject({
                    value : value,
                    log : this.log
                }), {
                    validate: true
                });
        }

    }, Tooltip.prototype);

    return Tooltip;
});
