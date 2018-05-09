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
 * Elements: (any suitable)
 * Navtype:  grid
 *
 * Plugin for the "grid" navtype.  This is used, primarily, for HTML5+CSS3
 * table-like "grids", composed of DIVs or similar elements.  To use this,
 * use display:table, display:table-row, and display:table-cell to style
 * your grid, give the table itself a tabindex of -1, but do not give the
 * rows or grid cell elements tab indicies.  Give the root element a navtype
 * of "grid" ('js-navtype="grid"').
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

    var stdnavPluginGrid = function () {
        gserial++;
        this.serial = gserial;
    };

    // FIRST EXTENSION PASS - FUNCTIONS
    // Provides forward-references for hashes in the second pass, so that
    // references in that second pass to functions declared here resolve
    // (since the entire pass is applied at once, they cannot be combined).
    $.extend(stdnavPluginGrid.prototype, {
        zinit: function (selector) {
            logger.debug('stdnavPluginGrid.init(' + selector + ")\n");
            return this;
        },

        // Registers the 'grid' navtype with stdnav.  stdnav must be loaded and
        // activated before this can be done.
        activate: function () {
            // This is the behaviour hash for the navtype.  These defaults pass
            // everything through to the browser, and are normally overridden
            // with $.extend based on specific tagnames and stdnav attributes.
            this.behavior = {
                'focusin': [this, stdnav.basicFocusIn, {
                    'maxdepth': 2,
                    'subfocusclass': 'gridcell'
                }],
                'focusout': [this, stdnav.ghostFocusOut, null],
                'subfocusin': [this, this._onSubfocusIn, null],
                'left': [this, this._onLeft, null],
                'right': [this, this._onRight, null],
                'up': [this, this._onUp, null],
                'down': [this, this._onDown, null],
                'hoverin': [this, this._onHover, null],
                'inherit': true,
                'inheritable': true
            };
            stdnav.registerNavtype(this.navtype, this.behavior, this.navtype_tags);
        },

        // Unregisters the 'grid' navtype from stdnav.  This must be done
        // before deactivating/unloading stdnav.
        deactivate: function () {
            stdnav.unregisterNavtype(this.navtype, this.behavior);
        },

        // Given a reference element anywhere within the grid, work back to
        // the grid's root, then determine which element has subfocus, and
        // return it.  Note that it is possible no element in the grid has
        // subfocus, and that the grid itself does not have focus.  In this
        // case, the function returns undefined.
        _findSubfocus: function (el) {
            var grid = $(el).closest('.grid');
            var sfels = grid.find('.subfocus');
            if (sfels !== undefined) {
                return $(sfels[0]);
            }
        },

        // Utility function: given a grid, gridrow, or gridcell, ensure that
        // the most appropriate element available (a gridcell, if possible)
        // has focus.
        _fixSubfocus: function (element) {
            // When the control gets focus, set the subfocus appropriately.
            // If ghostfocus is available in a grid cell, use that; if not,
            // use the first cell (normally the top left).
            var ghosts;
            var newSubfocus;
            if (element.hasClass('grid')) {
                // The usual case: the entire grid is gaininging focus.
                ghosts = element.children('.gridrow').children('.gridcell .ghostfocus');
                // If a cell itself has ghostfocus, swap that out for subfocus.
                if (ghosts.length > 0) {
                    newSubfocus = ghosts[0];
                } else {
                    // If no ghosts, select the first cell-- if there ARE any cells.
                    // Otherwise, select the first row, if there are any rows, and the
                    // grid itself if there are not.
                    var rows = element.children('.gridrow');
                    if (rows.length > 0) {
                        var cells = $(rows[0]).children('.gridcell');
                        if (cells.length > 0) {
                            newSubfocus = cells[0];
                        } else {
                            // The table has rows, but no cells-- set subfocus to the first row.
                            newSubfocus = rows[0];
                        }
                    } else {
                        // The entire grid is empty-- set subfocus to the root grid div.
                        newSubfocus = element;
                    }
                }
            } else if (element.hasClass('gridrow')) {
                // In this case, the grid rows are allowed tab indexes.  It isn't clear whether ghostfocus
                // will actually be helpful or annoying in this case, but it is implemented for now.
                ghosts = element.children('.gridcell .ghostfocus');
                newSubfocus = ghosts[0];
            } else if (element.hasClass('gridcell')) {
                // If grid cells are allowed tab indexes, they don't need (or benefit
                // from) ghost focus, so just set subfocus.
                newSubfocus = element;
            }
            return newSubfocus;
        },

        /* ========== NAVTYPE BEHAVIOR CALLBACKS =========== */

        _onSubfocusIn: function (element) {
            // Handle grids hosted in non-focusable controls (such as a cell in another grid).
            if ($(element).hasClass('gridcell') === false) {
                // Find a usable child cell.
                var subel = this._fixSubfocus($(element));
                // Adjust subfocus without firing callbacks.
                stdnav.setSubfocus(subel, false);
            }
        },

        _onLeft: function (element) {
            var sfel = this._findSubfocus(element);
            var newsf = $(false);

            if (sfel.hasClass('gridcell')) {
                newsf = sfel.prev('.gridcell');
            }

            if (newsf.length === 1) {
                stdnav.setSubfocus(newsf);
            }
            return false;
        },

        _onRight: function (element) {
            var sfel = this._findSubfocus(element);
            var newsf = $(false);

            if (sfel.hasClass('gridcell')) {
                newsf = sfel.next('.gridcell');
            }

            if (newsf.length === 1) {
                stdnav.setSubfocus(newsf);
            }
            return false;
        },

        _onUp: function (element) {
            var sfel = this._findSubfocus(element);
            var row = $(false);
            var newrow = $(false);
            var newsf = $(false);

            if (sfel.hasClass('gridcell')) {
                row = sfel.closest('.gridrow');
                if (row.length === 1) {
                    newrow = row.prev('.gridrow');
                }
                if (newrow.length === 1) {
                    // Determine the column position of the current cell and move
                    // to that position on the new row.
                    // FIXME: Handle merged/spanned/split cells properly by using
                    // data-stdnav-unmerged-offset
                    var iter = sfel;
                    var col = -1;
                    do {
                        col++;
                        iter = $(iter.prev('.gridcell')[0]);
                    }
                    while (iter.length > 0);

                    newsf = $(newrow.find('.gridcell')[0]);
                    while (col > 0) {
                        iter = newsf.next('.gridcell');
                        if (iter.length > 0) {
                            newsf = $(iter[0]);
                        }
                        col--;
                    }

                }
            }
            if (newsf.length === 1) {
                stdnav.setSubfocus(newsf);
            }
            return false;
        },

        _onDown: function (element) {
            var sfel = this._findSubfocus(element);
            var row = $(false);
            var newrow = $(false);
            var newsf = $(false);

            if (sfel.hasClass('gridcell')) {
                row = sfel.closest('.gridrow');
                if (row.length > 0) {
                    newrow = row.next('.gridrow');
                }
                if (newrow.length > 0) {
                    // Determine the column position of the current cell and move
                    // to that position on the new row.
                    // FIXME: Handle merged/spanned/split cells properly.
                    var iter = sfel;
                    var col = -1;
                    do {
                        col++;
                        iter = $(iter.prev('.gridcell')[0]);
                    }
                    while (iter.length > 0);

                    newsf = $(newrow.find('.gridcell')[0]);
                    while (col > 0) {
                        iter = newsf.next('.gridcell');
                        if (iter.length > 0) {
                            newsf = $(iter[0]);
                        }
                        col--;
                    }
                }
            }
            if (newsf.length === 1) {
                stdnav.setSubfocus(newsf);
            }
            return false;
        }
    });

    // SECOND EXTENSION PASS - ATTRIBUTES
    // Hash members in this pass can reference functions from the last pass.
    $.extend(stdnavPluginGrid.prototype, {
        // This is the name of the new navtype.  Each stdnav plugin must
        // define a unique name.
        navtype: 'grid',

        // This arrary extends the tag-to-navtype map in stdnav.  If your
        // plugin should apply to all elements of a given type, add those
        // element tagnames, in lower case, to this array.  It is normally
        // empty, and the page templates simply set an appropriate
        // "data-navtype=" attribute to get the expected behavior.
        //
        // CASE SENSITIVE - USE UPPER-CASE!
        navtype_tags: []
    });

    var newStdnavPluginGrid = new stdnavPluginGrid();
    return newStdnavPluginGrid;
});