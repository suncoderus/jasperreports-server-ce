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

(function ($) {

    var firstRequest = true;

    var iframe = $("#exportFrame")[0];
    $("#exportButton").click(function () {
        firstRequest = false;
        iframe.src = "export/export/{filename}.zip"
            .replace("{filename}", $(".fileName").val());
    });

    $(document).bind("global.ajaxPost", function (evt) {
        var result = $.parseJSON(evt.response);
        if (result) {
            $("#response").html(
                "<span style='background-color: green;font-weight: bold;'>Phase: {phase} Message: {message}</span>"
                    .replace("{phase}", evt.response.phase)
                    .replace("{message}", evt.response.message)
            );
        } else {
            $("#response").html("<span style='background-color: red;font-weight: bold;'>Failed uploading: Unrecognized response from the server.</span>");
        }
    });

    (function (iframe) {
        var event = new $.Event("global.ajaxPost");
        if (iframe.attachEvent) {
            iframe.attachEvent("onload", function () {
                event.response = iframe.contentWindow.document;
                //workaround for 'browser'
                !firstRequest && $(document).trigger(event)
            });
        } else {
            iframe.onload = function () {
                event.response = iframe.contentWindow.document;
                //workaround for FF
                !firstRequest && $(document).trigger(event);
            }
        }
    })(iframe);

})(jQuery);