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
 * @author: Zakhar Tomchenko
 * @version: $Id$
 */

define(function(require){
    "use strict";

    var TreePlugin = require("./TreePlugin"),
        _ = require("underscore"),
        Tooltip = require("common/component/tooltip/Tooltip");

    return TreePlugin.extend({
        initialize: function(options){
            this.list = options.tree.list;

            this.tooltip = Tooltip.attachTo(this.list.$el, options);

            this._initEvents();

            TreePlugin.prototype.initialize.apply(this, arguments);
        },

        _initEvents: function() {
            var self = this;

            this.listenTo(this.list, "list:item:mouseover", function(item){
                self.tooltip.show(item);
            });

            this.listenTo(this.list, "list:item:mouseout", function(){
                self.tooltip.hide();
            });
        },

        remove: function() {
            Tooltip.detachFrom(this.list.$el);

            this.tooltip.remove();

            TreePlugin.prototype.remove.apply(this, arguments);
        }
    });
});
