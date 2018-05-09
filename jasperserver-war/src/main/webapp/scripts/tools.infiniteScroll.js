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
 * @author: Yuriy Plakosh, Stas Chubar
 * @version: $Id$
 */

/**
 * Infinite scroll class. It automatically detects if content to scroll changed after onLoad method was invoked
 * to prevent unnecessary invocations of onLoad method.   
 *
 * @param options (JSON object) - set of options for infinite scroll:
 * <ul>
 *      <li>id - the identifier of the control element which has scroll,</li>
 *      <li>loadFactor - load factor to invoke onLoad method (optional). By default it is 0.95,</li>
 *      <li>contentId - the identifier of the content element inside control with scroll (optional). By default
 *          first child element is treated as content element.</li>
 * </ul>
 *
 */

/* global define, isSupportsTouch, isIPad */

define(function (require) {

	"use strict";

	require("prototype");
	require("utils.common");

	var InfiniteScroll = function (options) {
		this._id = options.id;
		this._contentId = options.contentId ? options.contentId : undefined;
		this._scroll = options.scroll;
		this._loadFactor = options.loadFactor ? options.loadFactor : 0.95;

		this._lastContentHeight = 0;
		this._loading = false;

		// getting control from 'id' of jquery object or from DOM element
		if (options.control) {
			if (options.control.length !== undefined && options.control.length > 1) {
				this._control = $(options.control[0]);
			} else {
				this._control = $(options.control);
			}
		} else {
			this._control = this._scroll ? this._scroll.parent : $(this._id);
		}


		if (this._scroll) {
			this._content = this._scroll.element;
		} else {

			// getting content from 'id' of jquery object or from DOM element
			if (options.content) {
				if (options.content.length !== undefined && options.content.length > 1) {
					this._content = $(options.content[0]);
				} else {
					this._content = $(options.content);
				}
			} else {
				this._content = this._contentId ? $(this._contentId) : this._control.childElements()[0];
			}
		}

		this._eventType = isSupportsTouch() ? "touchmove" : "scroll";
		this._control.observe(this._eventType, this._onScrollHandler.bind(this));
	};

	/**
	 * Destroys the infinite scroll.
	 */
	InfiniteScroll.addMethod('destroy', function () {
		if (this._control) {
			this._control.stopObserving(this._eventType, this._handler);
		}
	});

	/**
	 * Handler for scroll event on the control element.
	 */
	InfiniteScroll.addMethod('_onScrollHandler', function (e) {
		var controlScrollTop = this._scroll ? (this._scroll.y * -1) : this._control.scrollTop;
		var contentHeight = this._content.getHeight();
		var controlHeight = this._control.getHeight();
		if (contentHeight != this._lastContentHeight) {
			this._loading = false;
		}

		var allowLoad = !this._loading && contentHeight != 0;

		if (this._scroll) {
			allowLoad = allowLoad && this._scroll.isBottom();
		} else {
			allowLoad = allowLoad && (controlHeight + controlScrollTop) / contentHeight > this._loadFactor;
		}

		if (allowLoad) {
			this._loading = true;
			this._lastContentHeight = contentHeight;
			isIPad() && this.wait();
			this.onLoad();
		}
	});

	/**
	 * This method is invoked when new data should be loaded. Actually it should load new data. The method should be
	 * replaced in the instance of InfiniteScroll class. Next invocation of onLoad method will not occur till the content
	 * element height changed.
	 */
	InfiniteScroll.addMethod('onLoad', doNothing);

	/**
	 * Resets the infinite scroll for totally new content. This method should be invoked in case the old data is cleaned
	 * and the new data is set to the content element. Do not invoke this method in case new data is added in onLoad
	 * method - this case is processed automatically.
	 *
	 * The method also moves the slider of the scroll in control element to top position.
	 */
	InfiniteScroll.addMethod('reset', function () {
		isIPad() ? this._scroll.reset() : this._control.scrollTop = 0;
		this._loading = false;
	});

	InfiniteScroll.addMethod('wait', function () {
		if (!this._waitIndicator) {
			this._waitIndicator = new Element('div', {'class': 'dimmer resultsOverlay'});
			var dimensions = this._control.getDimensions();
			var offsets = this._control.positionedOffset();

			this._waitIndicator.setStyle({
				zIndex: '4000',
				top: offsets.top + 'px',
				left: offsets.left + 'px',
				height: dimensions.height + 'px',
				width: dimensions.width + 'px'
			});

			this._control.insert({after: this._waitIndicator});
		}
		this._waitIndicator.show();
	});

	InfiniteScroll.addMethod('stopWaiting', function () {
		this._waitIndicator && this._waitIndicator.hide();
	});

	// TODO: remove it when we move Repository Search into AMD style
	// Make it global due to RepositorySearch modules
	window.InfiniteScroll = InfiniteScroll;

	return InfiniteScroll;
});