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
 * @author afomin, inesterenko
 * @version: $Id$
 */

/* global jaspersoft, console, _, Mustache, dialogs, ajax */

jaspersoft.components.utils = (function(jQuery, _, Mustache, dialogs, ajax){
    var isIE = navigator.userAgent.toLowerCase().indexOf("msie") > -1;

    return {

            LOADING_DIALOG_DELAY : 800,

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
                        dialogs.popup.show($(ajax.LOADING_ID), modal);
                        jQuery.when(deferred).always(_.bind(function () {
                            //don't close loading dialog very fast it irritates user
                            this.wait(500).then(function () {
                                dialogs.popup.hide($(ajax.LOADING_ID))
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
    }
})(
    jQuery,
    _,
    Mustache,
    dialogs,
    ajax
);

