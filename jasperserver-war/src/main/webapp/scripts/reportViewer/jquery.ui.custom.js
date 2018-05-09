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
 * @version $Id$
 */

define(function(require){

    //try to use different parts of 'jquery-ui' instead of this one
    //pack for old JIVE in report viewer
    //maps it to old names like 'jquery.ui'

    require("jquery-ui/jquery.ui.position");
    require("jquery-ui/jquery.ui.datepicker");
    require("jquery-ui/jquery.ui.timepicker");
    require("jquery-ui/jquery.ui.draggable");
    require("jquery-ui/jquery.ui.droppable");
    require("jquery-ui/jquery.ui.resizable");
    require("jquery-ui/jquery.ui.sortable");

    return require("jquery");
});
