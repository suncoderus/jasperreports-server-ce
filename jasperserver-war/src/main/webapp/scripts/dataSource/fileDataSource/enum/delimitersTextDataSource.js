/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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
 * @author: Dima Gorbenko
 * @version: $Id$
 */

define(function () {
	"use strict";

	return [
		{value: "comma", dbValue: ",", label: "resource.datasource.text.fileProperties.delimiter.comma"},
		{value: "semicolon", dbValue: ";", label: "resource.datasource.text.fileProperties.delimiter.semicolon"},
		{value: "space", dbValue: " ", label: "resource.datasource.text.fileProperties.delimiter.space"},
		{value: "tab", dbValue: "\t", label: "resource.datasource.text.fileProperties.delimiter.tab"},
		{value: "newLineWin", dbValue: "\r\n", label: "resource.datasource.text.fileProperties.delimiter.newLineWindows"},
		{value: "newLineUnix", dbValue: "\n", label: "resource.datasource.text.fileProperties.delimiter.newLineUnix"},
		/*{value: "regex", dbValue: "", label: "resource.datasource.text.fileProperties.delimiter.regular"},
		{value: "plugin", dbValue: "", label: "resource.datasource.text.fileProperties.delimiter.plugin"},*/
		{value: "other", dbValue: "", label: "resource.datasource.text.fileProperties.delimiter.other"}
	];

});