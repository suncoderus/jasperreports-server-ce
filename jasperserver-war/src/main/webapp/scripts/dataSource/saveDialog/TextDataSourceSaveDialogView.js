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

define(function (require){

    "use strict";

    var _ = require('underscore'),
        i18n = require('bundle!all'),
        SimpleDomainView = require("dataSource/fileDataSource/SimpleDomainView"),
        BaseSaveDialogView = require("dataSource/saveDialog/BaseSaveDialogView"),
        textDataSourceSaveDialogTemplate = require('text!dataSource/saveDialog/template/textDataSourceSaveDialogTemplate.htm');


    return BaseSaveDialogView.extend({

        saveDialogTemplate: textDataSourceSaveDialogTemplate,

        constructor: function(options) {
            options || (options = {});
            this.options = _.extend({}, options);

            this.options.isEmbedded = !!(this.options.saveFn);

            BaseSaveDialogView.prototype.constructor.call(this, options);
        },

        initialize: function() {

            this.preSelectedFolder = this.options.model.options.parentFolderUri;

            BaseSaveDialogView.prototype.initialize.apply(this, arguments);

            this.listenTo(this.model, "change:prepareDataForReporting", this._onPrepareDataForReportingChange);
        },

        extendModel: function (sourceModel) {

            var model = BaseSaveDialogView.prototype.extendModel.call(this, sourceModel);

            model.set("prepareDataForReporting", !(this.options.isEmbedded || this.options.isEditMode));

            return model;
        },

        _onPrepareDataForReportingChange: function() {

            var textId = this._getLabelForSaveButton();

            this.changeButtonLabel("save", i18n[textId]);
        },

        _getLabelForSaveButton: function(model) {

            // this dialog can be called with any model
            model = model || this.model;

            var saveButtonLabel = "resource.datasource.saveDialog.save";

            if (model.get("dataSourceName") === "textDataSource" && !!model.get("prepareDataForReporting")) {
                saveButtonLabel = "resource.datasource.saveDialog.saveAndCreateDomain";
            }
            return saveButtonLabel;
        },

        _saveSuccessCallback: function (model, data) {

            if (!!this.model.get("prepareDataForReporting")) {

                var simpleDomainView = new SimpleDomainView({
                        cancel: this.options.success,
                        dataSource: this.model.toJSON()
                    });

                simpleDomainView.startDialog();

            } else {

                if (_.isFunction(this.options.success)) {
                    this.options.success();
                }
            }

            this._closeDialog();
        }
    });
});
