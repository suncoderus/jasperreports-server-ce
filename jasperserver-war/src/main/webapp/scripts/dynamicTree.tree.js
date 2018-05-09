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

/* global layoutModule, console, isNotNullORUndefined, cloneCustomAttributes */

/**
 * Main namespace for all tree related things
 */
var dynamicTree = {
    /**
     * tree map, use to find/store trees, ex: trees['myId']
     * Tree constructor registers the tree automatically in here
     */
    trees: {},

    /**
     * node index map, helps to find any node by id
     * TreeNode constructor registers the node automatically in here
     */
    nodes: {},

    /**
     * Current edited node
     */
    treeNodeEdited: null,

    /**
     * If true all changes of node title will be ignored
     */
    editaborted: false,

    /**
     *  The identifier of active tree.
     */
    activeTreeId: null,

    /*
     * The Tree Object.
     *
     * @param id {String} - tree id.
     * @param options {JSON Object} - Set of configuration options for tree :
     * <ul>
     * <li>root {Tree Node} - Root Node</li>
     * <li>bShowRoot {Boolean} - true if you want to show tree root node, false otherwise</li>
     * <li>handleNodeOnDblclick {Boolean} - false if you don't want to handle node on dblclick</li>
     * <li>multiSelectEnabled {Boolean} - true if you want select more then one node at the same time</li>
     * <li>showAllNodesOnStartup {Boolean} - Shows/Hides subnodes on startup</li>
     * <li>treeClassName {String} - a css class to override dynamicTree.Tree.DEFAULT_TREE_CLASS_NAME</li>
     * <li>dragPattern {String} - cssStyle pattern for which dragging is enabled</li>
     * <li>dropClasses {Array} - array of class names which can be dropped onto a tree node</li>
     * <li>dragClasses {String} - string with class names which will be used for modifying ghost of the tree node while dragging</li>
     * <li>selectOnMousedown (Boolean)</li> - should selection occure on mousedown (otherwise its on mouseup)</li>
     * <li>regionID (String)</li> - where is this tree located (e.g. AVAILABLE_FIELDS)
     * </ul>
     */
    Tree: function(id, options) {
        this.id = id;
        if (options) {
            this.setRootNode(options.root);
            this.bShowRoot = !!options.bShowRoot;
            this.handleNodeOnDblclick = options.handleNodeOnDblclick !== undefined ? options.handleNodeOnDblclick : true;
            this.multiSelectEnabled = !!options.multiSelectEnabled;
            this.showAllNodesOnStartup = !!options.showAllNodesOnStartup;
            this.treeClassName = options.treeClassName ? options.treeClassName : "";
            this.dragPattern = options.dragPattern;
            this.dragClasses = options.dragClasses;
            this.dropClasses = options.dropClasses;
            this.selectOnMousedown = options.selectOnMousedown;
            this.regionID = options.regionID;
            this.scroll = options.scroll;
            this.templateDomId = (options.templateDomId) ? options.templateDomId : this.DEFAULT_TREE_TEMPLATE_ID;
        } else {
            this.templateDomId = this.DEFAULT_TREE_TEMPLATE_ID;
            this.handleNodeOnDblclick = true;
        }


        this.stateObject = {};

        this.sortNodes = true;
        this.sorters = [this.sortByOrder, this.sortByName];

        this.selectedNodes = [];
        this.TREE_NN_ITEMS_SELECTED = "#{count} items selected";

        // self-indexing
        dynamicTree.trees[this.id] = this;

        this._createFromTemplate();    
        this.refreshStyle();
        this._registerEvents();
        this._registerCustomScroll();
    },

    /**
     * Global node id counter.
     */
    getNextId: function() {
        var nextId = 1; // private static var
        return function() {
            return nextId++;
        }
    }(),

    /**
     * Returns the lasts tree which was active by mouse down event on tree container.
     */
    getActiveTree: function() {
        return dynamicTree.trees[dynamicTree.activeTreeId];
    },

    /**
     * Returns tree node for the specified node identifier.
     *
     * @param nodeID the identifier of the node.
     */
    getTreeNode: function(nodeID) {
        return dynamicTree.nodes[nodeID];
    },

    /**
     * Returns the value from the localStorage by the specified name.
     *
     * @param name the name.
     */
    getStorageVal: function(name) {
        var tree = JSON.parse(window.localStorage.getItem("dynamicTree")) || {};
        return tree[name] || null;
    },

    /**
     * Stores the value in the localStorage using specified name.
     *
     * @param name the name.
     * @param value the value.
     */
    setStorageVal: function (name, value) {
        var localStorage = window.localStorage;
        var tree = JSON.parse(localStorage.getItem("dynamicTree")) || {};

        tree[name] = value;

        try {
            localStorage.setItem('dynamicTree', JSON.stringify(tree));
        } catch(e) {
            // TODO: use our logger
            window.console && console.log(e);
        }
    },

    _templateHash: {}
};

/**
 *  Name(s) of CSS class(es) for the default tree.
 */
dynamicTree.Tree.addVar('DEFAULT_TREE_TEMPLATE_ID', "list_responsive_collapsible");

/**
 * The name of the CSS class which is used to hide the root.
 */
dynamicTree.Tree.addVar('HIDE_ROOT_CLASS_NAME', "hideRoot");

/**
 * Returns the identifier of the tree.
 */
dynamicTree.Tree.addMethod('getId', function() { return this.id; });

/**
 * Returns the tree DOM element.
 */
dynamicTree.Tree.addMethod('_getElement', function() {
    if (!this._element) {
        this._element = $(this.id);
    }

    return this._element;
});

/**
 * Returns the tree DOM element.
 */
dynamicTree.Tree.addMethod('_createFromTemplate', function() {
    if (this._getElement()) {
		this._getElement().insert({
			after: this._getTemplateElement(this._getElement())
		});
		
		this._getElement().remove();
		this._element = null;
		
		this._getElement().update();
	}	

    //disableSelectionWithoutCursorStyle(this._getElement());
});

/**
 *
 */
dynamicTree.Tree.addMethod('_registerCustomScroll', function() {

    if (!this.scroll && this._getElement()) {
		var scrollBar = this._getElement().up(layoutModule.SWIPE_SCROLL_PATTERN);
        if(scrollBar) {
            var scroll = layoutModule.scrolls.get(scrollBar.identify());
            scroll && (this.scroll = scroll);
//            if (this.scroll /*&& this.scroll.tree != this*/) {
//                this.scroll.tree = this;
//
//                var onScrollStart = this.scroll.options.onScrollStart;
//
//                this.scroll.options.onScrollStart = function(e) {
//                    onScrollStart && onScrollStart();
//                    this.tree.revertSelection(e);
//                }
//            }
        }
	}

    //disableSelectionWithoutCursorStyle(this._getElement());
});

/**
 *  Sets root node.
 *
 * @param rootNode the root node to be set.
 */
dynamicTree.Tree.addMethod('setRootNode', function(rootNode) {
    this.rootNode = rootNode;

    if (this.rootNode) {
        this.rootNode.treeId = this.id;
    }
});

/**
 * Returns the root node.
 */
dynamicTree.Tree.addMethod('getRootNode', function() {
    return this.rootNode;
});

/**
 * Refresh the style of the tree.
 */
dynamicTree.Tree.addMethod('refreshStyle', function() {
    var element = this._getElement();

    if(element.templateClassName) { element.className = element.templateClassName; }
    
    this.treeClassName && element.addClassName(this.treeClassName);

    if (this.bShowRoot) {
        element.removeClassName(this.HIDE_ROOT_CLASS_NAME);
    } else {
        element.addClassName(this.HIDE_ROOT_CLASS_NAME);
    }
});

/**
 *  Resets the list of selected nodes.
 */
dynamicTree.Tree.addMethod('resetSelected', function() {
    this._prevSelectedNodes = this.selectedNodes.clone();
    this.selectedNodes = [];
});

dynamicTree.Tree.addMethod('revertSelection', function(evt) {
    //console.log("Revert");
    var nodes = this._prevSelectedNodes.clone();
    nodes = nodes.concat(this.selectedNodes);

    this.selectedNodes = this._prevSelectedNodes.clone();

    for (var i = 0; i < nodes.length; i++) {
        nodes[i].refreshStyle();
    }
});

/**
 * Returns the first selected node from the list of selected nodes. If no nodes were selected then null returned.
 */
dynamicTree.Tree.addMethod('getSelectedNode', function() {
    return (this.selectedNodes.length === 0) ? null : this.selectedNodes[0];
});

/**
 * Adds the specified node to the list of selected nodes.
 *
 * @param node the node.
 */
dynamicTree.Tree.addMethod('addNodeToSelected', function(node) {
    this.selectedNodes.push(node);
    this._prevSelectedNodes = this.selectedNodes.clone();
});

/**
 * Removes the specified node from the list of selected nodes.
 *
 * @param the node.
 */
dynamicTree.Tree.addMethod('removeNodeFromSelected', function(node) {
    for (var i = 0; i < this.selectedNodes.length; i++) {
        if (this.selectedNodes[i] == node) {
            this._prevSelectedNodes = this.selectedNodes.clone();
            this.selectedNodes.splice(i, 1);
            return;
        }
    }
});

/**
 * Returns <code>true</code> if specified node is selected, <code>false</code> otherwise.
 *
 * @param node the node.
 */
dynamicTree.Tree.addMethod('isNodeSelected', function(node) {
    var len = this.selectedNodes && this.selectedNodes.length;
    if (len) {
        for (var i = 0; i < len; i++) {
            if (this.selectedNodes[i] == node) {
                return true;
            }
        }
    }

    return false;
});

/**
 * Resorts the sub-tree for the specified parent node.
 *
 * @param parentNode the parent node.
 */
dynamicTree.Tree.addMethod('resortSubtree', function(parentNode) {
    if (parentNode) {
        parentNode.resortChilds();
        for (var i = 0; i < parentNode.childs.length; i++) {
            this.resortSubtree(parentNode.childs[i]);
        }
    }
});

/**
 * Resorts all the tree.
 */
dynamicTree.Tree.addMethod('resortTree', function() {
    if (this.sortNodes) {
        this.resortSubtree(this.rootNode);
    }
});

/**
 * Reads states from the localStorage.
 */
dynamicTree.Tree.addMethod('readStates', function() {
    var states = dynamicTree.getStorageVal('tree' + this.id);
    if (states) {
        for(var key in states){
            this.stateObject[key] = states[key];
        }
    }
});

/**
 * Returns the state of the specified node.
 *
 * @param nodeID the identifier of the state to be returned.
 */
dynamicTree.Tree.addMethod('getState', function(nodeID) {
    var state;
    var stateObject = this.stateObject;

    if(stateObject[nodeID]){
        state = stateObject[nodeID];
        if (state === null || state === '') {
            state = dynamicTree.TreeNode.State.CLOSED;
        }
        return state;
    }

    return dynamicTree.TreeNode.State.CLOSED;
});

/**
 *  Writes new state of the specified node to the localStorage.
 *
 * @param nodeID the identifier of the node.
 * @param newstate is a new state.
 */
dynamicTree.Tree.addMethod('writeStates', function(nodeID, newstate) {
    var obj = {};
    var stateObject = this.stateObject;

    for(var key in stateObject){
        obj[key] = stateObject[key];
    }

    stateObject[nodeID] = newstate;
    if (newstate != null) {
        obj[nodeID] = newstate;
    }

    dynamicTree.setStorageVal('tree' + this.id, obj);
});

/**
 * Resets states.
 */
dynamicTree.Tree.addMethod('resetStates', function() {
    this.stateObject = {};
    dynamicTree.setStorageVal('tree' + this.id,'');
});

/*
 * Comparer for sorting.
 * Calls sorters on order they appear in tree.sorters array until sorter returns non-zero value
 * @param {Object} node1 first node
 * @param {Object} node2 second node
 * @returns negative number if node1<node2, positive number if node1>node2, 0 otherwise
 */
dynamicTree.Tree.addMethod('comparer', function (node1, node2) {
    var i, k;
    if (this.sorters && this.sorters.length) {
        for (i = 0; i < this.sorters.length; i++) {
            k = this.sorters[i](node1, node2);
            if (k !== 0) {
                return k;
            }
        }
    }
    return 0;
});

/**
 * Renders tree into a container.
 */
dynamicTree.Tree.addMethod('renderTree', function() {
    this.readStates();

    this.stopWaiting();
    this.refreshStyle();

    /* Setting event handlers */
    var treeContainer = this._getElement();

    if (this.rootNode) {
        this.writeStates(this.rootNode.id, dynamicTree.TreeNode.State.OPEN);

        this.rootNode.showNode();
        this.rootNode.render(treeContainer);
        this.refreshScroll();
    }
	//TODO Removed for now - lack of available event causes issues    
	//var selected = this.getSelectedNode() || (this.bShowRoot ? this.rootNode : this.rootNode.getFirstChild()) ;
	//selected && selected.select();
});

dynamicTree.Tree.addMethod('refreshScroll', function(delay) {
    if (delay) {
        setTimeout(function(){
            this.scroll && this.scroll.refresh();
        }.bind(this), delay);
    } else {
        this.scroll && this.scroll.refresh();
    }
});

/**
 *
 */
dynamicTree.Tree.addMethod('binarySearchOfNode', function(nodes, node) {
    var low = 0;
    var high = nodes.length - 1;

    while (low <= high) {
        var mid = Math.round((low + high) / 2);
        var midNode = nodes[mid];

        if (this.comparer(midNode, node) < 0) {
            low = mid + 1;
        } else if (this.comparer(midNode, node) > 0) {
            high = mid - 1;
        } else {
            return mid; // key found
        }
    }

    return -(low + 1);  // key not found.
});

/**
 * Deselects all the nodes.
 */
dynamicTree.Tree.addMethod('_deselectAllNodes', function(event) {
    if (this.selectedNodes.length > 0) {
        var nodes = this.selectedNodes.clone();

        this.resetSelected();

        for (var i = 0; i < nodes.length; i++) {
            nodes[i].refreshStyle();
        }

        if (event) {
            var tree = dynamicTree.trees[this.id];
            tree.fireUnSelectAllEvent(event);
        }
    }
});

dynamicTree.Tree.addMethod('_selectOrEditNode', function(evt, node, ctrlHeld, shiftHeld, isContextMenuBtn, options) {
    var min, max, parent;
    var isContextMenu = node.isSelected() && isContextMenuBtn;
    var isDeselect = this.multiSelectEnabled && node.isSelected() && ctrlHeld && !isContextMenu;
    var isEdit = node.isSelected() && dynamicTree.treeNodeEdited !== node && !this.multiSelectEnabled && !ctrlHeld && !isContextMenu;
    var isDoEndEdit = dynamicTree.treeNodeEdited != null && (isDeselectAll || isDeselect);
    var isRangeSelect = this.multiSelectEnabled && !node.isSelected() && shiftHeld && isNotNullORUndefined(this._lastSelectedNode)
            && this._lastSelectedNode.parent === node.parent;
    var isRangeReduce = this.multiSelectEnabled && node.isSelected() && shiftHeld && isNotNullORUndefined(this._lastSelectedNode)
            && this._lastSelectedNode.parent === node.parent;

    var isDeselectAll = (!this.multiSelectEnabled && !node.isSelected()) || (this.multiSelectEnabled && !ctrlHeld && !node.isSelected());
    var isSelect = !node.isSelected() || (isDeselectAll && this.selectedNodes.length > 1);
    if(!shiftHeld || !this._lastSelectedNode) {
        this._lastSelectedNode = node;
    }

    if (isDeselect) {
        node.deselect(evt);
    }

    if (isDeselectAll) {
        this._deselectAllNodes(evt);
    }

    if (isEdit) {
        node.edit(evt);
        return;
    }

    if (isDoEndEdit) {
        dynamicTree.treeNodeEdited.doEndEdit(evt);
    }

	if(isRangeSelect || isRangeReduce) {
        parent = node.parent;
        var start = parent.childs.indexOf(this._lastSelectedNode);
        var end = parent.childs.indexOf(node);
        min = Math.min(start, end);
        max = Math.max(start, end);
	}

    if (isRangeSelect) {
        if (min > -1) {
            for (var i = min; i <= max; i++) {
                parent.childs[i].select(evt);
            }
        } else {
            parent.childs[max].select(evt);
        }
        return;
    }

    if (isRangeReduce) {
		for (var i = 0; i < min; i++) {
			parent.childs[i].deselect(evt);
		}

		for (var i = max+1; i < parent.childs.length; i++) {
			parent.childs[i].deselect(evt);
		}
        return;
    }

    if (isSelect) {
        node.select(evt, node.nofocus, options);
    }
});

dynamicTree.Tree.addMethod('_deselectOthers', function (evt, node, ctrlHeld, shiftHeld, isContextMenuBtn) {
    var isDeselectOther = (this.multiSelectEnabled && this.selectedNodes.length > 1 && node.isSelected() &&
            !(ctrlHeld || shiftHeld || isContextMenuBtn));

    isDeselectOther && this.selectedNodes.findAll(function(n) { return n != node; }.bind(this)).invoke('deselect', evt);
});

/**
 * find the next node in the tree (ignoring hierachy) and select it
 * @param {Object} node - current node
 */
dynamicTree.Tree.addMethod('_selectNextNode', function (node, event) {
	//recurse up the parent chain until we get a parent with a next sibling
	function getNextUncle(node) {
		node = node.parent;
		if (!node) {
			return null;
		} else if (node.nextSibling) {
			return node.nextSibling;
		}
		return getNextUncle(node);
	}
	var nextNode = (node.isOpen() && node.getFirstChild()) || node.nextSibling || getNextUncle(node);
	nextNode && (node.deselect() && nextNode.select(event));
});

/**
 * find the previous node in the tree (ignoring hierachy) and select it
 * @param {Object} node - current node
 */
dynamicTree.Tree.addMethod('_selectPreviousNode', function (node, event) {
	function getLastVisibleDescendant(node) {
		return (!(node.isOpen() && node.hasChilds()) && node) || getLastVisibleDescendant(node.getLastChild());
	}	
	var prevNode = (node.prevSibling && getLastVisibleDescendant(node.prevSibling)) || node.parent;
	prevNode && (node.deselect() && prevNode.select(event));
});

/**
 * if open go to first child node, otherwise open node
 * @param {Object} node
 */
dynamicTree.Tree.addMethod('_selectInwards', function (node, event) {
	var inNode = node.isOpen() && node.getFirstChild();
	inNode ? (node.deselect() && inNode.select(event)) : node.handleNode(event);
});	 

/**
 * if closed or leaf go to parent, otherwise close node
 * @param {Object} node
 */

dynamicTree.Tree.addMethod('_selectOutwards', function (node, event) {
	if (!node.isHiddenRootNode()) {
		var outNode = node.isOpen() ? null : node.parent;
		outNode ? (node.deselect() && outNode.select(event)) : node.handleNode(event);
	}
});

/*
 * Sorter by order value assigned to nodes.
 * Order has to be a number. Node that has some order is considered to be
 * LESS than node that does not have any order (order=null)
 * @param {Object} node1 first node
 * @param {Object} node2 second node
 * @returns negative number if node1<node2, positive number if node1>node2, 0 otherwise
 */
dynamicTree.Tree.addMethod('sortByOrder', function (node1, node2) {
    var order1 = node1.orderNumber;
    var order2 = node2.orderNumber;
    if (order1 == null && order2 == null) {
        return 0;
    }
    if (order1 == null) {
        return 1;
    }
    if (order2 == null) {
        return -1;
    }
    return order1 - order2;
});

/*
 * Sorter alphabetically by node names
 * @param {Object} node1 first node
 * @param {Object} node2 second node
 * @returns negative number if node1<node2, positive number if node1>node2, 0 otherwise
 */
dynamicTree.Tree.addMethod('sortByName', function (node1, node2) {
    var n1 = node1.name.toLowerCase();
    var n2 = node2.name.toLowerCase();
    return n1 > n2 ? 1 : (n1 < n2 ? -1 : 0);
});

/**
 * DOM identifier of the tree wait template.
 */
dynamicTree.Tree.addVar('TREE_WAIT_TEMPLATE_DOM_ID', "list_responsive_collapsible:loading");

/**
 * Makes visual effect of wait for tree loading.
 */
dynamicTree.Tree.addMethod('wait', function() {
    $(this.id).update($(this.TREE_WAIT_TEMPLATE_DOM_ID).cloneNode(true));
});

/**
 * Removes visual effect of wait for tree loading.
 */
dynamicTree.Tree.addMethod('stopWaiting', function() {
    $(this.id).update("");
});

dynamicTree.Tree.addMethod('_getTemplateElement', function(currentElement) {
    var id = this.templateDomId;
     /**
      * @see comment in _getHeaderTemplateElement in dynamicTree.treenode.js for an explanation of the commented below
      */
//    if (!dynamicTree._templateHash[id]) {
//        dynamicTree._templateHash[id] = $(id);
//    }

//    var clone = dynamicTree._templateHash[id].cloneNode(true);
    var clone = $(id).cloneNode(true);

    clone.writeAttribute("id", this.getId());
    clone.templateId = id;
    clone.templateClassName = clone.className;

    cloneCustomAttributes(currentElement, clone);

    return clone;
});
