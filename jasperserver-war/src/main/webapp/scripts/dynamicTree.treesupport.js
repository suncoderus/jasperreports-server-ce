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

/* global dynamicTree, deepClone, __jrsConfigs__, _, ajaxTargettedUpdate, baseErrorHandler, unescapeBackslash,
 ajaxTargettedUpdate, trim  */

/*
 * TreeSupport is extend Tree to use it with JasperServer.
 * You can extend it to change parameters and/or look and feel
 *
 * @param id {String} - unique ID for the tree on the page
 * @param options {JSON Object} - Set of configuration options for tree :
 * <ul>
 * <li>rootUri {String} - uri for the root of this tree
 *   (usually '/' but could be something like '/area/node' if this tree is supposed
 *   to show only particular branch from the data structure)
 * </li>
 * <li>providerId {String} - Data Provider ID</li>
 * <li>resetStatesOnShow {Boolean} - If false tree state will be restored from cookies</li>
 * <li>nodeClass {@link dynamicTree.TreeNode} - function that will be used to create instances of tree node</li>
 * <li>rootObjectModifier {Function} - modifies root object</li>
 * <li>urlGetNode {String} - server url for 'getNode' method</li>
 * <li>urlGetChildren {String} - server url for 'getChildren' method</li>
 * <li>urlGetMultipleChildren {String} - server url for 'getMultipleChildren' method</li>
 * <li>urlGetMessage {String} - server url for 'getMessage' method</li>
 * </ul>
 *
 * See {@link dynamicTree.Tree} for more options.
 */
dynamicTree.TreeSupport = function(id, options) {
    dynamicTree.Tree.call(this, id, options);

    // instance variables
    this.providerId = options.providerId;
    this.hideLoader = options.hideLoader;
    this.rootUri = Object.isUndefined(options.rootUri) ? '/' : options.rootUri;
    this.nodeClass = (options.nodeClass && Object.isFunction(options.nodeClass)) ? options.nodeClass : dynamicTree.TreeNode;
    if (options.rootObjectModifier) {
        this.modifyRootObject = options.rootObjectModifier;
    }

    this.resetStatesOnShow = Object.isUndefined(options.resetStatesOnShow) || options.resetStatesOnShow;
    this.inInit = true;

    // default ajax related values
    this.ajaxBufferId = 'ajaxbuffer'; // id of a DIV elements that receives server response
    this.nodeTextId = 'treeNodeText'; // id of a text element that contains JSONized tree
    this.urlGetNode = (options.urlGetNode) ? options.urlGetNode : this._getFlowUrl("getNode"); // server url for 'getNode' method
    this.urlGetChildren = (options.urlGetChildren) ? options.urlGetChildren : this._getFlowUrl("getChildren"); // server url for 'getChildren' method
    this.urlGetMultipleChildren = (options.urlGetMultipleChildren) ? options.urlGetMultipleChildren :
                                  this._getFlowUrl("getMultipleChildren"); // server url for 'getMultipleChildren' method
    this.urlGetMessage = (options.urlGetMessage) ? options.urlGetMessage : this._getFlowUrl("getMessage"); // server url for 'getMessage' method
    this.additionalParams = options.additionalParams ? options.additionalParams: {};
    /////////////////////////////////////
    // dragging related variables
    // (if you require drag support, please pass treeDragSupport argument in ctor and include drag.js)
    /////////////////////////////////////

    if (this.TREE_NN_ITEMS_SELECTED == null) {
        var callback = function(text) {
            this.TREE_NN_ITEMS_SELECTED = text;
        }.bind(this);
        this.getMessage('TREE_NN_ITEMS_SELECTED', callback, null);
    }

    this._initOpenListener();
};

dynamicTree.TreeSupport.prototype = deepClone(dynamicTree.Tree.prototype);

/////////////////////////////////////
// methods
/////////////////////////////////////

/**
 *  Adding handler of 'node:open' event. If event occurred, handler will loads children of node from server.
 */
dynamicTree.TreeSupport.addMethod('_initOpenListener', function () {
    this.observe('node:open', function(event) {
        var node = event.memo.node;

        if (node && !node.isloaded) {
            this.getTreeNodeChildren(node);
        }
    }.bindAsEventListener(this));
});

/**
 * Remove all or specified handler from event and adding default handler on 'node:open' event.
 *
 * @param eventName {String} event, from which should be removed handlers.
 * @param handler {Function} handler function, which should be removed
 */
dynamicTree.TreeSupport.addMethod('stopObserving', function(eventName, handler) {
    this._getElement().stopObserving(eventName, handler);

    if (eventName === 'node:open' && !handler) {
        this._initOpenListener();
    }
});

dynamicTree.TreeSupport.addMethod('_getFlowUrl', function (methodName) {
    return __jrsConfigs__.contextPath + "/flow.html?_flowId=treeFlow&method=" + methodName;
});

/*
 * Loads tree from server and renders it into given container.
 * Generates "tree:loaded" event, if request was successful and userCallbackFn isn't specified.
 * Generates "server:error" event, if request was unsuccessful and errorCallbackFn isn't specified.
 *
 * @type Asynchronous method
 * @param depth {Number} controls how many levels of children to prefetch at this load
 * @param userCallbackFn {Function} optional callback function, that will be called if request was successful
 * @param errorCallbackFn {Function} optional callback function, that will be called if request was unsuccessful
 * @param forceHtmlEscape {Boolean} asks server to  escape response as html before putting it into tree div
 */
dynamicTree.TreeSupport.addMethod('showTree', function (depth, userCallbackFn, errorCallbackFn, forceHtmlEscape) {
    var url = this.urlGetNode + '&provider=' + this.providerId + '&uri=' + this.rootUri + '&depth=' + depth;
    forceHtmlEscape && (url += '&forceHtmlEscape=true');
    url += '&' + this._evaluateAdditionalParams();
    this._showTree(url, userCallbackFn, errorCallbackFn);
});

dynamicTree.TreeSupport.addMethod('_evaluateAdditionalParams', function() {
    var params = this.additionalParams;
    if (_.isFunction(params)) {
        params = params.call(null);
    }
    if (_.isObject(params)) {
        return Object.toQueryString(params);
    }
    return null;
});

/*
 * Loads tree from the server and renders it into given container.
 * Generates "tree:loaded" event, if request was successful and userCallbackFn isn't specified.
 * Generates "server:error" event, if request was unsuccessful and errorCallbackFn isn't specified.
 *
 * @type Asynchronous method
 * @param prefetchedListStr {String} comma separated uris to prefetch (example: '/reports/samples,/adhoc/topics').
 * Controls which tree branches to prefetch at this load.
 * @param userCallbackFn {Function} optional user function object to call after tree gets loaded and rendered
 * @param errorCallbackFn {Function} optional user error handler function to call if error occured
 */
dynamicTree.TreeSupport.addMethod('showTreePrefetchNodes', function (prefetchedListStr, userCallbackFn, errorCallbackFn) {
    var url = this.urlGetNode + '&provider=' + this.providerId + '&uri=' + this.rootUri;
    if (prefetchedListStr) {
        url += '&prefetch=' + encodeURIComponent(prefetchedListStr);
    }
    url += '&' + this._evaluateAdditionalParams();
    this._showTree(url, userCallbackFn, errorCallbackFn);
});

dynamicTree.TreeSupport.addMethod('_showTree', function (url, userCallbackFn, errorCallbackFn) {

    var self = this;
    this.inInit = true;

    this.wait();

    var callback = function(obj, uc, ec) {
        return function() {
            return obj.showTreeCallback(uc, ec);
        }
    }(this, userCallbackFn, errorCallbackFn);

    var httpErrorHandler = function(){
        var rc = false;
        if (self.httpErrorHandler) {
            rc = self.httpErrorHandler.apply(window, arguments);
        }
        if (rc === false) {
            rc = baseErrorHandler.apply(window, arguments);
        }
        return rc;
    };

    ajaxTargettedUpdate(
        url,
        {
            fillLocation:this.ajaxBufferId,
            callback: callback,
            errorHandler: httpErrorHandler,
            hideLoader: this.hideLoader
        }
    );
});

dynamicTree.TreeSupport.addMethod('showTreeCallback', function (userCallbackFn, errorCallbackFn) {

    // get JSONized Node
    var div = document.getElementById(this.nodeTextId);
    if (div == null) {
        if (errorCallbackFn) {
            errorCallbackFn();
        } else {
            this.fireServerErrorEvent();
        }
        return;
    }

    //as this data was escaped before adding to DOM to prevent XSS (core.ajax.js:246)
    //here it should be unescaped for further use
    var json = xssUtil.unescape(jQuery(div).text());
    var rootObj = json.evalJSON();

    if (this.modifyRootObject) {
        rootObj = this.modifyRootObject(rootObj, false);
    }

    // clean AJAX buffer
    div = document.getElementById(this.ajaxBufferId);
    div.innerHTML = '';

    // build the tree
    this.setRootNode(this.processNode(rootObj));

    this.resortTree();
    if (this.resetStatesOnShow) {
        this.resetStates();
    }

    this.renderTree();

    this.inInit = false;

    if (userCallbackFn) {
        userCallbackFn();
    } else {
        this.fireTreeLoadedEvent( { tree: this } );
    }
});

/*
 * internally used method to turn server model into javascript tree model
 * Be advised of the power of 'extra' property of server node object.
 * You can set there pretty much anything and therefore customize you tree behaviour.
 * TreeNode is available in node handlers which you may assign as callback functions to your code
 *
 * @param metaNode {JSON Object} server node
 */
dynamicTree.TreeSupport.addMethod('processNode', function (metaNode) {
    var param = {};
    param.id = metaNode.id;
    param.type = metaNode.type;
    param.uri = metaNode.uri;
    param.extra = deepClone(metaNode.extra);
    if (metaNode.cssClass) {
        param.cssClass = metaNode.cssClass;
    }

    var NodeClassFn = this.nodeClass;
    var localRoot = new NodeClassFn({
        name : unescapeBackslash(metaNode.label),
        param : param,
        orderNumber : metaNode.order
    });

    if (metaNode.tooltip) {
        localRoot.tooltip = metaNode.tooltip;
    }
//    localRoot.iconTooltip = this.getIconTooltip(localRoot);

    var ch = metaNode.children;
    if (ch != null) {
        var len = ch.length;
        if (len === 0) {
            localRoot.setHasChilds(false);
        } else {
            for (var i = 0; i < len; i++) {
                var chNodeObj = ch[i];
                if (chNodeObj) {
                    var chTreeNode = this.processNode(chNodeObj);
                    localRoot.addChild(chTreeNode);
                }
            }
        }
        localRoot.isloaded = true;
    }
    return localRoot;
});

/*
 * Dynamically loads children for the node
 * Generates "children:loaded" event, if request was successful and userCallbackFn isn't specified.
 * Generates "server:error" event, if request was unsuccessful and errorCallbackFn isn't specified.
 *
 * @param parentNode {{@link dynamicTree.TreeNode}} the node, for which children should be loaded
 * @param userCallbackFn {Function} optional callback function, that will be called if request was successful
 * @param errorCallbackFn {Function} optional callback function, that will be called if request was unsuccessful
 */
dynamicTree.TreeSupport.addMethod('getTreeNodeChildren', function (parentNode, userCallbackFn, errorCallbackFn) {

    var uri = parentNode.param.uri;

    var callback = function(obj, ni, uc, ec) {
        return function() {
            return obj.getTreeNodeChildrenCallback(ni, uc, ec);
        }
    }(this, parentNode.id, userCallbackFn, errorCallbackFn);

    var treeErrorHandler = function(ajaxAgent) {
        if (ajaxAgent.status == 500 || ajaxAgent.getResponseHeader("JasperServerError")) {
            parentNode.stopWaiting();
        }
        baseErrorHandler(ajaxAgent);
    };

    ajaxTargettedUpdate(
        this.urlGetChildren + '&provider=' + this.providerId + '&uri=' + encodeURIComponent(encodeURIComponent(uri)) +
            '&' + this._evaluateAdditionalParams(),
        {fillLocation:this.ajaxBufferId, callback: callback, errorHandler: treeErrorHandler, hideLoader: this.hideLoader}
    );

    if (!this.inInit) {
        parentNode.wait();
    }

});

dynamicTree.TreeSupport.addMethod('getTreeNodeChildrenCallback', function (parentNodeId, userCallbackFn, errorCallbackFn) {

    var div = document.getElementById(this.nodeTextId);
    if (div == null) {
        if (errorCallbackFn) {
            errorCallbackFn();
        } else {
            this.fireServerErrorEvent();
        }
        return;
    }
    //var ns = window.eval('(' + div.innerHTML.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>') + ')'); // jshint ignore: line
    var ns = xssUtil.unescape(jQuery(div).text()).evalJSON();

    var parentNode = dynamicTree.nodes[parentNodeId];

    if (this.modifyRootObject) {
        ns = this.modifyRootObject(ns, true, parentNode);
    }

    div = document.getElementById(this.ajaxBufferId);
    div.innerHTML = '';

    parentNode.resetChilds();

    parentNode.stopWaiting();

    var len = ns.length;
    if (len === 0) {
        parentNode.setHasChilds(false);
    } else {
        var treeId = parentNode.getTreeId();
        var tree = (treeId) ? dynamicTree.trees[treeId] : null;
        var tmpSortNodes = (tree) ? tree.sortNodes : null;
        if (tree) {
            tree.sortNodes = false;
        }
        for (var i = 0; i < len; i++) {
            var node = this.processNode(ns[i]);
            parentNode.addChild(node);
        }
        if (tree) {
            tree.sortNodes = tmpSortNodes;
            parentNode.resortChilds();
        }
    }

    parentNode.isloaded = true;
    parentNode.refreshNode();

    if (userCallbackFn) {
        userCallbackFn(parentNode.childs);
    } else {
        this.fireChildrenLoadedEvent(parentNode.childs);
    }
});

/*
 * Loads children for several given nodes
 * Generates "multipleChildren:loaded" event, if request was successful and userCallbackFn isn't specified.
 * Generates "server:error" event, if request was unsuccessful and errorCallbackFn isn't specified.

 * @param parentNodes {Array<{@link dynamicTree.TreeNode}>} array of TreeNode instances
 * @param userCallbackFn {Function} optional callback function, that will be called if request was successful
 * @param errorCallbackFn {Function} optional callback function, that will be called if request was unsuccessful
 */
dynamicTree.TreeSupport.addMethod('getTreeMultipleNodesChildren', function (parentNodes, userCallbackFn, errorCallbackFn) {

    var uri = '';
    var ids = [];
    var i;
    if (parentNodes && parentNodes.length) {
        for (i = 0; i < parentNodes.length; i++) {
            if (i > 0) {
                uri += ',';
            }
            uri += encodeURIComponent(encodeURIComponent(parentNodes[i].param.uri));
            ids[i] = parentNodes[i].id;
        }
    }

    if (!uri.length) {
        // no nodes requested - just return
        return;
    }

    var callback = function(obj, ni, uc, ec) {
        return function() {
            return obj.getTreeMultipleNodesChildrenCallback(ni, uc, ec);
        }
    }(this, ids, userCallbackFn, errorCallbackFn);

    ajaxTargettedUpdate(
        this.urlGetMultipleChildren + '&provider=' + this.providerId,// + '&uris=' + uri,
        {
            fillLocation:this.ajaxBufferId,
            callback:callback,
            postData:'uris=' + uri,
            errorHandler: baseErrorHandler,
            hideLoader: this.hideLoader
        });

    if (!this.inInit) {
        for (i = 0; i < parentNodes.length; i++) {
            parentNodes[i].wait();
        }
    }

});

dynamicTree.TreeSupport.addMethod('getTreeMultipleNodesChildrenCallback', function (parentNodeIds, userCallbackFn, errorCallbackFn) {

    var div = document.getElementById(this.nodeTextId);
    if (div == null) {
        if (errorCallbackFn) {
            errorCallbackFn();
        } else {
            this.fireServerErrorEvent();
        }
        return;
    }
    //var ns = window.eval('(' + div.innerHTML.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>') + ')'); // jshint ignore: line
    var ns = xssUtil.unescape(jQuery(div).text()).evalJSON();

    div = document.getElementById(this.ajaxBufferId);
    div.innerHTML = '';


    if (userCallbackFn) {
        userCallbackFn(parentNodeIds, ns);
    } else {
        this.setMultipleNodesChilden(parentNodeIds, ns);
        this.fireMultipleChildrenLoadedEvent(parentNodeIds, ns);
    }
});

/*
 * Default processor for getTreeMultipleNodesChildren
 *
 * @param parentNodeIds {Array<String>} parent node IDs for which server was requested
 * @param nodeHolders {Array<JSON Object>} processed server response object
 * @param noRender {Boolean} if true nodes which loaded from server will not be displayed
 */
dynamicTree.TreeSupport.addMethod('setMultipleNodesChilden', function (parentNodeIds, nodeHolders, noRender) {

    if (parentNodeIds && nodeHolders) {
        for (var nn = 0; nn < nodeHolders.length; nn++) {
            var nodeHolder = nodeHolders[nn];
            var ns = nodeHolder.children;

            // find a parentNode
            var parentNode = null;
            for (var pn = 0; pn < parentNodeIds.length; pn ++) {
                var tempnode = dynamicTree.nodes[parentNodeIds[pn]];
                if (tempnode.param.uri == nodeHolder.parentUri) {
                    parentNode = tempnode;
                    break;
                }
            }

            if (parentNode) {

                parentNode.resetChilds();
                parentNode.stopWaiting();

                var len = ns.length;
                if (len === 0) {
                    parentNode.setHasChilds(false);
                } else {
                    var treeId = parentNode.getTreeId();
                    var tree = (treeId) ? dynamicTree.trees[treeId] : null;
                    var tmpSortNodes = (tree) ? tree.sortNodes : null;
                    if (tree) {
                        tree.sortNodes = false;
                    }
                    for (var i = 0; i < len; i++) {
                        var node = this.processNode(ns[i]);
                        parentNode.addChild(node);
                    }
                    if (tree) {
                        tree.sortNodes = tmpSortNodes;
                        parentNode.resortChilds();
                    }
                }

                parentNode.isloaded = true;
                if (!noRender) {
                    parentNode.refreshNode();
                }
            }

        }
    }
});

/*
 * Dynamically loads children for a given node.
 * Makes sure that all requested nodes get prefetched.
 * Nodes to be prefetched have to have parentNode as a common (grand*)parent
 *
 * Generates "multipleChildren:loaded" event, if request was successful and userCallbackFn isn't specified.
 * Generates "server:error" event, if request was unsuccessful and errorCallbackFn isn't specified.

 * @param parentNode {{@link dynamicTree.TreeNode}} the node for which children should be loaded
 * @param prefetchedListStr {String} comma separated URIs to be prefetched
 * @param userCallbackFn {Function} optional callback function, that will be called if request was successful
 * @param errorCallbackFn {Function} optional callback function, that will be called if request was unsuccessful
 */
dynamicTree.TreeSupport.addMethod('getTreeNodeChildrenPrefetched', function (parentNode, prefetchedListStr, userCallbackFn, errorCallbackFn, depth, disableSorting, noRender) {

    var uri = parentNode.param.uri;

    var url = this.urlGetNode + '&provider=' + this.providerId + '&uri=' + uri;
    var prefetch = '';
    if (prefetchedListStr) {
//            url += '&prefetch=' + prefetchedListStr;
        prefetch = '&prefetch=' + prefetchedListStr;

    }
    if (depth) {
        url += '&depth=' + depth;
    }
    var callback = function(obj, ni, uc, ec, ds, nr) {
        return function() {
            return obj.getTreeNodeChildrenPrefetchedCallback(ni, uc, ec, ds, nr);
        }
    }(this, parentNode.id, userCallbackFn, errorCallbackFn, disableSorting, noRender);

    ajaxTargettedUpdate(
        url,
        {
            fillLocation:this.ajaxBufferId,
            callback:callback,
            postData:prefetch,
            errorHandler: baseErrorHandler,
            hideLoader: this.hideLoader
        });

    if (!this.inInit) {
        parentNode.wait();
    }

});

dynamicTree.TreeSupport.addMethod('getTreeNodeChildrenPrefetchedCallback', function (parentNodeId, userCallbackFn, errorCallbackFn, disableSorting, noRender) {

    var div = document.getElementById(this.nodeTextId);
    if (div == null) {
        if (errorCallbackFn) {
            errorCallbackFn();
        } else {
            this.fireServerErrorEvent();
        }
        return;
    }
    //var n = window.eval('(' + div.innerHTML.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>') + ')'); // jshint ignore: line
    var n = xssUtil.unescape(jQuery(div).text()).evalJSON();

    div = document.getElementById(this.ajaxBufferId);
    div.innerHTML = '';

    var parentNode = dynamicTree.nodes[parentNodeId];
    parentNode.resetChilds();
    parentNode.stopWaiting();

    if (n.children) {
        var treeId = parentNode.getTreeId();
        var tree = (treeId) ? dynamicTree.trees[treeId] : null;
        var tmpSortNodes = (tree) ? tree.sortNodes : null;
        if (tree) {
            tree.sortNodes = false;
        }
        for (var i = 0; i < n.children.length; i++) {
            var node = this.processNode(n.children[i]);
            parentNode.addChild(node);
        }
        if (tree) {
            tree.sortNodes = tmpSortNodes;
            disableSorting || parentNode.resortChilds();
        }
    }

    parentNode.isloaded = true;
    if (!noRender) {
        parentNode.refreshNode();
    }

    if (userCallbackFn) {
        userCallbackFn();
    } else {
        this.fireChildredPrefetchedLoadedEvent(parentNode.childs);
    }
});

/**
 * Expands the tree up to a given node, and then select it
 *
 * @param uriStr {String} path to the node
 * @param fnAction {Function} optional action to be called
 * @param findFirstChild {Boolean} optional parameter, if true first child of the node will be open
 */
dynamicTree.TreeSupport.addMethod('openAndSelectNode', function(uriStr, fnAction, findFirstChild, options) {
    var fn = function(node) {
        var tree = dynamicTree.trees[node.getTreeId()];

        if (node.parent) {
            if (tree && tree.rootNode != node.parent && tree.getState(node.parent.id) == dynamicTree.TreeNode.State.CLOSED) {
                node.parent.handleNode();
            }
        }

        if(node && jQuery('#dataChooserSource').length) node.nofocus = true;
        tree._selectOrEditNode(undefined, node, false, false, false, options);
    };

    this.processNodePath(uriStr, fn, findFirstChild);

    // scroll tree container to make selected node visible
    var selectedNode = this.getSelectedNode();

    if (selectedNode) {
        selectedNode.scroll();
    }

    if (fnAction) {
        fnAction();
    }
});

dynamicTree.TreeSupport.addMethod('processNodePath', function(uriStr, fnForNode, findFirstChild) {
    var node = this.getRootNode();

    if(uriStr === "/") {
        fnForNode(node);
    }else{
        var path = uriStr.split('/');
        var i;

        for (i = 0; i < path.length; i++) {
            if (!path[i]) {
                continue;
            }
            var oldNode = node;
            node = this.findNodeChildByMetaName(node, path[i]);
            if (!node) {
                if(findFirstChild){
                    node = this.findNodeFirstNodeChildByAlphabeticalOrder(oldNode);
                    if(!node){
                        return;
                    }
                }else{
                    return;
                }
            }
            fnForNode(node);
        }
    }

});

/**
 * Returns TreeNode which is last node in node hierarchical chain for a given uri
 * If returned node corresponds to uriStr, it means no more server requests needed
 * If it corresponds to parent (grand-parent, etc.), the value shows existing root
 * from which the rest should be requested from server
 * Example: uriStr='/area/subarea/dept/prod1', return is TreeNode with uri '/area/subarea'.
 * It means, we need to load children of 'subarea' and children of 'dept' from server
 *
 * @param uriStr {String}
 */
dynamicTree.TreeSupport.addMethod('findLastLoadedNode', function(uriStr) {
    var nodeHolder = { node: null };
    var fn = function(holder) {
        return function(node) {
            holder.node = node;
        }
    }(nodeHolder);

    this.processNodePath(uriStr, fn);

    return nodeHolder.node;
});

dynamicTree.TreeSupport.addMethod('findNodeChildByMetaName', function (node, name) {
    if (node.hasChilds()) {
        for (var i = 0; i < node.childs.length; i++) {
            if (node.childs[i].param.id == name) {
                return node.childs[i];
            }
        }
    }
    return null;
});

/*
 * This function gets the first child based on alphabetical order.
 * For e.g. if the node contains children: {topic, mail, apple, orange}, apple will be returned..
 *
 * @param node {{@link dynamicTree.TreeNode}} parent node
 */
dynamicTree.TreeSupport.addMethod('findNodeFirstNodeChildByAlphabeticalOrder', function (node){
    var firstchildName = null;
    var firstchildIndex = null;
    if(node.childs.length > 0){
        firstchildName = node.childs[0].param.id;
        firstchildIndex = 0;
        var tempChildName = null;
        var tempChildIndex = null;
        for(var index = 1; index < node.childs.length; index++){
            tempChildName = node.childs[index].param.id;
            tempChildIndex = index;
            var loopCount = (tempChildName.length < firstchildName.length) ? tempChildName.length : firstchildName.length;
            for(var loopIndex = 0; loopIndex < loopCount; loopIndex++){
                if(tempChildName.charCodeAt(loopIndex) < firstchildName.charCodeAt(loopIndex)){
                    firstchildName = tempChildName;
                    firstchildIndex = tempChildIndex;
                    break;
                }else if(tempChildName.charCodeAt(loopIndex) > firstchildName.charCodeAt(loopIndex)){
                    //we break out.
                    break;
                }
            }
        }
        return node.childs[firstchildIndex];
    }else{
        return null;
    }
});

/**
 * Recursively searching for node by node identifier starting from root node.
 * @param nodeId Node identifier.
 */
dynamicTree.TreeSupport.addMethod('findNodeById', function (nodeId, startNode){
    return (function _findNodeById(nodeId, node){
        if (!node || !nodeId) {
            return null;
        }

        if (node.param.id === nodeId) {
            return node;
        }

        if (node.hasChilds()) {
            for (var i = 0; i < node.childs.length; ++i) {
                var found = _findNodeById(nodeId, node.childs[i]);
                if (found) {
                    return found;
                }
            }
        }
        return null;

    })(nodeId, startNode ? startNode : this.getRootNode());
});

/*
 * Checks that node has child folders that are loaded and open
 *
 * @param node {{@link dynamicTree.TreeNode}} parent node
 */
dynamicTree.TreeSupport.addMethod('hasVisibleFolders', function(rootObj) {
    if (this.bShowRoot) {
        return true;
    }

    var children = rootObj.children;

    if (children) {
        for (var i = 0; i < children.length; i++) {
            var grandchildren = rootObj.children[i].children;
            if (grandchildren && grandchildren.length>0) {
                return true;
            }
        }
    }

    return false;
});

///////////////////////////////////////////
// Message system support
///////////////////////////////////////////

dynamicTree.TreeSupport.addMethod('getMessage', function (messageId, userCallbackFn, errorCallbackFn) {

    var url = this.urlGetMessage + '&messageId=' + messageId;

    var callback = function(obj, uc, ec) {
        return function() {
            return obj.getMessageCallback(uc, ec);
        }
    }(this, userCallbackFn, errorCallbackFn);


    ajaxTargettedUpdate(
        url,
        {fillLocation:this.ajaxBufferId, callback:callback, errorHandler: baseErrorHandler, hideLoader: this.hideLoader}
    );

});

dynamicTree.TreeSupport.addMethod('getMessageCallback', function (userCallbackFn, errorCallbackFn) {

    var div = document.getElementById(this.ajaxBufferId);
    if (div == null) {
        if (errorCallbackFn) {
            errorCallbackFn();
        }
        return;
    }

    // clean AJAX buffer
    var text = trim(div.innerHTML);
    div.innerHTML = '';

    if (userCallbackFn) {
        userCallbackFn(text);
    }
});

////////////////////////////////////////////////////////
// Message system support
////////////////////////////////////////////////////////

/**
 * Generates "server:error" event.
 */
dynamicTree.TreeSupport.addMethod('fireServerErrorEvent', function() {
    this._getElement().fire('server:error', { });
});

/**
 * Generates "tree:loaded" event.
 */
dynamicTree.TreeSupport.addMethod('fireTreeLoadedEvent', function(tree) {
    this._getElement().fire('tree:loaded', { tree: tree });
});

/**
 * Generates "children:loaded" event.
 */
dynamicTree.TreeSupport.addMethod('fireChildrenLoadedEvent', function(nodes) {
    this._getElement().fire('children:loaded', { nodes: nodes });
});

/**
 * Generates "multipleChildren:loaded" event.
 */
dynamicTree.TreeSupport.addMethod('fireMultipleChildrenLoadedEvent', function(parentNodeIds, metaNodes) {
    this._getElement().fire('multipleChildren:loaded', { parentNodeIds: parentNodeIds, metaNodes: metaNodes });
});

/**
 * Generates "childredPrefetched:loaded" event.
 */
dynamicTree.TreeSupport.addMethod('fireChildredPrefetchedLoadedEvent', function(nodes) {
    this._getElement().fire('childredPrefetched:loaded', { nodes: nodes });
});
