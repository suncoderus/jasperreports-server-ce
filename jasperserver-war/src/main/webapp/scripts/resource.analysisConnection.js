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

/* global resource, picker, ValidationModule, ajaxTargettedUpdate, dialogs, baseErrorHandler, localContext */

var resourceAnalysisConnection = {
    TYPE_ID: "analysisConnection.type",
    LABEL_ID: "connectionLabel",
    RESOURCE_ID_ID: "connectionName",
    DESCRIPTION_ID: "connectionDescription",
    RESOURCE_INPUT_ID: "resourceUri",
    BROWSE_BUTTON_ID: "browser_button",
    TREE_ID: "folderTreeRepoLocation",
    FOLDERS_TREE_DATA_PROVIDER_ID: "repositoryExplorerTreeFoldersProvider",
    XMLA_CATALOG_ID: "xmlaCatalog",
    XMLA_DATA_SOURCE_ID: "xmlaDatasource",
    XMLA_CONNECTION_URI_ID: "xmlaConnectionUri",

    TEST_BUTTON_ID: "testXMLAConnection",
    NEXT_BUTTON_ID: "next",
    DONE_BUTTON_ID: "done",

    CHANGE_TYPE_BUTTON_ID: "changeCombo",

    _canGenerateId: true,

    initialize: function(options) {
        this._form = $(document.body).select('form')[0];
        this._type = $(this.TYPE_ID);
        this._label = $(this.LABEL_ID);
        this._resourceId = $(this.RESOURCE_ID_ID);
        this._description = $(this.DESCRIPTION_ID);

        var type = this._type.getValue();
        if (type == "olapXmlaCon") {
            this._xmlaCatalog = $(this.XMLA_CATALOG_ID);
            this._xmlaDataSource = $(this.XMLA_DATA_SOURCE_ID);
            this._xmlaConnectionUri = $(this.XMLA_CONNECTION_URI_ID);
        }

        this._nextButton = $(this.NEXT_BUTTON_ID);
        this._doneButton = $(this.DONE_BUTTON_ID);
        this._testButton = $(this.TEST_BUTTON_ID);

        this._changeTypeButton = $(this.CHANGE_TYPE_BUTTON_ID);

        this._isEditMode = options.isEditMode;

        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);

        if (type == "olapXmlaCon") {
            this._xmlaCatalog.validator = this._xmlaCatalogValidator.bind(this);
            this._xmlaDataSource.validator = this._xmlaDataSourceValidator.bind(this);
            this._xmlaConnectionUri.validator = this._xmlaConnectionUriValidator.bind(this);
        }

        this._initResourcePicker();
        this._initEvents();
    },

    _initResourcePicker: function() {
        new picker.FileSelector({
            treeId: this.TREE_ID,
            providerId: this.FOLDERS_TREE_DATA_PROVIDER_ID,
            uriTextboxId: this.RESOURCE_INPUT_ID,
            browseButtonId: this.BROWSE_BUTTON_ID,
            title: resource.messages["resource.SaveToFolder.Title"]
        });
    },

    _initEvents: function() {
        this._type.observe('change', function() {
            this._changeTypeButton.click();
        }.bindAsEventListener(this));

        var submitHandler = function(e) {
            if (!this._isDataValid()) {
                e.stop();
            }
        }.bindAsEventListener(this);

        var testConnectionHandler = function(e) {
            if (!this._isDataValid()) {
                e.stop();
            } else {
                this.testXMLAConnection();
            }
        }.bindAsEventListener(this);

        this._nextButton.observe('click', submitHandler);
        this._doneButton.observe('click', submitHandler);
        this._testButton && this._testButton.observe('click', testConnectionHandler);

        this._form.observe('keyup', function(e) {
            var element = e.element();
            var targetElements = [this._label, this._resourceId, this._description, this._xmlaCatalog, 
                this._xmlaDataSource, this._xmlaConnectionUri];

            if (targetElements.include(element)) {
                ValidationModule.validate(resource.getValidationEntries([element]));

                if (element == this._resourceId
                        && this._resourceId.getValue() != resource.generateResourceId(this._label.getValue())) {
                    this._canGenerateId = false;
                }

                if (element == this._label && !this._isEditMode && this._canGenerateId) {
                    this._resourceId.setValue(resource.generateResourceId(this._label.getValue()));

                    ValidationModule.validate(resource.getValidationEntries([this._resourceId]));
                }
            }
        }.bindAsEventListener(this));
    },

    testXMLAConnection : function() {
        var testButton = this._testButton;
        var form = jQuery(this._form);
        var formValues = form.serializeArray();
        formValues.push({name : "_eventId_testXMLAConnection", value : ""});

        ajaxTargettedUpdate(
            form.attr('action'),
            {
                postData : jQuery.param(formValues),
                fillLocation: 'ajaxbuffer',
                callback:function(msg) {
                    var response = jQuery(msg).text();
                    try {
                        var responseObj = JSON.parse(response);
                        responseObj.status == 'OK' ?
                            ValidationModule.showSuccess(testButton, resource.messages['connectionStatePassed']) :
                            ValidationModule.showError(testButton, responseObj.message, responseObj.details);
                    } catch (e)  {
                        dialogs.systemConfirm.show(response);
                    }
                },
                errorHandler: baseErrorHandler,
                hideLoader: false
            }
        );
    },

    _isDataValid: function() {
        var elementsToValidate = [this._label, this._resourceId, this._description];

        var type = this._type.getValue();
        if (type == "olapXmlaCon") {
            elementsToValidate.push(this._xmlaCatalog);
            elementsToValidate.push(this._xmlaDataSource);
            elementsToValidate.push(this._xmlaConnectionUri);
        }

        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    },

    _xmlaCatalogValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value.blank()) {
            errorMessage = resource.messages['catalogIsEmpty'];
            isValid = false;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _xmlaDataSourceValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value.blank()) {
            errorMessage = resource.messages['dataSourceIsEmpty'];
            isValid = false;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _xmlaConnectionUriValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value.blank()) {
            errorMessage = resource.messages['uriIsEmpty'];
            isValid = false;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    }
};

if (typeof require === "undefined") {
    document.observe("dom:loaded", function() {
        resourceAnalysisConnection.initialize(localContext.initOptions);
    });
}