define(function(require) {

    var _ = require("underscore"),

        collectParentIdsFactory = require("./factory/collectParentIdsFactory");

    var defaultSeparator = "/";

    var TreeViewState = function() {
        this.initialize();
    };

    _.extend(TreeViewState.prototype, {

        initialize: function(options) {
            options = options || {};

            this.clear();

            this.collectParentIds = collectParentIdsFactory.create(options.escapeCharacter, defaultSeparator);
        },

        clear: function() {
            this._TOGGLED_LEVELS_LIST = [
                {
                    id: "/",
                    collapsed: false
                }
            ];

            this._RECENTLY_EXPENDED_LEVELS_LIST = {};
            this._SELECTION = {};
        },

        selectItems: function(items) {
            items = _.isArray(items) ? items : [items];

            _.each(items, function(item) {
                this._SELECTION[item] = true;
            }, this);
        },

        deselectItems: function(items) {
            items = _.isArray(items) ? items : [items];

            _.each(items, function(item) {
                this._SELECTION[item] = false;
            }, this);
        },

        resetSelection: function(selection) {
            selection = selection || [];

            this._SELECTION = {};

            _.each(selection, function(value) {
                this._SELECTION[value] = true;
            }, this);
        },

        getSelection: function() {
            return this._SELECTION;
        },

        getToggledLevels: function() {
            return _.map(this._TOGGLED_LEVELS_LIST, function(toggledLevel) {
                return toggledLevel;
            });
        },

        getExpandedLevels: function() {
            return _.filter(this._TOGGLED_LEVELS_LIST, function(toggledLevel) {
                var toggledLevelParentIds = this.collectParentIds(toggledLevel.id);

                if (!this._isOneOfParentLevelsCollapsed(toggledLevelParentIds)
                    && this.isExpanded(toggledLevel.id)) {
                        return toggledLevel;
                }

            }, this);
        },

        isSelected: function(id) {
            return this._SELECTION[id];
        },

        isExpanded: function(id) {
            var toggledLevel = this._findToggledLevelById(id);

            return toggledLevel && !toggledLevel.collapsed;
        },

        markLevelAsRecentlyExpanded: function(id) {
            this._RECENTLY_EXPENDED_LEVELS_LIST[id] = true;
        },

        removeRecentlyExpandedLevel: function(id) {
            delete this._RECENTLY_EXPENDED_LEVELS_LIST[id];
        },

        isLevelRecentlyExpanded: function(id) {
            return this._RECENTLY_EXPENDED_LEVELS_LIST[id];
        },

        addToggledLevel: function(id) {
            var toggleLevelNotFound = !this._findToggledLevelById(id);

            if (toggleLevelNotFound) {
                this._addToggledLevel(id);
            }
        },

        markToggledLevelExpanded: function(id) {
            var toggledLevel = this._findToggledLevelById(id);

            if (toggledLevel) {
                toggledLevel.collapsed = false;
            }
        },

        markToggledLevelCollapsed: function(id) {
            var toggledLevel = this._findToggledLevelById(id);

            if (toggledLevel) {
                toggledLevel.collapsed = true;
            }
        },

        // PRIVATE METHODS

        _isOneOfParentLevelsCollapsed: function(parentIds) {
            return _.find(parentIds, function(parentId) {
                return !this.isExpanded(parentId);
            }, this);
        },

        _addToggledLevel: function(id) {
            this._TOGGLED_LEVELS_LIST.push({
                id: id,
                collapsed: false
            });
        },

        _findToggledLevelById: function(id) {
            return _.find(this._TOGGLED_LEVELS_LIST, function(toggledLevel) {
                return toggledLevel.id === id;
            });
        }
    });

    return TreeViewState;
});