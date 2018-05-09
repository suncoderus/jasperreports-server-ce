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

define(function(require){
    "use strict";

    var domReady = require("!domReady"),
        _ = require("underscore"),
        orgModule = require("org.user.mng.actions"),
        jrsConfigs = require("jrs.configs"),
        i18n = require("bundle!AttributesBundle"),
        i18n2 = require("bundle!CommonBundle"),
        attributesTypesEnum = require("attributes/enum/attributesTypesEnum"),
        ConfirmationDialog = require("common/component/dialog/ConfirmationDialog"),
        attributesViewOptionsFactory = require("attributes/factory/attributesViewOptionsFactory"),
        scrollEventTrait = require("attributes/trait/attributesViewScrollEventTrait"),
        AttributesViewFacade = require("attributes/AttributesViewFacade"),
        TenantsTreeView = require("tenantImportExport/view/TenantsTreeView");

    require("mng.common.actions");
    require("xregexp");
    require("backbone");
    require("org.user.mng.components");
    require("common/util/encrypter");
    require("utils.common");

    require("css!manageTenants.css");

    domReady(function() {
        if (typeof orgModule.messages === "undefined") {
            orgModule.messages = {};
        }

        if (typeof orgModule.Configuration === "undefined") {
            orgModule.Configuration = {};
        }

        _.extend(window.localContext, jrsConfigs.userManagement.localContext);
        _.extend(orgModule.messages, jrsConfigs.userManagement.orgModule.messages);
        _.extend(orgModule.Configuration, jrsConfigs.userManagement.orgModule.Configuration);

        orgModule.userManager.initialize({
            _: _,
            i18n: i18n,
            i18n2: i18n2,
            attributesViewOptionsFactory: attributesViewOptionsFactory,
            AttributesViewFacade: AttributesViewFacade,
            scrollEventTrait: scrollEventTrait,
            attributesTypesEnum: attributesTypesEnum,
            ConfirmationDialog: ConfirmationDialog,
            TenantsTreeView: TenantsTreeView
        });
    });
});