define(function(require) {

    var _ = require("underscore");

    return {
        omitEmptyResponse: function(response) {
            return _.compact(response);
        }
    }

});