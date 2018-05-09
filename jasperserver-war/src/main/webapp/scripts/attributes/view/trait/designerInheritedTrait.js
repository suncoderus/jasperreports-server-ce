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

    var _ = require("underscore");

    return {
        _findOriginallyInheritedModelByName: function(models, name) {
            return _.find(models, function(model) {
                return model.isOriginallyInherited() && model.get("name") === name;
            });
        },

        _filterInheritedViews: function(data) {
            this.filteredInheriteds = this.collection.filterInheritedAttributes(data);
        },

        _findInheriteds: function(name) {
            return this._findModelsWhere({name: name, inherited: true});
        },

        _revertInheritedRemoval: function(name) {
            if (!this._findInheriteds(name)) {
                var inheritedModel = this._findOriginallyInheritedModelByName(_.union(this.changedModels, this.overriddenInheritedModels), name);
                inheritedModel && this.revertViewRemoval(inheritedModel);
            }
        },

        _searchForInherited: function(models) {
            var self = this;

            return this.collection.search(models)
                .done(function(data) {
                    if (data) {
                        self._filterInheritedViews(data);
                        self.collection.addItemsToCollection(self.filteredInheriteds);
                    }
                });
        },

        _removeInheritedView: function(model) {
            var inheritedModel = this._findInheriteds(model.get("name"));
            inheritedModel && this.removeView(inheritedModel);
        },

        _addInheritedView: function(model) {
            if (this.filteredInheriteds && this.filteredInheriteds.length) {
                model.resetField("id");
                this.collection.addItemsToCollection(this.filteredInheriteds);
            }
        }
    };

});