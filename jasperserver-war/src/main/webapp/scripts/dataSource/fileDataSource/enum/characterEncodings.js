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
        {value: "ascii", dbValue: "ascii", label: "resource.datasource.text.fileProperties.encoding.ascii"},
        {value: "utf8", dbValue: "utf8", label: "resource.datasource.text.fileProperties.encoding.utf8"},
        {value: "utf16", dbValue: "utf16", label: "resource.datasource.text.fileProperties.encoding.utf16"},
        {value: "cp1251", dbValue: "cp1251", label: "resource.datasource.text.fileProperties.encoding.cp1251"},
        {value: "koi8r", dbValue: "koi8r", label: "resource.datasource.text.fileProperties.encoding.koi8r"}
    ];

});