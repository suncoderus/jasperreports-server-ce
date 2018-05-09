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
 * Elements: TABLE
 * Navtype:  table (element default)
 *
 * Plugin for the "table" navtype.  This is the default behavior for normal
 * HTML tables.  See also the Grid and DynamicList plugins.
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

    var stdnavPluginTable = function () {
        gserial++;
        this.serial = gserial;
    };

    // FIRST EXTENSION PASS - FUNCTIONS
    // Provides forward-references for hashes in the second pass, so that
    // references in that second pass to functions declared here resolve
    // (since the entire pass is applied at once, they cannot be combined).
    $.extend(stdnavPluginTable.prototype, {
        zinit: function (selector) {
            logger.debug('stdnavPluginTable.init(' + selector + ")\n");
            return this;
        },

        // Registers the 'table' navtype with stdnav.  stdnav must be loaded and
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
                'mousedown': [stdnav, stdnav.basicMouseDown],
                'right': [this, this._onRight, null],
                'up': [this, this._onUp, null],
                'superfocusin': [stdnav, stdnav.basicSuperfocusIn, {
                    'maxdepth': 4,
                    'focusSelector': 'td',
                    'ghostfocus': false
                }],
                'superfocusout': [stdnav, stdnav.basicSuperfocusOut, {
                    'ghostfocus': false
                }]
            };
            stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
        },

        // Unregisters the 'table' navtype from stdnav.  This must be done
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
            var $table = $(el);
            $table.attr('role', 'application');
            var label = $table.attr('aria-label');
            var labelledBy = $table.attr('aria-labelledby');
            var $items = $table.find('td,th');
            var allItemsAreLinks = false;
            if (stdnav.nullOrUndefined(label) && stdnav.nullOrUndefined(labelledBy)) {
                // FIXME: i18n
                var allLinks = $items.find('a');
                if (allLinks.length === $items.length) {
                    $table.attr('aria-label', 'Table of ' + $items.length + ' links.');
                    allItemsAreLinks = true;
                } else {
                    $table.attr('aria-label', 'Table of ' + $items.length + ' cells.');
                }
            }
            // Give the table cells the roles and labels of their enclosed text.
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
                        itemLabel = itemText;
                        /*  Turns out this is not helpful.
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

        // Given a reference element anywhere within the table, work back to
        // the table's root, then determine which element has subfocus, and
        // return it.  Note that it is possible no element in the table has
        // subfocus, and that the table itself does not have focus.  In this
        // case, the function returns undefined.
        _findSubfocus: function (el) {
            var table = $(el).closest('table');
            var sfels = table.find('.subfocus');
            if (sfels !== undefined) {
                return $(sfels[0]);
            }
        },

        // THIS FUNCTION MUST NOT WRAP - IT IS USED IN INTERNAL SEARCH LOGIC!
        _getPreviousSection: function (element) {
            var thisSection = $(element).closest('thead,tbody,tfoot');
            var prevSection;
            if (thisSection === undefined) {
                return undefined;
            }
            switch (thisSection.prop('nodeType')) {
            case 'TFOOT':
                prevSection = thisSection.parent().children('TBODY');
                break;
            case 'TBODY':
                prevSection = thisSection.parent().children('THEAD');
                break;
            case 'THEAD':
                break;
            default:
            }
            if ((prevSection !== undefined) && (prevSection.length > 0)) {
                return prevSection[0];
            }
            return undefined;
        },

        // THIS FUNCTION MUST NOT WRAP - IT IS USED IN INTERNAL SEARCH LOGIC!
        _getNextSection: function (element) {
            var thisSection = $(element).closest('thead,tbody,tfoot');
            var nextSection;
            if (thisSection === undefined) {
                return undefined;
            }
            switch (thisSection.prop('nodeType')) {
            case 'THEAD':
                nextSection = thisSection.parent().children('TBODY');
                break;
            case 'TBODY':
                nextSection = thisSection.parent().children('TFOOT');
                break;
            case 'TFOOT':
                break;
            default:
            }
            if ((nextSection !== undefined) && (nextSection.length > 0)) {
                return nextSection[0];
            }
            return undefined;
        },

        // Superfocus adjustment callback.  Given an element somewhere within a
        // within a table, identify the TABLE element that should become the
        // superfocus.
        _fixSuperfocus: function (element) {
            var newSuperfocus;
            var $closestTable = $(element).closest('table')
            if ($closestTable.length > 0) {
                newSuperfocus = $closestTable[0];
            } else {
                // FAULT, let StdNav fall back to BODY
                newSuperfocus = null;
            }
            return newSuperfocus;
        },

        // Utility function: given a <table>, <tr>, <td>, <tbody>, <thead>,
        // <tfoot>, or <caption> element, ensure that the most appropriate element
        // available (a <td>, if possible) has focus.
        _fixFocus: function (element) {
            var newFocus;
            //FIXME: Re-add js-navigable==false support
            // NORMAL CASES
            // Start with the most efficient cases and work back.
            switch ($(element).prop('nodeName')) {
            case 'TH':
                // Table column header.  Focusable, primarily to allow screen
                // reader (text-to-speech) navigation.
            case 'TD':
                // Table data cell.  Focusable.
                newFocus = element;
                break;

            case 'TR':
                // Table row.  Not focusable.
                // Look through the immediate children only, for a cell with ghostfocus set.
                newFocus = stdnav.closestDescendant(element, 'td,th .ghostfocus', null, 1);
                if (newFocus === undefined) {
                    // Failing that, find the first cell whatsoever.
                    newFocus = stdnav.closestDescendant(element, 'td,th', null, 1);
                    if (newFocus === undefined) {
                        // Row has no cells; try to give focus to the previous row.
                        newFocus = $(element).prev('tr');
                        if (newFocus === undefined) {
                            // No prior row.  Try again with the parent element.
                            newFocus = this._fixFocus($(element).parent());
                        }
                    }
                }
                break;

                // Table sections.  Not subfocusable.  Not used by all tables, and
                // tables that use any one may not necessarily use the other two.
                // It is important to remember that these sections scroll
                // independently of each other.
            case 'THEAD':
            case 'TBODY':
            case 'TFOOT':
                // Assuming all immediate children are table row ("TR") elements,
                // look for grandchildren which are either cells or table headers.
                newFocus = stdnav.closestDescendant(element, 'td,th .ghostfocus', null, 2);
                if (newFocus === undefined) {
                    newFocus = stdnav.closestDescendant(element, 'td,th', null, 2);
                    if (newFocus === undefined) {
                        // Section seems to be empty of cells.  Try again with the
                        // next section, if one is available.  If not, try again with
                        // the table itself.
                        var nextSection = this._getNextSection(element);
                        if (nextSection===undefined) {
                            newFocus = this._fixFocus($(element).closest('table'));
                        } else {
                            newFocus = this._fixFocus(nextSection);
                        }
                    }
                }
                break;

            case 'COLGROUP':
            case 'COL':
                // These styles are used for formatting only.  If they somehow get
                // subfocus, let the table logic select something more appropriate.
                newFocus = this._fixFocus($(element).closest('table'));
                break;

            case 'CAPTION':
                // Label/explanatory text above the table.  Screen-readers will
                // use this to describe the table, so it does not need to be
                // navigable.  If subfocused, redirect to table logic.
                newFocus = this._fixFocus($(element).closest('table'));
                break;

            case 'TABLE':
                // Look for descendants which are either cells or table headers.
                newFocus = stdnav.closestDescendant(element, 'td,th .ghostfocus', null, 5);
                if (newFocus === undefined) {
                    newFocus = stdnav.closestDescendant(element, 'td,th', null, 5);
                    if (newFocus === undefined) {
                        newFocus = element;
                    }
                }
                break;

            default:
                // This can happen if the data cells contains SPANs, etc.
                newFocus = this._fixFocus($(element).closest('td,th,table'));
            }
            return newFocus;
        },

        /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */

        _onSubfocusIn: function (element) {
            // Handle tables hosted in non-tab-focusable controls (such as a cell
            // in a parent table).
            var fixedSubfocus = element;
            if ($(element).is('td') === false) {
                // Find a usable child cell.
                fixedSubfocus = this._fixSubfocus($(element));
                // Adjust subfocus without firing callbacks.
                stdnav.setSubfocus(fixedSubfocus, false);
            }
            // Chain the basic handler.
            $.call(this, stdnav.basicSubfocusIn, fixedSubfocus);
        },

        _onLeft: function (element) {
            var sfel = this._findSubfocus(element);
            var newsf = $(false);
            if (sfel.is('td')) {
                newsf = sfel.prev('td');
            }
            if (newsf.length === 1) {
                return newsf;
            }
            return element;
        },

        _onRight: function (element) {
            var sfel = this._findSubfocus(element);
            var newsf = $(false);
            if (sfel.is('td')) {
                newsf = sfel.next('td');
            }
            if (newsf.length === 1) {
                return newsf;
            }
            return element;
        },

        _onUp: function (element) {
            var sfel = this._findSubfocus(element);
            var row = $(false);
            var newrow = $(false);
            var newsf = $(false);
            if (sfel.is('td')) {
                row = sfel.closest('tr');
                if (row.length === 1) {
                    newrow = row.prev('tr');
                }
                if (newrow.length == 1) {
                    // Determine the column position of the current cell and move
                    // to that position on the new row.
                    // FIXME: Handle merged/spanned/split cells properly by using
                    // data-stdnav-unmerged-offset
                    var iter = sfel;
                    var col = -1;
                    do {
                        col++;
                        iter = $(iter.prev('td')[0]);
                    }
                    while (iter.length > 0);
                    newsf = $(newrow.find('td')[0]);
                    while (col > 0) {
                        iter = newsf.next('td');
                        if (iter.length > 0) {
                            newsf = $(iter[0]);
                        }
                        col--;
                    }
                }
            }
            if (newsf.length === 1) {
                return newsf;
            }
            return element;
        },

        _onDown: function (element) {
            var sfel = this._findSubfocus(element);
            var row = $(false);
            var newrow = $(false);
            var newsf = $(false);
            if (sfel.is('td')) {
                row = sfel.closest('tr');
                if (row.length > 0) {
                    newrow = row.next('tr');
                }
                if (newrow.length > 0) {
                    // Determine the column position of the current cell and move
                    // to that position on the new row.
                    // FIXME: Handle merged/spanned/split cells properly.
                    var iter = sfel;
                    var col = -1;
                    do {
                        col++;
                        iter = $(iter.prev('td')[0]);
                    }
                    while (iter.length > 0);
                    newsf = $(newrow.find('td')[0]);
                    while (col > 0) {
                        iter = newsf.next('td');
                        if (iter.length > 0) {
                            newsf = $(iter[0]);
                        }
                        col--;
                    }
                }
            }
            if (newsf.length === 1) {
                return newsf;
            }
            return element;
        }
    });

    // SECOND EXTENSION PASS - ATTRIBUTES
    // Hash members in this pass can reference functions from the last pass.
    $.extend(stdnavPluginTable.prototype, {
        // This is the name of the new navtype.  Each stdnav plugin must
        // define a unique name.
        navtype: 'table',

        // This arrary extends the tag-to-navtype map in stdnav.  If your
        // plugin should apply to all elements of a given type, add those
        // element tagnames, in lower case, to this array.  It is normally
        // empty, and the page templates simply set an appropriate
        // "data-navtype=" attribute to get the expected behavior.
        //
        // CASE SENSITIVE - USE UPPER-CASE!
        // Use only the ROOT tags, not the children.
        navtype_tags: ['TABLE']
    });

    var newStdnavPluginTable = new stdnavPluginTable();
    return newStdnavPluginTable;
});