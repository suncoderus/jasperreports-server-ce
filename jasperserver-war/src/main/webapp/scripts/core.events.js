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

/* global isSupportsTouch, layoutModule, primaryNavModule, matchAny, isIPad, matchMeOrUp, relatedTargetInElementSubtree,
 isIE7, isRightClick, isIE
  */

/**
 * General mouse down event tracking
 */

document.observe(isSupportsTouch() ? 'touchstart' : 'mousedown', function(evt) {
    var primaryPanel ;
    var element = evt.element();
  
	if(typeof(element.match) !== 'undefined'){

		if (element.match(layoutModule.MINIMIZED_PATTERN)) {
			layoutModule.maximize(element);
			return;
		}

		if (element.match(layoutModule.MINIMIZER_PATTERN)) {
			layoutModule.minimize(element);
			return;
		}

		//for meta links
		if(element.match(layoutModule.META_LINKS_PATTERN)){
			if(element.readAttribute("id") === "main_logOut_link"){
				primaryNavModule.navigationOption("logOut");
			}
		}

		if (!isSupportsTouch() || !(event.treeEvent || event.listEvent)) {
			//for any button type element (including list/tree items, menu items, toolbar buttons and tabs)f
			var matched = matchAny(element, [layoutModule.BUTTON_PATTERN, layoutModule.MENU_LIST_PATTERN ], true);
			if (matched && !$(matched).match(layoutModule.PRESSED_PATTERN)) {
				buttonManager.down(matched);
			}

			//APC notes: disclosure events can never get here (noBubble). Delete?
			if (element.match(layoutModule.DISCLOSURE_BUTTON_PATTERN)) {
				buttonManager.down(element);
			}
		}
	}
});

if(isIPad()){
	window.addEventListener('touchmove',function(e){
	})
}
	
/**
 * General mouse over event tracking
 * @param draggable
 */
document.observe('mouseover', function(evt){
    var element = evt.element();
    var matched = null;

    //for navigation buttons
    matched = matchMeOrUp(element, layoutModule.NAVIGATION_MUTTON_PATTERN);
    if (matched && !relatedTargetInElementSubtree(evt,matched)) {
        primaryNavModule.onMenuHeaderMouseOver(evt, matched);
        //TODO: clean up
        matched.tabIndex = -1;
        matched.focus();
    }

    if (!isIE7()) {
        //for any button type element (including list/tree items, menu items, toolbar buttons and tabs)
        matched = matchAny(element, [layoutModule.BUTTON_PATTERN], true);
        if (matched && !relatedTargetInElementSubtree(evt, matched) && !matched.hasClassName(layoutModule.DROP_TARGET_CLASS)) {
            buttonManager.over(matched);
        }

        //APC notes: disclosure events can never get here (noBubble). Delete?
        //TODO: Remove this!!!!
        if (element.match && element.match(layoutModule.DISCLOSURE_BUTTON_PATTERN)) {
            buttonManager.over(element);
        }
    }
});
/**
 * General mouse out event tracking
 * @param draggable
 */
if(!isIE7()) {
	document.observe('mouseout', function(evt){
	    var matched = null;
	    var element = evt.element();

	    //for any button type element (including list/tree items, menu items, toolbar buttons and tabs)
	    matched = matchAny(element, [layoutModule.BUTTON_PATTERN],true);
	    if(matched && !relatedTargetInElementSubtree(evt,matched)){
	        buttonManager.out(matched);
	    }

	    //APC notes: disclosure events can never get here (noBubble). Delete?
	    //TODO: Remove this!!!!
	    if (element.match && element.match(layoutModule.DISCLOSURE_BUTTON_PATTERN)) {
	        buttonManager.out(element);
	    }
	});
}
/**
 * General mouse up event tracking
 * @param draggable
 */
document.stopObserving(isSupportsTouch() ? 'touchend' : 'mouseup').
        observe(isSupportsTouch() ? 'touchend' : 'mouseup', function(evt){

    var element = evt.element();
    var matched = null;

    matched = matchMeOrUp(element, layoutModule.NAVIGATION_PATTERN);
    if (matched){
        if(matched.identify() == layoutModule.MAIN_NAVIGATION_HOME_ITEM_ID){
            primaryNavModule.navigationOption("home");
        } else if(matched.identify() == layoutModule.MAIN_NAVIGATION_LIBRARY_ITEM_ID){
            primaryNavModule.navigationOption("library");
        } else {
            return;
        }
    }

    //for general tab-set tabs
    //TODO: If we use 'pressed' class instead of 'selected' class we can use existing buttonMgr down() and up() handlers
    //instead
    matched = matchMeOrUp(element, layoutModule.TABSET_TAB_PATTERN);
    if(matched/* && matched.match(layoutModule.BUTTON_PATTERN)*/) {
        if (!$(matched).match(layoutModule.SELECTED_PATTERN)){
            $(matched).siblings().each(function(object) {
                buttonManager.unSelect($(object));
            });
            buttonManager.select($(matched));
        }
    }

    //for any button type element (including list/tree items, menu items, toolbar buttons and tabs)
    matched = matchAny(element, [layoutModule.BUTTON_PATTERN,layoutModule.BUTTON_SET_BUTTON, layoutModule.MENU_LIST_PATTERN ], true);
    if (matched && !matched.match(layoutModule.TOOLBAR_CAPSULE_PATTERN)) {
        buttonManager.up(matched);
    }

    if (!isSupportsTouch() || !(event.treeEvent || event.listEvent)) {
        //APC notes: disclosure events can never get here (noBubble). Delete?
        //TODO : remove this code!!!
        if (element.match && element.match(layoutModule.DISCLOSURE_BUTTON_PATTERN)) {
            buttonManager.up(element);
        }
    }
    
    if (!isIPad() && isRightClick(evt)) {
        var node = evt.element();
        document.fire(layoutModule.ELEMENT_CONTEXTMENU, { targetEvent: evt, node: node});
    }
});

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
            var target  = findTargetFn ? findTargetFn(element) : element;
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

// Workaround for IE9 native context menu
document.observe('contextmenu', function(event) {
    Event.stop(event);
    return false;
});

document.observe('dom:loaded', function(event) {
   if (isIE()){
    document.body.setAttribute('oncontextmenu', 'return false');
}
});
