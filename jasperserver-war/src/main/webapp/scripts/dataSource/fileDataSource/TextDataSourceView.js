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

define(function(require) {
    "use strict";

    var
		$ = require("jquery"),
		_ = require("underscore"),
		i18n = require("bundle!all"),
        resourceLocator = require("resource.locate"),
        CustomDataSourceView = require("dataSource/view/CustomDataSourceView"),
        TextDataSourceModel = require("dataSource/fileDataSource/TextDataSourceModel"),
		delimitersTextDataSource = require("dataSource/fileDataSource/enum/delimitersTextDataSource"),
        characterEncodings = require("dataSource/fileDataSource/enum/characterEncodings"),
        fileSourceTypes = require("dataSource/fileDataSource/enum/fileSourceTypes"),
        fileSourceTypeOptions = _.reduce(fileSourceTypes, function(memo, value){
            if (value.name === "repository") {
                memo.push(value);
            }
            return memo;
        },[]),
        textBackboneTemplate = require("text!dataSource/fileDataSource/template/backboneTemplate.htm"),
		textFileLocationTemplate = require("text!dataSource/fileDataSource/template/fileLocationTemplate.htm"),
		textFilePropertiesTemplate = require("text!dataSource/fileDataSource/template/filePropertiesTemplate.htm"),
		selectDialogTemplate = require("text!common/templates/components.pickers.htm"),
        previewTableTemplate = require("text!dataSource/fileDataSource/template/previewTableTemplate.htm");

    require("css!dataSource/textDataSource.css");

    return CustomDataSourceView.extend({
        PAGE_TITLE_NEW_MESSAGE_CODE: "resource.datasource.text.page.title.new",
        PAGE_TITLE_EDIT_MESSAGE_CODE: "resource.datasource.text.page.title.edit",

        modelConstructor: TextDataSourceModel,
		browseButton: false,

		events: function() {
			var events = _.extend({}, CustomDataSourceView.prototype.events);

			events["change [name=fileSourceType]"] = "changeFileSourceType";

			return events;
		},

		initialize: function(options) {
			CustomDataSourceView.prototype.initialize.apply(this, arguments);

			this.listenTo(this.model, "change:serverFileName", this.adjustFileSystemConnectButton);
			this.listenTo(this.model, "change:serverAddress", this.adjustFtpServerConnectButton);
			this.listenTo(this.model, "change:serverPath", this.adjustFtpServerConnectButton);
			this.listenTo(this.model, "change:ftpsPort", this.adjustFtpServerConnectButton);

			this.listenTo(this.model, "change:fieldDelimiter", this.adjustFieldDelimiterSection);
			this.listenTo(this.model, "change:rowDelimiter", this.adjustRowDelimiterSection);

			this.listenTo(this.model, "change", this.adjustPreviewButton);

            this.listenTo(this.model, "sourceFileIsOK", this.sourceFileIsOK);
            this.listenTo(this.model, "sourceFileCantBeParsed", this.sourceFileCantBeParsed);
		},

		changeFileSourceType: function() {
			// since in the BaseDataSourceView there is a default listener, I'll trigger the rendering in the moment
			// right after all event listeners will be run
			_.defer(_.bind(function(){this.renderFileLocationSection();}, this));
		},

		render: function() {
            this.$el.empty();

			this.renderTextDataSourceSection();

            return this;
        },

		templateData: function() {
			return _.extend(
				CustomDataSourceView.prototype.templateData.apply(this, arguments),
				{
					fileSourceTypeOptions: fileSourceTypeOptions,
					fieldDelimiterOptions: delimitersTextDataSource,
					rowDelimiterOptions: delimitersTextDataSource,
					encodingOptions: characterEncodings
				}
			);
		},

		renderTextDataSourceSection: function() {
			this.$el.append(_.template(textBackboneTemplate, this.templateData()));

			this.renderFileLocationSection();
			this.renderFilePropertiesSection();
		},

		renderFileLocationSection: function() {

			this.renderOrAddAnyBlock(
				this.$el.find("[name=textDataSourceFieldsContainer]"),
				_.template(textFileLocationTemplate, this.templateData())
			);

			// remove the old browse button handler if exists
			if (this.browseButton) {
				this.browseButton.remove();
				this.browseButton = false;
			}

			if (this.model.get("fileSourceType") === "repository") {
				this.browseButton = resourceLocator.initialize({
					i18n: i18n,
					template: selectDialogTemplate,
					resourceInput: this.$el.find("[name=repositoryFileName]")[0],
					browseButton: this.$el.find("[name=repositoryBrowserButton]")[0],
					providerId: "contentResourceTreeDataProvider",
					dialogTitle: i18n["resource.Add.Files.Title"],
                    selectLeavesOnly: true,
					onChange: _.bind(function(value) {
						this.model.set("repositoryFileName", value);
						this.model.validate({"repositoryFileName": value});
					}, this)
				});
			}

			this.adjustFileSystemConnectButton();
			this.adjustFtpServerConnectButton();
		},

		adjustFileSystemConnectButton: function() {
			var flag = this.model.isValid("serverFileName");

			this._adjustButton("serverFileSystemConnectToServer", flag);
		},
		adjustFtpServerConnectButton: function() {
			var flag = this.model.isValid(["serverAddress", "serverPath", "ftpsPort"]);

			this._adjustButton("ftpConnectToServer", flag);
		},


		renderFilePropertiesSection: function() {
			this.renderOrAddAnyBlock(
				this.$el.find("[name=textDataSourceFieldsContainer]"),
				_.template(textFilePropertiesTemplate, this.templateData())
			);

			this.adjustFieldDelimiterSection();
			this.adjustRowDelimiterSection();
			this.adjustPreviewButton();
		},

		adjustFieldDelimiterSection: function() {
			this._adjustSection("fieldDelimiter", {
				regex: "fieldDelimiterRegexInput",
				plugin: "fieldDelimiterPluginInput",
				other: "fieldDelimiterOtherInput"
			});
		},

		adjustRowDelimiterSection: function() {
			this._adjustSection("rowDelimiter", {
				regex: "rowDelimiterRegexInput",
				plugin: "rowDelimiterPluginInput",
				other: "rowDelimiterOtherInput"
			});
		},

		adjustPreviewButton: function() {
			var flag1 = this.model.get("fileSourceType") === "repository" && this.model.isValid("repositoryFileName");
			var flag2 = this.model.get("fileSourceType") === "serverFileSystem" && this.model.isValid("serverFileName");
			var flag3 = this.model.get("fileSourceType") === "ftpServer" && this.model.isValid(["serverAddress", "serverPath", "ftpsPort"]);
			var flag = flag1 || flag2 || flag3;

			this._adjustButton("previewDataSource", flag);
		},



		_adjustButton: function(buttonSelector, enabledState) {
			var button = this.$el.find("[name=" + buttonSelector + "]");
			if (!!enabledState) {
				button.removeAttr("disabled");
			} else {
				button.attr("disabled", "disabled");
			}
		},

		_adjustSection: function(modelAttrId, elementsInSectionToAdjust) {
			var self = this;
			_.each(elementsInSectionToAdjust, function(elementName, valueItShouldHaveToBeEnabled) {
				var section = self.$el.find("[name=" + elementName + "]");
				section.toggleClass("hidden", self.model.get(modelAttrId) !== valueItShouldHaveToBeEnabled);
			});
		},

        sourceFileIsOK: function() {
            this.fieldIsValid(this, "repositoryFileName", "name");
        },

        sourceFileCantBeParsed: function() {
            this.fieldIsInvalid(this, "repositoryFileName", i18n["resource.file.cantBeProcessed"], "name");
        }
	});
});