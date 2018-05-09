//  DragDropExtra Scriptaculous Enhancement, version 0.2
//  (c) 2007-2008 Christopher Williams, Iterative Designs
//
// v0.3 release
//    - Fixed bug found by Phillip Sauerbeck psauerbeck@gmail. Tests added based on Phillip's efforts.
// v0.2 release
//      - Minor bug fix for the releasing of objects after they have been dropped, prevents memory leak.
// v0.1 release
//      - initial release for the super ghosting capability
//      - Drags from one scrolling list to the other (overflow:auto)
//      - Retains the original object so that it can remain present despite being dragged
// 
// dragdropextra.js is freely distributable under the terms of an MIT-style license.
// For details, see the Iterative Designs web site: http://www.iterativedesigns.com/
// Parts of this code have been taken from the original dragdrop.js library which is 
// copyrighted by (c) 2005-2007 Thomas Fuchs (http://script.aculo.us, 
// http://mir.aculo.us) and (c) 2005-2007 Sammi Williams 
// (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz) and available under 
// a MIT-style license.

//////////////////////////////////////////////////////////////////////////////////////////////
//Jaspersoft Updates (look for comment: JASPERSOFT #x)                  
//////////////////////////////////////////////////////////////////////////////////////////////
// #1 3/27/10 if options.mouseOffset is true then dragging thing positioned with mousepointer 
//       see Draggable.prototype.draw    
// #2 9/27/10 restore zIndex of coloned element after dragging     
// #1 11/30/100 updated #1 to allow for parent element offsets
// #4 09/28/11 fix of bug #24294. Druggable silhouette position should include scrolling
// #5 12/04/12 fix of bug #24133. Input control can move outside of report boundaries
//////////////////////////////////////////////////////////////////////////////////////////////

Draggable.prototype.startDrag = function(event) {
    this.dragging = true;
    if(!this.delta)
        this.delta = this.currentDelta();

    if(this.options.zindex) {
        this.originalZ = parseInt(Element.getStyle(this.element,'z-index') || 0);
        this.element.style.zIndex = this.options.zindex;
    }

    if(this.options.ghosting) {
        this._clone = this.element.cloneNode(true);
        this.element._originallyAbsolute = (this.element.getStyle('position') == 'absolute');
        if (!this.element._originallyAbsolute)
            Position.absolutize(this.element);
        this.element.parentNode.insertBefore(this._clone, this.element);
    }

    //JASPERSOFT #5 - see comment block top of page for more info
    var $body = jQuery("body"), $el = jQuery(this.element), offset = $el.offset();
    this._sizes = {
        window: {w: $body.outerWidth(true), h: $body.outerHeight(true)},
        element: {w: $el.outerWidth(), h: $el.outerHeight()},
        mouseOffset: {left: Event.pointerX(event) - offset.left, top: Event.pointerY(event) - offset.top}
    };
    //END JASPERSOFT #5 - see comment block top of page for more info

    if(this.options.superghosting) {
        Position.prepare();
        var pointer = [Event.pointerX(event), Event.pointerY(event)];
        body = document.getElementsByTagName("body")[0];
        me = this.element;
        this._clone = me.cloneNode(true);

        me.parentNode.insertBefore(this._clone, me);
        me.id = "clone_"+me.id;
        me.hide();

        Position.absolutize(me);
        me.parentNode.removeChild(me);
        body.appendChild(me);
        //Retain height and width of object only if it has been nulled out.  -v0.3 Fix
        if (me.style.width == "0px" || me.style.height == "0px")    {
            me.style.width=Element.getWidth(this._clone)+"px";
            me.style.height=Element.getHeight(this._clone)+"px";
        }

        //overloading in order to reduce repeated code weight.
        this.originalScrollTop = (Element.getHeight(this._clone)/2);

        this.draw(pointer);
        me.show();
    }

    if(this.options.scroll) {
        if (this.options.scroll == window) {
            var where = this._getWindowScroll(this.options.scroll);
            this.originalScrollLeft = where.left;
            this.originalScrollTop = where.top;
        } else {
            this.originalScrollLeft = this.options.scroll.scrollLeft;
            this.originalScrollTop = this.options.scroll.scrollTop;
        }
    }

    Draggables.notify('onStart', this, event);

    if(this.options.starteffect) this.options.starteffect(this.element);
}

//JASPERSOFT #5 - see comment block top of page for more info
Draggable.prototype.draw = function (point) {
    // because element can change it's size right after start of dragging, we need to
    // check it's size after some time
    if (typeof this.elemSizeCaptured === "undefined") {
        setTimeout(function(){
            if (this._sizes) {
                var $el = jQuery(this.element);
                this._sizes.element = {w: $el.outerWidth(), h: $el.outerHeight()};
            }
        }.bind(this), 100);
        this.elemSizeCaptured = true;
    }
    var $el = jQuery(this.element), originPos = $el.offset(),
        newPos = {
            left: point[0] - this._sizes.mouseOffset.left,
            top: point[1] - this._sizes.mouseOffset.top
        };

    // now, run the original code from the original content of the draw() function

    // -----------------------------
    // run the "snap" logic
    var p = [newPos.left, newPos.top];
    if (this.options.snap) {
        if (Object.isFunction(this.options.snap)) {
            p = this.options.snap(p[0],p[1],this);
        } else {
            if (Object.isArray(this.options.snap)) {
                p = p.map( function(v, i) {
                    return (v/this.options.snap[i]).round()*this.options.snap[i] }.bind(this))
            } else {
                p = p.map( function(v) {
                    return (v/this.options.snap).round()*this.options.snap }.bind(this))
            }
        }
    }
    // getting back our coordinates modified by snap
    newPos = {left: p[0], top: p[1]};

    // -----------------------------
    // run the 'constraint' logic
    if (this.options.constraint && this.options.constraint == 'horizontal')
        newPos.top = originPos.top;
    if (this.options.constraint && this.options.constraint == 'vertical')
        newPos.left = originPos.left;

    // -----------------------------
    // run the 'mouseOffset' logic -- see JASPERSOFT #1 - see comment block top of page for more info
    if (this.options.mouseOffset) {
        newPos = {left: point[0], top: point[1]};
    }


    // check if new coordinates of the element are inside window
    if (
        0 > newPos.top
            || 0 > newPos.left
            || (newPos.top + this._sizes.element.h) > this._sizes.window.h
            || (newPos.left + this._sizes.element.w) > this._sizes.window.w
        ) {
        // this means we are trying to move element outside the window, and we have to prevent this.
        // We need to fix the position to place element somewhere near the border of the window
        if (0 > newPos.top) newPos.top = 0;
        if (0 > newPos.left) newPos.left = 0;
        if ((newPos.top + this._sizes.element.h) > this._sizes.window.h) newPos.top = this._sizes.window.h - this._sizes.element.h;
        if ((newPos.left + this._sizes.element.w) > this._sizes.window.w) newPos.left = this._sizes.window.w - this._sizes.element.w;
    }


    // now, position our element on the page
    $el.offset(newPos);

    // WebKit engine + position gave in percents + our jQuery library makes incorrect behaviour:
    // Webkit issue, still opened: https://bugs.webkit.org/show_bug.cgi?id=29084
    // jQuery issue, fixed in version 1.7.2 (we use 1.7.1): http://bugs.jquery.com/ticket/10639
    // to fix it quickly and easily, we can call offset() function twice, which will set correct values.
    // Of course, we could convert persent into pixels, and so on, but it's quite and safe way, and almost no overhead
    // for user.
    this._sequencer = this._sequencer || 0;
    this._sequencer++;
    if (this._sequencer == 1) {
        $el.offset(newPos);
    }
}
//END JASPERSOFT #5 - see comment block top of page for more info


Droppables.isAffected = function(point, element, drop) {
    Position.prepare();
    positioned_within = Position.withinIncludingScrolloffsets(drop.element, point[0], point[1])
    return (
        (drop.element!=element) &&
            ((!drop._containers) ||
                this.isContained(element, drop)) &&
            ((!drop.accept) ||
                (Element.classNames(element).detect(
                    function(v) { return drop.accept.include(v) } ) )) && positioned_within );


}


Draggable.prototype.finishDrag =  function(event, success) {
    this.dragging = false;

    //JASPERSOFT #5 - see comment block top of page for more info
    delete this._sequencer;
    delete this._sizes;
    //END JASPERSOFT #5 - see comment block top of page for more info

    if(this.options.quiet){
        Position.prepare();
        var pointer = [Event.pointerX(event), Event.pointerY(event)];
        Droppables.show(pointer, this.element);
    }

    if(this.options.ghosting) {
        if (!this.element._originallyAbsolute)
            Position.relativize(this.element);
        //delete this.element._originallyAbsolute;
        this.element._originallyAbsolute = undefined; //IE barfs on delete
        Element.remove(this._clone);
        this._clone = null;
    }



    if(this.options.superghosting) {
        body = document.getElementsByTagName("body")[0];
        Element.remove(this.element);
        /*
         me = this.element;
         body.removeChild(me);
         */
        new Draggable(this._clone, this.options);
    }

    var dropped = false;
    if(success) {
        dropped = Droppables.fire(event, this.element);
        if (!dropped) dropped = false;
    }
    if(dropped && this.options.onDropped) this.options.onDropped(this.element);
    Draggables.notify('onEnd', this, event);

    var revert = this.options.revert;
    if(revert && typeof revert == 'function') revert = revert(this.element);

    var d = this.currentDelta();
    if(revert && this.options.reverteffect) {
        if (dropped == 0 || revert != 'failure')
            this.options.reverteffect(this.element,
                d[1]-this.delta[1], d[0]-this.delta[0]);
    } else {
        this.delta = d;
    }

    if (this.options.zindex) {
        this.element.style.zIndex = this.originalZ;
        //JASPERSOFT #2 - see comment block top of page for more info
        this._clone && (this._clone.style.zIndex = this.originalZ);
        //END JASPERSOFT #2
    }

    if(this.options.endeffect)
        this.options.endeffect(this.element);

    Draggables.deactivate(this);
    Droppables.reset();
}
