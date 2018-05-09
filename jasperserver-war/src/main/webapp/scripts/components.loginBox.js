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
 * @version: $Id$
 */

/* global webHelpModule, dialogs, layoutModule, ValidationModule */

var loginBox = {
    LOGIN_BOX_TEMPLATE_DOM_ID: "login",

    DOCUMENTATION_BUTTON_ID: "documentationButton",
    GOTO_JASPERFORGE_BUTTON_ID: "gotoJasperForge",
    CONTACT_SALES_BUTTON_ID: "contactSalesButton",
    NEED_HELP_LINK_ID: "needHelp",

    NEED_HELP_DIALOG_ID: "helpLoggingIn",

    CONTACT_SALES_URL: "http://www.jaspersoft.com/contact-us",

    _dom: null,

    _baseInitialize: function(options) {
        this._initVars(options);

        this._processTemplate();
        this._initHandlers();

        if (this._warningMessage) {
            this._customError.update(this._warningMessage);
            this._customError.removeClassName("hidden");
        }

        if (this._passwordExpiredDays) {
            this._passwordExpiredDays.setValue(this._passwordExpirationInDays);
        }
    },

    initialize: function(options) {
        this._baseInitialize(options);
    },

    _baseInitVars: function(options) {
        this._showLocaleMessage = options.showLocaleMessage;
        this._hideLocaleMessage = options.hideLocaleMessage;
        this._changePasswordMessage = options.changePasswordMessage;
        this._cancelPasswordMessage = options.cancelPasswordMessage;

        this._allowUserPasswordChange = options.allowUserPasswordChange;
        this._showPasswordChange = options.showPasswordChange;
        this._allowedPasswordPattern = new RegExp(options.allowedPasswordPattern);

        this._passwordExpirationInDays = options.passwordExpirationInDays;

        this._nonEmptyPasswordMessage = options.nonEmptyPasswordMessage;
        this._passwordNotMatchMessage = options.passwordNotMatchMessage;
        this._passwordNotMatchMessage = options.passwordNotMatchMessage;
        this._passwordTooWeakMessage = options.passwordTooWeakMessage;

        this._warningMessage = options.warningMessage;
    },

    _initVars: function(options) {
        this._baseInitVars(options);
    },

    _baseProcessTemplate: function() {
        this._dom = $(this.LOGIN_BOX_TEMPLATE_DOM_ID);

        this._usernameInput = $('j_username');
        this._passwordInput = $('j_password');
        this._showHideLocaleAndTimezone = $('showHideLocaleAndTimezone');
        this._localeAndTimeZone = $('localeAndTimeZone');
        this._userLocale = $('userLocale');

        this._changePassword = $('changePassword');
        this._j_newpassword1 = $('j_newpassword1');
        this._j_newpassword2 = $('j_newpassword2');
        this._showHideChangePassword = $('showHideChangePassword');
        this._passwordExpiredDays = this._dom.select('input[name="passwordExpiredDays"]')[0];

        this._customError = $("customError");

        this._loginForm = this._dom.up('form');

        this.documentationButton = $(this.DOCUMENTATION_BUTTON_ID);
        this.gotoJasperForge = $(this.GOTO_JASPERFORGE_BUTTON_ID);
        this.needHelpLink = $(this.NEED_HELP_LINK_ID);

        this.needHelpDialog = $(this.NEED_HELP_DIALOG_ID);
    },

    _processTemplate: function() {
        this._baseProcessTemplate();
    },

    _initHandlers: function() {
        this._showHideLocaleAndTimezone.observe('click', this._localeAndTimezoneShowHideHandler.bindAsEventListener(this));

        if(this._allowUserPasswordChange) {
            this._showHideChangePassword.observe('click', this._changePasswordShowHideHandler.bindAsEventListener(this));
            // use jQuery to work with login.js
            jQuery(this._loginForm).on('submit', this._submitValidateHandler.bind(this));
        }

        if (this._showPasswordChange) {
            this._changePasswordShowHideHandler();
        }

        //web help
        if (window.webHelpModule) {
            this.documentationButton && this.documentationButton.observe("click", function(e) {
                webHelpModule.displayWebHelp();
            }.bindAsEventListener(this));
        }

        this.gotoJasperForge && this.gotoJasperForge.observe("click", function(e) {
            var url = "http://jasperforge.org";
            window.name = "";
            var runPopup=window.open(url, "jasperforge.org");
            runPopup.focus();
        }.bindAsEventListener(this));

        this.needHelpLink.observe("click", function(e) {
            dialogs.popup.show(this.needHelpDialog);
        }.bindAsEventListener(this));

        var loginDialogs = [this.needHelpDialog];

        loginDialogs.each(function(dialog) {
            dialog.select(layoutModule.BUTTON_PATTERN)[0].observe("click", function(e) {
                dialogs.popup.hide(dialog);
            });
        });
    },

    _submitValidateHandler: function(event) {
        this._customError.addClassName("hidden");

        if (!this._changePassword.hasClassName("hidden")) {
            var isValid = ValidationModule.validate([
                {
                    validator: this._emptyPasswordValidator.bind(this),
                    element: this._j_newpassword1
                },
                {
                    validator: this._emptyPasswordValidator.bind(this),
                    element: this._j_newpassword2
                },
                {
                    validator: this._confirmationPasswordNotMatchValidator.bind(this),
                    element: this._j_newpassword2
                },
                {
                    validator: this._confirmationPasswordTooWeakValidator.bind(this),
                    element: this._j_newpassword1
                }
            ]);

            if (!isValid) {
                event.preventDefault();
            }
        }
    },

    _emptyPasswordValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value.blank()) {
            isValid = false;
            errorMessage = this._nonEmptyPasswordMessage;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _confirmationPasswordNotMatchValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value != this._j_newpassword1.getValue()) {
            isValid = false;
            errorMessage = this._passwordNotMatchMessage;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _confirmationPasswordTooWeakValidator: function(value) {
        return {
            isValid: this._allowedPasswordPattern.test(value),
            errorMessage: this._passwordTooWeakMessage
        }
    },

    _changePasswordShowHideHandler: function() {
        this._changePassword.toggleClassName("hidden");

        if (this._changePassword.hasClassName("hidden")) {
            this._showHideChangePassword.update(this._changePasswordMessage);
            this._j_newpassword1.setValue("");
            this._j_newpassword2.setValue("");
        } else {
            this._showHideChangePassword.update(this._cancelPasswordMessage);
            this._j_newpassword1.focus();
        }
    },

    _localeAndTimezoneShowHideHandler: function() {
        this._localeAndTimeZone.toggleClassName("hidden");

        if (this._localeAndTimeZone.hasClassName("hidden")) {
            this._showHideLocaleAndTimezone.update(this._showLocaleMessage);
        } else {
            this._showHideLocaleAndTimezone.update(this._hideLocaleMessage);
            this._userLocale.focus();
        }
    }
};
