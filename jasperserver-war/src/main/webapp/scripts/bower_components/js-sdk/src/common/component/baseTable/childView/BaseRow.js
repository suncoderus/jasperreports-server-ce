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
    var _ = require("underscore"),
        Marionette = require("backbone.marionette"),
        epoxyViewMixin = require("common/view/mixin/epoxyViewMixin"),
        Tooltip = require("common/component/baseTable/behaviors/TooltipRowBehavior");


    var BaseRow = Marionette.ItemView.extend({

        behaviors: {
            Tooltip: {
                behaviorClass: Tooltip
            }
        },

        /**
         * Initializes view and generates an abstract of Epoxy.View methods for mixin with BaseRow.
         */
        initialize: function() {
            this.epoxifyView();
        },

        /**
         * Renders view and applies Epoxy bindings.
         * @returns {BaseRow}
         */
        render: function() {
            Marionette.ItemView.prototype.render.apply(this, arguments);

            this.applyEpoxyBindings();

            return this;
        },

        /**
         *  Removes view and Epoxy bindings.
         */
        remove: function() {
            this.removeEpoxyBindings();

            Marionette.ItemView.prototype.remove.apply(this, arguments);
        }

    });

    _.extend(BaseRow.prototype, epoxyViewMixin);

    return BaseRow;
});