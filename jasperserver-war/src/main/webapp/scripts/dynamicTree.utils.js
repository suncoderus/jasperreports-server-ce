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

/* global dynamicTree, deepClone, isIPad, TouchController */

    // Creating RepositoryFolder class.
dynamicTree.RepositoryFolder = function(options) {
    dynamicTree.TreeNode.call(this, options);

    this.Types = {
        Folder : new dynamicTree.TreeNode.Type('com.jaspersoft.jasperserver.api.metadata.common.domain.Folder'),
        SuperRoot : new dynamicTree.TreeNode.Type('superroot'),
        Root : new dynamicTree.TreeNode.Type('root')
    };
    this.nodeHeaderTemplateDomId = "list_responsive_collapsible_folders:folders";

    if (this.param.extra && this.param.extra.isActiveThemeFolder) {
        this.param.cssClass = this.ACTIVE_THEME_CLASS;
    }
};
if(dynamicTree.TreeNode) {
    dynamicTree.RepositoryFolder.prototype = deepClone(dynamicTree.TreeNode.prototype);
}

dynamicTree.RepositoryFolder.addVar('ACTIVE_THEME_CLASS', "activeTheme");

dynamicTree.RepositoryFolder.addMethod('isParent', function() {
    return this.param.type == this.Types.Folder.name ||
           this.param.type == this.Types.SuperRoot.name ||
           this.param.type == this.Types.Root.name;
});

dynamicTree.RepositoryFolder.addMethod('isSuperRoot', function() {
   return this.param.type == this.Types.SuperRoot.name;
});

dynamicTree.RepositoryFolder.addMethod('isPublic', function() {
   return this.param.id === 'public';
});

dynamicTree.createRepositoryTree = function(id, options) {
    // Creating of TreeSupport instance and replacing its method for repository usage.
    var orgMode = (options.organizationId != null && options.organizationId !== "");

    //enable override for showing root node.
    if((options.bShowRoot ==  null)){
        options.bShowRoot = !orgMode;
    }

    if (!options.nodeClass) {
        options.nodeClass = dynamicTree.RepositoryFolder;
    }

    options.templateDomId = "list_responsive_collapsible_folders";

    var tree = new dynamicTree.TreeSupport(id, options);

    tree.organizationId = options.organizationId;
    tree.publicFolderUri = options.publicFolderUri;
    tree.orgMode = orgMode;

    tree.modifyRootObject = function (rootObj, isChildrenCallback, parentNode) {
        var updatedRootObj;
        if (isChildrenCallback) {
            updatedRootObj = [];
            for (var i=0; i<rootObj.length; i++) {
                if (rootObj[i].uri != this.publicFolderUri || (!parentNode || parentNode.param.uri == "/")) {
                    updatedRootObj.push(rootObj[i]);
                }
            }
        } else {
            if (this.orgMode && rootObj.children != null) {
                var publicFolder;
                var ch = rootObj.children;
                var newCh = [];
                for (var i=0; i<ch.length; i++) {
                    if (ch[i].uri == this.publicFolderUri) {
                        publicFolder = ch[i];
                    } else {
                        newCh.push(ch[i]);
                    }
                }
                rootObj.children = newCh;

                updatedRootObj = {
                    type: 'superroot',
                    label: '',
                    extra: {}
                };

                updatedRootObj.children = [rootObj, publicFolder];
            } else {
                updatedRootObj = rootObj;
            }
        }
        return updatedRootObj;
    };

    tree.getRootNode = function() {
        var rootNode = this.rootNode;

        if (rootNode.isSuperRoot()) {
            return (rootNode.childs[0].isPublic()) ? rootNode.childs[1] : rootNode.childs[0];
        }

        return rootNode;
    };

    tree.findNodeChildByMetaName = function(node, name) {
        if (node.isSuperRoot()) {
            var publicNode;
            var rootNode;
            if (node.childs[0].isPublic()) {
                publicNode = node.childs[0];
                rootNode = node.childs[1];
            } else {
                publicNode = node.childs[1];
                rootNode = node.childs[0];
            }

            if (name === 'public') {
                return publicNode;
            } else {
                node = rootNode;
            }
        }

        if (node.hasChilds()) {
            for (var i = 0; i < node.childs.length; i++) {
                if (node.childs[i].param.id == name) {
                    return node.childs[i];
                }
            }
        }

        if (name == 'public' && node.parent && node.parent.isSuperRoot()) {
            return tree.findNodeChildByMetaName(node.parent, name);
        }

        return null;
    };

    if(isIPad()){
        var scrollElement = $(id);
        tree.touchController = new TouchController(scrollElement, scrollElement.up(1), {
            absolute: true
        });
    }

    return tree;
};