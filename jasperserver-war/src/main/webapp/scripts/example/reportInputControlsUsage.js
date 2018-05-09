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


/**
 * @author: Igor.Nesterenko
 * @version: $Id: reportInputControlsUsage.js 43 2014-08-15 12:52:44Z ktsaregradskyi $
 */

/**
 *  Usage of Input Controls
 */

define(function (require, exports, module) {

    "use strict";

   var InputControlCollection = require("inputControl/collection/InputControlCollection"),
       InputControlCollectionView = require("inputControl/view/InputControlCollectionView"),
       log = require("logger").register(module),

       collection = new InputControlCollection([], {
           contextPath: "http://localhost:8080/jasperserver-pro", // TODO get from config
           resourceUri: "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic",
           //resourceUri: "/public/Samples/v/AllAccounts",
           //resourceUri: "/public/Samples/Reports/AllAccounts",
           //resourceUri: "/public/Samples/AllAccounts",
           container: "#inputControlsContainer"
        }),
       view = new InputControlCollectionView({
           collection: collection
       });

    collection.fetch();

    log.debug("init returned: ", collection, view);

});

