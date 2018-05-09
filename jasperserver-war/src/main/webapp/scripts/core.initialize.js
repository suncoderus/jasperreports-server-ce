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

/* global layoutModule, primaryNavModule, actionModel */

var initModule = {};
/**
 * This gets called when all script and dom model is loaded
 * Initialize all your modules here
 */
initModule.pageInit = function() {
    layoutModule.initialize();
    // *!* FIXME: Is this being called twice?  see commons.main.js
    primaryNavModule.initializeNavigation(); //navigation setup
    actionModel.initializeOneTimeMenuHandlers(); //menu setup
    //isNotNullORUndefined(window.accessibilityModule) && accessibilityModule.initialize();
};

document.observe('dom:loaded',initModule.pageInit.bind(initModule));

