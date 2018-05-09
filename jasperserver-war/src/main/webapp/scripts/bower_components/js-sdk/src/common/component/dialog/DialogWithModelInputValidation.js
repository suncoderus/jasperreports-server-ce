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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    // THIS CLASS IS DEPRECATED! DO NOT USE IT FOR NEW PROJECTS!!!
    // As alternative you should use Epoxy and epoxy binders to perform validation on model fields
    // You can find out how to use Epoxy at http://wiki.jaspersoft.com/display/FAF/Data-binding+with+Epoxy.js
    // Also for real world example you can check out Properties dialogs in bi-dashboard
    // (dashboard/view/designer/propertiesDialog/view/PropertiesDialogView) and its templates.

    var $ = require("jquery"),
        _ = require("underscore"),
        Dialog = require("./Dialog"),
        Validation = require('backbone.validation');

    return Dialog.extend({
        events: _.extend({
            "keyup input[type=text], textarea, select": "updateModelProperty",
            "change input[type=text], input:checkbox, textarea, select": "updateModelProperty"
        }, Dialog.prototype.events),

        initialize: function(){
            this._bindEvents();
            Dialog.prototype.initialize.apply(this, arguments);
        },

        _bindEvents: function(){
            this.on("resize", this._getBaseHeight);
        },

        _unbindEvents: function(){
            this.off("resize", this._getBaseHeight);
        },

        updateModelProperty: function(e){
            var update = {},
                target = $(e.target);

            if ((target[0].tagName.toLowerCase() === "input" && target.attr("type") === "text") ||
                (target[0].tagName.toLowerCase() === "select" && target.attr("multiple") !== "true") ||
                (target[0].tagName.toLowerCase() === "textarea")) {
                update[target.attr('name')] = $.trim(target.val());
            } else if (target[0].tagName.toLowerCase() === "input" && target.attr("type") === "checkbox") {
                update[target.attr('name')] = target.is(':checked');
            }

            this.beforeModelPropertySet && this.beforeModelPropertySet(update);

            this.model.set(update);
            this.model.validate(update);
        },

        bindValidation: function() {
            Validation.bind(this, {
                valid: this.fieldIsValid,
                invalid: this.fieldIsInvalid,
                forceUpdate: true,
                selector: "name"
            });
        },

        unbindValidation: function() {
            Validation.unbind(this);
        },

        fieldIsValid: function(view, attr, selector) {
            var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
            $parentEl.removeClass("error");
            $parentEl.find(".message.warning").text("");
            view._updateHeight();
        },

        fieldIsInvalid: function(view, attr, error, selector) {
            var $parentEl = view.$('[' + selector + '="' + attr + '"]').parent();
            $parentEl.addClass("error");
            $parentEl.find(".message.warning").text(error.toString());
            view._updateHeight();
        },

	    // simpler version of 'validField' which works always on the same
	    // context ('view' parameter is absent), and selector does not build from two parameters
	    validField: function(selector) {
		    var $parentEl = this.$(selector).parent();
		    $parentEl.removeClass("error");
		    $parentEl.find(".message.warning").text("");
	    },

	    // simpler version of 'fieldIsInvalid' which works always on the same
	    // context ('view' parameter is absent), and selector does not build from two parameters
	    // also, the order of parameters has changed
	    invalidField: function(selector, error) {
		    var $parentEl = this.$(selector).parent();
		    $parentEl.addClass("error");
		    $parentEl.find(".message.warning").text(error.toString());
	    },

	    clearValidationErrors: function() {
            this.$("label").removeClass("error");
            this.$(".message.warning").text("");
            this._updateHeight();
        },

        remove: function() {
            this.unbindValidation();
            this._unbindEvents();
            Dialog.prototype.remove.apply(this, arguments);
        },

        _updateHeight: function(){
            var modalHeight = this._getBaseHeight(this.$el.height());
            var heightErrorMsg = this._getAllErrorsHeight();

            if(heightErrorMsg !== 0){
                this.$el.height( heightErrorMsg + modalHeight );
            }
            else{
                this.$el.height( modalHeight );
            }
        },

        _getBaseHeight: function(height){
            var heightErrorMsg = this._getAllErrorsHeight();

            if(_.isNumber(height) && _.isUndefined(this._previousHeight)){
                this._previousHeight = height;
            }
            else if(_.isObject(height) && height.timeStamp){
                this._previousHeight = heightErrorMsg ? ( this.$el.height() - heightErrorMsg ) : this.$el.height();
            }

            return this._previousHeight;
        },

        _getAllErrorsHeight: function(){
            var heightErrorMsg = 0;
            this.$(".message.warning").each(function(){
                heightErrorMsg += $(this).height();
            });
            return heightErrorMsg;
        }
    });
});
