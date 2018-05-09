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

/* global isIPad */

define(function(require) {
    "use strict";

    var domReady = require("!domReady"),
        resourceReport = require("resource.report"),
        _ = require("underscore"),
        templateEngine = require("components.templateengine"),
        jrsConfigs = require("jrs.configs"),
        resource = require("resource.base"),
        jQuery = require("jquery");

    require("utils.common");

    domReady(function(){
        _.extend(resource.messages, jrsConfigs.addJasperReport.resource.messages);

        resourceReport.initialize();
        isIPad() && resource.initSwipeScroll();

        var resourcesContainer = jQuery(jQuery("#resources ul")[0]);

        addNonSuggestedResourcesSection(resourcesContainer, jrsConfigs.addJasperReport.nonSuggestedResources, jrsConfigs.addJasperReport.canChangeResources);
        addSuggestedResourcesSection(resourcesContainer, jrsConfigs.addJasperReport.suggestedResources);
        addAddResourceSection(resourcesContainer, {canChangeResources: jrsConfigs.addJasperReport.canChangeResources});

        var controlsContainer = jQuery(jQuery("#controls ul")[0]);

        addNonSuggestedControlsSection(controlsContainer, jrsConfigs.addJasperReport.nonSuggestedControls, jrsConfigs.addJasperReport.canChangeResources);
        addSuggestedControlsSection(controlsContainer, jrsConfigs.addJasperReport.suggestedControls);
        addAddControlSection(controlsContainer, {canChangeResources: jrsConfigs.addJasperReport.canChangeResources});
    });

    function addAddResourceSection(container, addResourceModel) {
        var addResourceTemplate = templateEngine.getTemplateText("addJasperReportAddResourceTemplate");

        var el = jQuery(templateEngine.render(addResourceTemplate, addResourceModel));

        el.find("a.launcher").on("click", function() {
            resourceReport.addResource();
            return false;
        });

        container.append(el);
    }

    function addAddControlSection(container, addControlModel) {
        var addControlTemplate = templateEngine.getTemplateText("addJasperReportAddControlTemplate");

        var el = jQuery(templateEngine.render(addControlTemplate, addControlModel));

        el.find("a.launcher").on("click", function() {
            resourceReport.addControl();
            return false;
        });

        container.append(el);
    }

    function addSuggestedResourcesSection(container, suggestedResources) {
        var suggestedResourceTemplate = templateEngine.getTemplateText("addJasperReportSuggestedResourceTemplate");

        _.each(suggestedResources, function(model) {
            var el = jQuery(templateEngine.render(suggestedResourceTemplate, model));

            el.find("a.emphasis").on("click", function() {
                resourceReport.editResource(model.name);
                return false;
            });

            if (!model.located) {
                el.find("a.launcher").on("click", function() {
                    resourceReport.editResource(model.name);
                    return false;
                });
            }

            container.append(el);
        });
    }

    function addSuggestedControlsSection(container, suggestedControls) {
        var suggestedControlTemplate = templateEngine.getTemplateText("addJasperReportSuggestedControlTemplate");

        _.each(suggestedControls, function(model) {
            var el = jQuery(templateEngine.render(suggestedControlTemplate, model));

            el.find("a.emphasis").on("click", function() {
                resourceReport.editControl(model.name);
                return false;
            });

            if (model.located) {
                el.find("a.launcher").on("click", function() {
                    resourceReport.removeControl(model.name);
                    return false;
                });
            }

            container.append(el);
        });
    }

    function addNonSuggestedResourcesSection(container, nonSuggestedResources, canChangeResources) {
        var nonSuggestedResourceTemplate = templateEngine.getTemplateText("addJasperReportNonSuggestedResourceTemplate");

        _.each(nonSuggestedResources, function(model) {
            var el = jQuery(templateEngine.render(nonSuggestedResourceTemplate, model));

            if (canChangeResources) {
                el.find("a.emphasis").on("click", function() {
                    resourceReport.editResource(model.name);
                    return false;
                });
                el.find("a.launcher").on("click", function() {
                    resourceReport.removeResource(model.name);
                    return false;
                });
            }

            container.append(el);
        });
    }

    function addNonSuggestedControlsSection(container, nonSuggestedControls, canChangeResources) {
        var nonSuggestedControlTemplate = templateEngine.getTemplateText("addJasperReportNonSuggestedControlTemplate");

        _.each(nonSuggestedControls, function(model) {
            var el = jQuery(templateEngine.render(nonSuggestedControlTemplate, model));

            if (canChangeResources) {
                el.find("a.emphasis").on("click", function() {
                    if (model.local) {
                        resourceReport.editControl(model.name);
                    } else {
                        resourceReport.editControl(model.referenceURI);
                    }
                    return false;
                });

                el.find("a.launcher").on("click", function() {
                    if (model.local) {
                        resourceReport.removeControl(model.name);
                    } else {
                        resourceReport.removeControl(model.referenceURI);
                    }
                    return false;
                });
            }

            container.append(el);
        });
    }
});
