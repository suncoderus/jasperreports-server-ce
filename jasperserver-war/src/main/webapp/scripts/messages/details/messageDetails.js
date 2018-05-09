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

/* global toolbarButtonModule, layoutModule, toolbarButtonModule, matchAny, localContext */

var messageDetailModule = {
    _flowExecutionKey: null,
    _message: null,

    toolbar: {
        _buttons: null,
        _id: "toolbar",

        initialize: function() {
            toolbarButtonModule.initialize({});
            this._buttons = document.body.select(layoutModule.TOOLBAR_CAPSULE_PATTERN);
            this._initEventHandlers();
        },

        refresh: function() {
            this._buttons.each(function(button) {
                toolbarButtonModule.setButtonState(button, true);
            }.bind(this));
        },

        _initEventHandlers: function() {
            $(this._id).observe('click', function(e) {
                var button = matchAny(e.element(), [layoutModule.BUTTON_PATTERN], true);
                document.location = 'flow.html?_flowExecutionKey=' + messageDetailModule._flowExecutionKey + '&_eventId=' + button.identify();
            }.bindAsEventListener(this));
        }
    },

    initialize: function(options) {
        this._flowExecutionKey = options.flowExecutionKey;
        this._message = options.message;

        this._process();

        this.toolbar.initialize();
    },

    _process: function() {
        $('subject').update(xssUtil.escape(this._message.subject));
        $('date').update(xssUtil.escape(this._message.date));
        $('component').update(xssUtil.escape(this._message.component));
        $('message').update(xssUtil.escape(this._message.message));
    }
};

if (typeof require === "undefined") {
    document.observe('dom:loaded', function() {
        messageDetailModule.initialize(localContext.initOptions);
    });
}
