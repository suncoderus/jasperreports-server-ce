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
 * @version: $Id$
 */

/* global dialogs */

define(function(require){

    "use strict";

    var $ = require('jquery'),
	    _ = require('underscore'),
        Backbone = require('backbone'),
        i18n = require('bundle!all'),
	    AlertDialog = require("common/component/dialog/AlertDialog"),
	    SaveDialogView = require("scheduler/saveDialog/SaveDialogView"),
        editorScheduleTabView = require('scheduler/view/editor/scheduleTabView'),
        editorParametersTabView = require('scheduler/view/editor/parametersTabView'),
        editorOutputTabView = require('scheduler/view/editor/outputTabView'),
        editorNotificationsTabView = require('scheduler/view/editor/notificationsTabView'),
        schedulerUtils = require("scheduler/util/schedulerUtils"),
        jobEditorViewTemplate = require("text!scheduler/template/jobEditorViewTemplate.htm");

    return Backbone.View.extend({

        editMode: false, // used for editing then we need to have different validations - for example we should skip the isPastDate

	    runNowMode: false, // this is a mode in which some tabs are not activated

	    events: {
		    "mouseup [name=tabs] li": "tabChangeClickEvent",
		    "touchend [name=tabs] li": "tabChangeClickEvent",
		    "click [name=openSaveDialog]": "openSaveDialogClick",
		    "click [name=cancelJobCreation]": "cancelJobCreationClick"
	    },

        // initialize view
        initialize: function(options) {

            this.options = _.extend({}, options);
            this.tabs = {};

            // model is valid, show success messages
	        this.listenTo(this.model, "valid", this.validModelListener);

            // model is invalid, show error messages
            this.listenTo(this.model, "invalid", this.invalidModelListener);

	        // handle any type of validation started
	        this.listenTo(this.model, "clearAllErrors", this.clearAllValidationErrors);

            // disable/enable parameters due to model state
            this.listenTo(this.model, "change:source", this.sourceKeyInModelChanged);

	        this.listenTo(this.model, "failedToGet_IC", this.InputControlsNotLoaded);

			this._initializeTabs(options);

	        this.listenTo(this.tabs.parametersTab, "IC_Displayed", this.InputControlsLoaded);
	        this.listenTo(this.tabs.parametersTab, "failedToGet_IC", this.InputControlsNotLoaded);
        },

		_initializeTabs: function (options) {
			var tabParams = {
				model: this.model,
				reportUri: options.reportUri,
				parentReportURI: options.parentReportURI
			};
			this.tabs.scheduleTab = new editorScheduleTabView(tabParams);
			this.tabs.parametersTab = new editorParametersTabView(tabParams);
			this.tabs.outputTab = new editorOutputTabView(tabParams);
			this.tabs.notificationsTab = new editorNotificationsTabView(tabParams);
		},


		remove: function() {
		    this.tabs.scheduleTab.remove();
		    this.tabs.parametersTab.remove();
		    this.tabs.outputTab.remove();
		    this.tabs.notificationsTab.remove();

		    Backbone.View.prototype.remove.apply(this, arguments);
	    },

	    // ==================================================================================================
	    // Event listeners methods

		tabChangeClickEvent: function (event) {
			var tabElement = $(event.currentTarget);
			var tabName = tabElement.attr("data-tab");

			// don't change if clicked tab is disabled
			if (tabElement.hasClass('disabled')) {
				return;
			}

			// stop if tab already selected
			if (this.currentActiveTabName === tabName) {
				return;
			}

			var self = this;
			this.model.validateAll(this.editMode).always(function() {

				// search for error(s) on current tab if there are any
				var errors = self.$el.find("[name=" + self.currentActiveTabName + "]").find(".error");

				// don't switch current tab if one has error(s)
				if (errors.length) {
					return;
				}

				self.changeActiveTab(tabName);

				// clear errors on the tab if any
				self.clearAllValidationErrors();
			});

		},

	    openSaveDialogClick: function () {
		    this.startSavingProcess();
	    },

	    cancelJobCreationClick: function() {
		    this.trigger("cancelJobCreation");
	    },

	    // ==================================================================================================
	    // render interface methods...

	    renderCreateNewJobInterface: function() {

		    // set the variable to indicate which mode we are going to render
		    this.editMode = false;

		    // do the actual rendering....
		    this._render();

		    // adjust the tabs according to the running mode
		    if (this.options.runInBackgroundMode || this.runNowMode) {
			    this.changeActiveTab("outputTab");
		    } else {
			    this.changeActiveTab("scheduleTab");
		    }

		    // disable save button till we load data for parameters tab
		    this.prepareSaveOrSubmitButton();
	    },

	    prepareModelForCreatingNewJob: function() {

		    // create new model from report's uri
		    this.model.clear({silent: true});

		    // remove ID
		    this.model.unset("id");

		    // prevent reaction on models' change
			this.dontReactOnModelChange = true;
		    this.model.createFromUri(this.options.reportUri);
		    this.dontReactOnModelChange = false;

		    if (this.runNowMode) {
			    this.model.set("label", "Immediate Execution");
		    }

		    // Next, load data for parameters tab
		    this.model.loadParameters(this.options.parentReportURI || false);
	    },

	    // edit job by job id
	    editExistingJob: function(jobId) {

		    this.editMode = true;

		    // private variables
		    var self = this;

			// don't let listeners for model's change be run
			this.model.clear({silent: true});

			this._render();

		    // select first tab
		    this.changeActiveTab("scheduleTab");

		    // set model id
		    this.model.set({id: jobId});

			this.prepareSaveOrSubmitButton();

			if (this.model._fetched) {
				this.model.set(this.model.parse(this.model._fetched));
				this.setTitle(this.model.get('label'));
				this.model._fetched = undefined;
			} else {
				// fetch model from server
				this.model.fetch({
					success: function(){
						// set proper title
						self.setTitle(self.model.get('label'));
					}
				});
			}
	    },

		prepareSaveOrSubmitButton: function() {
			var
				openSaveDialogButton = this.$el.find("[name=openSaveDialog]"),
				cancelButton = this.$el.find(".footer #cancel");

			// disable save button till we have a signal we may enable them back
			openSaveDialogButton.attr("disabled", "disabled").addClass("disabled");

			// but cancel button we enable (it will be disabled during save process)
			cancelButton.attr("disabled", null).removeClass("disabled");

			// delay of enabling Save/Submit button till all data will be loaded
			this.saveButtonReady = new $.Deferred();

			this.saveButtonReady.done(function () {
				// enable open save dialog button
				openSaveDialogButton.attr("disabled", null).removeClass("disabled");
			});
		},

	    InputControlsLoaded: function() {
		    this.saveButtonReady.resolve();
	    },

	    InputControlsNotLoaded: function() {
		    this.saveButtonReady.resolve();
		    this.toggleEnableTab('parametersTab', false);
	    },

	    // ==================================================================================================
	    // Service methods...

	    setRunNowMode: function(runNowMode) {
		    this.runNowMode = !!runNowMode;
	    },

	    _render: function() {
		    var templateData = {
			    i18n: i18n,
			    reportUri: this.options.reportUri,
			    runNowMode: this.runNowMode
		    };

		    this.setElement($(_.template(jobEditorViewTemplate, _.extend({}, templateData))));

		    if (this.runNowMode) {
			    this.$el.find("li[data-tab=scheduleTab]").addClass("disabled");
		    }

			this.tabs.outputTab.options.editMode = this.editMode;

			this.tabs.scheduleTab.render();
		    this.tabs.parametersTab.render();
		    this.tabs.outputTab.render();
		    this.tabs.notificationsTab.render();

		    this.$el.find("[name=scheduleTab]").append(this.tabs.scheduleTab.$el);
		    this.$el.find("[name=parametersTab]").append(this.tabs.parametersTab.$el);
		    this.$el.find("[name=outputTab]").append(this.tabs.outputTab.$el);
		    this.$el.find("[name=notificationsTab]").append(this.tabs.notificationsTab.$el);

		    // by default no tab is rendered
		    this.currentActiveTabName = "";
	    },

	    setTitle: function(title) {
		    this.$el.find('.header .title').text(title);
	    },

	    changeActiveTab: function(tabName) {

		    if (!tabName) { return; }

		    // get name of first tab with error
		    if (tabName === "error") {
			    tabName = this.$el.find("[name=tabHolder] > div").find('.error').first().parents('.tab').attr('name');
		    }

		    // stop if tab already selected
		    if (this.currentActiveTabName === tabName) {
			    return;
		    }

		    // change active tab on the tab's list
		    this.$el.find("[name=tabs] li").removeClass("selected").filter("[data-tab=" + tabName + "]").addClass("selected");

		    // and change the active tab which is visible
		    this.$el.find("[name=tabHolder] > div").addClass("hidden").filter("[name=" + tabName + "]").removeClass("hidden");

			this.currentActiveTabName = tabName;

	    },

        // set tab state
        toggleEnableTab: function(tabName, state) {

            var tabElement = this.$el.find("[name=tabs] li").filter("[data-tab=" + tabName + "]");

	        tabElement.attr('disabled', !state).toggleClass('disabled', !state);

            // select nearest enabled tab
            if (!state && tabElement.hasClass('selected')) {
	            this.changeActiveTab(tabElement.nextAll(':not(.disabled):first').data('tab'));
            }
        },

	    sourceKeyInModelChanged: function(model, value) {
		    var empty;

			// inside function create() we initialize the model with
			// default parameters. That initialization should not affect
			// save buttons because saveButtons must be handled only
			// after we made special request to server, fetch information about
			// parameters, save it to the model and handle them in this handler
			if (this.dontReactOnModelChange === true) {
				return;
			}

		    if (value.parameters) {

			    empty = _.keys(value.parameters.parameterValues).length === 1;

			    // parameters are empty if length is 1 and it is timezone
			    empty &= !!value.parameters.parameterValues.REPORT_TIME_ZONE;
		    }

		    // also adjust Parameters tab
			var doShowParametersTab = value.parameters && !empty;

			if (!doShowParametersTab) {
				// Save button is disabled while parameters tab is loading, but if
				// parameter tab will not be displayed we can enable Save button
				this.saveButtonReady.resolve();
			}

		    this.toggleEnableTab('parametersTab', doShowParametersTab);
		    this.$el.find('.schedule_for').find('.path').text(value.reportUnitURI);
	    },

	    // ==================================================================================================
	    // Save methods...

	    _prepareSaveDialog: function() {
		    this.saveDialog && this.saveDialog.remove();

		    var self = this;
		    this.saveDialog = new SaveDialogView(_.extend({}, this.options, {
			    model: this.model,
				isEditMode: this.editMode,
			    onSaveDone: _.bind(this._onSaveDone, this),
			    onSaveFail: _.bind(this._onSaveFail, this)
		    }));

		    // during the save procedure we call different methods to check
		    // validity of model's data. Some of them may return "invalid" state,
		    // which means user has to make some corrections.
		    this.listenTo(this.saveDialog, "saveValidationFailed", function() {
			    self.saveDialog.closeDialog();
		    });
	    },

	    startSavingProcess: function() {
			var self = this;
			this.model.validateAll(this.editMode).done(function() {
				self._prepareSaveDialog();
				self.saveDialog.startSaveDialog();
			}).fail(function() {
				self.changeActiveTab('error');
			});
		},

	    _onSaveDone: function() {
		    // the dialog is already closed himself, so don't worry
		    this.trigger("jobHasBeenCreated");
	    },

	    _onSaveFail: function(model, xhr, options) {

		    this.saveDialog.closeDialog();

		    var
			    self = this,
			    response = false,
			    justExit = false,
			    errorHandled = false;

		    try { response = JSON.parse(xhr.responseText); } catch(e) {}
		    if (response.error) {
			    response = response.error;
		    }
		    if (!_.isArray(response)) {
			    response = [response];
		    }

		    _.each(response, function(error) {

			    var message = "";
			    var field = "";

			    if (error.errorCode === "mandatory.parameter.error") {
				    message = i18n["report.scheduling.saveDialog.parameterIsMissing"];

				    if (error.parameters && error.parameters[0]) {
					    field = error.parameters[0].substr(error.parameters[0].indexOf(".") + 1);
				    }
			    }

			    if (error.errorCode === "error.duplicate.report.job.output.filename") {
				    field = "baseOutputFilename";
				    message = i18n["error.duplicate.report.job.output.filename"].replace("{0}", error.errorArguments[0]).replace("{1}", error.errorArguments[1]);
			    }
			    if (error.message && error.message.indexOf("will never fire") !== -1) {
				    field = "triggerWillNeverFire";
				    message = i18n["error.report.job.trigger.no.fire"];
			    }
			    if (error.errorCode === "error.pattern") {
				    if (error.field === "trigger.hours") {
					    field = "hours";
					    message = i18n["error.pattern.trigger.hours"];
				    }
				    if (error.field === "trigger.minutes") {
					    field = "minutes";
					    message = i18n["error.pattern.trigger.minutes"];
				    }
				    if (error.field === "trigger.monthDays") {
					    field = "datesInMonth";
					    message = i18n["error.pattern.trigger.monthDays"];
				    }
				    if (error.field === "contentRepositoryDestination.timestampPattern") {
					    field = "timestampPattern";
					    message = i18n["error.pattern.contentRepositoryDestination.timestampPattern"];
				    }
			    }

			    // A special case them we are trying to edit job which already been executed.
			    // This may happen if job is going to run in few seconds and user clicks on Edit icon.
			    // Job might be executed so if users clicks on Save we'll face next issue
			    if (error.errorCode === "resource.not.found") {
				    // in this case we have to show alert dialog and get back to Repository
				    var alertDialog = new AlertDialog({
					    modal: true,
					    additionalCssClasses: "schedulerJobRemovedAlertDialog"
				    });

				    alertDialog.setMessage(i18n["report.scheduling.editing.jobHasBeenRemoved"]);

				    self.listenTo(alertDialog, "close", function(){
					    self.trigger("errorEditingJob");
				    });

				    alertDialog.open();
				    justExit = true;
				    return;
			    }

			    if (message === "") {
				    return;
			    }

			    self.showValidationMessage("error", {
				    field: field,
				    message: message
			    });
			    errorHandled = true;
		    });

		    if (justExit === true) {
			    return;
		    }

		    if (errorHandled) {
			    this.changeActiveTab('error');
		    }

		    if (errorHandled === false) {
			    // otherwise, proceed with common error handling

			    var msg = i18n["report.scheduling.editing.failedToSave"] + ".";

			    if (response[0] && response[0].errorCode) msg += "<br/>The reason is: " + response[0].errorCode;
			    else if (response.message) msg += "<br/>The reason is: " + response.message;

			    msg += "<br/><br/>The full response from the server is: " + xhr.responseText;

			    dialogs.errorPopup.show(msg);
		    }
	    },

	    // ==================================================================================================
	    // Validation methods...

	    clearAllValidationErrors: function() {
		    this.$el.find('.error').removeClass('error');
	    },

	    validModelListener: function(messages) {
		    var self = this;
		    _.each(messages, function(message){
			    self.showValidationMessage('success', message);
		    });
	    },

	    invalidModelListener: function(errors, options) {

		    var self = this;
		    _.each(errors, function(error) {
			    self.showValidationMessage('error', error);
		    });

		    if (options && options.switchToErrors) {
			    self.changeActiveTab('error');
		    }
	    },

	    // show validation message
	    showValidationMessage: function(type, data) {

		    // skip errors we can't handle
		    if (!data.field) {
			    return;
		    }

		    // it happened what server sends an error message with different field names.
		    // correct it !
		    if (data.field === "contentRepositoryDestination.folderURI") {
			    data.field = "contentRepositoryDestination.outputRepository";
		    }

		    // remove prefixes
		    data.field = data.field.replace('trigger.', '');
		    data.field = data.field.replace('mailNotification.', '');
		    data.field = data.field.replace('contentRepositoryDestination.', '');

		    // get controls list
		    var message = this.$el.find('.warning[data-field=' + data.field + ']');

		    // set the "error" class into parent, because this is requirement of JRS CSS structure
		    message.parent().addClass("error");

		    // norw, adjust class type of the message field
		    message.removeClass("success").removeClass("error"); // remove all types of notifications
		    message.addClass(type); // now, add the class name which represents the current notification

		    var text = "";

		    if (data.message) {

			    text = data.message;

		    } else if (data.errorCode) {

			    // get error text from i18n
			    text = i18n[data.errorCode];

			    if (!text) {
				    return;
			    }

			    // insert error arguments
			    if (data.errorArguments) {
				    for (var i = 0, l = data.errorArguments.length; i < l; i++) {
					    text = text.replace('{' + i + '}', data.errorArguments[i]);
				    }
			    }
		    }

		    // show warning box
		    if (text !== "") {
			    message.text(text);
		    }
	    }
    });
});
