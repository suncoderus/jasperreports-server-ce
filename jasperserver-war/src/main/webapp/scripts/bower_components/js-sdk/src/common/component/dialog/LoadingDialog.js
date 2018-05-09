/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * Licensed under commercial Jaspersoft Subscription License Agreement
 */


/**
 * @author: Zahar Tomchenko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require('underscore'),
        Dialog = require("./Dialog"),
        confirmDialogTemplate = require("text!./template/loadingDialogTemplate.htm"),
        i18n = require('bundle!CommonBundle');

    return Dialog.extend({

        events: _.extend({}, Dialog.prototype.events, {
            "click button": "cancel"
        }),

        el: function(){
            return this.template({
                title: this.title,
                additionalCssClasses: this.additionalCssClasses,
                i18n: i18n
            });
        },

        constructor: function (options) {
            this.options = options || {};

            Dialog.prototype.constructor.call(this, {
                modal: true,
                additionalCssClasses: this.options.additionalCssClasses,
                template: confirmDialogTemplate,
                title: this.options.title || i18n["dialog.overlay.title"]
            });

            if (this.options.showProgress){
                this.progress = _.bind(function(progress){
                    if (arguments.length === 0){
                        return +this.$(".percents").text();
                    } else {
                        return this.$(".percents").text(progress + "%");
                    }
                }, this);

                this.on("open", _.bind(this.progress, this, 0));

            }
        },

        initialize: function () {
            Dialog.prototype.initialize.apply(this, arguments);

            if (this.options.cancellable){
                this.on("button:cancel", this.close);
            } else {
                this.$("button").hide();
            }
        },

        cancel: function(){
            this.trigger("button:cancel", this);
        }
    });
});
