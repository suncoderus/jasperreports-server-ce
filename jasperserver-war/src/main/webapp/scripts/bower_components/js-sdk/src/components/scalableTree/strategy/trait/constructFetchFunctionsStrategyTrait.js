define(function(require) {

    var _ = require("underscore");

    return {
        constructFetchFunctions: function(fetchFunction, levelsOptions) {
            var fn,
                fetchFunctions = [];

            _.each(levelsOptions, function(levelOptions) {
                fn = _.partial(fetchFunction, levelOptions);
                fetchFunctions.push(fn);
            }, this);

            return fetchFunctions;
        }
    }
});