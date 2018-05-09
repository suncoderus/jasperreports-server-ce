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
 * @author: Taras Bidyuk
 * @version: $Id$
 */

define(function(require) {
    var Marionette = require("backbone.marionette"),
        _ = require("underscore"),
        AttributesCollection = require("attributes/collection/AttributesCollection"),
        attributesDesignerFactory = require("attributes/factory/attributesDesignerFactory"),
        AttributesViewer = require("attributes/view/AttributesViewer");

    var AttributesViewFacade = Marionette.Controller.extend({

        initialize: function(options) {
            options = options || {};

            var designerViewOptions = _.extend({}, options, options.designer),
                viewerViewOptions = _.extend({}, options, options.viewer);

            this.designer = attributesDesignerFactory(designerViewOptions.type, designerViewOptions);
            this.viewer = new AttributesViewer(viewerViewOptions);

            this.listenTo(this.designer, "change", this._triggerChangeEvent);

            this.setCurrentView();
        },

        render: function(hideFilters) {
            this.getCurrentView().render(hideFilters);

            return this;
        },

        getCurrentView: function() {
            return this.currentView;
        },

        cancel: function() {
            return this.currentView.revertChanges();
        },

        containsUnsavedItems: function() {
            return this.currentView.containsUnsavedItems && this.currentView.containsUnsavedItems();
        },

        setCurrentView: function(currentView) {
            this.currentView = currentView || this.viewer;
        },

        toggleMode: function(mode, hideFilters) {
            this.getCurrentView().hide();
            this.setCurrentView(mode ? this.designer : this.viewer);
            this.render(hideFilters).getCurrentView().show();
        },

        _triggerChangeEvent: function() {
            this.trigger("change", this.containsUnsavedItems());
        }

    });

    return AttributesViewFacade;
});