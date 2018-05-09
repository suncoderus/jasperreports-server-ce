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

/* global dynamicTree, isArray, layoutModule, cancelEventBubbling, isIPad */

/*
 * The TreeNode Object
 *
 * @param options {JSON Object} - Set of configuration options for tree node :
 * <ul>
 * <li>name {String} - The title of this node</li>
 * <li>param {JSON Object} - A parameter, this can be pretty much anything. (eg. an array with information).</li>
 * <li>orderNumber {String} - If one is given the nodes will be sorted by this (else they`ll be sorted alphabetically (If sorting is on).</li>
 * </ul>
 */
dynamicTree.TreeNode = function(options) {
    this.id = dynamicTree.getNextId();
    this.treeId = null; // tree sets it
    this.name = (options.name != null ? options.name : this.DEFAULT_NAME);
    this.param = (options.param != null ? options.param : {});
    this.orderNumber = (options.orderNumber != null ? options.orderNumber : null );
    this.childs = [];
    this.parent = null;

    // default types
    // used to separate folders from leaves
    this.Types = {
        Folder : new dynamicTree.TreeNode.Type(this.FOLDER_TYPE_NAME)
    };

    this.isloaded = false;
    this.delayedRendering = true;

    this.haschilds = false;
    this.editable = false;

    this.isWaiting = false;
    this.hidden = false;

    this.isDropTarget = false;

    // self-indexing
    dynamicTree.nodes[this.id] = this;
}
/**
 *
 * @param name
 * @param cssClassName
 * @param templateDomId
 */
dynamicTree.TreeNode.Type = function (name, options) {
    this.name = name;
    if (options) {
        this.cssClassName = options.cssClassName;
        this.templateDomId = options.templateDomId;
    }
}
/**
 *
 */
dynamicTree.TreeNode.State = {
    OPEN : 'open',
    CLOSED : 'closed'
};

dynamicTree.TreeNode.addVar('FOLDER_TYPE_NAME', "com.jaspersoft.jasperserver.api.metadata.common.domain.Folder");
dynamicTree.TreeNode.addVar('DEFAULT_NAME', "unset name");

dynamicTree.TreeNode.addVar('NODE_ID_PREFIX', "node");
dynamicTree.TreeNode.addVar('SUB_NODE_ID_SUFFIX', "sub");
dynamicTree.TreeNode.addVar('HANDLER_ID_PREFIX', "handler");

dynamicTree.TreeNode.
        addVar('NODE_CLASS_NAME', "node").
        addVar('LEAF_CLASS_NAME', "leaf").
        addVar('OPEN_CLASS_NAME', "open").
        addVar('CLOSED_CLASS_NAME', "closed").
        addVar('SELECTED_CLASS_NAME', "selected").
        addVar('LOADING_CLASS_NAME', "loading").
        addVar('ROOTS_CLASS_NAME', "roots");

//
// Templates for tree UI
//
dynamicTree.TreeNode.addVar('nodeHeaderTemplateDomId', "list_responsive_collapsible:leaf");
dynamicTree.TreeNode.addVar('nodeFooterTemplateDomId', "list_responsive_collapsible");
dynamicTree.TreeNode.addVar('nodeInputTemplateDomId', "list_responsive_collapsible:input");

//selectable - defaults to false
dynamicTree.TreeNode.addVar('isSelectable', false);
/**
 * Gets ID of the tree to which this node belongs.
 *
 * @return {String}
 */
dynamicTree.TreeNode.addMethod('getTreeId', function() { return this.treeId || (this.parent && this.parent.getTreeId()); });
/**
 * Gets state of the node, that is saved in cookies.
 * All possible values of the state are defined in {@see dynamicTree.TreeNode.State}.
 *
 * @return {String} -
 */
dynamicTree.TreeNode.addMethod('getState', function() {
    return dynamicTree.trees[this.getTreeId()].getState(this.id);
});
/**
 * Use this method to distinguish node from leaf. Override it if you have changed supported Types by this node.
 */
dynamicTree.TreeNode.addMethod('isParent', function() { return this.param.type == this.Types.Folder.name; });
/**
 * Adds child to the node. New child will be shown if disabled delayed rendering.
 *
 * @param childNode {dynamicTree.TreeNode} - the node which should be added as child
 */
dynamicTree.TreeNode.addMethod('addChild', function(child) {
    if (!this.isParent()) {
        return;
    }

    var lastNode = this.childs[this.childs.length - 1];
    if (lastNode) {
        lastNode.nextSibling = child;
        child.prevSibling = lastNode;
    }

    this.childs.push(child);
    child.parent = this;

    this.resortChilds();

    if (this.delayedRendering) {
        return;
    }

    child.showNode();
    child.render(this._getChildrenElement(), child.nextSibling);
});
/**
 * Removes the node from the children of this node
 *
 * @param childNode {dynamicTree.TreeNode}
 */
dynamicTree.TreeNode.addMethod('removeChild', function(child) {
    for (var i = 0; i < this.childs.length; i++) {
        if (this.childs[i] == child) {
            this.childs.splice(i, 1);
            break;
        }
    }

    child.deselect();

    if (this.delayedRendering) {
        return;
    }

    var element = child._getElement();
    if (element) {
        element.remove();
    }
});
/**
 * Resort all children of this node and updates prevSibling and nextSibling
 */
dynamicTree.TreeNode.addMethod('resortChilds', function() {
    var treeId = this.getTreeId();
    var tree = dynamicTree.trees[treeId];

    if (tree && tree.sortNodes && isArray(this.childs)) {
        this.childs.sort(function(a, b) {return tree.comparer(a, b)});

        var count = this.childs.length;
        while(count--) {
            var node = this.childs[count];
            node.prevSibling = this.childs[count - 1];
            node.nextSibling = this.childs[count + 1];
        }
    }
});
/**
 * Removes all children of this node
 */
dynamicTree.TreeNode.addMethod('resetChilds', function() { this.childs = []; });
/**
 * Set to true if node has children.
 *
 * @param {Boolean}
 */
dynamicTree.TreeNode.addMethod('setHasChilds', function(hasChilds) { this.haschilds = hasChilds; });
/**
 * Returns true if node has children.
 *
 * @return {Boolean}
 */
dynamicTree.TreeNode.addMethod('hasChilds', function() {
    if (this.haschilds) {
        return true;
    }
    return (this.getChildCount() > 0);
});
/**
 * Gets count of children of the node.
 *
 * @return {Number}
 */
dynamicTree.TreeNode.addMethod('getChildCount', function() {
    return this.childs.length;
});
/**
 * Gets first child of the node.
 *
 * @return {dynamicTree.TreeNode}
 */
dynamicTree.TreeNode.addMethod('getFirstChild', function() {
    if (this.hasChilds()) {
        return this.childs[0];
    }
    return null;
});
/**
 * Gets last child of the node.
 *
 * @return {dynamicTree.TreeNode}
 */
dynamicTree.TreeNode.addMethod('getLastChild', function() {
    if (this.hasChilds()) {
        return this.childs[this.childs.length-1];
    }
    return null;
});

dynamicTree.TreeNode.addMethod('_getElement', function(container) {
    if (!this._element) {
        this._element = $(this.NODE_ID_PREFIX + this.id);
    }
    return this._element;
});

dynamicTree.TreeNode.addMethod('_getTitle', function() {
    var titleHolder = this._getElement().childElements()[0];
    titleHolder.cleanWhitespace();

    var title = titleHolder.childNodes[titleHolder.childNodes.length - 1];

    if (title.nodeName !== "#text") {
        title = document.createTextNode("");
        titleHolder.appendChild(title);
    }
    return title;
});

dynamicTree.TreeNode.addMethod('_getTitleInputElement', function() {
    return $(this._getElement().getElementsByTagName('input')[0]);
});

dynamicTree.TreeNode.addMethod('_getChildrenElement', function() {
    if (!this._childrenElement) {
        this._childrenElement = $(this.NODE_ID_PREFIX + this.id + this.SUB_NODE_ID_SUFFIX);
    }
    return this._childrenElement;
});
/**
 * Returns true if the node is open.
 *
 * @return {Boolean}
 */
dynamicTree.TreeNode.addMethod('isOpen', function() {
    return this.getState() === dynamicTree.TreeNode.State.OPEN;
});
/**
 * Change display name of the node.
 *
 * @param newName {String}
 */
dynamicTree.TreeNode.addMethod('changeName', function(newName) {
    this.name = newName;
    this._getTitle().data = this.name;
});
/**
 * Gets the type of the node. All possible value ara defined in Types.
 *
 * @return {dynamicTree.TreeNode.Type}
 */
dynamicTree.TreeNode.addMethod('getType', function() {
    for (var type in this.Types) {
        if (this.param.type === this.Types[type].name) {
            return this.Types[type];
        }
    }
    return undefined;
});

/**
 * Updates CSS classes of markup of the node .
 */
dynamicTree.TreeNode.addMethod('refreshStyle', function(element) {
    element = $(element) || this._getElement();

    if (!element) { return; }

    if(element.templateClassName) {
		 element.className = element.templateClassName; 
	}

    if (this.isParent()) {
        element.addClassName(this.NODE_CLASS_NAME).removeClassName(this.LEAF_CLASS_NAME);

        if (!this.isWaiting) {
            if (this.isOpen()) {
                element.addClassName(this.OPEN_CLASS_NAME).removeClassName(this.CLOSED_CLASS_NAME);
            } else {
                element.addClassName(this.CLOSED_CLASS_NAME).removeClassName(this.OPEN_CLASS_NAME);
            }
        }
    } else {
        element.addClassName(this.LEAF_CLASS_NAME).removeClassName(this.NODE_CLASS_NAME);
    }

    if (this.isWaiting) {
        element.addClassName(this.LOADING_CLASS_NAME);
    } else {
        element.removeClassName(this.LOADING_CLASS_NAME);
    }

    if (this.isSelected()) {
        element.addClassName(this.SELECTED_CLASS_NAME);
    } else {
        element.removeClassName(this.SELECTED_CLASS_NAME);
    }

    if (this.hidden) {
        element.addClassName(layoutModule.HIDDEN_CLASS);
    } else {
        element.removeClassName(layoutModule.HIDDEN_CLASS);
    }

    if (this.param.cssClass) { element.addClassName(this.param.cssClass); }

    var type = this.getType();
    if (type && type.cssClassName) { element.addClassName(type.cssClassName); }

    var subElement = element.down();
    this.isDropTarget && subElement && subElement.addClassName(layoutModule.DROP_TARGET_CLASS);
    !this.isDropTarget && subElement && subElement.removeClassName(layoutModule.DROP_TARGET_CLASS);
});

dynamicTree.TreeNode.addMethod('_createNode', function() {
    var id = this.id;
    var tree = dynamicTree.trees[this.getTreeId()];

    var templH = this._getHeaderTemplateElement();
    templH.id = this.NODE_ID_PREFIX + id;
    templH.tabIndex = -1;
    this.refreshStyle(templH);

    this.treeId = tree.id;
    
    // DOM element link on this tree node
    templH.treeNode = this;

    var wrapper = templH.childElements()[0];
    wrapper.insert(xssUtil.escape(this.name, {softHTMLEscape: true, whiteList: ['a']}));
    if (this.tooltip != null && this.tooltip.length > 0) {
        wrapper.title = this.tooltip;
    }

    wrapper.childElements().each(function(img, index) {
        if (index === 0) {
            img.id = this.HANDLER_ID_PREFIX + id;
        }

        var tip = this.iconTooltip;
        if (tip) {
            img.title = (isArray(tip)) ? tip[index] : tip;
        }
        //    img.onselectstart = function() { return false; }
    }.bind(this));

    this._element = templH;
});

dynamicTree.TreeNode.addMethod('_createNodeChildren', function() {
    var templF = this._getFooterTemplateElement();
    templF.id = this.NODE_ID_PREFIX + this.id + this.SUB_NODE_ID_SUFFIX ;

    this._childrenElement = templF;
});

/**
 * Shows the given node, and subnodes.
 */
dynamicTree.TreeNode.addMethod('showNode', function(container) {
    var tree = dynamicTree.trees[this.getTreeId()];
    this._createNode();

    if (this.isParent()) {
        var showChildren = (this.isOpen() || tree.showAllNodesOnStartup);

        if (showChildren) {
            this._createNodeChildren();

            for(var z = 0; z < this.getChildCount(); z++) {
                this.childs[z].showNode(this._getChildrenElement());
            }

            this.delayedRendering = false;
            tree.fireOpenEvent(this);
        }

    }
    this.render(container);
});
/**
 * Adds the node template to the DOM.
 */
dynamicTree.TreeNode.addMethod('render', function(container, beforeNode) {
    if (Object.isUndefined(container)) {
        return;
    }

    var element = $(container);

    if (element) {
        if (this._getChildrenElement()) {
            this._getElement().insert(this._getChildrenElement())
        }
        if (beforeNode) {
            element.insert(this._getElement(), {before : beforeNode._getElement()});
        } else {
            element.insert(this._getElement());
        }
    }
});

dynamicTree.TreeNode.addMethod('_renderChildren', function() {
    var element = this._getElement();

    if (element && this._getChildrenElement()) {
        element.insert(this._getChildrenElement())
    }
});
/**
 *
 */
dynamicTree.TreeNode.addMethod('refreshNode', function() {
    this.refreshStyle();

    if (this.isParent() && this.isOpen() && this.isloaded) {
        if (this.delayedRendering) {
            this._createNodeChildren();
            this._renderChildren();
        } else {
            this._getChildrenElement().update("");
        }

        for (var z = 0; z < this.getChildCount(); z++) {
            this.childs[z].showNode(this._getChildrenElement());
        }

        this.delayedRendering = false;
    }
    var tree = dynamicTree.trees[this.getTreeId()];

    tree.refreshScroll();
});
/**
 * Shows wait icon on the node.
 */
dynamicTree.TreeNode.addMethod('wait', function() {
    this.isWaiting = true;
    this.refreshStyle();
});
/**
 * Hides wait icon on the node.
 */
dynamicTree.TreeNode.addMethod('stopWaiting', function() {
    this.isWaiting = false;
    this.refreshStyle();
});
/**
 * Am I a root node and are we hiding root nodes
 *
 * @return {Boolean}
 */
dynamicTree.TreeNode.addMethod('isHiddenRootNode', function() {
    var tree = dynamicTree.trees[this.getTreeId()];
	return (tree.rootNode == this) && !tree.bShowRoot
});
/**
 * Deselect this node in the tree.
 *
 * @return {Boolean}
 */
dynamicTree.TreeNode.addMethod('deselect', function(event) {
    var tree = dynamicTree.trees[this.getTreeId()];
    if (tree && this.isSelected()) {

        tree.removeNodeFromSelected(this);
        this.refreshStyle();

        if (event) {
            tree.fireUnSelectEvent(this, event);
        }
        return true;
    } else {
        return false;
    }
});
/**
 * Select this node in the tree.
 *
 * @return {Boolean}
 */
dynamicTree.TreeNode.addMethod('select', function(event, focus, options) {
    options = options || {};

    !focus && this.focus(); // Commented out to fix bug http://bugzilla.jaspersoft.com/show_bug.cgi?id=19047
    if (!this.isSelected()) {
        var tree = dynamicTree.trees[this.getTreeId()];

        tree.addNodeToSelected(this);
        this.refreshStyle();

        !options.silent && tree.fireSelectEvent(this, event);
        return true;
    } else {
        return false;
    }
});
/**
 * focus on this node's element
 */
dynamicTree.TreeNode.addMethod('focus', function() {
    var self = this;
    if (!isIPad() && this._getElement()) {
        setTimeout(function() {
            self._getElement().focus()
        }, 100);
    }
});
/**
 * Returns true if this node is selected in the tree.
 *
 * @return {Boolean}
 */
dynamicTree.TreeNode.addMethod('isSelected', function() {
    var tree = dynamicTree.trees[this.getTreeId()];
    return tree && tree.isNodeSelected(this);
});

dynamicTree.TreeNode.addMethod('_removeTitle', function() {
    var title = this._getTitle();
    title.data = "";
    $(title.parentNode).cleanWhitespace();
});
/**
 * Begins inline edit of the node.
 *
 */
dynamicTree.TreeNode.addMethod('edit', function(evt) {
    if (this.editable) {
        if (dynamicTree.treeNodeEdited == this) {
            return;
        }

        dynamicTree.treeNodeEdited = this;

        var obj = this;
        var oldName = this.name;
        var titleHolder = $(this._getTitle().parentNode);
        var input = this._getInputTemplateElement();

        this._getTitle().data = "";
        titleHolder.cleanWhitespace();
        titleHolder.insert(input);

        input.value = xssUtil.unescape(this.name);
        input.focus();
        input.select(evt);

        input.onclick = function(e) {cancelEventBubbling(e)};
        input.ondblclick = function(e) {cancelEventBubbling(e)};
        input.onmousedown = function(e) {cancelEventBubbling(e)};
        input.onmouseup = function(e) {cancelEventBubbling(e)};

        input.onkeydown = function(evt) {
            var e = (window.event) ? window.event : evt;
            if (e.keyCode == 13) {
                input.onblur = null;
                this.doEndEdit(e);
            } else if(e.keyCode == 27) {
                input.onblur = null;
                input.value = oldName;
                this.doEndEdit(e);
            }
        }.bindAsEventListener(this);

        input.onblur = function (event) {
            this.doEndEdit(event);
        }.bindAsEventListener(this);

        dynamicTree.trees[this.getTreeId()].fireStartEditEvent(this, input);
    }
});
/**
 * Fires end of inline edit of the node.
 */
dynamicTree.TreeNode.addMethod('doEndEdit', function(evt) {
    this.editEnded();
    dynamicTree.trees[this.getTreeId()].fireEndEditEvent(this);
});
/**
 * Ends the edit of the node.
 */
dynamicTree.TreeNode.addMethod('editEnded', function() {
    var tree = dynamicTree.trees[this.getTreeId()];
    if (dynamicTree.treeNodeEdited != null) {
        var input = this._getTitleInputElement();
        var titleHolder = $(input.parentNode);
        var newValue = input.value;

        if (newValue == dynamicTree.treeNodeEdited.name) {
            input.remove();
            this._getTitle().data = dynamicTree.treeNodeEdited.name;

            dynamicTree.treeNodeEdited = null;
            return;
        }

        tree.fireEditEvent(dynamicTree.treeNodeEdited, newValue);

        if (!dynamicTree.editaborted) {
            dynamicTree.treeNodeEdited.name = newValue;
            input.remove();
            this._getTitle().data = newValue;
        }

        dynamicTree.treeNodeEdited = null;
    }
});
/**
 * Scrolls to the position of the node.
 */
dynamicTree.TreeNode.addMethod('scroll', function(element) {
    var rootNodeElement = dynamicTree.trees[this.getTreeId()].rootNode._getElement();
    var container = (element) ? $(element) : $(rootNodeElement.parentNode);
    var nodeElement = this._getElement();
    if (container) {
        var ch = container.clientHeight;
        var cw = container.clientWidth;
        var cst = container.scrollTop;
        var csl = container.scrollLeft;
        var nt = nodeElement.cumulativeOffset().top - container.cumulativeOffset().top;
        var nl = nodeElement.offsetLeft;
        var nh = nodeElement.clientHeight;
        var nw = nodeElement.clientWidth;
        if (nt > (cst + ch)) { // node is below
            container.scrollTop = nt - (ch / 2 - nh / 2);
        } else if ((nt + nh) < cst) { // node is above
            container.scrollTop = nt - (ch / 2 - nh / 2);
        }
        if (nl > (csl + cw)) { // node is out left
            container.scrollLeft = nl - (cw / 2 - nw / 2);
        } else if ((nl + nw) < csl) { // node is out right
            container.scrollTop = nl - (cw / 2 - nw / 2);
        }
    }
});
/**
 * Opens the node.
 */
dynamicTree.TreeNode.addMethod('handleNode', function(event) {
    if (!this.isParent()) { // No reason to handle a node without childs.
        return;
    }

    var tree = dynamicTree.trees[this.getTreeId()];

    if (this.isOpen()) {
        tree.writeStates(this.id, dynamicTree.TreeNode.State.CLOSED);
    } else {
        tree.writeStates(this.id, dynamicTree.TreeNode.State.OPEN);
        tree.fireOpenEvent(this, event);
    }

    this.refreshNode();
});

dynamicTree.TreeNode.addMethod('openNode', function(event){
    if (!this.isParent()) { // No reason to handle a node without childs.
        return;
    }

    var tree = dynamicTree.trees[this.getTreeId()];

    if (!this.isOpen()) {
        tree.writeStates(this.id, dynamicTree.TreeNode.State.OPEN);
        tree.fireOpenEvent(this, event);
    }

    this.refreshNode();
});

dynamicTree.TreeNode.addMethod('_getHeaderTemplateElement', function() {
    var type = this.getType();
    var id = (type && type.templateDomId) ? type.templateDomId : this.nodeHeaderTemplateDomId;
    /**
     * The commented out section is actually the preferred way to do this however, IE seems to pick up the wrong template
     * The scenario is when ever you are in ad hoc and switch to presentation mode and back again the tree is not rendered
     * simply because the cloned node doesn't have any sub nodes/children. In our current implementation, the LI node contains
     * a <span> and a <p>. These get left out.
     *
     * Note: Switching to the implementation where we simply create the clone from the id instead of using the hash may and is going to have
     * a performance issue.... (papanii)
     */
    //if (!dynamicTree._templateHash[id]) {
    //	dynamicTree._templateHash[id] = $(id);
    //}
    //var clone = dynamicTree._templateHash[id].cloneNode(true);
    
    var clone = $(id).cloneNode(true);

    clone.templateId = id;
    clone.templateClassName = clone.className;

    return clone;
});

dynamicTree.TreeNode.addMethod('_getFooterTemplateElement', function() {
    var id = this.nodeFooterTemplateDomId;
    /**
     * @see comment in _getHeaderTemplateElement above
     */
    //if (!dynamicTree._templateHash[id]) {
    //	dynamicTree._templateHash[id] = $(id);
    //}
    //var clone = dynamicTree._templateHash[id].cloneNode(true);
    var clone = $(id).cloneNode(true);
    clone.templateId = id;

    clone.update("");

    return clone;
});

dynamicTree.TreeNode.addMethod('_getInputTemplateElement', function() {
    var id = this.nodeInputTemplateDomId;
     /**
     * @see comment in _getHeaderTemplateElement above
     */
    //if (!dynamicTree._templateHash[id]) {
    //	dynamicTree._templateHash[id] = $(id);
    //}
    //var clone = dynamicTree._templateHash[id].cloneNode(true);
    var clone = $(id).cloneNode(true);
    clone.templateId = id;

    return clone;
});
