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

////////////////////////////////////////////////
// Generic tab utils
////////////////////////////////////////////////
// ** Seems no one is using this **

    var tabModule = {};

    tabModule.OVER = "over";
    tabModule.SELECTED = "selected";

    initTabs = function() {
        var tabSets = $$('ul.tabSet');
        for (var i = 0; i < tabSets.length; i++) {
            var tabs = $(tabSets[i]).select('li.tab');
            for (var j = 0; j < tabs.length; j++) {
                tabs[j].onmouseover = function(){
                    tabModule.mouseEnters(this);
                };
                tabs[j].onmouseout = function(){
                    tabModule.mouseLeaves(this);
                };
                tabs[j].onmousedown = function(){
                    tabModule.setSelected(this);
                    tabModule.callAction(this);
                };
            }
        }
    }

    tabModule.mouseEnters = function(tab) {
        if (!tabModule.isSelected(tab)) {
            tab.className += (" " + tabModule.OVER);
        }
    }

    tabModule.mouseLeaves = function(tab) {
        tab.className = tab.className.sub(tabModule.OVER , '');
    }

    tabModule.isSelected = function(tab) {
        if (tab) {
            return tab.className.include(tabModule.SELECTED);
        }
    }

    tabModule.setSelected = function(tab) {
        if (!tabModule.isSelected(tab)) {
            var thisTabSet = tab.parentNode;
            tabModule.unselectAll(thisTabSet);
            tab.className += (" " + tabModule.SELECTED);
        }
    }

    tabModule.setUnselected = function(tab) {
        if (tabModule.isSelected(tab)) {
            tab.className = tab.className.sub(tabModule.SELECTED , '');
        }
    }

    tabModule.callAction = function(tab) {
        //function named for parent Id of tabset, argument is this tab Id
        var theFunction = getAsFunction(tab.parentNode.getAttribute("id"));
        if (theFunction) {
            theFunction(tab.getAttribute("id"));
        }
    }

    /**
     * @param {Object} can be array of tabs OR the tabset (i.e. parent elem)
     */
    tabModule.unselectAll = function(tabs) {
        if (tabs.className === "tabSet") {
            tabArray = tabs.select('li.tab');
        } else {
            tabArray = tabs;
        }
        for (var i = 0; i < tabArray.length; i++) {
            tabModule.setUnselected(tabArray[i]);
        }
    }

    tabModule.clicked = function(tab, myFunction, myArgs) {
        tabModule.setSelected(tab);
        if (myArgs && isArray(myArgs)) {
            myFunction.apply(tab, myArgs);
        } else {
            myFunction(myArgs);
        }
    }

/////////////////////////////////////////////////////////////////////
// On load
/////////////////////////////////////////////////////////////////////

    initTabs();
