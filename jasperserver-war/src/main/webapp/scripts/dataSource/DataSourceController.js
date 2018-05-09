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

/* global dialogs, redirectToUrl */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        $ = require("jquery"),
        i18n = require("bundle!all"),
        BaseDataSourceModel = require("dataSource/model/BaseDataSourceModel"),
		TouchController = require("touchcontroller"),
        featureDetection = require("common/util/featureDetection"),
		history = require("util/historyHelper"),
        dataSourceViewFactory = require("dataSource/factory/dataSourceViewFactory"),
        dataSourceResourceTypes = require("dataSource/enum/dataSourceResourceTypes"),
        saveDialogViewFactory = require("dataSource/factory/saveDialogViewFactory"),
        dataSourceMainTemplate = require("text!dataSource/template/dataSourceMainTemplate.htm"),
		jrsConfigs = require('jrs.configs'),
        CustomDataSourcesCollection = require("dataSource/collection/CustomDataSourceCollection"),
        awsSettings = require("settings!awsSettings"),
        settingsUtility = require("dataSource/util/settingsUtility");

    return Backbone.View.extend({

		dataSourceType: false,
		dataSourceView: false,
		saveDialog: false,

        events: {
            "change select[name='dataSourceType']" : "onDataSourceTypeChange",
            "click #saveBtn" : "onSaveClick",
            "click #cancelBtn" : "onCancelClick"
        },

		historyBackToken: "DataSourceControllerHistory",

        constructor: function(options) {

			// since the options object will be changed, we need to make the copy of it
			options = $.extend(true, {}, options);
			arguments[0] = options;

            this.isEditMode = options.isEditMode;
            Backbone.View.apply(this, arguments);
        },

        initialize: function(options) {
            this.options = options;
            this.dataSourceType = undefined;

            featureDetection.supportsTouch && this.initSwipeScroll();
			history.saveReferrer(this.historyBackToken);

			// Starting the game !
			// from the early beginning we need to have answers to 2 questions:
			// a) what custom data sources are defined in XML files on the server ?
			// b) are we in the editing mode ? if yes, then we need to get that model from the server
			//
			// As you will see below, these questions need async requests to server, and because of
			// this we are creating two deferred objects
			// allows us to wait till we get 'custom data sources' from xml files
			this.fetchingCustomDataSourcesDeferred = $.Deferred();
			// allows us to wait till we fetch the model
			this.fetchingTheModelDeferred = $.Deferred();

			// So, fetching 'custom data sources' from the server
			// (these 'custom data sources' defined in XML files)
            this.customDataSourceCollection = new CustomDataSourcesCollection();
            this.customDataSourceCollection.fetch().done(_.bind(function(){this.renderDataSourceContainer();}, this));

            var deepDefaults = settingsUtility.deepDefaults(options, {
                awsSettings: awsSettings
            });
			// Also, deciding if we are in the editing mode, and if we are, then we need to fetch the model
			// If we aren't, then we can just resolve the deferred object
            if (this.options.resourceUri) {
                // we are in the editing an existing DS mode, so we need to fetch it from the server
                var self = this, modelToEdit = new BaseDataSourceModel({uri: this.options.resourceUri});
                modelToEdit.fetch().then(function(data, textStatus, jqXHR) {
                    self.dataSource = modelToEdit.attributes;
                    self.dataSourceType = dataSourceViewFactory.getViewType(jqXHR.getResponseHeader("Content-Type"), self.dataSource);
                    self.fetchingTheModelDeferred.resolve();
                });
            } else if (this.options.dataSource && this.options.dataSourceClientType) {
                // data source and it's client type are available. So, it's edit mode and no fetching is required.
                this.dataSource = this.options.dataSource;
                this.dataSourceType = dataSourceViewFactory.getViewType(this.options.dataSourceClientType, this.options.dataSource);
                this.fetchingTheModelDeferred.resolve();
            } else {
                // if this product working on AWS - by default we create AWS Data Source
                if (deepDefaults.awsSettings.productTypeIsEc2) {
                    this.dataSourceType = dataSourceViewFactory.getViewType(dataSourceResourceTypes.AWS, null);
                }
                this.fetchingTheModelDeferred.resolve();
            }
        },

        renderDataSourceContainer: function() {
            // key in dataSourceLabelSuffixes should be equal to corresponding key in dataSourceResourceTypes
            var dataSourceLabelSuffixes = {
                    AZURE_SQL: "AzureSql",
                    AWS: "Aws",
                    BEAN: "Bean",
                    JDBC: "JDBC",
                    JNDI: "JNDI",
                    VIRTUAL: "Virtual"
                };

            // fill in the pre-existent data sources
            var dataSourceTypeOptions =  _.chain(dataSourceLabelSuffixes)
                .map(function(value, key){
                    return {
                        value: dataSourceResourceTypes[key].toLowerCase(),
                        label: i18n['resource.dataSource.dstype' + value]
                    };
                })
                .value();

			// now, add custom data sources which are defined in XML files on the server
            this.customDataSourceCollection.forEach(function(element) {
                var currentCustomDataSourceType = element.get("id");
                // disable file data sources for now
        //        if (currentCustomDataSourceType in {"xlsDataSource": 1, "xlsxDataSource": 1, "textDataSource":1}) {
        //            return;
        //        }

                dataSourceTypeOptions.push({
                    value: currentCustomDataSourceType,
                    label: i18n[currentCustomDataSourceType + '.name'] ? i18n[currentCustomDataSourceType + '.name'] : currentCustomDataSourceType
                });
            });

			// sort them
            dataSourceTypeOptions = _.sortBy(dataSourceTypeOptions, function(option) {
                return option.label.toLowerCase();
            });

			// and display the interface header and select of data source type
            this.$el.append(_.template(dataSourceMainTemplate, {
                dataSourceTypeOptions: dataSourceTypeOptions,
                i18n: i18n,
                supportsTouch: featureDetection.supportsTouch,
                isEditMode: this.isEditMode
            }));

			// resolve deferred object
            this.fetchingCustomDataSourcesDeferred.resolve();
        },

        initSwipeScroll: function() {
            var display = this.$("#stepDisplay");
            display.length && new TouchController(display.parent()[0], display.parent().parent()[0], {});
        },

        render: function(){
            $.when(this.fetchingCustomDataSourcesDeferred, this.fetchingTheModelDeferred)
                .done(_.bind(function(){this._render();}, this));
        },

        _render: function() {

			var saveParams = {};
			if (this.dataSourceView) {
				saveParams = {
					label: this.dataSourceView.model.get("label"),
					name: this.dataSourceView.model.get("name"),
					description: this.dataSourceView.model.get("description")
				};
			}

            this.dataSourceView && this.dataSourceView.remove();
            delete this.dataSourceView;
            // if we already removed view, "body" element was also removed, so we need to recreate it for new view manually
            if (this.$(".row.inputs .body:eq(0)").length === 0) {
                this.$(".row.inputs > .column > .content").append("<div class='body dataSourceBody'></div>");
            }

			if (!this.dataSourceType) {
				this.dataSourceType = dataSourceResourceTypes.JDBC.toLowerCase();

				// Use it for development reasons: you can pre-select any DS type
				//this.dataSourceType = "MongoDbDataSource";
			}

            this.dataSourceView = dataSourceViewFactory.getView(_.extend(this.options, {
                dataSourceType: this.dataSourceType,
                dataSource: _.extend({}, this.dataSource, saveParams),
                el: this.$(".row.inputs .body:eq(0)")
            }));

			// set specific class to help customize specific page with css styles
			this.$(".dataSourceBody").attr("dstype", this.dataSourceType.toLowerCase());

            this.$("select[name='dataSourceType']").val(this.dataSourceType);
            return this;
        },

        onDataSourceTypeChange: function(e) {
            var selectedType = $(e.target).val();

            if (this.dataSourceType != selectedType) {
                this.dataSourceType = selectedType;
                this.render();
            }
        },


        _prepareSaveDialog: function() {
            this.saveDialog && this.saveDialog.remove();

            var SaveDialog = saveDialogViewFactory.getView(this.dataSourceType);

            this.saveDialog = new SaveDialog(_.extend({}, this.options, {
                model: this.dataSourceView.model,
                saveFn: this.options.saveFn,
                success: _.bind(this._onSaveDone, this),
                error: _.bind(this._onSaveFail, this)
            }));
        },

        onSaveClick: function() {

            if (!this.dataSourceView.model.isValid(true)) {
                return;
            }

            var self = this, funcOnceValidationPassed = function() {
                self._prepareSaveDialog();
                self.saveDialog.startSaveDialog();
            };

            // validationMethodOnSaveClick is moved to view in text-data-source branch,
            // but let's have them both (in model and in a view) here till merged
            if (!_.isUndefined(this.dataSourceView.model.validationMethodOnSaveClick)) {
                this.dataSourceView.model.validationMethodOnSaveClick(funcOnceValidationPassed);
                return;
            } else if (!_.isUndefined(this.dataSourceView.validationMethodOnSaveClick)) {
                this.dataSourceView.validationMethodOnSaveClick(funcOnceValidationPassed);
                return;
            }
            funcOnceValidationPassed();
        },

		_onSaveDone: function() {
			redirectToUrl(jrsConfigs.contextPath + "/flow.html?_flowId=repositoryConfirmFlow&resourceType=dataSource");
		},

		_onSaveFail: function(model, xhr) {

			if (this.saveDialog) {
				this.saveDialog.close();
				this.saveDialog.remove();
			}

			var self = this, errors = false, msg;
			var handled = false;
			try { errors = JSON.parse(xhr.responseText); } catch(e) {}

			if (!_.isArray(errors)) {
				errors = [errors];
			}

			_.each(errors, function(error){

				var field = false, msg = false;

				if (!error) {
					return;
				}

				if (error.errorCode === "mandatory.parameter.error") {
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

				// converting field names
				if (field === "ConnectionUrl") {
					field = "connectionUrl";
				}

				if (msg && field) {
					self.dataSourceView.invalidField(
						"[name=" + field + "]",
						msg
					);
					handled = true;
				}
			});

			if (handled === false) {
				// otherwise, proceed with common error handling

				msg = "Failed to save data source.";

				if (errors[0] && errors[0].errorCode) msg += "<br/>The reason is: " + errors[0].errorCode;
				else if (errors.message) msg += "<br/>The reason is: " + errors.message;

				msg += "<br/><br/>The full response from the server is: " + xhr.responseText;

				dialogs.errorPopup.show(msg);
			}
		},

		onCancelClick: function() {
            if (this.options.cancelFn) {
                this.options.cancelFn();
            } else {
				history.restore(this.historyBackToken);
            }
        }
    });
});