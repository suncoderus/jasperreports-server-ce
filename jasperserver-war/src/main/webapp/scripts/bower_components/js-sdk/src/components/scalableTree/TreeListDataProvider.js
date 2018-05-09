define(function(require) {

    var _ = require("underscore"),
        $ = require("jquery"),

        TreeItemIdAutoGenerationStrategy = require("./strategy/TreeItemIdAutoGenerationStrategy"),

        pathUtil = require("./util/pathUtil");

    var defaultSeparator = "/",
        escapeCharacter = "\\";

    var TreeDataProvider = function(options) {
        this.initialize(options);
    };

    _.extend(TreeDataProvider.prototype, {

        initialize: function(options) {
            options = options || {};

            this.fetchStrategy = options.fetchStrategy;

            this.treeLevelsToFetchProvider = options.treeLevelsToFetchProvider;

            this.onLevelFetched = options.onLevelFetched;
            this.processLevelItem = options.processLevelItem;

            this.itemIdGenerationStrategy = options.itemIdGenerationStrategy
                || new TreeItemIdAutoGenerationStrategy();

            this.escapeCharacter = options.escapeCharacter || escapeCharacter;

            this.processors = options.processors;

            this.treeDataConverter = options.treeDataConverter;
        },

        getData: function(options) {
            var self = this,
                dfd = new $.Deferred();

            var callback = this.onLevelFetched,
                levelsToFetch = this.treeLevelsToFetchProvider.getLevelsToFetch(options),
                levelsOptions = this.getLevelsOptions(levelsToFetch, options);

            this.fetchTreeLevels(levelsOptions, callback).then(function(data) {
                return self.treeDataConverter.getListItems(data, options);
            }).then(function(data, options) {

                data = invokeProcessors.call(self, data, options);

                dfd.resolve({
                    data: data,
                    total: options.total
                });
            });

            return dfd;
        },

        fetchTreeLevels: function(levelsOptions, callback) {
            var dfd = new $.Deferred(),
                self = this,
                levels = [];

            this.fetchStrategy.fetch(levelsOptions, function(response) {
                levels.push.apply(levels, response);
                self._onLevelsFetched(response, callback);
            }).done(function() {
                dfd.resolve(levels);
            });

            return dfd;
        },

        getLevelsOptions: function(levelsToFetch, options) {
            return _.map(levelsToFetch, function(level) {
                return {
                    id: level.id,
                    offset: level.offset || 0,
                    limit: options.limit
                }
            });
        },

        // PRIVATE METHODS

        _setIdGenerationStrategyInitialIndex: function(index) {
            this.itemIdGenerationStrategy.setInitialIndex
                && this.itemIdGenerationStrategy.setInitialIndex(index);
        },

        _onLevelsFetched: function(response, callback) {
            if (this.onLevelFetched) {
                _.each(response, function(responseEntry) {
                    this._processResponseData(responseEntry.data, responseEntry.options);
                    callback && callback(responseEntry.data, responseEntry.options);
                }, this);
            }
        },

        _processResponseData: function(data, options) {
            var index,
                dataLength = data.length;

            for (index = 0; index < dataLength; index++) {
                var item = data[index];

                this._setItemId(item, options);
                this._setItemIndex(item, index, options);

                this.processLevelItem && this.processLevelItem(item);
            }
        },

        _setItemId: function(item, fetchedLevel) {
            var parentLevelId = fetchedLevel.id,
                defaultItemId = this.itemIdGenerationStrategy.getId(item, parentLevelId);

            if (!item.id) {
                item.id = defaultItemId;
            }
        },

        _setItemIndex: function(item, index, options) {
            item.index = index + options.offset;
        }
    });


    function invokeProcessors(data, options) {
        var processedData, processedItem;

        _.each(this.processors, function(processor) {
            processedData = [];

            _.each(data, function(item, index) {
                processedItem = processor.processItem(item, options, index);
                if(!processedItem) {
                    throw new Error("Processor should return an item");
                } else {
                    processedData.push(processedItem);
                }
            }, this);

            data = processedData;
        }, this);

        return data;
    }

    return TreeDataProvider;
});
