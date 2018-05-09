/*
 * Copyright (C) 2015 TIBCO Software Inc. All rights reserved.
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
 * @author: ${username}
 * @version: $Id$
 */

/* Utility functions for properly triggering native browser events.
 */
define(function (require) {
    "use strict";

    var browserDetection=require("common/util/browserDetection");
    var $=require("jquery");

    return {
        mouseEventOptions: {
            bubbles: true,
            cancelable: true,
            view: document.defaultView,
            detail: 0,
            screenX: 0,
            screenY: 0,
            clientX: 0,
            clientY: 0,
            ctrlKey: false,
            altKey: false,
            shiftKey: false,
            metaKey: false,
            button: 0,
            relatedTarget: null,
            srcElement: null    // NOTE: For old IE versions
        },

        /* Fires native events using the best available objects, constructors,
         * and firing mechanisms available in the host browser and version.
         * Normally used for simulating a mouse click with a keystroke.
         */
        triggerNativeEvent: function(eventName, target, options) {
            var ie = browserDetection.isIE(), ev;
            target = target || (ie ? document.documentElement : window);

            // WARNING - MAINTAINING THIS CODE POORLY WILL BREAK RIGHT-CLICK
            // CONTEXT MENUS!  ALWAYS TEST LEFT AND RIGHT CLICK IN ALL
            // SUPPORTED BROWSERS AND VERSIONS AFTER MAINTAINING THIS CODE.
            // THIS HAS COME UP MULTIPLE TIMES!
            if (document.createEvent) { // W3C
                // Firefox is sensitive to mouse events only being triggered
                // with actual mouseEvent objects.
                var opts;
                switch(eventName){
                    case 'click':
                    case 'doubleclick':
                    case 'mousedown':
                    case 'mousemove':
                    case 'mouseout':
                    case 'mouseover':
                    case 'mouseup':
                        opts=$.extend({}, this.mouseEventOptions, options);
                        opts.srcElement=target;
                        //opts.relatedTarget=target;
                        if (typeof MouseEvent==='function') {
                            // Newer non-IE browsers, W3C recommendation
                            ev = new MouseEvent(eventName, opts);
                        } else {
                            // Newer IE versions
                            ev = document.createEvent('MouseEvents');
                            ev.initMouseEvent(eventName,
                                opts.bubbles,
                                opts.cancelable,
                                opts.view,
                                opts.detail,
                                opts.screenX,
                                opts.screenY,
                                opts.clientX,
                                opts.clientY,
                                opts.ctrlKey,
                                opts.altKey,
                                opts.shiftKey,
                                opts.metaKey,
                                opts.button,    // CAUTION
                                opts.relatedTarget);

                        }
                        break;
                    default:
                        opts=$.extend({}, this.eventOptions, options);
                        opts.srcElement=target;
                        ev = document.createEvent('HTMLEvents');
                        ev.initEvent(eventName,
                            opts.bubble,
                            opts.cancelable);
                }
                target.dispatchEvent(ev);
            }
            else { // older IE and certain captive browsers-- note the vast reduction in options
                ev=document.createEventObject();
                ev.srcElement=target;
                // IMPORTANT-- THIS IS REQUIRED TO SIMULATE CLICKING ON LINKS
                // THAT SHOULD OPEN IN NEW WINDOWS IN IE8.  And don't change
                // that type to "function"-- it isn't!
                if ((eventName=="click")&&(typeof target.click!=='undefined')){
                    target.click();
                } else {
                    target.fireEvent("on"+eventName, ev);
                }
            }
        },

        // Fire the FULL event simulation sequence, IN THE CORRECT ORDER.
        // Note that touch events look like "mousedown", not "click".
        simulateClickSequence: function (element) {
            this.triggerNativeEvent('mousedown', element);
            this.triggerNativeEvent('mouseup', element);
            this.triggerNativeEvent('click', element);
        },

        // Fire the FULL event simulation sequence, IN THE CORRECT ORDER.
        // Note that touch events look like "mousedown", not "click".
        simulateDoubleClickSequence: function (element) {
            this.triggerNativeEvent('mousedown', element);
            this.triggerNativeEvent('mouseup', element);
            this.triggerNativeEvent('click', element);
            // NOTE: IE5-8 do not trigger the second mousedown event on a real doubleclick.
            this.triggerNativeEvent('mousedown', element);
            this.triggerNativeEvent('mouseup', element);
            this.triggerNativeEvent('click', element);
            this.triggerNativeEvent('dblclick', element);
        }
    }
});
