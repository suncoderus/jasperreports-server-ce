/**
 * GitHub Gist https://gist.github.com/tlync/1016011
 */
define(function(require){
    "use strict";

    var $ = require("jquery"),
        hasTouch = /android|iphone|ipad/i.test(navigator.userAgent.toLowerCase()),
        eventName = hasTouch ? 'touchend' : 'click';

    /**
     * Bind an event handler to the "double tap" JavaScript event.
     * @param {function} doubleTapHandler
     * @param {number} [delay=300]
     */
    $.fn.doubletap = function(doubleTapHandler, delay){
        delay = (delay == null) ? 300 : delay;

        this.bind(eventName, function(event){
            var now = new Date().getTime();

            // the first time this will make delta a negative number
            var lastTouch = $(this).data('lastTouch') || now + 1;
            var delta = now - lastTouch;
            if(delta < delay && 0 < delta){
                // After we detct a doubletap, start over
                $(this).data('lastTouch', null);

                if(doubleTapHandler !== null && typeof doubleTapHandler === 'function'){
                    doubleTapHandler(event);
                }
            }else{
                $(this).data('lastTouch', now);
            }
        });
    };

});