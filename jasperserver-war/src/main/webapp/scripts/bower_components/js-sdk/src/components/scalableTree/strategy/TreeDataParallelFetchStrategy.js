define(function(require) {

    var $ = require("jquery"),
        _ = require("underscore"),

        wrapResponseStrategyTrait = require("./trait/wrapResponseStrategyTrait"),
        omitEmptyResponseStrategyTrait = require("./trait/omitEmptyResponseStrategyTrait"),
        injectLevelOptionsStrategyTrait = require("./trait/injectLevelOptionsStrategyTrait"),
        invokeFetchFunctionsStrategyTrait = require("./trait/invokeFetchFunctionsStrategyTrait"),
        processResponseStrategyTrait = require("./trait/processResponseStrategyTrait"),
        constructFetchFunctionsStrategyTrait = require("./trait/constructFetchFunctionsStrategyTrait");


    var TreeDataParallelFetchStrategy = function(options) {
        this.initialize(options);
    };

    _.extend(TreeDataParallelFetchStrategy.prototype,
        wrapResponseStrategyTrait,
        omitEmptyResponseStrategyTrait,
        injectLevelOptionsStrategyTrait,
        invokeFetchFunctionsStrategyTrait,
        processResponseStrategyTrait,
        constructFetchFunctionsStrategyTrait, {

        initialize: function(options) {
            options = options || {};
            this.fetchFunction = options.fetchFunction;
        },
        
        fetch: function(levelsOptions, callback) {
            var self = this,
                fetchFunctions = this.constructFetchFunctions(this.fetchFunction, levelsOptions);

            return $.when.apply($,
                this.invokeFetchFunctions(fetchFunctions)
            ).then(function() {
                var response = Array.prototype.slice.call(arguments, 0);

                response = self.wrapResponse(response);

                self.injectLevelOptions(response, levelsOptions);

                response = self.omitEmptyResponse(response);
                response = self.processResponse(response);

                callback && callback(response);
            });
        }
    });

    return TreeDataParallelFetchStrategy;

});