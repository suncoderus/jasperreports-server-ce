define(function(require) {

    var _ = require("underscore");

    return {
        invokeFetchFunctions: function(fetchFunctions) {
            return _.map(fetchFunctions, function(fn) {
                return fn();
            });
        }
    }
});