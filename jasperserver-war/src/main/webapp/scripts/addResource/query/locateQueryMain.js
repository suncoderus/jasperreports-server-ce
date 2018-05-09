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
 * @version: $Id$
 */

/* global isIPad */

define(function(require) {
    "use strict";

    var domReady = require("!domReady"),
        resourceQueryLocate = require("resource.query.locate"),
        _ = require("underscore"),
        jrsConfigs = require("jrs.configs"),
        resource = require("resource.base"),
        jQuery = require("jquery");

    domReady(function(){
        _.extend(resourceQueryLocate.messages, jrsConfigs.addJasperReport.resourceQueryLocate.messages);

        resourceQueryLocate.initialize();
        isIPad() && resource.initSwipeScroll();

        jQuery("#steps1_2").on("click", function() { return resourceQueryLocate.jumpTo("reportNaming"); });
        jQuery("#step3").on("click", function() { return resourceQueryLocate.jumpTo("resources"); });
        jQuery("#step4").on("click", function() { return resourceQueryLocate.jumpTo("dataSource"); });
        jQuery("#step5").on("click", function() { return resourceQueryLocate.jumpTo("query"); });
        jQuery("#step6").on("click", function() { return resourceQueryLocate.jumpTo("customization"); });
    });
});
