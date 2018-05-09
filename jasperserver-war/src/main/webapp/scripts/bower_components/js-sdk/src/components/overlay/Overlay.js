/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * Licensed under commercial Jaspersoft Subscription License Agreement
 */


/**
 * @author: valeriy.abornyev
 * @version: $Id: LoadingOverlay.js 1025 2016-08-11 12:58:43Z psavushc $
 */

define(function (require, exports, module) {
    "use strict";

    var _ = require("underscore"),
        $ = require("jquery"),
        Backbone = require("backbone"),

        overlayTemplate = require("text!./template/overlayTemplate.htm");


    return Backbone.View.extend({

        el: function () {
            return this.template();
        },

        template: _.template(overlayTemplate),

        initialize: function (options) {
            if(options) {
                this.delay = options.delay;
            }
            this.$el.parent().css({position: "relative"});
        },

        show: function(delay) {
            var self = this,
                show = function(){
                self.$el.show();
                self.$el.removeClass("jr-isHidden");
            };

            if (this.delay || delay){
                if (!this._timer){
                    this._timer = setTimeout(show, this.delay || delay);
                }
            } else show();
        },

        hide: function() {
            if (this._timer) {
                clearTimeout(this._timer);
                this._timer = null;
            }

            this.$el.hide();
            this.$el.addClass("jr-isHidden");
        }
    });

});
