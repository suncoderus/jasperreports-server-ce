define(function(require) {

    var $ = require("jquery"),
        _ = require("underscore"),

        getParentIdFactory = require("../factory/getParentIdFactory"),

        wrapResponseStrategyTrait = require("./trait/wrapResponseStrategyTrait"),
        omitEmptyResponseStrategyTrait = require("./trait/omitEmptyResponseStrategyTrait"),
        injectLevelOptionsStrategyTrait = require("./trait/injectLevelOptionsStrategyTrait"),
        invokeFetchFunctionsStrategyTrait = require("./trait/invokeFetchFunctionsStrategyTrait"),
        processResponseStrategyTrait = require("./trait/processResponseStrategyTrait"),
        constructFetchFunctionsStrategyTrait = require("./trait/constructFetchFunctionsStrategyTrait");

    var defaultEscapeCharacter = "\\",
        defaultSeparator = "/";

    var TreeDataSerialFetchStrategy = function(options) {
        this.initialize(options);
    };

    _.extend(TreeDataSerialFetchStrategy.prototype,
        wrapResponseStrategyTrait,
        omitEmptyResponseStrategyTrait,
        injectLevelOptionsStrategyTrait,
        invokeFetchFunctionsStrategyTrait,
        constructFetchFunctionsStrategyTrait,
        processResponseStrategyTrait, {

        initialize: function(options) {
            options = options || {};

            var escapeCharacter = options.escapeCharacter || defaultEscapeCharacter,
                separator = options.separator || defaultSeparator;

            this.getParentId = getParentIdFactory.create(escapeCharacter, separator);

            this.fetchFunction = options.fetchFunction;
            this.isLevelShouldBeFetched = options.isLevelShouldBeFetched || function() {return true};
        },

        fetch: function(levelsOptions, callback) {
            var dfd = new $.Deferred(),
                self = this,
                fetchFunctionsGroups = this._constructFetchFunctionsGroups(levelsOptions);

            this._invokeFetchFunctionsGroups(fetchFunctionsGroups, function(response, options) {

                response = self.wrapResponse(response);

                self.injectLevelOptions(response, options);

                response = self.omitEmptyResponse(response);
                response = self.processResponse(response);

                callback && callback(response);
            }).done(function() {
                dfd.resolve();
            });

            return dfd;
        },

        _constructFetchFunctionsGroups: function(levelsOptions) {
            var fetchFunctionsGroups = [],
                fetchFunctions = this.constructFetchFunctions(this.fetchFunction, levelsOptions);

            _.each(levelsOptions, function(levelOptions) {
                var id = levelOptions.id,
                    fetchFunctionsGroup = this._getFetchFunctionsGroup(id, fetchFunctions, levelsOptions);

                fetchFunctionsGroups.push(fetchFunctionsGroup);
            }, this);

            return fetchFunctionsGroups;
        },

        _getFetchFunctionsGroup: function(id, fetchFunctions, levelsOptions) {
            var length = levelsOptions.length,
                fetchFunctionsOptions = [],
                fetchFunctionsGroup = [];

            for (var index = 0; index < length; index++) {
                var levelOptions = levelsOptions[index],
                    parentId = this.getParentId(levelOptions.id);

                if (id === parentId) {
                    fetchFunctionsGroup.push(fetchFunctions[index]);
                    fetchFunctionsOptions.push(levelOptions);
                }
            }

            return {
                functions: fetchFunctionsGroup,
                options: fetchFunctionsOptions
            };
        },

        _invokeFetchFunctionsGroups: function(groupedFetchFunctions, callback, index, dfd) {
            var self = this;

            dfd = dfd || new $.Deferred();
            index = index || 0;

            if (index >= groupedFetchFunctions.length) {
                dfd.resolve();
                return;
            }

            var fetchFunctionsGroup = groupedFetchFunctions[index];
            fetchFunctionsGroup = this._filterFetchFunctionsGroup(fetchFunctionsGroup);

            if (fetchFunctionsGroup.functions.length) {
                $.when.apply($,
                    this.invokeFetchFunctions(fetchFunctionsGroup.functions)
                ).then(function() {
                    var response = Array.prototype.slice.call(arguments, 0);

                    callback && callback(response, fetchFunctionsGroup.options);

                    index += 1;
                    self._invokeFetchFunctionsGroups(groupedFetchFunctions, callback, index, dfd);
                });
            } else {
                index += 1;
                self._invokeFetchFunctionsGroups(groupedFetchFunctions, callback, index, dfd);
            }

            return dfd;
        },

        _filterFetchFunctionsGroup: function(fetchFunctionsGroup) {
            var filteredFetchFunctionsGroup = {
                functions: [],
                options: []
            };

            _.each(fetchFunctionsGroup.options, function(level, index) {
                if (this.isLevelShouldBeFetched(level.id)) {
                    var fn = fetchFunctionsGroup.functions[index];

                    filteredFetchFunctionsGroup.functions.push(fn);
                    filteredFetchFunctionsGroup.options.push(level);
                }
            }, this);

            return filteredFetchFunctionsGroup;
        }
    });

    return TreeDataSerialFetchStrategy;
});