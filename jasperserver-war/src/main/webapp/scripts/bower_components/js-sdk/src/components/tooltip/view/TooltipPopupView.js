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

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        tooltipPopupTemplate = require("text!../template/tooltipPopupTemplate.htm"),
        TooltipPopupModel = require("../model/TooltipPopupModel"),
        placements = require("../enum/tooltipPlacements"),
        capitalize = _.str.capitalize;

    var TooltipPopupView =  Backbone.View.extend({

        template: _.template(tooltipPopupTemplate),

        el: function () {
            if (!this.model){
                this.model = new TooltipPopupModel()
            }
            return this.template(this.model.toJSON());
        },

        initialize: function (options) {

            options = options || {};

            this.placement = options.placement;

            this.listenTo(this.model, "change", this.render);
        },

        render: function () {

            if(this.model.hasChanged("content")){
                var content = this.model.get("content");

                this.$(".jr-jTooltipText").text(content.text);
                this.$(".jr-jTooltipLabel").text(content.label);
            }

            if (this.model.hasChanged("visible")){
                if (this.model.get("visible")){
                    this.$el.removeClass("jr-isInvisible");
                }else{
                    this.$el.addClass("jr-isInvisible");
                }
            }

            if (this.model.hasChanged("position")){
                var position =  this.model.get("position");
                this.$el.css({
                    position :  "absolute",
                    top : position.top + "px",
                    left : position.left + "px"
                });
            }

            if (this.model.hasChanged("placement")){
                var prevPlacement = capitalize(this.model.previousAttributes().placement),
                    newPlacement = capitalize(this.model.get("placement"));
                this.$el
                    .removeClass("jr-mTooltip" + prevPlacement)
                    .addClass("jr-mTooltip" + newPlacement);

            }

            return this;
        }

    }, {
        PLACEMENTS : placements
    });

    TooltipPopupView.prototype = _.extend({

        get placement(){
            return this._placement;
        },

        set placement(value){

            if (_.isUndefined(value) || (_.isString(value) && value.length === 0)){
                value = TooltipPopupView.PLACEMENTS.BOTTOM;
            }else if (!_.isString(value)){
                throw new TypeError("'placement' should be string");
            }

            var isOneOfAvailablePlacements = Object.keys(TooltipPopupView.PLACEMENTS).some(function(key){
                return TooltipPopupView.PLACEMENTS[key] === value;
            });

            if (!isOneOfAvailablePlacements){
                throw new Error("'placement' should be one of the available placements");
            }

            this._placement = value;
        }

    }, TooltipPopupView.prototype);

    return TooltipPopupView;
});
