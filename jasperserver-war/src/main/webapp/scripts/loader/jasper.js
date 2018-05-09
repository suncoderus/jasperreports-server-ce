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
 * @author: Igor Nesterenko
 * @version $Id$
 */

/*global jasperContext, window, console, jasperjs, require, requirejs */

(function(global){

    "use strict";

    var errors = {
            BASE_URL_NOT_FOUND : "Illegal 'conf' argument, it should be url to server or contains 'url' property",
            ILLEGAL_CALLBACK : "Illegal 'callback' argument, it should be function or undefined"
        },
        defaultCallback = function(err){
            if (err){
                throw err;
            }
        },
        getOriginFromUrl = function(url) {
            var a = document.createElement('a');
            a.href = url;

            return a.origin || (a.protocol + "//" + a.host);
        },
        LoadManager = function(jasperCallback, jasperError){
            function execute() {
                jasperCallback && jasperCallback.call(this, jasperError, this.require);
                for (var i = 0; i < this._callbacks.length; i++) {
                    this._callbacks[i](this.require);
                }
            }

            this._callbacks = [];

            this.then = function(handler){
                if (this.require){
                    handler(this.require);
                } else {
                    this._callbacks.push(handler);
                }
            };

            this.resolveSettings  = function(settings){
                this.settings = settings;
                this.require && execute.call(this);
            };

            this.resolveConfig = function(req){
                this.require = req;
                this.settings && execute.call(this);
            }
        };

    function jasper(conf, callback){

        var error, url, root, loggerEnabled, logLevel, scripts, loaderPromise, result;

        if (conf){
            if (typeof conf === "object"){
                url = conf.url;
                loggerEnabled = conf.logEnabled;
                logLevel = conf.logLevel;
                scripts = conf.scripts;

                require.config({
                    config: {
                        logger : {
                            enabled : conf.logEnabled,
                            level : conf.logLevel,
                            appenders: ["console"]
                        },
                        theme: conf.theme
                    }
                });
            } else if (typeof conf === "string"){
                url = conf;
            }
        }

        if (!url){
            error =  new Error(errors.BASE_URL_NOT_FOUND);
        }

        if (callback){
            if (typeof callback !== "function"){
                error = new Error(errors.ILLEGAL_CALLBACK);
                callback = null;
            }
        }

        if (!callback){
            callback = defaultCallback;
        }

        if (error){
            callback.apply(this, [error]);
            result = function(modules){
                throw new Error("Can't load " + modules);
            };
        }else{
            var isSameOrigin;

            try {
                isSameOrigin = (getOriginFromUrl(window.location.href) === getOriginFromUrl(url));
            } catch(ex) {
                isSameOrigin = false;
            }

            loaderPromise = new LoadManager(callback, error);

            require(["loader/core/Root", "logger", "xdm"], function(Root, logger, xdm){
                // expose easyXDM variable to global scope only when we are on the same domain
                if (isSameOrigin) {
                    window.easyXDM = xdm;
                }

                root = new Root(url, loggerEnabled, logLevel, scripts);
                var log =  logger.register("Jasper");

                root.settings().done(function(settings) {
                    log.debug("Server settings: ", settings);
                    loaderPromise.resolveSettings(settings);
                });

                root.requirejs().done(function(reqConfig) {
                    //apply require config
                    var req = requirejs.config(reqConfig);
                    loaderPromise.resolveConfig(req);
                });

            }, callback);

            result = function (modules, handler){
                loaderPromise.then(function(req){
                    req.apply(this, [modules, handler, callback]);
                });
            };

        }

        return result;
    }

    // noConflict functionality
    var _jasper = global.jasper,
        _jasperjs = global.jasperjs;

    jasper.noConflict = function (deep) {
        if (global.jasper === jasper) {
            global.jasper = _jasper;
        }

        if (deep && global.jasperjs === jasperjs) {
            global.jasperjs = _jasperjs;
        }

        return jasper;
    };

    // Added jasper to global scope
    global.jasper = jasper;
    global.jasperjs = jasper;

    jasper._errors_ = errors;

    jasper.version = "0.0.1a";
})(this);
