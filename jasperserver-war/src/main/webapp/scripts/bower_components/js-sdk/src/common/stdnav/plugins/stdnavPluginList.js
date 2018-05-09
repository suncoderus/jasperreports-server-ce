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
 * Navtype:  list (element default)
 *
 * Plugin for the "list" navtype.  This is the default behavior for ordered
 * and unorderedlists ("ul" and "ol" tags, and their "li" children).
 * Horizontal lists are supported if the "horizontal" CSS class is specified.
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
        version = "0.0.1",
        singleton = null,
        gserial = 0;

    // Local object definition.

    var stdnavPluginList = function () {
        gserial++;
        this.serial = gserial;
    };

    // FIRST EXTENSION PASS - FUNCTIONS
    // Provides forward-references for hashes in the second pass, so that
    // references in that second pass to functions declared here resolve
    // (since the entire pass is applied at once, they cannot be combined).
    $.extend(stdnavPluginList.prototype, {
        zinit: function (selector) {
            logger.debug('stdnavPluginList.init(' + selector + ")\n");
            return this;
        },

        // Registers the 'list' navtype with stdnav.  stdnav must be loaded and
        // activated before this can be done.
        activate: function () {
            // This is the behaviour hash for the navtype.  These defaults pass
            // everything through to the browser, and are normally overridden
            // with $.extend based on specific tagnames and stdnav attributes.
            this.behavior = {
                'ariaprep': [this, this._ariaPrep, null],
                'ariarefresh': [this, this._ariaRefresh, null],
                'down': [this, this._onDown, null],
                'fixfocus': [this, this._fixFocus, null],
                'fixsuperfocus': [this, this._fixSuperfocus, null],
                'inherit': true,
                'inheritable': true,
                'left': [this, this._onLeft, null],
                'right': [this, this._onRight, null],
                'up': [this, this._onUp, null],
                'superfocusin': [stdnav, stdnav.basicSuperfocusIn, {
                    'maxdepth': 1,
                    'focusSelector': 'li',
                    'ghostfocus': false
                }],
                'superfocusout': [stdnav, stdnav.basicSuperfocusOut, {
                    'ghostfocus': false
                }]
            };
            stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
        },

        // Unregisters the 'list' navtype from stdnav.  This must be done
        // before deactivating/unloading stdnav.
        deactivate: function () {
            stdnav.unregisterNavtype('list', this.behavior);
        },

        // This callback is run when the page is initially rendered.  Add the
        // appropriate ARIA tags for the handled construt, if they do not already
        // exist.  The element passed will be the superfocus for the construct
        // being instrumented, but this construct may not actually have focus at
        // the time this function is called.
        _ariaPrep: function (el) {
            this._ariaRefresh(el);
        },

        // This callback is run when the superfocus changes to the construct.
        // Its two purposes are to update existing constructs, and to handle
        // dynamically-created content whose creation was not detected during
        // initial page construction (possibly because no part of the construct
        // existed yet).  The element passed will be the superfocus for the
        // construct, but this construct may not actually have focus at the time
        // this function is called.
        _ariaRefresh: function (el) {
            var $list = $(el);
            $list.attr('role', 'application');
            //$list.attr('role', 'list');
            var label = $list.attr('aria-label');
            var labelledBy = $list.attr('aria-labelledby');
            var $items = $list.children('li');
            var itemPlural=$list.attr('js-itemplural');
            if ((itemPlural==="")||(!itemPlural)){
                itemPlural="items";
            }
            var allItemsAreLinks = false;
            if (stdnav.nullOrUndefined(label) && stdnav.nullOrUndefined(labelledBy)) {
                // FIXME: i18n
                var allLinks = $items.find('a');
                if (allLinks.length === $items.length) {
                    $list.attr('aria-label', 'List of ' + $items.length + ' links.');
                    allItemsAreLinks = true;
                } else {
                    $list.attr('aria-label', 'List of ' + $items.length + itemPlural);
                }
            }
            // Give the list items the roles and labels of their enclosed text.
            $.each($items, function (index, item) {
                // See if the item includes a link
                var $item = $(item);
                var $itemLinks = $item.find('a');
                if ($itemLinks.length > 0) {
                    $item.attr('role', 'link');
                    // FIXME: i18n
                    var itemLabel = $item.attr('aria-label');
                    var itemLabelledBy = $item.attr('aria-labelledby');
                    if (stdnav.nullOrUndefined(itemLabel) && stdnav.nullOrUndefined(itemLabelledBy)) {
                        var itemText = $item.text();
                        var itemLinkText = $($itemLinks[0]).text();
                        itemLabel = itemText + ". " + (index+1) + " of " + $items.length + " " + itemPlural + ".";
                        /* It looks like this may not be needed.
                        if (!allItemsAreLinks) {
                            if (itemText == itemLinkText) {
                                // FIXME: i18n
                                itemLabel += ' link.';
                            } else {
                                itemLabel += ', linked to ' + itemLinkText + '.';
                            }
                        }
                        */
                        $item.attr('aria-label', itemLabel);
                    }
                }
            });
            return null;
        },

        // Focus adjustment callback.  Given an element somewhere within a sub-DOM
        // within a list, ensures that a list item (LI) is given focus.
        _fixFocus: function (element) {
            // When the list element itself (OL, UL) gets focus, translate the focus
            // appropriately.  If ghostfocus is available in a list item, use that;
            // if not, use the first item (normally the top).
            var ghosts;
            var newFocus;
            var $el = $(element);

            if ($el.is("fieldset") && $el.children("ul.inputSet").length) {
                return element;
            }

            if ($el.is('ul,ol')) {
                // The usual case: the entire list is gaining focus.
                ghosts = $el.children('li .ghostfocus');
                // If a list item itself has ghostfocus, swap that out for focus.
                if (ghosts.length > 0) {
                    newFocus = ghosts[0];
                } else {
                    // If no ghosts, select the first list item-- if there ARE any.
                    // Otherwise, select the list itself.
                    var items = $el.children('li');
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
                }
            }
            return newFocus;
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

        _onClick: function (element) {
            $(element).closest('ul, ol').focus();
            stdnav.setSubfocus(this._fixSubfocus(element));
        },

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
        },

        _onLeft: function (element) {
            var newFocus = element;
            var $list = $(element).closest('ul,ol');
            if ($list.hasClass('horizontal')) {
                var $newFocus = $(element).prev('li');
                if ($newFocus.length === 0) {
                    if ($list.attr('js-stdnav-wrap') == 'wrap') {
                        var $items = $list.children('li');
                        if ($items.length > 0) {
                            newFocus = $items[$items.length - 1];
                        }
                    }
                } else {
                    newFocus = $newFocus[0];
                }
            }
            return newFocus;
        },

        _onRight: function (element) {
            var newFocus = element;
            var $list = $(element).closest('ul,ol');
            if ($list.hasClass('horizontal')) {
                var $newFocus = $(element).next('li');
                if ($newFocus.length === 0) {
                    if ($list.attr('js-stdnav-wrap') == 'wrap') {
                        var $items = $list.children('li');
                        if ($items.length > 0) {
                            newFocus = $items[0];
                        }
                    }
                } else {
                    newFocus = $newFocus[0];
                }
            }
            return newFocus;
        },

        _onUp: function (element) {
            var newFocus = element;
            var $list = $(element).closest('ul,ol');
            if (!$list.hasClass('horizontal')) {
                var $newFocus = $(element).prev('li');
                if ($newFocus.length === 0) {
                    if ($list.attr('js-stdnav-wrap') == 'wrap') {
                        var $items = $list.children('li');
                        if ($items.length > 0) {
                            newFocus = $items[$items.length - 1];
                        }
                    }
                } else {
                    newFocus = $newFocus[0];
                }
            }
            return newFocus;
        },

        _onDown: function (element) {
            var newFocus = element;
            var $list = $(element).closest('ul,ol');
            if (!$list.hasClass('horizontal')) {
                var $newFocus = $(element).next('li');
                if ($newFocus.length === 0) {
                    if ($list.attr('js-stdnav-wrap') == 'wrap') {
                        var $items = $list.children('li');
                        if ($items.length > 0) {
                            newFocus = $items[0];
                        }
                    }
                } else {
                    newFocus = $newFocus[0];
                }
            }
            return newFocus;
        }
    });

    // SECOND EXTENSION PASS - ATTRIBUTES
    // Hash members in this pass can reference functions from the last pass.
    $.extend(stdnavPluginList.prototype, {
        // This is the name of the new navtype.  Each stdnav plugin must
        // define a unique name.
        navtype: 'list',

        // This arrary extends the tag-to-navtype map in stdnav.  If your
        // plugin should apply to all elements of a given type, add those
        // element tagnames, in lower case, to this array.  It is normally
        // empty, and the page templates simply set an appropriate
        // "data-navtype=" attribute to get the expected behavior.
        //
        // CASE SENSITIVE - USE UPPER-CASE!
        // Use only the ROOT tags, not the children.
        navtype_tags: ['UL', 'OL']
    });
    var newStdnavPluginList = new stdnavPluginList();
    return newStdnavPluginList;
});