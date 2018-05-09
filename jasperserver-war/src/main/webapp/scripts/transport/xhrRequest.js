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
 * @author: Andrew Godovanec
 * @version: $Id$
 */

define(function(require) {
    "use strict";

    var $ = require("jquery"),
        _ = require("underscore"),
        log = require("logger").register("request");

    // workaround for optimizer which usually runs in Node env without document defined
    if (typeof document === "undefined") {
        return {};
    }

    // TODO: observableRequest functionality is temporary here. This should be revised after Amber release.

    var $document = $(document);

    function triggerEvent() {
        $document.trigger.apply($document, arguments);
    }

    function checkSessionExpiration(xhr) {
        //TODO: custom error handlers should be used instead
        if (xhr.status == 401 || xhr.getResponseHeader("LoginRequested")) {
            //trigger custom event to allow do something before page reloading
            $(window).trigger("sessionExpired");
            log.warn("Session timed-out. Redirecting to login page.");
            //Force page reload
            window.location.reload();
        }
    }

    return function(ajaxParams) {
        _.partial(triggerEvent, "request:before").apply(null, arguments);

        return $.ajax(ajaxParams)
            .fail(checkSessionExpiration)
            .fail(function(jqXHR, textStatus, errorThrown) {
                if (jqXHR.getResponseHeader("adhocException")){
                    log.error(jqXHR.getResponseHeader("adhocException"));
                } else if (jqXHR.status == 500 || (jqXHR.getResponseHeader("JasperServerError") && !jqXHR.getResponseHeader("SuppressError"))) {
                    log.error(jqXHR.responseText);
                }
            })
            .fail(_.partial(triggerEvent, "request:failure"))
            .done(_.partial(triggerEvent, "request:success"));
    };
});