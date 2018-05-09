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

/* global Hash, __jrsConfigs__ */

define(function (require) {
    "use strict";

    var domReady = require("!domReady"),
        text = require("text"),
        jrsConfigs = require("jrs.configs"),
        baseControls = require("controls.base"),
        _ = require("underscore"),
        $ = require("jquery"),
        layoutModule = require("core.layout"),
        Report = require("report.view");

    require("components.toolbar");
    require("controls.report");
    require("json2");
    require("jquery.ui.mouse.touch");

    window.printRequest = function () {
        // Leave empty. This function is invoked by Flash charts in report for managed print.
        // Printing is not available in report view, only in dashboard view.
    };

    window.FC_Rendered = function (DOMId) {
        $('#' + DOMId).hide().show();
    };

    domReady(function() {
        if(window.Prototype) {
            delete Object.prototype.toJSON;
            delete Array.prototype.toJSON;
            delete Hash.prototype.toJSON;
            delete String.prototype.toJSON;
        }

        _.extend(baseControls, jrsConfigs.inputControlConstants);
        _.extend(Report, jrsConfigs.reportViewer.ReportSettings);

        if (Report.hasInputControls && (Report.reportControlsLayout === 2 || Report.reportControlsLayout === 4)) {
            layoutModule.resizeOnClient("inputControlsForm", "reportViewFrame");
        }

        require(["ReportRequireJsConfig"], function() {
            //We can start load report viewer only when additional requirejs config is loaded
            //so check that additional config is for sure loaded
        require(['reportViewer/viewer'], function(Viewer) {
            window.viewer = new Viewer({
                at: '#reportContainer',
                reporturi: Report.reportUnitURI,
                async: true,
                page: 0,
                contextPath: __jrsConfigs__.contextPath
            });

            window.jasperreports = {
                reportviewertoolbar: window.viewer
                };

            Report.initialize();
        });

       //workaround for deprecated JIVE components
       //it fixes styling of jquery.ui.datepicker
       var $body = $("body");
       if (!$body.hasClass("jr")){
           $body.addClass("jr");
       }

    });
    });
});
