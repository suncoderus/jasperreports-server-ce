/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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

    var _ = require("underscore"),
        $ = require("jquery"),

        getLevelNestingFactory = require("./factory/getLevelNestingFactory"),
        coerceIdsToCommonLevelFactory = require("./factory/coerceIdsToCommonLevelFactory");

    var defaultSeparator = "/";

    function TreeDataConverter(options) {
        this.treeCache = options.treeCache;
        this.viewState = options.viewState;

        var escapeCharacter = options.escapeCharacter || "\\";

        this.getLevelNesting = getLevelNestingFactory.create(escapeCharacter, defaultSeparator);
        this.coerceIdsToCommonLevel = coerceIdsToCommonLevelFactory.create(escapeCharacter, defaultSeparator);
    }

    _.extend(TreeDataConverter.prototype, {
        // returns items which should be rendered in a list
        // @params data[Array] - fetched data for each level
        getListItems: function(data, options) {
            var result,
                dfd = new $.Deferred();

            // this will be sparse array
            result = this._convertTreeData(data);

            options.total = this.treeCache.getTotals();

            result = result.slice(options.offset, options.offset + options.limit);

            return dfd.resolve(result, options);
        },

        // ------- PRIVATE METHODS --------

        // convert tree data into list representation
        // entry parameter represents level data and level options
        _convertTreeData: function(data) {
            var converted = [];

            _.each(data, function(entry) {
                var levelId = entry.options.id,
                    levelGlobalIndex = this.treeCache.getGlobalIndex(levelId),
                    startIndex = levelGlobalIndex + entry.options.offset;

                _.each(entry.data, function(level, index) {
                    var levelClone = _.cloneDeep(level),
                        totalAboveNode;

                    this._setExpandedFlag(levelClone);

                    totalAboveNode = this._getTotalAboveNode(levelId, levelClone);

                    converted[startIndex + totalAboveNode + index] = levelClone;
                }, this);
            }, this);

            return converted;
        },

        _setExpandedFlag: function(levelClone) {
            if (this.viewState.isExpanded(levelClone.id)) {
                levelClone.expanded = true;
            }
        },

        _getTotalAboveNode: function(parentLevelId, level) {
            var self = this,
                expandedLevelsWithinParent = this.treeCache.getExpandedLevelsWithinGivenLevel(parentLevelId);

            // if position of a target (the one to be rendered) node is below some other expanded sibling node,
            // then add total of that sibling to target node (or totals of a few siblings)
            return _.reduce(expandedLevelsWithinParent, function(memo, expandedLevel) {
                var expandedLevelIndex,
                    expandedCommonLevelId = {firstId: expandedLevel.id},
                    expandedLevelNesting = self.getLevelNesting(expandedLevel.id),
                    currentNodeNesting = self.getLevelNesting(level.id);

                if (expandedLevel.id !== level.id) {

                    if (expandedLevelNesting > currentNodeNesting) {
                        expandedCommonLevelId = self.coerceIdsToCommonLevel(expandedLevel.id, level.id);
                    }

                    expandedLevelIndex = self.treeCache.getLevelIndex(expandedCommonLevelId.firstId);

                    if (expandedLevelIndex < level.index) {
                        memo += self.treeCache.getTotalsByLevelId(expandedLevel.id);
                    }
                }
                return memo;
            }, 0);
        }
    });

    return TreeDataConverter;
});