define(function(require) {

    var _ = require("underscore");

    return {
        processResponse: function(response) {
            return _.map(response, function(responseEntry) {
                var data = responseEntry[0],
                    total = responseEntry[1],
                    options = responseEntry[2];

                responseEntry = {
                    data: data,
                    options: _.extend({}, options, {
                        total: total
                    })
                };

                return responseEntry;
            });
        }
    }
});