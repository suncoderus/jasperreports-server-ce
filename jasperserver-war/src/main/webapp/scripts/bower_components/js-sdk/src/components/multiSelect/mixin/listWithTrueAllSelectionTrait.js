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
 * @author: Sergey Prilukin
 * @version: $Id: listWithTrueAllSelectionTrait.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

define(function (require) {
    'use strict';

    var ListWithNavigation = require("components/scalableList/view/ListWithNavigation");

    var listWithTrueAllSelectionTrait = {

        //Add selected attributes to model
        postProcessChunkModelItem: function(item, i) {
            ListWithNavigation.prototype.postProcessChunkModelItem.call(this, item, i);
            if (this.getTrueAll()) {
                item.selected = true;
            }
        },

        setTrueAll: function(all, options) {
            this.isTrueAll = all;
            //this.setDisabled(all);
            this.setValue({}, options);
        },

        getTrueAll: function() {
            return this.isTrueAll;
        }
    };

    return listWithTrueAllSelectionTrait;
});
