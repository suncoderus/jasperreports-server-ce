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

    var $ = require("jquery"),
        _ = require("underscore"),
        Backbone = require("backbone"),
        Marionette = require("backbone.marionette"),
        i18n = require("bundle!AttributesBundle"),
        epoxyViewMixin = require("common/view/mixin/epoxyViewMixin");

    var DEFAULT_FILTER = "defaultFilter",
        BOOLEAN_VALUES = ["true", "false"];

    var AttributesFilterCollectionView = Marionette.CompositeView.extend({
        className: "filtersContainer",

        events: {
            "change select": "_onCurrentFilterChange"
        },

        templateHelpers: function() {
            return {
                i18n: i18n
            };
        },

        constructor: function(options) {
            this._initCurrentCriteria();

            Marionette.CompositeView.apply(this, arguments);
        },

        initialize: function(options) {
            this.targetCollection = (options && options.targetCollection) || [];

            this.epoxifyView();

            this.initEvents();
        },

        initEvents: function() {
            this.listenTo(this.targetCollection, "add", this.addItemToFilterCollection);
            this.listenTo(this.targetCollection, "remove", this.removeItemFromFilterCollection);
            this.listenTo(this.targetCollection, "sync", this._initFilterCollection);
        },

        filter: function(criteria) {
            var currentCriteria = criteria || this.currentCriteria,
                filtered = currentCriteria.defaultFilter ?
                    this.filterCollection.models :
                    this.filterCollection.where(currentCriteria);

            this.targetCollection.reset(filtered);
        },

        reset: function() {
            if (!this.currentCriteria[DEFAULT_FILTER]) {
                this.$el.find("option[value='" + DEFAULT_FILTER + "::true']").prop("selected", true);
                this._initCurrentCriteria();
                this.filter();
            }
        },

        addItemToFilterCollection: function(model) {
            this.filterCollection.add(model);
        },

        removeItemFromFilterCollection: function(model) {
            this.filterCollection.remove(model);
        },

        render: function() {
            Marionette.CompositeView.prototype.render.apply(this, arguments);

            this.applyEpoxyBindings();
            this.delegateEvents(this.events);

            return this;
        },

        remove: function() {
            this.removeEpoxyBindings();

            Marionette.CompositeView.prototype.remove.apply(this, arguments);
        },

        _initCurrentCriteria: function(prop, val) {
            prop = prop || DEFAULT_FILTER;
            val = !_.isUndefined(val) ? val : true;

            this.currentCriteria = {};
            this.currentCriteria[prop] = val;
        },

        _initFilterCollection: function() {
            this.filterCollection = new Backbone.Collection(this.targetCollection.models);
        },

        _onCurrentFilterChange: function(e) {
            var splittedValue = $(e.target).val().split("::"),
                prop = splittedValue[0],
                val = splittedValue[1];

            if (_.indexOf(BOOLEAN_VALUES, val) !== -1) {
                this._initCurrentCriteria(prop, $.parseJSON(val));
                this.filter();
            }
        }
    });

    _.extend(AttributesFilterCollectionView.prototype, epoxyViewMixin);

    return AttributesFilterCollectionView;
});