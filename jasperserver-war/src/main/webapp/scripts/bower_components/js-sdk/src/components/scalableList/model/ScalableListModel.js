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
 * @version: $Id: ScalableListModel.js 1772 2015-10-28 18:05:44Z inestere $
 */

/**
 * Base model for ScalableList component.
 *  - It holds not all data but only buffer which should be rendered.
 *  - It make decision when buffer should be reloaded,
 *  - It does actual data fetching from data provider
 *  - It triggers "change" event when buffer was refreshed.
 *
 * Model API:
 *      get("total")            -  returns total elements count in data set
 *      get("bufferStartIndex") -  returns index of first element in buffer
 *      get("bufferEndIndex")   -  returns index of last element in buffer
 *      fetch                   -  should decide whether fetching is necessary and fetch data from data provider if so.
 *                                 If fetch is necessary, after data was fetched it should update start and end indexes
 *                                 and trigger "change" event on this instance
 *      get("items")            -  returns array with model of items in buffer which should be rendered.
 *      on("change", function)  -  should bind passed function to "change" event so it will be called then then data buffer was updated
 *      off                     -  should unbind all functions from events.
 *
 * this Model implementation look for following options in hash provided to constructor:
 *      * getData               - is a function which is used to fetch data for list.
 *      fetch(options)            it return promise (object with done, fail methods).
 *                                getData should looks for optional parameter options.
 *                                options parameter is an optional parameter with following properties:
 *                                      offset  -   first item to fetch, 0 if falsy value
 *                                      limit   -   max amount of elements to fetch, length - 1 if falsy value.
 *
 *
 *      bufferSize     - size of buffer (size of items which should be rendered). Default is 100 items
 *      loadFactor     - value which define how close to buffer edges should be visible items to cause data fetching. Default is 0.95
 *
 *      * - required items.
 *
 */

define(function (require) {
    'use strict';

    var Backbone = require("backbone"),
        _ = require("underscore");

    // Model default values
    var DEFAULT_BUFFER_SIZE = 100; //default amount of elements in buffer
    var DEFAULT_LOAD_FACTOR = 0.95; //value which define how close to buffer edges should be visible items to cause data fetching

    var ScalableListModel = Backbone.Model.extend({

        /*
         Main init method
         */
        initialize: function(options) {
            _.bindAll(this, "_fetchComplete", "fetchFailed", "afterFetchComplete", "fetch");

            this.set({items: []}, {silent: true});
            this.getData = options.getData;
            this.bufferSize = options.bufferSize || DEFAULT_BUFFER_SIZE;
            this.loadFactor = options.loadFactor || DEFAULT_LOAD_FACTOR;
        },

        /* Internal helper methods */

        _fetchComplete: function(values) {
            this.attributes.total = values.total;
            this.set({items: values.data}, {silent:true});
            //we have to correct bufferEndIndex if we recieved
            //less data than we queried
            this.attributes.bufferEndIndex =
                Math.max(0, Math.min(this.attributes.bufferStartIndex + this.bufferSize - 1,
                    this.attributes.bufferStartIndex + values.data.length - 1));
            if (this.attributes.bufferEndIndex < this.attributes.bufferStartIndex + this.bufferSize - 1) {
                //we should not trust given total values from data provider
                this.attributes.total = Math.min(values.total, this.attributes.bufferEndIndex + 1);
            }

            this.afterFetchComplete && this.afterFetchComplete(values.data, this.attributes.total);

            this.trigger("change", this);
        },

        _isBufferReloadNecessary: function(topVisibleItem, bottomVisibleItem) {
            if (typeof this.get("bufferStartIndex") === "undefined" || typeof this.get("bufferEndIndex") === "undefined") {
                //No data fetching was ever done
                return true;
            }

            if ((topVisibleItem >= 0 && topVisibleItem < this.get("bufferStartIndex"))
                || (bottomVisibleItem < this.get("total") && bottomVisibleItem > this.get("bufferEndIndex"))) {
                //scroll is outside of a buffer
                return true;
            }

            //Special case: then topVisibleItem and bottomVisibleItem exactly matches buffer bounds
            //fetch should not be done.
            if (topVisibleItem === this.get("bufferStartIndex") && bottomVisibleItem === this.get("bufferEndIndex")) {
                return false;
            }

            //calculate whether data should be fetched based on load factor
            var topLoadFactor = 1 - (topVisibleItem - this.get("bufferStartIndex")) / this.bufferSize;
            var bottomLoadFactor = 1 - (this.get("bufferEndIndex") - bottomVisibleItem) / this.bufferSize;

            return (this.get("bufferStartIndex") > 0 && topLoadFactor >= this.loadFactor)
                || (this.get("bufferEndIndex") < this.get("total") && bottomLoadFactor >= this.loadFactor);
        },

        /* Methods which supposed to be overridden */

        afterFetchComplete: function(items, total) {

        },

        fetchFailed: function(responseStatus, error) {
            this.trigger("fetchFailed", responseStatus, error);
        },

        /*-------------------------
         * API
         -------------------------*/

        fetch: function(options) {
            options = _.extend({
                top: this.get("bufferStartIndex") || 0,
                bottom: this.get("bufferEndIndex") || this.bufferSize - 1
            }, options);

            //If we want to not keep buffer position on scroll - use reset method
            //to clear position
            if (options.force || this._isBufferReloadNecessary(options.top, options.bottom)) {

                if (options.top !== this.attributes.bufferStartIndex
                    || options.bottom !== this.attributes.bufferEndIndex || options.force) {

                    var bufferCenter = options.top + Math.floor((options.bottom - options.top) / 2);
                    var bufferHalf = Math.floor(this.bufferSize / 2);

                    this.attributes.bufferStartIndex = Math.max(0, bufferCenter - bufferHalf);

                    if (this.get("total") && !options.force) {
                        this.attributes.bufferEndIndex = Math.min(
                            this.get("total"), this.attributes.bufferStartIndex + this.bufferSize) - 1;
                    } else {
                        this.attributes.bufferEndIndex = this.attributes.bufferStartIndex + this.bufferSize - 1;
                    }
                }

                this.getData({
                    offset: this.get("bufferStartIndex"),
                    limit: this.get("bufferEndIndex") - this.get("bufferStartIndex") + 1
                }).done(this._fetchComplete).fail(this.fetchFailed);
            } else {
                this.afterFetchComplete && this.afterFetchComplete(this.get("items"), this.get("total"));
            }
        },

        reset: function(options) {
            this.attributes = {};
            this.set({items: []}, {silent: true});

            if (!options || !options.silent) {
                this.trigger("change", this);
            }
        }
    });

    return ScalableListModel;

});
