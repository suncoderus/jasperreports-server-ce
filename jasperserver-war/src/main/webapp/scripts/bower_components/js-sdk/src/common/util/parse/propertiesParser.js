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
 * @author: Savushchyk P
 * @version: $Id: javaProperties.js 1437 2015-07-22 11:17:03Z psavushc $
 */

define(function (require) {
    "use strict";

    /**
     * the next code in function taken from next repository which is under MIT license:
     *
     * https://github.com/willemdewit/java.properties.js
     *
     * https://github.com/willemdewit/java.properties.js/blob/master/LICENSE
     *
    */

    return function(propertiesContent) {
        var propertyMap = {},
            lines = propertiesContent.split(/\r?\n/),
            currentLine = '',
            matches;


        lines.forEach(function(line) {
            // check if it is a comment line
            if (/^\s*(\#|\!|$)/.test(line)) { // line is whitespace or first non-whitespace character is '#' or '!'
                return;
            }
            line = line.replace(/^\s*/, ''); // remove space at start of line
            currentLine += line;
            if (/(\\\\)*\\$/.test(currentLine)) { // line ends with an odd number of '\' (backslash)
                // line ends with continuation character, remember it and don't process further
                currentLine = currentLine.replace(/\\$/, '');
            } else {
                matches = /^\s*((?:[^\s:=\\]|\\.)+)\s*[:=\s]\s*(.*)$/.exec(currentLine);

                propertyMap[matches[1]] = matches[2];

                currentLine = '';
            }
        });

        return propertyMap;
    };


});