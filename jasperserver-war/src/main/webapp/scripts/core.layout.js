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
 * @author: Angus Croll
 * @version: $Id$
 */

/* global $H, Truncator, isIE7, parseFunc, centerElement, iScroll, matchMeOrUp, Draggable, isIPad, doNothing, $$,
 getDimensionInPx, JSCookie, getActualDimension, matchMeOrUp, Effect, isIE8, isIE9
*/

var layoutModule = {
    ELEMENT_CONTEXTMENU: "element:contextmenu",

    //predefine CSS patterns for element sifting
    SIZEABLE_PATTERN: '.sizeable',
    SIZER_PATTERN: '.sizer',
    RESIZE_OVERLAY_PATTERN: '.resizeOverlay',
    MOVEABLE_PATTERN: '.moveable',
    MOVER_PATTERN: '.mover',
    MINIMIZER_PATTERN: '.maximized .minimize',
    MINIMIZED_PATTERN: '.minimized .minimize',
    TRUNCATE_PATTERN: '.trunc',
    TREE_LEAF_PATTERN: 'a.>node',
    TREE_CONTAINER_PATTERN: '.collapsible',
    TOOLBAR_CAPSULE_PATTERN: '#toolbar .capsule',
    NAVIGATION_MUTTON_PATTERN: 'li.mutton',
    NAVIGATION_PATTERN: '#navigationOptions li',
    NAVIGATION_HOVER_PATTERN: '#navigationOptions li.over',
    META_LINKS_PATTERN: '#metaLinks #main_logOut_link',
    BUTTON_SET_BUTTON: '.buttonSet > *',
    TABSET_TAB_PATTERN: '.tab',
    // This class is given to both actual button elements, and instances of
    // other element types that should be handled by the buttonManager.
    BUTTON_PATTERN: '.button',
    PRESSED_PATTERN: '.pressed',
    SELECTED_PATTERN: '.selected',
    LIST_ITEM_PATTERN: '.list.responsive > li',
    LIST_ITEM_WRAP_PATTERN: '.wrap',
    DISCLOSURE_BUTTON_PATTERN: ".disclosure",
    // Entries in menu bars, popup menus, dropdown menus, and context menus.
    MENU_LIST_PATTERN: '#menu li',
    // Visual separators in menus that aren't focusable or cursorable.
    SEPARATOR_PATTERN: '.separator',
    MESSAGE_PATTERN: '.message',
    MESSAGE_WARNING_PATTERN: '.message.warning',
    MESSAGE_WARNING_DETAILS_PATTERN: '.details',
    MESSAGE_SUCCESS_PATTERN: '.message.success',
    DETAILS_PATTERN : '.details',
    COLUMN_NODE_WRAPPER_PATTERN: ".node > .wrap > .column",
    COLUMN_LEAF_WRAPPER_PATTERN: ".leaf > .wrap > .column",
    SEARCH_LOCKUP_PATTERN: ".searchLockup",
    DIALOG_PATTERN: ".dialog",
    DIALOG_LOADING_PATTERN: ".dialog.loading",
    SWIPE_SCROLL_PATTERN: ".swipeScroll",
    SCROLL_WRAPPER_PATTERN: ".scrollWrapper",

    //classes
    ADHOC_MODE_OPTION_CLASS: 'tab mode horizontal', //not used?
    DRAGGING_CLASS: 'dragging',
    COPY_CLASS: 'copy',
    PRESSED_CLASS : 'pressed',
    SELECTED_CLASS : 'selected',
    DISABLED_CLASS : 'disabled',
    // The button manager adds this class when a mouseover event occurs, and
    // removes it when a mouseout event occurs. Keyboard, touch, and automation
    // navigation should manage it as well.
    HOVERED_CLASS : 'over',
    ERROR_CLASS : 'error',
    SUCCESS_CLASS : 'error',
    MESSAGE_CLASS : 'error',
    UP_CLASS : 'up',
    LAST_CLASS : 'last',
    FIRST_CLASS : 'first',
    SCHEDULED_CLASS: 'scheduled',
    // The main menu bar, and any other menu bars.  Not used for context menus.
    MENU_ROOT_CLASS :'menuRoot',
    // Multi-purpose:
    //    For menus: a menu entry that has a drop-down or pop-out menu.
    //               Not used for context menus.
    NODE_CLASS: 'node',
    // Multi-purpose:
    //    For menus: a menu entry that has no drop-down or pop-out menus.
    //               Normally triggerable by clicking, touching, or pressing
    //               ENTER.
    LEAF_CLASS: 'leaf',
    ICON_CLASS: 'icon',
    // Node elements such as folders in tree views and list items with subitems
    // should have this class when open.
    OPEN_CLASS: 'open',
    // Node elements such as folders in tree views and list items with subitems
    // should have this class when closed.
    CLOSED_CLASS: 'closed',
    LOADING_CLASS: 'loading',
    NOTHING_TO_DISPLAY_CLASS: 'nothingToDisplay',
    COLLAPSIBLE_CLASS: "collapsible",
    RESPONSIVE_CLASS: "responsive",
    HIDDEN_CLASS: "hidden",
    NO_GENERATORS_CLASS: "noGenerators",
    STACKTRACE_CLASS: "stacktrace",
    EMPHASIS_CLASS: "emphasis",
    ASCENDING_CLASS: "ascending",
    DESCENDING_CLASS: "descending",
    CANCELLABLE_CLASS: "cancellable",
    DROP_TARGET_CLASS: "dropTarget",
    BUTTON_CLASS: "button",
    ONE_COLUMN_CLASS: "oneColumn",
    CONTROL_PAGE_CLASS: "controlPage",

    //attributes
    DISABLED_ATTR_NAME: "disabled",
    SELECTED_ATTR_NAME: "selected",
    OPEN_ATTR_NAME: "open",
    CLOSED_ATTR_NAME: "closed",
    READONLY_ATTR_NAME: "readonly",

    // Predefined DOM elements IDs.
    // ----------------------------
    DIMMER_ID: "pageDimmer",
    PAGE_BODY_ID: "display",
    // ID of the multipurpose dropdown/popup/context menu.
    MENU_ID: "menu",
    // IDs of the actual main menu bar and its entries.
    MAIN_NAVIGATION_ID: "mainNavigation",
    MAIN_NAVIGATION_HOME_ITEM_ID: "main_home",
    MAIN_NAVIGATION_LIBRARY_ITEM_ID: "main_library",
    META_LINK_LOGOUT_ID : "main_logOut_link",


    // DnD
    SCROLL_SENSITIVITY: 50,
    DND_MOVABLE_ZINDEX: 2000,

	//dimmer
	DIMMER_Z_INDEX: 900,

    //Resizable folders/filters/properties/fields panels
    FOLDERS_RESIZABLE_PANEL_ID: 'folders',
    FILTERS_RESIZABLE_PANEL_ID: 'filters',
    PROPERTIES_RESIZABLE_PANEL_ID: 'properties',
    FIELDS_RESIZABLE_PANEL_ID: 'fields',

    FOLDERS_RESIZABLE_PANEL_COOKIE_NAME: 'foldersPanelWidth',
    FOLDERS_MINIMIZED_PANEL_COOKIE_NAME: 'foldersMinimized',

    FILTERS_RESIZABLE_PANEL_COOKIE_NAME: 'filtersPanelWidth',
    FILTERS_MINIMIZED_PANEL_COOKIE_NAME: 'filtersMinimized',

    PROPERTIES_RESIZABLE_PANEL_COOKIE_NAME: 'propertiesPanelWidth',
    PROPERTIES_MINIMIZED_PANEL_COOKIE_NAME: 'propertiesMinimized',

    FIELDS_RESIZABLE_PANEL_COOKIE_NAME: 'fieldsPanelWidth',
    FIELDS_MINIMIZED_PANEL_COOKIE_NAME: 'fieldsMinimized',

    PANEL_WIDTH: 'PanelWidth',
    MINIMIZED: 'Minimized',
    PANEL_STATE_CHANGED_MANUALLY: 'PSCM',

    // swipe scroll
    scrolls: $H(),

    //////////////////////////////////////////////////////////////////////
    // Initialize: Programmatically modifies the DOM objects upfront
    //////////////////////////////////////////////////////////////////////

    initialize: function(container){
		container = $(container) || $(document.body);

        //truncate matching patterns
        new Truncator(container.select(layoutModule.TRUNCATE_PATTERN));

		//IE7 needs explicit style set (no sensible alternative to browser sniffing here)
		isIE7() && this.fixIE7Sizes();

        container.select(this.SIZEABLE_PATTERN).each(this.createSizer.bind(this));

        //init moveables (note: DOM elements created after page load will need to be initialized for move after creation)
        container.select(this.MOVEABLE_PATTERN).each(this.createMover.bind(this));

        //elements for centering (note: DOM elements created after page load will need to be initialized for center after creation)
        container.select('.centered_horz,.centered_vert').each(function(elem){
            var fn = parseFunc(elem, 'centered_fn_');
            fn || (fn = centerElement);

            fn(elem, {
                horz: elem.match('.centered_horz'),
                vert: elem.match('.centered_vert')
            });
        });

		//page dimmer init
        if ($(layoutModule.DIMMER_ID) != null) {
            $(layoutModule.DIMMER_ID).style.zIndex = layoutModule.DIMMER_Z_INDEX;
        }
        // in case of ajaxClobberedUpdate we have to reinitialize datepicker
        if (jQuery.datepicker && jQuery.datepicker.initialized) jQuery.datepicker.initialized = false;

        //make sure ajaxbuffer is available (for dynamicTree)
        jQuery(function() {
            if(jQuery("#ajaxbuffer").length === 0) {
                jQuery("body").append(jQuery('<div id="ajaxbuffer" style="display:none"></div>'));
            }
        });

        jQuery(window).resize(function(){
            if(this.resizeTo) clearTimeout(this.resizeTo);
            this.resizeTo = setTimeout(function(){
                jQuery(this).trigger('resizeEnd');
            }, 500);
        });
    },

    updateOnOrientation: function(){
    },

    //////////////////////////////////////////////////////////////////////
    // visibility
    //////////////////////////////////////////////////////////////////////

    isVisible : function(element) {
        return element && !$(element).hasClassName(layoutModule.HIDDEN_CLASS);
    },

    //////////////////////////////////////////////////////////////////////
    // panel scrolling
    //////////////////////////////////////////////////////////////////////

    createScroller : function(scrollContainer, options){
        if (scrollContainer) {
            scrollContainer = scrollContainer.identify ? scrollContainer : $(scrollContainer);
            var existingScroll = layoutModule.scrolls.get(scrollContainer.identify());
            existingScroll && existingScroll.destroy();

            var disableTransformFor = ["mainTableContainer", "resultsContainer", "filtersPanel",
                "foldersPodContent"];

            scrollContainer.identify().startsWith("iframeScroll_") && disableTransformFor.push(scrollContainer.identify());
            options = Object.extend({
            	hideScrollbar: false,
                useTransform: !disableTransformFor.include(scrollContainer.identify()),
                bounce: ["resultsContainer"].include(scrollContainer.identify())
                //checkDOMChanges: true

            }, options || {});
            var scroll = new iScroll(scrollContainer, options);
            layoutModule.scrolls.set(scrollContainer.identify(), scroll);

            return scroll;
        }

        return null;
    },

    //////////////////////////////////////////////////////////////////////
    // panel resizing
    //////////////////////////////////////////////////////////////////////

    createSizer : function(sizeable, options){
        sizeable = $(sizeable); // Prototype and jQuery compatibilu
        //sizeable is the element we will resize
        //sizer is the handle we drag to resize it
        var sizer = options && options.sizer ? $(options.sizer) : sizeable.down(this.SIZER_PATTERN); //get first descendant with class sizer
        if (!sizer || matchMeOrUp(sizeable,"." + layoutModule.HIDDEN_CLASS)) {return;}
        var sizerType = this.getSizerType(sizer);
        var sizerAlign = this.alignmentModule.getSizerAlignment(sizer, sizerType);

        var dragger = new Draggable(sizer);
        dragger.resizeablePanel = sizeable;
        dragger.primaryPanel = $(sizeable).siblings().find(function(s){
            return s.match('.primary')
        });

        if (options && options.fillerPattern) {
            dragger.filler = $(jQuery(sizeable).find(options.fillerPattern)[0]);
        }
        var sizerMousedownCallback = function() {
            return function(evt) {
                //set co-ords now - if we wait until drag start it is too late - mouse already moved a bit
                dragger.pointerX = evt.pointerX();
                dragger.pointerY = evt.pointerY();
            }
        }();

        //have to observer at this level - otherwise draggable has already swallowed up event
        sizer.observe('mousedown', sizerMousedownCallback);
        jQuery(sizer).bind({
        	'touchstart':function(e){
        		sizerMousedownCallback(e.originalEvent);
        		this.style.background = '#666';
                this.style.zIndex = 9;
        	},
        	'touchend':function(){
        		this.style.background = 'transparent';
        	}
        })
        dragger.sizerType = sizerType;
        dragger.alignment = sizerAlign;

        dragger.options = {
            constraint: ((sizerType === 'diagonal') ? '' : sizerType),
            onStart: layoutModule.beginSizerDragging.bind(layoutModule),
            onDrag: !isIPad() ? layoutModule.respondToDrag.bind(layoutModule) : doNothing,
            onEnd: layoutModule.endSizerDragging.bind(layoutModule)
        }

    },

    getSizerType: function(sizer) {
        return (sizer.match('.horizontal') && 'horizontal') ||
                (sizer.match('.vertical') && 'vertical') ||
                (sizer.match('.diagonal') && 'diagonal');
    },

    alignmentModule: {

        alignments: [
            ['left','right'],
            ['top','bottom']
        ],

        alignmentIndexes: {
            'horizontal':0,
            'vertical':1
        },

        dimensions: ['width','height'],

        /**
         * which edge(s) of the sizeable object is the sizer aligned with
         * @param {Object} sizer
         * @param {Object} sizerType
         * @return (Array) ['left', 'right' or '']['top', 'bottom' or '']
         */
        getSizerAlignment: function(sizer, sizerType){
            var result = ['',''];
            if (sizerType==='diagonal') {
                //diagonals sizer always the same alignment
                return ['right','bottom'];
            }
            var alignmentIndex = this.alignmentIndexes[sizerType]; //0 or 1
            var alignmentStyles = this.alignments[alignmentIndex]; //['left','right'] or ['top','bottom']
            var alignmentOffsets = [0,1].map(
				function(al) {
					return parseInt(sizer.getStyle(alignmentStyles[al]))
			});
            //calculate sizer anchorage by testing which offset is smaller (NaN mean not anchored - therefore not aligned)
			var alignment = (isNaN(alignmentOffsets[0]) || (alignmentOffsets[0] > alignmentOffsets[1])) ?
				alignmentStyles[1] : alignmentStyles[0];
            //now set the appropriate element
            result[alignmentIndex] = alignment;
            //e.g. for horizontal: ['right',''];
            return result;
        }
    },

    beginSizerDragging : function(dragger, event) {
        var sizer = dragger.element;
        dragger.parentOffset = [sizer.getStyle(dragger.alignment[0]), sizer.getStyle(dragger.alignment[1])];
        dragger.offsetUnit = 'px'; //TODO: fix me
        dragger.pointerX = dragger.pointerX || event.pointerX();
        dragger.pointerY = dragger.pointerY || event.pointerY();
        dragger.element.addClassName(layoutModule.DRAGGING_CLASS);
        $$(layoutModule.RESIZE_OVERLAY_PATTERN).each(function(overlay){
            overlay.removeClassName(layoutModule.HIDDEN_CLASS);
        });
    },

    endSizerDragging : function(dragger, event){
        layoutModule.respondToDrag(dragger, event);
        dragger.element.removeClassName(layoutModule.DRAGGING_CLASS);
        $$(layoutModule.RESIZE_OVERLAY_PATTERN).each(function (overlay) {
            overlay.addClassName(layoutModule.HIDDEN_CLASS);
        });
        //fire custom event so that we can know when we have finished sizing
        document.fire("dragger:sizer", {targetEvent:event, element: dragger.element});
        jQuery(window).trigger("resize", {targetEvent:event, element: dragger.element})
    },

    respondToDrag : function(dragger, event) {
        //no else stmt - diagonal sizer will do both
        if (dragger.sizerType !== 'vertical') { //includes diagonal
            this.resizeOnDrag(dragger, event, 'horizontal');
        }
        if (dragger.sizerType !== 'horizontal') { //includes diagonal
            this.resizeOnDrag(dragger, event, 'vertical');
        }
    },

    resizeOnDrag : function(dragger, event, orientation) {
        //set vars based on dragger orientation
        var dragData = {};
        dragData.axis = (orientation == 'horizontal') ? 0 : 1;
        dragData.myAlignments = this.alignmentModule.alignments[dragData.axis];
        dragData.dimension = this.alignmentModule.dimensions[dragData.axis];
        dragData.sizeProperty = dragData.axis ? 'offsetHeight' : 'offsetWidth';

        var pointerName = dragData.axis ? 'pointerY' : 'pointerX';
        var c = {
        	pointerY: 'clientY',
        	pointerX: 'clientX'
        }
        //generic code starts here
        var sizer = dragger.element;
        dragData.sizeableAnchorage = (dragger.alignment[dragData.axis] === dragData.myAlignments[0]) ? dragData.myAlignments[1] : dragData.myAlignments[0];

        var p = event.changedTouches ?  event.changedTouches[0][c[pointerName]] : event[pointerName]();
        var dragDelta = dragger[pointerName] ? p - dragger[pointerName] : 0;
        dragDelta = (dragger.alignment[dragData.axis] === dragData.myAlignments[1]) ? dragDelta : 0 - dragDelta; //directional correction
        dragData.dragDelta = dragDelta;
        dragger[pointerName] = event[pointerName]();

        this.resizePanelsOnDrag(dragger, dragData);
    },

    resizePanelsOnDrag : function(dragger, dragData) {
        var sizeTracker = this.getSizeTracker();
        sizeTracker.checkpoint(dragger.resizeablePanel[dragData.sizeProperty]);

        if (dragger.filler && !dragger['fillerDelta'+dragData.dimension]){
            dragger['fillerDelta'+dragData.dimension] = dragger.resizeablePanel[dragData.sizeProperty] - dragger.filler[dragData.sizeProperty];
        }

        //size the panel
		var panelSize = getDimensionInPx(dragger.resizeablePanel, dragData.dimension); //worsk for % or px
        if (this.isInBounds(dragger, dragData) || (!this.isInBounds(dragger, dragData) && dragData.dragDelta < 0)) {
            dragger.resizeablePanel.setStyle(dragData.dimension + ':' + (panelSize + dragData.dragDelta) + 'px');
            //size filling element if any
            if (dragger.filler) {
                dragger.filler.setStyle(dragData.dimension + ':' + (panelSize + dragData.dragDelta - dragger['fillerDelta' + dragData.dimension]) + 'px');
            }
        }

        if (window.localStorage) {
            localStorage.setItem(dragger.resizeablePanel.identify() + layoutModule.PANEL_WIDTH,
                dragger.resizeablePanel.getWidth());
        }

        //could not size the panel? restore position of sizer and style of panel
        if (!sizeTracker.changed(dragger.resizeablePanel[dragData.sizeProperty])) {
			var actualSize = getActualDimension(dragger.resizeablePanel, dragData.dimension);
            if (dragger.alignment[dragData.axis] != dragData.myAlignments[0]) {
                //if sizer not naturally left/top aligned, remove left/top styling added by scriptaculous dragger
                dragger.element.style[dragData.myAlignments[0]] = "";
            }
            dragger.element.setStyle(dragger.alignment[dragData.axis] + ':' + dragger.parentOffset[dragData.axis]);
            dragger.resizeablePanel.setStyle(dragData.dimension + ':' + actualSize + 'px');
        	if (dragger.primaryPanel && (dragger.primaryPanel.style.zIndex == dragger.resizeablePanel.style.zIndex)) {
				dragger.primaryPanel.setStyle(dragData.sizeableAnchorage + ':' + (actualSize) + 'px');
			}
            if (dragger.filler) {
                dragger.filler.setStyle(dragData.dimension + ':' + (actualSize - dragger['fillerDelta'+dragData.dimension]) + 'px');
            }
            return;
        }

        var minWidth;
        if (dragger.resizeablePanel.currentStyle){
            minWidth = dragger.resizeablePanel.currentStyle.minWidth;
        }
        else{
            minWidth = document.defaultView.getComputedStyle(dragger.resizeablePanel,null);
            minWidth = minWidth['min-width'] ? minWidth['min-width'] : minWidth['minWidth'];
        }
        minWidth = minWidth.substring(0,minWidth.indexOf('px'));

        //size primary panel if any (and in same layer)
        if (dragger.primaryPanel && (dragger.primaryPanel.style.zIndex == dragger.resizeablePanel.style.zIndex)) {
			var primaryOffset = getDimensionInPx(dragger.primaryPanel, dragData.sizeableAnchorage) + dragData.dragDelta;
            var newOffset = primaryOffset < minWidth ? minWidth : primaryOffset;
            dragger.primaryPanel.setStyle(dragData.sizeableAnchorage + ':' + newOffset + 'px'); //resize main (middle) panel if any
        }

        //finally restore sizer`s alignment w/ respect to its parent
        if (dragger.alignment[dragData.axis] != dragData.myAlignments[0]) {
            dragger.element.style[dragData.myAlignments[0]] = "";
        }
        dragger.element.setStyle(dragger.alignment[dragData.axis] + ':' + dragger.parentOffset[dragData.axis]);

        jQuery('body').trigger('layout_update');
    },

    isInBounds: function(dragger, dragData) {
        //just init variables
        var primaryMinMeasurement,sizeableAnchorageMeasurement,
            avaliableParentMeasurement,pimaryPanelMarginMeasurement, outerDimentionFunc,
            primaryPanel = jQuery(dragger.primaryPanel),
            primaryPanelParent = primaryPanel.parent(),
            dimention = dragData.dimension;

        if (dragger.primaryPanel) {
            //uppercase first charter
            dimention = dimention.replace(dimention.charAt(0), dimention.charAt(0).toUpperCase());
            //chose function to get current dimmention
            outerDimentionFunc = jQuery.fn['outer'+dimention];

            //calculate measurement
            pimaryPanelMarginMeasurement = outerDimentionFunc.call(primaryPanel, true) - outerDimentionFunc.call(primaryPanel);
            primaryMinMeasurement = parseInt(primaryPanel.css("min-" + dragData.dimension));
            if (isNaN(primaryMinMeasurement)) {
                primaryMinMeasurement = 0;
            }
            sizeableAnchorageMeasurement = parseInt(primaryPanel.css(dragData.sizeableAnchorage));
            avaliableParentMeasurement = parseInt(primaryPanelParent.css(dragData.dimension));

            //if parent container has other divs then include their distentions in bound validation
            var panels = primaryPanelParent.children('div:visible:not(.minimized)');
            jQuery.each(panels, function() {
                var elem = jQuery(this),
                    isTemplatable = elem.width() === 0 || elem.height() === 0;
                if (dragger.resizeablePanel != this && dragger.primaryPanel != this && !isTemplatable) {
                    avaliableParentMeasurement -= outerDimentionFunc.call(elem, true);
                }
            });

            return avaliableParentMeasurement - sizeableAnchorageMeasurement - dragData.dragDelta > primaryMinMeasurement + pimaryPanelMarginMeasurement;
        } else {
            return true;
        }
    },

    getSizeTracker: function() {
        lastSize = -1; // jshint ignore: line

        return {
            checkpoint: function(size){
                lastSize = size; // jshint ignore: line
            },
            changed: function(size){
                return size != lastSize; // jshint ignore: line
            }
        }
    },

    ///////////////////////////////////////////////////////////////////////////////////
    // Make moveable
    ///////////////////////////////////////////////////////////////////////////////////

    createMover : function(movable){
        //movable is the element we will move
        //mover is the handle we drag to move it
        var mover = movable.down(this.MOVER_PATTERN); //get first descendant with class mover
        if (!mover || matchMeOrUp(movable,"." + layoutModule.HIDDEN_CLASS)) {return;}

        // Default zindex in Draggable#initialize is 1000
        // But some dialogs can have zindex over 1000

        var zIndex = Math.max(
            this.DND_MOVABLE_ZINDEX,     // taken from hardcoded default in Draggable#initialize
            movable.getStyle('zIndex')); // Some dialogs can have zindex over constant zindex

        var dragger = new Draggable(movable, {
            handle: mover,
            zindex: zIndex    // To move dialog on foreground we need initialize DnD with big zIndex
        });
    },

    ///////////////////////////////////////////////////////////////////////////////////
    // Minimizer / Maximizer
    ///////////////////////////////////////////////////////////////////////////////////

    maximize : function(elem, noEffects){
    	noEffects = true;
        var element = elem.hasClassName ? elem : $(elem);
        var toMaximize = element.hasClassName('column') ? element : element.up('.column');
        /*
         * Special case for Adhoc Designer on iPad.
         */
        if( window.orientation === 0){
            toMaximize.id == 'filters' && layoutModule.minimize(document.getElementById('fields'));
            toMaximize.id == 'fields' && layoutModule.minimize(document.getElementById('filters'));
        }

        var aligments = layoutModule.alignmentModule.alignments[0];
        var offsets = aligments.map(function(al) {
            return parseInt(toMaximize.getStyle(al));
        });
        var thisAlignment = aligments[offsets[0] === 0 ? 0 : 1]; //left at 0, right at 1
        var primaryPanel = toMaximize.siblings().find(function(s) {
            return s.match('.primary')
        });
        var ppOffset;
        if (primaryPanel && window.localStorage) {
            ppOffset = localStorage.getItem(toMaximize.identify() + layoutModule.PANEL_WIDTH);
        }

        function saveState() {
            toMaximize.removeClassName('minimized_if_vertical_orientation');
            toMaximize.removeClassName('minimized');
            toMaximize.addClassName("maximized");

            window.localStorage && localStorage.setItem(toMaximize.identify() + layoutModule.MINIMIZED, false);
            jQuery('div.content',toMaximize).show();
        }

        if (ppOffset && !isNaN(ppOffset)) {
            ppOffset += 'px';

            toMaximize.setStyle({width: ppOffset});
            if (noEffects) {
                primaryPanel.style[thisAlignment] = ppOffset;

                toMaximize.removeClassName('minimized_if_vertical_orientation');
                toMaximize.removeClassName('minimized');
                toMaximize.addClassName("maximized");

                window.localStorage && localStorage.setItem(toMaximize.identify() + layoutModule.MINIMIZED, false);
                jQuery('div.content',toMaximize).show();
                jQuery('div.vtitle',toMaximize).hide();
            } else {
                //This...
                new Effect.Morph(primaryPanel, {style: thisAlignment + ":" + ppOffset, duration: 0.6, afterFinish: function(){jQuery('body').trigger('layout_update');}});
                //...runs parallel to these two...
                new Effect.Opacity(toMaximize, {to: 0, from: 1, duration: 0.3, queue: {position: 'front', scope: 'sidePanel'},
                    afterFinish: saveState});
                new Effect.Opacity(toMaximize, {to: 1, from: 0, duration: 0.3, queue: {position: 'end', scope: 'sidePanel'}});
            }
        }
        jQuery(toMaximize).find(".sizer").show();
        jQuery('body').trigger('layout_update',{elem: elem.parentNode, type: 'panel-maximize'});
        jQuery('.column.secondary > div.content > .body').show().height();
    },

    minimize : function(elem, noEffects){
        var primaryPanel, ppOffset;
        noEffects = true;

        var element = elem.hasClassName ? elem : $(elem);
        var toMinimize = element.hasClassName('column') ? element : element.up('.column');
        var isMinimized = window.localStorage && localStorage.getItem(toMinimize.identify() + layoutModule.MINIMIZED) == 'true';

        !isMinimized && window.localStorage && localStorage.setItem(toMinimize.identify() + layoutModule.PANEL_WIDTH,
            toMinimize.getWidth());

        //figure out alignment (left or right)
        var aligments = layoutModule.alignmentModule.alignments[0];
        var offsets = aligments.map(function(al){
            return parseInt(toMinimize.getStyle(al));
        });
        var thisAlignment = aligments[offsets[0] === 0 ? 0 : 1]; //left at 0, right at 1

        primaryPanel = toMinimize.siblings().find(function(s) {return s.match('.primary')});

        function saveState() {
            toMinimize.addClassName("minimized");
            toMinimize.removeClassName("maximized");
            toMinimize.removeClassName('minimized_if_vertical_orientation');

            window.localStorage && localStorage.setItem(toMinimize.identify() + layoutModule.MINIMIZED, true);

            jQuery('div.content',toMinimize).hide();
        }

        if (primaryPanel) {
            ppOffset = primaryPanel.getStyle(thisAlignment);
            if (noEffects) {
                var title = jQuery('div.title',toMinimize).eq(0);
                var vTitle = jQuery('div.vtitle',toMinimize);
                var headerText = title.html();
                var w = title.width();
                w = jQuery.trim(title.text()).length * 7;

            	primaryPanel.style[thisAlignment] = '24px';

                toMinimize.addClassName("minimized");
                toMinimize.removeClassName("maximized");
                toMinimize.removeClassName('minimized_if_vertical_orientation');
                window.localStorage && localStorage.setItem(toMinimize.identify() + layoutModule.MINIMIZED, true);

                if(!vTitle.length) {
                    vTitle = jQuery('<div class="vtitle"></div>').appendTo(toMinimize);
                    vTitle.on('mouseup touchend',function(evt){
                        layoutModule.maximize(vTitle[0]);
                        evt.preventDefault();
                    });
                    vTitle.html(jQuery.trim(headerText)).width(w+24);
                }
                if(isIE7() || isIE8() || isIE9()){
                    vTitle.css({
                        top:0,
                        left:0
                    });
                } else {
                    vTitle.css('top',w+24+'px');
                }
                if(location.href.indexOf('nui=1') > 0) {
                    vTitle.hide();
                } else {
                    vTitle.show();
                }

                jQuery('div.content',toMinimize).hide();
            } else {
                //This...
                new Effect.Morph(primaryPanel, {style: thisAlignment + ':0px', duration: 0.6, afterFinish: function(){jQuery('body').trigger('layout_update');}});
                //...runs parallel to these two...
                new Effect.Opacity(toMinimize, {to: 0, from: 1, duration: 0.3, queue: {position: 'front', scope: 'sidePanel'},
                    afterFinish: saveState});
                new Effect.Opacity(toMinimize, {to: 1, from: 0, duration: 0.3, queue: {position: 'end', scope: 'sidePanel'}});
            }
        }
        jQuery(toMinimize).find(".sizer").hide();
        jQuery('body').trigger('layout_update',{elem: elem.parentNode, type: 'panel-minimize'});
    },

    resizeOnClient: function(leftPanelID, mainPanelID, rightPanelID) {
        var panels = [];
        var jMainPanelElement = jQuery('#'+mainPanelID);
        var diff = jQuery('#display').width() - 20 - parseInt(jMainPanelElement.width());
        var minWidth;
        /*
         * Get panel state and dimensions
         */
        jQuery.each([leftPanelID,rightPanelID],function(i,v){
            if(v) {
                panels.push({id: v, isLeft: i === 0,  jo: jQuery('#'+v), width: window.localStorage ? parseInt(localStorage.getItem(v + layoutModule.PANEL_WIDTH)) : 250 });

                panels[i].minimized = window.localStorage ? localStorage.getItem(v + layoutModule.MINIMIZED) : false;
                panels[i].minimized = !Object.isUndefined(panels[i].minimized) ? (panels[i].minimized == "true") : panels[i].jo.hasClass("minimized");

                minWidth = panels[i].jo[0].currentStyle ?
                    panels[i].jo[0].currentStyle.minWidth :
                    document.defaultView.getComputedStyle(panels[i].jo[0],null)['minWidth'];

                panels[i].minWidth = (minWidth && minWidth.indexOf('px')) > 0 ? minWidth.substring(0,minWidth.indexOf('px')) : 200;
                panels[i].width = panels[i].width ? panels[i].width : panels[i].jo.width();
                diff -= panels[i].width;
            }
        });
        var offset = diff < 0 ? Math.abs(diff) : 0;
        diff = 0;
        jQuery.each(panels,function(i,panel) {
            if (panel.jo.length && panel.jo.is(":visible")) {
                if (panel.minimized) {
                    panel.jo.removeClass("maximized").addClass("minimized");
                    layoutModule.minimize(panel.jo[0], true);
                }
                else {
                    if(i == 1 && !panels[0].minimized && panels[0].jo.is(":visible")) {
                        panel.width -= offset;
                        if(panel.width < panel.minWidth) {
                            diff = panel.width - panel.minWidth;
                            panel.width = panel.minWidth;
                        }
                    }
                    panel.jo.removeClass("minimized").addClass("maximized");
                    jMainPanelElement.css(panel.isLeft ? 'left' : 'right', panel.width + 'px');
                    panel.jo.css('width', panel.width + 'px');
                }
            }
        });
        if(diff !== 0) {
            panels[0].width += diff;
            panels[0].width = panels[0].width < panels[0].minWidth ? panels[0].minWidth : panels[0].width;
            jMainPanelElement.css('left', panels[0].width + 'px');
            panels[0].jo.css('width', panels[0].width + 'px');
        }
    },

    autoMaximize: function(element) {
        var minimized = window.localStorage ? localStorage.getItem(element.identify() + layoutModule.MINIMIZED) : 'false';

        if (minimized == 'true') {
            element.hasClassName('minimized') && layoutModule.maximize(element, true);
        } else {
            !element.hasClassName('minimized') && layoutModule.minimize(element, true);
        }
    },

    autoMinimize: function(element) {
        var minimized = window.localStorage ? localStorage.getItem(element.identify() + layoutModule.MINIMIZED) : 'false';
        if (minimized == 'false') {
            layoutModule.minimize(element, true);
        } else {
            element.removeClassName('minimized');
        }
    },

	fixIE7Sizes: function() {
		$$(".control.tabSet.buttons.vertical").each(function(elem) {
			elem.immediateDescendants().each(function(child) {
				child.style.width = elem.clientWidth || elem.offsetWidth;
			});
		});
	},

    storePanelWidth: function(panelName, width) {
        window.localStorage && localStorage.setItem(panelName + layoutModule.PANEL_WIDTH, width);
    },

    getPanelWidth: function(panelName) {
        return window.localStorage ? localStorage.getItem(panelName + layoutModule.PANEL_WIDTH) : undefined;
    },

    getPanelMinimizedState: function(panelName) {
        return window.localStorage ? localStorage.getItem(panelName + layoutModule.MINIMIZED) : undefined;
    },

    storePanelMinimizedState: function(panelName, value) {
        window.localStorage && localStorage.setItem(panelName + layoutModule.MINIMIZED, value.toString());
    },

    manuallyChangePanelState: function(panelName, clientKey, value) {
        window.localStorage && localStorage.setItem(panelName + layoutModule.PANEL_STATE_CHANGED_MANUALLY + clientKey,
            value);
    },

    panelStateWasManuallyChanged: function(panelName, clientKey) {
        return window.localStorage ?
            localStorage.getItem(panelName + layoutModule.PANEL_STATE_CHANGED_MANUALLY + clientKey) === "true" : false;
    }
};



