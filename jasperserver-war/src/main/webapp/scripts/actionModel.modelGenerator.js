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
 * @author Angus Croll, Papanii Okai
 * @version: $Id$
 */

/* global localContext, isProVersion, designerBase, isIE, isIE7, isNotNullORUndefined,
 isSupportsTouch, fitObjectIntoScreen, primaryNavModule, _, getAsFunction, getWindowHeight, getWindowWidth, alert,
 buttonManager, layoutModule, toFunction */

/**
 * ActionModel Object Originally written by Angus Croll.
 * Refactored for VFR project by Papanii Okai (a.k.a The Code Pimp)
 */

if (typeof(localContext) == "undefined"){
    localContext = window // jshint ignore: line
}
//var localContext = window;
var actionModel = {};

//action constants
actionModel.SIMPLE_ACTION = "simpleAction";
actionModel.SELECT_ACTION = "selectAction";
actionModel.INPUT_ACTION = "inputAction";
actionModel.OPTION_ACTION = "optionAction";
actionModel.SEPARATOR = "separator";
actionModel.SINGLE_SELECT_CONSTRAINT = "singleSelect";
actionModel.HIGH_Z_INDEX = 99999;

//reserved variables
actionModel.RES_LABEL = "$label";
actionModel.RES_ID = "$id";
actionModel.RES_SELECTED = "$selected";
actionModel.EVENT = "$event";

actionModel.selObjects = [];
actionModel.lastItemWasSeparator = false;
actionModel.noItemsYet = true;
actionModel.data = null;
actionModel.openedSubMenus = [];

actionModel.parentMenu = "";
actionModel.menuDom = null;
actionModel.menuListDom = null;
actionModel.PARENT_MENU_CONTAINER = "menuList";
actionModel.PARENT_MENU_STYLE = "menu vertical context hidden";
actionModel.SIMPLE_ACTION_DOM_ID = "menuList_simpleAction";
actionModel.SEPARATOR_DOM_ID = "menuList_separator";
actionModel.FLYOUT_PARENT_DOM_ID = "menuList_flyout";
actionModel.EXTRA_INPUT_DOM_ID = "menuList_extraInput";
actionModel.LIST_ITEM_DOM_ID = "menuList_listItem";
actionModel.DISPLAY_STYLE = "inline";

//patterns
actionModel.DROP_DOWN_MENU_CLASS = "dropDown";
actionModel.MOUSE_OUT_PATTERNS = [
    "#"+layoutModule.MENU_ID,
    "."+layoutModule.MENU_ROOT_CLASS,
    ".toolbar .capsule",
    ".header > .button.mutton",
    "#"+layoutModule.MENU_ID+" *",
    layoutModule.MENU_ROOT_CLASS+" *"
];

////////////////////////////////////////////////////////////////////
// Menu building
////////////////////////////////////////////////////////////////////


/*
 * Used to present the context menu to the user.
 * @param menuContext
 * @param event the event tied to the menu.
 * @param className class name a caller may wish to use instead of the default.
 * @param coordinates used for menu positioning.
 * @actionModelScriptTag
 * @updateContextActionModel - optional method which can update context action model
 */
actionModel.showDynamicMenu = function(menuContext, event, className, coordinates, actionModelScriptTag, updateContextActionModel){
    if(menuContext == null){
        if(isProVersion()){
            var selectionObj = designerBase.getSelectedObject();
            if (selectionObj && selectionObj.menuLevel) {
                menuContext = selectionObj.menuLevel;
            }else {
                return; //no context - nothing to show
            }
        } else {
            return;
        }
    }

    actionModel.resetMenu();
    actionModel.updateCSSClass(className);
    actionModel.initActionModelData(actionModelScriptTag);
    actionModel.assembleMenuFromActionModel(menuContext, event, actionModel.menuListDom, updateContextActionModel);
    actionModel.setMenuPosition(actionModel.menuDom, event, coordinates);
    actionModel.setMenuEventHandlers(actionModel.menuDom);
    if(isIE()) {
        jQuery('#'+actionModel.menuDom.id).prepend('<div class="msshadow"></div>');
    }
    actionModel.makeMenuVisible(actionModel.menuDom);
    actionModel.adjustMenuPosition(actionModel.menuDom);
    //actionModel.focusMenu();

    jQuery(actionModel.MOUSE_OUT_PATTERNS.join(",")).on('mouseout', actionModel.closeHandler);
};

actionModel.closeHandler = function (event) {
    var target = jQuery(event.relatedTarget);
    if (((target.hasClass("mutton") || target.parents(".mutton").length) && event.relatedTarget) ||
        (target.attr("id") === "menu" || target.parents("#menu").length)) {  //|| actionModel.lastInput=="key"
        //event.stopPropagation();
    }
    else {
        jQuery('body').trigger('actionmodel-mouseout');
        actionModel.hideMenu();
    }
};

/*
 * Hide any menus created by the action model.
 * This means removing all child nodes and setting it back to default state.
 */
actionModel.resetMenu = function(){
    var dom = $("menu");
    dom.parentId = undefined;
    dom.setAttribute("tabIndex",-1);
    dom.childElements().each(function(child){Element.remove(child);});
    //add common template
    dom.innerHTML = $('commonMenu').cloneNode(true).innerHTML;
    var menuList = dom.down("ul");
    var menuListId = menuList.readAttribute("id");
    menuList.writeAttribute("id", actionModel.updateTemplateId(menuListId));
    //remove any generated styles.
    dom.className = actionModel.PARENT_MENU_STYLE;
    dom.writeAttribute("style", "");
    actionModel.menuDom = dom;
    actionModel.menuListDom = menuList;
    // One of: ["none","keyboard","mouse","touch","automation"].
    actionModel.lastInputSrc="none";
};

/**
 * Used to update a template's id
 */
actionModel.updateTemplateId = function(templateId){
    var re = new RegExp("_template");
    return templateId.replace(re, "");
};


/*
 * Method used to update the menu's css className
 * @param className
 */
actionModel.updateCSSClass = function(className){
    if(isNotNullORUndefined(className)){
        $(actionModel.menuDom).className = className + " hidden";
    }
};

/*
 *  Determine position of menu based on mouse position..
 * @param event
 */
actionModel.setMenuPosition = function(menuObj, event, coordinates){
    var ev = event ? event : window.event,
        location;

    if (ev){
        if (isSupportsTouch() && ev.changedTouches) {
            // Touch-initiated.
            location = {
                x: ev.changedTouches[0].pageX + 20,
                y: ev.changedTouches[0].pageY
            }
        } else {
            // Mouse-initiated.
            location = Event.pointer(ev);
        }
    } else {
        // Keyboard-initiated.
        location = {
            x: 0,
            y: 0
        }
    }

    var leftOffset;
    var topOffset;

    //if coordinates are provided, override.
    if(isNotNullORUndefined(coordinates)){
        if(isNotNullORUndefined(coordinates.menuLeft)){
            leftOffset = coordinates.menuLeft;
        }else{
            leftOffset = location.x;
        }
        if(isNotNullORUndefined(coordinates.menuTop)){
            topOffset = coordinates.menuTop;
        }else{
            topOffset = location.y;
        }
    }else{
        leftOffset = location.x;
        topOffset = location.y;
    }

    //set top left to the mouse click
    menuObj.setStyle({
        'left': leftOffset + 'px',
        'top' : topOffset +'px'
    });
};

actionModel.adjustMenuPosition = function(menu, left, top, width, height){
    if(!$(menu).hasClassName(this.DROP_DOWN_MENU_CLASS) || $(menu).hasClassName("fitable")){
        fitObjectIntoScreen(menu, null, top, null, height);
    }
};

/*
 * construct the menu dynamically based on the client side action model
 * @param menuContext
 * @param event
 * @updateContextActionModel - optional method which can update context action model
 */
actionModel.assembleMenuFromActionModel = function(menuContext, event, contentParent, updateContextActionModel) {
    actionModel.noItemsYet = true;
    var contextActionModel = actionModel.data[menuContext];
    //In this place we can update action model for specified context on the fly.
    //This is necessary to have ability to construct fully dynamic menus which items can be calculated only
    //right before showing menu
    if (updateContextActionModel){
        contextActionModel = updateContextActionModel(menuContext, contextActionModel);
    }
    contextActionModel.each(function(thisAction){
        actionModel.appendToMenu(thisAction, event, contentParent)
    });
    actionModel.removeTrailingSeparator();
};

/*
 * Initialize Action Model data with the JSON evaluated from the given element or directly from object.
 *
 * @param scriptTag Script holder identifier or simple object representing data.
 */
actionModel.initActionModelData = function(scriptTag) {
    if(scriptTag === "navigationActionModel"){
        actionModel.data = primaryNavModule.JSON;
    }else {
        actionModel.data = _.isString(scriptTag) ? jQuery('#' + scriptTag).html().evalJSON() : scriptTag;
    }
};

/*
 * Helper method used in the construction of the menu
 * @param thisAction
 * @param event
 */
actionModel.appendToMenu = function(thisAction, event, contentParent) {
    if (!actionModel.passesClientTest(thisAction)) {
        return; //does not satisfy client-side condition to appear in this menu
    }

    var mouseUpFunction = getMenuMouseupFunction(
            getAsFunction(thisAction.action),
            thisAction.actionArgs,
            thisAction.text,
            thisAction.id,
            event);

    actionModel.appendDesiredRowType(thisAction, event, mouseUpFunction, contentParent);
};

/*
 * Append desired row type to menu
 * @param thisAction
 * @param mouseUpFunction
 * @param contentParent
 * @param event
 */
actionModel.appendDesiredRowType = function(thisAction, event, mouseUpFunction, contentParent){
    var mouseUpFunctionWithMenuHiding = function() {
        if (actionModel.isMenuShowing()){
            actionModel.hideMenu();
        }
        mouseUpFunction && mouseUpFunction();
    };
    //add new <li> element
    if (thisAction.type == actionModel.SIMPLE_ACTION) {
        actionModel.addSimpleActionRow(thisAction, mouseUpFunctionWithMenuHiding, contentParent);
    }
    //add new <li> separator
    if (thisAction.type == actionModel.SEPARATOR && !actionModel.lastItemWasSeparator) {
        actionModel.addSeparatorRows(thisAction, contentParent);
    }
    //add new <li> flyout or new window?
    if (thisAction.type == actionModel.SELECT_ACTION) {
        actionModel.addSelector(thisAction, contentParent, event);
    }
    //add new <li> option
    if (thisAction.type == actionModel.OPTION_ACTION) {
        actionModel.addOption(thisAction, mouseUpFunctionWithMenuHiding, contentParent);
    }
};


/*
 * Add a simple action row to the menu
 * @param thisAction
 * @param mouseUpFunction
 * @param contentParent
 */
actionModel.addSimpleActionRow = function(thisAction, mouseUpFunction, contentParent) {
    var newMenuRow = null;
    if(thisAction.className && (thisAction.className == "requiresInput")){
        newMenuRow = $(actionModel.EXTRA_INPUT_DOM_ID).cloneNode(true);
    }else{
        newMenuRow = $(actionModel.SIMPLE_ACTION_DOM_ID).cloneNode(true);
    }
    if (isSupportsTouch()) {
        newMenuRow.ontouchend = mouseUpFunction;
    } else {
        newMenuRow.onmouseup = mouseUpFunction;
    }
    newMenuRow.onmouseup_saved = mouseUpFunction;
    var textPlacement = $(newMenuRow).down(".button");
    var newText = document.createTextNode(thisAction.text);
    newText.nodeValue = replaceNbsps(newText.nodeValue);
    textPlacement.appendChild(newText);

    //set new id to make it unique.
    var menuId = newMenuRow.readAttribute("id") + getRandomId();
    newMenuRow.writeAttribute("id", menuId);

    actionModel.insertMenuRow(contentParent, newMenuRow, false, thisAction.isDisabled, thisAction.id, null);
};


/*
 * Add a separator row to the menu
 * @param thisAction
 * @param contentParent
 */
actionModel.addSeparatorRows = function(thisAction, contentParent){
    var cssOverride = null;
    if (actionModel.noItemsYet) {
        return; //don't need separator at very top;
    }
    var separator = $(actionModel.SEPARATOR_DOM_ID).cloneNode(true);
    var separatorId = separator.readAttribute("id") + getRandomId();
    separator.writeAttribute("id", separatorId);
    if(thisAction.className){
        cssOverride = thisAction.className;
    }
    actionModel.insertMenuRow(contentParent, separator, true, thisAction.isDisabled, null, cssOverride);
};

/*
 * Used to add a option
 * @param thisAction
 * @param mouseUpFunction
 * @param contentParent
 */
actionModel.addOption = function(thisAction, mouseUpFunction, contentParent){
    if (!actionModel.passesClientTest(thisAction)) {
        return; //does not satisfy client-side condition to appear in this menu
    }

    var cssOverride = null;
    var newOptionRow = $(actionModel.LIST_ITEM_DOM_ID).cloneNode(true);
    thisAction.button = (thisAction.button && String(thisAction.button).toLowerCase()=="true") ? true : false;
    if (isSupportsTouch()) {
        newOptionRow.ontouchend = mouseUpFunction;
    } else {
        newOptionRow.onmouseup = mouseUpFunction;
    }
    newOptionRow.onmouseup_saved = mouseUpFunction;
    var textPlacement = $(newOptionRow).down(".button");
    var newText = document.createTextNode(thisAction.text);
    newText.nodeValue = replaceNbsps(newText.nodeValue);

    textPlacement.appendChild(newText);
    var newOptionRowId = newOptionRow.readAttribute("id") + getRandomId();
    newOptionRow.writeAttribute("id", newOptionRowId);//update dom id

    if(thisAction.className){
        cssOverride = thisAction.className;
    }

    if (!thisAction.button && evaluateTest(thisAction.isSelectedTest, thisAction.isSelectedTestArgs, thisAction.text, thisAction.id, false, false)) {
        textPlacement.addClassName("down");
    }

    actionModel.insertMenuRow(contentParent, newOptionRow, false, thisAction.isDisabled, thisAction.id, cssOverride);
};

/*
 * Creates selector row and all sub menus related to that row
 * @param thisAction
 * @param contentParent
 * @param event
 */
actionModel.addSelector = function(thisAction, contentParent, event) {
    //since in the action model we specify the type of menu using the class nomenclature, we can check what
    //type of selector we want by simply checking the class in the action object
    var parentSelector;
    var className = thisAction.className;
    if(className == "flyout"){
        //do not render flyout list item if no submenu items exist
        if (thisAction && (thisAction.children.length > 0)) {
            parentSelector = $(actionModel.FLYOUT_PARENT_DOM_ID).cloneNode(true);
            var textPlacement = $(parentSelector).down(".button");
            var newText = document.createTextNode(thisAction.text);
            newText.nodeValue = replaceNbsps(newText.nodeValue);
            textPlacement.appendChild(newText);
            var parentSelectorId = parentSelector.readAttribute("id") + getRandomId();
            parentSelector.writeAttribute("id", parentSelectorId);
            actionModel.insertMenuRow(contentParent, parentSelector, false, thisAction.isDisabled, thisAction.id, null);
            //now create submenu
            var submenu = actionModel.menuDom.cloneNode(false);
            submenu.writeAttribute("id", parentSelector.readAttribute("id") + "_subMenu");
            //add common template
            submenu.innerHTML = $('commonMenu').cloneNode(true).innerHTML;
            var submenuList = submenu.down("ul");
            var submenuListId = submenuList.readAttribute("id");
            submenuList.writeAttribute("id", actionModel.updateTemplateId(submenuListId));
            //get containers width.
            parentSelector.appendChild(submenu);
            var subMenuContainer = submenu.select('ul')[0];
            var subMenuContainerId = subMenuContainer.writeAttribute("id", subMenuContainer.readAttribute("id") + "_subMenu");
            actionModel.buildSubMenu(thisAction, subMenuContainerId, event);
        }
    } else {
        thisAction.children.each(function(childAction){
            var mouseUpFunction = getMenuMouseupFunction(
                    getAsFunction(childAction.action),
                    childAction.actionArgs,
                    childAction.text,
                    childAction.id,
                    event);
            actionModel.appendDesiredRowType(childAction, event, mouseUpFunction, contentParent);
        });
    }
};

/*
 * This method gets the left value for a submenu. It calculates if by getting the with of the parents content and adds
 * the parents left value to it since all menu objects are absolute.
 * @param selectorObj
 */
actionModel.getSubMenuLeft = function(selectorObj){
    //<div menu>
    var width = $(selectorObj).up(2).clientWidth; //width of parent window
    var leftOffSet = Math.abs(parseInt($(selectorObj).up(1).offsetLeft)); //offset
    return (width + leftOffSet);
};

actionModel.getSubMenuTop = function(selectorObj){
    return Math.abs(parseInt($(selectorObj).offsetTop)); //offset
};

/*
 * Used to position the submenu with respect to its parent.
 * @param parent
 */
actionModel.showChildSubmenu = function(parent){
    var pid = parent.parentNode.parentNode.parentNode.id;

    /*
     * Hides submenus previously open
     */
    var i;
    var j;
    var f = false;
    for(i=0;i<actionModel.openedSubMenus.length;i++){
        if(actionModel.openedSubMenus[i] == pid) {
            j = i;
            f = true;
            jQuery('#'+pid+'>div.content>ul').children('li.node').each(function(){
                actionModel.hideChildSubmenu(jQuery(this).get(0));
            })
        }
        if(f) {
            jQuery('#'+actionModel.openedSubMenus[i]+'>div.content>ul').children('li.node').each(function(){
                actionModel.hideChildSubmenu(jQuery(this).get(0));
            })
        }
    }
    actionModel.openedSubMenus.splice(j,actionModel.openedSubMenus.length-j);

    var submenuTop = null;
    var submenuHeight = null;
    var submenuWidth = null;
    var bottomPadding = 20; //hack to prevent  bug20863
    var submenu = parent.childElements()[1];

    //applying style for flyout submenu....
    submenu.setStyle({display: actionModel.DISPLAY_STYLE});
    submenu.setStyle({position:'absolute'});
    var leftValue = actionModel.getSubMenuLeft(parent);
    var topValue = 0;
    //    var topValue = actionModel.getSubMenuTop(parent) + "px";
    //set top left to the mouse click
    submenu.setStyle({
        'left': leftValue + "px",
        'top' : topValue + "px"
    });
    if(isIE()) {
        jQuery(submenu).prepend('<div class="msshadow"></div>');
    }
    actionModel.makeMenuVisible(submenu);
    submenuTop  = $(submenu).cumulativeOffset()[1];
    submenuHeight = $(submenu).getHeight();

    //hack to prevent  bug20863  (Due to Tim's markup change)
    var windowHeight = getWindowHeight();
    var submenuBottom = submenuTop + submenuHeight;
    if(windowHeight < submenuBottom){
        topValue = (windowHeight - submenuBottom - bottomPadding);
        submenu.setStyle({
            'top' : topValue + "px"
        });
    }

    var submenuLeftOffset = submenu.cumulativeOffset()[0];
    submenuWidth = submenu.getWidth();

    if(getWindowWidth() < (submenuLeftOffset + submenuWidth)){
        submenu.setStyle({
            'left' : "-" + submenuWidth + "px"
        });
    }

    actionModel.openedSubMenus.push(pid);
};

/*
 * Used to hide submenu
 * @param parent
 */
actionModel.hideChildSubmenu = function(parent){
    var submenu = parent.childElements()[1];
    submenu.setStyle({display: "none"});
    submenu.setStyle({position:'absolute'});
    submenu.setStyle({left: 0});
    actionModel.makeMenuInVisible($(submenu));
};

/*
 * Used to change the css display value to make it visible.
 * @param menu object we changing the display style for.
 */
actionModel.makeMenuVisible = function(menu){
    //test to see if we have any items to show
    var list = menu.down("ul");
    if(list.childElements().length > 0){
        menu.setStyle({zIndex: actionModel.HIGH_Z_INDEX});
        $(menu).removeClassName("hidden");
    }
};

/*
 * Used to change the css display value to make it invisible
 * @param menu object we changing the display style for.
 */
actionModel.makeMenuInVisible = function(menu){
    menu.addClassName("hidden");
};

/*
 * Used to build a the logical structure for a flyout menu
 * @param thisAction
 * @param parentId
 * @param event
 */
actionModel.buildSubMenu = function(thisAction, parentId, event){
    thisAction.children.each(function(childAction){
        actionModel.appendToMenu(childAction, event, $(parentId));
    });
};


actionModel.isMenuShowing = function(){
    return !($("menu").getStyle("display") == "none" || $('menu').hasClassName("hidden"));
};

/*
 * Helper method to update row object
 * @param domObject
 * @param actionText
 */
actionModel.updateRowDom = function(domObject, actionText){
    if(domObject){
        domObject.innerHTML = actionText;
        var domObjectId = domObject.readAttribute("id") + getRandomId();
        domObject.writeAttribute("id", domObjectId);
    }
    return domObject;
};

/**
 * Used to launch new menu (E.g. Calculated fields.)
 */
actionModel.launchNewMenu = function(){
    actionModel.hideMenu();
    alert("action not implemented...");
};

/*
 * Helper to insert row in to menu
 * @param container
 * @param newMenuRow
 * @param isSeparator
 * @param isDisabled
 * @param userId
 */
actionModel.insertMenuRow = function(container, newMenuRow, isSeparator, isDisabled, userId, className) {
    //check to see if item has a id, if not set one..
    if(newMenuRow.readAttribute("id") == null){
        if (userId) {
            newMenuRow.setAttribute('id',userId);
        } else {
            newMenuRow.identify(); //sets unique id
        }
    }

    if (isDisabled) {
        actionModel.disableMenuOption(newMenuRow);
    }
    if(className){
        newMenuRow.addClassName(className);
    }
    container.appendChild(newMenuRow);
    actionModel.lastItemWasSeparator = isSeparator;
    actionModel.noItemsYet = false;
};

actionModel.disableMenuOption = function(menuOption){
    buttonManager.disable(menuOption);
    _.isEmpty(menuOption.childNodes) && buttonManager.disable(menuOption.childNodes[0]);
};

/*
 * Checking success of test.
 * @param action
 */
actionModel.passesClientTest = function(action) {
    var clientTestFunction;
    var result = true; //default to true because if there are no tests, we passed by default!
    //passes explicit test?
    
    if (action.clientTest) {
        clientTestFunction = action.clientTest;
        var testForNegative = false;
        if (clientTestFunction.startsWith("!")) {
            //negative test
            clientTestFunction  =  action.clientTest.sub("!","");
            testForNegative = true;
        }
        result = evaluateTest(clientTestFunction, action.clientTestArgs, action.text, action.id, testForNegative, null);
    }
    //number of selections ok?
    var selected = actionModel.selObjects;
    if (!_.isEmpty(selected)) {
        if (result && action.selectionConstraint) {
            result = action.selectionConstraint == actionModel.SINGLE_SELECT_CONSTRAINT ? selected.length == 1 : selected.length > 1;
        }
    }
    return result;
};

/***
 * If last row is a separator, remove it
 */
actionModel.removeTrailingSeparator = function(){
    var lastRow = actionModel.menuListDom.lastChild;
    if ($(lastRow) && !actionModel.noItemsYet) {
        var lastRowId = lastRow.identify();
        var reg = new RegExp("\w*separator\w*"); // jshint ignore: line
        if (reg.test(lastRowId)) {
            lastRow.remove();
        }
    }
};

/**
 * Only applies to certain menus (e.g. naviagtion menu has a parent mutton)
 */
actionModel.getMenuParent = function() {
    return $(actionModel.menuDom.parentId);
};

actionModel.getFirstMenuButton = function() {
    return actionModel.menuDom.down(layoutModule.BUTTON_PATTERN, 0);
};

/* Move focus into the menu, starting with the top item in the list.  This
 * function runs when a user presses the down arrow key from a top-level main
 * menu entry.
 */
actionModel.focusMenu = function() {
    var jQMenuDom=jQuery('#'+actionModel.menuDom.id);

    // If the menu already has focus, do nothing.
    if (!jQuery.contains(jQMenuDom, document.activeElement)){

        //will restore old focus when menu is closed
        actionModel.lastFocused = document.activeElement;
        //!menuDom.hasClassName(layoutModule.HIDDEN_CLASS) && menuDom.focus();

       //if (!jQMenuDom.hasClass(actionModel.DROP_DOWN_MENU_CLASS) && !is SupportsTouch()) {
       /*
       if (!jQMenuDom.hasClass(actionModel.DROP_DOWN_MENU_CLASS)) {
            var jQChildItems = jQMenuDom.children("li");
            if (jQChildItems[0]) {
                var firstButton = jQChildItems[0].find(layoutModule.BUTTON_PATTERN);
                firstButton && buttonManager.over(firstButton);
            }
       }
       */
       var jQChildItems=jQMenuDom.find(layoutModule.BUTTON_PATTERN);
       if (jQChildItems.length>0){
           $(jQChildItems[0]).focus();
       }
    }
};


/*
 * Used to display fine positioned menu relative to the object
 *
 * @param event Initial event
 * @param object Element that cause element to appear
 * @param context actionModel context in which we show this menu. Used to find proper metadata
 * @param cssClass Menu css class
 * @param aModel action model
 */
actionModel.showDropDownMenu = function(event, object, context, cssClass, aModel){
    object = jQuery(object);
    var offsets = object.offset(),
        coordinates = {
        "menuLeft":offsets.left,
        "menuTop":offsets.top + object.height() - 1
    };
    actionModel.showDynamicMenu(context, event, cssClass, coordinates, aModel);
};

////////////////////////////////////////////////////////////
// General section for menu events
////////////////////////////////////////////////////////////
/**
 * Register event handlers to menu objects
 */
actionModel.setMenuEventHandlers = function(menu){
    menu.select("li").each(function(object){
        /*
         * Special mouse-enter and mouse-leave events for fly-outs
         */
        if ((!object.hasClassName("disabled"))) {
            if (object.hasClassName("node")) {
                if (isSupportsTouch()) {
                    Event.observe(object, 'touchstart', function(event) {
                        if (!event.firstCall) {
                            actionModel.showChildSubmenu(object);
                        }
                        event.firstCall = true;
                    });
                    /*
                    Event.observe(object, 'touchend', function(event) {
                        var child = object.down();
                        buttonManager.up(child);
                        buttonManager.out(child);
                        event.stop();
                    });
                    */
                } else {
                    Event.observe(object, 'mouseenter', function(event) {
                        actionModel.showChildSubmenu(object);
                    });
                    Event.observe(object, 'mouseleave', function(event) {
                        actionModel.hideChildSubmenu(object);
                    });
                }
            }
        }
    });
};

actionModel.initializeOneTimeMenuHandlers = function() {
    /**
     * generic mouseenter for menu - to initialize for mouse use
     */
    // FIXME-- turn back on, disabled for debugging
    /*
    $('menu').observe('mouseenter', function(event) {
        var selected = this.select("." + layoutModule.HOVERED_CLASS)[0];
        selected && buttonManager.out(selected);
    }.bind($('menu')))
    */
};

/**
 * Public method used to hide a menu.
 */
actionModel.hideMenu = function(){
    if (actionModel.isMenuShowing()) {
        var setNextFocus = function(){
            var nextFocus = actionModel.lastFocused;
            try {
                nextFocus && nextFocus.focus();
            } catch(ex) {
                //IE gets bothered if you try to focus something that it can't
            }
        };
        !isSupportsTouch() && setTimeout(setNextFocus, 0); //timeout so that any current key action misses this focus
        actionModel.makeMenuInVisible($("menu"));
        jQuery(actionModel.MOUSE_OUT_PATTERNS.join(",")).off('mouseout', actionModel.closeHandler);
    }
};

/*
 * Utility method which helps to create menu element
 * @param type type of menu element, one of ["simpleAction", "selectAction", "optionAction"]
 *  this is the only mandatory parameter to create manu item.
 * @param options - object with optional arguments.
 *  Possible object structure is:
 * {
 *  text: test,
 *  clientTest: clientTest,
 *  clientTestArgs: clientTestArgs,
 *  action: action,
 *  actionArgs: actionArgs,
 *  children: children,
 *  isSelectedTest: isSelectedTest,
 *  isSelectedTestArgs: isSelectedTestArgs,
 *  selectionConstraint: selectionConstraint,
 *  className: className
 *  }
 */
actionModel.createMenuElement = function(type, options){
    if (!type) {
        throw new Error("Can not construct actionModel object: type can not be empty");
    }

    return Object.extend({type: type}, options);
};

actionModel.setSelected = function(selected) {
    actionModel.selObjects = selected;
};

////////////////////////////////////////////////////////////
// function utils
////////////////////////////////////////////////////////////

function replaceNbsps(str) {
    var re = new RegExp("&nbsp;", "g");
    return str.replace(re, " ");
}



function fireMenuAction(funcString, event) {
    var func = getAsFunction(funcString);
    if (!func) {
        return;
    }
    func();
}

function fireMenuActionWithEvent(args, funcString, event) {
    var func = toFunction(funcString), belongsToLocalContext;
    func = func || (belongsToLocalContext = toFunction("localContext." + funcString));
    if (!func) {
        return;
    }
    args = _.isArray(args) ? args : [args];
    func.apply(belongsToLocalContext ? localContext : null, args);
}

function getMenuMouseupFunction(actionFunction, argsToInvokeWith, label, id, event) {
    if (!actionFunction) {
        return null;
    }
    if (!argsToInvokeWith) {
        return function(){
            fireMenuAction(actionFunction, event);
        };
    } else {
        var args = resolveReservedStringsInArray(argsToInvokeWith, label, id, event);

        return function(){
            fireMenuActionWithEvent(args, actionFunction, event);
        };
    }
}


function evaluateTest(thisFunctionName, functionArgs, label, id, testForNegative, defaultResult) {
    var result = (defaultResult!=null) ? defaultResult : true; //if no default assume true;
    var theFunction = getAsFunction(thisFunctionName);
    if (theFunction) {
        result =  theFunction.apply(this,resolveReservedStringsInArray(functionArgs, label, id, null));
        if (testForNegative) {
            result = !result;
        }
    }
    return result;
}



function resolveReservedStringsInArray(oldArray, label, id, ev) {
    if (!oldArray) {
        return [];
    }
    return oldArray.collect(function(thisString) {return resolveReservedStrings(thisString, label, id, ev)});
}



function resolveReservedStrings(thisString, label, id, ev) {
    if (thisString==actionModel.RES_SELECTED) {
        return actionModel.selObjects;
    }
    if (thisString==actionModel.RES_LABEL) {
        return label;
    }
    if (thisString==actionModel.RES_ID) {
        return id;
    }
    if (thisString==actionModel.EVENT) {
        return ev;
    }
    return thisString;
}


/**
 * Get a random number.
 * May need to move this to common utils
 */
function getRandomId(){
    var rand =  Math.round(Math.random(10) * 50);
    return "_" + rand;
}
