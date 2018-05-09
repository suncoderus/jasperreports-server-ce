/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * Licensed under commercial Jaspersoft Subscription License Agreement
 */


/**
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require('underscore'),
        Dialog = require("./Dialog"),
        confirmDialogTemplate = require("text!./template/confirmDialogTemplate.htm"),
        i18n = require('bundle!js-sdk/CommonBundle');

    return Dialog.extend(/** @lends ConfirmationDialog.prototype */{
        /**
         * @constructor ConfirmationDialog
         * @extends Dialog
         * @classdesc ConfirmationDialog component.
         * @param {object} options
         * @param {string} options.text Message for dialog
         * @param {string} options.title Title for dialog
         * @fires ConfirmationDialog#button:yes
         * @fires ConfirmationDialog#button:no
         */
        constructor: function(options) {
            options || (options = {});

            this.confirmDialogTemplate = _.template(confirmDialogTemplate);

            Dialog.prototype.constructor.call(this, {
                modal: true,
                additionalCssClasses: options.additionalCssClasses || "confirmationDialog",
                title: options.title || i18n["dialog.confirm.title"],
                content: this.confirmDialogTemplate({ text: options.text }),
                buttons: [
                    { label: options.yesLabel || i18n["button.yes"], action: "yes", primary: true },
                    { label: options.noLabel || i18n["button.no"], action: "no", primary: false }
                ]
            });
        },

        initialize: function() {
            Dialog.prototype.initialize.apply(this, arguments);

            /**
             * @event ConfirmationDialog#button:no
             */
            /**
             * @event ConfirmationDialog#button:yes
             */
            this.on("button:yes", this.close);
            this.on("button:no", this.close);
        },

        setContent: function(content) {
            Dialog.prototype.setContent.call(this, this.confirmDialogTemplate({text: content}));
        }
    });
});
