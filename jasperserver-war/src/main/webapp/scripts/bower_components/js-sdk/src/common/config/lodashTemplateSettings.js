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
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

define(function (require) {

    "use strict";

    var _ = require("lodash.custom"),
        xssUtil = require("common/util/xssUtil");


    _.str = require('underscore.string');

    //provide default templates
    _.templateSettings = {
        evaluate:/\{\{([\s\S]+?)\}\}/g,
        interpolate:/\{\{=([\s\S]+?)\}\}/g,
        escape:/\{\{-([\s\S]+?)\}\}/g
    };

    // Mix in non-conflict functions to Underscore namespace if you want
    _.mixin(_.str.exports());

    _.xssEscape = xssUtil.escape;

    var originalTemplate = _.template;

    _.template = function(text, data, options) {
        var settings = _.templateSettings,
            reNoMatch = /($^)/;

        text = String(text || '');
        options = _.defaults({}, options, settings);

        var reDelimiters = RegExp((options.escape || reNoMatch).source + '|$', 'g');

        text = text.replace(reDelimiters, '{{ print(_.xssEscape($1)); }}');

        return originalTemplate.call(_, text, data, options);
    };

    return _;
});