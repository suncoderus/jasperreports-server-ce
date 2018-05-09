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

    var AttachableComponent = require("common/component/base/AttachableComponent"),
        $ = require("jquery"),
        _ = require("underscore");


    return AttachableComponent.extend(
        /** @lends ClickComponent.prototype */
        {

        /**
         * @constructor ClickComponent
         * @classdesc Component that is show when element is clicked
         * @extends AttachableComponent
         * @param {HTMLElement|jQuery} attachTo HTML DOM or jQuery object
         */
        constructor: function(attachTo){
            AttachableComponent.apply(this, arguments);

            _.bindAll(this, "_onElementClick", "_onDocumentMousedown");

            this.$attachTo.on("click", this._onElementClick);
            $("body").on("mousedown",this._onDocumentMousedown);
        },

        /**
         * @description on attached element click handler. Shows component.
         * @access protected
         */
        _onElementClick: function() {
            if (this.$attachTo.attr('disabled')) return;

            this.show();
        },

        /**
         * @description on document mouse down handler. Hides component.
         * @access protected
         */
        _onDocumentMousedown: function(e) {
            if (!$.contains(this.$el[0], e.target) && !this.$el.is(e.target) && !$.contains(this.$attachTo[0], e.target) && !this.$attachTo.is(e.target)) {
                this.hide();
            }
        },

        /**
         * @description removes component.
         */
        remove: function() {
            this.$attachTo.off("click", this._onElementClick);
            $("body").off("mousedown", this._onDocumentMousedown);
        }
    });
});