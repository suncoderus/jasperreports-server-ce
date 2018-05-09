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

/* global ajaxTargettedUpdate, appendPostData, localContext, AjaxRequester, buttonManager, layoutModule */

var addListOfValues = {
    flowExecutionKey: '',
    messages: [],

    initialize: function() {
        this.initEvents();
    },

    initEvents: function() {
        $("stepDisplay").observe('submit', function(event) {
            if (!this.allowSubmit()) {
                event.stop();
            }
        });

        $("name").observe('change', function(event) {
            this.allowSubmit();
        }.bindAsEventListener(this));

        $("value").observe('change', function(event) {
            this.allowSubmit();
        }.bindAsEventListener(this));

        $("labelID").observe('change', function(event) {
            this.allowSubmit();
        }.bindAsEventListener(this));

        $("resourceID").observe('change', function(event) {
            this.allowSubmit();
        }.bindAsEventListener(this));

        $("listOfValues").observe('change', function(event) {
            var element = event.element();

            if (element.nodeName == "a" && element.identify() != "add") {
                var id = element.identify();

            }
        }.bindAsEventListener(this));

        $("add").observe('click', function(event) {
            $("stepDisplay").submit();
        }.bindAsEventListener(this));

        $("labelID").observe("keyup", function() {
            ajaxTargettedUpdate("flow.html?_flowId=resourceActionFlow&method=generateResourceName", {
                postData: appendPostData("", {ParentFolderUri: "/",
                    resourceLabel: $("labelID").getValue()}),
                callback: function(evalledJSON) {
                    if (localContext.initOptions.editMode == "false") {
                        $("resourceID").setValue(evalledJSON.newId);
                    }
                },
                mode: AjaxRequester.prototype.EVAL_JSON
            });
        }.bindAsEventListener(this));
    },

    allowSubmit: function() {
        if (!$("labelID").getValue().blank() && !$("resourceID").getValue().blank()) {
            buttonManager.enable("save");
            $("save").writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
        } else {
            buttonManager.disable("save");
            $("save").writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
        if (!$("name").getValue().blank() && !$("value").getValue().blank()) {
            buttonManager.enable("add");
            $("add").writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
        } else {
            buttonManager.disable("add");
            $("add").writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
    }
}

document.observe('dom:loaded', function() {
    addListOfValues.initialize();
});