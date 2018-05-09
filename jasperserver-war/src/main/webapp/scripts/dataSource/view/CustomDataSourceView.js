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

define(function(require) {
	"use strict";

	var _ = require("underscore"),
		$ = require("jquery"),
		i18n = require("bundle!all"),
		BaseDataSourceView = require("dataSource/view/BaseDataSourceView"),
		customDataSourceTemplate = require("text!dataSource/template/customDataSourceTemplate.htm"),
		CustomDataSourceModel = require("dataSource/model/CustomDataSourceModel");

	return BaseDataSourceView.extend({
		PAGE_TITLE_NEW_MESSAGE_CODE: "resource.datasource.custom.page.title.new",
		PAGE_TITLE_EDIT_MESSAGE_CODE: "resource.datasource.custom.page.title.edit",

		modelConstructor: CustomDataSourceModel, // to be defined in specific view if you want to re-define it
		customFieldsTemplate: customDataSourceTemplate, // to be defined in specific view if you want to re-define it

		render: function() {
			this.$el.empty();

			this.renderCustomFieldsSection();

			if (this.model.testable) {
				this.renderTestConnectionSection();
			}
			return this;
		},

		templateData: function() {
			return _.extend({},
				BaseDataSourceView.prototype.templateData.apply(this, arguments),
				{
					i18n: i18n, // this redefines the basic 'jasperserver_messages' bundle with 'all' bundle
					customFields: this.model.customFields
				}
			);
		},

		renderCustomFieldsSection: function() {
			var html = _.template(this.customFieldsTemplate, this.templateData()),
				container = this.$el.find("[name=customFieldsContainer]");

			if (container.length > 0) {
				// the render has happened already, so we need to clean the container
				// and put custom field elements inside it
				container.empty().append($(html).children());
			} else {
				// the render happens first time, there is no any custom field elements and there is no custom
				// container, so we can simply put it there
				this.$el.append(html);
			}
		}
	}, {});
});