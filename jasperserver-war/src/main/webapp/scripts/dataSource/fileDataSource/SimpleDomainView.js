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

/* global dialogs, redirectToUrl */

define(function (require){

    "use strict";

    var $ = require("jquery"),
        _ = require('underscore'),
        i18n = require('bundle!all'),
        javaTypeMapper = require("common/enum/javaTypeMapper"),
        javaTypes = require("dataSource/fileDataSource/enum/javaTypes"),
        jrsConfigs = require('jrs.configs'),
        SimpleDomainModel = require("dataSource/fileDataSource/SimpleDomainModel"),
        DialogWithModelInputValidation = require("common/component/dialog/DialogWithModelInputValidation"),
        DomainSaveDialogView = require("dataSource/saveDialog/DomainSaveDialogView"),
        standardConfirmTemplate = require("text!common/templates/standardConfirm.htm"),
        simpleDomainDialogTemplate = require('text!dataSource/fileDataSource/template/simpleDomainDialogTemplate.htm');

    return DialogWithModelInputValidation.extend({

        hasChanges: false,
        modelIsValid: true,
        theDialogIsOpen: false,

        constructor: function(options) {

            options || (options = {});
            this.options = options;
            this.dataSource = options.dataSource ? options.dataSource : {};

            var saveButtonLabel = "resource.datasource.createDomain.save";
            var cancelButtonLabel = "resource.datasource.createDomain.cancel";

            this.model = new SimpleDomainModel({}, options);

            DialogWithModelInputValidation.prototype.constructor.call(this, {
                modal: true,
                model: this.model,
                width: 400,
                additionalCssClasses: "dataSourceCreateNewDomainDialog",
                title: i18n["resource.datasource.createDomain.dialogTitle"],
                content: "",
                buttons: [
                    { label: i18n[saveButtonLabel], action: "save", primary: true },
                    { label: i18n[cancelButtonLabel], action: "cancel", primary: false }
                ]
            });

            this.on('button:save', _.bind(this._onSaveButtonClick, this));
            this.on('button:cancel', _.bind(this._onCancelButtonClick, this));
            this.model.on('validationPassed', _.bind(this.clearValidationErrors, this));
            this.model.on('validationFailed', _.bind(this.validationFailed, this));
        },

        initialize: function(options) {
            DialogWithModelInputValidation.prototype.initialize.apply(this, arguments);
        },

        updateModelProperty: function(e) {

            var target = $(e.target),
                type = target.attr('type'),
                name = target.attr('name'),
                fieldId = target.parents("tr").attr("data-fieldId"),
                columns, value;

            if (type === "checkbox" || type === "radio") {
                value = target.is(":checked");
            } else {
                value = $.trim(target.val());
            }

            columns = this.model.get("columns");

            columns[fieldId][name] = value;

            this.model.set("columns", columns);

            this.model.validate("columns");

            if (this.hasChanges === false) {
                this.changeButtonLabel("save", i18n["resource.datasource.createDomain.applyAndSave"]);
            }
            this.hasChanges = true;
        },

        startDialog: function() {

            var self = this;

            if (this.theDialogIsOpen) {
                return;
            }

            this.bindValidation();

            this.model.fetchMetadata(this.dataSource).done(function() {

                DialogWithModelInputValidation.prototype.open.apply(self, arguments);

                var content = _.template(simpleDomainDialogTemplate, {
                    i18n: i18n,
                    javaTypes: javaTypes,
                    columns: self.model.get("columns"),
                    javaTypeMapper: javaTypeMapper
                });

                self.setContent(content);
                self._center();
            });

            this.theDialogIsOpen = true;
        },

        // block default validation handlers
        fieldIsValid: function() {},
        fieldIsInvalid: function(error) {},

        clearValidationErrors: function() {
            this.$el.find(".error").removeClass("error");
            this.modelIsValid = true;
        },

        validationFailed: function(errors) {
            var self = this;

            this.clearValidationErrors();
            this.modelIsValid = false;

            _.each(errors, function(error) {
                var errorRow = $(self.$el.find("table tr").get(error.rowId + 1));
                errorRow.find("[name=" + error.name + "]").parent().addClass("error");
                errorRow.find(".message").text(i18n["resource.datasource.createDomain.validation.missing.label"]);
            });
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

        _onCancelButtonClick: function() {

            var self = this,
                msg = i18n["resource.datasource.createDomain.cancelMessage"],
                confirm = $(standardConfirmTemplate);

            confirm.find(".body").html(msg);

            dialogs.popupConfirm.show(confirm.get(0), true, {
                okButtonSelector: "[name=buttonOK]",
                cancelButtonSelector: "[name=buttonCancel]"
            }).done(function() {
                self._closeDialog();
                if (self.options.cancel) {
                    self.options.cancel();
                }
            });

        },

        _onSaveButtonClick: function() {

            if (!this.modelIsValid) {
                return;
            }

            this._closeDialog();

            this.saveDialog = new DomainSaveDialogView(_.extend({}, this.options, {
                model: this.model,
                success: function(){
                    redirectToUrl(jrsConfigs.contextPath
                        + "/flow.html?_flowId=repositoryConfirmFlow&resourceType=dataSource&resourceType=domain");
                }
            }));

            this.saveDialog.startSaveDialog();
        }
    });

});