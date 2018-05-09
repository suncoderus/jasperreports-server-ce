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

/* global isNotNullORUndefined, actionModel, __jrsConfigs__, getAsFunction */

var primaryNavModule = {

    NAVIGATION_MENU_CLASS : "menu vertical dropDown",
    ACTION_MODEL_TAG : "navigationActionModel",
    CONTEXT_POSTFIX : "_mutton",
    NAVIGATION_MUTTON_DOM_ID : "navigation_mutton",
    NAVIGATION_MENU_PARENT_DOM_ID : "navigationOptions",
    JSON : null,

    /**
     * Navigation paths used in the navigation menu
     */
    navigationPaths : {
        browse : {url : "flow.html", params : "_flowId=searchFlow"},
        home : {url : "home.html"},
        library : {url : "flow.html", params : "_flowId=searchFlow&mode=library"},
        logOut : {url : "exituser.html"},
        search : {url : "flow.html", params : "_flowId=searchFlow&mode=search"},
        report : {url : "flow.html", params : "_flowId=searchFlow&mode=search&filterId=resourceTypeFilter&filterOption=resourceTypeFilter-reports&searchText="},
        jobs : {url : "scheduler/main.html"},
        olap : {url : "flow.html", params : "_flowId=searchFlow&mode=search&filterId=resourceTypeFilter&filterOption=resourceTypeFilter-view&searchText="},
        event : {url : "flow.html", params : "_flowId=logEventFlow"},
        adminHome : {url : "flow.html", params : "_flowId=adminHomeFlow"},
        organization : {url : "flow.html", params : "_flowId=tenantFlow"},
        etl : {url : "etl"},
        mondrianProperties : {url : "olap/properties.html"},
        flush : {url : "olap/flush.html"},
        user : {url : "flow.html", params : "_flowId=userListFlow"},
        role : {url : "flow.html", params : "_flowId=roleListFlow"},
        analysisOptions : {url : "flow.html", params : "_flowId=mondrianPropertiesFlow"},
        designerOptions : {url : "flow.html", params : "_flowId=designerOptionsFlow"},
        designerCache : {url : "flow.html", params : "_flowId=designerCacheFlow"},
        awsSettings : {url : "flow.html", params : "_flowId=awsSettingsFlow"},
        designer : {url : "flow.html", params : "_flowId=adhocFlow"},
        dashboard : {url : "dashboard/designer.html"},
        legacyDashboard : {url : "flow.html", params : "_flowId=dashboardDesignerFlow&createNew=true"},
        domain : {url : "flow.html", params : "_flowId=createSLDatasourceFlow&ParentFolderUri="},
        dataSource : {url : "flow.html", params : "_flowId=addDataSourceFlow&ParentFolderUri=" + encodeURIComponent("/datasources")},
        logSettings : {url : "log_settings.html"},
        createReport: {url:"view/view/modules/adhoc/createReport"}
    },

    /**
     * List of dom Id's for pages that require user confirmation before leaving.
     */
    bodyIds : {
        "designer" : "designerBase.confirmAndLeave",
        "dashboardDesigner" : "designerBase.confirmAndLeave",
        "repoBrowse": "repositorySearch.confirmAndLeave",
        "repoSearch": "repositorySearch.confirmAndLeave",
        "manage_users": "orgModule.confirmAndLeave",
        "manage_roles": "orgModule.confirmAndLeave",
        "manage_orgs": "orgModule.confirmAndLeave",
        "domainDesigner_tables": "domain.designer.confirmAndLeave",
        "domainDesigner_derivedTables": "domain.designer.confirmAndLeave",
        "domainDesigner_joins": "domain.designer.confirmAndLeave",
        "domainDesigner_calculatedFields": "domain.designer.confirmAndLeave",
        "domainDesigner_filters": "domain.designer.confirmAndLeave",
        "domainDesigner_display": "domain.designer.confirmAndLeave",
        "dataChooserDisplay": "domain.chooser.confirmAndLeave",
        "dataChooserFields": "domain.chooser.confirmAndLeave",
        "dataChooserPreFilters": "domain.chooser.confirmAndLeave",
        "dataChooserSaveAsTopic": "domain.chooser.confirmAndLeave",
        "reportViewer": "Report.confirmAndLeave"
    },

    globalActions : {
        "logOut" : function(bodyId) { return primaryNavModule.bodyIds[bodyId]; }
    },

    /**
     * This method initializes the primary menu. This needs to be called only once.
     */
    initializeNavigation : function(){
        var navKey;
        var navId;
        var navObject;

        if ($(this.ACTION_MODEL_TAG) === null) {
            return;
        }

        this.JSON =  !!$(this.ACTION_MODEL_TAG).text ? ($(this.ACTION_MODEL_TAG).text.evalJSON()) : {};
        var re = /[A-Za-z]+[_]{1}[A-Za-z]+/;
        //go through json and get keys. Keys == action model context == nav menu muttons
        for(navKey in this.JSON){
            navId = re.exec(navKey)[0]; //strip out ids
            navObject = this.JSON[navKey][0];  //get labels
            if(isNotNullORUndefined(navObject)){
                this.createMutton(navId, navObject.text);
            } else {
                if (navId === "main_home" || navId === "main_library"){
                    var leaf = $(navId);
                    leaf && $(leaf).removeClassName("hidden");
                }
            }
        }
    },


    /**
     * helper to create dom object
     */
    createMutton : function(domId, label){
        var mutton = $(this.NAVIGATION_MUTTON_DOM_ID).cloneNode("true");
        var textPlacement = $(mutton).down(".button");

        $(mutton).setAttribute("id", domId);
        //TODO: see if we can do this with builder (maybe not)
        var text = document.createTextNode(label);
        textPlacement.appendChild(text);

        var navigationMenuParent = $(this.NAVIGATION_MENU_PARENT_DOM_ID);
        navigationMenuParent && navigationMenuParent.appendChild(mutton);
    },

    /* Show the drop-down menu for a given top-level menu item on the menu bar. */
    showNavButtonMenu : function(event, object){
        var elementId = jQuery(object).attr("id");
        actionModel.showDropDownMenu(event, object, elementId + this.CONTEXT_POSTFIX, this.NAVIGATION_MENU_CLASS, this.ACTION_MODEL_TAG);
        $("menu").parentId = elementId;
    },

    /**
     * Used to determine if a element is part of the navigation button
     * @param object
     */
    isNavButton : function(object){
        return ($(object).hasClassName("mutton") || $(object).hasClassName("icon"));

    },

    /**
     * Object based method used to create a url based on the navigation path object
     * @param place Place where to go described in primaryNavModule.navigationPaths
     * @param params Request parameters
     */
    setNewLocation : function(place, params){
        var locObj = this.navigationPaths[place], queryParams = params || locObj.params;
        if(!locObj) return;

        queryParams = queryParams ? "?" + queryParams : "";
        window.location.href = __jrsConfigs__.urlContext + "/" + locObj.url + queryParams;
    },


    /**
     * Method used to create a url based on the navigation path object
     * @param option
     */
    navigationOption : function(option){
        var bodyId = $(document.body).readAttribute("id"), execFunction = null;
        if(primaryNavModule.globalActions[option]){
            execFunction = primaryNavModule.globalActions[option](bodyId);
        } else if(primaryNavModule.bodyIds[bodyId]){
            execFunction = primaryNavModule.bodyIds[bodyId];
        }
        if (execFunction) {
            var executableFunction = getAsFunction(execFunction);
            var answer = executableFunction();
            if (typeof answer == 'function') {
                answer(function() {
                    primaryNavModule.setNewLocation(option);
                });
                return;
            } else if(!answer){
                return;
            }
        }
        primaryNavModule.setNewLocation(option);
    },

    /* ==== EVENTS ==== */

    /**
     * Event for fired on mouse over. Used to show a menu.
     * @param event
     * @param object
     */
    onMenuHeaderMouseOver : function(event, object){
        this.showNavButtonMenu(event, object);
    }
};


