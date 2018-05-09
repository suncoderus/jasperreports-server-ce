/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: Taras Bidyuk
 * @version: $Id$
 */

define(function(require) {

    var _ = require("underscore"),
        $ = require("jquery"),
        Backbone = require("backbone"),

        pathUtil = require("components/scalableTree/util/pathUtil"),

        nativeMultiSelectionTrait = require("components/scalableList/trait/nativeMultiSelectionTrait"),
        List = require("components/scalableList/view/ListWithSelection"),

        listModelFactory = require("./factory/listModelFactory"),
        getLevelNestingFactory = require("./factory/getLevelNestingFactory"),

        defaultItemsTemplate = require("text!./template/defaultItemsTemplate.htm"),
        paddingProcessor = require("./processor/paddingProcessor"),
        valueProcessor = require("./processor/valueProcessor"),
        selectionProcessor = require("./processor/selectionProcessor"),

        treeDataFetchStrategyEnum = require("./enum/treeDataFetchStrategyEnum"),
        treeDataFetchStrategyFactory = require("./factory/treeDataFetchStrategyFactory"),

        TreeDataParallelFetchStrategy = require("./strategy/TreeDataParallelFetchStrategy"),
        TreeDataSerialFetchStrategy = require("./strategy/TreeDataSerialFetchStrategy"),

        TreeDataConverter = require("./TreeDataConverter"),
        TreeViewState = require("./TreeViewState"),
        TreeStateController = require("./TreeStateController"),
        TreeCache = require("./TreeCache"),
        TreeListDataProvider = require("./TreeListDataProvider"),
        TreeLevelsToFetchProvider = require("./TreeLevelsToFetchProvider");

    var defaultSeparator = "/",
        defaultEscapeCharacter = "\\";

    var RESTORE_TREE_STATE_DEFAULT_BUFFER_SIZE = 5000;

    var eventsEnum = {
        SELECT: "select",
        EXPAND: "expand",
        COLLAPSE: "collapse",
        CLEAR_SELECTION: "clearSelection"
    };

    var pluginsLifeCyclePhaseEnum = {
        TREE_INITIALIZED: "treeInitialized",
        BEFORE_ITEMS_RENDERED: "beforeItemsRendered",
        ITEMS_RENDERED: "itemsRendered",
        REMOVE: "treeRemoved"
    };

    var defaultSelectors = {
        collapser : ".jr-jTreeCollapser"
    };

    // controller
    var ScalableTree = Backbone.View.extend({

        className: "scalableTree",

        initialize: function(options) {
            options = options || {};

            this._collapserWasClicked = false;

            this.fetchStrategy = options.fetchStrategy || treeDataFetchStrategyEnum.PARALLEL_FETCH_STRATEGY;
            this.lazy = options.lazy || false;
            this.processors = options.processors || [];
            this.nodePadding = _.isUndefined(options.nodePadding) ? 10 : options.nodePadding;
            this.itemsTemplate = options.itemsTemplate || defaultItemsTemplate;
            this.listItemHeight = options.listItemHeight;
            this.selection = options.selection || {};
            this.collapser = !_.isUndefined(options.collapser)
                ? options.collapser
                : defaultSelectors.collapser;

            this._initTreeViewState(options);
            this._initTreeCache(options);
            this._initTreeLevelsToFetchProvider(options);
            this._initTreeDataConverter(options);

            this._initTreeListDataProvider(options);
            this._initTreeStateController(options);

            this._initDefaultProcessors(options);

            this._initList(options);
            this._initEvents();
            this._initPlugins();

            if (!this.lazy) {
                this.fetch();
            }
        },

        // PUBLIC METHODS
        getItem: function(id) {
            return this.treeCache.getItem(id);
        },

        isExpanded: function(id) {
            return this.treeViewState.isExpanded(id);
        },

        getSelection: function() {
            var self = this,
                items = [];

            _.each(this.treeViewState.getSelection(), function(value, key) {
                var item = self.treeCache.getItem(key);
                item && items.push(item);
            });

            return items;
        },

        expand: function(items, options) {
            this.treeStateController.expand(items, options);
            this._triggerFetchWithEvent(eventsEnum.EXPAND, options, items);
        },

        expandPath: function (path) {
            var self = this,
                levelsToExpand = this._getLevelsToExpandByPath(path);

            levelsToExpand && this._expandLevels(levelsToExpand).done(function() {
                self.fetch();
            });
        },

        collapse: function(items, options) {
            this.treeStateController.collapse(items, options);
            this._triggerFetchWithEvent(eventsEnum.COLLAPSE, options, items);
        },

        select: function(items, options) {
            items = _.isArray(items) ? items : [items];

            options = options || {};

            this.clearSelection({silent: true});

            this.treeStateController.select(items);
            this.list.setValue(items, {
                silent: true
            });

            !options.silent && this.trigger(eventsEnum.SELECT, items);
        },

        clearSelection: function(options) {
            options = options || {};

            this.treeStateController.resetSelection();
            this.listModel.clearSelection();

            !options.silent && this.trigger(eventsEnum.CLEAR_SELECTION);
        },

        setElement: function() {
            Backbone.View.prototype.setElement.apply(this, arguments);
            this.list && this.list.setElement(this.$el);

            return this;
        },

        render: function() {
            return this;
        },

        remove: function() {
            runPluginsLifeCyclePhase.call(this, pluginsLifeCyclePhaseEnum.REMOVE);

            this.list.remove();

            this.processors = [];
            this.plugins = [];

            return Backbone.View.prototype.remove.apply(this, arguments);
        },

        fetch: function(options, callback) {
            options = options || {};

            var selection = [],
                levels,
                self = this,
                dfd = $.Deferred();

            if (options.clear) {
                levels = this.treeViewState.getExpandedLevels();

                if (options.keepSelection) {
                    selection = this._collectCurrentSelectedItemIds();
                }

                this.clearSelection({silent: true});
                this.treeStateController.clearState();

                if (options.keepExpanded) {
                    this._expandLevels(levels).done(function() {
                        self.treeStateController.select(selection);
                        dfd.resolve();
                    });
                } else {
                    self.treeStateController.select(selection);
                    dfd.resolve();
                }
            } else {
                dfd.resolve();
            }

            dfd.done(function() {
                self.list.fetch(callback, {keepPosition: true});
            });
        },

        // PRIVATE METHODS

        _getLevelsToExpandByPath: function (path) {
            path = path || "";

            var levelsToExpand,
                pathFragments = pathUtil.split(path, defaultEscapeCharacter, defaultSeparator)
                    .filter(function(val) {
                        return val;
                    });

            if (pathFragments.length > 0) {
                levelsToExpand = pathFragments.reduce(function (memo, fragment, index) {
                    var levelId,
                        prevLevel = memo[index - 1];

                    if (prevLevel) {
                        levelId = prevLevel.id + defaultSeparator + fragment;
                    } else {
                        levelId = defaultSeparator + fragment;
                    }

                    memo.push({id: levelId});

                    return memo;
                }, []);
            }

            return levelsToExpand;

        },

        _collectCurrentSelectedItemIds: function() {
            return _.map(this.treeViewState.getSelection(), function(value, key) {
                return key;
            });
        },

        _getFetchStrategy: function(options) {
            var self = this;

            return treeDataFetchStrategyFactory(this.fetchStrategy, {
                fetchFunction: options.getData,
                escapeCharacter: options.escapeCharacter,

                isLevelShouldBeFetched: function(id) {
                    return (id === "/") || self.treeCache.getItem(id);
                }
            });
        },

        _initTreeViewState: function(options) {
            this.treeViewState = options.treeViewState || new TreeViewState({
                escapeCharacter: options.escapeCharacter
            });
        },

        _initTreeCache: function(options) {
            this.treeCache = options.treeCache || new TreeCache({
                viewState: this.treeViewState,
                escapeCharacter: options.escapeCharacter
            });
        },

        _initTreeLevelsToFetchProvider: function(options) {
            this.treeLevelsToFetchProvider = new TreeLevelsToFetchProvider({
                viewState: this.treeViewState,
                treeCache: this.treeCache
            });
        },

        _initTreeDataConverter: function(options) {
            this.treeDataConverter = new TreeDataConverter({
                treeCache: this.treeCache,
                viewState: this.treeViewState,
                escapeCharacter: options.escapeCharacter
            });
        },

        _initTreeListDataProvider: function(options) {
            var self = this,
                fetchStrategy = this._getFetchStrategy(options);

            this.treeListDataProvider = options.treeListDataProvider || new TreeListDataProvider({
                processors: this.processors,
                itemIdGenerationStrategy: options.itemIdGenerationStrategy,
                escapeCharacter: options.escapeCharacter,

                fetchStrategy: fetchStrategy,
                treeDataConverter: this.treeDataConverter,

                processLevelItem: function(item) {
                    self.treeStateController.addItemToCache(item);
                },

                onLevelFetched: function(data, options) {
                    self.treeStateController.updateState(data, options);
                },

                treeLevelsToFetchProvider: this.treeLevelsToFetchProvider
            });
        },

        _initTreeStateController: function(options) {
            this.treeStateController = options.treeStateController || new TreeStateController({
                treeCache: this.treeCache,
                viewState: this.treeViewState
            });
        },

        _initList: function(options) {
            listModelFactory = options.listModelFactory || listModelFactory;

            this.listModel = listModelFactory.create({
                bufferSize: options.bufferSize,
                getData: _.bind(this.treeListDataProvider.getData, this.treeListDataProvider)
            });

            if (this.selection.multiple) {
                List = List.extend(nativeMultiSelectionTrait);
            }

            this.list = options.list || new List({
                el: this.$el,
                itemsTemplate: this.itemsTemplate,
                listItemHeight: this.listItemHeight,
                lazy: true,
                selection: this.selection,
                model: this.listModel,
                selectedClass: options.selectedClass
            });
        },

        _initEvents: function() {
            this.listenTo(this.list, "before:render:data", this._onBeforeRenderData);
            this.listenTo(this.list, "render:data", this._onRenderData);
            this.listenTo(this.list, "selection:change", this._onListSelectionChange);
            this.listenTo(this.list, "item:dblclick", this._onItemDblClick);
            this.listenTo(this.list, "list:item:contextmenu", this._onContextMenu);
            this.listenTo(this.list, "list:item:click", this._onClick);
            this.listenTo(this.list, "list:item:mouseover", this._onItemMouseOver);
            this.listenTo(this.list, "list:item:mouseout", this._onItemMouseOut);
        },

        _triggerFetchWithEvent: function(event, options) {
            options = options || {};

            var args = Array.prototype.slice.call(arguments, 2),
                self = this;

            !options.silent && this.fetch(options, function() {
                args.unshift(event);
                self.trigger.apply(self, args);
            });
        },

        _onBeforeRenderData: function() {
            this.trigger("before:render:items");
            runPluginsLifeCyclePhase.call(this, pluginsLifeCyclePhaseEnum.BEFORE_ITEMS_RENDERED);
        },

        _onRenderData: function() {
            // need to clear collapser flag in case it was set.
            this._collapserWasClicked = false;

            this.trigger("render:items");
            runPluginsLifeCyclePhase.call(this, pluginsLifeCyclePhaseEnum.ITEMS_RENDERED);
        },

        _initDefaultProcessors: function(options) {
            var overriddenPaddingProcessor = this._getOverriddenPaddingProcessor(options),
                overriddenSelectionProcessor = this._getOverriddenSelectionProcessor();

            this.processors.unshift(overriddenSelectionProcessor);
            this.processors.unshift(overriddenPaddingProcessor);
            this.processors.unshift(valueProcessor);
        },

        _getOverriddenPaddingProcessor: function(options) {
            var getLevelNesting = getLevelNestingFactory.create(options.escapeCharacter, defaultSeparator);

            return {
                processItem: _.partial(paddingProcessor.processItem, getLevelNesting, this.nodePadding)
            }
        },

        _getOverriddenSelectionProcessor: function() {
            return {
                processItem: _.partial(selectionProcessor.processItem,
                    this.treeViewState, this.treeStateController)
            }
        },

        _initPlugins: function() {
            this.plugins = [];

            _.each(this._plugins, function(pluginObj) {

                var plugin = pluginObj.constr;

                this.plugins.push(new plugin(_.extend({}, {
                    tree: this
                }, pluginObj.options)));

                runPluginsLifeCyclePhase.call(this, pluginsLifeCyclePhaseEnum.TREE_INITIALIZED);
            }, this);
        },

        _onCollapserClick: function(event) {
            // set collapser flag to avoid toggle in case of dblclick
            this._collapserWasClicked = true;

            var id = $(event.target).parents("li").first().data("id");
            id && this._toggleLevel(id);
        },

        _onListSelectionChange: function(selection) {
            this.treeStateController.resetSelection(selection);

            this.trigger("selection:change", this.getSelection());
        },

        _onItemDblClick: function(selection) {
            var id = selection && selection[0];
            // check collapser flag, if true do not trigger toggle
            if (id && !this._collapserWasClicked) {
                this._toggleLevel(id);
                this.trigger("item:dblclick", this.getSelection());
            }
        },

        _onContextMenu: function(item, event) {
            this.trigger("item:contextmenu", item, event);
        },

        _onItemMouseOver: function(item, event) {
            this.trigger("item:mouseover", item, event);
        },

        _onItemMouseOut: function(item, event) {
            this.trigger("item:mouseout", item, event);
        },

        _isCollapserEventTarget: function(event) {
            return $(event.target).is(this.collapser);
        },

        _onClick: function(item, event) {
            if (this._isCollapserEventTarget(event)) {
                this._onCollapserClick(event);
            }

            this.trigger("item:click", item, event);
        },

        _toggleLevel: function(id) {
            this.treeViewState.isExpanded(id)
                ? this.collapse(id)
                : this.expand(id);
        },

        _expandLevels: function(levels) {
            var self = this;

            var levelsOptions = this.treeListDataProvider.getLevelsOptions(levels, {
                limit: RESTORE_TREE_STATE_DEFAULT_BUFFER_SIZE
            });

            return this.treeListDataProvider.fetchTreeLevels(levelsOptions, function(data, fetchedLevel) {
                self.treeStateController.expand(fetchedLevel.id);
                self.treeStateController.updateState(data, fetchedLevel);
            });
        }
    }, {
        instance: function(options){
            return new this(options);
        }
    });

    return {
        use: function(plugin, options){
            return (function(constructor){
                return {
                    use: function(plugin, options){
                        constructor.prototype._plugins.push({
                            constr: plugin,
                            options: options
                        });
                        return this;
                    },
                    create: function(){
                        return constructor;
                    }
                }
            }(ScalableTree.extend({
                _plugins: [{
                    constr: plugin,
                    options:options
                }]
            })));
        },
        create: function(){
            return ScalableTree;
        },
        Selectors : defaultSelectors
    };

    function runPluginsLifeCyclePhase(pluginLifeCyclePhase) {
        _.each(this.plugins, function(plugin) {
            plugin[pluginLifeCyclePhase] && plugin[pluginLifeCyclePhase]();
        }, this);
    }
});
