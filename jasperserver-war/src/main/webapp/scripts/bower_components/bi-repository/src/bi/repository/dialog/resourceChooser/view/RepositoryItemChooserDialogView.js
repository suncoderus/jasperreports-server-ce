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
 * @author: Dima Gorbenko
 * @version: $Id$
 */

define(function (require) {

    "use strict";

    var _ = require('underscore'),
        $ = require("jquery"),
        i18n = require('bundle!all'),
        jrsConfigs = require('jrs.configs'),

        Dialog = require("common/component/dialog/Dialog"),
        AlertDialog = require("common/component/dialog/AlertDialog"),
        Tree = require('common/component/tree/Tree'),
        TreeDataLayer = require('common/component/tree/TreeDataLayer'),

        tabbedPanelTrait = require('common/component/panel/trait/tabbedPanelTrait'),

        SearchTreePlugin = require('common/component/tree/plugin/SearchPlugin'),
        TooltipPlugin = require('common/component/tree/plugin/TooltipPlugin'),
        InfiniteScrollPlugin = require('common/component/tree/plugin/InfiniteScrollPlugin'),
        NoSearchResultsMessagePlugin = require('common/component/tree/plugin/NoSearchResultsMessagePlugin'),

        contextPath = require("jrs.configs").contextPath,
        repositoryResourceTypes = require("bi/repository/enum/repositoryResourceTypes"),
        browserDetection = require("common/util/browserDetection"),

        resourceDialogTemplate = require("text!../template/itemDialog/resourceDialogTemplate.htm"),
        sidebarTreeLeafTemplate = require("text!../template/itemDialog/sidebarTreeLeafTemplate.htm"),
        tabPanelButtonTemplate = require("text!../template/itemDialog/tabPanelButtonTemplate.htm"),
        treeTooltipTemplate = require("text!../template/itemDialog/treeTooltipTemplate.htm");

    var DISABLE_LIST = false;

    var ITEM_HEIGHT = 22;
    var LIST_TAB_NAME = "list";
    var TREE_TAB_NAME = "tree";
    var TREE_BUFFER_SIZE = 5000;

    var BASE_URI = contextPath + "/rest_v2/api/resources?{{= id != '@fakeRoot' ? 'folderUri=' + id : ''}}";
    var URI_SUFFIX = "&offset={{= offset }}&limit={{= limit }}&forceTotalCount=true&forceFullPage=true";

    var dataUriTemplateForResourceTreeView = BASE_URI + "&recursive=false&containerType=" + repositoryResourceTypes.FOLDER + URI_SUFFIX;
    var dataUriTemplateForResourceListView = BASE_URI + "&recursive=true" + URI_SUFFIX;
    var dataUriTemplate_hackForRootFolderInTree = contextPath + "/flow.html?_flowId=searchFlow&method=getNode&provider=repositoryExplorerTreeFoldersProvider&uri=/&depth=1";

    //////////////////////////////////////////////////////////////

    function cssClassItemProcessor(item) {

        switch (item.value.resourceType) {

            case repositoryResourceTypes.REPORT_UNIT:
                item.cssClass = "report";
                break;

            case repositoryResourceTypes.DOMAIN_TOPIC:
                item.cssClass = "domain topic";
                break;

            case repositoryResourceTypes.SEMANTIC_LAYER_DATA_SOURCE:
                item.cssClass = "domain";
                break;

            case repositoryResourceTypes.ADHOC_DATA_VIEW:
                item.cssClass = "adhocDataView";
                break;

            case repositoryResourceTypes.OLAP_CUBE:
                item.cssClass = "olap";
                break;

            default:
                item.cssClass = "adhocDataView";
                break;
        }

        return item;
    }

    return Dialog.extend({

        constructor: function (options) {
            options || (options = {});
            this.options = options;

            if (this.options.disableListTab === true) {
                DISABLE_LIST = true;
            }

            if (!this.options.resourcesTypeToSelect || !this.options.resourcesTypeToSelect.length) {
                this.options.resourcesTypeToSelect = [repositoryResourceTypes.REPORT_UNIT];
            }

            if (!this.options.resourcesTypeToSelectTree || !this.options.resourcesTypeToSelectTree.length) {
                this.options.resourcesTypeToSelectTree = this.options.resourcesTypeToSelect;
            }

            this._dfdRenderSerachFormTo = $.Deferred();

            var processors = {
                treeNodeProcessor: {
                    processItem: function (item) {
                        item._node = _.contains([repositoryResourceTypes.FOLDER], item.value.resourceType);
                        return item;
                    }
                },
                filterPublicFolderProcessor: {
                    processItem: function (item) {
                        if (item.value.uri !== '/public') {
                            return item;
                        }
                    }
                },
                filterTempFolderProcessor: {
                    processItem: function (item) {
                        if (item.value.uri.indexOf('/temp') === -1) {
                            return item;
                        }
                    }
                },
                filterEmptyFoldersProcessor: {
                    processItem: function (item) {
                        if (!(item.value.resourceType === 'folder' && item.value._links.content === '')) {
                            return item;
                        }
                    }
                },
                cssClassItemProcessor: {
					processItem: options.cssClassItemProcessor || cssClassItemProcessor
                }
            };

            this.resourcesTreeView = Tree
                .use(InfiniteScrollPlugin)
                .use(TooltipPlugin, {
                    i18n: i18n,
                    attachTo: this.$el,
                    contentTemplate: treeTooltipTemplate
                })
                .create()
                .instance({
                    type: "tree",
                    itemsTemplate: sidebarTreeLeafTemplate,
                    listItemHeight: ITEM_HEIGHT,
                    selection: {allowed: true, multiple: false},
                    rootless: true,
                    collapsed: true,
                    lazyLoad: true,
                    bufferSize: TREE_BUFFER_SIZE,
                    additionalCssClasses: "folders",
                    dataUriTemplate: this.prepareChooserDialogUrl(dataUriTemplateForResourceTreeView, this.options.resourcesTypeToSelectTree),
                    levelDataId: "uri",

                    customDataLayers: {
                        //workaround for correct viewing of '/public' folder label
                        "/": _.extend(new TreeDataLayer({
                            dataUriTemplate: dataUriTemplate_hackForRootFolderInTree,
                            processors: _.chain(processors).omit("filterPublicFolderProcessor").values().value(),
                            getDataArray: function (data) {
                                data = JSON.parse($(data).text());

                                var rootItems = [
                                    {
                                        id: "@fakeRoot",
                                        label: data.label,
                                        uri: "/",
                                        resourceType: 'folder',
                                        _links: {content: "@fakeContentLink"}
                                    }
                                ];

                                if (jrsConfigs.isProVersion) {

                                    var publicFolderLabel = _.find(data.children, function (item) {
                                        return item.uri === '/public';
                                    }).label;

                                    rootItems.push({
                                        id: "/public",
                                        label: publicFolderLabel,
                                        uri: "/public",
                                        resourceType: 'folder',
                                        _links: {content: "@fakeContentLink"}
                                    });
                                }

                                return rootItems;
                            }
                        }), {
                            accept: 'text/html',
                            dataType: 'text'
                        })
                    },

                    processors: _.values(processors),

                    getDataArray: function (data, status, xhr) {
                        return data && data[repositoryResourceTypes.RESOURCE_LOOKUP] ? data[repositoryResourceTypes.RESOURCE_LOOKUP] : [];
                    },

                    getDataSize: function (data, status, xhr) {
                        return +xhr.getResponseHeader("Total-Count");
                    }
                });


            this.resourcesListView = Tree
                .use(NoSearchResultsMessagePlugin)
                .use(TooltipPlugin, {
                    i18n: i18n,
                    attachTo: this.$el,
                    contentTemplate: treeTooltipTemplate
                })
                .use(SearchTreePlugin, {dfdRenderTo: this._dfdRenderSerachFormTo})
                .create()
                .instance({
                    type: "list",
                    rootLevelHeight: _.bind(this._getContentHeight, this),
                    itemsTemplate: sidebarTreeLeafTemplate,
                    listItemHeight: ITEM_HEIGHT,
                    selection: {allowed: true, multiple: false},
                    rootless: true,
                    collapsed: true,
                    lazyLoad: true,
                    dataUriTemplate: this.prepareChooserDialogUrl(dataUriTemplateForResourceListView, this.options.resourcesTypeToSelect),
                    levelDataId: "uri",
                    cache: {
                        searchKey: "searchString",
                        pageSize: 100
                    },

                    processors: [
                        processors.cssClassItemProcessor,
                        processors.filterTempFolderProcessor
                    ],

                    getDataArray: function (data, status, xhr) {
                        return data && data[repositoryResourceTypes.RESOURCE_LOOKUP] ? data[repositoryResourceTypes.RESOURCE_LOOKUP] : [];
                    },

                    getDataSize: function (data, status, xhr) {
                        return +xhr.getResponseHeader("Total-Count");
                    }
                });

            Dialog.prototype.constructor.call(this, {
                template: resourceDialogTemplate,
                modal: true,
                resizable: true,
                additionalCssClasses: "sourceDialogNew" + (DISABLE_LIST ? " listDisabled" : ""),
                title: i18n["Repository.ItemSelectDialog.dialogTitle"],
                traits: [tabbedPanelTrait],
                tabHeaderContainerSelector: ".tabHeaderContainer",
                tabContainerClass: "tabContainer control groupBox treeBox",
                optionTemplate: tabPanelButtonTemplate,
                toggleClass: "down",
                tabs: [
                    {
                        label: i18n["Repository.ItemSelectDialog.foldersTab"],
                        action: TREE_TAB_NAME,
                        content: this.resourcesTreeView,
                        exposeAction: true,
                        additionalCssClasses: "action square small",
                        i18n: i18n
                    },
                    {
                        label: i18n["Repository.ItemSelectDialog.listTab"],
                        action: LIST_TAB_NAME,
                        content: this.resourcesListView,
                        exposeAction: true,
                        additionalCssClasses: "action square small",
                        i18n: i18n
                    }
                ],
                buttons: [
                    {label: i18n["Repository.ItemSelectDialog.okButton"], action: "apply", primary: true},
                    {label: i18n["Repository.ItemSelectDialog.cancelButton"], action: "cancel", primary: false}
                ]
            });
        },

        prepareChooserDialogUrl: function (baseUrl, types) {
            return baseUrl + "&" + _.map(types, function (type) {
                    return "type=" + type;
                }).join("&");
        },

        initialize: function (options) {

            this.listenTo(this.resourcesTreeView, "selection:change", this._selectionListener);
            this.listenTo(this.resourcesTreeView, "levelRenderError", this._onLevelRenderError);
            this.listenTo(this.resourcesTreeView, "item:dblclick", this._onOkButtonClick);

            this.listenTo(this.resourcesListView, "selection:change", this._selectionListener);
            this.listenTo(this.resourcesListView, "levelRenderError", this._onLevelRenderError);
            this.listenTo(this.resourcesListView, "item:dblclick", this._onOkButtonClick);
            this.listenTo(this.resourcesListView.searchForm, "search", this._onSearch);
            this.listenTo(this.resourcesListView.searchForm, "clear", this._resetTreeAndList);

            this.listenTo(this, 'button:apply', _.bind(this._onOkButtonClick, this));
            this.listenTo(this, 'button:cancel', _.bind(this._onCancelButtonClick, this));
            this.listenTo(this, "tab:tree tab:list", this._tabChangedEvent);

            Dialog.prototype.initialize.apply(this, arguments);

            this._haveBeenOpened = false;
            this._isOpened = false;
            this.selectedResource = null;
            this._lastSelectedResource = null;
        },

        events: {resize: "_onResize"},

        render: function () {
            Dialog.prototype.render.apply(this, arguments);

            // connect search form to dialog header
            this._dfdRenderSerachFormTo.resolve(this.$tabHeaderContainer);

            return this;
        },

        open: function () {
            this._openDialog();
        },

        close: function () {
            this._closeDialog();
        },

        remove: function () {
            this._closeDialog();

            // remove internal components
            this.resourcesTreeView.remove();
            this.resourcesListView.remove();

            Dialog.prototype.remove.apply(this, arguments);
        },

        setDefaultSelectedItem: function (defaultSelectedItem) {
            this._defaultSelectedItem = defaultSelectedItem;
        },

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Next go private methods

        _onOkButtonClick: function () {
            if (this.selectedResource) {
                this._closeDialog();
                this._lastSelectedResource = this.selectedResource;
            }
        },

        _onCancelButtonClick: function () {
            // removing the selection if some was made
            this.selectedResource = null;
            if (this._lastSelectedResource) {
                this.selectedResource = this._lastSelectedResource;
            }

            this._closeDialog();
        },

        _openDialog: function () {

            if (this._isOpened) {
                return;
            }

            var self = this;
            this._resizableContainerShiftHeight = 6 - (DISABLE_LIST ? 40 : 0);

            // QA team said: if user has changed size of the dialog, then closed it, and then opened it again -- the dialog
            // should restore it's size. Le'ts implement it !
            if (this._haveBeenOpened === false) {
                this._initialDialogWidth = this.$el.css("width");
                this._initialDialogHeight = this.$el.css("height");
                this._haveBeenOpened = true;
            } else {
                // restore saved values upon opening
                this.$el.css({
                    width: this._initialDialogWidth,
                    height: this._initialDialogHeight
                });
            }

            Dialog.prototype.open.apply(this, arguments);

            var setTabContainerStyles = _.bind(function () {
                this.$contentContainer.find(".tabContainer").css({
                    "height": "inherit",
                    "overflow-y": "auto"
                });
            }, this);

            //IE8, IE9 compatibility workaround
            if (browserDetection.isIE8() || browserDetection.isIE9()) {
                setTimeout(setTabContainerStyles, 1);
            } else {
                setTabContainerStyles();
            }

            this.$el
                .children()
                .not(".subcontainer")
                .not(".ui-resizable-e")
                .not(".ui-resizable-se")
                .each(function () {
                    self._resizableContainerShiftHeight += self.$(this).outerHeight(true);
                });
            this.$contentContainer.height(this.$el.height() - this._resizableContainerShiftHeight);

            // At this moment we have to reset search field
            this.resourcesListView.searchForm.clearSilently();
            // and reset the tree itself !
            this._resetTreeAndList();

            this.disableButton("apply");

            // prepare deferred object to let other components know then tree or list will be visible
            this.resourcesTreeView._onceVisible = new $.Deferred();
            this.resourcesListView._onceVisible = new $.Deferred();

            // now, select (actually, launch listeners for tab to ne opened) the default item in the tree and the list
            this._preselectItem();

            // then, open some default tab
            this._openTab(DISABLE_LIST ? TREE_TAB_NAME : LIST_TAB_NAME);

            this._isOpened = true;
        },

        _closeDialog: function () {
            if (!this._isOpened) {
                return;
            }

            // "close" deferred object in tree and list
            this.resourcesTreeView._onceVisible.reject();
            this.resourcesListView._onceVisible.reject();

            Dialog.prototype.close.apply(this, arguments);

            this._isOpened = false;
        },

        _selectionListener: function (selection) {

            var selectionKeys = Object.keys(selection);

            var itemSelected = selection && (_.isArray(selection) || _.isObject(selection)) && selection[selectionKeys[0]];
            var resourceUri = itemSelected ? itemSelected.uri : undefined;
            var resourceType = itemSelected ? itemSelected.resourceType : undefined;

            if (!(itemSelected || resourceUri || resourceType)) {
                this.disableButton("apply");
                this.$('.itemDescription').empty();
                return;
            }

            if (resourceType === repositoryResourceTypes.FOLDER) {
                this.$('.itemDescription').empty();
            } else {
                this.$(".itemDescription").text(itemSelected.description || "");
            }

            // do not enable OK button if user has clicked on folders which he might want to open
            if (_.contains(_.union([repositoryResourceTypes.FOLDER]), resourceType)) {
                // drop information about selecting which probably was made already
                this.selectedResource = null;
                this.disableButton("apply");
                return;
            }

            // save selected resource information.
            // this could be used by anyone to get information about what has been selected
            this.selectedResource = {
                resourceUri: resourceUri,
                event: this._getAdHocFlowEvent(resourceType)
            };

            this.enableButton("apply");
        },

        _resetTreeAndList: function () {

            // reset Tree
            this.resourcesTreeView.collapse("@fakeRoot", {silent: true});
            this.resourcesTreeView.collapse("/public", {silent: true});
            this.resourcesTreeView.resetSelection();

            // reset List
            this.resourcesListView.resetSelection();

            // and description as well
            this.$('.itemDescription').empty();
        },

        _preselectItem: function () {

            // This function should select some item in the tree or in the list
            // once the dialog is opened.
            // There can be several "sources" of items which we might select:
            // 1) pre-default selection given during dialog initialization
            // 2) last selected item (user opened dialog, selected item and clicked "OK")
            // So, the logic would be the next:
            // we look if we have "last selected item", if yes -- then use it, if not --
            // then look if we have "pre-default selection", if yes -- the use it, if not -- nothing to do

            var itemToSelect = false;
            if (this._lastSelectedResource) {
                itemToSelect = this._lastSelectedResource.resourceUri;
            } else if (this._defaultSelectedItem) {
                itemToSelect = this._defaultSelectedItem;
            }
            if (!itemToSelect) {
                // nothing to do
                return;
            }

            // now, we have to wait once the tree or list will be visible
            // we need it because we have to scroll tree or list, and this requires them to be visible
            // on the page

            var treeScrollArea = this.resourcesTreeView.$el.parent();
            this.resourcesTreeView._onceVisible.done(_.bind(function () {
                this.resourcesTreeView._selectTreeNode(itemToSelect, treeScrollArea);
            }, this));

            var listScrollArea = $(this.resourcesListView.$el.find("div.subcontainer")[0]);
            this.resourcesListView._onceVisible.done(_.bind(function () {
                //TODO: Not Yet Ready
                //this.resourcesListView._selectTreeNode(itemToSelect, listScrollArea);
            }, this));
        },

        _onResize: function () {
            this.$contentContainer.height(this.$el.height() - this._resizableContainerShiftHeight);

            this.resourcesListView.rootLevel.list.$el.height(this.$el.height() - this._resizableContainerShiftHeight - 6);
            this.resourcesTreeView.rootLevel.list.$el.css("height", "100%");
        },

        _getContentHeight: function () {
            return this.$el.height() - this._resizableContainerShiftHeight - 6;
        },

        _onSearch: function () {
            this._openTab(LIST_TAB_NAME);
            this.disableButton("apply");
        },

        _tabChangedEvent: function () {
            if (this.selectedTab === TREE_TAB_NAME) {
                this.resourcesTreeView._onceVisible.resolve();
            } else if (this.selectedTab === LIST_TAB_NAME) {
                this.resourcesListView._onceVisible.resolve();
            }
        },

        _openTab: function (tabName) {

            this.openTab.apply(this, arguments);

        },

        _getAdHocFlowEvent: function (resourceType) {
            var adHocFlowEvent = "";
            if (resourceType == repositoryResourceTypes.REPORT_UNIT || resourceType == repositoryResourceTypes.DOMAIN_TOPIC) {
                adHocFlowEvent = "startAdHocWithTopic";
            } else if (resourceType == repositoryResourceTypes.SEMANTIC_LAYER_DATA_SOURCE) {
                adHocFlowEvent = "startQueryBuilder";
            }

            return adHocFlowEvent;
        },

        _getErrorDialog: function () {
            return this.errorDialog ? this.errorDialog : (this.errorDialog = new AlertDialog());
        },

        _onLevelRenderError: function (responseStatus, error, level) {
            error = JSON.parse(error);
            var errorDialog = this._getErrorDialog();
            errorDialog.setMessage(error.parameters[0].substring(error.parameters[0].indexOf(": ") + 2, error.parameters[0].indexOf("\n")));
            errorDialog.open();
            level.$el.removeClass(level.loadingClass).addClass(level.openClass);
        }
    });
});
