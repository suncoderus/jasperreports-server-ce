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
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function(require) {

    var _ = require("underscore"),
        OptionContainer = require("common/component/base/OptionContainer"),
        confirmDialogTypesEnum = require("serverSettingsCommon/enum/confirmDialogTypesEnum"),
        buttonTemplate = require("text!serverSettingsCommon/templates/buttonTemplate.htm");

    return {
        _initButtons: function(options) {
            this.buttons = new OptionContainer({
                options: options.buttons,
                el: options.buttonsContainer,
                contextName: "button",
                optionTemplate: buttonTemplate
            });

            this.buttons.disable();

            this.listenTo(this.buttons, "button:save", this.saveChildren);
            this.listenTo(this.buttons, "button:cancel", _.bind(this._openConfirm, this, null, confirmDialogTypesEnum.CANCEL_CONFIRM));
        },

        toggleButtons: function() {
            var buttons = this.buttons;

            if (this.containsUnsavedItems()) {
                buttons.enable();
            } else {
                // removes buttons selection before disabling it.
                _.each(buttons.options, function(optionView) {
                    optionView.$el.removeClass("over");
                });

                buttons.disable();
            }
        }
    };

});