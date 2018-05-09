define(function(require) {

    var _ = require("underscore");

    var privateProperties = ["pos"];

    var TreeLevelsToFetchProvider = function(options) {
        this.initialize(options);
    };

    _.extend(TreeLevelsToFetchProvider.prototype, {
        
        initialize: function(options) {
            this.viewState = options.viewState;
            this.treeCache = options.treeCache;
        },
        
        getLevelsToFetch: function(options) {
            var levelsToFetch = [],
                potentialVisibleLevels = this._getPotentialVisibleLevels(options);

            _.each(potentialVisibleLevels, function(level) {
                var lastAddedLevelForFetch = _.last(levelsToFetch),
                    offset;

                if (lastAddedLevelForFetch && options.offset) {
                    if (this._isLevelForFetchVisible(lastAddedLevelForFetch, options)) {
                        offset = this._calculateOffsetValueOnLevel(level, options);
                    }
                } else {
                    offset = this._calculateOffsetValueOnLevel(level, options);
                }

                if (offset >= 0) {
                    this._setLevelOffset(level, offset);
                    levelsToFetch.push(level);
                }
            }, this);

            return this._omitPrivateProperties(levelsToFetch).reverse();
        },

        // PRIVATE METHODS

        _calculateOffsetValueOnLevel: function(level, options) {
            var levelOffset = 0,
                levelGlobalIndex = this.treeCache.getGlobalIndex(level.id),
                totals = this.treeCache.getTotalsByLevelId(level.id);

            if (!totals || (levelGlobalIndex >= options.offset)) {
                return levelOffset;
            }

            var totalOnLevelAboveBuffer = this._calculateTotalOnLevelAboveBuffer(level, options);

            levelOffset = options.offset - totalOnLevelAboveBuffer - levelGlobalIndex;

            if (levelOffset < 0) {
                return 0;
            }

            return levelOffset;
        },

        _calculateTotalOnLevelAboveBuffer: function(level, options) {
            var treeCache = this.treeCache,
                buffer = options.offset + options.limit;

            // find totals of expanded nodes on current level
            return _.reduce(this.treeCache.getExpandedLevelsWithinGivenLevel(level.id),
                function (memo, expandedLevel) {
                    var expandedLevelGlobalIndex = treeCache.getGlobalIndex(expandedLevel.id),
                        expandedLevelTotalsOnLevel= treeCache.getTotalsOnLevel(expandedLevel.id),
                        expandedLevelTotals = treeCache.getTotalsByLevelId(expandedLevel.id);

                    if (expandedLevelGlobalIndex + expandedLevelTotalsOnLevel + expandedLevelTotals < buffer) {
                        return memo + expandedLevelTotals;
                    }
                    return memo + 0;
                }, 0);
        },

        _getPotentialVisibleLevels: function(options) {
            var treeCache = this.treeCache,
                potentialVisibleLevels = [],
                buffer = options.offset + options.limit;

            // reverse is required since root level always satisfies condition
            var expandedLevels = this.viewState.getExpandedLevels().reverse();

            for (var i = 0; i < expandedLevels.length; i++) {
                var level = expandedLevels[i],
                    potentialVisibleLevel = {},
                    totals = treeCache.getTotalsByLevelId(level.id),
                    levelGlobalIndex = treeCache.getGlobalIndex(level.id),
                    totalsOnLevel = treeCache.getTotalsOnLevel(level.id);

                potentialVisibleLevel.id = level.id;
                potentialVisibleLevel.pos = {
                    start: levelGlobalIndex,
                    end: levelGlobalIndex
                };

                if (!totals) {
                    this._addPotentialVisibleLevel(potentialVisibleLevels, potentialVisibleLevel);
                    continue;
                }

                potentialVisibleLevel.pos.end = totals + totalsOnLevel + levelGlobalIndex;

                if ((potentialVisibleLevel.pos.start <= options.offset)
                    && (levelGlobalIndex + potentialVisibleLevel.pos.end >= options.offset)) {
                    this._addPotentialVisibleLevel(potentialVisibleLevels, potentialVisibleLevel);
                }

                if ((potentialVisibleLevel.pos.start >= options.offset) && (potentialVisibleLevel.pos.start < buffer)) {
                    this._addPotentialVisibleLevel(potentialVisibleLevels, potentialVisibleLevel);
                }
            }

            return potentialVisibleLevels;
        },

        _addPotentialVisibleLevel: function(array, level) {
            !_.find(array, function(element) {
                return element.id === level.id
            }) && array.push(level);
        },

        _isLevelForFetchVisible: function(level, options) {
            var buffer = options.offset + options.limit,
                levelStartPosition = level.pos.start,
                levelEndPosition = level.pos.end;

            return (levelStartPosition > options.offset) || (levelEndPosition < buffer);
        },

        _setLevelOffset: function(level, offset) {
            level.offset = offset;
        },

        _omitPrivateProperties: function(levelsToFetch) {
            return _.map(levelsToFetch, function(level) {
                return _.omit(level, privateProperties);
            });
        }
        
    });

    return TreeLevelsToFetchProvider;
});