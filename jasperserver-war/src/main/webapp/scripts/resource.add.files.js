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

/* global resource, Form, ValidationModule, localContext, buttonManager, matchAny, resourceLocator */

var addFileResource = {
    PAGE_ID: "addResourceFile",
    TYPE_ID: "fileType",
    PATH_ID: "filePath",
    LABEL_ID: "addFileInputResourceLabelID",
    RESOURCE_ID_ID: "addFileInputResourceID",
    DESCRIPTION_ID: "addFileInputDescription",
    FOLDER_URI_ID: "folderUri",
    SAVE_BUTTON_ID: "save",

    typeToExtMap: {
        "accessGrantSchema": ["xml"],
        "css": ["css"],
        "font": ["ttf"],
        "img": ["jpg", "jpeg", "gif", "bmp", "png"],
        "jar": ["jar"],
        "jrxml": ["jrxml"],
        "olapMondrianSchema": ["xml"],
        "prop": ["properties"],
        "jrtx": ["jrtx"],
        "xml": ["xml", "agxml"],
	    "config": ["config"],
		"cer": ["p12", "pfx"],
        "contentResource": ["docx", "doc", "ppt", "pptx", "xls", "xlsx", "ods", "odt", "odp", "pdf", "rtf", "html", "txt", "csv", "json"],
        "secureFile": ["*"]
    },

    _canGenerateId: true,

    initialize: function(options) {
        this._form = $(this.PAGE_ID).select('form')[0];
        this._type = $(this.TYPE_ID);
        this._path = $(this.PATH_ID);
        this._label = $(this.LABEL_ID);
        this._resourceId = $(this.RESOURCE_ID_ID);
        this._description = $(this.DESCRIPTION_ID);
        this._folderUri = $(this.FOLDER_URI_ID);
        this._saveButton = $(this.SAVE_BUTTON_ID);

        this._isEditMode = options.isEditMode;

        this._type.validationEntry = {
            validator: this._typeValidator.bind(this),
            element: this._type,
            onValid: function(){
                if (!this._manual){
                    var fileName = this._getFileNameFromPath(this._path.getValue());

                    this._label.value = fileName;
                    if(!this._resourceId.readOnly) {
                        this._resourceId.value = resource.generateResourceId(this._label.getValue());
                    }
                }
            }.bind(this)
        }
        this._path.validator = this._pathValidator.bind(this);
        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);

        resourceLocator.initialize({
            resourceInput : 'folderUri',
            browseButton : 'browser_button',
            treeId : 'addFileTreeRepoLocation',
            providerId : 'repositoryExplorerTreeFoldersProvider',
            dialogTitle : resource.messages["resource.Add.Files.Title"]
        });
        this._initEvents();
    },

    _initEvents: function() {
        this._saveButton.observe('click', function(e) {
            if (!this._isDataValid()) {
                e.stop();
            }
        }.bindAsEventListener(this));

        this._type.observe('change', function(e) {
            this._validateTypeAndPath();
        }.bindAsEventListener(this));

        this._path.observe('change', function(e) {
            this._validateTypeAndPath();
        }.bindAsEventListener(this));

        this._form && new Form.Observer(this._form, 0.3, function() {
            if (this._folderUri.getValue().blank()) {
                buttonManager.disable("save");
            } else {
                buttonManager.enable("save");
            }
        }.bindAsEventListener(this));

        this._form.observe('keyup', function(e) {
            var element = e.element();
            var targetElements = [this._label, this._resourceId, this._description];

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
            this._manual = true;
        }.bindAsEventListener(this));

        this._form.observe('keydown', function(e) {
            var targetElement = matchAny(e.element(),
                    ["#" + this.DESCRIPTION_ID], true);
            if (!targetElement){
                if (e.keyCode == 13){
                    this._saveButton.focus();
                }
            }
        }.bindAsEventListener(this));
    },

    _isDataValid: function() {
        var elementsToValidate = [this._label, this._resourceId, this._description, this._path, this._type];

        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    },

    _validateTypeAndPath: function() {
        var elementsToValidate = [this._type, this._path];
        
        ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    },

    _typeValidator: function(value) {
        var isValid = true;
        var errorMessage = "";
	    var result = {
		    isValid: isValid,
		    errorMessage: errorMessage
	    };

        var extension = this._getExtension();
	    
	    if (extension.blank()) {
		    return result;
	    }
	    if (this.typeToExtMap[value] && this.typeToExtMap[value].include("*")) {
            return result;
	    }
	    var types = this._getTypesForExtension(extension);

        if (!types.include(value)) {
	        result.errorMessage = resource.messages['typeIsNotValid'];
	        result.isValid = false;
        }

        return result;
    },

    _pathValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (!this._isEditMode && value.blank()) {
            errorMessage = resource.messages['pathIsEmpty'];
            isValid = false;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _getTypesForExtension: function (extension) {
        var types = [];
        for (var type in this.typeToExtMap) {
            if (this.typeToExtMap[type] && this.typeToExtMap[type].include(extension.toLowerCase())) {
                types.push(type);
            }
        }
        return types;
    },

    _getExtension: function() {
        var filename = this._path.getValue();
        if (filename.blank()) {
            return "";
        }

        var dotPosition = filename.lastIndexOf(".");
        if (dotPosition == -1) {
            return "";
        }

        return  filename.substr(dotPosition + 1);
    },

    _getFileNameFromPath: function(path) {
        var startPos = path.lastIndexOf("\\") > 0 ? path.lastIndexOf("\\") + 1 : 0;

        return path.substring(startPos, path.length);
    }
};

if (typeof require === "undefined") {
    document.observe('dom:loaded', function() {
        addFileResource.initialize(localContext.initOptions);
    });
}
