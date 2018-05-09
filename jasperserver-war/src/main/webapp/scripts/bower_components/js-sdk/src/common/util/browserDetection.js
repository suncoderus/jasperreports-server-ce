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

define(function (require) {
	"use strict";

	var $ = require("jquery");

	return {
		isMozilla: function () {
			return navigator.appName === "Netscape";
		},
		isFirefox: function () {
			return (navigator.userAgent.toLowerCase().indexOf("firefox") !== -1);
		},
		isWebKitEngine: function () {
			return ($.browser && $.browser.webkit);
		},
		isChrome: function () {
			return navigator.userAgent.toLowerCase().indexOf('chrome');
		},
		isIPad: function () {
			return navigator.platform == "iPad";
		},
        isIOS: function() {
            return navigator.userAgent.match(/(iPad|iPhone|iPod)/g);
        },
		isAndroid: function () {
			return navigator.userAgent.toLowerCase().indexOf('android');
		},
		isIE: function () {
			return navigator.appName === "Microsoft Internet Explorer" || navigator.userAgent.indexOf("Trident/") >= 0;
		},
		isIE6: function () {
			return this.isIEVersion(6);
		},
		isIE7: function () {
			return this.isIEVersion(7);
		},
		isIE8: function () {
			return this.isIEVersion(8);
		},
		isIE9: function () {
			return this.isIEVersion(9);
		},
		isIE10: function () {
			return this.isIEVersion(10);
		},
		isIE11: function () {
			return this.isIEVersion(11);
		},
		isIEVersion7Upwards: function () {
			return this.getIEVersion() >= 7;
		},
		isIEVersion: function (testVersion) {
			return this.getIEVersion() === testVersion;
		},
		getIEVersion: function () {
			var version = 0; // not IE
			if (this.getIEVersion.version >= 0) {
				return this.getIEVersion.version;
			}

			if (this.isIE()) {
				if (navigator.appName === "Netscape") {
					var ua = navigator.userAgent;
					var re = new RegExp("Trident/.*rv:([0-9]{1,}[\\.0-9]{0,})");
					if (re.exec(ua) != null) {
						version = parseFloat(RegExp.$1);
					}
				} else {
					var msVersion = navigator.appVersion.split("MSIE")[1];
					version = parseFloat(msVersion);
				}
			}

			this.getIEVersion.version = version;
			return version;
		}
	}
});
