define(function(require) {

    var _ = require("underscore"),

        getLevelPrefixRegExp = require("./util/getLevelPrefixRegExp"),

        getParentIdFactory = require("./factory/getParentIdFactory"),
        getLevelNestingFactory = require("./factory/getLevelNestingFactory"),
        collectParentIdsFactory = require("./factory/collectParentIdsFactory"),
        coerceIdsToCommonLevelFactory = require("./factory/coerceIdsToCommonLevelFactory");

    var defaultSeparator = "/";

    var TreeCache = function(options) {
        this.initialize(options);
    };

    _.extend(TreeCache.prototype, {

        initialize: function(options) {
            options = options || {};

            this.getParentId = getParentIdFactory.create(options.escapeCharacter, defaultSeparator);
            this.getLevelNesting = getLevelNestingFactory.create(options.escapeCharacter, defaultSeparator);
            this.collectParentIds = collectParentIdsFactory.create(options.escapeCharacter, defaultSeparator);
            this.coerceIdsToCommonLevel = coerceIdsToCommonLevelFactory.create(options.escapeCharacter,
                defaultSeparator);

            this.clear();

            this.viewState = options.viewState;
        },

        clear: function() {
            this._GLOBAL_INDEXEX_CACHE = {
                "/": 0
            };

            this._TOTALS_CACHE = {};
            this._TOTALS_ON_LEVEL_CACHE = {};

            this._INDEXES_CACHE = {
                "/": 0
            };

            this._ITEMS_CACHE = {};
        },

        isNode: function(id) {
            var item = this.getItem(id);

            return item && item.node;
        },

        getItem: function(id) {
            return this._ITEMS_CACHE[id];
        },

        addItemToCache: function(id, item) {
            this._ITEMS_CACHE[id] = item;
        },

        removeItemFromCache: function(id) {
            delete this._ITEMS_CACHE[id];
        },

        getLevelIndex: function(id) {
            return this._INDEXES_CACHE[id];
        },

        setLevelIndex: function(id, index) {
            this._INDEXES_CACHE[id] = index;
        },

        getTotalsByLevelId: function(id) {
            return this._TOTALS_CACHE[id] || 0;
        },

        getTotals: function() {
            return _.reduce(this.viewState.getExpandedLevels(), function(memo, expandedLevel) {
                return memo + this.getTotalsByLevelId(expandedLevel.id);
            }, 0, this);
        },

        setTotals: function(id, totals) {
            this._TOTALS_CACHE[id] = totals;
        },

        getTotalsOnLevel: function(id) {
            return this._TOTALS_ON_LEVEL_CACHE[id] || 0;
        },

        setTotalsOnLevel: function(id, totalsOnLevel) {
            this._TOTALS_ON_LEVEL_CACHE[id] = totalsOnLevel;
        },

        getGlobalIndex: function(id) {
            return this._GLOBAL_INDEXEX_CACHE[id];
        },

        setGlobalIndex: function(id, globalIndex) {
            this._GLOBAL_INDEXEX_CACHE[id] = globalIndex;
        },

        getSummaryTotals: function(id) {
            return this.getTotalsByLevelId(id) + this.getTotalsOnLevel(id);
        },

        incrementTotalsOnExpandedLevels: function(id, delta) {
            this._updateTotalsOnExpandedLevels(id, delta);
        },

        decrementTotalsOnExpandedLevels: function(id, delta) {
            this._updateTotalsOnExpandedLevels(id, delta * -1);
        },

        incrementExpandedLevelsGlobalIndexes: function(id, delta) {
            this._updateExpandedLevelsGlobalIndexes(id, delta);
        },

        decrementExpandedLevelsGlobalIndexes: function(id, delta) {
            this._updateExpandedLevelsGlobalIndexes(id, delta * -1);
        },

        calculateGlobalIndex: function(id) {
            var levelIndex = this.getLevelIndex(id),
                levelGlobalIndex = this.getGlobalIndex(id),
                parentLevelGlobalIndex = this.getGlobalIndex(this.getParentId(id));

            if (!_.isUndefined(levelGlobalIndex)) {
                return levelGlobalIndex;
            }

            var totalAboveLevel = this._calculateTotalAboveLevel(id);

            return totalAboveLevel + levelIndex + parentLevelGlobalIndex;
        },

        getLevelsBelowToggled: function(id, levels) {
            var self = this,
                levelsBelowToggled = [],
                addedMap = {};

            iterateLevelsRelativeToToggled.call(this, id, levels,
                function(expandedLevel, expandedCommonLevelId, toggledCommonLevelId) {
                    var expandedLevelIndex = self.getLevelIndex(expandedCommonLevelId),
                        toggledLevelIndex = self.getLevelIndex(toggledCommonLevelId);

                    if (expandedLevelIndex > toggledLevelIndex) {
                        // TODO: not sure if this map is needed
                        !addedMap[expandedLevel.id] && levelsBelowToggled.push(expandedLevel.id);
                        addedMap[expandedLevel.id] = true;
                    }
                }
            );

            return levelsBelowToggled;
        },

        getLevelsAboveToggled: function(id, levels) {
            var self = this,
                addedMap = {},
                levelsAboveToggled = id !== "/" ? ["/"] : [];

            iterateLevelsRelativeToToggled.call(this, id, levels,
                function(expandedLevel, expandedCommonLevelId, toggledCommonLevelId) {
                    var expandedLevelIndex = self.getLevelIndex(expandedCommonLevelId),
                        toggledLevelIndex = self.getLevelIndex(toggledCommonLevelId);

                    if (expandedLevelIndex < toggledLevelIndex) {

                        !addedMap[expandedLevel.id] && levelsAboveToggled.push(expandedLevel.id);
                        addedMap[expandedLevel.id] = true;

                    // there is a case when levels are equals (we need collect parent level of toggled one too)
                    } else if ((expandedCommonLevelId === toggledCommonLevelId)
                        && (expandedCommonLevelId !== "/")
                        && (toggledCommonLevelId !== "/")
                        && (expandedCommonLevelId !== id)
                    ) {
                        !addedMap[expandedLevel.id] && levelsAboveToggled.push(expandedLevel.id);
                        addedMap[expandedLevel.id] = true;
                    }
                }
            );

            return levelsAboveToggled;
        },

        getExpandedLevelsWithinGivenLevel: function(id) {
            var levelGlobalIndex = this.getGlobalIndex(id),
                expandedLevelsWithinParent = [];

            if (!_.isUndefined(levelGlobalIndex)) {
                _.each(this.viewState.getExpandedLevels(), function (expandedLevel) {

                    var expandedLevelGlobalIndex = this.getGlobalIndex(expandedLevel.id);

                    if ((expandedLevelGlobalIndex > levelGlobalIndex)
                        && (expandedLevel.id !== id)
                        && (this.getLevelNesting(expandedLevel.id) > this.getLevelNesting(id))
                        && (expandedLevel.id.match(getLevelPrefixRegExp(id)))) {

                        expandedLevelsWithinParent.push(expandedLevel);
                    }
                }, this);
            }

            return expandedLevelsWithinParent;
        },

        // PRIVATE METHODS

        _updateExpandedLevelsGlobalIndexes: function(id, delta) {
            var levelsBelowToggled = this.getLevelsBelowToggled(id, this.viewState.getToggledLevels());

            _.each(levelsBelowToggled, function(id) {
                var currentGlobalIndex = this.getGlobalIndex(id);

                var newGlobalIndex = currentGlobalIndex
                    ? currentGlobalIndex + delta
                    : delta;

                this.setGlobalIndex(id, newGlobalIndex);
            }, this);
        },

        _updateTotalsOnExpandedLevels: function(id, delta) {
            var parents = this.collectParentIds(id);

            _.each(parents, function(parentId) {
                var currentTotalsOnLevel = this.getTotalsOnLevel(parentId);

                var newTotalsOnLevel = currentTotalsOnLevel
                    ? currentTotalsOnLevel + delta
                    : delta;

                this.setTotalsOnLevel(parentId, newTotalsOnLevel);
            }, this);
        },

        _calculateTotalAboveLevel: function(id) {
            var levelsAboveToggled,
                parents = this.collectParentIds(id),
                isLevelRightUnderRoot = parents.length === 1;

            var filter = function(levelId) { return levelId !== "/"; };

            if (isLevelRightUnderRoot) {
                levelsAboveToggled = this.getLevelsAboveToggled(id, this.viewState.getExpandedLevels());

                filter = function(levelId) {
                    return _.indexOf(parents, levelId) === -1;
                }
            } else {
                levelsAboveToggled = this.getLevelsAboveToggled(id,
                    this.getExpandedLevelsWithinGivenLevel(this.getParentId(id)));
            }

            return _.reduce(levelsAboveToggled, function(memo, levelId) {
                if (filter(levelId)) {
                    var total = this.getTotalsByLevelId(levelId) || 0;
                    return memo + total;
                }
                return memo;
            }, 1, this);
        }
    });

    function iterateLevelsRelativeToToggled(id, levels, callback) {
        var toggledLevelNesting = this.getLevelNesting(id);

        _.each(levels, function(level) {
            var commonLevelIds,
                expandedLevelId = level.id,
                expandedCommonLevelId = expandedLevelId,
                toggledCommonLevelId = id,
                expandedLevelNesting = this.getLevelNesting(expandedLevelId);

            if (expandedLevelNesting < toggledLevelNesting) {
                commonLevelIds = this.coerceIdsToCommonLevel(id, expandedLevelId);

                toggledCommonLevelId = commonLevelIds.firstId;
                expandedCommonLevelId = commonLevelIds.secondId;
            } else {
                commonLevelIds = this.coerceIdsToCommonLevel(expandedLevelId, id);

                expandedCommonLevelId = commonLevelIds.firstId;
                toggledCommonLevelId = commonLevelIds.secondId;
            }

            callback(level, expandedCommonLevelId, toggledCommonLevelId);
        }, this);
    }

    return TreeCache;

});