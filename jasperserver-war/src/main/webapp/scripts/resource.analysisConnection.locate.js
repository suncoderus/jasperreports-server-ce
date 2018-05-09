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

/* global dynamicTree, console */

var resourceOLAPLocate = {
    initialize: function() {
        resourceOLAPLocate.foldersPanel.initialize();
    }
};

resourceOLAPLocate.foldersPanel = {
    _treeId: 'OLAPTreeRepoLocation',
    _uri: "/",

    initialize: function (options) {
        // Setup folders tree
        this.tree = new dynamicTree.createRepositoryTree(this.getTreeId(), {
            providerId: 'OLAPTreeDataProvider'
        });

        this.tree.observe('server:error', function() {
            if (window.console) {
                console.log("Server load error.");
            }
        });

        this.tree.observe('tree:loaded', function(event) {
            //            this.tree.openAndSelectNode(this._uri);
        }.bind(this));

        this.tree.observe('leaf:selected', function(event) {
            this._uri = event.memo.node.param.uri;
            $("resourceUri").setValue(this._uri);
        }.bindAsEventListener(this));

        this.tree.showTree(1);

        return this;
    },

    getTreeId: function() {
        return this._treeId;
    },

    selectFolder: function(folderUri) {
        this.tree.openAndSelectNode(folderUri);
    },

    getSelectedFolderUri: function() {
        return this._selectedFolderUri;
    }
};

document.observe('dom:loaded', function() {
    resourceOLAPLocate.initialize();
});