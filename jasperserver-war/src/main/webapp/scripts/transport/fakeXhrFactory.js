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
 * @author: Andriy Godovanec, Taras Bidyuk
 */

define(function(require) {

    var _ = require("underscore");

    return function (xhr){

        var requestHeadersNames = {};
        var requestHeaders = {};

        return _.extend(xhr, {
            getResponseHeader: function( key ) {
                var match;
                var rheaders = /^(.*?):[ \t]*([^\r\n]*)$/mg;
                if ( xhr.readyState === 4 ) {
                    if ( !this.responseHeaders ) {
                        this.responseHeaders = {};
                        while ( (match = rheaders.exec( this.responseHeadersString )) ) {
                            this.responseHeaders[ match[1].toLowerCase() ] = match[ 2 ];
                        }
                    }
                    match = this.responseHeaders[ key.toLowerCase() ];
                }
                return match == null ? null : match;
            },

            // Raw string
            getAllResponseHeaders: function() {
                return xhr.readyState === 2 ? this.responseHeadersString : null;
            },

            // Caches the header
            setRequestHeader: function( name, value ) {

                var lname = name.toLowerCase();
                if ( !xhr.readyState ) {
                    name = requestHeadersNames[ lname ] = requestHeadersNames[ lname ] || name;
                    requestHeaders[ name ] = value;
                }
                return this;
            }
        });

    }
});
