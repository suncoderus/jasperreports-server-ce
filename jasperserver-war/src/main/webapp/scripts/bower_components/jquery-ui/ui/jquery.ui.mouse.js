/*!
 * jQuery UI Mouse @VERSION
 * http://jqueryui.com
 *
 * Copyright 2014 jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/mouse/
 *
 * Depends:
 *	jquery.ui.widget.js
 */

///////////////////////////////////////////////////////////////////////
//Jaspersoft Updates (look for comment: JASPERSOFT #x)
///////////////////////////////////////////////////////////////////////
// JASPERSOFT #1 add AMD-wrapper to head
// JASPERSOFT #2 add AMD-wrapper to bottom
// JASPERSOFT #3 JS-33242: adding fix for handling mouse up events in case of scrolling. Only IE case
///////////////////////////////////////////////////////////////////////

//JASPERSOFT #1 #3
define(function (require) {

	var $ = require("./jquery.ui.widget");

	var isIE = function () {
		return navigator.appName === "Microsoft Internet Explorer" || navigator.userAgent.indexOf("Trident/") >= 0;
	};

	var detectedIEVersion = false;

	var getScrollbarWidth = function () {
		var outer = document.createElement("div");
		outer.style.visibility = "hidden";
		outer.style.width = "100px";
		outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

		document.body.appendChild(outer);

		var widthNoScroll = outer.offsetWidth;
		// force scrollbars
		outer.style.overflow = "scroll";

		// add innerdiv
		var inner = document.createElement("div");
		inner.style.width = "100%";
		outer.appendChild(inner);

		var widthWithScroll = inner.offsetWidth;

		// remove divs
		outer.parentNode.removeChild(outer);

		return widthNoScroll - widthWithScroll;
	};
	var _detectedScrollbarSize = getScrollbarWidth();

	var getIEVersion = function () {
		var version = 0;
		if (detectedIEVersion !== false) {
			return detectedIEVersion;
		}

		if (isIE()) {
			if (navigator.appName === "Netscape") {
				var ua = navigator.userAgent;
				var re = new RegExp("Trident/.*rv:([0-9]{1,}[\\.0-9]{0,})");
				if (re.exec(ua) != null) {
					version = parseFloat(RegExp.$1);
				}
			} else {
				var msVersion = navigator.appVersion.split("MSIE")[1];
				version = parseFloat(msVersion);
			}
		}

		detectedIEVersion = version;
		return version;
	};

	var isIE11 = function () {
		return getIEVersion() === 11;
	};
	var inRect = function (rect, x, y) {
		return (y >= rect.top && y <= rect.bottom) && (x >= rect.left && x <= rect.right);
	};

	var hasScroll = function ($element, axis) {
		var overflow = $element.css("overflow"),
			overflowAxis;

		if (typeof axis === "undefined" || axis === "y") {
			overflowAxis = $element.css("overflow-y");
		} else {
			overflowAxis = $element.css("overflow-x");
		}

		var bShouldScroll = $element.get(0).scrollHeight > $element.innerHeight();

		var bAllowedScroll = (overflow === "auto" || overflow === "visible") ||
			(overflowAxis === "auto" || overflowAxis === "visible");

		var bOverrideScroll = overflow === "scroll" || overflowAxis === "scroll";

		return (bShouldScroll && bAllowedScroll) || bOverrideScroll;
	};

	var inScrollRange = function (event) {

		var
			clickPointX = event.pageX,
			clickPointY = event.pageY,
			$element = $(event.target),
			hasVerticalScroll = hasScroll($element),
			hasHorizontalScroll = hasScroll($element, "x"),
			horizontalScroll = null,
			verticalScroll = null;

		if (hasVerticalScroll) {
			verticalScroll = {};
			verticalScroll.top = $element.offset().top;
			verticalScroll.right = $element.offset().left + $element.outerWidth();
			verticalScroll.bottom = verticalScroll.top + $element.outerHeight();
			verticalScroll.left = verticalScroll.right - _detectedScrollbarSize;

			if (hasHorizontalScroll) {
				//verticalScroll.bottom -= scrollSize;
			}

			if (inRect(verticalScroll, clickPointX, clickPointY)) {
				return true;
			}
		}

		if (hasHorizontalScroll) {
			horizontalScroll = {};
			horizontalScroll.bottom = $element.offset().top + $element.outerHeight();
			horizontalScroll.left = $element.offset().left;
			horizontalScroll.top = horizontalScroll.bottom - _detectedScrollbarSize;
			horizontalScroll.right = horizontalScroll.left + $element.outerWidth();

			if (hasVerticalScroll) {
				//horizontalScroll.right -= scrollSize;
			}

			if (inRect(horizontalScroll, clickPointX, clickPointY)) {
				return true;
			}
		}

		return false;
	};
//JASPERSOFT #1 #3 END


var mouseHandled = false;
$( document ).mouseup( function() {
	mouseHandled = false;
});

$.widget("ui.mouse", {
	version: "@VERSION",
	options: {
		cancel: "input,textarea,button,select,option",
		distance: 1,
		delay: 0
	},
	_mouseInit: function() {
		var that = this;

//JASPERSOFT #3
		if (isIE11()) {
			this._mousePressedOnScroll = false;
			$(document).mousemove(this._mouseMove_IE_Fix.bind(this));
			this.element.mousemove(this._mouseMove_IE_Fix.bind(this));
		}
//JASPERSOFT #3 END

		this.element
			.bind("mousedown."+this.widgetName, function(event) {
				return that._mouseDown(event);
			})
			.bind("click."+this.widgetName, function(event) {
				if (true === $.data(event.target, that.widgetName + ".preventClickEvent")) {
					$.removeData(event.target, that.widgetName + ".preventClickEvent");
					event.stopImmediatePropagation();
					return false;
				}
			});

		this.started = false;
	},

//JASPERSOFT #3
	_mouseMove_IE_Fix: function () {
		if (this._mousePressedOnScroll) {
			this._mouseUpDelegate();
			this._mousePressedOnScroll = false;
		}
	},
//JASPERSOFT #3 END

	// TODO: make sure destroying one instance of mouse doesn't mess with
	// other instances of mouse
	_mouseDestroy: function() {
		this.element.unbind("."+this.widgetName);
		if ( this._mouseMoveDelegate ) {
			$(document)
				.unbind("mousemove."+this.widgetName, this._mouseMoveDelegate)
				.unbind("mouseup."+this.widgetName, this._mouseUpDelegate);
		}
	},

	_mouseDown: function(event) {
		// don't let more than one widget handle mouseStart
		if( mouseHandled ) { return; }

		// we may have missed mouseup (out of window)
		(this._mouseStarted && this._mouseUp(event));

		this._mouseDownEvent = event;

		var that = this,
			btnIsLeft = (event.which === 1),
			// event.target.nodeName works around a bug in IE 8 with
			// disabled inputs (#7620)
			elIsCancel = (typeof this.options.cancel === "string" && event.target.nodeName ? $(event.target).closest(this.options.cancel).length : false);
		if (!btnIsLeft || elIsCancel || !this._mouseCapture(event)) {
			return true;
		}

		this.mouseDelayMet = !this.options.delay;
		if (!this.mouseDelayMet) {
			this._mouseDelayTimer = setTimeout(function() {
				that.mouseDelayMet = true;
			}, this.options.delay);
		}

		if (this._mouseDistanceMet(event) && this._mouseDelayMet(event)) {
			this._mouseStarted = (this._mouseStart(event) !== false);
			if (!this._mouseStarted) {
				event.preventDefault();
				return true;
			}
		}

		// Click event may never have fired (Gecko & Opera)
		if (true === $.data(event.target, this.widgetName + ".preventClickEvent")) {
			$.removeData(event.target, this.widgetName + ".preventClickEvent");
		}

		// these delegates are required to keep context
		this._mouseMoveDelegate = function(event) {
			return that._mouseMove(event);
		};
		this._mouseUpDelegate = function(event) {
			return that._mouseUp(event);
		};
		$(document)
			.bind("mousemove."+this.widgetName, this._mouseMoveDelegate)
			.bind("mouseup."+this.widgetName, this._mouseUpDelegate);

//JASPERSOFT #3
            if (isIE11()) {
                if (inScrollRange(event)) {
                    this._mousePressedOnScroll = true;
                }
            }
//JASPERSOFT #3 END

		event.preventDefault();

		mouseHandled = true;
		return true;
	},

	_mouseMove: function(event) {
		// IE mouseup check - mouseup happened when mouse was out of window
		if ($.ui.ie && ( !document.documentMode || document.documentMode < 9 ) && !event.button) {
			return this._mouseUp(event);
		}

		if (this._mouseStarted) {
			this._mouseDrag(event);
			return event.preventDefault();
		}

		if (this._mouseDistanceMet(event) && this._mouseDelayMet(event)) {
			this._mouseStarted =
				(this._mouseStart(this._mouseDownEvent, event) !== false);
			(this._mouseStarted ? this._mouseDrag(event) : this._mouseUp(event));
		}

		return !this._mouseStarted;
	},

	_mouseUp: function(event) {
		$(document)
			.unbind("mousemove."+this.widgetName, this._mouseMoveDelegate)
			.unbind("mouseup."+this.widgetName, this._mouseUpDelegate);

		if (this._mouseStarted) {
			this._mouseStarted = false;

			if (event.target === this._mouseDownEvent.target) {
				$.data(event.target, this.widgetName + ".preventClickEvent", true);
			}

			this._mouseStop(event);
		}

		return false;
	},

	_mouseDistanceMet: function(event) {
		return (Math.max(
				Math.abs(this._mouseDownEvent.pageX - event.pageX),
				Math.abs(this._mouseDownEvent.pageY - event.pageY)
			) >= this.options.distance
		);
	},

	_mouseDelayMet: function(/* event */) {
		return this.mouseDelayMet;
	},

	// These are placeholder methods, to be overriden by extending plugin
	_mouseStart: function(/* event */) {},
	_mouseDrag: function(/* event */) {},
	_mouseStop: function(/* event */) {},
	_mouseCapture: function(/* event */) { return true; }
});

//JASPERSOFT #2
	return $;
});
//JASPERSOFT #2 END
