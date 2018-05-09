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
 * <p>Checks the scope for available properties.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
(function (factory, globalScope) {
    "use strict";

    if (typeof define === "function" && define.amd) {
        define([], factory);
    } else {
        // Browser globals.
        globalScope.ScopeChecker = factory();
    }
}(function () {
    "use strict";

    function ScopeChecker(scope) {
        this.scope = scope;
    }

    ScopeChecker.prototype.getPropertiesCount = function() {
        return this.getPropertiesNames().length;
    };

    ScopeChecker.prototype.getPropertiesNames = function() {
        return Object.keys(this.scope);
    };

    ScopeChecker.prototype.compareProperties = function(scope1PropertiesNames, scope2PropertiesNames) {
        if (!scope1PropertiesNames) {
            throw "Properties for scope 1 not specified";
        }
        if (!scope2PropertiesNames) {
            scope2PropertiesNames = this.getPropertiesNames();
        }

        var comparisonResult = {
            added: [],
            removed: [],
            madeUndefined: [],
            pollution: []
        };

        var i, j;
        for (i = 0; i < scope1PropertiesNames.length; i++) {
            comparisonResult.removed.push(scope1PropertiesNames[i]);
            for (j = 0; j < scope2PropertiesNames.length; j++) {
                if (scope1PropertiesNames[i] === scope2PropertiesNames[j]) {
                    comparisonResult.removed.pop();
                    break;
                }
            }
        }

        for (i = 0; i < scope2PropertiesNames.length; i++) {
            comparisonResult.added.push(scope2PropertiesNames[i]);
            for (j = 0; j < scope1PropertiesNames.length; j++) {
                if (scope2PropertiesNames[i] === scope1PropertiesNames[j]) {
                    comparisonResult.added.pop();

                    break;
                }
            }
        }

        for (i = 0; i < comparisonResult.added.length; i++) {
            if (this.scope[comparisonResult.added[i]] === undefined) {
                comparisonResult.madeUndefined.push(comparisonResult.added[i]);
            } else {
                comparisonResult.pollution.push(comparisonResult.added[i]);
            }
        }

        return comparisonResult;
    };

    return ScopeChecker;
}, this));
