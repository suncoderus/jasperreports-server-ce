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

/* jshint undef: false */

//JasperServer namespace  mess


jQuery.noConflict();
/*
 *  One of Jasperserver namespaces.
 */
var JRS = {
    vars: {
        element_scrolled: false,
        ajax_in_progress: false,
        current_flow: null,
        contextPath: __jrsConfigs__.contextPath
    },
    i18n : {}
};

var Calendar = {};


if (typeof isEncryptionOn === "undefined") {
	var isEncryptionOn = false;
}

function isProVersion(){
    return __jrsConfigs__.isProVersion;
}


var jaspersoft = {

    components:{},
    i18n:{}

};


if(typeof(JRS) == "undefined"){
    JRS = {
        Mocks : {}
    };
}

if (typeof(JRS.vars) == "undefined"){
    JRS.vars = {
        element_scrolled: false,
        ajax_in_progress: false,
        current_flow: null
    };
}

//TODO: move closer to Import/Export
if (typeof(JRS.Export) == "undefined"){
    JRS.Export = {i18n : {
        "file.name.empty": "export.file.name.empty",
        "file.name.too.long":"export.file.name.too.long",
        "file.name.not.valid":"export.file.name.not.valid",
        "export.select.users":"export.select.users",
        "export.select.roles":"export.select.roles",
        "export.session.expired":"export.session.expired",
        "error.timeout":"export.file.name.empty"
    }, configs:{
        TIMEOUT : 1200000,
        DELAY: 3000
    }};
}

if (typeof(localContext) == "undefined"){
    localContext = { }
}

//TODO: move to common module
if (__jrsConfigs__.calendar){

    JRS.i18n["bundledCalendarFormat"] = __jrsConfigs__.calendar.i18n.bundledCalendarFormat;
    JRS.i18n["bundledCalendarTimeFormat"] = __jrsConfigs__.calendar.i18n.bundledCalendarTimeFormat;
}

