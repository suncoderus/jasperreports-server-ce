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

/* global resource, ValidationModule, localContext */


var resourceDataType = {
    PAGE_ID: "addResource_dataType",
    DATA_TYPE_ID: "dataType.dataTypeType",
    LABEL_ID: "dataType.label",
    RESOURCE_ID_ID: "dataType.name",
    DESCRIPTION_ID: "dataType.description",
    SAVE_BUTTON_ID: "done",
    CHANGE_TYPE_BUTTON_ID: "changeCombo",

    _canGenerateId: true,

    initialize: function(options) {
        this._form = $(this.PAGE_ID).select('form')[0];
        this._dataType = $(this.DATA_TYPE_ID);
        this._label = $(this.LABEL_ID);
        this._resourceId = $(this.RESOURCE_ID_ID);
        this._description = $(this.DESCRIPTION_ID);
        this._saveButton = $(this.SAVE_BUTTON_ID);
        this._changeTypeButton = $(this.CHANGE_TYPE_BUTTON_ID);

        this._isEditMode = options.isEditMode;

        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);

        this._initEvents();
    },

    _initEvents: function() {
        this._dataType.observe('change', function() {
            this._changeTypeButton.click();
        }.bindAsEventListener(this));

        this._saveButton.observe('click', function(e) {
            if (!this._isDataValid()) {
                e.stop();
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
        }.bindAsEventListener(this));
    },

    _isDataValid: function() {
        var elementsToValidate = [this._label, this._resourceId, this._description];

        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    }    
};

if (typeof require === "undefined") {
    document.observe('dom:loaded', function() {
        resourceDataType.initialize(localContext.initOptions);
    });
}