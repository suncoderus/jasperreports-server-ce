/**
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
 * @author: Olesya Bobruyko
 * @version:
 */

define(function(require) {

    var _ = require("underscore"),
        Dialog = require("common/component/dialog/Dialog"),
        ImportView = require("./ImportView"),
        i18n = require("bundle!CommonBundle"),
        i18n2 = require("bundle!ImportExportBundle"),
        importExportTypesEnum = require("tenantImportExport/export/enum/exportTypesEnum");

    return Dialog.extend({

        constructor: function(options) {

            options || (options = {});
            this.options = options;

            Dialog.prototype.constructor.call(this, {
                model: this.model,
                modal: true,
                resizable: true,
                additionalCssClasses: "tenant-import-dialog",
                content: "",
                buttons: [
                    { label: i18n["button.import"], action: "import", primary: true },
                    { label: i18n["button.cancel"], action: "cancel", primary: false }
                ]
            });

            this.on('button:import', _.bind(this._onImportButtonClick, this));
            this.on('button:cancel', _.bind(this._closeImportDialog, this));
        },

        initialize: function(options) {
            Dialog.prototype.initialize.apply(this, arguments);

            this.importView = new ImportView();
            this.listenTo(this.importView, "import:finished", function(tenantId) {
                this.close();
                this.trigger("import:finished", tenantId);
            });

            this.listenTo(this.importView.model, "validated", function(isValid) {
                isValid ? this.buttons.enable("import") : this.buttons.disable("import");
            }, this);
        },

        openDialog: function(tenant) {
            var title = i18n2["tenant.import.dialog.title"] + " " + tenant.name;

            var type = !tenant.isRoot() ? importExportTypesEnum.TENANT : importExportTypesEnum.ROOT_TENANT;

            tenant.isRoot() && this.addCssClasses("tenant");

            this.setContent(this.importView.render({type: type, tenantId: tenant.id}).$el);
            this.setTitle(title);

            Dialog.prototype.open.apply(this, arguments);

            //TODO: Find out why applyEpoxyBindings and delegateEvents should be called after opening dialog with new content.
            this.importView.applyEpoxyBindings();
            this.importView.delegateEvents();

            this.$el.css("minHeight", this.$el.outerHeight());
        },

        // block default validation handlers
        fieldIsValid: function() {

        },

        fieldIsInvalid: function(error) {

        },

        _closeImportDialog: function() {
            this.$el.css({minHeight: 0, height: "auto", width: "auto"});
            this.close();
        },

        _onImportButtonClick: function() {
            this.importView.doImport();
            this._closeImportDialog();
        }
    });

});