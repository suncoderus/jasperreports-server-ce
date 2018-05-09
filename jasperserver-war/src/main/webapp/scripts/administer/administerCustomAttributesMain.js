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

define(function(require) {
    "use strict";

    var domReady = require("!domReady"),
        $ = require("jquery"),
        logging = require("administer.logging"),
        Administer = require("administer.base"),
        jrsConfigs = require("jrs.configs"),
        attributesDesignerFactory = require("attributes/factory/attributesDesignerFactory"),
        attributesViewOptionsFactory = require("attributes/factory/attributesViewOptionsFactory"),
        scrollEventTrait = require("attributes/trait/attributesViewScrollEventTrait"),
        attributesTypesEnum = require("attributes/enum/attributesTypesEnum");

    domReady(function() {
        Administer.urlContext = jrsConfigs.urlContext;

        logging.initialize();

        var attributesView = attributesDesignerFactory(attributesTypesEnum.SERVER, attributesViewOptionsFactory({
            type: attributesTypesEnum.SERVER,
            el: $('.attributes')
        }));

        attributesView.setContext()
            .done(attributesView.render).then(function() {
                scrollEventTrait.initScrollEvent(attributesView);
            });
    });
});