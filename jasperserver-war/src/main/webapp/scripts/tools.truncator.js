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
 * @author: stas.chubar
 * @version: $Id$
 */

/* global Class, isIE, Template */

/*
 * Truncator is based on Abbreviator jQuery Plugin
 *
 * Abbreviator Homepage: http://bentlegen.github.com/abbreviator
 * Abbreviator is hosted on GitHub: http://github.com/bentlegen/abbreviator/tree/master
 * Abbreviator Author: Ben Vinegar (http://www.benlog.org)
 * Abbreviator is distributed under the MIT License: http://www.opensource.org/licenses/mit-license.php
 */

/**
 * Truncator Prototype Plugin (Prototype >= 1.6.0.3)
 *
 * Truncator can be applied to elements only with text
 * Usage:
 *      new Truncator($$('selector')); - truncate array of elements
 *      new Truncator().truncate($('id')); - truncate one element
 *      new Truncator().truncate($$('selector'),100); - truncate array to 100px  (Angus added)
 *      new Truncator().untruncate($$('selector')); - untruncate array of elements
 */
var Truncator = Class.create({
    initialize: function(elements, fixedWidth) {
        if (!elements || !elements[0]) return;
        var columnWidth= null;
        if (fixedWidth) {
             columnWidth = fixedWidth;
        } else {
            /*
              Note: commenting this out cause it is causing an error.
              getMarginLeft, getMarginRight, getPaddingLeft and getPaddingRight where part of the old search layout..
              will send an email out on this...
             */
//            var parent = $(elements[0].parentNode);
//            var columnWidth = parent.getWidth() - parent.getMarginLeft() - parent.getMarginRight() - parent.getPaddingLeft() - parent.getPaddingRight();
//            var columnWidth = parent.getWidth() - getBufferWidth(parent, true);
        }

        elements.each(function(element) {
            this.truncate(element, columnWidth);
        }.bind(this));
    },

    truncate: function(element, columnWidth) {
        if(element.nodeName == 'ABBR') {
            var fullContent = element.title.replace("&quot;", "\"");
            element = element.parentNode;
            element.update(fullContent.strip().escapeHTML());
        }

        //var content = element.innerHTML;
        var content = Object.isUndefined(element.innerText) ? element.textContent : element.innerText;
        element.insert(Truncator.template.evaluate({
            divId: Truncator.tmpDivId,
            spanId: Truncator.tmpSpanId
        }));

        var contentTmp = $(Truncator.tmpSpanId);
        contentTmp.insert(content.escapeHTML());

        var containerWidth = columnWidth ? columnWidth : element.getWidth();
        var contentWidth = contentTmp.getWidth();

        if (contentWidth <= containerWidth) {
          $(Truncator.tmpDivId).remove();
          return;
        }

//        var coverage = containerWidth / contentWidth;
//        var l = content.length;
//
//        var truncatedContent = content.substr(0, parseInt(l * coverage));
//
//        while (contentWidth >= containerWidth) {
//            if (isIE()) {
//                contentTmp.innerText = this.ellipsifyString(truncatedContent);
//            } else {
//                contentTmp.update(this.ellipsifyString(truncatedContent));
//            }
//            contentWidth = (contentTmp.getWidth() < 0) ? 0 : contentTmp.getWidth();
//
//            truncatedContent = truncatedContent.substring(0, truncatedContent.length - 1);
//
//            if (truncatedContent.length == 0) {
//                break;
//            }
//        }

        var truncatedContent = content;
        var a = 0;
        var b = content.length;
        var p = (b - a) / 2;
        while (p > 1) {
            p += a;
            truncatedContent = content.substr(0, p);
            if (isIE()) {
                contentTmp.innerText = truncatedContent;
            } else {
                contentTmp.update(truncatedContent.escapeHTML());
            }
            if (contentTmp.getWidth() + 30 > containerWidth) {
                b = p;
            } else {
                a = p;
            }
            p = (b - a) / 2;
        }

        $(Truncator.tmpDivId).remove();

        element.update(this.truncateString(this.ellipsifyString(truncatedContent.escapeHTML()), content.escapeHTML().replace("\"", "&quot;")));
    },

    untruncate: function(elements) {
        elements.each(function (element) {
            if(element.nodeName == 'ABBR') {
                element.update(element.title.strip().escapeHTML());
            }
        });
    },

    ellipsifyString: function(s) {
        return s + '&hellip;';
    },

    truncateString: function(s, full) {
        return '<abbr title="' + full + '">' + s + '</abbr>';
    }
});

Truncator.tmpDivId = 'truncator-tmp-div';
Truncator.tmpSpanId = 'truncator-tmp-span';
/*jshint multistr: true */
Truncator.template = new Template('\
    <div id="#{divId}" style="width:9999px; left:-9999px; top:-9999px; display:block; position: absolute">\
        <span id="#{spanId}"></span>\
    </div>');
