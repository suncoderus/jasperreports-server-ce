/*
 * Copyright (C) 2014 - 2015 TIBCO Software Inc. All rights reserved.
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

/* Standard Navigation library (stdnav) plugin
 * Elements: LI, OL, UL
 * Navtype:  actionmenu
 *
 * Plugin for "actionModel"-based menus, such as the JRS main menu, which
 * handle mouse and touch events, but offer no keyboard support.  (Note
 * that "actionModel" itself is not used in a Backbone context yet, in
 * case its name suggests that to you; it's a coincidence.)
 *
 * This module supports enhancements intended to improve compliance with
 * section 508 of the Rehabilitation Act of 1973, 29 USC 798, as amended
 * 1998.
 */

define(function (require, exports, module) {

    "use strict";

    var
        $ = require("jquery"),
        _ = require("underscore"),
        logger = require("logger").register(module),
        stdnav = require("stdnav"),
        actionModel = require("actionModel.modelGenerator"),
        buttonManager=require("core.events.bis"),
        layoutModule = require("core.layout"),
        primaryNavigation = require("actionModel.primaryNavigation"),
        singleton = null,
        gserial = 0;

    // ===EXTERNAL SYMBOLS USED:=============================================
    // layoutModule

    // Local object definition.

    var stdnavPluginActionMenu = function () {
        gserial++;
        this.serial = gserial;
        this.menu_item_callbacks = {
            click: {}
        };
    };

    _.extend(stdnavPluginActionMenu.prototype, {
        zinit: function (selector) {
            return this;
        },

        // Registers the 'actionMenu' navtype with stdnav.  Both stdnav and ActionMenu
        // must be loaded and activated before this can be done.
        activate: function () {
            // This is the behaviour hash for the navtype.  These defaults pass
            // everything through to the browser, and are normally overridden
            // with $.extend based on specific tagnames and stdnav attributes.
            this.behavior = {
                'down': [this, this._onDown, null],
                'enter': null,
                'exit': null,
                'fixfocus': [this, this._fixFocus, null],
                'fixsubfocus': [this, this._fixFocus, null],
                'fixsuperfocus': [this, this._fixSuperfocus, null],
                'focusin': [this, this._onFocusIn, null],
                'focusout': [this, this._onFocusOut, null],
                'left': [this, this._onLeft, null],
                //'mouseout': [this, this._onMouseOut, null],
                //'mouseover': [this, this._onMouseOver, null],
                'right': [this, this._onRight, null],
                'superfocusout': [this, this._onSuperfocusOut, null],
                'up': [this, this._onUp, null],
                'inherit': false,
                'inheritable': true
            };
            stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
        },

        // Unregisters the 'actionMenu' navtype from stdnav.  This must be done
        // before deactivating/unloading stdnav.
        deactivate: function () {
            stdnav.unregisterNavtype(this.navtype, this);
        },

        /* ====== Focus Management ====== */


        // Focus adjustment callback.  Ensures that if the DIV or UL elements
        // themselves are given focus, that it is promoted to the first LI.
        _fixFocus: function (element) {
            var newFocus;
            var $el = $(element);
            if ($el.is('div,ul,ol')) {
                // The usual case: the entire menu is gaining focus.  One of
                // the reasons this can happen, of course, is in response to a
                // mouse click, not a TAB press-- as a result, check to see if
                // any of the menu options has the "pressed" CSS class, and
                // focus that, if it does.  However, ignore the "over" class,
                // as the mouse pointer can wind up anywhere for a blind user
                // by total coincidence, so it must be ignored when setting
                // focus in response to TAB.
                //
                // If nothing seems to be in use yet, select the first list
                // item-- if there ARE any.
                //
                // Failing even that, select the list itself.
                var items = $el.find('.pressed');
                if (items.length > 0) {
                    newFocus = items[0];
                } else {
                    items = $el.find('li');
                    if (items.length > 0) {
                        newFocus = items[0];
                    } else {
                        // The entire list is empty-- set focus to the root list element
                        // after all.
                        newFocus = element;
                    }
                }
            } else if ($el.is('li')) {
                // Focus is already appropriate.
                newFocus = element;
            } else {
                // Assume we're in a span or something within a list item.
                var lis = $el.closest('li');
                if (lis.length > 0) {
                    if ($(lis[0]).prop['js-navigable'] === false) {
                        // Clicked on a header or something; focus the list instead.
                        newFocus = $el.closest('ul,ol');
                    } else {
                        newFocus = lis[0];
                    }
                } else {
                    // Clicked on non-list-item content inside the list.
                    // This COULD happen, but probably indicates a bad template.
                    // Either way, don't crash.
                    newFocus = $el.closest('ul,ol');
                    newFocus = $el.find('li');
                    if (newFocus.length > 0) {
                        newFocus = newFocus[0];
                    }
                }
            }
            return newFocus;
        },

        // Superfocus adjustment callback.  Because of the way actionModel
        // works, the menu is expected to be rooted in an enclosing DIV that 
        // contains the list, so use the DIV for the superfocus region.
        // However, context menus are _not_ 
        _fixSuperfocus: function (element) {
            var newSuperfocus;
            var $root = $(element).closest('.menuRoot,.dropDown');
            if ($root.length > 0) {
                newSuperfocus = $root[0];
            } else {
                // FAULT, let StdNav fall back to BODY
                newSuperfocus = null;
            }
            return newSuperfocus;
        },

        _onFocusIn: function (element) {
            var
                $thisItem,
                $matched,
                $next = $(element);
            var $selected = $(element);
            if ($selected.length>0) {
                $thisItem = $selected.closest(layoutModule.NAVIGATION_PATTERN);
                if ($thisItem.length>0) {
                    // An item in the main menu bar has been focused.
                    buttonManager.over($thisItem.find(layoutModule.BUTTON_PATTERN)[0]);
                    //buttonManager.over($thisItem.find(layoutModule.MENU_LIST_PATTERN)[0]);
                    $matched = $thisItem.closest(layoutModule.NAVIGATION_MUTTON_PATTERN);
                    if ($matched.length>0) {
                        // The item has a submenu-- show it.
                        //actionModel.hideMenu();
                        primaryNavigation.showNavButtonMenu(null, $matched[0]);
                    } else {
                        // An item with no drop-down menu has been focused;
                        // ensure any drop-down menus for other items are closed.
                        actionModel.hideMenu();
                    }
                } else {
                    $thisItem = $selected.closest(layoutModule.MENU_LIST_PATTERN);
                    if ($thisItem.length>0) {
                        // An item in a drop-down or context menu has been focused.
                        buttonManager.over($thisItem.find(layoutModule.BUTTON_PATTERN)[0]);
                        $matched = $thisItem.closest(layoutModule.NAVIGATION_MUTTON_PATTERN);
                        /* FIXME: flyouts (none exist in main menu right now)
                        if ($matched.length>0) {
                            // NOTE: This function expects an event object that will
                            // not exist for keyboard navigation and automation
                            // purposes.  Fix that code flow-- DO NOT try to synthesize
                            // an event or provide a surrogate object.  This flow
                            // must work for a null object.
                            primaryNavigation.showNavButtonMenu(null, $matched[0]);
                        }
                        */
                    }
                }
            }
            return element;
        },

        _onFocusOut: function (element) {
            var
                $thisItem = $(element).closest(layoutModule.NAVIGATION_PATTERN);

            if ($thisItem.length>0) {
                // A top-level item in the main menu bar has lost focus.
                //actionModel.hideMenu();
                // Don't remove the style if we've moved into a drop-down menu.
                if (this.lastMenuBarItem!==element){
                    buttonManager.out($thisItem.find(layoutModule.BUTTON_PATTERN)[0]);
                }
                //buttonManager.out($thisItem.find(layoutModule.MENU_LIST_PATTERN)[0]);
            } else {
                $thisItem = $(element).closest(layoutModule.MENU_LIST_PATTERN);
                if ($thisItem.length>0) {
                    // An item in a drop-down or context menu has lost focus.
                    buttonManager.out($thisItem.find(layoutModule.BUTTON_PATTERN)[0]);
                }
            }
            return null;
        },

        // When the entire menu loses superfocus, ensure that any remaining
        // hover events and classes for actionMenu fire as expected, and that
        // the context menu is hidden.
        _onSuperfocusOut: function(element){
            var $matched,
                $next = $(element),
                $nav = $("#"+layoutModule.MAIN_NAVIGATION_ID);

            if ($nav.length<1){
                // There is no main navigation in embedded mode.
                return element;
            }
            // Hide the dropdown menu unless the new focus is _in_ the
            // dropdown menu.  Because of the way actionModel works, the
            // dropdown menu is NOT a DOM descendant of the main menu.
            var newFocus=$(document.activeElement);
            if (newFocus.closest('.dropDown').length<1){
                var $selected = $nav.find("." + layoutModule.HOVERED_CLASS);
                if ($selected.length>0) {
                    buttonManager.out($selected[0]);
                }
                // FIXME-- TURN THIS BACK ON once context-menu focus issues are
                // resolved.  RETEST bug #43172, which is INTERMITTENT, after
                // doing so.
                //actionModel.hideMenu();
            }
        },

        /* ====== MENU BEHAVIOR ====== */
        _focus_prev_menu_entry: function (entry) {
            var newFocus;
            var wasOpen = false;

            if (entry.hasClass('node') && (!entry.children('.menu').hasClass('is-closed'))) {
                wasOpen = true;
            }
            newFocus = entry.prev();
            if (newFocus.length === 0) {
                // Wrap around to the end
                newFocus = entry;
                while (newFocus.next().length > 0) {
                    newFocus = newFocus.next();
                }
            }
            logger.debug('Granting focus to ' + newFocus.attr('id'));
            stdnav.setSubfocus(newFocus);
            if (wasOpen === true) {
                //this._open_submenu(newFocus.children('.menu'));
            }
        },

        _focus_next_menu_entry: function (entry) {
            var newFocus;
            var wasOpen = false;

            if (entry.hasClass('node') && (!entry.children('.menu').hasClass('is-closed'))) {
                wasOpen = true;
            }
            newFocus = entry.next();
            if (newFocus.length === 0) {
                // Wrap around to the beginning
                newFocus = entry;
                while (newFocus.prev().length > 0) {
                    newFocus = newFocus.prev();
                }
            }
            logger.debug('Granting focus to ' + newFocus.attr('id'));
            stdnav.setSubfocus(newFocus);
            if (wasOpen === true) {
                //this._open_submenu(newFocus.children('.menu'));
            }
        },

        /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */

        _onSubfocusIn: function (element) {
            // Handle menus hosted in non-focusable elements (such as a cell in a grid).
            if (($(element).prop('nodeName') === 'li') === false) {
                // Find a usable child element.
                var subel = this._fixSubfocus(element);
                // Adjust subfocus without firing callbacks.
                stdnav.setSubfocus(subel, false);
            }
        },

        _onExit: function (element) {
            // Closes everything and returns focus to the menu root itself.
            //ev.data.jsam._close_submenus_below($(ev.currentTarget).closest('.menuRoot'));
            $(element).closest('.menuRoot').focus();
        },

        /* ========== MOUSE BEHAVIOR =========== */
        /*
        _onMouseOver: function(element){
            // Keyboard navigation within the menu can 
            return null;
        },

        _onMouseOut: function(element){
            return null;
        },
        */

        /* ========== KEYBOARD BEHAVIOR =========== */
        _onLeft: function (element){
            var $thisItem = $(element).closest(layoutModule.NAVIGATION_PATTERN);
            var $prev=$(element);
            if ($thisItem.length>0){
                // We're in the menu bar.
                $prev = $thisItem.prev(layoutModule.NAVIGATION_PATTERN);
            } else {
                $thisItem = $(element).closest(layoutModule.MENU_LIST_PATTERN);
                if ($thisItem.length>0){
                    // We're in the drop list.  Get the previous item for the
                    // last menu bar item and redirect focus to that.
                    $prev=$(this.lastMenuBarItem).prev(layoutModule.NAVIGATION_PATTERN);
                    this.lastMenuBarItem=null;
                }
            }
            if ($prev.length>0) {
                return $prev[0];
            } else {
                // FIXME: Wrap menu
                return element;
            }
            return element;
        },

        _onRight: function (element) {
            var $thisItem = $(element).closest(layoutModule.NAVIGATION_PATTERN);
            var $next=$(element);
            if ($thisItem.length>0){
                // We're in the menu bar.
                $next = $thisItem.next(layoutModule.NAVIGATION_PATTERN);
            } else {
                $thisItem = $(element).closest(layoutModule.MENU_LIST_PATTERN);
                if ($thisItem.length>0){
                    // We're in the drop list.  Get the previous item for the
                    // last menu bar item and redirect focus to that.
                    $next=$(this.lastMenuBarItem).next(layoutModule.NAVIGATION_PATTERN);
                    this.lastMenuBarItem=null;
                }
            }
            if ($next.length>0) {
                return $next[0];
            } else {
                // FIXME: Wrap menu
                return element;
            }
            return element;
        },

        _onUp: function (element) {
            var $prev = $(element);
            // Figure out whether we're in the main menu or the popup.
            var $menu = $(document.activeElement).closest("."+actionModel.DROP_DOWN_MENU_CLASS);
            if ($menu.length>0){
                // We're in a drop-down or context menu.
                var $thisItem=$(document.activeElement).closest(layoutModule.MENU_LIST_PATTERN);
                $prev=$thisItem.prev(layoutModule.MENU_LIST_PATTERN);
                // Oddly, trying to add ":not(.separator)" to the pattern
                // above did not work; the separator was indeed skipped, but
                // the menu item after it was not returned.  This code skips
                // over any number of adjacent separators.
                while ($prev.is(layoutModule.SEPARATOR_PATTERN)){
                    $prev=$prev.prev(layoutModule.MENU_LIST_PATTERN);
                }
                // If we were already at the top, we want to move back into the
                // menu bar.
                if ($prev.length<1){
                    $prev=$(this.lastMenuBarItem);
                    this.lastMenuBarItem=null;
                }
            } else {
                // If we're not in a popup/context menu, we should be in the main
                // menu bar itself.  Up-arrow has no effect here.
                return element;
            }
            if ($prev.length>0){
                return $prev[0];
            } else {
                return element;
            }
        },

        _onDown: function (element) {
            var $next = $(element);
            // Figure out whether we're in the main menu or the popup.
            var $menu = $(document.activeElement).closest("."+actionModel.DROP_DOWN_MENU_CLASS);
            if ($menu.length>0){
                // We're in a drop-down or context menu.
                var $thisItem=$(document.activeElement).closest(layoutModule.MENU_LIST_PATTERN);
                $next=$thisItem.next(layoutModule.MENU_LIST_PATTERN);
                // Oddly, trying to add ":not(.separator)" to the pattern
                // above did not work; the separator was indeed skipped, but
                // the menu item after it was not returned.  This code skips
                // over any number of adjacent separators.
                while ($next.is(layoutModule.SEPARATOR_PATTERN)){
                    $next=$next.next(layoutModule.MENU_LIST_PATTERN);
                }
            } else {
                // If we're not in a popup/context menu, we should be in the main
                // menu bar itself.  No other cases should occur, but they are
                // ignored if they do.
                $menu = $(document.activeElement).closest("."+layoutModule.MENU_ROOT_CLASS);
                if ($menu.length<1){
                    return element;
                }
                // The drop-down menu should have been displayed when this
                // node was focused.  Therefore, we need merely move focus to
                // the first element in the drop-down menu-- unless this menu
                // bar item has no drop-down, in which case we do nothing.
                // We also need to store where we were in the menu bar, in case
                // we come back to it via up-arrow or ESCAPE.
                if (actionModel.isMenuShowing()) {
                    this.lastMenuBarItem=element;
                    $next=$(layoutModule.MENU_LIST_PATTERN);
                }
            }
            if ($next.length>0){
                return $next[0];
            } else {
                return element;
            }
        }
    });

    // SECOND EXTENSION PASS - ATTRIBUTES
    // Hash members in this pass can reference functions from the last pass.
    $.extend(stdnavPluginActionMenu.prototype, {
        // This is the name of the new navtype.  Each stdnav plugin must
        // define a unique name.
        navtype: 'actionmenu',

        // This arrray extends the tag-to-navtype map in stdnav.  If your
        // plugin should apply to all elements of a given type, add those
        // element tagnames, in lower case, to this array.  It is normally
        // empty, and the page templates simply set an appropriate
        // "data-navtype=" attribute to get the expected behavior.
        //
        // NOTE: The HTML5 "menu" element is still very broken.  In practice
        // our menus are built with list items and use "js-navtype" overrides to
        // this type.
        //
        // CASE SENSITIVE - USE UPPER-CASE!
        navtype_tags: []
    });

    return new stdnavPluginActionMenu();
});
