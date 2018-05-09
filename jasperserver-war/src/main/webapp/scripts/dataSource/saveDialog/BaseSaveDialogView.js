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

define(function (require) {

    "use strict";

    var _ = require('underscore'),
        i18n = require('bundle!all'),
        browserDetection = require("common/util/browserDetection"),
        ResourceModel = require("bi/repository/model/RepositoryResourceModel"),
        DialogWithModelInputValidation = require("common/component/dialog/DialogWithModelInputValidation"),
        RepositoryFolderTree = require("bi/repository/repositoryFolderTree/RepositoryFolderTree"),
        baseSaveDialogTemplate = require('text!dataSource/saveDialog/template/baseSaveDialogTemplate.htm');

    return DialogWithModelInputValidation.extend({

        theDialogIsOpen: false,
        autoUpdateResourceID: true,
        saveDialogTemplate: baseSaveDialogTemplate,

        events: _.extend({
            "resize": "_onDialogResize"
        }, DialogWithModelInputValidation.prototype.events),

        constructor: function (options) {
            options || (options = {});
            this.options = options;

            var model = this.extendModel(this.options.model);

            var saveButtonLabel = this._getLabelForSaveButton(model);
            var cancelButtonLabel = "resource.datasource.saveDialog.cancel";

            this.autoUpdateResourceID = !this.options.isEditMode;
            this.preSelectedFolder = options.parentFolderUri;

            DialogWithModelInputValidation.prototype.constructor.call(this, {
                skipLocation: !!options.skipLocation,
                modal: true,
                model: model,
                resizable: !options.skipLocation,
                additionalCssClasses: "dataSourceSaveDialog" + (options.skipLocation ? " no-minheight" : ""),
                title: i18n["resource.datasource.saveDialog.save"],

                content: _.template(this.saveDialogTemplate, {
                    i18n: i18n,
                    model: _.extend({}, model.attributes),
                    skipLocation: !!(this.options.skipLocation),
                    isEmbedded: this.options.isEmbedded,
                    isEditMode: this.options.isEditMode
                }),
                buttons: [
                    {label: i18n[saveButtonLabel], action: "save", primary: true},
                    {label: i18n[cancelButtonLabel], action: "cancel", primary: false}
                ]
            });

            this.on('button:save', _.bind(this._onSaveDialogSaveButtonClick, this));
            this.on('button:cancel', _.bind(this._onSaveDialogCancelButtonClick, this));
        },

        initialize: function (options) {

            DialogWithModelInputValidation.prototype.initialize.apply(this, arguments);

            // check if this variables has been re-defined by inherited class
            if (_.isUndefined(this.preSelectedFolder) || !this.preSelectedFolder) {
                this.preSelectedFolder = "/";
            }
            if (!options.skipLocation) {
                this.initializeTree();
            }

            this.listenTo(this.model, "change:label", this._onDataSourceNameChange);
            this.$contentContainer.find("[name=name]").change(_.bind(this._onResourceIDInputChange, this));
        },

        restoreModel: function () {
            if (this.originalModelValidation) {
                this.model.validation = this.originalModelValidation;
            }
        },

        extendModel: function (model) {
            this.originalModelValidation = model.validation;

            model.validation = _.extend({}, ResourceModel.prototype.validation, {
                label: [
                    {
                        required: true,
                        msg: i18n["resource.datasource.saveDialog.validation.not.empty.label"]
                    },
                    {
                        maxLength: ResourceModel.settings.LABEL_MAX_LENGTH,
                        msg: i18n["resource.datasource.saveDialog.validation.too.long.label"]
                    }
                ],
                name: [
                    {
                        required: true,
                        msg: i18n["resource.datasource.saveDialog.validation.not.empty.name"]
                    },
                    {
                        maxLength: ResourceModel.settings.NAME_MAX_LENGTH,
                        msg: i18n["resource.datasource.saveDialog.validation.too.long.name"]
                    },
                    {
                        doesNotContainSymbols: ResourceModel.settings.NAME_NOT_SUPPORTED_SYMBOLS,
                        msg: i18n["resource.datasource.saveDialog.validation.invalid.chars.name"]
                    }
                ],
                description: [
                    {
                        required: false
                    },
                    {
                        maxLength: ResourceModel.settings.DESCRIPTION_MAX_LENGTH,
                        msg: i18n["resource.datasource.saveDialog.validation.too.long.description"]
                    }
                ],
                parentFolderUri: [
                    {
                        fn: function (value) {
                            if (!this.options.skipLocation) {
                                if (_.isNull(value) || _.isUndefined(value) || (_.isString(value) && value === '')) {
                                    return i18n["resource.datasource.saveDialog.validation.not.empty.parentFolderIsEmpty"];
                                }
                                if (value.slice(0, 1) !== '/') {
                                    return i18n["resource.datasource.saveDialog.validation.folder.not.found"].replace("{0}", value);
                                }
                            }
                        }
                    }
                ]
            });

            return model;
        },

        initializeTree: function () {

            this.foldersTree = RepositoryFolderTree();

            this.listenTo(this.foldersTree, "selection:change", function (selection) {
                var parentFolderUri;

                if (selection && _.isArray(selection) && selection[0] && selection[0].uri) {
                    parentFolderUri = selection[0].uri;
                }
                if (!parentFolderUri) {
                    return;
                }

                this.model.set("parentFolderUri", parentFolderUri);
            });

            this.$el.find(".treeBox .folders").append(this.foldersTree.render().el);

            var $scrollContainer = this.foldersTree.$el.parent().parent().parent();
            this.foldersTree._selectTreeNode(this.preSelectedFolder, $scrollContainer);
        },

        startSaveDialog: function () {

            this._openDialog();
        },

        _openDialog: function () {

            if (this.theDialogIsOpen) {
                return;
            }

            this.bindValidation();

            DialogWithModelInputValidation.prototype.open.apply(this, arguments);

            this.$contentContainer.find("[name=label]").focus();

            this.theDialogIsOpen = true;

            // set the initial size of the internal components of the dialog by calling function which
            // reacts on resizing at a first time
            var onDialogResize = _.bind(this._onDialogResize, this);
            if (browserDetection.isIE8() || browserDetection.isIE9()) {
                setTimeout(onDialogResize, 1);
            } else {
                onDialogResize();
            }
        },

        _closeDialog: function () {

            if (!this.theDialogIsOpen) {
                return;
            }

            this.unbindValidation();
            this.clearValidationErrors();

            DialogWithModelInputValidation.prototype.close.apply(this, arguments);

            this.theDialogIsOpen = false;
        },

        _getLabelForSaveButton: function () {

            return "resource.datasource.saveDialog.save";
        },

        /*
         Because under IE it's very hard to maintain auto-resizable area by height (IE8 doesn't support
         calc() css property), we need to do resizing manually, each time reacting on resizing.
         Dirty, ugly hack, but it is what it is.
         */
        _onDialogResize: function () {
            var self = this;
            var shiftHeight = 73;
            var dialogSavingArea = this.$contentContainer.find(".control.groupBox.treeBox");
            var wholeDialog = this.$contentContainer.closest(".jr-mDialog");

            this.$contentContainer
                .children()
                .not(".control.groupBox.treeBox")
                .each(function () {
                    shiftHeight += self.$(this).outerHeight(true);
                });
            shiftHeight += this.$contentContainer.innerHeight() - this.$contentContainer.height();

            dialogSavingArea.height(wholeDialog.outerHeight(true) - shiftHeight);
        },

        _onDataSourceNameChange: function () {
            if (this.autoUpdateResourceID) {
                var resourceId = ResourceModel.generateResourceName(this.model.get("label"));
                this.model.set("name", resourceId);
                this.$("input[name='name']").val(resourceId);
            }
        },

        _onResourceIDInputChange: function () {
            this.autoUpdateResourceID = false;
        },

        _onSaveDialogCancelButtonClick: function () {
            this.restoreModel();
            this._closeDialog();
        },

        _onSaveDialogSaveButtonClick: function () {
            var self = this;
            this._onDialogResize();
            if (!this.model.isValid(true)) {
                return;
            }

            this.performSave();
        },

        performSave: function () {

            if (this.options.saveFn) {
                this.options.saveFn(this.model.attributes, this.model);
                return;
            }

            this.model.save({}, {
                success: _.bind(this._saveSuccessCallback, this),
                error: _.bind(this._saveErrorCallback, this)
            });
        },

        _saveSuccessCallback: function (model, data) {
            this._closeDialog();

            if (_.isFunction(this.options.success)) {
                this.options.success();
            }
        },

        _saveErrorCallback: function (model, xhr, options) {

            var self = this, errors = false, msg;
            var handled = false;

            try {
                errors = JSON.parse(xhr.responseText);
            } catch (e) {
            }

            if (!_.isArray(errors)) {
                errors = [errors];
            }

            _.each(errors, function (error) {

                var field = false, msg = false;

                if (!error) {
                    return;
                }

                // in case of opened dialog, we can highlight some fields with error
                if (self.theDialogIsOpen) {

                    // check if we faced Conflict issue, it's when we are trying to save DS under existing resourceID
                    if (error.errorCode === "version.not.match") {
                        field = "name";
                        msg = i18n["resource.dataSource.resource.alreadyInUse"];
                    }

                    else if (error.errorCode === "mandatory.parameter.error") {
                        if (error.parameters && error.parameters[0]) {
                            msg = i18n["resource.datasource.saveDialog.parameterIsMissing"];
                            field = error.parameters[0].substr(error.parameters[0].indexOf(".") + 1);
                        }
                    }

                    else if (error.errorCode === "illegal.parameter.value.error") {
                        if (error.parameters && error.parameters[0]) {
                            field = error.parameters[0].substr(error.parameters[0].indexOf(".") + 1);
                            msg = i18n["resource.datasource.saveDialog.parameterIsWrong"];
                        }
                    }

                    else if (error.errorCode === "folder.not.found") {
                        field = "parentFolderUri";
                        msg = i18n["ReportDataSourceValidator.error.folder.not.found"].replace("{0}", error.parameters[0]);
                    }

                    else if (error.errorCode === "access.denied") {
                        field = "parentFolderUri";
                        msg = i18n["jsp.accessDenied.errorMsg"];
                    }
                }

                if (msg && field && ["label", "name", "description", "parentFolderUri"].indexOf(field) !== -1) {
                    self.invalidField(
                        "[name=" + field + "]",
                        msg
                    );
                    handled = true;
                }
            });


            // otherwise, pass this error to DataSourceController
            if (handled === false) {
                if (_.isFunction(this.options.error)) {
                    this.options.error(model, xhr, options);
                }
            }
        }
    });
});
