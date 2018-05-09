define(function(require) {

    var _ = require("underscore");

    return {
        wrapResponse: function(response) {
            response = _.isNumber(response[1]) ? [response] : response;
            return response;
        }
    }
});