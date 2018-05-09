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

    var _ = require("underscore"),
        i18n = require('bundle!AttributesBundle'),
        AttributesCollection = require("attributes/collection/AttributesCollection"),
        DesignerRowView = require("attributes/view/DesignerRowView"),
        DesignerEmptyView = require("attributes/view/DesignerEmptyView"),
        viewWithPermissionTrait = require("attributes/view/viewWithPermissionTrait"),
        RowView = require("attributes/view/RowView"),
        attributesTypesEnum = require("attributes/enum/attributesTypesEnum"),
        i18n2 = require("bundle!CommonBundle"),
        AttributeModel = require("attributes/model/AttributeModel"),
        modelWithPermissionTrait = require("attributes/model/modelWithPermissionTrait"),
        tooltipTemplate = require("text!attributes/templates/tooltipTemplate.htm");

    return function(options) {
        var collection = new AttributesCollection([], {
                context: options.context,
                model: options.type === attributesTypesEnum.USER ? AttributeModel : AttributeModel.extend(modelWithPermissionTrait)
            }),
            commonOptions = {
                type: options.type,
                collection: collection,
                childViewContainer: ".tbody",
                tooltip: {
                    template: tooltipTemplate,
                    i18n: i18n
                }
            };

        return _.extend(commonOptions, options.type !== attributesTypesEnum.SERVER ? {
            $container: options.container,
            designer: {
                childView: options.type === attributesTypesEnum.USER ? DesignerRowView : DesignerRowView.extend(viewWithPermissionTrait),
                emptyView: DesignerEmptyView,
                filters: [
                    {
                        title: i18n["attributes.table.filters.all"],
                        value: "true",
                        field: "defaultFilter",
                        selected: true
                    },
                    {
                        title: i18n["attributes.table.filters.true"],
                        value: "true",
                        field: "inherited"
                    },
                    {
                        title: i18n["attributes.table.filters.false"],
                        value: "false",
                        field: "inherited"
                    }
                ]
            },
            viewer: {
                childView: RowView
            }
        } : {
            el: options.el,
            childView: DesignerRowView.extend(viewWithPermissionTrait),
            emptyView: DesignerEmptyView,
            buttons: [
                { label: i18n2["button.save"], action: "save", primary: true },
                { label: i18n2["button.cancel"], action: "cancel", primary: false }
            ],
            buttonsContainer: ".buttonsContainer"
        });
    };

});