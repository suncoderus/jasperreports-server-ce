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
 * Elements: A
 * Navtype:  anchor (element default)
 *
 * Plugin for the "anchor" navtype.  This is the default behavior for "A"
 * tags, handling both normal "HREF" links and "#" internal/Backbone/fake
 * button links.
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
        version = "0.0.1",
        singleton = null,
        gserial = 0;

    // Local object definition.

    var stdnavPluginAnchor = function () {
        gserial++;
        this.serial = gserial;
    };

    // FIRST EXTENSION PASS - FUNCTIONS
    // Provides forward-references for hashes in the second pass, so that
    // references in that second pass to functions declared here resolve
    // (since the entire pass is applied at once, they cannot be combined).
    $.extend(stdnavPluginAnchor.prototype, {
        zinit: function (selector) {
            logger.debug('stdnavPluginAnchor.init(' + selector + ")\n");
            return this;
        },

        // Registers the 'anchor' navtype with stdnav.  stdnav must be loaded and
        // activated before this can be done.
        activate: function () {
            // This is the behaviour hash for the navtype.  These defaults pass
            // everything through to the browser, and are normally overridden
            // with $.extend based on specific tagnames and stdnav attributes.
            this.behavior = {
                'ariaprep': [this, this._ariaPrep, null],
                'ariarefresh': [this, this._ariaRefresh, null],
                'down': [this, this._onLeftOrUp, null],
                'enter': [this, this._onEnterOrEntered, null],
                'entered': [this, this._onEnterOrEntered, null],
                'inherit': false,
                'inheritable': true,
                'left': [this, this._onLeftOrUp, null],
                'right': [this, this._onRightOrDown, null],
                'up': [this, this._onRightOrDown, null]
            };
            stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
        },

        // Unregisters the 'anchor' navtype from stdnav.  This must be done
        // before deactivating/unloading stdnav.
        deactivate: function () {
            stdnav.unregisterNavtype('anchor', this.behavior);
        },

        // This callback is run when the page is initially rendered.  Add the
        // appropriate ARIA tags for the handled construct, if they do not already
        // exist.  The element passed will be the superfocus for the construct
        // being instrumented, but this construct may not actually have focus at
        // the time this function is called.
        _ariaPrep: function (el) {
            $(el).attr('role', 'link');
            /*
            var label = $(el).attr('aria-label');
            if (stdnav.nullOrUndefined(label)) {
                $(el).attr('aria-label', $(el).text());
            }
            */
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
            $(el).attr('role', 'link');
            /*
            var label = $(el).attr('aria-label');
            if (stdnav.nullOrUndefined(label)) {
                $(el).attr('aria-label', $(el).text());
            }
            */
            return null;
        },

        // Utility function: given a list, ensure the most appropriate element
        // available (in this case, the top-level anchor node, only) has focus.
        _fixSubfocus: function (element) {
            var ghosts;
            var newSubfocus;
            var $el = $(element);

            if ($el.is('A')) {
                newSubfocus = $el;
            } else {
                newSubfocus = $(element).closest('A');
                if (newSubfocus === undefined) {
                    // Safety
                    return undefined;
                }
            }

            // Nothing in or under this node should have ghostfocus; clear anything
            // that does, and ensure that only the anchor element itself has subfocus.
            newSubfocus.find('.ghostfocus').removeClass('.ghostfocus');
            newSubfocus.children().find('.subfocus').removeClass('.subfocus');
            return newSubfocus;
        },

        /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */

        _onFocusIn: function (element) {
            var newSubfocus;
            // This should fire on the list itself, not the item.
            var ghosts = $(element).children('.ghostfocus');
            if (ghosts.length > 0) {
                ghosts.removeClass('ghostfocus');
                // Promote ghost.       
                newSubfocus = this._fixSubfocus(ghosts[0]);
            } else {
                // Set subfocus to first item, without firing callbacks.
                var items = $(element).children('li[js-navigable!="false"]');
                if (items.length > 0) {
                    newSubfocus = this._fixSubfocus(items[0]);
                } else {
                    // For visual reasons, in the case of an empty list, we give up and
                    // set subfocus to the list itself.
                    newSubfocus = element;
                }
            }
            return newSubfocus;
        },

        _onLeftOrUp: function(element) {
            var $prev=$(element).prev('a');
            if ($prev.length<1){
                return element;
            }
            return $prev[0];
        },

        _onRightOrDown: function(element) {
            var $next=$(element).next('a');
            if ($next.length<1){
                return element;
            }
            return $next[0];
        },

        //FIXME: Do not set ghostfocus on focus out

        _onSubfocusIn: function (element) {
            if ($(element).prop('nodeName') != 'A') {
                // Find a usable child element.
                var subel = this._fixSubfocus(element);
                // Adjust subfocus without firing callbacks.
                stdnav.setSubfocus(subel, false);
            }
        },

        // When activated as a member of a navigable control, such as a list or
        // table, this is a cascade event from the navigable control's "enter"
        // handler, which will cause our "entered" handler to fire.  However, if
        // the anchor is a simple link, such as an <a> tag embedded in a <p> block,
        // it will be a simple tabstop, which is therefore focusable.  If such a
        // link is focused, pressing ENTER will fire our "enter" handler.  The same
        // logic works for both cases, so this callback should be mapped to both
        // events. 
        _onEnterOrEntered: function (element) {
            // Activate the link.
            var $el = $(element);
            if ($el.is('a')) {
                // Fire the click handler
                eventAutomation.simulateClickSequence(element);
            }
            return element;
        }
    });

    // SECOND EXTENSION PASS - ATTRIBUTES
    // Hash members in this pass can reference functions from the last pass.
    $.extend(stdnavPluginAnchor.prototype, {
        // This is the name of the new navtype.  Each stdnav plugin must
        // define a unique name.
        navtype: 'anchor',

        // This arrary extends the tag-to-navtype map in stdnav.  If your
        // plugin should apply to all elements of a given type, add those
        // element tagnames, in lower case, to this array.  It is normally
        // empty, and the page templates simply set an appropriate
        // "data-navtype=" attribute to get the expected behavior.
        //
        // CASE SENSITIVE - USE UPPER-CASE!
        navtype_tags: ['A']
    });
    var newStdnavPluginAnchor = new stdnavPluginAnchor();
    return newStdnavPluginAnchor;
});