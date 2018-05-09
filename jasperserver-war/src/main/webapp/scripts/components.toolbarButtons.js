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

/* global actionModel, buttonManager */

////////////////////////////////////////////////
// Generic toolbar button utils
////////////////////////////////////////////////

var toolbarButtonModule = {

    UP : "up",
    DOWN : "down",
    OVER : "over",
    DISABLED : "disabled",
    PRESSED : "pressed",
    CONTENT_PREFIX : "toolbar_", //action model
    MenuClass : "menu vertical dropDown",
    TOOLBAR_MENU_CLASS : "menu vertical dropDown fitable",
    ACTION_MODEL_TAG : "adhocActionModel",
    CAPSULE_PATTERN : "capsule",

    /**
     * Used by all toolbar components to show menu
     * @param event
     * @param object object we want the menu for
     */
    showButtonMenu : function(event, object){
        object = jQuery(object);
        var context = this.CONTENT_PREFIX + object.attr("id"),
            model = this.ACTION_MODEL_TAG == 'adhocActionModel' ? this.actionModel : this.ACTION_MODEL_TAG;

        actionModel.showDropDownMenu(event, object, context, this.TOOLBAR_MENU_CLASS, model);
    },

    setActionModel: function(actionModel){
        this.actionModel = actionModel;
    },

    /**
     * Test to see if the button clicked is a toolbar type
     * @param button
     */
    isToolBarButton : function(button){
        if (button){
            return (jQuery(button).hasClass("capsule"));
        } else{
            return false;
        }

    },

    /**
     * Used to enable toolbar button
     * @param button
     * @param enable
     */
    enable : function(button, enable){
        buttonManager.enable(button);
    },

    /**
     * Used to disable toolbar button
     * @param button
     */
    disable : function(button){
        buttonManager.disable(button);
    },

    /**
     * Used to enable or disable button based on adhoc state variables
     * @param button
     * @param enable
     */
    setButtonState : function(button, enable){
        if(enable){
            this.enable(button);
        }else{
            this.disable(button);
        }
    }
};



