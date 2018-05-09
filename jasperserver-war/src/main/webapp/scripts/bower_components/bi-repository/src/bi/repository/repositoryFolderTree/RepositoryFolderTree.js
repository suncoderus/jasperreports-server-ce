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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var Tree = require('common/component/tree/Tree'),
        $ = require("jquery"),
        _ = require("underscore"),
        jrsConfigs = require('jrs.configs'),
        TreeDataLayer = require('common/component/tree/TreeDataLayer'),
        TooltipTreePlugin = require('common/component/tree/plugin/TooltipPlugin'),
        ContextMenuTreePlugin = require("common/component/tree/plugin/ContextMenuTreePlugin"),
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),

        defaultTooltipTemplate = require("text!./template/repositoryFolderChooserDialogTooltipTemplate.htm"),
        repositoryFoldersTreeLevelTemplate = require('text!./template/repositoryFoldersTreeLevelTemplate.htm'),

        i18n = require('bundle!all'),
        defaultSettings = require("settings/generalSettings"),

        processors = {
            folderTreeProcessor: {
                processItem: function (item) {
                    item._node = true;
                    item._readonly = !(item.value.permissionMask == 1 || item.value.permissionMask & 4);
                    return item;
                }
            },

            filterPublicFolderProcessor: {
                processItem: function (item) {
                    if (item.value.uri !== repositoryFoldersTreeFactory.settings.publicFolderUri && item.value.uri !== repositoryFoldersTreeFactory.settings.tempFolderUri) {
                        return item;
                    }
                }
            },

            i18n: {
                processItem: function (item) {
                    item.i18n = i18n;
                    return item;
                }
            },

            tenantProcessor: {
                processItem: function (item) {
                    item._node = true;
                    item.value.label = item.value.tenantName;
                    item.value.uri = item.value.tenantUri;
                    return item;
                }
            }
        };

    var repositoryFoldersTreeFactory = function (options) {

        var contextPath = (options && options.contextPath) || jrsConfigs.contextPath;
        var tooltipTemplate = (options && options.tooltipTemplate) || defaultTooltipTemplate;

        var tree = Tree.use(TooltipTreePlugin, {
            i18n: i18n,
            contentTemplate: tooltipTemplate
        }).create().instance({
            additionalCssClasses: "folders",
            dataUriTemplate: contextPath + "/rest_v2/resources?{{= id != '@fakeRoot' ? 'folderUri=' + id : ''}}&recursive=false&type=" + repositoryResourceTypes.FOLDER + "&offset={{= offset }}&limit={{= limit }}",
            levelDataId: "uri",
            itemsTemplate: repositoryFoldersTreeLevelTemplate,
            collapsed: true,
            lazyLoad: true,
            rootless: true,
            selection: {allowed: true, multiple: false},
            customDataLayers: {
                //workaround for correct viewing of '/public' and '/' folder labels
                "/": _.extend(new TreeDataLayer({
                    dataUriTemplate: contextPath + "/flow.html?_flowId=searchFlow&method=getNode&provider=repositoryExplorerTreeFoldersProvider&uri=/&depth=1",
                    processors: _.chain(processors).omit("filterPublicFolderProcessor", "tenantProcessor").values().value(),
                    getDataArray: function (data) {
                        data = JSON.parse($(data).text());
                        var publicFolder = _.find(data.children, function (item) {
                                return item.uri === '/public';
                            }),
                            res = [{
                                id: "@fakeRoot",
                                label: data.label,
                                uri: "/",
                                resourceType: 'folder',
                                permissionMask: computePermissionMask(data.extra),
                                _links: {content: "@fakeContentLink"}
                            }];

                        if (publicFolder) {
                            res.push({
                                id: "/public",
                                label: publicFolder.label,
                                uri: "/public",
                                resourceType: 'folder',
                                permissionMask: computePermissionMask(publicFolder.extra),
                                _links: {content: "@fakeContentLink"}
                            });
                        }

                        return res;
                    }
                }), {
                    accept: 'text/html',
                    dataType: 'text'
                })
            },
            processors: [processors.i18n, processors.folderTreeProcessor, processors.filterPublicFolderProcessor],
            getDataArray: function (data, status, xhr) {
                return data ? data[repositoryResourceTypes.RESOURCE_LOOKUP] : [];
            }
        });

        return tree;
    };


    function computePermissionMask(extra) {
        var mask = 2;

        extra.isWritable && (mask = mask | 4);
        extra.isRemovable && (mask = mask | 16);
        extra.isAdministrable && (mask = 1);

        return mask;
    }

    repositoryFoldersTreeFactory.settings = defaultSettings;


    return repositoryFoldersTreeFactory;

});
