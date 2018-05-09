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

/* global matchAny, buttonManager, layoutModule */

var STEP_DISPLAY_ID = "stepDisplay";
var WIZARD_NAV_ID = "wizardNav";
var FRAME_ID = "frame";
var VALUE_ID_PATTERN = "#value";
var LABEL_ID_PATTERN = "#labelID";
var ADD_PATTERN = "#add";
var REMOVE_PATTERN = "#remove";
var CONTROLS_PATTERN = "#controls";

var addListOfValues = {
    flowExecutionKey: '',
    messages: [],

    initialize: function() {
        this._stepDisplay = $(STEP_DISPLAY_ID);
        this._wizardNav = $(WIZARD_NAV_ID);
        this.initEvents();
    },

    initEvents: function() {

       $(FRAME_ID).observe('click', function(event) {
            var elem = event.element();
            if (matchAny(elem, [ADD_PATTERN])) {
                $("ar").writeAttribute("name", "_eventId_addItem");
                $("extra").submit();
            }

            if (elem.nodeName == "A" && elem.identify() != "add") {
                var id = elem.identify();
                $("itemToDelete").setValue(id);
                $("ar").writeAttribute("name", "_eventId_removeItem");
                $("extra").submit()
            }
        }.bindAsEventListener(this));

        $(FRAME_ID).observe('keyup', function(event) {
            var elem = event.element();

            if (matchAny(elem, [VALUE_ID_PATTERN, LABEL_ID_PATTERN])) {
                this.allowSubmit();
            }

        }.bindAsEventListener(this));
    },

    allowSubmit: function() {
        if (!$("labelID").getValue().blank()) {
            buttonManager.enable("save");
            $("save").writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
        } else {
            buttonManager.disable("save");
            $("save").writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
        if (!$("value").getValue().blank()) {
            buttonManager.enable("add");
            $("add").writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
        } else {
            buttonManager.disable("add");
            $("add").writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
    }
}

if (typeof require === "undefined") {
    document.observe('dom:loaded', function() {
        addListOfValues.initialize();
        addListOfValues.allowSubmit();
    });
}
