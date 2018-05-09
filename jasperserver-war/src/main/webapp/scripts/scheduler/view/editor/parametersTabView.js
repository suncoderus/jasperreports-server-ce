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

/* global ControlsBase, JRS, OptionsDialog */

define(function(require){

    "use strict";

    require('controls.options');
    require('controls.controller');

    // dependencies
    var $ = require('jquery'),
        _ = require('underscore'),
	    i18n = require('bundle!all'),
        config = require('jrs.configs'),
        Backbone = require('backbone'),
	    parametersTabTemplate = require("text!scheduler/template/editor/parametersTabTemplate.htm");

	_.extend(ControlsBase, config.inputControlsConstants);

    // return backbone view
    return Backbone.View.extend({

        // initialize view
        initialize: function(options) {

	        this.options = _.extend({}, options);

	        this.listenTo(this.model, "change:source", this.sourceChange);

            // handle values change
	        $(document).on("viewmodel:values:changed", _.bind(this.viewModelValuesChange, this));
        },

	    render: function() {
		    this.setElement($(_.template(parametersTabTemplate, {
			    i18n: i18n,
			    config: config
		    })));
	    },

	    saveAsDialogButtonSaveClick: function() {
		    var self = this,
		        optionName = this.reportOptionsDialog.input.getValue(),
			    selectedData = this.model.controlsController.getViewModel().get('selection'),
			    overwrite = optionName === this.reportOptionsDialog.optionNameToOverwrite;

		    $.when(this.reportOptions.add(this.reportOptions.optionsUrl || this.reportOptions.url, optionName, selectedData, overwrite)).
			    done(function () {
				    self.reportOptionsDialog.hideWarning();
				    var container = self.reportOptions.getElem().parent();
				    if (container.length === 0) {
					    container = self.$el.find('.saveCurrentValuesContainer');
					    container.prepend(self.reportOptions.getElem());
				    }
				    self.reportOptionsDialog.hide();
				    delete self.reportOptionsDialog.optionNameToOverwrite;
			    }).fail(function(err){
				    try {
					    var response = $.parseJSON(err.responseText);
					    if (response.errorCode === "report.options.dialog.confirm.message"){
						    !overwrite && (self.reportOptionsDialog.optionNameToOverwrite = optionName);
					    }
					    self.reportOptionsDialog.showWarning(response.message);
				    } catch (e) {
					    // In this scenario security error is handled earlier, in errorHandler, so we can ignore exception here.
					    // Comment this because it will not work in IE, but can be uncommented for debug purpose.
					    // console.error("Can't parse server response: %s", "controls.core", err.responseText);
				    }
			    });
	    },

	    sourceChange: function(model, value) {

		    // get url for report
		    var url = value && value.reportUnitURI;

		    // get parameters
		    var parameters = this.model.get('source').parameters;

		    // check for parameters
		    if (!parameters) {
			    return;
		    }

		    // go deeper in structure
		    var preSelectedParameterValues = parameters.parameterValues;

		    for(var item in preSelectedParameterValues) {
			    if (preSelectedParameterValues.hasOwnProperty(item)) {
			        if (preSelectedParameterValues[item] === null) {
				        delete preSelectedParameterValues[item];
			        }
			    }
		    }

		    if (this.reportOptions) {
			    this.reportOptions.url = undefined;
		    }

		    // create controls controller
			this.model.controlsController = new JRS.Controls.Controller({
				reportUri: url,
				reportOptionUri: '',
				preSelectedData: preSelectedParameterValues
			});

		    var self = this;
			this.model.controlsController.fetchControlsStructure(preSelectedParameterValues).done(function(){
				self.trigger("IC_Displayed");
			}).fail(function(){
				self.trigger("failedToGet_IC");
			});
	    },
	    viewModelValuesChange: function() {
		    // update model
		    this.model.set('source', {
			    reportUnitURI: this.model.get('source').reportUnitURI,
			    parameters: {
				    parameterValues: this.model.controlsController.getViewModel().get('selection')
			    }
		    }, { validate: false, silent: true });
	    }

    });

});