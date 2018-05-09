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
 * @author: Igor Nesterenko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        sizerTemplate = require("text!./template/sizerTemplate.htm"),
        log = require("logger").register('Sizer');

    require("jquery-ui/jquery.ui.resizable");

    return Backbone.View.extend({

        template: _.template(sizerTemplate),

        el : function(){
            return this.template();
        },

        /**
         * @constructor Sizer
         * @param {string} [options.container=""] CSS selector for container to make it resizable
         * @param {string} [options.conent=""] CSS selector for UI representation of sizer. Optional
         */

        initialize: function (options) {

            this.container = options.container;

            this.sizerOptions = _.clone(options);

            delete this.sizerOptions.container;

            this.$container = $(this.container);

            this.render();

        },

        render: function(){

            _.defaults(this.sizerOptions, {
                minHeight: this.$container.height(),
                handles: {
                    s : this.$el
                }
            });

            this.$container.resizable(this.sizerOptions);

            return this;
        },

        show: function() {
            this.$el.removeClass("jr-isInvisible");
            return this;
        },
        hide: function() {
            this.$el.addClass("jr-isInvisible");
            return this;
        },

        updateMinMax: function(boundaries) {
            this.$container.resizable("option", "minHeight", boundaries.minHeight);
            this.$container.resizable("option", "maxHeight", boundaries.maxHeight);
        },

        /**
         * @description Remove component from DOM.
         */
        remove: function () {
            try {
                this.$container.resizable("destroy");
            }catch (err){
                //in some cases remove method called twice
                log.debug(err);
            }
            Backbone.View.prototype.remove.apply(this, arguments);
        }

    });

});
