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
 * @author: Igor Nesterenko
 * @version: $Id$
 *
 * Generic functionality for our models
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        Backbone = require("backbone"),
        httpStatusCodes = require("common/enum/httpStatusCodes"),
        errors = require("common/enum/errorCodes");


    var BaseModel =  Backbone.Model.extend({

        /*
         *  Provide unified interface to work with server's errors
         */
        initialize : function(){
            this.on("error", BaseModel.unifyServerErrors);
        },


        /*
         * Serialize model attributes to JSON. Attributes that have their own 'toJSON' method are serialized using it.
         */
        serialize: function() {
            return _.clone(this.attributes);
        }

    }, {

        /*
         * Try to parse JSON error response from server and issue new 'error' events.
         */
        unifyServerErrors: function(model, xhr) {
            var errorStatus = httpStatusCodes[xhr.status],
                errorObj = BaseModel.createServerError(xhr);

            model.trigger("error:" + errorStatus, model, errorObj, xhr);
            model.trigger("error:all", model, errorObj, xhr);
        },

        /*
         *  Create error object by paring server response
         */
        createServerError: function (xhr) {
            var error ;

            try {
                error = JSON.parse(xhr.responseText);
            } catch (e) {
                error = {
                    message : "Can't parse server response",
                    errorCode: errors.UNEXPECTED_ERROR,
                    parameters : []
                };
            }

            return error;
        }

    });

    return BaseModel;

});