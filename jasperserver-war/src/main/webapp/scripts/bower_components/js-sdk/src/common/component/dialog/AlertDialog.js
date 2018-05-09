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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require('underscore'),
        Dialog = require("./Dialog"),
        xssUtil = require("common/util/xssUtil"),
        alertDialogTemplate = require("text!./template/alertDialogTemplate.htm"),
        i18n = require('bundle!js-sdk/CommonBundle');

    return Dialog.extend(/** @lends AlertDialog.prototype */{
        contentTemplate: _.template(alertDialogTemplate),

        /**
         * @constructor AlertDialog
         * @extends Dialog
         * @classdesc AlertDialog component.
         * @param {object} options
         * @param {string} options.message Message for dialog
         * @fires button:close
         */
        constructor: function(options) {
            options || (options = {});

            Dialog.prototype.constructor.call(this, {
                modal: options.modal !== false,
                message: options.message,
                additionalCssClasses: "alertDialog " + (options.additionalCssClasses || ""),
                title: options.title || i18n["dialog.exception.title"],
                buttons: [
                    { label: i18n["button.close"], action: "close", primary: true }
                ]
            }, options);
        },

        initialize: function(options) {
            Dialog.prototype.initialize.apply(this, arguments);

            this.on("button:close", this.close);

            this.setMessage(options.message);
        },

        /**
         * @description Set dialog message.
         * @param {string} message Message for dialog
         */
        setMessage: function(message) {

            // remove all tags but let new line separator (tag <br/>) be available to be used
            message = xssUtil.escape(message, {softHTMLEscape: true, whiteList: ["br"]});

            this.content = this.contentTemplate({message: message});

            var rendered = this.renderContent();

            this.$contentContainer.html(rendered);
        }
    });
});
