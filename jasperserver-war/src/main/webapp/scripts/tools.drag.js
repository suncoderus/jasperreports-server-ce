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
 * @author: Angus Croll
 * @version: $Id$
 */

//////////////////////////////////////////////////////////
// Drag.js
// Generic drag objects and functions
// Note: Not yet utilized by all dragging in Jasperserver
//////////////////////////////////////////////////////////

/**
 * Drag Listener
 * Co-ordinates drag callbacks
 */
function DragListener() {
    this.agents = [];
    this.currentAgentName = null;
    this.dragger;
}

/**
 * Standard drag events
 */
DragListener.DRAGGING_STARTED = 'draggingStarted';
DragListener.DRAGGING_FINISHED = 'draggingFinished';
DragListener.DRAGGING = 'dragging';

/**
 * register a dragging agent with teh drag listener
 * @param {String} agentName A key identifying the distinct functional area from which the drag originates (eg 'Tree', Table' etc.) 
 * This allows us to segregate event actions according to type of drag 
 */
DragListener.prototype.registerAgent = function(agentName) {
    this.agents[agentName] = [];
};

/**
 * Register the event with an action
 * @param {String} agentName A key identifying the distinct functional area from which the drag originates (eg Tree, Table etc.) 
 * @param {String} event Something that happens during drag, e.g. 'mouseOverColumn', 'mouseOutGroup' 'draggingStarted'
 * @param {Function} action Function to be performed when the specified event is triggered by the specified agent
 */
DragListener.prototype.publishEvent = function(agentName,event,action) {
    this.agents[agentName][event] = action;
};

/**
 * Trigger the event
 * @param {Object} dragListenerEvent Something that happens during drag, e.g. 'mouseOverColumn', 'mouseOutGroup' 'draggingStarted'
 * @param {Object} browserEvent The window event
 */
DragListener.prototype.notify = function(dragListenerEvent,browserEvent) {
    var currentAgent = this.agents[this.currentAgentName];
    if (currentAgent) {
        var thisAction = currentAgent[dragListenerEvent];
        if (thisAction) {
            var draggingObjs = this.dragger ? this.dragger.draggingObjs : null;
            thisAction(browserEvent,draggingObjs);
        }
    }
};

/**
 * any dragging going on right now?
 */
DragListener.prototype.isDragging = function() {
    return this.currentAgentName != null;
};

DragListener.prototype.setCurrentAgentName = function(agentName) {
    this.currentAgentName = agentName;
};

DragListener.prototype.getCurrentAgentName = function() {
    return this.currentAgentName;
};

/**
 * Dragger
 * Drags anything......
 * @param {Object} evt current window event that triggered drag
 * @param {Array} draggingObjs are all objects being dragged whether or not the mouse is over them (multi-select)
 * @param {Boolean} dragsX can we drag horizontally 
 * @param {Boolean} dragsY can we drag vertically
 * @param {Number} sigMove the number of pixels user must move mouse before we consider drag initiated
 * @param {DragListener} dragListener a dragListener instance
 * @param {Function} cleanUpUtil an optional utlity function to be invoked after drag is released (i.e. dropped)
 */
function Dragger(evt,draggingObjs,dragsX,dragsY,sigMove,dragListener,cleanUpUtil) {

    evt = evt ? evt : event;

    this.originalX = [];
    this.originalY = [];

    this.draggingObjs = draggingObjs;
    this.dragsX = dragsX;
    this.dragsY = dragsY;
    this.sigMove = sigMove;
    this.dragListener = dragListener;
    this.cleanUpUtil = cleanUpUtil;

    if (this.dragListener) {
        this.dragListener.dragger = this;
    }

    for (var i=0; i<draggingObjs.length; i++) {
        this.originalX[i] = parseInt(draggingObjs[i].style.left);
        this.originalY[i] = parseInt(draggingObjs[i].style.top);
    }

    this.mouseX=evt.clientX;
    this.mouseY=evt.clientY;
    this.isDragging=false;

    this.initDragger(evt);

}

var invokeDragging = function(dragger) {
    return function(event) {
        dragger.dragging(event);
    }
};

var invokeDraggingFinished = function(dragger) {
    return function(event) {
        dragger.draggingFinished(event);
    }
};

/**
 * Add an additional Element to the collection of dragging objects already defined in an existing Dragger
 * @param {Object} evt The window event
 * @param {Element} draggingObj An HTML element
 */
Dragger.prototype.addAnotherDraggingObject = function(evt,draggingObj) {
    evt = evt ? evt : event;

    this.draggingObjs[this.draggingObjs.length] = draggingObj;
    this.originalX[this.originalX.length]  = parseInt(draggingObj.style.left);
    this.originalY[this.originalY.length] = parseInt(draggingObj.style.top);

    this.initDragger(evt);

};

Dragger.prototype.initDragger = function(evt) {
    this.isDragging=false;

    this.mouseX=evt.clientX;
    this.mouseY=evt.clientY;

    document.onmousemove = invokeDragging(this);
    document.onmouseup = invokeDraggingFinished(this);
};

Dragger.prototype.dragging = function(evt) {

    var e = evt?evt:event;

    var movedSignificantly = false;
    var xDiff = 0;
    var yDiff = 0;

    if (this.dragsX) {
        xDiff = e.clientX-this.mouseX;
        movedSignificantly = Math.abs(xDiff)>this.sigMove;
    }

    if (this.dragsY) {
        yDiff = e.clientY-this.mouseY;
        movedSignificantly = movedSignificantly || Math.abs(yDiff)>this.sigMove;
    }

    if (!this.isDragging && movedSignificantly) {
        this.isDragging=true;
        if (this.dragListener) {
            this.dragListener.notify(DragListener.DRAGGING_STARTED,evt);
        }
        for (var i=0; i<this.draggingObjs.length; i++) {
            this.draggingObjs[i].style.display="block";
        }
    }

    if (this.isDragging) {
        if (xDiff) {
            for (var i=0; i<this.draggingObjs.length; i++) {
                var left = parseInt(this.originalX[i] + xDiff);
//                if (left < 0 || left + $(this.draggingObjs[i]).getWidth() > document.body.clientWidth) {
//                    xDiff = false;
//                    break;
//                }
            }
            if (xDiff) {
                for (var i=0; i<this.draggingObjs.length; i++) {
                    this.draggingObjs[i].style.left=parseInt(this.originalX[i] + xDiff) + "px";
                }
            }
        }
        if (yDiff) {
            for (var i=0; i<this.draggingObjs.length; i++) {
                var top = parseInt(this.originalY[i] + yDiff);
//                if (top < 0 || top + $(this.draggingObjs[i]).getHeight() > document.body.clientHeight) {
//                    yDiff = false;
//                    break;
//                }
            }
            if (yDiff) {
                for (var i=0; i<this.draggingObjs.length; i++) {
                    this.draggingObjs[i].style.top=parseInt(this.originalY[i] + yDiff) + "px";
                }
            }
        }
        if (xDiff || yDiff) {
            this.dragListener.notify(DragListener.DRAGGING,evt);
        }
    }
};

Dragger.prototype.draggingFinished = function(evt) {

    document.onmousemove='default';
    document.onmouseup='default';

    if (this.isDragging) {
        this.isDragging = false;

        //generic housekeeping for this drag host
        if (this.cleanUpUtil) {
            this.cleanUpUtil();
        }

        if (this.dragListener) {
            this.dragListener.notify(DragListener.DRAGGING_FINISHED,evt);
            this.dragListener.setCurrentAgentName(null);
        }
    } else {
        if (this.dragListener) {
            this.dragListener.setCurrentAgentName(null);
        }
    }
};

Dragger.prototype.clearDraggingObjects = function() {
    for (var i=0; i<this.draggingObjs.length; i++) {
        var thisOne = this.draggingObjs[i];
        if (thisOne) {
            if (thisOne.parentNode) {
                thisOne.parentNode.removeChild(thisOne);
            }
            thisOne = null;
        }
    }
};

