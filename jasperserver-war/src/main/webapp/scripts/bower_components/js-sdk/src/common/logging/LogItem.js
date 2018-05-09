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
 * @author: Pavel Savushchyk
 * @version: $Id$
 */
(function (factory, global) {
    if (typeof define === "function" && define.amd) {
        define([], factory);
    } else {
        global.logging || (global.logging = {});
        global.logging.LogItem = factory();
    }
}(function () {
    "use strict";
    
    function formatTime(date) {
        var timeString,
            h = date.getHours().toString(),
            m = date.getMinutes().toString(),
            s = date.getSeconds().toString(),
            ms = date.getMilliseconds();

        if (h.length == 1) {
            h = "0" + h;
        }
        if (m.length == 1) {
            m = "0" + m;
        }
        if (s.length == 1) {
            s = "0" + s;
        }

        timeString = h + ":" + m + ":" + s + "." + ms;

        return timeString;
    }

    function LogItem(options) {
        for (var i in options) {
            if (options.hasOwnProperty(i)) {
                if (i === "args") {
                    for (var k = 0, l = options[i].length; k < l; k++) {
                        if (options[i][k] instanceof Error) {
                            options[i][k] = options[i][k].message;
                        }
                    }
                }
                this[i] = options[i];
            }
        }
    }

    LogItem.prototype.toArray = function() {
        var logParams = [];
        logParams.push(formatTime(this.time));
        logParams.push("[" + this.id + "]");
        if (this.file !== "unknown"){
            logParams.push("[" + this.file + ":" + this.line + "]");
        }
        logParams.push("[" + this.level.toString() + "] -");
        logParams = logParams.concat(this.args);

        return logParams;
    };

    return LogItem;
}, this));