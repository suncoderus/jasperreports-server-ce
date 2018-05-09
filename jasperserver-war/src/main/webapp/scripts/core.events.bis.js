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

/* global isSupportsTouch, matchMeOrUp, layoutModule, isIPad, isRightClick, isIE, hasDisabledAttributeSet,
 primaryNavModule, TouchController, tooltipModule
*/

///////////////////////////////////////////////////////////////////////////////////
// Drag with MouseDown
///////////////////////////////////////////////////////////////////////////////////
document.observe(isSupportsTouch() ? 'drag:touchstart' : 'drag:mousedown', function(evt) {
    var element = evt.memo.targetEvent.element();

    if (!isSupportsTouch() || !(event.treeEvent || event.listEvent)) {
        var li = matchMeOrUp(element, layoutModule.LIST_ITEM_PATTERN);
        if (li && !element.match(layoutModule.DISCLOSURE_BUTTON_PATTERN)) {
            buttonManager.down(li, function(element) {
                return $(element).down(layoutModule.LIST_ITEM_WRAP_PATTERN);
            });
        }

        if (element.match(layoutModule.DISCLOSURE_BUTTON_PATTERN)) {
            buttonManager.down(element);
        }
    }
});

///////////////////////////////////////////////////////////////////////////////////
// Mouse Effects
///////////////////////////////////////////////////////////////////////////////////

var buttonManager = {
    over: function(element, findTargetFn) {
        if (element && !this.isSelected(element)) {
            var target  = findTargetFn ? findTargetFn(element) : element;
            $(target).addClassName(layoutModule.HOVERED_CLASS);
        }
    },

    out: function(element, findTargetFn) {
        if (element) {
            var target  = findTargetFn ? findTargetFn(element) : element;
            $(target).removeClassName(layoutModule.HOVERED_CLASS).removeClassName(layoutModule.PRESSED_CLASS);
        }
    },

    down: function(element, findTargetFn) {
        if (element && !this.isSelected(element)) {
            var target  = findTargetFn ? findTargetFn(element) : element;
            $(target).removeClassName(layoutModule.HOVERED_CLASS).addClassName(layoutModule.PRESSED_CLASS);
        }
    },

    up: function(element, findTargetFn) {
        if (element && !this.isSelected(element)) {
            var target  = findTargetFn ? findTargetFn(element) : element;
            target = $(target);
            target.removeClassName(layoutModule.PRESSED_CLASS);
            !isIPad() && target.addClassName(layoutModule.HOVERED_CLASS);
        }
    },

    disable: function(element) {
        if (element) {
            buttonManager.out(element);
            $(element).writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);
        }
    },

    enable: function(element) {
        if (element) {
            buttonManager.out(element);
            $(element).writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
        }
    },

    /**
     * @deprecated custom jasperhandler in Prototype.js will suppress disabled elems
     * @param {Object} element
     */
    isDisabled: function(element) {
        if (element) {
            return $(element).readAttribute(layoutModule.DISABLED_ATTR_NAME) === layoutModule.DISABLED_ATTR_NAME ||
                $(element).hasClassName(layoutModule.DISABLED_CLASS);
        }
    },

    ///////////////////////////////////////////////////////////////////////////////////////
    // TODO: Only used by tab manager - maybe we should use up and down functions instead.
    // (just need to make tabs use 'pressed' class instead of 'selected')
    ///////////////////////////////////////////////////////////////////////////////////////

    unSelect : function(element){
        if(element){
            $(element).removeClassName(layoutModule.SELECTED_CLASS);
        }
    },


    select : function(element){
        if(element){
            $(element).addClassName(layoutModule.SELECTED_CLASS);
        }
    },

    isSelected : function(element, findTargetFn){
        if(element){
            var target  = findTargetFn ? findTargetFn(element) : $(element);
            var tagetListItem = target.up('li');
            return tagetListItem && tagetListItem.hasClassName(layoutModule.SELECTED_CLASS);
        }
        return false;
    }
};

///////////////////////////////////////////////////////////////////////////////////
// Suppress Default Context Menu
///////////////////////////////////////////////////////////////////////////////////
//use this to cancel the default event. Weird behavior on mac Gecko browser
//see link:http://unixpapa.com/js/mouse.html for more info

document.observe('mouseup', function(evt){
    if (isRightClick(evt)) {
        var node = evt.element();
        document.fire(layoutModule.ELEMENT_CONTEXTMENU, { targetEvent: evt, node: node});
    }
});
// Workaround for IE9 native context menu
document.observe('contextmenu', function(event) {
    Event.stop(event);
    return false;
});

document.observe('dom:loaded', function(event) {
    isIE() && document.body.setAttribute('oncontextmenu', 'return false');

    jQuery('body').on('mouseover',layoutModule.BUTTON_PATTERN,function(evt){
        if(!hasDisabledAttributeSet(this)) buttonManager.over(this);
    });

    jQuery('body').on('mouseout',layoutModule.BUTTON_PATTERN,function(evt){
        buttonManager.out(this);
    });

    jQuery('body').on('focus',layoutModule.BUTTON_PATTERN,function(evt){
        if(!hasDisabledAttributeSet(this)) buttonManager.over(this);
    });

    jQuery('body').on('blur',layoutModule.BUTTON_PATTERN+'.'+layoutModule.HOVERED_CLASS,function(evt){
        if(!hasDisabledAttributeSet(this)) buttonManager.out(this);
    });

    jQuery('body').on('mousedown mouseup touchstart touchend',[layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN, layoutModule.DISCLOSURE_BUTTON_PATTERN, layoutModule.META_LINKS_PATTERN].join(','),function(evt){
        if (evt.type == 'mousedown' || evt.type == 'touchstart') {
            !hasDisabledAttributeSet(this) && buttonManager.down(this);
        } else {
            !hasDisabledAttributeSet(this) && buttonManager.up(this);
        }
        if((evt.type == 'mouseup' || evt.type == 'touchend')) {
            if(this.parentNode.id == layoutModule.MAIN_NAVIGATION_HOME_ITEM_ID) primaryNavModule.navigationOption("home");
            if(this.parentNode.id == layoutModule.MAIN_NAVIGATION_LIBRARY_ITEM_ID) primaryNavModule.navigationOption("library");
        }
    });

    jQuery('#frame').on('touchend mouseup','.minimize',function(evt){
        if(this.parentNode.className.indexOf('maximized') >= 0) {
            layoutModule.minimize(this);
        } else {
            layoutModule.maximize(this);
        }
        evt.preventDefault();
    });

    jQuery('#frame').on('touchend mouseup',layoutModule.TABSET_TAB_PATTERN,function(evt){
        if(!hasDisabledAttributeSet(this) && jQuery(this.parentNode).attr("disableCoreEvents") !== "true") {
            jQuery(this).siblings().removeClass(layoutModule.SELECTED_CLASS).each(function(index,element){
                jQuery(jQuery(this).attr("tabId")).addClass("hidden");
            });
            jQuery(this).addClass(layoutModule.SELECTED_CLASS);
            jQuery(jQuery(this).attr("tabId")).removeClass("hidden");

            // Dirty hack to make anchor bigger when attributes tab is selected.
            var $anchor = jQuery(this).closest(".tabs").find(".control.tabSet.anchor");
            jQuery(this).attr("tabId") === "#attributesTab"
                ? $anchor.addClass("attributesAnchor")
                : $anchor.removeClass("attributesAnchor")
        }
    });

    jQuery('#'+layoutModule.META_LINK_LOGOUT_ID).on('mousedown touchstart', function(evt){
        evt.preventDefault();
        primaryNavModule.navigationOption("logOut");
    });
    /*
     * Tooltips
     */
    jQuery('body').on('mouseover mouseout click','[tooltiptext]',function(evt){
        (evt.type == 'mouseout' || evt.type == 'click') && tooltipModule.hideJSTooltip(this);
        evt.type == 'mouseover' && tooltipModule.showJSTooltip(this, [evt.clientX, evt.clientY]);
    });
    /*
     * Top navigation menu
     */
    jQuery('#mainNavigation').on('mouseover',layoutModule.NAVIGATION_MUTTON_PATTERN,function(evt){
        primaryNavModule.showNavButtonMenu(evt, this);
    });

    if(isSupportsTouch()){
        document.body.addEventListener('touchstart', function(e) {
            window.calendar && window.calendar.hide && !window.calendar.hidden && window.calendar.hide();
            if(typeof TouchController !== 'undefined') TouchController.element_scrolled = false;
        }, false);
        document.body.addEventListener('touchmove', function(e) {
            //e.preventDefault();
        }, false);
    }
    /*
     * Bug fix 28602.
     */
    jQuery('#filePath').on('mouseenter mouseout',function(){
        jQuery('#fake_upload_button').toggleClass('over');
    });
});
