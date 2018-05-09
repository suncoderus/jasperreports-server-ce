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

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        $ = require("jquery"),
        _ = require("underscore"),
        layoutModule = require("core.layout"),
        dynamicList = require("components.list"),
        featureDetection = require("common/util/featureDetection"),
        subDataSourceItemTemplate = require("text!dataSource/template/subDataSourceItemTemplate.htm");

   var Validation = require("backbone.validation");

    return Backbone.View.extend({
        events: {
            "keyup input[type='text']": "updateDataSourceId",
            "change input[type='text']": "updateDataSourceId"
        },

        initialize: function(options) {
            var templateData = this.model.toJSON();

            this._listItem = new dynamicList.UnderscoreTemplatedListItem({
                template: subDataSourceItemTemplate,
                cssClassName: layoutModule.LEAF_CLASS,
                value: templateData,
                tooltipText: this.model.get("uri")
            });

            Validation.bind(this, {
                valid: this.fieldIsValid,
                invalid: this.fieldIsInvalid,
                forceUpdate: true
            });
        },

        updateDataSourceId: function(e) {
            var $targetEl = this.$("input[type='text']"),
                valueObj = {};

            valueObj["id"] = $.trim($targetEl.val());

            this.model.set(valueObj);
            this.model.validate(valueObj);

            this._listItem.setValue(this.model.toJSON());
        },

        getListItem: function() {
            return this._listItem;
        },

        setRootElement: function() {
            this.setElement(this._listItem._getElement());
        },

        fieldIsValid: function(view, attr, selector) {
            var $parentEl = view.$("input[type='text']").parent();
            $parentEl.removeClass("error");
            $parentEl.find(".validatorMessageContainer").removeClass("error");
            $parentEl.find(".message.warning").text("");
        },

        fieldIsInvalid: function(view, attr, error, selector) {
            var $parentEl = view.$("input[type='text']").parent();
            $parentEl.addClass("error");
            $parentEl.find(".validatorMessageContainer").addClass("error");
            $parentEl.find(".message.warning").text(error);
        }
    });
});