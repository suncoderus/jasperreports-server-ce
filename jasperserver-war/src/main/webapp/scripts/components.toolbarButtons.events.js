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

/* global toolbarButtonModule, toolbarButtonModule, isFirefox, getAsFunction, buttonManager,  */

toolbarButtonModule.initialize = function(actionMap){
    toolbarButtonModule.actionMap = actionMap;
    jQuery('.toolbar').on('mouseup mouseover mouseout','button',function(evt){
        if(!jQuery(this).prop('disabled'))toolbarButtonModule['mouse' + evt.type.substring(5,6).toUpperCase() + evt.type.substring(6) + 'Handler'](evt,this);
    });

    if (isFirefox()) { // workaround for Bug 26223
        jQuery(".toolbar li").each(function(index,li){
             jQuery(li).css("padding", "0 2px");
        });
    }
};

toolbarButtonModule.mouseUpHandler = function(evt,el){
    /*
    if (element.hasClassName(toolbarButtonModule.CAPSULE_PATTERN)) {
        var elementId = element.readAttribute("id");
        var execFunction = toolbarButtonModule.actionMap[elementId];
        if (execFunction) {
            var executableFunction = getAsFunction(execFunction);
            executableFunction(evt);
            evt.stop();
        }
    }
    */
    var execFunction = el.className.indexOf('capsule') >= 0 ? toolbarButtonModule.actionMap[el.id] : null;
    if (execFunction) {
        var executableFunction = getAsFunction(execFunction);
        executableFunction(evt);
        evt.stopPropagation();
    }
};

toolbarButtonModule.mouseOverHandler = function(evt,el){
    /*
    if (element.hasClassName(toolbarButtonModule.CAPSULE_PATTERN)) {
        if (element.hasClassName("mutton") && !buttonManager.isDisabled(element)) {
            toolbarButtonModule.showButtonMenu(evt, element);
        }
    }
    */
    if(el.className.indexOf('capsule') >= 0 && el.className.indexOf('mutton') >= 0 && !buttonManager.isDisabled(el)){
        toolbarButtonModule.showButtonMenu(evt.originalEvent,el);
    }
};

toolbarButtonModule.mouseOutHandler = function(evt,el){};




