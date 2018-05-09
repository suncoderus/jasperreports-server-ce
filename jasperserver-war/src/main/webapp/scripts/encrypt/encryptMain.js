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
 * @author: Dima Gorbenko <dgorbenko@jaspersoft.com>
 * @version: $Id: encrypt.page.js 8790 2015-04-22 21:28:09Z obobruyk $
 */

/* global isEncryptionOn */

define(function (require) {

	"use strict";

	require("commons.main");

	var $ = require("jquery"),
		_ = require("underscore"),
		domReady = require("domReady"),
		JSEncrypter = require("common/util/encrypter");

	domReady(function(){

		var self = this, $text1 = $("#text1"), $text2 = $("#text2");

		var submitEncrypt = function(event) {

			if (! $.trim($text1.val()) ) {
				return;
			}

			if (isEncryptionOn) {
				JSEncrypter.encryptData({j_password: $text1.val()},
					function(encData) {
						for (var k in encData) {
							$text2.removeAttr('disabled').val(encData[k]);
						}
					});
			}

			event.preventDefault();
		};

		var doClear = function(event) {
			$text1.val('');
			$text2.val('');
			event.preventDefault();
		};

		// event handling
		$('#clearButton').click(doClear);
		$('#submitButton').click(submitEncrypt);
		$text1.keypress(function(event) {
			if ((event.keyCode || event.which) != 13) {
				return;
			}
			_.defer(submitEncrypt, event);
		});

	});
});