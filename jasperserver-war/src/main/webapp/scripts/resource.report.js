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

/* global resource, resourceLocator, ValidationModule, isIE, isIE7, isIE8, isIE9, isIE10, localContext */

var resourceReport = {
    SET_UP_PAGE_ID: "addReport_SetUp",
    LABEL_ID: "label",
    RESOURCE_ID_ID: "resourceID",
    DESCRIPTION_ID: "reportUnit.description",
    FILE_PATH_ID: "filePath",
    RESOURCE_URI_ID: "resourceUri",
    FILE_SYSTEM_SOURCE_ID: "FILE_SYSTEM",
    CONTENT_REPOSITORY_SOURCE_ID: "CONTENT_REPOSITORY",

    RESOURCE_NAME_ID: "resourceName",
    EDIT_RESOURCE_BUTTON_ID: "editResourceButton",
    REMOVE_RESOURCE_BUTTON_ID: "removeResourceButton",
    ADD_RESOURCE_BUTTON_ID: "addResourceButton",
    EDIT_CONTROL_BUTTON_ID: "editControlButton",
    REMOVE_CONTROL_BUTTON_ID: "removeControlButton",
    ADD_CONTROL_BUTTON_ID: "addControlButton",
    FILE_NAME_ID: "fileName",
    FILE_UPLOAD_BUTTON_ID: "fake_upload_button",


    SAVE_BUTTON_ID: "done",
    
    JRXML_FILE_PATH_COOKIE: 'jrxmlFilePath',

    _canGenerateId: true,

    initialize: function(options) {
        this._setUpPage = $(this.SET_UP_PAGE_ID);

        if (this._setUpPage) {
            this._form = $(document.body).select('form')[0];
            this._label = $(this.LABEL_ID);
            this._resourceId = $(this.RESOURCE_ID_ID);
            this._description = $(this.DESCRIPTION_ID);
            this._filePath = $(this.FILE_PATH_ID);
            this._resourceUri = $(this.RESOURCE_URI_ID);
            this._fileName = $(this.FILE_NAME_ID);

            this._fileSystemSource = $(this.FILE_SYSTEM_SOURCE_ID);
            this._contentRepositorySource = $(this.CONTENT_REPOSITORY_SOURCE_ID);

            this._saveButton = $(this.SAVE_BUTTON_ID);

            this._isEditMode = options ? options.isEditMode : false;
            this._initialSource =
                    this._fileSystemSource.checked ? this._fileSystemSource : this._contentRepositorySource;
            this._jrxmlFileResourceAlreadyUploaded = options.jrxmlFileResourceAlreadyUploaded;
            this._label.validator = resource.labelValidator.bind(this);
            this._resourceId.validator = resource.resourceIdValidator.bind(this);
            this._description.validator = resource.descriptionValidator.bind(this);
            this._filePath.validator = this._filePathValidator.bind(this);
            this._resourceUri.validator = this._resourceUriValidator.bind(this);

            this._initEvents();
			this._adjustFileSelectorPosition();

            this._fileName.value =  this._jrxmlFileResourceAlreadyUploaded;
        }

        // Resource locator.
        var resourceOptions =  {
            fileUploadInput : 'filePath',
            fakeFileUploadInput : 'fake_upload_button',
            fakeFileUploadInputText : 'fileName',
            resourceInput : 'resourceUri',
            browseButton : 'browser_button',
            uploadButton: 'upload_button',
            treeId: 'resourceTreeRepoLocation',
            dialogTitle: resource.messages["resource.Report.Title"],
            selectLeavesOnly: true
        };
        if (options && $(resourceOptions.browseButton)) {
            if (options.type == "fileResource") {
                resourceOptions.providerId = 'fileResourceTreeDataProvider';
            } else if (options.type == "jrxml") {
                resourceOptions.providerId = 'jrxmlTreeDataProvider';
            } else if (options.type == "olapMondrianSchema") {
                resourceOptions.providerId = 'olapSchemaTreeDataProvider';
            } else if (options.type == "folder") {
                resourceOptions.treeId = 'addFileTreeRepoLocation';
                resourceOptions.providerId = 'repositoryExplorerTreeFoldersProvider';
                resourceOptions.resourceInput = 'folderUri';
            }
            resourceLocator.initialize(resourceOptions);
            resourceLocator._updateResourceSelectorState(jQuery('input[type=radio]:checked').attr('id'));
        }
    },

    _initEvents: function() {

		var self = this;

        //opens file upload selection dialog, when user clicks on ordinary button
        jQuery("#" + this.FILE_UPLOAD_BUTTON_ID).click(function(e) {
            e.preventDefault();
			jQuery("#" + self.FILE_PATH_ID).trigger('click');
        });

        this._saveButton.observe('click', function(e){
            if (!self._isDataValid()) {
                e.stop();
            }
        });

        this._form.observe('keyup', function(e) {
            var element = e.element();
            var targetElements = [self._label, self._resourceId, self._description];

            if (targetElements.include(element)) {
                ValidationModule.validate(resource.getValidationEntries([element]));

                if (element == self._resourceId
                        && self._resourceId.getValue() != resource.generateResourceId(self._label.getValue())) {
                    self._canGenerateId = false;
                }

                if (element == self._label && !self._isEditMode && self._canGenerateId) {
                    self._resourceId.setValue(resource.generateResourceId(self._label.getValue()));

                    ValidationModule.validate(resource.getValidationEntries([self._resourceId]));
                }
            }
        });

        this._filePath.observe('change', function() {
        	if (isIE()){
                //IE can for security reasons change real patch to file on 'c:\fakepath\'
                //is should not be shown to user, in this case show only filename
        		if (this.value.toLowerCase().indexOf('c:\\fakepath\\') != -1) {
        			self._fileName.value = this.value.substring('c:\\fakepath\\'.length,this.value.length);
        		}
        		else{
        			self._fileName.value = this.value;
        		}
        	}
        	else{
        		self._fileName.value = this.files[0].name;
            }
            $('fileUpload').removeClassName('error');
			self._adjustFileSelectorPosition();
        });
    },

    _isDataValid: function() {
        var elementsToValidate = [this._label, this._resourceId, this._description, this._filePath, this._resourceUri];
        if (isIE()){
        	this.file = this._filePath.value;
        }
        else{
        	this.file = this._filePath.files[0];
        }      
        this.html = this._filePath.innerHTML;
        
        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    },

    _filePathValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (this._fileSystemSource.checked && value.blank()
                && (!this._isEditMode || this._initialSource != this._fileSystemSource)
                && !this._jrxmlFileResourceAlreadyUploaded) {
            errorMessage = resource.messages['filePathIsEmpty'];
            isValid = false;
        }

        if (isValid)  ValidationModule.hideError($('fileName'));
        else ValidationModule.showError($('fileName'), errorMessage);

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _resourceUriValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (this._contentRepositorySource.checked && value.blank()) {
            errorMessage = resource.messages['resourceUriIsEmpty'];
            isValid = false;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

	_adjustFileSelectorPosition: function() {
		var fp = jQuery("#filePath");

		if (isIE7() || isIE8() || isIE9() || isIE10()) {
			var top = 20, right = 0, width = 95, height = 30,
				hasError = fp.parents("label").hasClass("error");

			if (hasError) {
				// adjust the position because of the error message
				top += 13;
			}

			fp.css({
				opacity: "0",
				position: "absolute",
				right: right,
				top: top,
				width: width,
				height: height
			});
		} else {
			fp.css({
				position: "fixed",
				right: "-1000px",
				top: "-1000px"
			});
		}
	},

    editResource: function(resourceName) {
        $(this.RESOURCE_NAME_ID).setValue(resourceName);
        $(this.EDIT_RESOURCE_BUTTON_ID).click();
    },

    removeResource: function(resourceName) {
        $(this.RESOURCE_NAME_ID).setValue(resourceName);
        $(this.REMOVE_RESOURCE_BUTTON_ID).click();
    },

    addResource: function() {
        $(this.ADD_RESOURCE_BUTTON_ID).click();
    },

    editControl: function(resourceName) {
        $(this.RESOURCE_NAME_ID).setValue(resourceName);
        $(this.EDIT_CONTROL_BUTTON_ID).click();
    },

    removeControl: function(resourceName) {
        $(this.RESOURCE_NAME_ID).setValue(resourceName);
        $(this.REMOVE_CONTROL_BUTTON_ID).click();
    },

    addControl: function() {
        $(this.ADD_CONTROL_BUTTON_ID).click();
    }
};

if (typeof require === "undefined") {
    document.observe("dom:loaded", function() {
        resourceReport.initialize(localContext.initOptions);
    });
}

