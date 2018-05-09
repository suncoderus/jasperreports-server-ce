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

/* Standard Navigation library (stdnav)
 * ------------------------------------
 * This library provides keyboard and screen-reader support for areas of the
 * application that do not explicitly enable that support.  It attempts to
 * stay out of the way of areas that already handle it properly, and can be
 * disabled in problem areas by adding 'js-stdnav="false"' to any element
 * that contains the area.
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
        browserDetection = require("common/util/browserDetection"),
        logger = require("logger").register(module),
        version = "0.0.1",
        singleton = null,
        stdnav_gserial = 0;

    // Local object definition.
    // The top-level stdnav object is a function.  You can get the Effective
    // Navigation Topology for a given element be passing in a jQuery selector,
    // then use that to check what a particular key will do when that element
    // focus/subfocus, like this:
    // var onLeft = stdnav("#HomeButton")[stdnav-left];
    // ...or change it, like this:
    // stdnav("#HomeButton")[stdnav-left]="#";
    // (This example disables the "left arrow" key on the HomeButton element,
    // by pointing it to "#", or "self".)
    //
    // If you call it with no parameters, you interact with the library instance
    // itself.  For example, the critical:
    // stdnav.activate();

    var stdnav = function () {
        //logger.debug('JSAM('+selector+")\n");
        stdnav_gserial++;
        this.serial = stdnav_gserial;
        this.menuItemCallbacks = {
            click: {}
        };

        // This hash stores navtype plugins.  Look up the plugin objects by the
        // navtype string, if you need to tweak one for some reason.
        // For example: var gridPluginObj = root.stdnav.plugins['grid'];
        this.plugins = {};

        // True if a modal dialog seems to be active (specifically, if
        // stdnav.beginModalFocus was called).
        this.modalDialogActive=false;

        // If a modal dialog seems to be active, this is the DOM object of its
        // root element.  Otherwise, this should be null.
        this.modalDialogRoot=null;

        // If set, focus debugging data will be formatted into this element,
        // when it exists, as a simple string.  The element will be updated as
        // focus changes.  If left null, the system is disabled.
        this.debugElementID=null;
        //this.debugElementID='copyright';

        // Number of non-blocking spaces to increment the delta sink area with;
        // these are used to convince Internet Explorer its rendering cache
        // (and therefore its tab order) are out of date and force it to
        // recompute them.
        this.chaffLength=1;

        // Maximum length of the rendering cache countermeasure string.
        this.maxChaffLength=8;

    };

    $.extend(stdnav.prototype, {
        // ===LIMITS AND SAFETY BAILOUTS=======================================

        // Prevents runaway recursion in case of a jQuery filter bug or other
        // DOM search failure.  Keep this value significantly higher than your
        // maximum expected DOM depth.
        _maxNavDepth: 100,

        // ===GENERAL-PURPOSE PUBLIC UTILITY FUNCTIONS=========================
        // (NOTE: MOST OF THESE SHOULD BE MOVED TO JQUERY PLUGINS, UTILS, ETC.)

        nullOrUndefined: function (koan) {
            if ((koan === null) || (typeof (koan) === 'undefined')) {
                return true;
            }
            return false;
        },

        // Like "$.closest" but with an additional barrier filter and a maximum
        // number of levels.  All ancestors will be searched unless they match
        // the optional jQuery selector "barrier", in which case the search
        // stops and returns undefined.  If maxdepth is specified and exceeded,
        // the search stops and returns undefined.  If maxdepth is zero, only
        // the start is searched.  If a match is found, it is returned
        // immediately.  If no match is found all the way to the root element,
        // the function returns undefined.
        // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
        closestAncestor: function (start, target, barrier, maxdepth) {
            if ((start === undefined) || (start === null)) {
                // FAILURE, BAD START
                return undefined;
            }
            var iterator = $(start).parent();
            var depth = 1;
            while (iterator !== undefined) {
                if ((maxdepth !== null) && (maxdepth !== undefined) && (depth >= maxdepth)) {
                    // FAILURE, RECURSION LIMIT EXCEEDED
                    return undefined;
                }
                if (iterator.is(barrier)) {
                    // FAILURE, BARRIER DETECTED
                    return undefined;
                }
                if (iterator.is(target)) {
                    // SUCCESS
                    return iterator;
                }
                depth++;
                iterator = iterator.parent();
            }
            // FAILURE, NO ANCESTOR MATCHED CRITERIA
            return undefined;
        },

        // Like "$.closest" but working down.  Returns the first instance of
        // an element matching the jQuery selector "target" at or beneath the
        // element passed as "start".  All children will be recursively searched
        // unless they match the optional jQuery selector "barrier".  If "barrier"
        // is omitted, null, or undefined, no filtering is performed.  An optional
        // maximum depth can be specified as "maxdepth"; a value of 0 will search
        // nothing, because the starting point is not a solution candidate.
        // Negative values will abort immediately.  If this parameter is omitted,
        // null, or undefined, a library default is used.
        // TIPS:
        // - Keep in mind that either selector can use OR or NOT specifiers as
        //   usual for jQuery.
        // - "start" is never considered a match.
        // - If a node matches the target AND the barrier, it will still be
        //   returned.  The barrier prevents enumeration of children only, which
        //   is why it is named "barrier" and not "filter".
        // - If no candidate is found, the function returns undefined.
        // - This uses a breadth-first search.
        // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
        closestDescendant: function (start, target, barrier, maxdepth) {
            if ((start === null) || (start === undefined) || (target === null) || (target === undefined)) {
                // Parameters cannot result in a match.
                return undefined;
            }

            var effective_maxdepth;
            if ((maxdepth === null) || (maxdepth === undefined)) {
                effective_maxdepth = this._maxNavDepth;
            } else {
                effective_maxdepth = maxdepth;
            }

            var search_queue = [];

            // Perform a breadth-first search with the "start" element as the root.
            // Each iteration of this loop is a different absolute depth level,
            // relative to "start".

            // FIXME - This algorithm uses a naive queue because the depth is not
            // expected to be large.  Replace with a higher-performance queue if
            // one is available, or change this to TODO if no higher-performance
            // queue is available.

            var depth = 0,
                items_at_current_depth = 1,
                items_at_next_depth = 0,
                iterator = start;
            while (iterator !== undefined) {
                // See if the iterator is the element we're looking for.
                if ($(iterator).is(target) && (depth > 0)) {
                    // Success!
                    return iterator;
                }

                // See if its children should be enqueued for search.
                if (!($(iterator).is(barrier))) {
                    var children = $(iterator).children();
                    children.each(function (index, child) {
                        // Enqueue children for search after this level is completed.
                        search_queue.unshift(child);
                        items_at_next_depth++;
                    });
                }

                // See if this was our last element at this depth (our next dequeue
                // will be the first one at a lower depth), and swap tracking values
                // if it is.
                if (--items_at_current_depth === 0) {
                    depth++;
                    items_at_current_depth = items_at_next_depth;
                    items_at_next_depth = 0;
                }
                if (depth > effective_maxdepth) {
                    // Maximum depth exceeded, no match.
                    return undefined;
                }
                // Get the next item to be checked.
                iterator = search_queue.pop();
            }
            // Search space exhausted, no match.
            return undefined;
        },

        zinit: function (selector) {
            logger.debug('stdnav.init(' + selector + ")\n");
            return this;
        },

        // This is the node-to-navtype map.  This is used to determine the
        // default navtype for a given element.  Since all elements are
        // supported by plugins, this map is initially empty.
        navtype_nodeNames: {},

        // ===INTERNAL DEBUGGING FUNCTIONS=====================================
        updateDebugInfo: function(){
            if (!this.debugElementID){
                return;
            }
            // DO NOT CACHE THIS
            var $dbg=$('#'+this.debugElementID),
                $super=$('.superfocus'),
                $focus=$(document.activeElement),
                $sub=$('.subfocus'),
                txt='[sup:';
            if ($dbg.length<1){
                return;
            }
            txt+=$super.length;
            if ($super.length>0){
                txt+=':'+$super[0].nodeName+'#'+$super[0].id;
            } else {
                txt+='?';
            }
            txt+=', foc:';
            txt+=$focus.length;
            if ($focus.length>0){
                txt+=':'+$focus[0].nodeName+'#'+$focus[0].id;
            } else {
                txt+='?';
            }
            txt+=', sub:';
            txt+=$sub.length;
            if ($sub.length>0){
                txt+=':'+$sub[0].nodeName+'#'+$sub[0].id;
            } else {
                txt+='?';
            }

            // Priors
            // DO NOT CACHE THIS
            $super=$(this._priorSuperfocus),
            $focus=$(this._priorFocus),
            $sub=$(this._priorSubfocus);
            txt+=' :: psup:';
            if ($dbg.length<1){
                return;
            }
            txt+=$super.length;
            if ($super.length>0){
                txt+=':'+$super[0].nodeName+'#'+$super[0].id;
            } else {
                txt+='?';
            }
            txt+=', pfoc:';
            txt+=$focus.length;
            if ($focus.length>0){
                txt+=':'+$focus[0].nodeName+'#'+$focus[0].id;
            } else {
                txt+='?';
            }
            txt+=', psub:';
            txt+=$sub.length;
            if ($sub.length>0){
                txt+=':'+$sub[0].nodeName+'#'+$sub[0].id;
            } else {
                txt+='?';
            }

            txt+=']';
            $dbg.text(txt);
        },

        // ===LIBRARY-SPECIFIC PUBLIC UTILITY FUNCTIONS========================

        // Suspends the focusability of all elements in the DOM which do not
        // have the element provided as a parent, except for the element itself.
        // TODO: Find a faster way to do this!
        beginModalFocus: function(element){
            if (!element){
                return;
            }

            // If a modal dialog was already active, assume it has been
            // superceded by this one.  This should also handle the case
            // where two different "threads" pop up the same dialog.
            if (this.modalDialogActive===true){
                this.endModalFocus(this.modalDialogRoot);
            }

            var $el=$(element);
            var $nonDialog=$('body *').filter(function(){
                return (!($.contains(element, this)||($el.is(this))));
            });
            this.modalDialogActive=true;
            this.modalDialogRoot=element;
            $nonDialog.each(function(index, ndEl){
                var $ndEl=$(ndEl);
                var priorTabIndex=$ndEl.attr('tabIndex');
                if (typeof(priorTabIndex)==='undefined'){
                    $ndEl.attr('js-nonmodal-tabindex', 'undefined');
                    $ndEl.attr('tabIndex', '-1');
                }else{
                    $ndEl.attr('js-nonmodal-tabindex', priorTabIndex);
                    $ndEl.attr('tabIndex', '-1');
                }
            });
        },

        // Resumes the focusability of all elements in the DOM which do not
        // have the element provided as a parent.
        // TODO: Find a faster way to do this!
        endModalFocus: function(element){
            var $el, $nonDialog;
            if (!element) {
                // Try to allow failsafe tabindex restoration.
                $el=$(); // (Returns an empty jQuery set.)
                $nonDialog=$('body *');
            } else {
                $el=$(element);
                $nonDialog=$('body *').filter(function(){
                    return (!($.contains($el, this)||($el.is(this))));
                });
            }
            $nonDialog.each(function(index, ndEl){
                var $ndEl=$(ndEl);
                var priorTabIndex=$ndEl.attr('js-nonmodal-tabindex');
                if (typeof(priorTabIndex)==='undefined'){
                    // Shouldn't happen, but it might.  Implies content was
                    // added to the non-modal DOM sections WHILE the modal
                    // subDOM was being shown.  For now, do nothing.
                }
                else if (priorTabIndex==='undefined'){
                    $ndEl.removeAttr('js-nonmodal-tabindex');
                    $ndEl.removeAttr('tabIndex');
                }else{
                    $ndEl.removeAttr('js-nonmodal-tabindex');
                    $ndEl.attr('tabIndex', priorTabIndex);
                }
            });
            this.modalDialogActive=false;
            this.modalDialogRoot=null;
        },

        // Returns true if the element is navigable.
        isNavigable: function (element) {
            if ($(element).is(this._navigableFilter) && (!$(element).is(this._unnavigableFilter))) {
                return true;
            }
            return false;
        },

        // Returns the closest parent or further ancestor that is navigable, or
        // undefined if the element is the shallowest navigable ancestor.
        // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
        closestNavigableAncestor: function (element) {
            return this.closestAncestor(element, this._navigableFilter, this._unnavigableFilter, this._maxNavDepth);
        },

        // Returns the closest child or further descendant that is navigable,
        // or undefined if no such descendant exists.
        // NOTE THAT THIS FUNCTION RETURNS A jQuery-WRAPPED ELEMENT.
        closestNavigableDescendant: function (element) {
            return this.closestDescendant(element, this._navigableFilter, this._unnavigableFilter, this._maxNavDepth);
        },

        // ===PLUGIN MANAGEMENT FUNCTIONS======================================

        // Build the jQuery filters used to rapidly locate navigable elements.
        // This must be redone when plugins are registered.
        _rebuildNavigationFilters: function () {
            // Rebuild the jQuery filter used to determine if a component is
            // navigable.  This is a logical OR of all known nodeNames as well as
            // anything with a js-navtype explicitly set.  This whitelist is
            // applied first.
            var newFilter = '';
            $.each(this.navtype_nodeNames, function (navtype, nodeNames) {
                $.each(nodeNames, function (index, nodeName) {
                    newFilter += nodeName + ',';
                });
            });
            // Note that an element with js-navtype specified is still considered
            // unnavigable if no loaded plugin provides that navtype.  However, for
            // performance reasons, that check is only performed if we actually
            // have a candidate element.
            newFilter += '[js-navtype]';
            this._navigableFilter = newFilter;

            // Elements matching this filter are never navigable under any
            // circumstances.  This blacklist is applied last.
            this._unnavigableFilter = '[js-stdnav="false"],[js-navtype="none"]';
        },

        // Registers a new plugin.  Must be called by plugins in their AMD
        // loading sequence, NOT in their Activate methods.
        registerPlugin: function (name, plugin) {
            this.plugins[name] = plugin;
        },

        // Unregisters a plugin.  Only useful if you need to free the memory.
        unregisterPlugin: function (name) {
            this.plugins[name].unload();
            delete this.plugins[name];
        },

        // Registers a new navtype.  Intended for use by UI control modules,
        // such as JSAM (Jaspersoft Accessible Menus).  May be called at any
        // time, but it's probably wise to register all navtypes before
        // calling activate().
        //
        // Parameters:
        // navtype:    A string describing the new navigation type.
        //             (NOTE: navtypes are NOT case-sensitive)
        // behaviors:  An object mapping one or more of the nav actions to
        //             new callbacks, targets, or undefined.  Note that the
        //             value of "this" for these callbacks will be the
        //             stdnav object.
        // nodeNames:  An optional array of HTML node names (element types)
        //             that intrinsically imply the navtype.  Elements of this
        //             type will not require a "js-navtype" attribute to leverage
        //             the plugin's behavior; it becomes implied by default.
        registerNavtype: function (navtype, behaviors, nodeNames) {
            this.navtypeBehaviors[navtype.toLowerCase()] = behaviors;
            this.navtype_nodeNames[navtype.toLowerCase()] = nodeNames;
            this._rebuildNavigationFilters();
        },

        // Unregisters the navtype.  May be done at any time, for example, to
        // allow a user preference "Apply" to change navigation handlers.
        unregisterNavtype: function (navtype, behaviors) {
            delete this.navtypeBehaviors[navtype.toLowerCase()];
            delete this.navtype_nodeNames[navtype.toLowerCase()];
            this._rebuildNavigationFilters();
        },

        // ===PRIMARY ACTIVATION AND INITIALIZATION FUNCTIONS==================

        // Loads the library and initializes it.  After calling this function,
        // you may register plugins dynamically at any time.  When possible,
        // however, plugins should be loaded immediately after calling activate,
        // and before calling stdnav.start().  Calling stdnav.start() is still
        // required to initialize ARIA Live Regions by setting body[aria-busy] to
        // false.
        activate: function () {
            // This is the null behavior hash.  Its purpose is only to define valid
            // keys for the hash, but it is also used in some optimized overlays.
            // Any behavior keys used anywhere else MUST exist here.
            this.nullBehavior = {
                'ariaprep': null,
                'ariarefresh': null,
                'click': null,
                'down': null,
                'end': null,
                'enter': null,
                'entered': null,
                'exit': null,
                'fixfocus': null,
                'fixsubfocus': null,
                'fixsuperfocus': null,
                'focusin': null,
                'focusout': null,
                'home': null,
                'hoverin': null,
                'hoverout': null,
                'inherit': null,
                'inheritable': null,
                'left': null,
                'mousedown': null,
                'mouseover': null,
                'mouseout': null,
                'mouseup': null,
                'next': null,
                'pagedown': null,
                'pageup': null,
                'prev': null,
                'rejoined': null,
                'right': null,
                'subfocusin': null,
                'subfocusout': null,
                'superfocusin': null,
                'superfocusout': null,
                'toggle': null,
                'touchend': null,
                'touchstart': null,
                'up': null
            };

            // This is the root "default" behaviour hash.  These defaults pass
            // everything through to the browser, and are normally overridden
            // with $.extend based on specific node names and stdnav attributes.
            this.defaultBehavior = {
                //          'click': [this, this.basicClick, null],
                'enter': [this, this.basicEnter, null],
                'entered': [this, this.basicEntered, null],
                'exit': [this, this.basicExit, null],
                'fixfocus': [this, this.basicFixFocus, null],
                'fixsubfocus': [this, this.basicFixSubfocus, null],
                'fixsuperfocus': [this, this.basicFixSuperfocus, null],
                'focusin': [this, this.basicFocusIn, {
                    'maxdepth': 0
                }],
                'focusout': [this, this.basicFocusOut, null],
                // By default, overrides in parents should be used (unless those parents
                // have inheritable===false).
                'inherit': true,
                // By default, allow overrides to apply to any children with inherit===true.
                'inheritable': true,
                'rejoined': [this, this.basicRejoined, null],
                'subfocusin': [this, this.basicSubfocusIn, null],
                'subfocusout': [this, this.basicSubfocusOut, null],
                'superfocusin': [this, this.basicSuperfocusIn, null],
                'superfocusout': [this, this.basicSuperfocusOut, {
                    'ghostfocus': false
                }]
            };

            this.navtypeBehaviors = {
                'block': {},
                'global': {},
                'inline': {},
                'modal': {
                    'next': this._onModalNext,
                    'inherit': false,
                    'inheritable': true
                }
            };

            // Initialize state management
            this._refocusing = false;
            this._priorSuperfocus = null;
            this._priorFocus = null;
            this._priorSubfocus = null;
            /*
            this._currentSuperfocus = $('body')[0];
            this._currentSubfocus = $('body')[0];
            $(this._currentSuperfocus).addClass('superfocus');
            $(this._currentSubfocus).addClass('subfocus');
            */
            this._currentSuperfocus = document.activeElement;
            this._currentFocus = document.activeElement;
            this._currentSubfocus = document.activeElement;
            $(this._currentSuperfocus).addClass('superfocus');
            $(this._currentSubfocus).addClass('subfocus');

            // Build the jQuery filters used to rapidly locate navigable elements.
            // This must be redone when plugins are registered.
            this._rebuildNavigationFilters();

            this._bindFocusEvents();
            this._bindKeyboardEvents();
            this._bindMouseEvents();
            this._bindTouchEvents();
        },

        // Initializes ARIA Live Regions and performs final initial static
        // analysis, if any, prior to dynamic monitoring.  Finally, forces focus
        // to move to the LAST element found with the CSS class
        // "stdnavinitialfocus" (only one should be defined, however).
        // Call this function ONLY when the main DOM has been fully rendered
        // (it's okay if large, slow resources are not yet loaded).
        start: function () {
            // WARNING-- THIS IS *NOT* REDUNDANT WITH TABINDEX VALUES!
            // Do not change this without understanding the accessibility issues
            // involved.  It is here to force correct initial browser focus even
            // when screen readers are in use, due to screen reader bugs in ARIA
            // support.
            //
            // If the templates get the tab order correct (which cannot always
            // be done), the above code may not actually result in a focus
            // change.  This means the focus events may not be triggered, and
            // as a result, the superfocus and subfocus reevaluation
            // code will not run.  Therefore, force a focus change.
            var $initialFocus = $('.stdnavinitialfocus');
            if ($initialFocus.length<1) {
                if (document.activeElement && document.activeElement!=$('body')[0]) {
                    $initialFocus=$(document.activeElement);
                } else {
                    $initialFocus=$('body').children("[tabindex='0']:first");
                    if ($initialFocus.length<1){
                        // Give up
                        $initialFocus=$('body');
                    }
                }
            }
            // Hack for IE11, force a potential visual change
            if (browserDetection.isIE11()) {
                this.forceFocus($('body')[0]);
                $('#IECM').html('&nbsp;');
                this._unforceFocus($('body')[0]);
                $('body').blur();
                $('#IECM').html('&nbsp;&nbsp;');
            }

            this.forceFocus($initialFocus[$initialFocus.length-1]);

            $('body').attr('aria-busy', false);
            $('#ariastatus').attr('aria-label', "Standard Navigation initialized.");
            this.updateDebugInfo();
        },

        // ===CLEANUP AND SHUTDOWN FUNCTIONS===================================

        // Stops dynamic DOM monitoring without unbinding keyboard or mouse
        // events.  Presently no effect.
        stop: function () {},

        // Unbind the keyboard and mouse handlers.
        deactivate: function () {
            this._unbindTouchEvents();
            this._unbindMouseEvents();
            this._unbindKeyboardEvents();
            this._unbindFocusEvents();
        },

        // ===DOM EVENT HANDLERS, EVENT BINDING AND UNBINDING==================
        _bindFocusEvents: function() {
            $('body').on('focusin', $.proxy(this._onFocusIn, this));
            $('body').on('focusout', $.proxy(this._onFocusOut, this));
            //$('body').on('focus', $.proxy(this._onFocusIn, this));
            //$('body').on('blur', $.proxy(this._onFocusOut, this));
        },

        _bindKeyboardEvents: function () {
            var _this = this;
            $(document).on('keydown', $.proxy(this._onKeydown, this));
            // TODO: Evaluate whether focus and blur have better compatibility
        },

        _bindMouseEvents: function () {
            $('body').on('click', $.proxy(this._onClick, this));
            $('body').on('dblclick', $.proxy(this._onDblClick, this));
            $('body').on('mousedown', $.proxy(this._onMouseDown, this));
            $('body').on('mouseover', $.proxy(this._onMouseOut, this));
            $('body').on('mouseout', $.proxy(this._onMouseOver, this));
            $('body').on('mouseup', $.proxy(this._onMouseUp, this));
            $('body').on('mouseover', '[aria-label]', $.proxy(this._onLabeledTagOver, this));
        },

        _bindTouchEvents: function () {
            $('body').on('touchend', $.proxy(this._onTouchStart, this));
            $('body').on('touchstart', $.proxy(this._onTouchStart, this));
        },

        _unbindTouchEvents: function () {
            $('body').off('touchend', $.proxy(this._onTouchStart, this));
            $('body').off('touchstart', $.proxy(this._onTouchStart, this));
        },

        _unbindMouseEvents: function () {
            $('body').off('click', $.proxy(this._onClick, this));
            $('body').off('dblclick', $.proxy(this._onDblClick, this));
            $('body').off('mousedown', $.proxy(this._onMouseDown, this));
            $('body').off('mouseover', $.proxy(this._onMouseOut, this));
            $('body').off('mouseout', $.proxy(this._onMouseOver, this));
            $('body').off('mouseup', $.proxy(this._onMouseUp, this));
        },

        _unbindKeyboardEvents: function () {
            var _this = this;
            $(document).off('keydown', $.proxy(this._onKeydown, this));
        },

        _unbindFocusEvents: function () {
            var _this = this;
            $('body').off('focusin', $.proxy(this._onFocusIn, this));
            $('body').off('focusout', $.proxy(this._onFocusOut, this));
            //$('body').off('focus', $.proxy(this._onFocusIn, this));
            //$('body').off('blur', $.proxy(this._onFocusOut, this));
        },

        // ===BEHAVIOR ASSEMBLY================================================

        // Takes a jQuery element and builds the a behavior overlay hash composed
        // of that element's explicit overrides only.
        // FIXME - Try to come up with a more generic mechanism so this isn't
        // brittle if we add new attributes/overrides.  However, this may be
        // complicated by differences in JS and HTML naming conventions in our
        // coding standards.  Also, see if we can optimize this by quickly
        // building a js-stdnav-* array/hash.
        _getExplicitBehavior: function (el) {
            var overlay = {};
            if (el.attr('js-stdanv-enter') !== undefined) {
                overlay['enter'] = el.attr('js-stdnav-enter');
            }
            if (el.attr('js-stdanv-exit') !== undefined) {
                overlay['exit'] = el.attr('js-stdnav-exit');
            }
            if (el.attr('js-stdanv-entered') !== undefined) {
                overlay['entered'] = el.attr('js-stdnav-entered');
            }
            if (el.attr('js-stdanv-rejoin') !== undefined) {
                overlay['rejoin'] = el.attr('js-stdnav-rejoin');
            }
            if (el.attr('js-stdanv-up') !== undefined) {
                overlay['up'] = el.attr('js-stdnav-up');
            }
            if (el.attr('js-stdanv-down') !== undefined) {
                overlay['down'] = el.attr('js-stdnav-down');
            }
            if (el.attr('js-stdanv-left') !== undefined) {
                overlay['left'] = el.attr('js-stdnav-left');
            }
            if (el.attr('js-stdanv-right') !== undefined) {
                overlay['right'] = el.attr('js-stdnav-right');
            }
            if (el.attr('js-stdanv-next') !== undefined) {
                overlay['next'] = el.attr('js-stdnav-next');
            }
            if (el.attr('js-stdanv-prev') !== undefined) {
                overlay['prev'] = el.attr('js-stdnav-prev');
            }
            if (el.attr('js-stdanv-inherit') !== undefined) {
                overlay['inherit'] = el.attr('js-stdnav-inherit');
            }
            if (el.attr('js-stdanv-inheritable') !== undefined) {
                overlay['inheritable'] = el.attr('js-stdnav-inheritable');
            }
            return overlay;
        },

        // Builds the effective navigation behavior hash for the ancestry of the element,
        // NOT including the element itself.
        //
        // Right now, this includes node/element behavior, navtype behavior, and explicit
        // overrides; it may be more appropriate include only explicit overrides.
        //
        // Traversal stops when any of the following are true:
        // - The iterator has "inherit='false'"
        // - The parent has "inheritable='false'"
        // - The root is reached
        // - The parent is an IFRAME
        //
        // PERFORMANCE / MEMORY NOTE: RECURSIVE SOLUTION
        _buildParentBehaviorOBSOLETE: function (element) {
            // IMPORTANT - DO NOT "OPTIMIZE" THIS BY JUST RETURNING
            // this.defaultBehavior!  We need to use a new object, because it gets
            // modified recursively!  This code does a deep copy:
            //        var compositeBehavior = $.extend(true, {}, this.defaultBehavior);


            // NOTE: the case where:
            //    element.prop('js-stdnav-inherit')==false
            // is ruled out by the caller.

            // ABORT CASE: Somehow, element has no parent.  Shouldn't happen,
            // but might occur in a testing framework, or detached DOM objects.
            var parentEl = $(element.parent()[0]);
            if (parentEl === undefined) {
                //return compositeBehavior;
                return this.defaultBehavior;
            }

            // ALL REMAINING CASES REQUIRE COMPUTATION OF PARENT BEHAVIOR
            // Therefore, we must now determine the parent's immediate behavior, so
            // we can figure out if we should 1) overlay it, and, 2) go further.
            var parentImmediateBehavior = this._buildImmediateBehavior(parentEl);

            if (parentImmediateBehavior['inheritable'] === false) {
                // BASE CASE: Parent disallows inheritance by element.
                return this.defaultBehavior;
            }

            // ALL REMAINING CASES ALLOW PARENT INHERITANCE
            // (the parent's behavior will be overlayed on the defaults and returned)
            // The remaining variants may or may not allow GRANDPARENT inheritance.
            if ((parentImmediateBehavior['inherit'] === false) || parentEl.is('body,iframe')) {
                // BASE CASE: Parent allows inheritance of itself but disallows inheritance of grandparent.
                // BASE CASE: Parent is BODY; no grandparent to check.
                // BASE CASE: Parent is an IFRAME; treat as document root.
                return $.extend({}, true, this.defaultBehavior, parentImmediateBehavior);
            }

            // RECURSIVE CASE
            // Go check the grandparent.
            var grandparentBehavior = this._buildParentBehavior(parentEl);
            // The special option 'inherit' is not, itself, inheritable; override
            // it back to true.
            return $.extend({}, true, grandparentBehavior, parentImmediateBehavior, {
                'inherit': false
            });
        },

        // Builds the effective navigation behavior hash for an element.
        //
        // This is a non-recursive version of the previous approach, to improve
        // performance, reduce stack overhead (critical), improve debugging, and
        // provide a failsafe bailout.
        //
        // The new approach is:
        // - copy the NULL behavior hash to a new object
        // - for each ancestor, build the immediate behavior for that ancestor
        // - use that behavior to fill in ONLY non-NULL elements in the working hash
        // - abort when inherit==false or parent->inheritable==false
        // - also abort if maximum ancestry depth is reached (failsafe)
        // - fill in remaining behavior from defaults hash
        //
        // ...basically, an underlay instead of an overlay.
        //
        // NOTE: This function returns a deep-copy throwaway you can modify at
        // will.
        _buildBehavior: function (element) {
            // Make a copy of the default behavior, or you'll corrupt it!  And make
            // sure it's a DEEP copy, or you won't get the arrays and subobjects!
            var behavior = $.extend(true, {}, this.nullBehavior);
            var iter = element;
            var height = 0;
            var immediateBehavior = this._buildImmediateBehavior(iter);

            while (height < this._maxNavDepth) {
                // UNDERLAY this node's behavior
                $.each(immediateBehavior, function (key, value) {
                    if (key in behavior) {
                        if (behavior[key] === null) {
                            // This key is known, but has not been seen yet, use the
                            // ancestor's value.
                            behavior[key] = value;
                        }
                        // ...else, the key has been overridden by a descendant and this
                        // ancestor value is therefore unimportant.
                    } else {
                        // The key does not yet exist in the behavior hash.  This
                        // indicates the behavior is missing from the NULL behavior hash,
                        // which will cause performance issues.  However, it is not fatal,
                        // so recover.  TEST THIS WITH A BREAKPOINT AND TAG IT DURING
                        // PERFORMANCE AUDITS!
                        logger.warn("StdNav: Key '" + key + "' is missing from the NULL-behavior hash");
                        behavior[key] = value;
                    }
                });
                if (immediateBehavior['inherit'] === false) {
                    // Node does not want to inherit parent behavior, stop search.
                    break;
                }
                height++;
                iter = $(iter).parent()[0];
                immediateBehavior = this._buildImmediateBehavior(iter);
                if (immediateBehavior['inheritable'] === false) {
                    // Parent is not inheritable, stop search.
                    break;
                }
            }

            // Fill in any remaining values from the default behavior hash.
            $.each(this.defaultBehavior, function (key, value) {
                if (key in behavior) {
                    if (behavior[key] === null) {
                        // This key is known, but has not been seen yet, use the
                        // default value.
                        behavior[key] = value;
                    }
                    // ...else, the key has been specified in the DOM, and the default
                    // value is therefore unimportant.
                } else {
                    // The key does not yet exist in the behavior hash.  This
                    // indicates the behavior is missing from the NULL behavior hash,
                    // which will cause performance issues.  However, it is not fatal,
                    // so recover.  TEST THIS WITH A BREAKPOINT AND TAG IT DURING
                    // PERFORMANCE AUDITS!
                    logger.warn("StdNav: Key '" + key + "' is defined in defaults but missing from the NULL-behavior hash");
                    behavior[key] = value;
                }
            });

            // IMPORTANT!  The underlay technique above assigns SHALLOW COPIES.
            // That would be fine if we could count on being able to return a
            // const object, but that's not widely supported yet, so instead, we
            // fix the result into a new object with a deep copy.
            return $.extend(true, {}, behavior);
        },

        // Takes a DOM element and builds the "immediate behavior"-- the behavior
        // from the node/element type, navtype, and explicit overrides, but NOT
        // the ancestors and defaults.
        _buildImmediateBehavior: function (element) {
            var self = this;
            // Do something sensible if subfocus was unset and we got bad input.
            if (this.nullOrUndefined(element)) {
                return this.defaultBehavior;
            }

            var el = $(element);

            // Figure out the default navtype for the node/element type.
            var defaultNavtype;
            var nodeName = el.prop('nodeName');
            $.each(this.navtype_nodeNames, function (navtype, supportedNodeNames) {
                if ($.inArray(nodeName, supportedNodeNames) > -1) {
                    if (self.isNavigable(element)) {
                        defaultNavtype = navtype;
                    }
                }
            });
            var nodeBehavior = {};
            if (defaultNavtype !== undefined) {
                nodeBehavior = this.navtypeBehaviors[defaultNavtype];
            }

            // Determine the navtype explicitly requested, if any.
            // NOTE: Children inherit the resulting _behavior_ of a parent with
            // an explicit navtype; they do not inherit the navtype attribute
            // itself.  This means, for example, that a particular child cell in
            // a table or grid can have a navtype that provides special behavior
            // for a few keystrokes but otherwise leaves the rest of the behavior
            // as it was for the higher-level, more complete navtype.  While this
            // can also be done by using explicit behavior, using a navtype is
            // more efficient, as the callback lookup does not need to be parsed.
            var navtype = el.attr('js-navtype');
            var navtypeBehavior = {};
            if (navtype !== undefined) {
                navtypeBehavior = this.navtypeBehaviors[navtype.toLowerCase()];
            }

            // Get other explicitly-specified behavior for the element, if any.
            var explicitBehavior = this._getExplicitBehavior(el);

            // FIXME-- this may be expensive in actual use, consider just checking required tags
            // Return the overlaid "immediate behavior" hash.
            return $.extend(true, {
                'inherit': true
            }, nodeBehavior, navtypeBehavior, explicitBehavior);
        },

        // Takes a DOM element and builds the effective navigation behavior hash.
        _buildBehaviorOBSOLETE: function (element) {
            // Do something sensible if subfocus was unset and we got bad input.
            if ((element === undefined) || (element === null)) {
                return this.defaultBehavior;
            }
            var el = $(element);
            // BUILD THE EFFECTIVE BEHAVIOR HASH FROM THE OVERLAYS
            // A. Build the inherited behavior:
            //  1. Start with default behavior (normally none).
            //  2. Overlay any inheritable behavior.
            // B. Build the immediate behavior:
            //  3. Overlay node/element type behavior.
            //  4. Overlay explicit navtype behavior, if specified.
            //  5. Finally, overlay any other explicitly-specified behavior.
            var immediateBehavior = this._buildImmediateBehavior(el);
            var inheritedBehavior = {};
            if (immediateBehavior['inherit'] === true) {
                inheritedBehavior = this._buildParentBehavior(el);
            }
            return $.extend(true, inheritedBehavior, immediateBehavior);
        },

        // Returns true if the element is PROGRAMMATICALLY focusable-- in other
        // words, whether or not JavaScript logic can move focus to this
        // element.  This does NOT necessarily mean the TAB key can move to
        // this element, or that it will receive focus automatically if clicked
        // on; it simply means an element.focus() call should succeed.
        //
        // Element type is checked, then we check to see if the element is\
        // enabled, then we check explicit overrides from tabindex.
        //
        // Note that not all navigable controls can be focused.  For example, a
        // table cell is navigable if the table plugin is loaded, but is not
        // normally focusable (though the table itself will be if the table
        // element specifies a tabindex).
        isLogicFocusable: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.error("isLogicFocusable called on a null or undefined element");
                return false;
            }

            var $el = $(element),
                canFocus = false,
                nodeName = $el.prop('nodeName'),
                tabindex = $el.attr('tabindex');

            // These elements are always focusable (...unless you're in Safari
            // and haven't turned on keyboard accessibility!)
            if ($el.is(':input') || ($.inArray(nodeName, ['A', 'BUTTON', 'INPUT', 'SELECT', 'OBJECT', 'TEXTAREA']) > -1)) {
                // ...unless they've been disabled.
                if ($el.is(':enabled')) {
                    canFocus = true;
                }
            }

            /* else if (nodeName == 'A') {
              // Anchor tags are only focusable if they are clickable links.
              // NOTE: Anchor tags are sometimes given explicit tabindexes anyway.
              if (el.attr('href') != undefined) {
                canFocus = true;
              }
            }*/

            // If any tabindex value has been manually assigned, the control
            // can receive focus through JS logic.
            if ($el.is('[tabindex]')) {
                canFocus=true;
            }
            return canFocus;
        },

        // Returns true if the element is USER focusable-- the TAB key, a mouse
        // click, or a touch can focus this element.  This DOES imply that the
        // element can be given focus via JavaScript logic such as
        // element.focus().
        //
        // Element type is checked, then we check to see if the element is\
        // enabled, then we check explicit overrides from tabindex.
        //
        // Note that not all navigable controls can be focused.  For example, a
        // table cell is navigable if the table plugin is loaded, but is not
        // normally focusable (though the table itself will be if the table
        // element specifies a tabindex).
        isUserFocusable: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.error("isUserFocusable called on a null or undefined element");
                return false;
            }
            var el = $(element),
                canFocus = false,
                nodeName = el.prop('nodeName'),
                tabindex = el.attr('tabindex');

            // These elements are always focusable...
            if (el.is(':input') || ($.inArray(nodeName, ['A', 'BUTTON', 'INPUT', 'SELECT', 'OBJECT', 'TEXTAREA']) > -1)) {
                // ...unless they've been disabled.
                if (el.is(':enabled')) {
                    canFocus = true;
                }
            }

            /* else if (nodeName == 'A') {
              // Anchor tags are only focusable if they are clickable links.
              // NOTE: Anchor tags are sometimes given explicit tabindexes anyway.
              if (el.attr('href') != undefined) {
                canFocus = true;
              }
            }*/

            // Explicit overrides
            if (tabindex !== undefined) {
                if (tabindex == '-1') {
                    canFocus = false;
                } else {
                    // tabindex>=0
                    canFocus = true;
                }
            }
            return canFocus;
        },

        // This function temporarily makes an element unfocusable.  It is
        // normally used to make a container unfocusable while focused on a
        // contained element, so that SHIFT-TAB will navigate to the PRIOR
        // container, rather than merely the contained element's container
        // (which would immediately promote focus back to the contained element,
        // giving no net change).
        //
        // - Unfocusable items will not become focusable.
        // - Redundant calls are harmless.
        suspendFocusability: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.warn('stdnav.suspendFocusability called on null or undefined element');
                return;
            }
            var $el = $(element);
            if (!this.nullOrUndefined($el.attr('js-suspended-tabindex'))) {
                // Redundant call, abort.
                logger.warn('stdnav.suspendFocusability called on already suspended element ' + element.nodeType + '#' + element.id);
                return;
            }
            var currentTabindex = $el.attr('tabindex');
            if (!this.nullOrUndefined(currentTabindex)) {
                $el.attr('js-suspended-tabindex', currentTabindex);
            } else {
                $el.attr('js-suspended-tabindex', 'none');
            }
            // Explicitly make the element unfocusable-- temporarily.
            $el.attr('tabindex', '-1');
        },

        resumeFocusability: function (element) {
            if (this.nullOrUndefined(element)) {
                logger.warn('stdnav.resumeFocusability called on null or undefined element');
                return;
            }
            var $el = $(element);
            var suspendedTabindex = $el.attr('js-suspended-tabindex');
            if (this.nullOrUndefined(suspendedTabindex)) {
                // Out-of-order call, abort.
                logger.warn('stdnav.resumeFocusability called on non-suspended element ' + element.nodeName + '#' + element.id);
                return;
            }
            if (suspendedTabindex === 'none') {
                // No tabindex was present, so remove the temporary one.
                $el.removeAttr('tabindex');
            } else {
                // The original tabindex must have explicitly been given a negative
                // number to suppress normal tab navigation; restore that value.
                $el.attr('tabindex', suspendedTabindex);
            }
            $el.removeAttr('js-suspended-tabindex');
        },

        // There are various weird ways we can lose focus beyond normal UI
        // actions and JavaScript logic-- ALT-TABbing to another app, etc.
        // For embedded uses, "alert" popups in third-party apps can cause
        // this.  And, the debugging tools can cause this-- which is very
        // frustrating.  That can result in a loss of focusability and cause
        // various other problems.  Therefore, this function looks for
        // anything with a suspended-tabindex and restores it, _unless_ it
        // is an ancestor of the element passed-- this avoid breaking
        // SHIFT-TAB, since most containers will auto-promote focus to their
        // children.
        ensureFocusabilityBeyond: function(element){
            var $suspendees=$('[suspended-tabindex]');
            $suspendees.each(function(suspendee){
                if (!$.contains(suspendee, element)){
                    var $suspendee=$(suspendee);
                    var suspendedTabindex=$suspendee.attr('js-suspended-tabindex');
                    $suspendee.attr('tabindex', suspendedTabindex);
                    $suspendee.removeAttr('js-suspended-tabindex');
                }
            });
        },

        // Set browser focus if and only if it will not be redundant.
        // Additionally, if the element is not normally focusable, keep track of
        // the reason why, but make it temporarily focusable anyway.  Finally,
        // if an ancestor has a tabindex greater than 0, use that value.

        forceFocus: function (element) {
            logger.debug("stdnav.forceFocus(" + element.nodeName + "#" + element.id + ")");
            var $el = $(element);

            // See if we should use a tabindex of zero, or something else.  Keep in mind
            // that we need to check _suspended_ tabindexes as well, or the logic that
            // lets SHIFT-TAB get out of a region will break this.  For (example, a list
            // suspends focus on its UL while an LI has focus, so that SHIFT-TAB doesn't
            // just refocus the list.  But if the UL has tabindex=2, which will have
            // been suspended, we still want the LIs to get tabindex=2, to avoid breaking
            // the tab cycle in Internet Explorer.)
            var newTabIndex = 0;
            $.each($el.parents('[tabindex]'), function(index, ancestor){
                var $ancestor = $(ancestor);
                if ($ancestor.attr('tabindex')>newTabIndex) {
                    newTabIndex=$ancestor.attr('tabindex');
                }
                if ($ancestor.attr('js-suspended-tabindex')>newTabIndex) {
                    newTabIndex=$ancestor.attr('js-suspended-tabindex');
                }
            });

            if (!$el.is($(document.activeElement))) {
                // Avoid redundant focus sets.
                if ( (!this.isLogicFocusable(element)) || (newTabIndex>$el.attr('tabindex')) ) {
                    // Temporarily make the element focusable or have a higher tabindex.
                    // This SHOULD be undefined, but if it IS, leave things
                    // alone.
                    if (!$el.attr('priortabindex')){
                        var priorTabindex = $el.attr('tabindex');
                        if (priorTabindex !== undefined) {
                            $el.data('prior-tabindex', priorTabindex);
                        } else {
                            $el.data('prior-tabindex', 'none');
                        }
                        // Explicitly make the element focusable-- temporarily.
                        $el.attr('tabindex', newTabIndex);
                    }
                }
                $(element).focus();
            }
        },

        /*
        forceFocus: function(element) {
            logger.debug("stdnav.forceFocus(" + element.nodeName + "#" + element.id + ")");
            var $el = $(element);

            if (!$el.is($(document.activeElement))) {
                // Avoid redundant focus sets.
                if ( (!this.isLogicFocusable(element)) ) {
                    // Temporarily make the element focusable or have a higher tabindex.
                    // This SHOULD be undefined, but if it IS, leave things
                    // alone.
                    if (!$el.attr('priortabindex')){
                        var priorTabindex = $el.attr('tabindex');
                        if (priorTabindex !== undefined) {
                            $el.data('prior-tabindex', priorTabindex);
                        } else {
                            $el.data('prior-tabindex', 'none');
                        }
                        // Explicitly make the element focusable-- temporarily.
                        $el.attr('tabindex', 0);
                    }
                }
                $(element).focus();
            }
        },
        */

        // Reset any tabindex changes that were made to force the element to be
        // temporarily focusable for navigation and screen reader purposes.  Note
        // that this function does not actually change browser focus, as it is
        // intended to be called as a reaction to "focusout".
        _unforceFocus: function (element) {
            var $el = $(element);
            var priorTabindex = $el.data('prior-tabindex');
            if (this.nullOrUndefined(priorTabindex)) {
                // forceFocus does not seem to have been called, was not needed, or
                // had no effect; do nothing.
            } else {
                if (priorTabindex == 'none') {
                    // No tabindex was originally present, so remove the temporary one.
                    $el.removeAttr('tabindex');
                } else {
                    // The original tabindex must have explicitly been given a negative
                    // number to suppress normal tab navigation; restore that value.
                    $el.attr('tabindex', priorTabindex);
                }
                $el.removeData('prior-tabindex');
            }
        },

        // Callback run when an element in the DOM is clicked.
        _onClick: function (ev) {
            var element = $(ev.target);

            // Sometimes this happens...
            if (element.length != 1) {
                return;
            }

            // Get the behavior for the clicked element.
            var behavior = this._buildBehavior(element);
            if (behavior['click'] != null) {
                var bubble = this.runAction('click', ev.target);
                if (bubble === false) {
                    ev.stopPropagation();
                    ev.preventDefault();
                }
            }
        },

        // Callback run when an element in the DOM is clicked.
        _onMouseDown: function (ev) {
            var element = $(ev.target);

            // Sometimes this happens...
            if (element.length != 1) {
                return;
            }

            // Get the behavior for the clicked element.
            var behavior = this._buildBehavior(element);
            if (behavior['mousedown'] != null) {
                var bubble = this.runAction('mousedown', ev.target);
                if (bubble === false) {
                    ev.stopPropagation();
                    ev.preventDefault();
                }
            }
        },

        // Callback run when an element in the DOM is clicked.
        _onMouseUp: function (ev) {
            var element = $(ev.target);

            // Sometimes this happens...
            if (element.length != 1) {
                return;
            }

            // Get the behavior for the clicked element.
            var behavior = this._buildBehavior(element);
            if (behavior['mouseup'] != null) {
                var bubble = this.runAction('mouseup', ev.target);
                if (bubble === false) {
                    ev.stopPropagation();
                    ev.preventDefault();
                }
            }
        },

        // Callback run when cursor is over on element with aria-label attribute.
        _onLabeledTagOver: function(ev) {
            var $target = $(ev.currentTarget);

            if ($target.attr("aria-label") && $target.data("title")) {
                $target.attr("title", $target.attr("aria-label"));
            }
        },

        // Callback run when an element in the DOM has acquired focus.
        _onFocusIn: function (ev) {
            this._currentFocus = ev.target;
            var fixedFocus = null,
                $target = $(ev.target);

            if ($target.attr("aria-label")) {
                $target.removeAttr("title");
            }

            logger.info("stdnav._onFocusIn  ev.target==" + ev.target.nodeName + "#" + ev.target.id);
            this.updateDebugInfo();

            // Hack for IE11, force a potential visual change
            var chaff='';
            for (var n=0; n<this.chaffLength; n++) {
                chaff+='&nbsp;';
            }
            $('#IECM').html(chaff);
            this.chaffLength=(this.chaffLength+1)%this.maxChaffLength;
            if (this.chaffLength===0){
                this.chaffLength=1;
            }

            // Sometimes this happens...
            if ($(ev.target).length < 1) {
                // Safety
                this._refocusing = false;
                return;
            }
            var eventFocus = ev.target;

            // Figure out what element should actually have focus.  For example,
            // a table may have an initial tabstop, but the cells should actually
            // get focus, if there are any.
            fixedFocus = this.runAction('fixfocus', eventFocus);
            if (fixedFocus === null) {
                fixedFocus = eventFocus;
            }

            // Does that actually result in a change?
            if (fixedFocus !== eventFocus) {
                // Yes.  Run a refocusing cycle, and DO NOT fire callbacks yet.
                this._refocusing = true;
                // (NOTE: Do NOT call _unforceFocus for the initial target.)
                this.forceFocus(fixedFocus);
                // No further processing should occur.
            } else {
                // This is our final focus target.
                this._refocusing = false;
                // Was there a net change?
                if (eventFocus === this._priorFocus) {
                    // No-- no net change.  Suppress the redundant events and do NOT
                    // run callbacks.  Do NOT update prior latches.
                } else {
                    // Yes-- fixed focus has moved.
                    // Remove subfocus CSS class from prior subfocus element.
                    // As a safety, we just globally remove the class.
                    $('.subfocus').removeClass('subfocus');
                    if (this._priorSubfocus){
                        this.runAction('subfocusout', this._priorSubfocus);
                    }
                    if (this._priorFocus){
                        this.runAction('focusout', this._priorFocus);
                        // If the control was temporarily made focusable, undo that.
                        this._unforceFocus(this._priorFocus);
                    }
                    // Latch the new focus.
                    this._currentFocus = eventFocus;
                    // Figure out the new superfocus, partly so that we can tell if the
                    // superfocus has changed.
                    this._currentSuperfocus = this.runAction('fixsuperfocus', this._currentFocus);
                    if (this._currentSuperfocus === null) {
                        // Superfocus could not be determined; fall back to BODY.
                        this._currentSuperfocus = $('body')[0];
                    }
                    logger.debug("stdnav._onFocusIn: fixsuperfocus(" + this._currentFocus.nodeName + "#" + this._currentFocus.id + ") returned " + this._currentSuperfocus.nodeName + "#" + this._currentSuperfocus.id);
                    if (this._currentSuperfocus !== this._priorSuperfocus) {
                        // Superfocus has changed; move the CSS class and run the
                        // callbacks.
                        var $superfocus = $('.superfocus');
                        if ($superfocus.length > 0) {
                            logger.debug("Removing .superfocus from " + $superfocus[0].nodeName + '#' + $superfocus[0].id);
                        } else {
                            logger.warn("No current .superfocus to remove");
                        }
                        $('.superfocus').removeClass('superfocus');
                        if (this._priorSuperfocus){
                            this.runAction('superfocusout', this._priorSuperfocus);
                            this.resumeFocusability(this._priorSuperfocus);
                        }
                        // Refresh the automatic ARIA markup for the region.
                        logger.debug("Running ARIA refresh for " + this._currentSuperfocus.nodeName + '#' + this._currentSuperfocus.id);
                        this.runAction('ariarefresh', this._currentSuperfocus);
                        logger.debug("Adding .superfocus to " + this._currentSuperfocus.nodeName + '#' + this._currentSuperfocus.id);
                        $(this._currentSuperfocus).addClass('superfocus');
                        // This ensures SHIFT-TAB will properly move to the prior region,
                        // rather than moving from an inner focus to the container (which
                        // is not useful and breaks backwards navigation).  However, do
                        // NOT do this if the superfocus is the same element as the focus,
                        // since it is not needed.
                        if (!$(this._currentSuperfocus).is(this._currentFocus)) {
                            this.suspendFocusability(this._currentSuperfocus);
                        }
                        this.runAction('superfocusin', this._currentSuperfocus);
                    }
                    // Run the focusin handler on the new focus.  This callback returns
                    // the new subfocus.  If it returns null, use the same value as the
                    // focus itself.
                    this._currentSubfocus = this.runAction('focusin', this._currentFocus);
                    if (this._currentSubfocus === null) {
                        this._currentSubfocus = this._currentFocus;
                    }
                    // Finally, set the subfocus CSS class on the new subfocus, and run
                    // the subfocusin callback for the new subfocus.
                    $(this._currentSubfocus).addClass('subfocus');
                    this.runAction('subfocusin', this._currentSubfocus);

                    // Ensure focus can be moved to other areas in the DOM.
                    // This is a safety, and helps deal with ALT-TAB, alerts,
                    // popups, debuggers, etc.  It does NOT restore the
                    // tabindex of the current focus container.
                    this.ensureFocusabilityBeyond(this._currentFocus);

                }
            }
            this.updateDebugInfo();
        },

        // Callback run when an element in the DOM is about to lose focus.
        _onFocusOut: function (ev) {
            this.updateDebugInfo();
            if (this._refocusing === true) {
                // Intermediate target-- do not update latches.
            } else {
                if ($('.superfocus').length > 0) {
                    this._priorSuperfocus = $('.superfocus')[0];
                } else {
                    logger.warn("no prior superfocus");
                    this._priorSuperfocus = $('body')[0];
                }
                this._priorFocus = ev.target;
                if ($('.subfocus').length > 0) {
                    this._priorSubfocus = $('.subfocus')[0];
                } else {
                    logger.warn("no prior subfocus");
                    this._priorSubfocus = ev.target;
                }
            }
            this.updateDebugInfo();
        },

        // Callback run when an element in the DOM has just acquired subfocus.
        // Note that this is AFTER the change, which is different than the way
        // 'onfocusin' works-- this is DELIBERATE, to ensure either callback
        // can preempt the other if necessary without recursing.
        //
        // Thie callback only fires if the subfocus change happens through
        // stdnav.setSubfocus!  Do not just set the CSS class!
        _prepSubfocusIn: function (element) {
            // If you move subfocus outside of a parent that has browser focus,
            // browser focus will be set to the subfocus.  This means that if
            // you, for example, give tabindexes to grid cells, moving left will
            // move both focus and subfocus-- give tabindexes to the top level
            // grid element ONLY, if you want to avoid this.
            if ($(element).parents(':focus').length === 0) {
                // No parent has focus.  Set browser focus to the new subfocus element.
                $(element).focus();
            }
        },

        // Callback run when an element in the DOM is about to relinquish subfocus.
        // Only fires if this happens through stdnav.setSubfocus!
        //_prepSubfocusOut: function(element) {},
        // OBSOLETE

        // Sets superfocus to the element provided, if it can take superfocus, or
        // to the closest ancestor that can.  If no ancestor can take superfocus,
        // superfocus falls back to BODY.  If the new superfocus is the same as
        // the current superfocus, nothing happens and the function returns
        // immediately.
        //
        // If the superfocus is indeed different, the following things occur, in
        // this order:
        // 1. The "superfocusout" action runs on the prior superfocus element.
        // 2. The superfocus CSS style is removed from the prior superfocus element.
        // 3. The superfocus CSS style is added to the new superfocus element.
        // 4. The "superfocusin" action runs on the new superfocus element.
        // 4A. This action MUST set browser focus appropriately.
        //
        setSuperfocus: function (element, fireCallbacks) {
            logger.debug("stdnav.setSuperfocus(" + element.nodeName + "#" + element.id + ", " + fireCallbacks + ")");
            if (fireCallbacks === undefined) {
                fireCallbacks = true;
            }
            var oldSubfocus = $('.subfocus');
            if (oldSubfocus.length > 0) {
                if (fireCallbacks) {
                    // Call the subfocusout action on the old subfocus.
                    this.runAction('subfocusout', oldSubfocus[0]);
                }
                // Remove subfocus from the current (prior) element, if any (should be exactly one at all times).
                oldSubfocus.removeClass('subfocus');
            }

            // Take subfocus.
            $(element).addClass('subfocus');

            if (fireCallbacks) {
                // This default behavior, which ensures subfocus is always within
                // browser focus, runs no matter what.
                this._prepSubfocusIn(element);

                // Call the subfocusin action on the new subfocus.
                // This behavior can be overridden.
                this.runAction('subfocusin', element);
            }
        },

        // Sets subfocus to the element provided.
        // Your plugin should normally use this function instead of messing with
        // the "subfocus" class directly.
        // Using it ensures:
        //
        // - Exactly one element has subfocus at all times.
        //
        // - Subfocus never falls outside the inclusive subdom of the element
        //   that has superfocus.  Main focus will move if necessary.
        //
        // - Plugins can receive event callbacks when subfocus changes.
        //
        // fireCallbacks is optional, and defaults to true.
        setSubfocus: function (element, fireCallbacks) {
            logger.debug("stdnav.setSubfocus(" + element.nodeName + "#" + element.id + ", " + fireCallbacks + ")");
            if (fireCallbacks === undefined) {
                fireCallbacks = true;
            }
            var oldSubfocus = $('.subfocus');
            if (oldSubfocus.length > 0) {
                if (fireCallbacks) {
                    // Call the subfocusout action on the old subfocus.
                    this.runAction('subfocusout', oldSubfocus[0]);
                }
                // Remove subfocus from the current (prior) element, if any (should be exactly one at all times).
                oldSubfocus.removeClass('subfocus');
            }

            // Take subfocus.
            $(element).addClass('subfocus');

            if (fireCallbacks) {
                // This default behavior, which ensures subfocus is always within
                // browser focus, runs no matter what.
                this._prepSubfocusIn(element);

                // Call the subfocusin action on the new subfocus.
                // This behavior can be overridden.
                this.runAction('subfocusin', element);
            }
        },

        // ===ACTION LOGIC=====================================================

        // Runs the action defined in the effective behavior appropriate for a given element.
        // This function may be called by a testing framework or other automation.
        runAction: function (actionName, element) {
            if (element === null) {
                logger.warn("tried to run action '" + actionName + "' on null element");
                return;
            }
            if (typeof (element) === 'undefined') {
                logger.warn("tried to run action '" + actionName + "' on undefined element");
                return;
            }
            logger.debug("stdnav.runAction(" + actionName + ", " + element.nodeName + "#" + element.id + ")");
            var behavior = this._buildBehavior(element);
            if (!this.nullOrUndefined(behavior[actionName])) {
                return this._runActionDesc(behavior[actionName], element);
            }
        },

        // Runs the action described by the object provided.  Normally called
        // by runAction, which will figure out the behavior appropriate for a
        // given element.
        //
        // 1. A string that specifies the ID of an element to navigate to,
        //    preceded by a '#'.  The element will be given the focus and/or
        //    subfocus, as appropriate.  You can also use these special codes:
        //    "##parent"    Parent element.
        //    "##child"     First child element.
        //    "##prev"      Previous sibling element.
        //    "##next"      Next sibling element.
        //    These actions come from behavior explicitly specified in the markup.
        //    Examples: "#otherDiv"
        //              "##parent"
        //
        // 2. A string that specifies a function to call, the object to call
        //    it on, and any optional parameters that can be reasonably
        //    specified as substrings, preceded by a '@' if the object name
        //    is registered with stdnav, or '@@' if the value of
        //    "data-stdnav-context" should be used as the object/context.
        //    These actions also come from behavior explicitly specified in
        //    the markup.
        //    Examples: "@myObj:myFunc('param1', 'param2', 'paramChicken')"
        //              "@myObj:myFunc"
        //              "@@myFunc('param1', 'parm2')"
        //              "@@myFunc"
        //
        // 3. An array that specifies a context object, callback function,
        //    and parameter object (or null).  These actions normally come
        //    from behavior overalys in StdNav plugins, or default behavior
        //    in StdNav itself.  While they can be changed dynamically, this
        //    is not usually the best approach.
        //
        // 4. If actionDesc is null, no action is taken.  However, the event
        //    handler that has called this function is expected to NOT stop
        //    event propagation in this case.  Explicitly specifying null as the
        //    event handler-- as opposed to leaving the key out entirely-- tells
        //    the system that something other than StdNav is going to handle the
        //    event.
        _runActionDesc: function (actionDesc, element) {
            var retval = true;
            if ((typeof actionDesc === "string") || (actionDesc instanceof String)) {
                // A string can be used to indicate a simple subfocus change to a new
                // element, for example, "#someOtherDiv".
                if (actionDesc.substr(0, 1) == "#") {
                    if (actionDesc == "##parent") {
                        this.forceFocus($(element).parent());
                    } else if (actionDesc == "##child") {
                        var children = $(element).children();
                        if (children.length > 0) {
                            this.forceFocus($(element).children()[0]);
                        }
                    } else if (actionDesc == "##prev") {
                        var prev = $(element).prev();
                        if (prev.length > 0) {
                            this.forceFocus($(element).prev()[0]);
                        }
                    } else if (actionDesc == "##next") {
                        var next = $(element).next();
                        if (next.length > 0) {
                            this.forceFocus($(element).next()[0]);
                        }
                    } else {
                        var newEl = $(actionDesc);
                        if (newEl !== undefined) {
                            this.forceFocus(newEl);
                        }
                    }
                } else if (actionDesc.substr(0, 1) == '@') {
                    var funcName, func, paramstr, colon, lparen, rparen;
                    // If no context is specified, use the stdnav instance.
                    var context = this;
                    if (actionDesc.substr(1, 1) == '@') {
                        context = $(element).data('stdnav-context');
                    } else {
                        colon = actionDesc.indexOf(':');
                        if (colon > -1) {
                            var contextName = actionDesc.substr(0, colon);
                            context = this.actionContexts[contextName];
                        }
                    }
                    lparen = actionDesc.indexOf('(');
                    if (lparen == -1) {
                        funcName = actionDesc.substr(2);
                    } else {
                        rparen = actionDesc.indexOf(')');
                        funcName = actionDesc.substr(2, lparen - 2);
                        if (rparen == -1) {
                            logger.warn('Bad stdnav action: ' + actionDesc);
                        } else {
                            paramstr = actionDesc.substr(lparen, rparen - lparen);
                        }
                    }
                    func = context[funcName];
                    retval = func.call(context, paramstr);
                }
            } else if (actionDesc instanceof Array) {
                // The format is [context, callback, paramsObject].
                if (actionDesc[1] === undefined) {
                    logger.warn("undefined actionDesc[1]");
                }
                var context = actionDesc[0];
                if ((context === null) || typeof (context) === 'undefined') {
                    context = this;
                }
                var callback = actionDesc[1];
                var params = actionDesc[2];
                if ((params === null) || typeof (params) === 'undefined') {
                    params = {};
                }
                retval = callback.call(context, element, params);
            }
            return retval;
        },

        // ===DEFAULT BEHAVIOR HANDLERS========================================

        // The default click handler tries to find an appropriate element to
        // focus based on what you clicked, but also allows the click event to
        // bubble to other handlers.  This behavior may be redundant with basic
        // mousedown behavior, so consider whether you really want to use both.
        basicClick: function (element, args) {
            var fixedFocus = this.runAction('fixfocus', element);
            if (this.nullOrUndefined(fixedFocus)) {
                logger.debug("stdnav.basicClick: " + element.nodeName + "#" + element.id + " has no navigable ancestor, ignoring");
            } else {
                logger.debug("stdnav.basicClick(" + element.nodeName + "#" + element.id + ") refocusing to " + fixedFocus.nodeName + '#' + fixedFocus.id);
                if (!this.nullOrUndefined(fixedFocus)) {
                    this.forceFocus(fixedFocus);
                }
            }
            // Let the event bubble up.
            return true;
        },

        // The default fixfocus callback returns the element if it is navigable,
        // or if it is a form field/input.  Otherwise, the closest navigable
        // ancestor is returned.  If no navigable ancestor exists, the function
        // returns null, and focus should not change; this is useful in click
        // handlers.
        basicFixFocus: function (element, args) {
            if (this.isNavigable(element) || $(element).is(':input')) {
                return element;
            }
            var ancestor = this.closestNavigableAncestor(element);
            if (!this.nullOrUndefined(ancestor)) {
                return ancestor;
            }
            return null;
        },

        // FIXME-- UPDATE DOCS FOR LOGIC CHANGES
        // The default fixsuperfocus callback returns the closest navigable
        // ancestor for form elements, buttons, and links.  For other elements,
        // it returns the element itself, if it is navigable, otherwise the
        // closest navigable ancestor.  If nothing navigable can be identified,
        // superfocus is returned as BODY.
        //
        // If you place a tabindex="0" on an element that contains a form, this
        // will give you the behavior you expect: you can tab between the form
        // elements, but the overall form will be indicated as the active UI
        // region, and a fixsuperfocus call on the container itself will return
        // the container.
        basicFixSuperfocus: function (element, args) {
            var newSuperfocus = null;
            if ($(element).is(':input,fieldset')) {
                // Form element, input, button, or link.
                var $forms = $(element).closest('form');
                $(element).closest('form');
                if ($forms.length > 0) {
                    newSuperfocus = $forms[0];
                }
            } else {
                // Non-[input,form,link] element.
                if (this.isNavigable(element)) {
                    newSuperfocus = element;
                } else {
                    newSuperfocus = this.closestNavigableAncestor(element);
                }
            }
            if ((newSuperfocus === null) || (typeof (newSuperfocus) == 'undefined')) {
                newSuperfocus = element;
            }
            return newSuperfocus;
        },

        // This handy callback lets you set superfocus to whatever is appropriate
        // for your parent.  Use it when working with navtypes that have to
        // specify "inherit=false" but shouldn't be superfocii.
        parentFixSuperfocus: function (element, args) {
            return this.basicFixSuperfocus(element.parentNode, args);
        },

        // The default fixsubfocus callback simply returns the element passed.
        // This has the effect of setting subfocus to focus, which is almost
        // always what you want.
        basicFixSubfocus: function (element, args) {
            return element;
        },

        // The default focusin handler returns the same element as the new
        // subfocus.
        basicFocusIn: function (element, args) {
            logger.debug("stdnav.basicFocusIn(" + element.nodeName + "#" + element.id + ")");
            return element;
        },

        // The default focusout handler disallows demotion to ghostfocus.  This
        // is to prevent visual clutter with forms and other areas where there are
        // groups of tabbed elements with only one subfocusable region.
        basicFocusOut: function (element, args) {
            logger.debug("stdnav.basicFocusOut(" + element.nodeName + "#" + element.id + ")");
            return null;
        },

        // The default mousedown handler tries to find an appropriate element to
        // focus based on what you clicked, but also allows the mousedown event to
        // bubble to other handlers.
        //
        // Optional Arguments:
        //
        // TODO:
        // 'onlyfor': An optional jQuery selector whitelist.  Only elements matching
        //            this selector will have this behavior.  The event will bubble
        //            up whether the element matches the selector or not, however.
        basicMouseDown: function (element, args) {
            var fixedFocus = this.runAction('fixfocus', element);
            if (this.nullOrUndefined(fixedFocus)) {
                logger.debug("stdnav.basicClick: " + element.nodeName + "#" + element.id + " has no navigable ancestor, ignoring");
            } else {
                logger.debug("stdnav.basicClick(" + element.nodeName + "#" + element.id + ") refocusing to " + fixedFocus.nodeName + '#' + fixedFocus.id);
                if (!this.nullOrUndefined(fixedFocus)) {
                    this.forceFocus(fixedFocus);
                }
            }
            // Let the event bubble up.
            return true;
        },

        // The default mouseup handler takes no action, but allows the
        // mouseup event to bubble to other handlers.
        basicMouseUp: function (element, args) {
            // Let the event bubble up.
            return true;
        },

        // The default superfocusin handler indicates that focus should be given
        // to the same element as superfocus, unless an optional jQuery selector
        // matches one or more descendant elements within a maximum depth.  If
        // matches are found, and one also has the special CSS class "ghostfocus",
        // then it will be returned.  Otherwise, the first match will be returned.
        // Note that if the region contains any input controls, these will also
        // be focused.
        basicSuperfocusIn: function (element, args) {
            logger.debug("stdnav.basicSuperfocusIn(" + element.nodeName + "#" + element.id + ")");
            var newFocus = null;

            if ('focusSelector' in args) {
                var focusSelector = ':input,fieldset,' + args['focusSelector'] + '[js-navigable!="false"]';
                var barrier = null;
                if ('barrier' in args) {
                    barrier = args['barrier'];
                }
                var maxdepth = this._maxNavDepth;
                if ('maxdepth' in args) {
                    maxdepth = args['maxdepth'];
                }
                if (('ghostfocus' in args) && (args['ghostfocus'] === true)) {
                    newFocus = this.closestDescendant(element, focusSelector + ' .ghostfocus', barrier, maxdepth);
                    // Remove ghostfocus, since it is being promoted to focus.
                    $(newFocus).removeClass('ghostfocus');
                }
                if (newFocus == null) {
                    // No ghost available, or ghostfocus not allowed.
                    newFocus = this.closestDescendant(element, focusSelector, barrier, maxdepth);
                }
            }

            if (newFocus == null) {
                // If other approaches have failed, return the same element that has
                // superfocus.  This is appropriate for form fields, etc.
                newFocus = element;
            }
            return newFocus;
        },

        // The default superfocusout handler adds ghostfocus to the prior focus,
        // if the args hash includes 'ghostfocus':true.
        basicSuperfocusOut: function (element, args) {
            logger.debug("stdnav.basicSuperfocusOut(" + element.nodeName + "#" + element.id + ")");
            if ('ghostfocus' in args) {
                if (args['ghostfocus'] === true) {
                    if (this._priorFocus){
                        $(this._priorFocus).addClass('ghostfocus');
                    }
                }
            }
            return null;
        },

        // The default subfocusin handler takes no action.
        basicSubfocusIn: function (element, args) {
            logger.debug("stdnav.basicSubfocusIn(" + element.nodeName + "#" + element.id + ")");
            return null;
        },

        // The default subfocusout handler takes no action.
        basicSubfocusOut: function (element, args) {
            logger.debug("stdnav.basicSubfocusOut(" + element.nodeName + "#" + element.id + ")");
            return null;
        },

        // "Enter" is a catch-all "activate" function, normally triggered by
        // pressing the ENTER key.  It is distinct from acquiring focus or
        // subfocus, because it implies a deliberate user action _within_ the
        // element when it already has focus.
        //
        // The default "enter" handler finds the closest navigable descendant,
        // if one exists, and fires its "entered" handler.
        //
        // EXAMPLE:
        // If the Anchor and Button plugins are loaded, their "entered"
        // handlers will fire when they are entered; those handlers simulate a
        // "click" event but do not move focus or subfocus.  The net effect of
        // this is that pressing ENTER in a table cell that contains a button
        // will press the button, but pressing RIGHT ARROW immediately after
        // this will still move one cell to the right, because subfocus remains
        // on the table cell.
        basicEnter: function (element, args) {
            logger.debug("stdnav.basicEnter(" + element.nodeName + "#" + element.id + ")");
            var target = this.closestNavigableDescendant(element);
            if (target !== undefined) {
                this.runAction('entered', target);
            }
            return element;
        },

        // "exit" is the operation when the user presses Escape.  (There is no
        // mouse equivalent for this, so do not use this as a sort of
        // "onDefocus" event.)  The default behavior for "exit" is to look for
        // the closest navigable ancestor of the superfocus, and return that as
        // the new focus.  If no such ancestor exists, if BODY is returned, or
        // if NULL is somehow returned, focus instead moves to the main menu.
        // In any case, the "rejoined" action will fire for the new focus.
        basicExit: function (element, args) {
            logger.debug("stdnav.basicExit(" + element.nodeName + "#" + element.id + ")");
            var target = this.closestNavigableAncestor($('.superfocus'));
            if (!target) {
                // Something's gotten confused.  Refocus the main menu instead.
                target = $('#mainNavigation');
            } //else {
            //if (target.is(element)) {

            //}
            //}
            if (target !== undefined) {
                this.runAction('rejoined', target[0]);
            }
            return target[0];
        },

        // "entered" behavior is run when an element is being entered _from
        // another element_.  In other words, the ENTER key was pressed in
        // another control, which determined that this child element was the
        // intended target of the use action.  This is distinct from the "enter"
        // action.
        basicEntered: function (element, args) {
            logger.debug("stdnav.basicEntered(" + element.nodeName + "#" + element.id + ")");
            //var fixedFocus = this.runAction('fixfocus', element);
            //$(element).focus();
        },

        // "rejoined" behavior is run when a child element is being exited due to
        // a user action, normally the ESCAPE key.  When ESCAPE is pressed,
        // "exit" behavior will run in the element that has focus, and "rejoin"
        // behavior will run in the nearest navigable parent.  This default
        // handler simply forces focus to the element, which causes superfocus to
        // be recomputed, etc.
        basicRejoined: function (element, args) {
            logger.debug("stdnav.basicRejoined(" + element.nodeName + "#" + element.id + ")");
            //var fixedFocus = this.runAction('fixfocus', element);
            //$(element).focus();
        },

        _onKeydown: function (ev) {
            // Determine precisely what had browser focus when keyboard input was received.
            var elFocus = ev.target;

            // If nothing has subfocus, we cannot presume stdnav is safe
            // to use; bail out.
            if ($('.subfocus').length === 0) {
                return;
            }
            var elSubfocus = $('.subfocus')[0];
            var enabled = false;

            // While we allow form elements to be subfocused for visual consistency,
            // we never handle keyboard input from them, with a single exception:
            // the ESCAPE key must still allow focus to be returned to the menu.
            var nodeName = $(elSubfocus).prop('nodeName');
            // NOTE: 'BUTTON' was in the array below, but we desire ENTER to work
            // on this as well.
            if ($(elSubfocus).is(':input') || ($.inArray(nodeName, ['INPUT', 'SELECT', 'OBJECT', 'TEXTAREA']) > -1)) {
                if ((nodeName != 'BUTTON') && (ev.keyCode != 27)) {
                    return;
                }
            }

            // Unless there is a parent element in the DOM with the
            // attribute "js-stdnav=true" set, and no parent element
            // with "js-stdnav=false" set lower than that, take no
            // action.
            var parents = $(elSubfocus).parents();
            for (var n = 0; n < parents.length; n++) {
                if (!this.nullOrUndefined($(parents[n]).attr('js-stdnav'))) {
                    if ($(parents[n]).attr('js-stdnav') == 'true') {
                        enabled = true;
                    } else {
                        enabled = false;
                    }
                    break;
                }
            }

            // Take no action in DOM sections we are not enabled in, no matter what.
            // This is really important to avoid breaking text entry fields, editors, etc.
            if (!enabled) {
                logger.warn("StdNav is disabled in this subdom, aborting action");
                return;
            }

            var action;
            switch (ev.keyCode) {
            case 13: // Enter (CR)
                action = 'enter';
                break;

            case 27: // Escape.
                action = 'exit';
                break;

            case 32: // Space.
                action = 'toggle';
                break;

            case 33: // Page up.
                action = 'pageup';
                break;

            case 34: // Page down.
                action = 'pagedown';
                break;

            case 35: // End.
                action = 'end';
                break;

            case 36: // Home.
                action = 'home';
                break;

                /* Arrow key behavior depends on menu orientation. */
            case 37: // Left arrow
                action = 'left';
                break;

            case 38: // Up arrow
                action = 'up';
                break;

            case 39: // Right arrow
                action = 'right';
                break;

            case 40: // Down arrow
                action = 'down';
                break;

            case 91: // '[':
                // FIXME: Ignore unless CTRL or ALT is pressed.
                action = 'structleft';
                break;

            case 93: // ']':
                // FIXME: Ignore unless CTRL or ALT is pressed
                action = 'structright';
                break;

            default:
                // Allow default behavior for other keys.
            }
            if (!this.nullOrUndefined(action)) {
                var newFocus = this.runAction(action, elSubfocus);
                if (!this.nullOrUndefined(newFocus)) {
                    // Returning null indicates the event should bubble up.
                    // Otherwise, it indicates a new focus target.
                    ev.stopPropagation();
                    ev.preventDefault();
                    // If focus should change as a result of navigation, force the
                    // element to be focusable, and focus it.
                    // Note that redundant focus sets will be suppressed.
                    this.forceFocus(newFocus);
                }
            }
        }

    });

    var newStdnav = new stdnav();
    return newStdnav;
});