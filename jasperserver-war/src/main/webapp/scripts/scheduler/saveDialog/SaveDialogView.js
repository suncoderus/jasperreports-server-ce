/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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
 * @author: Dima Gorbenko
 * @version: $Id$
 */

/* global dialogs */

define(function(require) {

    "use strict";

    var _ = require('underscore'),
        i18n = require('bundle!all'),
        ResourceModel = require('bi/repository/model/RepositoryResourceModel'),

        // TODO: replace this dialog with newer version (details see in 'DialogWithModelInputValidation' file)
        DialogWithModelInputValidation = require("common/component/dialog/DialogWithModelInputValidation"),

        saveDialogTemplate = require('text!scheduler/saveDialog/template/saveDialogTemplate.htm');

    var _savedOptions = {}; // object used to save options which came to us

    return DialogWithModelInputValidation.extend({

        theDialogIsOpen: false,
        saveDialogTemplate: saveDialogTemplate,

        constructor: function(options) {
            options || (options = {});
            _savedOptions = _.extend({}, options);

            var model = this.extendModel(options.model);

            var saveButtonLabel = this._getLabelForSaveButton(model);
            var cancelButtonLabel = "report.scheduling.saveDialog.cancel";

            // TODO: replace this dialog with newer version (details see in 'DialogWithModelInputValidation' file)
            DialogWithModelInputValidation.prototype.constructor.call(this, {
                skipLocation: !!_savedOptions.skipLocation,
                modal: true,
                model: model,
                resizable: true,
                additionalCssClasses: "schedulerSaveDialog jr-uWidth-425px jr-uHeight-275px",
                title: i18n["report.scheduling.saveDialog.save"],
                content: _.template(this.saveDialogTemplate, {
                    i18n: i18n,
                    model: _.extend({}, model.attributes),
                    skipLocation: !!(_savedOptions.skipLocation),
                    isEmbedded: _savedOptions.isEmbedded,
                    isEditMode: _savedOptions.isEditMode
                }),
                buttons: [
                    {label: i18n[saveButtonLabel], action: "save", primary: true},
                    {label: i18n[cancelButtonLabel], action: "cancel", primary: false}
                ]
            });

            this.on('button:save', _.bind(this._onSaveDialogSaveButtonClick, this));
            this.on('button:cancel', _.bind(this._onSaveDialogCancelButtonClick, this));
        },

        initialize: function() {
            DialogWithModelInputValidation.prototype.initialize.apply(this, arguments);
        },

        restoreModel: function() {
            if (this.originalModelValidation) {
                this.model.validation = this.originalModelValidation;
            }
        },

        extendModel: function(model) {
            this.originalModelValidation = model.validation;

            model.validation = _.extend({}, ResourceModel.prototype.validation, {
                name: null,
                parentFolderUri: null,
                label: [
                    {
                        required: true,
                        msg: i18n["report.scheduling.saveDialog.validation.not.empty.label"]
                    },
                    {
                        maxLength: ResourceModel.LABEL_MAX_LENGTH,
                        msg: i18n["report.scheduling.saveDialog.validation.too.long.label"]
                    }
                ],
                description: [
                    {
                        required: false
                    },
                    {
                        maxLength: ResourceModel.DESCRIPTION_MAX_LENGTH,
                        msg: i18n["report.scheduling.saveDialog.validation.too.long.description"]
                    }
                ]
            });

            return model;
        },

        startSaveDialog: function() {

            this._openDialog();
        },

        closeDialog: function() {
            this.restoreModel();
            this._closeDialog();
        },

        _openDialog: function() {

            if (this.theDialogIsOpen) {
                return;
            }

            this.bindValidation();

            DialogWithModelInputValidation.prototype.open.apply(this, arguments);

            this.$contentContainer.find("[name=label]").focus();

            this.theDialogIsOpen = true;
        },

        _closeDialog: function() {

            if (!this.theDialogIsOpen) {
                return;
            }

            this.unbindValidation();
            this.clearValidationErrors();

            DialogWithModelInputValidation.prototype.close.apply(this, arguments);

            this.theDialogIsOpen = false;
        },

        _getLabelForSaveButton: function() {

            return "report.scheduling.saveDialog.save";
        },

        _onSaveDialogCancelButtonClick: function() {
            this.restoreModel();
            this._closeDialog();
        },

        _onSaveDialogSaveButtonClick: function() {
            var self = this;

            if (!this.model.isValid(true)) {
                return;
            }

            this.performSave();
        },

        performSave: function() {

            var self = this;

            this.model.checkSaveValidation({editMode: _savedOptions.isEditMode}).done(function() {

                self.model.save({}, {
                    success: _.bind(self._saveSuccessCallback, self),
                    error: _.bind(self._saveErrorCallback, self)
                });

            }).fail(function() {

                self.trigger("saveValidationFailed");
            });
        },

        _saveSuccessCallback: function(model, data) {
            this._closeDialog();

            if (_.isFunction(_savedOptions.onSaveDone)) {
                _savedOptions.onSaveDone();
            }
        },

        _saveErrorCallback: function(model, xhr, options) {

            var
                self = this,
                response = false,
                errorHandled = false;

            try {
                response = JSON.parse(xhr.responseText);
            } catch (e) {
            }
            if (response.error) {
                response = response.error;
            }
            if (!_.isArray(response)) {
                response = [response];
            }

            _.each(response, function(error) {

                if (error.field !== "label") {
                    return;
                }

                var message = "";

                if (error.errorCode === "error.not.empty") {
                    message = i18n["error.not.empty.label"];
                }
                if (error.errorCode === "version.not.match") {
                    message = i18n["report.scheduling.resource.exists"];
                }
                if (error.errorCode === "mandatory.parameter.error") {
                    message = i18n["report.scheduling.saveDialog.parameterIsMissing"];
                }

                if (message === "") {
                    return;
                }

                self.invalidField(
                    "[name=label]",
                    message
                );

                errorHandled = true;
            });


            if (errorHandled === false) {
                if (_.isFunction(_savedOptions.onSaveFail)) {
                    _savedOptions.onSaveFail(model, xhr, options);
                }
            }
        }
    });

});
