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

/* global Template, layoutModule, $$, $break, _, dialogs, matchAny */

var ControlsBase = {
    INPUT_CONTROLS_FORM: "inputControlsForm",
    INPUT_CONTROLS_CONTAINER: "inputControlsContainer",
    INPUT_CONTROLS_DIALOG: "inputControls",
    SAVE_REPORT_OPTIONS_DIALOG: "saveValues",
    REPORT_OPTIONS_SELECTOR: "savedValues",
    TOOLBAR_CONTROLS_BUTTON: 'ICDialog',
    DEFAULT_OPTION_TEXT : "Options",
    // These constants are set from server
    NULL_SUBSTITUTION_VALUE: null,
    NULL_SUBSTITUTION_LABEL: null,
    NOTHING_SUBSTITUTION_VALUE: null,

    _BUTTON_OK: "ok",
    _BUTTON_SAVE: "save",
    _BUTTON_REMOVE: "remove",
    _BUTTON_APPLY: "apply",
    _BUTTON_CANCEL: "cancel",

    TEMP_REPORT_CONTAINER: 'tempReportContainer',
    WRAPPER_ERROR_FLAG_PREFIX_SELECTOR: 'div[id^=error_]',
    WRAPPERS_ERRORS_DIV: 'wrappersErrors',
    EVAL_SCRIPT_ELEMENT_NAME: "_evalScript",

    // Used to properly position icon cross-browser
    CALENDAR_ICON_SPAN: '<span class="button picker calTriggerWrapper" />',

    _messages: {},
    getMessage: function(messageId, object) {
        var message = this._messages[messageId];
        return message ? new Template(message).evaluate(object ? object : {}) : "";
    },

    setControlError: function(errorDiv, error) {
        if (!errorDiv) return;
        errorDiv = $(errorDiv);

        if (error) {
            errorDiv.up().addClassName(layoutModule.ERROR_CLASS);
            errorDiv.nextSiblings()[0].update(error);
        } else {
            errorDiv.up().removeClassName(layoutModule.ERROR_CLASS);
            errorDiv.nextSiblings()[0].update('');
        }
    },

    setControlsErrors : function(errors) {
		var errorDivs = $$(ControlsBase.WRAPPER_ERROR_FLAG_PREFIX_SELECTOR);
		var index = 0;
		var offset = -1;
		errorDivs.each(function(div) {
			var error = false;
			errors && errors.each(function(pair) {
				if (div.id.indexOf(pair[0]) > -1) {
					ControlsBase.setControlError(div, pair[1]);
					if (offset < 0) {
                        jQuery('#inputControlsForm .leaf').each(function(i,element){
                            if (i<index) offset += element.clientHeight;
                        });
						(jQuery('.groupBox .content .body')[0]).scrollTop = offset;
					}
					error = true;
					throw $break;
				} else
					index++;
			});
			if (!error) {
				ControlsBase.setControlError(div, '');
			}
		});
	},
    /*
        TODO: remove after Emerald2 release.
     */
    /*
    evalScripts: function() {
        var elements = document.getElementsByName(ControlsBase.EVAL_SCRIPT_ELEMENT_NAME);
        for (var i = 0; i < elements.length; i++) {
            window.eval(elements[i].value);
        }

        ControlsBase.removeEvalScripts(elements);
    },

    removeEvalScripts: function(scriptElements) {
        var elements = scriptElements ? scriptElements : document.getElementsByName(ControlsBase.EVAL_SCRIPT_ELEMENT_NAME);

        // Remove evaluated elements so they newer will be evaluated twice.
        $A(elements).clone().each(function(elem) {
            $(elem).remove();
        })
    },
    */

    // params <string, string || array<string>>
    buildParams: function(params) {
        var paramsString = '';
        if (_.isObject(params) && !_.isEmpty(params)) {
            var and = function(uri) {
                if (uri.length > 0 && uri[uri.length - 1] != '&') {
                    uri += '&';
                }
                return uri;
            };
            _.each(params, function(paramValue, paramName) {
                paramsString = and(paramsString);
                if (_.isArray(paramValue)) {
                    _.each(paramValue, function(value) {
                        paramsString = and(paramsString);
                        paramsString += paramName + '=' + encodeURIComponent(value);
                    });
                } else {
                    paramsString += paramName + '=' + encodeURIComponent(paramValue);
                }
            });
        }
        return paramsString;
    },

    /**
     * Used for creating post data to replace old form functionality
     * @param selectedData Selected values of input controls,
     * if null or undefined, empty string will be returned.
     * @param extraParams Extra params in the same format as selectedData,
     * this parameter is optional.
     */
    buildSelectedDataUri:function (selectedData, extraParams) {
        var params = ControlsBase.buildParams(selectedData);
        if (_.isObject(extraParams) && !_.isEmpty(extraParams)) {
            var extraParamsString = ControlsBase.buildParams(extraParams);
            return params + '&' + extraParamsString;
        } else {
            return params;
        }
    }
};

//////////////////////////
//  Input Controls dialog
//////////////////////////
var ControlDialog = function(buttonActions) {
    var it = this;
    this._dom = $(ControlsBase.INPUT_CONTROLS_DIALOG);

    this.buttonActions = buttonActions;

//    if (isIPad()) {
//         var el = document.getElementById(ControlsBase.INPUT_CONTROLS_CONTAINER).parentNode;
//         new TouchController(el, el.parentNode);
//    }

    jQuery('#'+ControlsBase.INPUT_CONTROLS_DIALOG).on('mouseup touchend', 'button', function(evt) {
        if (this.id && !this.id.empty() && !this.disabled){
            var action = it.buttonActions['button#'+this.id];
            action && action();
        }
    });
};

// observe buttons
ControlDialog.addMethod("_dialogClickHandler", function(e) {
    var element = e.element();

    // observe buttons
    for (var pattern in this.buttonActions) {
        if (matchAny(element,  [pattern], true)) {
            this.buttonActions[pattern]();
            e.stop();
            return;
        }
    }
});

ControlDialog.addMethod("show", function() {
    dialogs.popup.show(this._dom);
});

ControlDialog.addMethod("hide", function() {
    dialogs.popup.hide(this._dom);
});


///////////////////////////////
//  Save Report Options dialog
///////////////////////////////
var OptionsDialog = function(buttonActions) {
    this._dom = $(ControlsBase.SAVE_REPORT_OPTIONS_DIALOG);
    this.input = this._dom.select('input#savedValuesName')[0];
    this.overwrite = false;

    this.buttonActions = buttonActions;
	this._dialogClickHandler = this._dialogClickHandler.bindAsEventListener(this);
    this._dom.observe('click', this._dialogClickHandler);
};

OptionsDialog.addMethod("remove", function() {
	this._dom.stopObserving('click', this._dialogClickHandler);
});

// observe buttons
OptionsDialog.addMethod("_dialogClickHandler", function(e) {
    var element = e.element();

    // observe buttons
    for (var pattern in this.buttonActions) {
        if (matchAny(element,  [pattern], true)) {
            this.buttonActions[pattern]();
            e.stop();
            return;
        }
    }
});

OptionsDialog.addMethod("show", function() {
    this.input.setValue(ControlsBase.DEFAULT_OPTION_TEXT);
    dialogs.popup.show(this._dom);
});

OptionsDialog.addMethod("hide", function() {
    dialogs.popup.hide(this._dom);
    this.hideWarning();
});

OptionsDialog.addMethod("showWarning", function(text){
    var warning = jQuery("#"+ControlsBase.SAVE_REPORT_OPTIONS_DIALOG).find(".warning");
    if (text){
        warning.text(text);
    }
    warning.show();
});

OptionsDialog.addMethod("hideWarning", function(){
    var warning = jQuery("#"+ControlsBase.SAVE_REPORT_OPTIONS_DIALOG).find(".warning");
    warning.text("");
    warning.hide();
});
