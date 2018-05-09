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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var BiComponentError = require("./BiComponentError"),
        errorCodes = require("./enum/biComponentErrorCodes"),
        messages = require("./enum/biComponentErrorMessages");

    return BiComponentError.extend({
        constructor: function (validationError) {
            this.validationError = validationError;

            var propertyName = validationError.dataPath.substring(1);

            BiComponentError.prototype.constructor.call(
                this,
                errorCodes.SCHEMA_VALIDATION_ERROR,
                messages[errorCodes.SCHEMA_VALIDATION_ERROR]+ ": " + propertyName + ": " + this.validationError.message,
                [validationError.dataPath, this.validationError.message]);
        }
    });
});