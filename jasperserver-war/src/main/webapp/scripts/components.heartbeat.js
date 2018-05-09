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

/* global layoutModule, ajaxNonReturningUpdate, baseErrorHandler, dialogs, appendPostData */

var heartbeat = {
    init: false,
    DOM_ID: "heartbeatOptin",
    PERMIT_CHECKBOX_ID: "heartbeatCheck",

    initialize: function(options) {
        this._baseUrl = options.baseUrl;
        this._showDialog = options.showDialog;
        this._sendClientInfo = options.sendClientInfo;

        this._dom = $(this.DOM_ID);
        if (!this._dom) {
            return;
        }
        this._permit = $(this.PERMIT_CHECKBOX_ID);
        this._okButton = this._dom.select(layoutModule.BUTTON_PATTERN)[0];

        this._okButton.observe("click", this._okHandler.bind(this));
        this.init = true;
    },

    _okHandler: function() {
        dialogs.popup.hide(this._dom);

        var url = this._baseUrl +"/heartbeat.html?permit=" + this._permit.checked;

        ajaxNonReturningUpdate(url, { errorHandler: baseErrorHandler });
    },

    start: function() {
        if (this.init === false) {
            return;
        }

        if (this._showDialog) {
            dialogs.popup.show(this._dom);

            this._okButton.focus();
        }
        if (this._sendClientInfo) {
            this._doSendClientInfo();
        }
    },

    _doSendClientInfo: function() {
        try {
            var params = {};
            params["navAppName"] = navigator.appName;
            params["navAppVersion"] = navigator.appVersion;
            params["scrWidth"] = screen.width;
            params["scrHeight"] = screen.height;
            params["scrColorDepth"] = screen.colorDepth;
            var postData = appendPostData("", params);

            var url = this._baseUrl + "/heartbeatInfo.html";

            ajaxNonReturningUpdate(url, {postData: postData});
        } catch(e) {
            // Ignore.
        }
    }
};