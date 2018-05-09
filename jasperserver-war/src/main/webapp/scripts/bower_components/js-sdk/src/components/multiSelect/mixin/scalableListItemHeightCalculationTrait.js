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
 * @author Sergey Prilukin; modified by Ken Penn
 * @version: $Id: scalableListItemHeightCalculationTrait.js 1110 2015-04-20 13:00:54Z spriluki $
 */

/**
 * Trait which allows ScalableList to determine height of the item
 */

define(function (require) {
    'use strict';

    var $ = require("jquery"),
        ScalableListModel = require("components/scalableList/model/ScalableListModel"),
        browserDetection = require("common/util/browserDetection"),
        doCalcOnVisibleNodeClone = require("components/scalableList/util/domAndCssUtil").doCalcOnVisibleNodeClone;

    var scalableListItemHeightCalculationTrait = {
        render: function() {

            //do this only once to calculate itemHeight
            if (!this._itemHeightSet) {
                this._calcItemHeight();
                this._itemHeightSet = true;
            }

            return this;
        },

        _calcItemHeight: function() {

            var self = this;

            //backup original values
            this._model = this.model;
            this._lazy = this.lazy;
            this._visibility = this.$el.css("visibility");
            this._display = this.$el.css("display");

            //create model with just single item
            this.model = new ScalableListModel({
                getData: function() {
                    var deferred = new $.Deferred();
                    deferred.resolve({
                        total: 1,
                        data: [
                            {value: "test value", label: "test label"}
                        ]
                    });

                    return deferred;
                }
            });

            //make element visible for measurements
            //but hidden for user
            this.$el.css({
                "visibility": "hidden",
                "display": "block"
            });

            this.model.once("change", this.onModelChange, this);
            this.renderData();

            //render list copy to a body so it's visible and
            //we can calculate height
            doCalcOnVisibleNodeClone({
                el: this.$el,
                css: {"width": "100px"},
                classes: " jr " + (browserDetection.isIPad() ? "ipad" : ""),
                callback: function($el) {
                    //calculate itemHeight
                    self.itemHeight = $el.find("li").outerHeight(true);
                    //Calibrate canvas chunk height so even number of items will fit into one chunk
                    self.itemsPerChunk = Math.floor(self.defaultChunkHeight / self.itemHeight);
                    self.chunkHeight = self.itemsPerChunk * self.itemHeight;
                }
            });

            //clean element
            this.$el.empty().css({
                "visibility": this._visibility,
                "display": this._display
            });
            delete this._visibility;
            delete this._display;

            //restore original variables;
            this.totalItems = undefined;
            this.$firstViewChunk = undefined;

            this.model = this._model;
            delete this._model;
            this.lazy = this._lazy;
            this._lazy && delete this._lazy;
        },

        _calcViewPortConstants: function() {
            if (!this.viewPortConstantsInitialized) {
                if (!this._itemHeightSet) {
                    //we can not calculate constants without itemHeight
                    return;
                }

                //calc viewPortHeight
                this._calcViewPortHeight();

                //We have to rerender list with new dimensions
                this._renderViewChunks(true);
                this._renderItems();

                this.viewPortConstantsInitialized = true;
            }
        }
    };

    return scalableListItemHeightCalculationTrait;
});

