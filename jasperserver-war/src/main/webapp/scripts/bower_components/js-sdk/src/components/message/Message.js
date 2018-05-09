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

define(function (require) {
    "use strict";

    var $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        messageTemplate = require("text!./template/messageTemplate.htm"),
        MessageTypes = require("./enums/messageTypes");

    var MessageModel = Backbone.Model.extend({
        defaults: {
            visible: true,
            icon: false,
            title: "Title",
            text: "Text",
            type: MessageTypes.Type.Info
        }
    });

    return Backbone.View.extend({

        template: _.template(messageTemplate),

        initialize: function (options) {
            this.model = new MessageModel(options);

            this.listenTo(this.model, "change", this.render);

            this.render();
        },

        render: function() {
            this.$el.html(this.template(this.model.toJSON()));
            return this;
        }
    },
        MessageTypes
    );
});
