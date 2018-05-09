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
 * @version: $Id: historyHelper.js 812 2015-01-27 11:01:30Z psavushchik $
 */

/*global JSCookie, redirectToUrl */

define(function (require) {

	"use strict";

	require("utils.common");
	var jrsConfigs = require("jrs.configs");

	return {

		saveCurrent: function(token) {
			new JSCookie(token, encodeURIComponent(document.location.href));
		},

		saveReferrer: function(token) {
			if (document.referrer.indexOf("login.html") === -1) {
				new JSCookie(token, encodeURIComponent(document.referrer));
			}
		},

		restore: function(token, defaultUrl) {

			var currentUrl = encodeURIComponent(document.location.href);
			var tokenUrl = new JSCookie(token).value;

			if (tokenUrl && tokenUrl !== currentUrl) {
				var url = decodeURIComponent(tokenUrl);
				if (url) {
					redirectToUrl(url);
					return;
				}
			}

			// in worse scenario, we need to get to the starting point
			defaultUrl = defaultUrl || "/";

			redirectToUrl(jrsConfigs.contextPath + defaultUrl);
		}

	};
});