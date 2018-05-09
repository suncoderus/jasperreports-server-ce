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
 * Navtype:  dynamiclist
 *
 * Plugin for the "dynamiclist" navtype.  This is used for ordered and
 * unorderedlists ("ul" and "ol" tags, and their "li" children) which do not
 * load all content on startup, and which use a tabular visual presentation
 * even the though the semantic presentation is as a list.  Properly handles
 * scrolling resulting from keyboard navigation.
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
        eventAutomation = require("common/util/eventAutomation"),
        logger = require("logger").register(module),
        stdnav = require("stdnav"),
        dynamicList = require("components.list"),
        version = "0.0.1",
        singleton = null,
        gserial = 0;

    // Local object definition.

    var stdnavPluginDynamicList = function () {
        gserial++;
        this.serial = gserial;
    };

    // FIRST EXTENSION PASS - FUNCTIONS
    // Provides forward-references for hashes in the second pass, so that
    // references in that second pass to functions declared here resolve
    // (since the entire pass is applied at once, they cannot be combined).
    $.extend(stdnavPluginDynamicList.prototype, {
        zinit: function (selector) {
            logger.debug('stdnavPluginDynamicList.init(' + selector + ")\n");
            return this;
        },

        // Registers the 'dynamiclist' navtype with stdnav.  stdnav must be loaded and
        // activated before this can be done.
        activate: function () {
            // This is the behaviour hash for the navtype.  These defaults pass
            // everything through to the browser, and are normally overridden
            // with $.extend based on specific tagnames and stdnav attributes.
            //
            // For dynamic lists, pass arrow keys through to the existing keyboard
            // behavior.
            this.behavior = {
                'ariaprep': [this, this._ariaPrep, null],
                'ariarefresh': [this, this._ariaRefresh, null],
                'click': null,
                'down': null,
                'end': null,
                'enter': [this, this._onEnter, null],
                'fixfocus': [this, this._fixFocus, null],
                'fixsuperfocus': [this, this._fixSuperfocus, null],
                'home': null,
                'inherit': true,
                'inheritable': true,
                'left': null,
                'pagedown': null,
                'pageup': null,
                'right': null,
                'superfocusin': [stdnav, stdnav.basicSuperfocusIn, {
                    'maxdepth': 1,
                    'focusSelector': 'li',
                    'ghostfocus': false
                }],
                'superfocusout': [stdnav, stdnav.basicSuperfocusOut, {
                    'ghostfocus': false
                }],
                'up': null
            };
            stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
        },

        // Unregisters the 'dynamiclist' navtype from stdnav.  This must be done
        // before deactivating/unloading stdnav.
        deactivate: function () {
            stdnav.unregisterNavtype(this.navtype, this.behavior);
        },

        // This callback is run when the page is initially rendered.  Add the
        // appropriate ARIA tags for the handled construt, if they do not already
        // exist.  The element passed will be the superfocus for the construct
        // being instrumented, but this construct may not actually have focus at
        // the time this function is called.
        _ariaPrep: function (el) {
            $(el).attr('role', 'application');
            $(el).attr('aria-label', 'Dynamic List');
            return null;
        },

        // This callback is run when the superfocus changes to the construct.
        // Its two purposes are to update existing constructs, and to handle
        // dynamically-created content whose creation was not detected during
        // initial page construction (possibly because no part of the construct
        // existed yet).  The element passed will be the superfocus for the
        // construct, but this construct may not actually have focus at the time
        // this function is called.
        _ariaRefresh: function (el) {
            $(el).attr('role', 'application');
            $(el).attr('aria-label', 'Dynamic List');
            return null;
        },

        _onEnter: function(element) {
            var $el=$(element);
            if ($el.is('.supercursor')) {
                // If a sublist is expanded, and a subitem has an active cursor,
                // then the parent item gets the "supercursor" class instead,
                // and should NEVER get keyboard focus at the same time (or the
                // cursor would have been removed from the sublist and this item
                // would have "cursor" instead of "supercursor" class).
                logger.error("dynamicList item has supercursor class-- should not have focus while this is true.");
                if ($el.find("ul > li.cursor")<0) {
                    logger.error("...additionally, no sublist item appears to have the cursor class.");
                }
            } else {
                // Using "stdnav.getClosestNavigableDescendant" here would just
                // give us the sublist, if any, if that appears in the markup
                // prior to the actual link on this item.  Therefore, explicitly
                // look for buttons, links, and form fields only.
                var $links=$el.find('a,:input');
                if ($links.length>0){
                    // Fire the click handler
                    eventAutomation.simulateClickSequence($links[0]);
                }
            }
            // DO NOT move focus.
            return element;
        },

        // Focus adjustment callback.  Given an element somewhere within a
        // dynamicList, including the list itself, return the element which should
        // have get focused.  This is done by obtaining the cursor for the list.
        _fixFocus: function (element) {
            var $listEl = $(element).closest("[js-navtype='" + this.navtype + "']");
            if ($listEl.length < 1) {
                logger.warn("Can't find a dynamic list to fix focus to");
                return element;
            }
            var listObj = dynamicList.getDynamicListForElement($listEl[0]);
            if (stdnav.nullOrUndefined(listObj)) {
                logger.warn("Can't map a dynamic list to fix focus to");
                return element;
            }
            return listObj.getCursorElement();
        },

        // Subfocus adjustment callback.  Use the same element that has focus.
        _fixSubfocus: function (element) {
            return null;
        },

        // Superfocus adjustment callback.  Given an element somewhere within a
        // sub-DOM within a list, ensures that the list itself (OL,UL) is given
        // superfocus.
        _fixSuperfocus: function (element) {
            var newSuperfocus;
            var $closestList = $(element).closest('ol,ul')
            if ($closestList.length > 0) {
                newSuperfocus = $closestList[0];
            } else {
                // FAULT, let StdNav fall back to BODY
                newSuperfocus = null;
            }
            return newSuperfocus;
        },

        /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */

        // The list has been entered from another control, and superfocus has been
        // set appropriately.  Browser focus may be temporarily on the control's
        // container.  Return the element which should receive browser focus for
        // accessibility purposes.
        _onSuperfocusIn: function (element) {
            var newFocus;
            // This should fire on the list itself, not the item.
            var ghosts = $(element).children('li .ghostfocus');
            if (ghosts.length > 0) {
                ghosts.removeClass('ghostfocus');
                // Promote ghost.
                newFocus = ghosts[0];
            } else {
                // No ghosts.  Set focus to first item.
                var items = $(element).children('li[js-navigable!="false"]');
                if (items.length > 0) {
                    newFocus = items[0];
                } else {
                    // For visual reasons, in the case of an empty list, we give up and
                    // set subfocus to the list itself.
                    newFocus = element;
                }
            }
            return newFocus;
        },

        // The list is being exited for another control, and superfocus has been
        // lost.
        _onSuperfocusOut: function (element) {
            // FIXME: ghostfocus
        }

    });

    // SECOND EXTENSION PASS - ATTRIBUTES
    // Hash members in this pass can reference functions from the last pass.
    $.extend(stdnavPluginDynamicList.prototype, {
        // This is the name of the new navtype.  Each stdnav plugin must
        // define a unique name.
        navtype: 'dynamiclist',

        // This arrary extends the tag-to-navtype map in stdnav.  If your
        // plugin should apply to all elements of a given type, add those
        // element tagnames, in lower case, to this array.  It is normally
        // empty, and the page templates simply set an appropriate
        // "data-navtype=" attribute to get the expected behavior.
        //
        // The normal List plugin should handle UL and OL tags by default.
        navtype_tags: []
    });
    var newStdnavPluginDynamicList = new stdnavPluginDynamicList();
    return newStdnavPluginDynamicList;
});