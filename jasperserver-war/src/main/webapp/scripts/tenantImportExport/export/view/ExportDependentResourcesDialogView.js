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
 * @author: Zakhar Tomchenko
 * @version:
 */

define(function(require) {

    var _ = require("underscore"),
        i18n = require("bundle!CommonBundle"),
        i18n2 = require("bundle!ImportExportBundle"),
        BaseWarningDialogView = require("tenantImportExport/view/BaseWarningDialogView");

    return BaseWarningDialogView.extend({

        constructor: function(options) {
            options || (options = {});

            _.extend(options, {
                resizable: true,
                buttons: [
                    { label: i18n2["export.button.continue"], action: "export", primary: true },
                    { label: i18n["button.cancel"], action: "cancel", primary: false }
                ]});

            BaseWarningDialogView.prototype.constructor.call(this, options);

            this.on('button:export', _.bind(this.close, this));
            this.on('button:cancel', _.bind(this.close, this));
        },

        open: function(options) {
            _.extend(options, {message: i18n2["export.dialog.broken.dependencies.intro"]});

            BaseWarningDialogView.prototype.open.call(this, options);
        }

    });

});