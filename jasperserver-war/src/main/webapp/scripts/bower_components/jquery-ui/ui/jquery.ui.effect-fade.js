/*!
 * jQuery UI Effects Fade @VERSION
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/fade-effect/
 *
 * Depends:
 *	jquery.ui.effect.js
 */
///////////////////////////////////////////////////////////////////////
//Jaspersoft Updates (look for comment: JASPERSOFT #x)
///////////////////////////////////////////////////////////////////////
// JASPERSOFT #1 add AMD-wrapper to head
// JASPERSOFT #2 add AMD-wrapper to bottom
///////////////////////////////////////////////////////////////////////

//JASPERSOFT #1
define(function(require) {

		var $ = require("./jquery.ui.effect");
//JASPERSOFT #1 END

$.effects.effect.fade = function( o, done ) {
	var el = $( this ),
		mode = $.effects.setMode( el, o.mode || "toggle" );

	el.animate({
		opacity: mode
	}, {
		queue: false,
		duration: o.duration,
		easing: o.easing,
		complete: done
	});
};

//JASPERSOFT #2
		return $;
});
//JASPERSOFT #2 END

