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

/* global Class, $break */

var ControlValidator = Class.create({
    initialize : function (id, controlToValidate, event, errorMessage, text) {
        this.id = id;
        this.controlToValidate = controlToValidate;
        this.event = event;
        this.text = text;
        this.errorMessage = errorMessage;

        this.eventEvaluation = false;

        this.dependencies = [];

        this.validationSummary = null;

        this.state = ControlValidator.State.UNKNOWN;

        if (!(controlToValidate) && !($(controlToValidate))) {
            throw new Error("ControlValidator: Cannot find control with id : " + this.controlToValidate);
        }


        if (event && event.length > 0) {
            var validatorInstance = this;

            $(controlToValidate).observe(event, function(e) {

                validatorInstance.evaluateIsValid(true);

                if (validatorInstance.isInvalid()) {

                    validatorInstance.highlight();

                    return false;
                }
            });
        }
    },

    getId : function() {

        return this.id;
    },

    isValid : function() {

        return this.state == ControlValidator.State.SUCCESS;
    },

    isInvalid : function() {

        return this.state == ControlValidator.State.UNSUCCESS;
    },

    isEventEvaluation : function() {

        return this.eventEvaluation;
    },

    getState : function() {

        return this.state;
    },

    setState : function(state) {

        this.state = state;
        this.refresh();
    },

    setDependencies : function(dependencies) {

        this.dependencies = dependencies;
    },

    getValue : function(id) {

        var control = $(id) ;

        if (!(control)) {
            throw new Error("ControlValidator.getValue: Cannot find control with id : " + this.controlToValidate);
        }

        if (control.tagName == "INPUT") {
            if (control.type == "text" || control.type == "password") {

                return control.value;
            } else {

                throw new Error("Input type=\"" + control.type + "\" is not supported.");
            }
        } else {

            throw new Error("Control " + control.tagName + " is not supported.");
        }
    },

    highlight : function() {

        var doSelect = function (id) {
            return function () {
                try {
                    var control = $(id) ;

                    control.focus();
                    control.select();
                } catch(ex) {}
            }
        }

        setTimeout(doSelect(this.controlToValidate), 50);
    },

    getErrorMessage : function() {
        return this.errorMessage;
    },

    refresh : function() {

        var component = $(this.id);

        if (component) {

            component.innerHTML = '';

            if (this.state == ControlValidator.State.UNSUCCESS) {
                component.style.visibility = "visible";

                if (this.text && this.text.length > 0) {

                    component.innerHTML = this.text;
                } else if (this.errorMessage && this.errorMessage.length > 0) {

                    component.innerHTML = this.errorMessage;
                }
            } else {

                component.style.visibility = "hidden";
            }
        }

        if (this.validationSummary) {

            this.validationSummary.refresh();
        }
    },

    setValidationSummary : function (validationSummary) {

        this.validationSummary = validationSummary;
    },

    evaluateIsValid : function() { /* Do Nothing*/ },

    onsuccess : function() { /* Do Nothing*/ },

    onunsuccess : function() { /* Do Nothing*/ }
});

ControlValidator.State = {
    UNSUCCESS : 0,
    SUCCESS : 1,
    IN_PROGRESS : 3,
    UNKNOWN : 4
}

var RequiredFieldValidator  = Class.create(ControlValidator, {

    evaluateIsValid : function(eventEvaluation) {

        this.eventEvaluation = (eventEvaluation);

        var value = this.getValue(this.controlToValidate);

        if (value.blank()) {

            this.setState(ControlValidator.State.UNSUCCESS);
            this.onunsuccess();
            return false;
        } else {

            this.setState(ControlValidator.State.SUCCESS);
            this.onsuccess();
            return true;
        }
    }

});

var RegularExpressionValidator  = Class.create(ControlValidator, {

    initialize : function ($super, id, validationExpression, controlToValidate, event, errorMessage, text) {

        $super(id, controlToValidate, event, errorMessage, text);

        if (!(validationExpression)) {
            throw new Error("Validation expression cannot be null.");
        }

        this.validationExpression = validationExpression;
    },

    evaluateIsValid : function(eventEvaluation) {

        this.eventEvaluation = eventEvaluation;

        var value = this.getValue(this.controlToValidate);

        var matches = this.validationExpression.exec(value);

        if ((matches == null && value.length === 0) || (matches != null && value == matches[0])) {

            this.setState(ControlValidator.State.SUCCESS);
            this.onsuccess();
            return true;
        } else {

            this.setState(ControlValidator.State.UNSUCCESS);
            this.onunsuccess();
            return false;
        }
    }

});

var CompareValidator  = Class.create(ControlValidator, {

    initialize : function ($super, id, operator, controlToValidate, event, controlToCompare, errorMessage, text, notValidateIfEmpty) {

        $super(id, controlToValidate, event, errorMessage, text);

        this.operator = operator;
        this.controlToCompare = controlToCompare;
        this.notValidateIfEmpty = (notValidateIfEmpty === undefined) ? true : notValidateIfEmpty;
        if (!(this.operator)) {

            throw new Error("Operator cannot be null.");
        }

        if (!(this.controlToCompare) || !$(controlToCompare)) {

            throw new Error("Control to compare cannot be null.");
        }

    },

    evaluateIsValid : function(eventEvaluation) {

        this.eventEvaluation = eventEvaluation;

        var valueToValidate = this.getValue(this.controlToValidate);
        var valueToCompare = this.getValue(this.controlToCompare);

        var isValid = true;

        if ((this.notValidateIfEmpty) ? !valueToValidate.blank() : true) {
            if (this.operator == CompareValidator.Operator.EQUALS) {

                isValid = (valueToValidate == valueToCompare);
            } else if (this.operator == CompareValidator.Operator.NOT_EQUALS) {

                isValid = (valueToValidate != valueToCompare);
            }
        }

        if (isValid) {

            this.setState(ControlValidator.State.SUCCESS);
            this.onsuccess();
            return true;
        } else {

            this.setState(ControlValidator.State.UNSUCCESS);
            this.onunsuccess();
            return false;
        }

    }

});

CompareValidator.Operator = {
    EQUALS : 1,
    NOT_EQUALS : 2
};

var ValidationSummary = Class.create({

    initialize : function (id) {

        this.id = id;

        this.validators = [];

        $(this.id).innerHTML = '<ul></ul>';
    },

    registrValidator : function (validator) {

        this.validators.push(validator);
        validator.setValidationSummary(this);
    },

    refresh : function () {

        var element = $(this.id);

        if (element) {

            var html = '<ul>';

            this.validators.each(function (v) {

                if (v.isInvalid()) {

                    html += "<li>" + v.getErrorMessage() + "</li>";
                }
            });

            html += "</ul>"

            element.innerHTML = html;
        }
    }
});

var ValidationStack = Class.create({

    initialize : function () {
        this.validators = [];
    },

    addValidator : function (validator) {

        var validationStackInstance = this;
        this.validators.push(validator);

        var len = this.validators.length;
        for (var i = 0; i  < len; i++) {

            var prevValidator = this.validators[i];
            var nextValidator = this.validators[i + 1];

            if (!prevValidator.mainSuccessHandler) {

                prevValidator.mainSuccessHandler = prevValidator.onsuccess;
            }

            if (!prevValidator.mainUnsuccessHandler) {

                prevValidator.mainUnsuccessHandler = prevValidator.onunsuccess;
            }

            var oldSuccessHandler = prevValidator.mainSuccessHandler;
            var oldUnsuccessHandler = prevValidator.mainUnsuccessHandler;

            if (nextValidator) {

                prevValidator.onsuccess = this._getEvalNextFn(prevValidator, nextValidator, oldSuccessHandler);
                prevValidator.onunsuccess = this._getEvalNextFn(prevValidator, nextValidator, oldUnsuccessHandler);
            } else {

                prevValidator.onsuccess = this._getSuccessFn(prevValidator, oldSuccessHandler);
                prevValidator.onunsuccess = this._getUnsuccessFn(prevValidator, oldUnsuccessHandler);
            }
        }
    },

    _getEvalNextFn : function (validator, nextValidator, oldHandler) {
        return function () {

            if (oldHandler) {
                oldHandler();
            }

            if (!validator.isEventEvaluation()) {

                nextValidator.evaluateIsValid();
            }
        }
    },

    _getSuccessFn : function (validator, oldHandler) {
        var validationStackInstance = this;

        return function () {

            if (oldHandler) {
                oldHandler();
            }

            if (!validator.isEventEvaluation()) {

                var event = (validationStackInstance.getState() == ControlValidator.State.SUCCESS)
                        ? validationStackInstance.onsuccess : validationStackInstance.onunsuccess;

                event();
            }
        }
    },

    _getUnsuccessFn : function (validator, oldHandler) {
        var validationStackInstance = this;

        return function () {

            if (oldHandler) {
                oldHandler();
            }

            if (!validator.isEventEvaluation()) {

                validationStackInstance.onunsuccess();
            }
        }
    },

    getState : function () {

        var result;

        var successValidators = [];
        var unsuccessValidators = [];

        this.validators.each(function (validator) {

            var isInProgress = (validator.getState() == ControlValidator.State.IN_PROGRESS);
            var isUnknown = (validator.getState() == ControlValidator.State.UNKNOWN);
            var isSuccess = (validator.getState() == ControlValidator.State.SUCCESS);
            var isUnuccess = (validator.getState() == ControlValidator.State.UNSUCCESS);

            if (isInProgress) {

                result = ControlValidator.State.IN_PROGRESS;
            } else if (isUnknown) {

                result = ControlValidator.State.UNKNOWN;
            } else if (isSuccess) {

                successValidators.push(validator);
            } else if (isUnuccess) {

                unsuccessValidators.push(validator);
            }
        });


        if (result) {

            return result;
        } else {

            return (successValidators.length == this.validators.length)
                    ? ControlValidator.State.SUCCESS
                    : ControlValidator.State.UNSUCCESS;
        }
    },

    evaluateIsValid : function () {

        var len = this.validators.length;
        if (len > 0) {

            this.validators[0].evaluateIsValid();
        }
    },

    hasUnuccess : function () {

        var hasUnuccess = false;

        this.validators.each(function (validator) {

            var isUnuccess = (validator.getState() == ControlValidator.State.UNSUCCESS);

            if (isUnuccess) {

                hasUnuccess = true;
                throw $break;
            }
        });

        return hasUnuccess;
    },

    onsuccess : function() { /* Do Nothing*/ },

    onunsuccess : function() { /* Do Nothing*/ }

});

var ValidationChain = Class.create(ValidationStack, {

    initialize : function (controlToValidate, event) {
        this.controlToValidate = controlToValidate;
        this.event = event;

        this.validators = [];

        if (!this.controlToValidate || !$(this.controlToValidate)) {

            throw new Error("Cannot find control with id : " + this.controlToValidate);
        }
    },

    addValidator : function (validator) {

        var validationChainInstance = this;

        if (this.controlToValidate != validator.controlToValidate) {

            throw new Error("Validation chain connot be created for different controls");
        } else {

            $(this.controlToValidate).observe(this.event, function () {

                validationChainInstance.validators[0].evaluateIsValid();
            });
        }

        this.validators.push(validator);

        var len = this.validators.length;
        for (var i = 0; i  < len; i++) {

            var prevValidator = this.validators[i];
            var nextValidator = this.validators[i + 1];

            if (!prevValidator.mainSuccessHandler) {

                prevValidator.mainSuccessHandler = prevValidator.onsuccess;
            }

            if (!prevValidator.mainUnsuccessHandler) {

                prevValidator.mainUnsuccessHandler = prevValidator.onunsuccess;
            }

            var oldSuccessHandler = prevValidator.mainSuccessHandler;
            var oldUnsuccessHandler = prevValidator.mainUnsuccessHandler;

            if (nextValidator) {

                var resetOther = function (index, oldHandler) {
                    return function (){

                        if (oldHandler) {
                            oldHandler();
                        }

//                        var vs = validationChainInstance.validators;
//                        var len = vs.length;
//
//                        for (var j = index + 1; j < len; j ++) {
//
//                            vs[j].setState(ControlValidator.State.UNKNOWN);
//                        }
//
                        validationChainInstance.onunsuccess();
                    }
                }(i, oldUnsuccessHandler);

                prevValidator.onsuccess = this._getEvalNextFn(prevValidator, nextValidator, oldSuccessHandler);
                prevValidator.onunsuccess = resetOther;
            } else {

                prevValidator.onsuccess = this._getSuccessFn(prevValidator, oldSuccessHandler);

                prevValidator.onunsuccess = this._getUnsuccessFn(prevValidator, oldUnsuccessHandler);

            }
        }
    }

});
