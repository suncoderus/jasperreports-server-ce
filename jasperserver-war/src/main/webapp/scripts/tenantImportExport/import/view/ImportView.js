/**
 * Copyright (C) 2005 - 2015 Jaspersoft Corporation. All rights reserved.
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
 * @author: Zakhar Tomchenko
 * @version:
 */

define(function(require) {
    "use strict";

    require("css!importExport.css");

    var $ = require("jquery"),
        _ = require("underscore"),
        Backbone = require("backbone"),
        ImportModel = require("../model/ImportModel"),
        ImportStateModel = require("../model/ImportStateModel"),
        ImportPendingStateEnum = require("../enum/importPendingStatesEnum"),
        BrokenDependencyStrategyEnum = require("../enum/brokenDependencyStrategyEnum"),
        LoadingDialog = require("../../export/view/LoadingDialog"),
        MergeTenantDialogView = require("./MergeTenantDialogView"),
        ImportDependentResourcesDialogView = require("./ImportDependentResourcesDialogView"),
        ImportWarningsDialogView = require("./ImportWarningsDialogView"),
        warningsFactory = require("tenantImportExport/import/factory/warningsFactory"),
        Notification = require("common/component/notification/Notification"),
        epoxyViewMixin = require("common/view/mixin/epoxyViewMixin"),
        importTemplate = require("text!tenantImportExport/import/template/importTemplate.htm"),

        i18n = require('bundle!ImportExportBundle'),
        i18n2 = require('bundle!CommonBundle'),

    ImportView = Backbone.View.extend({
        tagName: "form",
        className: "import-view",
        id: "importDataFile",

        events: {
            "change input[type='file']": "validateFile",
            "click .checkBox label": "_clickOnCheckbox"
        },

        initialize:function () {
            this.model || (this.model = new ImportModel(null, {form: this.el}));

            this.stateModel = new ImportStateModel();

            this.loadingDialog = new LoadingDialog({
                content: i18n2["dialog.overlay.loading"]
            });

            this.mergeTenantDialogView = new MergeTenantDialogView();
            this.dependentResourcesDialogView = new ImportDependentResourcesDialogView();
            this.warningsDialogView = new ImportWarningsDialogView();

            this.notification = new Notification();

            this.listenTo(this.stateModel, "change:phase", this._handleImportPhase, this);

            var notFoundNotification = {
                    delay: false,
                    message: i18n["import.error.cancelled"]
                },
                unexpectedErrorNotification = {
                    delay: false,
                    message: i18n["import.error.unexpected"]
                };

            this.listenTo(this.model, "change", this._onModelChange);

            this.listenTo(this.model, "error:notFound", _.bind(this.notification.show, this.notification, notFoundNotification));
            this.listenTo(this.stateModel, "error:notFound", _.bind(this.notification.show, this.notification, notFoundNotification));

            this.listenTo(this.model, "error:internalServerError", _.bind(this.notification.show, this.notification, unexpectedErrorNotification));
            this.listenTo(this.stateModel, "error:internalServerError", _.bind(this.notification.show, this.notification, unexpectedErrorNotification));

            this.listenTo(this.model, "error", _.bind(this.loadingDialog.close, this.loadingDialog));
            this.listenTo(this.stateModel, "error", _.bind(this.loadingDialog.close, this.loadingDialog));

            this.listenTo(this.mergeTenantDialogView, "button:import", function() {
                this.model.set("mergeOrganization", true);
                this.doImport();
            }, this);

            this.listenTo(this.mergeTenantDialogView, "button:cancel", _.bind(cancelImportTask, this, ImportStateModel.STATE.CANCELLED));

            this.listenTo(this.dependentResourcesDialogView, "button:skip", applyBrokenDependencyStrategy(this, BrokenDependencyStrategyEnum.SKIP));
            this.listenTo(this.dependentResourcesDialogView, "button:include", applyBrokenDependencyStrategy(this, BrokenDependencyStrategyEnum.INCLUDE));
            this.listenTo(this.dependentResourcesDialogView, "button:cancel", _.bind(cancelImportTask, this, ImportStateModel.STATE.CANCELLED));

            this.epoxifyView();
        },

        render: function (options) {
            this.type = options.type;

            this.model.reset(this.type, {organization: options.tenantId});

            this.$el.html(_.template(importTemplate)({
                i18n: i18n,
                model: this.model.toJSON()
            }));

            this.applyEpoxyBindings();

            return this;
        },

        validateFile: function (evt) {
            this.model.set("fileName", $(evt.target).val());

            var $file = $(evt.target),
                $parent = $file.parent();

            if (this.model.isValid(true)) {
                $parent.removeClass("error");
            } else {
                $parent.addClass("error");
            }
        },

        doImport: function () {
            var self = this;

            this.loadingDialog.open();

            if (this.model.isValid(true)) {
                this.model.save().always(function (res) {
                    self.stateModel.set(res);
                });
            }
        },

        _handleImportPhase: function() {
            var phase = this.stateModel.get("phase");

            if (phase !== ImportStateModel.STATE.INPROGRESS) {
                this.loadingDialog.close();
            }

            (phase === ImportStateModel.STATE.READY && finishImport.call(this, "import.finished", "success")) ||
            (phase === ImportStateModel.STATE.FAILED && finishImport.call(this, this.stateModel.get("error").errorCode)) ||
            (phase === ImportStateModel.STATE.CANCELLED && finishImport.call(this, "import.error.cancelled")) ||
            (phase === ImportStateModel.STATE.PENDING && finishPendingImport.call(this));
        },

        _clickOnCheckbox:function (evt) {
            var checkbox = $(evt.target).next();
            if (!checkbox[0].disabled) {
                checkbox[0].checked = !checkbox[0].checked;
                checkbox.trigger("change");
            }
        },

        _onModelChange: function() {
            this.model.isValid(true);
        }
    });

    _.extend(ImportView.prototype, epoxyViewMixin);

    return ImportView;

    function getWarnings() {
        var warnings = this.stateModel.get("warnings");

        return _.map(warnings, function(value) {
            return warningsFactory(value);
        });
    }

    function finishImport(code, notificationType) {
        notificationType = notificationType || "warning";

        var warnings = getWarnings.call(this);

        if (code === "import.finished" && !_.isEmpty(warnings)) {
            this.warningsDialogView.open({items: warnings});
        }

        this.notification.show({
            delay: false,
            message: i18n[code] || i18n["import.error.unexpected"],
            type: notificationType
        });

        this.trigger("import:finished", this.model.get("organization"));

        this.model.reset(this.type);
    }

    function finishPendingImport(){
        var error = this.stateModel.get("error");

        if (error.errorCode === ImportPendingStateEnum.BROKEN_DEPS){
            this.dependentResourcesDialogView.open({items: error.parameters});

        } else if (error.errorCode === ImportPendingStateEnum.TENANT_MISMATCH){
            this.mergeTenantDialogView.open({fileTenantId: error.parameters[0], selectedTenantId: this.model.get("organization")});
        }
    }

    function applyBrokenDependencyStrategy(self, strategy){
        return function(str){
            self.model.set("brokenDependencies", strategy);
            self.doImport();
        };
    }

    function cancelImportTask(phase) {
        this.model.cancel();
        this.stateModel.set("phase", phase);
    }

});