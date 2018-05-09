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

/* global layoutModule, webHelpModule, Administer, matchAny, matchMeOrUp, buttonManager, dialogs,
*/

var Options = {

    SAVE_PFX: 'save',
    CANCEL_PFX: 'cancel',
    ERROR_PFX: 'error_',
    INPUT_PFX: 'input_',
    BUTTON_FLASH: 'flushOLAPCache',

    initialize: function() {
        if (jQuery("#serverSettingsMenu").length > 0) {
            layoutModule.resizeOnClient('serverSettingsMenu', 'settings');
        }

        webHelpModule.setCurrentContext("admin");

        $('display').observe('click', function(e) {
            var elem = e.element();

            // observe navigation
            for (var pattern in Administer.menuActions) {
                if (matchAny(elem, [pattern], true) && !(matchMeOrUp(elem.parentNode, 'li').hasClassName('selected'))) {
                    document.location = Administer.menuActions[pattern]();
                    return;
                }
            }

            if (matchAny(elem, ['#' + Options.BUTTON_FLASH], true)) {
                Event.stop(e);
                Options.flushCache();
                return;
            }

            var button = matchAny(elem, ['#' + Options.SAVE_PFX], true);
            if (button) {
                Event.stop(e);
                var name = button.name;
                Options.saveValue(name);
                return;
            }

            button = matchAny(elem, ['#' + Options.CANCEL_PFX], true);
            if (button) {
                Event.stop(e);
                Options.resetValue(button.name,button.value);
            }


            // observe inputs
            if (matchAny(elem,['.checkBox > input','select'])) {
                Options.switchButtons(elem, true);
            }
        });


        /*
         // observe inputs
         $('display').observe('change', function(e) {
         var elem = e.element();

         if (matchAny(elem,['input','select'])) {
         Options.switchButtons(elem, true);
         }
         });
         */

        $('display').observe('keydown', function(e) {
            var elem = e.element();

            if (elem.match('input')) {
                Options.switchButtons(elem, true);
            }
        });

    },

    saveValue: function(nm) {

        var input = $(Options.INPUT_PFX + nm);
        var params = {
            name: nm,
            value: (input.type == "checkbox") ? input.checked : input.value,
            _flowExecutionKey: Administer.flowExecutionKey,
            _eventId: 'saveSingleProperty'
        }

        var url = 'flow.html?' + Object.toQueryString(params);
        Administer._sendRequest(url,params,Options._updateCallback);
    },

    resetValue: function(nm,vl) {
        var input = $(Options.INPUT_PFX + nm);
        if (input.type == "checkbox") {
            input.checked = ( String(vl) == "true" );
        } else {
            input.value = vl;
        }
        Options.switchButtons(input,false);
        $(document.body).select('[for="' + Options.INPUT_PFX + nm + '"]')[0].removeClassName(layoutModule.ERROR_CLASS);
    },

    flushCache: function() {
        var url = window.isProVersion() ?
            'flow.html?_flowExecutionKey=' + Administer.flowExecutionKey + '&_eventId=flushCache' : 'flush.html';

        Administer._sendRequest(url, null, Options._flushCallback);
    },


    switchButtons: function(input, enable) {
        if (typeof(input) == "string") {
            input = $(Options.INPUT_PFX + input)
        }
        Options._enableButton($(matchMeOrUp(input, "li")).select("button")[0],enable);
        Options._enableButton($(matchMeOrUp(input, "li")).select("button")[1],enable);
    },

    _enableButton: function(button, enable) {
        if (enable) {
            buttonManager.enable(button);
        } else {
            buttonManager.disable(button);
        }
    },


    _updateCallback: function(response) {
        if (response.error) {
            $(document.body).select('[for="'+ Options.INPUT_PFX + response.optionName+'"]')[0].addClassName(layoutModule.ERROR_CLASS);
            $(Options.ERROR_PFX + response.optionName).update(Administer.getMessage(response.error));
        } else {
            Options.switchButtons(response.optionName,false);
            dialogs.systemConfirm.show(Administer.getMessage(response.result));
            $(document.body).select('[for="'+ Options.INPUT_PFX + response.optionName+'"]')[0].removeClassName(layoutModule.ERROR_CLASS);
        }
    },

    _flushCallback: function(response) {
        if (response.error) {
            dialogs.systemConfirm.show(Administer.getMessage("JAM_018_ERROR") + ": " + response.error);
        } else {
            dialogs.systemConfirm.show(Administer.getMessage(response.result));
        }

    }

};

if (typeof require === "undefined") {
    // prevent conflict with domReady plugin in RequireJS environment
    document.observe('dom:loaded',Options.initialize.bind(Options));
}

