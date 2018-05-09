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
        define(["common/logging/Level", "common/logging/LogItem"], factory);
    } else {
        global.logging || (global.logging = {});
        global.logging.Log = factory(global.logging.Level, global.logging.LogItem);
    }
}(function(Level, LogItem) {

    function createLogMethod(type) {
        return function() {
            return this._prepareLogItem({
                level: Level.getLevel(type),
                args: arguments
            });
        }
    }

    function Log(settings, callback) {
        this._id = settings.id;
        this._callback = callback;
    }

    Log.prototype._prepareLogItem = function(logItem) {
        logItem.id = this._id;
        logItem.args = Array.prototype.slice.call(logItem.args, 0);
        logItem.time = new Date();

        // TODO cross browser support
        var stack = new Error().stack;
        if (stack) {
            var lineAccessingLogger = stack.split("\n")[2];
            var res = lineAccessingLogger.match(/\/(\w+\.\w+):(\d+)/i);
            if (res) {
                logItem.file = res[1];
                logItem.line = res[2];
            }
        }
        if (!logItem.file) {
            logItem.file = "unknown";
            logItem.line = "0";
        }

        logItem = new LogItem(logItem);

        this._callback(logItem);
        return logItem;
    };

    Log.prototype.debug = createLogMethod("debug");
    Log.prototype.info = createLogMethod("info");
    Log.prototype.warn = createLogMethod("warn");
    Log.prototype.error = createLogMethod("error");

    return Log;
}, this));