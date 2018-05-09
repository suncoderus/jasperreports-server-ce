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
 * @author: afomin, inesterenko
 * @version: $Id$
 */

/* global JRS, ajax, console, _, Mustache, dialogs */

JRS.Controls = (function(jQuery, _, Mustache, dialogs){

    //module:
    //
    //  controls.core
    //
    //summary:
    //
    //  Define base objects types to work with input controls
    //
    //main types:
    //
    // Base             - provide inheritance,
    // TemplateEngine   - load templates, can get template's sections
    // Utils            - provide common functions
    //
    //dependencies:
    //
    //  jQuery          - v1.7.1
    //  _,              - underscore.js 1.3.1
    //  Mustache       - template engine
    //  dialogs        - components.dialogs module
    var isIE = navigator.userAgent.toLowerCase().indexOf("msie") > -1;


    return {

        Base:_.extend(function () {}, {

            // Helper function to correctly set up the prototype chain, for subclasses.
            // Similar to `goog.inherits`, but uses a hash of prototype properties and
            // class properties to be extended.
            extend : function(protoProps, staticProps) {
                var parent = this;
                var child;

                // The constructor function for the new subclass is either defined by you
                // (the "constructor" property in your `extend` definition), or defaulted
                // by us to simply call the parent's constructor.
                if (protoProps && _.has(protoProps, 'constructor')) {
                    child = protoProps.constructor;
                } else {
                    child = function(){ return parent.apply(this, arguments); };
                }

                // Add static properties to the constructor function, if supplied.
                _.extend(child, parent, staticProps);

                // Set the prototype chain to inherit from `parent`, without calling
                // `parent`'s constructor function.
                var Surrogate = function(){ this.constructor = child; };
                Surrogate.prototype = parent.prototype;
                child.prototype = new Surrogate();

                // Add prototype properties (instance properties) to the subclass,
                // if supplied.
                if (protoProps) _.extend(child.prototype, protoProps);

                // Set a convenience property in case the parent's prototype is needed
                // later.
                child.__super__ = parent.prototype;

                return child;
            }
        }),

        // Provide functionality to get templates
        TemplateEngine:{

            // Populate template with data
            render:function (templateText, model, type) {
                if (!type){
                    //Mustache by default
                    return Mustache.to_html(templateText, model);
                }else if(type == this.STD_PLACEHOLDERS){
                    var result = String(templateText);
                    _.each(model,  function(val, index){
                        var regExp = new RegExp("\\{"+index+"\\}");
                        result = result.replace(regExp, val);
                    });
                    return result;
                }
            },
            renderUrl:function(templateText, model, encoded){
              var url = Mustache.to_html(templateText, model);
                if(isIE && !encoded){
                    url = encodeURI(url);
                }
                return url;
            },
            // Return template's text
            getTemplateText:function (templateId) {
                var scriptTag = jQuery("#" + templateId);
                return scriptTag.html();
            },

            // Return template's function for given id
            createTemplate:function (templateId) {
                var scriptTag = jQuery("#" + templateId);
                var templateText = scriptTag.html();

                if (templateText && templateText.length > 0) {
                    return function (model) {
                        return Mustache.to_html(templateText, model);
                    };
                }
            },

            // Cut template's text chunk and wrap with a function
            createTemplateSection:function (section, templateId) {
                var regexpTemplate = '\\{\\{#val\\}\\}(\\s|\\S)*\\{\\{/val\\}\\}';
                var concreteSectionRegexpPattern = regexpTemplate.replace(/val/g, section);
                var regexp = new RegExp(concreteSectionRegexpPattern, "g");
                var templateText = this.getTemplateText(templateId);
                var templateSectionText = templateText.match(regexp)[0];
                return  function (model) {
                    return Mustache.to_html(templateSectionText, model);
                };
            },

            STD_PLACEHOLDERS : "std_placeholder"
        },

        Utils:{

            LOADING_DIALOG_DELAY : 800,
            INPUT_CONTROLS_COMPONENT_ID : "input-controls",

            //check presents of element in DOM
            isElementInDom:function (elem) {
                var nextSibling = elem.nextSibling;
                var parent = elem.parentNode && elem.parentNode.nodeType  !== 11;
                return nextSibling || parent;

            },

            // Optimized inserting of big content to DOM
            setInnerHtml:function (el, template, data) {

                var nextSibling, parent, display;

                if (this.isElementInDom(el)) {
                    nextSibling = el.nextSibling;
                    parent = el.parentNode;
                    display = el.style.display;
                    //turn off css reflows on it element during update
                    el.style.display = 'none';
                    //remove from the dom, it also reduce reflows on this element
                    parent.removeChild(el);
                }

                el.innerHTML = "";

                if (isIE && (el.tagName == "SELECT")) {
                    //workaround for bug in IE, select element and innerHTML functionality
                    //TODO: rewrite it, because it hardcoded for one special case it doesn't use template
                    var fragment = document.createDocumentFragment();
                    _.each(data.data, function (data) {
                        var option = document.createElement('OPTION');
                        //hardcoded workaround for report options
                        option.value = !_.isUndefined(data.value) ? data.value : data.id;
                        option.innerHTML = data.label;
                        if (data.selected) {
                            option.setAttribute('selected', 'selected');
                        }
                        fragment.appendChild(option);
                    });
                    el.appendChild(fragment);
                } else {
                    el.innerHTML = template(data);
                }

                if (nextSibling) {
                    parent.insertBefore(el, nextSibling);
                } else {
                    parent.appendChild(el);
                }

                el.style.display = display;

                if (isIE && (el.tagName == "SELECT")){
                    //workaround for IE8,9, where width of 'select' not updates, before user click on.
                    //reset style to force browser's reflows for 'select' element
                    var styleContent = el.getAttribute("style");
                    el.removeAttribute("style");
                    el.setAttribute("style", styleContent);
                }

            },

            wait:function (delay) {
                return jQuery.Deferred(function (dfr) {
                    setTimeout(dfr.resolve, delay);
                });
            },

            showLoadingDialogOn:function (deferred, delay, modal) {
                this.wait(delay ? delay : this.LOADING_DIALOG_DELAY).then(_.bind(function () {
                    if (deferred.state() == "pending") {
                        //Do not focus on loading dialog
                        dialogs.popup.showShared($(ajax.LOADING_ID), modal, {focus: false, owner: this.INPUT_CONTROLS_COMPONENT_ID});

                        jQuery.when(deferred).always(_.bind(function () {
                            var ownerId = this.INPUT_CONTROLS_COMPONENT_ID;
                            //don't close loading dialog very fast it irritates user
                            this.wait(500).then(function () {
                                if(window.viewer && window.viewer.loading) {
                                    // wait until componentsRegistered event in viewer.js
                                    window.viewer.loaded && dialogs.popup.hideShared($(ajax.LOADING_ID), ownerId);
                                } else {
                                    dialogs.popup.hideShared($(ajax.LOADING_ID), ownerId);
                                }
                            });
                        },this));
                    }
                }, this));
            },

            createTimer : function(message){
                var timer = new jQuery.Deferred();
                timer.done(function(startTime){
                    var endTime = (new Date()).getTime();
                    var diff = endTime - startTime;
                    console.log(message+ " took time: "+ diff + " msec.");
                });
                return {
                    start : function(){
                        this.startTime = (new Date()).getTime();
                        return this;
                    },

                    stop : function(){
                        timer.resolve(this.startTime);
                        return this;
                    }
                };
            }

        },

        listen:function (listeners, context) {
            _.each(listeners, function (eventHandler, eventName) {
                jQuery(document).bind(eventName, _.bind(eventHandler, this));
            }, !context ? this : context);
        },

        ignore:function(eventName, handler){
            jQuery(document).unbind(eventName, handler);
        }
    };

})(
    jQuery,
    _,
    Mustache,
    dialogs
);

