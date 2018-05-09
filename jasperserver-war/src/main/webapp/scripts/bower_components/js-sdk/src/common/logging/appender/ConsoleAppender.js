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

/*global console */

(function (factory, global) {
    if (typeof define === "function" && define.amd) {
        define([], factory);
    } else {
        global.logging || (global.logging = {});
        global.logging.appender || (global.logging.appender = {});
        global.logging.appender.ConsoleAppender = factory();
    }
}(function () {
        function ConsoleAppender() {}

        ConsoleAppender.prototype.console = (function() {
            if (typeof console === "undefined") {
                var f = function() {};

                return {
                    assert: f,
                    clear: f,
                    count: f,
                    debug: f,
                    dir: f,
                    dirxml: f,
                    error: f,
                    group: f,
                    groupCollapsed: f,
                    groupEnd: f,
                    info: f,
                    log: f,
                    markTimeline: f,
                    profile: f,
                    profileEnd: f,
                    table: f,
                    time: f,
                    timeEnd: f,
                    timeStamp: f,
                    trace: f,
                    warn: f
                }
            } else {
                return console;
            }
        })();

        ConsoleAppender.prototype.write = function(logItem) {

            var f = this.console.log;

            switch (logItem.level.toString()) {
                case "DEBUG":
                    f = this.console.debug || this.console.log;
                    break;
                case "INFO":
                    f = this.console.info || this.console.log;
                    break;
                case "WARN":
                    f = this.console.warn;
                    break;
                case "ERROR":
                    f = this.console.error;
                    break;
            }

            try
            {
                f.apply(this.console, logItem.toArray());
            }
            catch (e)
            {
                try {
                    Function.prototype.apply.call(f, this.console, logItem.toArray());
                } catch(ex) {}
            }
        };

        return ConsoleAppender;
}, this));