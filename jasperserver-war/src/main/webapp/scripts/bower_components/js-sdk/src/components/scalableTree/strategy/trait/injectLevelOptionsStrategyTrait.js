define(function(require) {

    var _ = require("underscore");

    return {
        injectLevelOptions: function(response, levelsOptions) {
            _.each(response, function(element, index) {
                element && element.push(levelsOptions[index]);
            });
        }
    }
});