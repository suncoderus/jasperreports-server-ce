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

/* global layoutModule, webHelpModule, matchAny, Administer */

var logging = {

    initialize: function() {
        layoutModule.resizeOnClient('serverSettingsMenu', 'settings');
        webHelpModule.setCurrentContext("admin");

        this.initEvents();
    },

    initEvents: function() {
        var self = this;

        $('display').observe('click', function(e) {
            var elem = e.element();

            var button = matchAny(elem, [layoutModule.BUTTON_PATTERN], true);
            if (button) {
                // observe navigation
                for (var pattern in Administer.menuActions) {
                    if (button.match(pattern) && !button.up('li').hasClassName('selected')) {
                        document.location = Administer.menuActions[pattern]();
                        return;
                    }
                }

            }

        });

        jQuery(".js-logSettings select").on("change", function(e) {
            var $el = jQuery(e.target),
                loggerName;

            if ($el.hasClass("js-newLogger")) {
                loggerName = jQuery("#newLoggerName").val();
            } else {
                loggerName = $el.parent().prev().text();
            }

            self._setLevel(encodeURIComponent(loggerName), $el.val());
        });
    },

    _setLevel: function(logger, level) {
        document.location = 'log_settings.html?logger=' + logger + '&level=' + level;
    }
};

if (typeof require === "undefined") {
    // prevent conflict with domReady plugin in RequireJS environment
    document.observe('dom:loaded', function() {
        logging.initialize();
    });
}