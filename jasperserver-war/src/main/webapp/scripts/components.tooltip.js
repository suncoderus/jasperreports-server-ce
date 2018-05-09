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
 * @version: $Id$
 */

/* global isArray, _, getBoxOffsets, getScrollLeft, getScrollTop, layoutModule
*/

/**
 * Usage:
 *      Within HTML markup. (Not working with button element)
 *
 *      <code>
 *      &lt;li tooltiptext="Hello"&gt;Item 1&lt;/li&gt;
 *      &lt;li tooltiptext="Hello" tooltiplabel="URA!"&gt;Item 1&lt;/li&gt;
 *      &lt;li tooltiptext="Hello" tooltiplabel="URA!" tooltiptemplate="tID"&gt;Item 1&lt;/li&gt;
 *      </code>
 *
 *      Within JavaScript
 *      <code>
 *      new JSTooltip(element, { text: "tooltipText" }).show();
 *      new JSTooltip(element, { text: "tooltipText", offsets: [100, 50] }).show();
 *      new JSTooltip(element, { text: "tooltipText" }).show([100, 50]);
  *     new JSTooltip(element, { label: "tooltipLabel", text: "tooltipText", templateId: "templateId" });
 *      new JSTooltip(element, { label: ["tooltipLabel1", "tooltipLabel2"], text: ["tooltipText1", "tooltipText2", "tooltipText3"], templateId: "templateId" });
 *
 *      tooltipModule.showJSTooltip(element, [100, 50]);
 *      tooltipModule.hideJSTooltip(element);
 *      </code>
 * @param element
 * @param options {Object}
 * <ul>
 *      <li>label {String} - Label for the tooltip</li>
 *      <li>label {Array} - List of labels for complex tooltip</li>
 *      <li>text {String} - Text for the tooltip</li>
 *      <li>text {Array} - List of messages for complex tooltip</li>
 *      <li>templateId {String} - DOM ID of template for the tooltip </li>
 *      <li>offsets {Array} - Used to show the tooltip in position. First item in array is the X point adn second is the Y point </li>
 *      <li>showBelow {Boolean} - Show bellow element</li>
 * </ul>
 *
 */
function JSTooltip(element, options) {
    if (element) {
        this.srcElement = element;

        if (options) {
            this.label = options.label;
            this.text = options.text;
            this.offsets = options.offsets;
            this.showBelow = !! options.showBelow;
            this.templateId = options.templateId;
            this.loadTextCallback = options.loadTextCallback;
            this.loadTextExecuted = false;
        }

        this.disabled = false;
        this.removed = false;

        if (this.templateId) {
            this._toAttribute(this.TOOLTIP_TEMPLATE, this.templateId);
        } else {
            var id = this._fromAttribute(this.TOOLTIP_TEMPLATE);
            this.templateId = (id && id.length > 0) ? id : this.TOOLTIP_TEMPLATE_ID;
        }
        if (this.label) {
            this._toAttribute(this.TOOLTIP_LABEL, this.label);
        } else {
            this.label = this._fromAttribute(this.TOOLTIP_LABEL);
        }
        if (this.text) {
            this._toAttribute(this.TOOLTIP_TEXT, this.text);
        } else {
            this.text = this._fromAttribute(this.TOOLTIP_TEXT);
        }

        // escape against XSS
        if (this.label)
            this.label = this._escapeText(this.label);
        if (this.text)
            this.text = this._escapeText(this.text);

        this.srcElement.jsTooltip = this;

        tooltipModule.tooltips.push(this);
    }
}

JSTooltip.addVar("SEPARATOR", "@@");
JSTooltip.addVar("TOOLTIP_LABEL", "tooltiplabel");
JSTooltip.addVar("TOOLTIP_TEXT", "tooltiptext");
JSTooltip.addVar("TOOLTIP_TEMPLATE", "tooltiptemplate");
JSTooltip.addVar("TOOLTIP_TEMPLATE_ID", "jsTooltip");
JSTooltip.addVar("LABEL_PATTERN", ".message.label");
JSTooltip.addVar("TEXT_PATTERN", ".message:not(.label)");

JSTooltip.addMethod("_toAttribute", function(attrName, value) {
    if(this.srcElement) {
        value = xssUtil.escape(value);
        this.srcElement.writeAttribute(attrName, isArray(value) ? value.join(this.SEPARATOR) : value);
    }
});

JSTooltip.addMethod("_fromAttribute", function(attrName) {
    if(this.srcElement && this.srcElement.hasAttribute(attrName)) {
        var value = this.srcElement.readAttribute(attrName);
        return value.include(this.SEPARATOR) ? value.split(this.SEPARATOR) : value;
    }

    return null;
});

JSTooltip.addMethod("_setValues", function(elements, values) {
    elements.each(function (element, index) {
        if (values[index]) {
            element.update(xssUtil.escape(values[index]));
        }
    });
});

JSTooltip.addMethod("_calculateZIndex", function(element) {
    function getElementZIndex(element) {
        return parseInt(jQuery(element).css("z-index"));
    }

    // Get tooltip element zIndex.
    var initialZIndex = getElementZIndex(element);
    if (!_.isNumber(initialZIndex) || _.isNaN(initialZIndex)) {
        initialZIndex = 1000;
    }

    var parents = _.flatten([this.srcElement, jQuery(this.srcElement).parents().toArray()]);

    // Calculates max z-index between tooltip element and parent elements.
    var maxZIndex = _.reduce(parents, function(memo, parent) {
        var zIndex = getElementZIndex(parent);

        if (_.isNumber(zIndex) && !_.isNaN(zIndex)) {
            memo = Math.max(memo, zIndex);
        }

        return memo;
    }, initialZIndex);

    return maxZIndex + 1;
});

JSTooltip.addMethod("_escapeText", function(text) {
    return _.isArray(text)
                ? _.map(text, function (txt) { return xssUtil.escape(txt)})
                : xssUtil.escape(text);
});

JSTooltip.addMethod("show", function(offsets) {
    if(offsets) {
        this.offsets = offsets;
    }

    this._element = $(this.templateId);

    var offsets;
    if (this.showBelow) {
        offsets = getBoxOffsets(this.srcElement);
        offsets[1] += this.srcElement.clientHeight + 5;
    } else {
        offsets = [getScrollLeft() + 5, getScrollTop() + 5];
    }

    if (this.offsets) {
        offsets[0] += this.offsets[0];
        offsets[1] += this.offsets[1];
    }

    this._element.setStyle({
        position: "absolute",
        left: offsets[0] + 'px',
        top: offsets[1] + 'px',
        zIndex: this._calculateZIndex(this._element)
    });

    var labelElements = this._element.select(this.LABEL_PATTERN);
    var textElements = this._element.select(this.TEXT_PATTERN);

    if(this.label) {
        this._setValues(labelElements, isArray(this.label) ? this.label : [this.label]);
    }
    if(this.text) {
        this._setValues(textElements, isArray(this.text) ? this.text : [this.text]);
    }

    this._element.removeClassName(layoutModule.HIDDEN_CLASS);

    if (offsets[0] + this._element.clientWidth > jQuery(window).width()) {
        var leftOffset = offsets[0] - this._element.clientWidth > 0 ? offsets[0] - this._element.clientWidth : 15;

        this._element.setStyle({ left: leftOffset + "px"});
    }
    if (offsets[1] + this._element.clientHeight > jQuery(window).height()) {
        var topOffset = offsets[1] - this._element.clientHeight - 10;
        this._element.setStyle({ top: topOffset + "px"});
    }

    if (this.loadTextCallback && !this.loadTextExecuted) {
        this.loadTextExecuted = true;

        this.loadTextCallback(this);
    }

    return this;
});

JSTooltip.addMethod("updateText", function(text) {
    this.text = this._escapeText(text);

    this.show();
});

JSTooltip.addMethod("hide", function() {
    if (this._element) {
        this._element.addClassName(layoutModule.HIDDEN_CLASS);
    }
});

JSTooltip.addMethod("disable", function() {
    tooltipModule.hideJSTooltip(this.srcElement);
    this.disabled = true;
});

JSTooltip.addMethod("enable", function() {
    this.disabled = false;
    tooltipModule.showJSTooltip(this.srcElement, this.offsets);
});

JSTooltip.addMethod("remove", function() {
    var index = tooltipModule.tooltips.indexOf(this.srcElement.jsTooltip);
    if (index !== -1) {
        tooltipModule.hideJSTooltip(this.srcElement);
        tooltipModule.tooltips.splice(index, 1);
    }
    this.removed = true;
});

JSTooltip.addMethod("isRemoved", function() {
    return this.removed;
});

var tooltipModule = {
    TOOLTIP_PATTERN: "*[tooltiptext] > *",
    ELEMENT_WITH_TOOLTIP_PATTERN: "*[tooltiptext]",

    actualX:0,
    actualY:0,

    tooltips: [],

    showJSTooltip: function(element, offsets) {
        if (!element.jsTooltip) {
            element.jsTooltip = new JSTooltip(element, {});
        } else if (!element.jsTooltip.disabled) {
            this.actualX = offsets[0];
            this.actualY = offsets[1];

            this.cleanUp();

            var appearDelay = jQuery(element).attr("tooltipappeardelay");
            appearDelay = appearDelay ? parseInt(appearDelay) : 1000;

            element.jsTooltip.timer && clearTimeout(element.jsTooltip.timer);
            element.jsTooltip.timer = setTimeout(function() {
                element.jsTooltip.show([tooltipModule.actualX, tooltipModule.actualY]);
            }, appearDelay);

            jQuery(element).on("mousemove", function(evt){
                tooltipModule.actualX = evt.clientX;
                tooltipModule.actualY = evt.clientY;
            });
        }
    },

    hideJSTooltip: function(element) {
        if (element && element.jsTooltip) {
            element.jsTooltip.timer && clearTimeout(element.jsTooltip.timer);

            element.jsTooltip.hide();

            jQuery(element).off("mousemove");
        }
    },

    cleanUp: function() {
        if (this.tooltips && this.tooltips.length > 0) {
            var removed = [];
            var testId = "fuigasuifdughaiadbvguaidbapvuwbev";
            this.tooltips.each(function(tooltip) {
                if (!(tooltip.srcElement.id && document.getElementById(tooltip.srcElement.id))){
                    tooltip.srcElement.setAttribute("id",testId);
                    if (!document.getElementById(testId)){
                        tooltip.hide();
                        removed.push(tooltip);
                    }
                    tooltip.srcElement.setAttribute("id",null);
                }
            });

            if (removed.length > 0) {
                this.tooltips = this.tooltips.reject(function(t) {
                    return removed.include(t);
                });
            }
        }
    }
};

