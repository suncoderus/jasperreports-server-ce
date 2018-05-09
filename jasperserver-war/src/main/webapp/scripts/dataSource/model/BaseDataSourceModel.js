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
 * @author: Dima Gorbenko
 * @version: $Id$
 */

/* global ajax, dialogs, AjaxRequester */

define(function (require) {
    "use strict";

    var
		ResourceModel = require("bi/repository/model/RepositoryResourceModel"),
        _ = require("underscore"),
        $ = require("jquery"),
        Backbone = require("backbone"),
        jrsConfigs = require("jrs.configs");

    return ResourceModel.extend({
        defaults: (function (){
            var defaults = {};

            _.extend(defaults, ResourceModel.prototype.defaults, {
                connectionType: undefined
            });

            return defaults;
        })(),

        validation: {},

        initialize: function(attributes, options) {
			options = _.defaults(options || {}, {contextPath: jrsConfigs.contextPath});

            this.options = options;

			if (this.isNew()) {
				options.parentFolderUri && this.set("parentFolderUri", options.parentFolderUri, { silent: true });
			}

            var parentFolderUri = options.parentFolderUri ? options.parentFolderUri : attributes.parentFolderUri;

            if(attributes.name && parentFolderUri && !attributes.uri && options.isEditMode === true) {
                // if resource name and parent folder URI is given, but resource URI isn't set,
                // then let's generate it and set to the model for consistency purpose
	            // but we'll do it only in case of when we are in the editing mode
                this.set("uri", ResourceModel.constructUri(parentFolderUri, attributes.name), { silent: true });
            }

			ResourceModel.prototype.initialize.call(this, attributes, options);
        },
        
		testConnection: function() {
			this.validate();
			if (!this._isValid) return;

			// launch the loading timer and create a deferred object to encapsulate this timer
			var dfr = $.Deferred(),
				loadingDialog = $("#" + ajax.LOADING_ID)[0],
				loadingDialogOpened = false,
				responseTimer = window.setTimeout(function() {
						loadingDialogOpened = true;
						dialogs.popup.show(loadingDialog, true);
					}, AjaxRequester.prototype.MAX_WAIT_TIME);

			var data = this.toJSON();
            Backbone.ajax({
				type: "POST",
				url: jrsConfigs.contextPath + "/rest_v2/connections",
				contentType: data.connectionType,
                headers:{"Accept":"application/json"},
                data: JSON.stringify(data)
			}).always(function(){

				// remove that timer and close the dialog if it was open
				window.clearTimeout(responseTimer);
				loadingDialogOpened && dialogs.popup.hide(loadingDialog);

			}).done(dfr.resolve).fail(dfr.reject);

			return dfr.promise();
		}
    });
});
