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

var addInputControl = {
    PAGE_ID: "addResource_inputControl",
    LABEL_ID: "label",
    RESOURCE_ID_ID: "name",
    DESCRIPTION_ID: "description",
    NEXT_BUTTON_ID: "next",
    NEXT_AND_SUBMIT_BUTTON_ID: "nextAndSubmit",
    TYPE_ID: "dataTypeKind",

    _canGenerateId: true,

    initialize: function(options) {
        this._form = $(this.PAGE_ID).select('form')[0];
        this._label = $(this.LABEL_ID);
        this._resourceId = $(this.RESOURCE_ID_ID);
        this._description = $(this.DESCRIPTION_ID);
        this._nextButton = $(this.NEXT_BUTTON_ID);
        this._nextAndSubmitButton = $(this.NEXT_AND_SUBMIT_BUTTON_ID);
        this._typeId = $(this.TYPE_ID);

        this._isEditMode = options.isEditMode;

        this._label.validator = resource.labelValidator.bind(this);
        this._resourceId.validator = resource.resourceIdValidator.bind(this);
        this._description.validator = resource.descriptionValidator.bind(this);

        this._initEvents();
    },

    _initEvents: function() {
        var nextHandler = function(e) {
            if (!this._isDataValid()) {
                e.stop();
            }
        }.bindAsEventListener(this);

        this._nextButton.observe('click', nextHandler);
        this._nextAndSubmitButton.observe('click', nextHandler);

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

        this._typeId.observe('change', function(e) {
            if (e.element().value == 1) {
                this._nextButton.addClassName('hidden');
                this._nextAndSubmitButton.removeClassName('hidden');
            } else {
                this._nextButton.removeClassName('hidden');
                this._nextAndSubmitButton.addClassName('hidden');
            }
        }.bindAsEventListener(this));
    },

    _isDataValid: function() {
        var elementsToValidate = [this._label, this._resourceId, this._description];

        return ValidationModule.validate(resource.getValidationEntries(elementsToValidate));
    }
};

if(typeof require === "undefined") {
    document.observe('dom:loaded', function() {
        addInputControl.initialize(localContext.initOptions);
    });
}