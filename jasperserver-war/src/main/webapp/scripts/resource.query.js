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

/* global resource, resourceLocator, ValidationModule, localContext, require */

var resourceQuery = {
    STEP1_PAGE_ID: "addResource_query_step1",
    STEP2_PAGE_ID: "addResource_locateDataSource",
    STEP3_PAGE_ID: "addResource_query_step3",
    LABEL_ID: "query.label",
    RESOURCE_ID_ID: "query.name",
    DESCRIPTION_ID: "query.description",
    JUMP_TO_PAGE_ID: "jumpToPage",
   // PREVIOUS_BUTTON_ID: "previous",
    DONE_BUTTON_ID: "done",
    NEXT_BUTTON_ID: "next",
    JUMP_TO_BUTTON_ID: "jumpButton",
    NEW_DATA_SOURCE_LINK_ID: "newDataSourceLink",
    LOCAL_DATA_SOURCE_ID: "LOCAL",
    RESOURCE_URI_INPUT_ID: "resourceUri",
    QUERY_SQL: "query.sql",

    _canGenerateId: true,

    initialize: function(options) {
        this._step1Page = $(this.STEP1_PAGE_ID);
        this._step2Page = $(this.STEP2_PAGE_ID);
        this._step3Page = $(this.STEP3_PAGE_ID);

        if (this._step1Page) {
            this._form = $(document.body).select('form')[0];
            this._label = $(this.LABEL_ID);
            this._resourceId = $(this.RESOURCE_ID_ID);
            this._description = $(this.DESCRIPTION_ID);

            this._label.validator = resource.labelValidator.bind(this);
            this._resourceId.validator = resource.resourceIdValidator.bind(this);
            this._description.validator = resource.descriptionValidator.bind(this);
            
        }
        
        if (this._step2Page) {
            this._form = $(document.body).select('form')[0];
            this._dataSourceUri = $(this.RESOURCE_URI_INPUT_ID);

            this._dataSourceUri.validator =  resource.dataSourceValidator.bind(this);
        }
        
        if (this._step3Page) {
            this._form = $(document.body).select('form')[0];
            this._query = $(this.QUERY_SQL);

            this._query.validator = resource.queryValidator.bind(this);
        }
        this._nextButton = $(this.NEXT_BUTTON_ID);
        this._doneButton = $(this.DONE_BUTTON_ID);

        this._jumpToPage = $(this.JUMP_TO_PAGE_ID);
        this._jumpButton = $(this.JUMP_TO_BUTTON_ID);

        this._newDataSourceLink = $(this.NEW_DATA_SOURCE_LINK_ID);
        this._localDataSource = $(this.LOCAL_DATA_SOURCE_ID);

        this._isEditMode = options ? options.isEditMode : false;

        window.resourceLocator && resourceLocator.initialize({
            resourceInput : 'resourceUri',
            browseButton : 'browser_button',
            newResourceLink : 'newDataSourceLink',
            treeId : 'queryTreeRepoLocation',
            providerId : 'dsTreeDataProvider',
            dialogTitle : resource.messages["resource.Query.Title"],
            selectLeavesOnly: true
        });

        this._initEvents();

    },

    _initEvents: function() {
    	 this._nextButton.observe('click', function(e) {
             if (!this._isDataValid()) {
                 e.stop();
             }
             if (this._localDataSource && this._localDataSource.checked){
                 e.stop();
                 this._newDataSourceLink.click();
             }
         }.bindAsEventListener(this));
    	 
    	 this._doneButton.observe('click', function(e) {
             if (!this._isDataValid()) {
                 e.stop();
             }
         }.bindAsEventListener(this));
    	 
            this._form.observe('keyup', function(e) {
            	var element = e.element();
            	if (element == this._resourceId
                        && this._resourceId.getValue() != resource.generateResourceId(this._label.getValue())) {
                    this._canGenerateId = false;
                }

                if (element == this._label && !this._isEditMode && this._canGenerateId) {
                    this._resourceId.setValue(resource.generateResourceId(this._label.getValue()));

                    ValidationModule.validate(resource.getValidationEntries([this._resourceId]));
                }
            }.bindAsEventListener(this));


        if (this._newDataSourceLink) {
            var self = this;
            this._newDataSourceLink.observe('click', function(e) {
                if (self._localDataSource.checked) {
                    require(["dataSource/DataSourceController", "jrs.configs", "jquery", "underscore"], function(DataSourceController, jrsConfigs, jquery, _){
                        var options = jrsConfigs.addDataSource ? _.clone(jrsConfigs.addDataSource.initOptions) : {};
                        options.saveFn = function(dataSource, model){
                            dataSource.type = model.type;
                            var input = document.createElement('input');
                            input.type = 'hidden';
                            input.name = 'dataSourceJson';
                            input.value = JSON.stringify(model.toJSON());
                            self._form.appendChild(input);
                            input = document.createElement('input');
                            input.type = 'hidden';
                            input.name = 'dataSourceType';
                            input.value = model.type;
                            self._form.appendChild(input);
                            input = document.createElement('input');
                            input.type = 'hidden';
                            input.name = '_eventId_saveDatasource';
                            self._form.appendChild(input);
                            self._form.submit();
                        };
                        options.cancelFn = function(){
                            self._form.submit();
                        };
                        options.skipLocation = true;
                        if(localContext && localContext.initOptions && localContext.initOptions){
                            if(localContext.initOptions.dataSourceJson){
                                // data source page has been opened. Here are previously edited attributes and client type
                                options.isEditMode = true;
                                options.dataSource = localContext.initOptions.dataSourceJson;
                                options.dataSourceClientType = localContext.initOptions.dataSourceJson.type;
                            } else if(localContext.initOptions.dataSourceUri){
                                // first edit. No model yet.
                                options.isEditMode = true;
                                options.resourceUri = localContext.initOptions.dataSourceUri;
                            }
                        }
                        var dataSourceController = new DataSourceController(options);
                        jquery("#display").append(dataSourceController.$el);
                        // remove conflicting dom elements
                        jquery("#selectFromRepository").remove();
                        dataSourceController.render();
                    });
                }
            }.bindAsEventListener(this));
        }
    },

    _isDataValid: function() {
    	var res = true;
        if (this._label)
        	res = res && ValidationModule.validate(resource.getValidationEntries([this._label]));
        
        if (this._resourceId){
            if (this._resourceId.getValue() != resource.generateResourceId(this._label.getValue())) {
                this._canGenerateId = false;
            }

            if (!this._isEditMode && this._canGenerateId) {
                this._resourceId.setValue(resource.generateResourceId(this._label.getValue()));
                res = res && ValidationModule.validate(resource.getValidationEntries([this._resourceId]));
            }
        }       
        if (this._description)
        	res = res && ValidationModule.validate(resource.getValidationEntries([this._description]));    

        // When using Prototype JS to disable element its sets disabled property to appropriate state.
        // So, we should check its state using this property to handle states correctly.
        if (this._dataSourceUri && !this._dataSourceUri.disabled)
        	res = res && ValidationModule.validate(resource.getValidationEntries([this._dataSourceUri]));   
        
        if (this._query)
        	res = res && ValidationModule.validate(resource.getValidationEntries([this._query]));
        return res;
    },

    jumpTo: function(pageTo) {
        this._jumpToPage.setValue(pageTo);
        this._jumpButton.click();
        
        return false;
    }
};

if (typeof require === "undefined") {
    document.observe("dom:loaded", function() {
        resourceQuery.initialize(localContext.initOptions);
    });
}
