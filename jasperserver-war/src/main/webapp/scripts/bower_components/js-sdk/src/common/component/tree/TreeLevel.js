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
 * @author: Zakhar Tomchenko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        Panel = require('common/component/panel/Panel'),
        collapsiblePanelTrait = require('common/component/panel/trait/collapsiblePanelTrait'),
        List = require("common/component/list/view/ListWithSelection"),
        addToSelectionModelTrait = require("common/component/tree/trait/addToSelectionModelTrait"),
        ListWithSelectionModel = require("common/component/list/model/ListWithSelectionModel").extend(addToSelectionModelTrait),
        TooltipPlugin = require("common/component/tree/plugin/TooltipPlugin"),
        DataProviderWithSearchCache = require("./dataprovider/DataProviderWithSearchCache");

    return Panel.extend({
        constructor: function(options) {
            options || (options = {});

            _.extend(options, {
                traits: [ collapsiblePanelTrait ]
            });

            Panel.prototype.constructor.call(this, options);
        },

        initialize: function(options) {
            options || (options = {});

            this._isReady = false;
            this.item = options.item || {};
            this.id = this.item.id;
            this.owner = options.owner;
            this.parent = options.parent;

            this.itemsTemplate = options.itemsTemplate;
            this.listItemHeight = options.listItemHeight;
            this.levelHeight = options.levelHeight;
            this.lazyLoad =  !!options.lazyLoad;
            this.selection = options.selection || {allowed: false, multiple: false};
            this.resource = this.item.value;
            this.bufferSize = options.bufferSize;
            this.cache = typeof options.cache != "undefined" ? options.cache : false;
            this.allowMouseDownEventPropagation = options.allowMouseDownEventPropagation;

            this.items = {};
            this.plugins = [];

            var defaults = {el: this.el, model: this.model};

            for (var i = 0, l = options.plugins.length; i<l; i++){
                var plugin = options.plugins[i].constr;
                this.plugins.push(new plugin(_.extend({}, defaults, options.plugins[i].options)));
            }

            this.dataLayer = this.owner.getDataLayer(this);
            _.invoke(this.plugins, "dataLayerObtained", this.dataLayer);

            Panel.prototype.initialize.apply(this, arguments);
        },

        render: function() {
            var model = new ListWithSelectionModel({
                bufferSize: this.bufferSize,
                getData: this._getDataProvider(this.cache)
            });

            this.list = new List({
                markerClass: "." + this.cid,
                eventListenerPattern: "." + this.cid + ":not(.readonly) > p",
                el: this.$contentContainer,
                itemsTemplate: this.itemsTemplate,
                listItemHeight: this.listItemHeight,
                lazy: true,
                selection: this.selection,
                allowMouseDownEventPropagation: this.allowMouseDownEventPropagation,
                model: model
            });

            this.list.on("render:data", _.bind(listRendered, this));
            this.list.on("listRenderError", _.bind(listRenderError, this));
            this.list.on("selection:change", _.bind(itemsSelected, this));
            this.list.on("item:dblclick", _.bind(itemsDblClicked, this));

            if (this.lazyLoad){
                this.on("open", this._onOpen, this);
            } else {
                this.list.renderData();
            }

            return this;
        },

        _getDataProvider: function (cache) {
            var self = this,
                getData = _.bind(function(options) {
                    return this.obtainData(options, self);
                }, this.dataLayer),
                options = {};

            if (cache) {
                if (typeof cache === "object") {
                    options = _.extend(options, cache);
                }

                options.request = options.request || getData;
                this.dataProvider = new DataProviderWithSearchCache(options);
                getData = this.dataProvider.getData
            }

            return function (options) {
                options || (options = {
                    offset: 0,
                    limit: 100
                });
                options.id = self.id;

                return getData(_.extend({}, self.owner.context, options), self);
            };
        },

        _onOpen: function() {
            this.$el.removeClass(this.openClass).addClass(this.loadingClass);
            this.list.renderData();
        },

        setElement: function(el){
            var res = Panel.prototype.setElement.apply(this, arguments);

            if (this.list){
                this.list.setElement(this.$contentContainer);
                this.list.totalItems = -1;
            }

            _.each(this.plugins, function(plugin){
                 plugin.setElement(el);
            });

            this.lazyLoad || this.list && this.list.renderData();

            if(!this.collapsed) {
                this.open({silent: true, depth: 0});
                if(this.lazyLoad){
                    this.list.renderData();
                }
            }

            return res;
        },

        open: function(options){
            itemsIterator(this, Panel.prototype.open, options, options ? options.depth : 0);
            Panel.prototype.open.call(this, options);
        },

        close: function(options){
            Panel.prototype.close.call(this, options);
            itemsIterator(this, Panel.prototype.close, options, options ? options.depth : undefined);
            clearSelection(this);
        },

        getLevel: function(levelId) {
            return _(this.items)
                .reduce(function(memo, item) {
                    return memo || (item.id === levelId ? item : item.getLevel(levelId))
                }, false);
        },

        refresh: function(options){

            if(this.listRenderError) return;

            if ((this.lazyLoad && !this.collapsed) || !this.lazyLoad){
                this.once("ready", _.bind(itemsIterator, this, this, this.refresh, options, 1));

                this.list.model.fetch({top: 0, bottom: this.list.model.bufferSize, force: true});
                this.list.scrollTo(0);
            } else {
                this.list.model.set("bufferStartIndex", undefined, {silent: true});
                this.list.model.set("bufferEndIndex", undefined, {silent: true});
                this.list.model.set("total", undefined, {silent: true});

                itemsIterator(this, this.refresh, options);
            }
        },

        fetch: function(options, callback) {
            this.list.fetch(callback, options);
        },

        recalcConstraints: function() {
            this.list.resize();
        },

        fetchVisibleData: function() {
            this.list._fetchVisibleData();
        },

        resetSelection: function(options){
            clearSelection(this, options);
            (options && options.silent) || this.trigger("selection:change", []);
        },

        hasItems: function() {
            return !!this.$("> .subcontainer > .j-view-port-chunk > ul > li").length;
        },

        clearCache: function() {
            var self = this;

            _.each(this.items, function(value){
                value.clearCache();
            });

            this.dataProvider && this.dataProvider.clear();
        },

        remove: function() {
            var self = this,
                index = -1;

            _.forEach(_.keys(this.items), function(key){
                self.items[key].remove();
            });

            //clear cache if any
            this.dataProvider && this.dataProvider.clear();

            this.list.remove();

            if (this.parent) {
                var parentLevelId = this.parent.id,
                    dataLayer = this.owner.getDataLayer(parentLevelId);

                if (dataLayer && dataLayer.predefinedData && dataLayer.predefinedData[parentLevelId] && _.isArray(dataLayer.predefinedData[parentLevelId])) {
                    for (var i = 0; i < dataLayer.predefinedData[parentLevelId].length; i++) {
                        if (dataLayer.predefinedData[parentLevelId][i].id === this.id) {
                            index = i;
                            break;
                        }
                    }

                    if (index > -1) {
                        dataLayer.predefinedData[parentLevelId].splice(index, 1);
                        delete dataLayer.predefinedData[this.id];
                    }
                }

                delete this.parent.items[this.id];
            }

            Panel.prototype.remove.apply(this, arguments);
        },

        isReady: function() {
            return this._isReady;
        }
    });

    function listRendered(){
        this.$el.removeClass(this.loadingClass).addClass(this.openClass);
        _.invoke(this.plugins, "itemsRendered", this.list.model, this.list);

        var count = this.list.$("> .j-view-port-chunk > ul > li").addClass(this.cid).length,
            height = _.isFunction(this.levelHeight) ? this.levelHeight() : this.levelHeight;

        if (height && (count * this.listItemHeight) > height) {
            this.list.$el.css({ "height": height + "px", "overflow-y": "auto" });
        } else {
            this.list.$el.css({ "height": "auto", "overflow": "auto" });
        }

        this.list._calcViewPortHeight();

        this._isReady = true;
        this.trigger("ready", this);
    }

    function listRenderError(responseStatus, error){
        this.listRenderError = true;
        this.trigger('listRenderError', responseStatus, error, this);
    }

    function itemsSelected(items){
        var result = [];
        _.chain(items).keys(items).each(function(key){
            result.push(items[key]);
        });

        this.selection.multiple || clearSelection(this);

        this.trigger("selection:change", result, this, []);
    }

    function itemsDblClicked(items){
        var result = [];
        _.chain(items).keys(items).each(function(key){
            result.push(items[key]);
        });

        this.selection.multiple || clearSelection(this);

        this.trigger("item:dblclick", result);
    }

    function isLevelToExclude(level, levelsToExclude) {
        for(var i = 0; i < levelsToExclude.length; i++) {
            if(levelsToExclude[i] === level) return true;
        }
    }

    function itemsIterator(topLevel, handler, options, depth){
        if(options && options.exclude && _.isArray(options.exclude) && isLevelToExclude(topLevel, options.exclude)){
            return;
        }
        if (depth === undefined || depth > 0){
            _.chain(topLevel.items).keys().each(function(key){

                if(!(options && options.exclude && _.isArray(options.exclude)
                    && isLevelToExclude(topLevel.items[key], options.exclude))) {
                    handler.call(topLevel.items[key], options);
                }

                itemsIterator(topLevel.items[key], handler, options, depth === undefined ? depth : --depth);
            });
        }
    }

    function clearSelection(topLevel, options){
        options = options && {exclude: options.exclude};
        itemsIterator(topLevel, function(opts){
            this.list.clearSelection();
            this.list.model.selection = [];
        }, options);
    }
});
