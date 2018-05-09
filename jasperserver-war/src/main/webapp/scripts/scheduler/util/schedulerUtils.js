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
 * @author yaroslav.kovalchyk
 * @version: $Id$
 */

/* global JSCookie, redirectToUrl */

define(function (require) {

	"use strict";

	var config = require('jrs.configs'),
		_ = require("underscore");

    return {
        SCHEDULER_BACK_URL_STORAGE_NAME: "tibco-jrs-scheduler-back-url",

	    getParamsFromUri: function() {
		    var params = {};

		    // first, let's parse normal query parameters

		    var parts = document.location.search.substr(1).split("&");
		    _.each(parts, function(part) {
			    var tmp = part.split("="), key = tmp[0], value = tmp[1];
			    if (value === "") {
				    return;
			    }
			    params[key] = decodeURIComponent(value);
		    });

		    /// then, let's parse hash
		    var hash = document.location.hash.substr(1);

		    // get rid of "runInBackground@" mark in document.location.hash
		    if (hash.indexOf("runInBackground@") === 0) {
			    hash = hash.replace("runInBackground@", "");
		    }

		    var hashParts = hash.split("@@");

		    params["reportUri"] = hashParts.shift(); // get first element from array
		    _.each(hashParts, function(part) {
			    var tmp = part.split("="), key = tmp[0], value = tmp[1];
			    if (value === "") {
				    return;
			    }
			    params[key] = decodeURIComponent(value);
		    });

		    return params;
	    },

	    saveCurrentLocation: function() {
		    if (document.referrer.indexOf("login.html") === -1) {
			    window.localStorage && localStorage.setItem(this.SCHEDULER_BACK_URL_STORAGE_NAME, encodeURIComponent(document.referrer));
		    }
	    },

	    getBackToPreviousLocation: function() {
			var currentUrl = encodeURIComponent(document.location.href);

            var lastUrl = window.localStorage ? localStorage.getItem(this.SCHEDULER_BACK_URL_STORAGE_NAME) : undefined;
			if (lastUrl && lastUrl !== currentUrl) {
                var url = decodeURIComponent(lastUrl);
                if (url) {
                    redirectToUrl(url);
                    return;
                }
            }
            // in bad scenario, we need to get to the standard URL
			redirectToUrl(config.contextPath + "/flow.html?_flowId=searchFlow");
        }
    };
});