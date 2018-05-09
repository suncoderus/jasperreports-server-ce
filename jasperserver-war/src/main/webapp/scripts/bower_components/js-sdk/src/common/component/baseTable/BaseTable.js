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
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function(require) {

    var Marionette = require("backbone.marionette"),
        Tooltip = require("common/component/tooltip/Tooltip");

    /**
     * BaseTable component.
     */

    var BaseTable = Marionette.CompositeView.extend({
        /** @lends BaseTable.prototype */

        /**
         * @constructor BaseTable
         * @description Initializes component and tooltip.
         * @param {object} options - holds options for initializing component.
         *        The same options as for Marionette.CompositeView { @link http://marionettejs.com/docs/v2.3.1/marionette.compositeview.html }
         * @param {object} [options.tooltip] - object that contains i18n messages and template for tooltip.
         */

        initialize: function(options) {
            options = options || {};

            var tooltip = options.tooltip;

            if (tooltip && tooltip.template && tooltip.i18n) {
                this.tooltip = new Tooltip({
                    i18n: tooltip.i18n,
                    attachTo: this.$el,
                    contentTemplate: tooltip.template
                });
            }
        },

        /**
         * @method remove
         * @description Removes component and tooltip.
         */

        remove: function() {
            this.tooltip && this.tooltip.remove();

            Marionette.CompositeView.prototype.remove.apply(this, arguments);
        }
    });

    return BaseTable;
});