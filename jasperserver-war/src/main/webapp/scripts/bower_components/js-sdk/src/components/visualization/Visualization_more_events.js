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

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        template = require("text!./template/visualizationTemplate.htm");

    return Backbone.View.extend({

        template: _.template(template),

        events: {
            "click .jr-mVisualization-switcher" : "_switcherActivated"
        },

        initialize: function (options) {

            if (options && options.props){
                this.props = options.props;
            }else{
                this.props = {
                    title : false
                }
            }

            watch(this.props, "title", this._onTitleChange.bind(this));


            this.listenTo(this, "canvas:switch", this.switchCanvasTo.bind(this));

            this.listenTo(this, "canvas:ready", options.evts["canvas:ready"]);

            this.render();


        },

        render: function () {

            this.$el = $(this.template({
                props : this.props
            }));

            //provide mount points

            this.$canvas = this.$(".jr-mVisualization-canvas");

            this.trigger("canvas:ready", this.$canvas);

            this.$switcher = this.$(".jr-mVisualization-switcher");
            this.$title = this.$(".jr-mVisualization-title");

            return this;
        },

        switchCanvasTo : function ($view) {
            this.$canvas.children().detach();
            $view && this.$canvas.append($view);
        },


        remove: function () {
            this.switchCanvasTo(null);
            Backbone.View.prototype.remove.call(this);

        },

        _onTitleChange: function(newTitle){

            var $switcherIcon = this.$switcher.find(".jr-mIcon");

            if (newTitle){
                this.$title.find(".jr-mVisualization-title-text").text(newTitle);
                this.$title.removeClass("jr-isHidden");
                this.$switcher.removeClass("jr-isMinimized");

                if ($switcherIcon.hasClass("jr-meatball")){
                    $switcherIcon
                        .removeClass("jr-meatball'")
                        .addClass("jr-chartColumn'");
                }
            }else{
                this.$title.addClass("jr-isHidden");
                this.$switcher.addClass("jr-isMinimized");

                if ($switcherIcon.hasClass("jr-chartColumn")){
                    $switcherIcon
                        .removeClass("jr-chartColumn")
                        .addClass("jr-meatball");
                }
            }
        },

        _switcherActivated: function () {
            this.trigger("switcher:activated");
        }

    });

    //Utils to watch for prop change in POJO

    function watch(obj, prop, handler) {
        var oldval = obj[prop], newval = oldval,
            getter = function () {
                return newval;
            },
            setter = function (val) {
                oldval = newval;
                newval = handler.call(this, val, oldval, prop)
                return newval;
            };
        if (delete obj[prop]) { // can't watch constants
            if (Object.defineProperty) // ECMAScript 5
                Object.defineProperty(obj, prop, {
                    get: getter,
                    set: setter
                });
            else if (Object.prototype.__defineGetter__ && Object.prototype.__defineSetter__) { // legacy
                Object.prototype.__defineGetter__.call(obj, prop, getter);
                Object.prototype.__defineSetter__.call(obj, prop, setter);
            }
        }
    }

    function unwatch(obj, prop) {
        var val = obj[prop];
        delete obj[prop]; // remove accessors
        obj[prop] = val;
    }



});
