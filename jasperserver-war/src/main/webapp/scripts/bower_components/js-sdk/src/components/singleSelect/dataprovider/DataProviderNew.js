/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
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
 * @author: Sergey Prilukin
 * @version: $Id: ListWithSelection.js 43947 2014-04-02 17:51:07Z sergey.prilukin $
 */

/*
 * New kind of data provider which supports search,
 * and does caching so no new request call for same limit and offset will be done
 * Also it supports cache for several search terms.
 *
 * It works as a caching proxy.
 * It does requests by pages with 1000 elements by default
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        $ = require("jquery");

    var PAGE_SIZE = 999,
        SEARCH_CACHE_SIZE = 50,
        SERIAL_REQUESTS_DELAY = 50, //ms
        CRITERIA_PARAMETER_NAME = "criteria",
        MAX_VALUE = 2147483647;  //Equivalent to Java's Integer.MAX_VALUE

    var DataProvider = function(options) {
        _.bindAll(this, "getData");

        options = options || {};

        //Holds cached data
        this.dataCache = [];
        //holds total data count
        this.dataCacheTotal = null;
        //holds information about cached pages in format:
        //[true, undefined, true]
        //means that there are two cached pages: 0-999, 2000-2999
        this.dataCachePages = [];
        //size of the page
        this.pageSize = options.pageSize || PAGE_SIZE;
        //name of the parameter used for searching by criteria
        this.criteriaKey = options.searchKey || CRITERIA_PARAMETER_NAME;

        //max count of cached searches
        this.maxSearchCacheSize = options.maxSearchCacheSize || SEARCH_CACHE_SIZE;
        //holds info about data providers for specific searches
        this.getDataByCriteriaHolder = {};

        //whether to control when we need to get total
        this.controlGetTotal = typeof options.controlGetTotal !== "undefined" ? options.controlGetTotal : false;

            //optional dataconverter for data which cames through request
        this.dataConverter = options.dataConverter;

        //if true - last criteria will be saved untill it will be changed
        this.saveLastCriteria = typeof options.saveLastCriteria !== "undefined" ? options.saveLastCriteria : false;
        //saved criteria;
        this.lastCriteria = null;

        //Quick serial requests with same params will be delayed if > 0
        this.serialRequestsDelay = typeof options.serialRequestsDelay !== "undefined" ? options.serialRequestsDelay : SERIAL_REQUESTS_DELAY;

        //Object where promise for last sent request is stored until request will be done
        //uset to put all subsequent requests with same params to a queue
        this.serialRequestsDelayedPromise = null;

        //original data provider. by default it's AJAX request
        this.request = options.request;
    };

    _.extend(DataProvider.prototype, {


        /* API */

        getData: function(options) {
            options = options || {};
            if (options[this.criteriaKey]) {
                return this._getDataByCriteria(options[this.criteriaKey])(options);
            } else if (typeof options[this.criteriaKey] === "undefined" && this.saveLastCriteria && this.lastCriteria) {
                return this._getDataByCriteria(this.lastCriteria)(options);
            }

            //clear last criteria
            this.lastCriteria = null;

            var deferred = $.Deferred();

            var pagesToLoad = this._getPagesToLoad(options);

            if (!pagesToLoad) {
                this._resolveDeferredUsingDataFromCache(deferred, options);
            } else {
                this._requestData(options, pagesToLoad, deferred);
            }

            return deferred.promise();
        },

        /*
         * Clear all cached data
         */
        clear: function() {
            this.dataCache = [];
            this.dataCacheTotal = null;
            this.dataCachePages = [];
            this.getDataByCriteriaHolder = {};
            this.lastCriteria = null;
        },

        /* Internal methods */
        _requestData: function(options, pagesToLoad, deferred) {

            var limit = (pagesToLoad[pagesToLoad.length - 1] - pagesToLoad[0] + 1) * this.pageSize;

            var optionsToQuery = _.extend({}, options, {
                offset: pagesToLoad[0] * this.pageSize,
                limit: limit
            });

            if (this.controlGetTotal) {
                optionsToQuery.skipGetTotal = true;
            }

            //Fix limit because server doesn't accept very big value (security?)
            if (limit >= (MAX_VALUE / this.pageSize) * this.pageSize) {
                delete optionsToQuery.limit;
            }

            //a little optimization: do not request total second time
            if (this.controlGetTotal && typeof this.dataCacheTotal !== "number") {
                delete optionsToQuery.skipGetTotal
            }

            if (this.serialRequestsDelay > 0) {
                clearTimeout(this.deferredTimeout);

                this.deferredTimeout =
                    setTimeout(_.bind(this._requestDataDeferred, this, options, optionsToQuery, deferred), this.serialRequestsDelay);
            } else {
                this._requestDataDeferred(options, optionsToQuery, deferred);
            }
        },

        _requestDataDeferred: function(options, optionsToQuery, deferred) {
            var pagesToLoad = this._getPagesToLoad(options);

            if (!pagesToLoad) {
                //Data already in cache
                //no need to fetch it
                this._resolveDeferredUsingDataFromCache(deferred, options);
            } else {
                if (this.serialRequestsDelayedPromise && _.isEqual(optionsToQuery, this.serialRequestsDelayedPromise.query)) {
                    //if request with same query parameters were already called - do not init other request,
                    //just wait until original request ends
                    this.serialRequestsDelayedPromise.promise
                        .done(deferred.resolve).fail(deferred.reject);
                } else {
                    //Does actual request call and save promise and
                    //params for this request
                    this.serialRequestsDelayedPromise = {
                        promise: deferred.promise(),
                        query: optionsToQuery
                    };

                    this.request(optionsToQuery)
                        .done(_.bind(this._requestDone, this, options, optionsToQuery, deferred))
                        .fail(deferred.reject);
                }
            }
        },

        //Called when external request is done
        _requestDone: function(options, optionsToQuery, deferred, data) {
            this._putDataToCache(optionsToQuery, data);

            //clear promise holder since request is done
            this.serialRequestsDelayedPromise = null;

            this._resolveDeferredUsingDataFromCache(deferred, options);
        },

        _resolveDeferredUsingDataFromCache: function(deferred, options) {
            var dataToReturn = this._getDataFromCache(options);
            deferred.resolve({
                total: this.dataCacheTotal,
                data: dataToReturn
            });
        },

        _getPagesToLoad: function(options) {
            var firstLast = this._convertLimitOffsetToFirstLast(options);

            var startRange = this._findPageIndex(firstLast.first),
                endRange = this._findPageIndex(firstLast.last),
                indexes = [],
                i;

            //start page to load
            for (i = startRange; i <= endRange; i++) {
                if (!this.dataCachePages[i]) {
                    indexes.push(i);
                    break;
                }
            }

            //end page to load
            for (i = endRange; i >= startRange; i--) {
                if (!this.dataCachePages[i]) {
                    indexes.push(i);
                    break;
                }
            }

            return indexes.length > 0 ? indexes : null;
        },

        _findPageIndex: function(index) {
            return Math.floor(index / this.pageSize);
        },

        _convertLimitOffsetToFirstLast: function(options) {
            var first = options && options.offset ? options.offset : 0,
                last = options && options.limit
                    ? first + options.limit - 1
                    : (this.dataCacheTotal ? this.dataCacheTotal - 1 : MAX_VALUE);

            if (this.dataCacheTotal && last > this.dataCacheTotal - 1) {
                last = this.dataCacheTotal - 1;
            }

            return {
                first: first,
                last: last
            }
        },

        _getDataFromCache: function(options) {
            options = _.defaults(options, {
                offset: 0,
                limit: MAX_VALUE
            });

            var first = Math.max(0, options.offset),
                last = Math.min(first + options.limit, this.dataCacheTotal);

            return this.dataCache.slice(first, last);
        },

        _putDataToCache: function(options, result) {
            var i;

            if (typeof result.total !== "undefined") {
                this.dataCacheTotal = result.total;
            }

            options.limit = options.limit || MAX_VALUE;

            for (i = 0; i < result.data.length; i++) {
                this.dataCache[i + options.offset] = this.dataConverter
                    ? this.dataConverter(result.data[i])
                    : result.data[i];
            }

            var cachePageStart = options.offset / this.pageSize,
                cachePageEnd = Math.floor((Math.min(options.limit, result.data.length) + options.offset - 1) / this.pageSize);

            for (i = cachePageStart; i <= cachePageEnd; i++) {
                this.dataCachePages[i] = true;
            }
        },

        /* Working  with search by criteria */

        /*
         * We allow to cache maxSearchCacheSize criteria queries
         * otherwise old ones will be removed from the cache
         * @param criteria
         * @returns {*}
         * @private
         */
        _getDataByCriteria: function(criteria) {
            if (!this.getDataByCriteriaHolder[criteria]) {
                var firstCacheIndex = Number.MAX_VALUE,
                    lastCacheIndex = 0,
                    firstCacheCriteria,
                    cacheSize = 0;

                for (var key in this.getDataByCriteriaHolder) {
                    if (this.getDataByCriteriaHolder.hasOwnProperty(key)) {
                        cacheSize += 1;
                        var cachedCriteriaObj = this.getDataByCriteriaHolder[key];
                        if (cachedCriteriaObj.index < firstCacheIndex) {
                            firstCacheIndex = cachedCriteriaObj.index;
                            firstCacheCriteria = key;
                        }

                        if (cachedCriteriaObj.index > lastCacheIndex) {
                            lastCacheIndex = cachedCriteriaObj.index;
                        }
                    }
                }

                if (cacheSize >= this.maxSearchCacheSize) {
                    delete this.getDataByCriteriaHolder[firstCacheCriteria];
                }

                this.getDataByCriteriaHolder[criteria] = {
                    getData: this._createDataProviderForCriteria(criteria),
                    index: lastCacheIndex + 1
                };
            }

            this.lastCriteria = criteria;
            return this.getDataByCriteriaHolder[criteria].getData;
        },

        /*
         * Creates modified DataProvider where criteria will be always hardcoded.
         * This will alllows us to reuse single caching mechanism
         * and not duplicate code to query with criteria
         * @param criteria
         * @returns {DataProvider}
         * @private
         */
        _createDataProviderForCriteria: function(criteria) {
            var request = this.request,
                self = this;

            var dataProvider = new DataProvider({
                pageSize: this.pageSize,
                dataConverter: this.dataConverter,
                serialRequestsDelay: this.serialRequestsDelay,
                searchKey: this.criteriaKey,
                controlGetTotal: this.controlGetTotal,
                request: function(options) {
                    var params = _.extend({}, options);

                    params[self.criteriaKey] = criteria;
                    return request(params)
                }
            });

            var origGetData = dataProvider.getData;
            dataProvider.getData = function(options) {
                var optionsCopy = _.extend({}, options);
                delete optionsCopy[self.criteriaKey];

                return origGetData.call(dataProvider, optionsCopy);
            };

            return dataProvider.getData;
        }
    });

    return DataProvider;
});
