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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var scrollbarWidth = 0;

    return {
        getScrollbarWidth: function() {
            if (scrollbarWidth) {
                return scrollbarWidth;
            }

            // Create the measurement node
            var scrollDiv = document.createElement("div");
            scrollDiv.style.width = "100px";
            scrollDiv.style.height = "100px";
            scrollDiv.style.overflow = "scroll";
            scrollDiv.style.position = "absolute";
            scrollDiv.style.top = "-9999px";

            document.body.appendChild(scrollDiv);

            scrollbarWidth = scrollDiv.offsetWidth - scrollDiv.clientWidth;

            document.body.removeChild(scrollDiv);

            return scrollbarWidth;
        },

        hasScrollBar: function(el, direction) {

            direction = (direction === 'vertical') ? 'scrollTop' : 'scrollLeft';
            var result = !! el[direction];

            if (!result) {
                el[direction] = 1;
                result = !!el[direction];
                el[direction] = 0;
            }
            return result;
        },

        isScrollable: function(el) {
            // TODO use polyfill
            var style = window.getComputedStyle ? window.getComputedStyle(el) : el.currentStyle;

            if (!style) {
                return;
            }

            return style.overflow == 'scroll'
                || style.overflow == 'auto'
                || style.overflowX == 'scroll'
                || style.overflowX == 'auto'
                || style.overflowY == 'scroll'
                || style.overflowY == 'auto';
        },

        getElementOffset: function (el) {
            var left = 0,
                top = 0;

            if (el.offsetParent) {
                left = el.offsetLeft;
                top  = el.offsetTop;

                while (el = el.offsetParent) { // jshint ignore: line
                    left += el.offsetLeft;
                    top  += el.offsetTop;
                }
            }

            return { left : left,  top : top };
        }
    }
});