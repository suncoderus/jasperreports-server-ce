define(function(require) {

    var _ = require("underscore");

    var TreeController = function(options) {
        this.initialize(options);
    };

    _.extend(TreeController.prototype, {

        initialize: function(options) {
            this.viewState = options.viewState;
            this.treeCache = options.treeCache;
        },

        expand: function(items) {
            var viewState = this.viewState,
                treeCache = this.treeCache,
                globalIndex;

            items = _.isArray(items) ? items : [items];

            iterateThroughPresentItems(treeCache, items, function(id) {
                if (!viewState.isExpanded(id) && treeCache.isNode(id)) {
                    viewState.addToggledLevel(id);
                    viewState.markToggledLevelExpanded(id);

                    globalIndex = treeCache.calculateGlobalIndex(id);
                    treeCache.setGlobalIndex(id, globalIndex);

                    viewState.markLevelAsRecentlyExpanded(id);
                }
            });
        },

        collapse: function(items) {
            var viewState = this.viewState,
                treeCache = this.treeCache,
                summaryTotals;

            items = _.isArray(items) ? items : [items];

            iterateThroughPresentItems(treeCache, items, function(id) {
                if (viewState.isExpanded(id) && treeCache.isNode(id)) {
                    viewState.markToggledLevelCollapsed(id);

                    summaryTotals = treeCache.getSummaryTotals(id);

                    treeCache.decrementTotalsOnExpandedLevels(id, summaryTotals);
                    treeCache.decrementExpandedLevelsGlobalIndexes(id, summaryTotals);
                }
            });
        },

        select: function(items) {
            this.viewState.selectItems(items);
        },

        deselect: function(items) {
            this.viewState.deselectItems(items);
        },

        resetSelection: function(selection) {
            this.viewState.resetSelection(selection);
        },

        addItemToCache: function(item) {
            this.treeCache.setLevelIndex(item.id, item.index);
            this.treeCache.addItemToCache(item.id, item);
        },

        updateState: function(data, fetchedLevel) {
            var levelTotals = this.treeCache.getTotalsByLevelId(fetchedLevel.id);

            var shouldUpdateState = (levelTotals !== fetchedLevel.total)
                || this.viewState.isLevelRecentlyExpanded(fetchedLevel.id);

            if (shouldUpdateState) {

                var delta = this._calculateIndexesAndTotalsDeltaForStateUpdate(fetchedLevel),
                    shouldIncrement = this._shouldIncrementIndexesAndTotals(fetchedLevel);

                this.treeCache.setTotals(fetchedLevel.id, fetchedLevel.total);

                if (shouldIncrement) {
                    this.treeCache.incrementTotalsOnExpandedLevels(fetchedLevel.id, delta);
                    this.treeCache.incrementExpandedLevelsGlobalIndexes(fetchedLevel.id, delta);
                } else {
                    this.treeCache.decrementTotalsOnExpandedLevels(fetchedLevel.id, delta);
                    this.treeCache.decrementExpandedLevelsGlobalIndexes(fetchedLevel.id, delta);
                }
            }

            this.viewState.removeRecentlyExpandedLevel(fetchedLevel.id);
        },

        clearState: function() {
            this.viewState.clear();
            this.treeCache.clear();
        },

        // PRIVATE METHODS

        _calculateIndexesAndTotalsDeltaForStateUpdate: function(fetchedLevel) {
            var delta,
                levelTotals = this.treeCache.getTotalsByLevelId(fetchedLevel.id),
                totalsOnLevel = this.treeCache.getTotalsOnLevel(fetchedLevel.id);

            if (!_.isUndefined(levelTotals) && (levelTotals !== fetchedLevel.total)) {
                if (levelTotals > fetchedLevel.total) {
                    delta = levelTotals - fetchedLevel.total;
                } else {
                    delta = fetchedLevel.total - levelTotals;
                }
            } else {
                delta = fetchedLevel.total + totalsOnLevel;
            }

            return delta;
        },

        _shouldIncrementIndexesAndTotals: function(fetchedLevel) {
            var shouldIncrement = true,
                levelTotals = this.treeCache.getTotalsByLevelId(fetchedLevel.id);

            if (!_.isUndefined(levelTotals) && (levelTotals !== fetchedLevel.total)) {
                if (levelTotals > fetchedLevel.total) {
                    shouldIncrement = false;
                }
            }

            return shouldIncrement;
        }
    });

    function iterateThroughPresentItems(treeCache, items, callback) {
        _.each(items, function(id) {
            treeCache.getItem(id) && callback && callback(id);
        });
    }

    return TreeController;

});