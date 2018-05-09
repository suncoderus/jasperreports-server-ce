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

/* global __jrsConfigs__ */

define(function(require){

    "use strict";

    require("commons.bare.main");

    var domReady = require("!domReady");
    var heartbeat = require("components.heartbeat");
    var jrsConfigs = require("jrs.configs");
    var aboutDialog = require("components.about");
    var webHelp = require("components.webHelp");
    var $ = require("jquery");
    var AttributesCollection = require("attributes/collection/AttributesCollection");
    var AttributeModel = require("attributes/model/AttributeModel");

    if (__jrsConfigs__.enableAccessibility === 'true') {
        // Standard Navigation system (stdnav)
        // Provides focus management and keyboard event handling features.
        var stdnav = require("stdnav");

        // Basic stdnav plugins from js-sdk
        var stdnavPluginAnchor = require("stdnavPluginAnchor");
        var stdnavPluginButton = require("stdnavPluginButton");
        var stdnavPluginForms = require("stdnavPluginForms");
        var stdnavPluginGrid = require("stdnavPluginGrid");
        var stdnavPluginList = require("stdnavPluginList");
        var stdnavPluginTable = require("stdnavPluginTable");

        // JasperServer-specific stdnav plugins from jrs-ui
        var stdnavPluginActionMenu = require("stdnavPluginActionMenu");
        var stdnavPluginDynamicList = require("stdnavPluginDynamicList");
    }

    //configure date/time pickers globally
    require('config/dateAndTimeSettings');

    domReady(function(){
        //Heartbeat
        heartbeat.initialize(jrsConfigs.heartbeatInitOptions);
        heartbeat.start();

        jrsConfigs.initAdditionalUIComponents && aboutDialog.initialize();

        //Web help
        var helpLink = $("#helpLink");
        if (helpLink) {
            helpLink.on("click", function(e) {
                e.preventDefault();
                webHelp.displayWebHelp();
            });
        }

        if (__jrsConfigs__.enableAccessibility === 'true') {
            // Basic stdnav plugins from js-sdk
            stdnav.activate();
            stdnavPluginAnchor.activate(stdnav);
            stdnavPluginButton.activate(stdnav);
            stdnavPluginForms.activate(stdnav);
            stdnavPluginGrid.activate(stdnav);
            stdnavPluginList.activate(stdnav);
            stdnavPluginTable.activate(stdnav);

            // JasperServer-specific stdnav plugins from jrs-ui
            stdnavPluginActionMenu.activate(stdnav);
            stdnavPluginDynamicList.activate(stdnav);
            stdnav.start();
        }
    });
});

