define(function(require) {

    var treeDataFetchStrategyEnum = require("../enum/treeDataFetchStrategyEnum"),

        fetchStrategyMap = {};

    var TreeDataParallelFetchStrategy = require("../strategy/TreeDataParallelFetchStrategy"),
        TreeDataSerialFetchStrategy = require("../strategy/TreeDataSerialFetchStrategy");

    fetchStrategyMap[treeDataFetchStrategyEnum.PARALLEL_FETCH_STRATEGY] = TreeDataParallelFetchStrategy;
    fetchStrategyMap[treeDataFetchStrategyEnum.SERIAL_FETCH_STRATEGY] = TreeDataSerialFetchStrategy;

    return function(strategy, options) {
        var Strategy = fetchStrategyMap[strategy];

        if (Strategy) {
            return new Strategy(options);
        }
    }
});