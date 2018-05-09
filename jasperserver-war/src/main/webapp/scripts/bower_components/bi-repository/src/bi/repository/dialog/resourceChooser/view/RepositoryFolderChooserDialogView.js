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
        i18n = require('bundle!all'),
        Dialog = require("common/component/dialog/Dialog"),
        dialogTemplate = require('text!../template/repositoryFolderChooserDialogTemplate.htm'),
        RepositoryFolderTree = require("bi/repository/repositoryFolderTree/RepositoryFolderTree");

    return Dialog.extend({

        constructor: function (options) {
            options || (options = {});

            var additionalCssClasses = "selectFolder newResizeableDialog";
            if (options.additionalCssClasses) {
                additionalCssClasses += " " + options.additionalCssClasses;
            }

            this.foldersTree = RepositoryFolderTree();

            Dialog.prototype.constructor.call(this, {
                modal: true,
                resizable: true,
                minWidth: 400,
                minHeight: 400,
                additionalCssClasses: additionalCssClasses,
                title: i18n["repository.content"],
                content: _.template(dialogTemplate)({i18n: i18n}),
                buttons: [
                    {label: i18n["button.select"], action: "select", primary: true},
                    {label: i18n["button.cancel"], action: "cancel", primary: false}
                ]
            });

            var $bodyOfDialog = this.$contentContainer.find(".control.groupBox .body");
            $bodyOfDialog.append(this.foldersTree.render().el);
        },

        initialize: function (options) {

            this.listenTo(this.foldersTree, "selection:change", this._selectionListener);

            this.listenTo(this, 'button:select', this._onOkButtonClick);
            this.listenTo(this, 'button:cancel', this._onCancelButtonClick);

            Dialog.prototype.initialize.apply(this, arguments);

            this._isOpened = false;
            this.selectedResource = null;
            this._lastSelectedResource = null;
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
            this.foldersTree.remove();

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

            Dialog.prototype.open.apply(this, arguments);

            // reset the tree itself !
            this._resetTree();

            this.disableButton("select");

            // now, select the default item in the tree
            this._preselectItem();

            this._isOpened = true;
        },

        _closeDialog: function () {
            if (!this._isOpened) {
                return;
            }

            Dialog.prototype.close.apply(this, arguments);

            this._isOpened = false;
        },

        _selectionListener: function (selection) {

            var selectionKeys = Object.keys(selection);

            var itemSelected = selection && (_.isArray(selection) || _.isObject(selection)) && selection[selectionKeys[0]];
            var resourceUri = itemSelected ? itemSelected.uri : undefined;
            var resourceType = itemSelected ? itemSelected.resourceType : undefined;

            if (!(itemSelected || resourceUri || resourceType)) {
                this.disableButton("select");
                return;
            }

            // save selected resource information.
            // this could be used by anyone to get information about what has been selected
            this.selectedResource = {
                resourceUri: resourceUri
            };

            this.enableButton("select");
        },

        _resetTree: function () {

            // reset Tree
            this.foldersTree.collapse("@fakeRoot", {silent: true});
            this.foldersTree.collapse("/public", {silent: true});
            this.foldersTree.resetSelection();
        },

        _preselectItem: function () {

            // This function should select some item in the tree once the dialog is opened.
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

            var scrollArea = this.foldersTree.$el.parent();
            this.foldersTree._selectTreeNode(itemToSelect, scrollArea);
        }
    });

});
