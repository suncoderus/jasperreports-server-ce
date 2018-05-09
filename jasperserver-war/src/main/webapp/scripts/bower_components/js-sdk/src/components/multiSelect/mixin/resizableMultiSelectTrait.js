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
 * @author Sergey Prilukin; modified by Ken Penn
 * @version: $Id: resizableMultiSelectTrait.js 1153 2015-04-24 15:21:10Z spriluki $
 */

/**
 * AvailableItems list -  Part of Tabbed MultiSelect
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        $ = require("jquery"),
        MultiSelect = require("../view/MultiSelect");

    require("jquery-ui/jquery.ui.resizable");

    var DEFAULT_VISIBLE_ITEMS_COUNT = 10,
        DEFAULT_MIN_ITEMS_COUNT = 3;

    return {

        _checkDataSize: function () {

            var options = this.resizableOptions,
                sizer = options.sizer,
                el = options.el,
                defaultItemsCount = options.defaultItemsCount,
                total = this.availableItemsList.listViewModel.get("total");

            if (this.resized) {
                sizer.removeClass('hidden');
                return;
            }

            if (total <= options.defaultItemsCount) {
                sizer.addClass('hidden');
                el.css("height", this.calcHeightByItemsCount(total) + "px");
            } else {
                sizer.removeClass('hidden');
                el.css("height", this.calcHeightByItemsCount(defaultItemsCount) + "px");
            }
        },

        /* API */

        makeResizable: function (options) {
            options = options || {};

            if (!options.el || !options.sizer) {
                throw "resizableMultiSelectTrait expect el and sizable to be defined";
            }

            _.defaults(options, {
                defaultItemsCount: DEFAULT_VISIBLE_ITEMS_COUNT,
                minItemsCount: DEFAULT_MIN_ITEMS_COUNT,
                sizerOptions: {}
            });

            this.resizableOptions = options;
            options.el = $(options.el);
            options.sizer = $(options.sizer);

            var resizable = options.el,
                sizer = options.sizer,
                sizerOptions = options.sizerOptions,
                minItemsCount = options.minItemsCount,
                defaultItemsCount = Math.max(options.defaultItemsCount, minItemsCount),
                height = this.calcHeightByItemsCount(defaultItemsCount);

            _.defaults(sizerOptions, {
                handles: {
                    's': sizer
                },
                minHeight: this.calcHeightByItemsCount(minItemsCount),
                stop: _.bind(function () {
                    this.resize();
                    this.resized = true;
                }, this)
            });

            sizer.addClass(options.sizerClass);

            resizable
                .css("height", height + "px")
                .resizable(sizerOptions);

            this.listenTo(this.availableItemsList.model, "change:totalValues", this._checkDataSize, this);

            return this;
        },

        calcHeightByItemsCount: function (items) {
            var itemHeight = this.availableItemsList.listView.itemHeight;

            return items * itemHeight + this.emptyContainerHeight;
        },

        destroyResizable: function () {
            try {
                this.resizableOptions && this.resizableOptions.el.resizable("destroy");
            } catch (e) {
            }
        },

        enableResizable: function () {
            try {
                this.resizableOptions && this.resizableOptions.el.resizable("disable");
            } catch (e) {
            }
        },

        disableResizable: function () {
            try {
                this.resizableOptions && this.resizableOptions.el.resizable("enable");
            } catch (e) {
            }
        },

        remove: function () {
            this.destroyResizable();
            MultiSelect.prototype.remove.call(this);
        }
    };
});

