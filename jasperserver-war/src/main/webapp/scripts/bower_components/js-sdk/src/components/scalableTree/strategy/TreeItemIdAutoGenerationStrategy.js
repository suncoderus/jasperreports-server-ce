define(function(require) {

    var _ = require("underscore"),
        idPrefix = "id";

    var defaultSeparator = "/";

    var TreeItemIdAutoGenerationStrategy = function(options) {
        options = options || {};

        this.idPrefix = options.idPrefix || idPrefix;
        this.initialIndex = _.isNumber(options.index) ? options.index : 0;

        this.reset();
    };

    _.extend(TreeItemIdAutoGenerationStrategy.prototype, {
        getId: function(item, parentLevelId) {
            var uriFragment = this.idPrefix + this.index++;

            return _.isEqual(parentLevelId, defaultSeparator)
                ? parentLevelId + uriFragment
                : parentLevelId + defaultSeparator + uriFragment;
        },

        setInitialIndex: function(index) {
            this.initialIndex = index;

            this.reset();
        },

        reset: function() {
            this.index = this.initialIndex;
        }
    });

    return TreeItemIdAutoGenerationStrategy;

});
