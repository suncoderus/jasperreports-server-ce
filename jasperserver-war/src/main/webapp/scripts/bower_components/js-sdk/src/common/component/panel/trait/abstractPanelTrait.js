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
 * @author: Zakhar Tomchenko, Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function() {
    "use strict";

    /**
     * @mixin abstractPanelTrait
     * @description Abstract trait for Panel. Should be extended with concrete implementation.
     */
    var abstractPanelTrait = {
        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Additional methods to expose through Panel's API.
         */
        extension: {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call inside Panel's constructor.
         */
        onConstructor: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call before Panel initialization.
         */
        beforeInitialize: function() {},

        /**
         * @abstract
         * @description Method to call after Panel initialization.
         */
        afterInitialize: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call before Panel element is set.
         */
        beforeSetElement: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call after Panel element is set.
         */
        afterSetElement: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call before Panel is opened.
         */
        beforeOpen: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call after Panel is opened.
         */
        afterOpen: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call before Panel is closed.
         */
        beforeClose: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call after Panel is closed.
         */
        afterClose: function() {},

        /**
         * @abstract
         * @memberof! abstractPanelTrait
         * @description Method to call when Panel is removed.
         */
        onRemove: function() {}
    };

    return abstractPanelTrait;
});
