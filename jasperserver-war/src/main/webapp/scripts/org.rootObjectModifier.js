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

/* global organizationId, publicFolderUri */

// To use this script organizationId and publicFolderUri variables should be predefined and initialized.
var rom_orgMode = null;
var rom_showRoot = null;
function rom_init() {
    rom_orgMode = organizationId !== null && organizationId !== "";
    rom_showRoot = !rom_orgMode;
}
var rom_repositoryModifier = function (rootObj, isChildrenCallback) {
    var updatedRootObj;
    if (isChildrenCallback) {
        updatedRootObj = [];
        for (var i=0; i<rootObj.length; i++) {
            if (rootObj[i].uri != publicFolderUri) {
                updatedRootObj.push(rootObj[i]);
            }
        }
    } else {
        if (rom_orgMode && rootObj.children != null) {
            var publicFolder;
            var ch = rootObj.children;
            var newCh = [];
            for (var i=0; i<ch.length; i++) {
                if (ch[i].uri == publicFolderUri) {
                    publicFolder = ch[i];
                } else {
                    newCh.push(ch[i]);
                }
            }
            rootObj.children = newCh;

            updatedRootObj = {type:'superroot',label:'',extra:{}};
            updatedRootObj.children = [rootObj, publicFolder];
        } else {
            updatedRootObj = rootObj;
        }
    }
    return updatedRootObj;
};
var rom_getRootNode = function() {
    var rootNode = this.tree.rootNode;

    if (rootNode.param && rootNode.param.type == 'superroot') {
        if (rootNode.childs[0].param.id == 'public') {
            rootNode = rootNode.childs[1];
        } else {
            rootNode = rootNode.childs[0];
        }
    }

    return rootNode;
};
var rom_findNodeChildByMetaName = function(node, name) {
    if (node.param && node.param.type == 'superroot') {
        var publicNode;
        var rootNode;
        if (node.childs[0].param.id == 'public') {
            publicNode = node.childs[0];
            rootNode = node.childs[1];
        } else {
            publicNode = node.childs[1];
            rootNode = node.childs[0];
        }

        if (name == 'public') {
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
    return null;
};
