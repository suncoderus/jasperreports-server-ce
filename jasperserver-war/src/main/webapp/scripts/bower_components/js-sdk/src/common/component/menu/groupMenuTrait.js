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
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require("underscore");

    /**
     * @mixin groupMenuTrait
     * @description extends any Menu component, adding menu groups.
     * @example
     *  var GroupContextMenu = ClickMenu.extend(groupMenuTrait);
     *
     *  var groupContextMenu = new GroupContextMenu([
     *      { label: "List View", groupId: "view", action: "list", "default": true },
     *      { label: "Folder View", groupId: "view", action: "folder" },
     *      { label: "All", groupId: "visualization", action: "all", "default": true },
     *      { label: "Reports", groupId: "visualization", action: "report" },
     *      { label: "Ad Hoc Views", groupId: "visualization", action: "adhoc"}
     *  ], "#someElement", { toggle: true });
     */

    var groupMenuTrait = {
        /**
         * @description Groups the menu items and adds separator.
         * @memberof groupMenuTrait
         * @private
         */
        _onInitialize: function () {
            var $items = this.$contentContainer.find("li"),
                GROUP_PROPERTY_NAME = "groupId",
                groupNames = this._getGroupNames(this.collection.models, GROUP_PROPERTY_NAME);

            _.each(groupNames, function (groupName) {
                var $el = $items.filter("[data-" + GROUP_PROPERTY_NAME + "='" + groupName + "']").first();
                if ($el.index()) {
                    $el.before("<li class='leaf separator'></li>");
                }
            }, this);
        },

        /**
         * @description Gets group names.
         * @param {object[]} models - option models
         * @param {string} groupNameProperty - "groupId" property
         * @returns {string[]} - group names array
         * @memberof groupMenuTrait
         * @private
         */
        _getGroupNames: function (models, groupNameProperty) {
            return _.keys(_.groupBy(models, function (m) {
                return m.get(groupNameProperty);
            }));
        }
    };

    return groupMenuTrait;
});