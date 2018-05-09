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
 * @author: Taras Bidyuk
 * @version: $Id$
 */

define(function(require) {

    var i18n = require("bundle!AttributesBundle"),
        _ = require("underscore");

    var errorCodeToMessageMap = {},
        unknownError = [
            i18n["attributes.error.message.unknown.error"]
        ];

    errorCodeToMessageMap["access.denied"] = [
        i18n["attributes.error.message.access.denied"],
        ["name"]
    ];
    errorCodeToMessageMap[401] = [
        i18n["attributes.error.message.not.authenticated"]
    ];
    errorCodeToMessageMap["illegal.parameter.value.error"] = [
        i18n["attributes.illegal.parameter.value.error"]
    ];
    errorCodeToMessageMap["attribute.invalid.permission.order"] = [
        i18n["attributes.error.message.invalid.permission.order"],
        [
            "name",
            {
                field: "strongerPermission", fn: getPermissionMask
            },
            {
                field: "inheritedPermission", fn: getPermissionMask
            }
        ]
    ];
    errorCodeToMessageMap["attribute.duplicate.server.level"] = [
        i18n["attributes.error.message.duplicate.server.level"],
        ["name"]
    ];


    function getParamsObject(paramsArray, parameters) {
        var paramsObj = {};

        _.each(paramsArray, function(param, index) {
            if (!_.isObject(param)) {
                paramsObj[param] = parameters[index];
            } else {
                paramsObj[param.field] = param.fn(parameters[index]);
            }
        });

        return paramsObj;
    }

    function getPermissionMask(code) {
        return i18n["attributes.attribute.permissionMask." + code];
    }

    return function(response) {
        var responseJSON = response.responseJSON || {},
            errorCode = responseJSON.errorCode,
            parameters = responseJSON.parameters,
            responseStatus = response.status,
            msgObj = errorCodeToMessageMap[errorCode ? errorCode : responseStatus] || unknownError,
            msg;

        msg = _.template(msgObj[0])(msgObj[1] ? getParamsObject(msgObj[1], parameters) : {});

        return msg;
    }

});