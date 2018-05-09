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

/* global $$, dynamicTree, buttonManager, actionModel, layoutModule, isShiftHeld, dynamicList */

var accessibilityModule = {

    ATTR_TAB_INDEX: "data-tab-index",
    ATTR_COMPONENT_TYPE: "data-component-type",

    delayTime: 500,

    components: [],
    currentTabIndex: -1,

    disabled: true,

    initialize: function() {
        var elements = $$("*[" + this.ATTR_TAB_INDEX +"]");
        elements.each(function(element) {
            var tabIndex = this.getTabIndex(element);
            tabIndex > 0 && (this.components[tabIndex] = element);
        }.bind(this));
        this.enable();
    },

    enable: function() { this.disabled = false; },

    disable: function() { this.disabled = true; },

    _focusByType: {
        'tree': function(component) {
            var id = component.readAttribute('id');
            var tree = dynamicTree.trees[id];
            if (tree) {
                var node = tree.selectedNodes.last();
                node = node || (tree.bShowRoot ? tree.getRootNode() : tree.getRootNode().getFirstChild());
                if (node) {
                    node.isSelected() || node.select();
                    accessibilityModule._doFocus(node);
                }
            }
        },
        'list': function(component) {
            var id = component.readAttribute('id');
            var list = dynamicList.lists[id];
            if (list) {
                var item = list.getSelectedItems().last();
                item = item || list.getItems().first();

                if (item) {
                    item.isSelected() || item.select();
                    accessibilityModule._doFocus(item);
                }
            }
        },
        'search': function(component) {
            var target = component.down('input');
            target && target.select();
            accessibilityModule._doFocus(target);
        },
        'navigation': function(component) {
            var target = component.down('#main_home');
            buttonManager.over(target.down(layoutModule.BUTTON_PATTERN));
            accessibilityModule._doFocus(target);
        },
        'linkList': function(component) {
            buttonManager.over(component.down('a'));
            accessibilityModule._doFocus(component);
        }
    },

    _blurByType: {
        'navigation': function(component) {
            actionModel.hideMenu();
            //deselect object.
            component.select('.over').each(function(object){
                buttonManager.out(object);
            });
        },
        'linkList': function(component) {
            component.select('a.over').each(function(object){
                buttonManager.out(object);
            });
        }
    },

    _doFocus: function(target) {
        setTimeout(function() { target.focus(); }, this.delayTime);
    },

    _focusComponent: function(component) {
        var focusFunction = component && this.getType(component) && this._focusByType[this.getType(component)];
        return focusFunction && focusFunction(component);
    },

    _blurComponent: function(component) {
        var blurFunction = component && this.getType(component) && this._blurByType[this.getType(component)];
        return blurFunction && blurFunction(component);
    },

    findTabComponent: function(component) {
        var match;
        while (component && !match) {
            match = component.hasAttribute && component.hasAttribute(this.ATTR_TAB_INDEX) && component;
            component = $(component.parentNode);
//            console.log("findTabComponent")

        }
        return match;
    },

    getTabIndex: function(component) {
        var tabIndex = -1;

        if (component && component.hasAttribute) {
            component.hasAttribute(this.ATTR_TAB_INDEX) || (component = accessibilityModule.findTabComponent(component));

            var index = component ? component.readAttribute(this.ATTR_TAB_INDEX) : this.currentTabIndex;
            try {
                index && index.length > 0 && (tabIndex = parseInt(index));
            } catch(e) {}
        }

        return tabIndex;
    },

    getType: function(component) {
        return component && component.hasAttribute(this.ATTR_COMPONENT_TYPE) ?
                component.readAttribute(this.ATTR_COMPONENT_TYPE) : undefined;
    },

    getNext: function(startIndex) {
        var nextIndex = -1;
        if (this.components.length > 0) {
            startIndex || (startIndex = this.currentTabIndex);
            startIndex < 0 && (startIndex = 0);

            nextIndex = startIndex + 1;
            while(Object.isUndefined(this.components[nextIndex]) && nextIndex != startIndex) {
                nextIndex ++;
                nextIndex < this.components.length || (nextIndex = 0);
//                console.log("getNext")
            }
        }
        return nextIndex
    },

    getPrevious: function(startIndex) {
        var previousIndex = -1;
        if (this.components.length > 0) {
            startIndex || (startIndex = this.currentTabIndex);

            startIndex < 0 && (startIndex = 0);

            previousIndex = startIndex - 1;
            while(Object.isUndefined(this.components[previousIndex]) && previousIndex != startIndex) {
                previousIndex --;
                previousIndex > -1 || (previousIndex = this.components.length - 1);
                //console.log("getPrevious");
            }
        }

        return previousIndex;
    },

    focusNext: function(startIndex) {
        this.focusByIndex(this.getNext(startIndex));
    },

    focusPrevious: function(startIndex) {
        this.focusByIndex(this.getPrevious(startIndex));
    },

    focusByIndex: function(index) {
        index > -1 && (this.currentTabIndex = index) && this._focusComponent(this.components[index]);
    },

    blurCurrent: function() {
        var component = this.components[this.currentTabIndex];
        component && this._blurComponent(component);
    },

    blurByIndex: function(index) {
        var component = this.components[index];
        component && this._blurComponent(component);
    }

};

document.observe('key:tab', function(event) {
    if(!accessibilityModule.disabled) {
        var e;
        if (!e) {
            e = window.event;
        }
        var tabIndex = accessibilityModule.getTabIndex(event.element());

        accessibilityModule.blurCurrent();
        isShiftHeld(event) ? accessibilityModule.focusPrevious(tabIndex) : accessibilityModule.focusNext(tabIndex);

        event.stop();
        return false;
    }
});

//document.onfocusout = function(e) {
//    var event = e ? e : Event.extend(window.event);
//    var tabIndex = accessibilityModule.getTabIndex(event.element());
//
//    accessibilityModule.blurByIndex(tabIndex);
//};
//
//document.addEventListener && document.addEventListener('blur', document.onfocusout, true);
