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

define(function(require) {
    "use strict";

    var errorCodes = require("./biComponentErrorCodes");

    var messages = {};

    messages[errorCodes.UNEXPECTED_ERROR] = "An unexpected error has occurred";
    messages[errorCodes.SCHEMA_VALIDATION_ERROR] = "JSON schema validation failed";
    messages[errorCodes.UNSUPPORTED_CONFIGURATION_ERROR] = "Unsupported configuration provided";
    messages[errorCodes.AUTHENTICATION_ERROR] = "Authentication error";
    messages[errorCodes.AUTHORIZATION_ERROR] = "Authorization error";
    messages[errorCodes.CONTAINER_NOT_FOUND_ERROR] = "Container was not found in DOM";
    messages[errorCodes.REPORT_EXECUTION_FAILED] = "Report execution failed";
    messages[errorCodes.REPORT_EXECUTION_CANCELLED] = "Report execution was cancelled";
    messages[errorCodes.REPORT_EXPORT_FAILED] = "Report export failed";
    messages[errorCodes.REPORT_EXPORT_CANCELLED] = "Report export was cancelled";
    messages[errorCodes.REPORT_RENDER_ERROR] = "Report render error";
    messages[errorCodes.REPORT_RENDER_HIGHCHARTS_ERROR] = "Highcharts render error";
    messages[errorCodes.INPUT_CONTROLS_VALIDATION_ERROR] = "InputControls validation error";
    messages[errorCodes.ALREADY_DESTROYED_ERROR] = "Component has been already destroyed";
    messages[errorCodes.NOT_YET_RENDERED_ERROR] = "Component has not yet been rendered";
    messages[errorCodes.AD_HOC_VIEW_RENDER_ERROR] = "Ad Hoc View render error";

    return messages;
});
