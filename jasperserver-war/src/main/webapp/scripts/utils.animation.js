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

/**
 * A collection of Common Animation Utilities.
 */

///////////////////////////////////////////////////////////////////////////////////////////////////////
// ** See also: common.js **
///////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////
// Script.aculo.us helpers
///////////////////////////////////////////


///////////////////////////////////////////
// Move
///////////////////////////////////////////

/**
 * Move to absolute position
 * @param {Object} element or element id
 * @param {Object} toX
 * @param {Object} toY
 * @param {Object} duration in seconds (default 1) 
 */
function moveTo(element, toX, toY, duration) {
    new Effect.Move($(element),{sync: false, x: toX, y: toY, mode: 'absolute', duration: (duration?duration:0.2)});
}

/**
 * Move by given x and y deltas
 * @param {Object} element or element id
 * @param {Object} toX
 * @param {Object} toY
 * @param {Object} duration in seconds (default 1)
 */
function move(element, moveX, moveY, duration) {
    new Effect.Move($(element),{sync: false, x: moveX, y: moveY, mode: 'relative', duration: (duration?duration:0.2)});
}

/**
 * Move an array of elems in parallele by given x and y deltas
 * @param {Array} elements, array of elements to move
 * @param {Object} toX
 * @param {Object} toY
 * @param {Object} duration in seconds (default 1)
 */
function moveMany(elements, moveX, moveY, duration){
    var effects = [];
    for (var i=0; i<elements.length; ++i) {
        effects[i] = new Effect.Move(elements[i],{sync: true, x: moveX, y: moveY, mode: 'relative'});
    }
    new Effect.Parallel(
        effects, {duration: duration?duration:1.0}
    );
}

///////////////////////////////////////////
// Fade in and out
///////////////////////////////////////////

/**
 * fade element out
 * @param {Object} element or element id
 * @param {Object} duration in seconds (default 1 second)
 * @param {Object} fadeTo opacity (default 0)
 */
function fade(element, duration, fadeTo) {
    $(element).fade({ duration: duration?duration:1.0, from: 1, to: fadeTo?fadeTo:0 });
} 

/**
 * Fade an array of elems 
 * @param {Array} elements, array of elements to move
 * @param {Object} duration (default 1 second)
 * @param {Object} fadeTo opacity (default 0)
 */
function fadeMany(elements, duration, fadeTo){
    var effects = [];
    for (var i=0; i<elements.length; ++i) {
        effects[i] = new Effect.Fade(elements[i],{sync: true, from: 1, to: fadeTo?fadeTo:0});
    }
    new Effect.Parallel(
        effects, {duration: duration?duration:1.0}
    );
}

/**
 * fade element in
 * @param {Object} element or element id
 * @param {Object} duration in seconds (default 1 second)
 * @param {Object} appearTo opacity (default 1)
 */
function appear(element, duration, appearTo) {
    $(element).appear({ duration: duration?duration:1.0, from: 0, to: appearTo?appearTo:1 });
}

/**
 * make an array of elems appear gradually
 * @param {Array} elements, array of elements to appear
 * @param {Object} duration (default 1 second)
 * @param {Object} appearTo opacity (default 1)
 */
function appearMany(elements, duration, appearTo){
    var effects = [];
    for (var i=0; i<elements.length; ++i) {
        effects[i] = new Effect.Appear(elements[i],{sync: true, from: 0, to: appearTo?appearTo:1});
    }
    new Effect.Parallel(
        effects, {duration: duration?duration:1.0}
    );
}


/**
 * Causes a element to pulsate
 * @param element the element we want the pulsating to appear on
 * @param numberOfPulses number of pulses in the duration
 * @param duration time in secs
 */
function pulsate(element, numberOfPulses, duration){
    var id = $(element).identify();
    Effect.Pulsate(id, { pulses: numberOfPulses ? numberOfPulses : 5, duration: duration ? duration : 2.0 });
}



///////////////////////////////////////////
// Mouse pointer effects
///////////////////////////////////////////


/**
 * render this image directly over the mousepointer
 * @param {Object} img - actual image or imageId
 * @param {Object} event
 * @return image - assign to var for future clean up
 */
function renderImageOverMousePointer(img, event) {
    var img = $(img);
    img.show();
    img.style.left=event.clientX;
    img.style.top=event.clientY;
    return img;
}
