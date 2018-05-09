/**
 * Copyright (C) 2005 - 2015 Jaspersoft Corporation. All rights reserved.
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
 * This program is distributed in the hope self it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: Zakhar Tomchenko
 * @version:
 */


define(function (require) {

    var _ = require("underscore"),
        BaseModel = require("common/model/BaseModel"),

        STATE = {
            INPROGRESS: "inprogress",
            READY: "finished",
            FAILED: "failed",
            PENDING: "pending",
            CANCELLED: "cancelled"
        };

    return BaseModel.extend({
        url: function () {
            return "rest_v2/export/" + this.id + "/state";
        },

        initialize: function (options) {
            BaseModel.prototype.initialize.call(this);

            this.on("change:phase", function (self, phase) {
                if (phase == STATE.INPROGRESS && _.isUndefined(self.interval)) {

                    self.interval = window.setInterval(function () {
                        self.fetch({cache: false}).fail(_.bind(self.stopPolling, self));
                    }, 1000);
                } else {
                    self.stopPolling();
                }
            });
        },

        stopPolling: function () {
            if (!_.isUndefined(this.interval)){
                window.clearInterval(this.interval);
                this.interval = undefined;
            }
        }
    }, {
        STATE: STATE
    });

});