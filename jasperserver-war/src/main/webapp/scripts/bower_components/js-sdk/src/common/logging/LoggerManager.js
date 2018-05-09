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


(function (factory, global, _) {
    if (typeof define === "function" && define.amd) {
        define(["underscore", "common/logging/Log", "common/logging/Level", "common/logging/appender/ConsoleAppender"], factory);
    } else {
        global.logging || (global.logging = {});
        global.logging.LoggerManager = factory(_, global.logging.Log, global.logging.Level, global.logging.appender.ConsoleAppender);
    }
}(function (_, Log, Level, ConsoleAppender) {

    var appenderConstructors = {
            console: ConsoleAppender
        };

    var LoggerManager = function(options) {
        this.initialize(options || {});
    };

    _.extend(LoggerManager.prototype, {

        defaults : function() {
            return {
                enabled: false,
                level: "error",
                appenders: {},
                appenderInstances: {},
                loggers: {}
            }
        },

        initialize: function(options) {
            this.attributes = _.defaults(options, this.defaults());

            // initialize appenders
            var appenders = {};
            _.each(appenderConstructors, function(appender, name){
                appenders[name] = new appender();
            });
            this.set("appenderInstances", appenders);

        },

        get: function(attr) {
            return this.attributes[attr];
        },

        set: function(attr, value) {
            this.attributes[attr] = value;
        },

        register: function(options) {
            var settings = {
                id: "root"
            };

            if (typeof options === "string" && options !== "") {
                settings.id = options;
            } else if (options && options.hasOwnProperty("id")) {
                settings.id = options.id;
            }

            if (!this.get("loggers").hasOwnProperty(settings.id)) {
                var loggers = this.get("loggers");
                loggers[settings.id] = new Log(settings, _.bind(this._processLogItem, this));
                this.set("loggers", loggers);
            }

            return this.get("loggers")[settings.id];
        },
        disable: function() {
            this.set("enabled", false);
        },
        enable: function(level) {
            if (level) {
                this.set("level", Level.getLevel(level));
            }
            this.set("enabled", true);
        },
        setLevel: function(level) {
            this.set("level", level);
        },

        _processLogItem: function(logItem) {
            if (this.get("enabled") && logItem.level.isGreaterOrEqual(this.get("level"))) {
                this._appendLogItem(logItem);
            }
        },
        _appendLogItem: function(logItem) {
            var appenders = this.get("appenders"),
                appenderInstances = this.get("appenderInstances");
            for (var i in appenders) {
                if (appenderInstances.hasOwnProperty(appenders[i])) {
                    appenderInstances[appenders[i]].write(logItem);
                }
            }
        }
    });

    return LoggerManager;
}, this, this._));