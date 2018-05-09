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
 * @author: Yuriy Plakosh
 * @version: $Id$
 */

/* global layoutModule, buttonManager */

/*
 * Search box class.
 *
 * @param options {JSON Object} - Set of options for search box:
 * <ul>
 *      <li>id - the identifier of search box form element in the DOM</li>
 * </ul>
 */

var SearchBox = function(options) {
    this._id = options.id;
    this._dom = $(this._id);
    this._enabled = true;

    this._process();
    this._assignHandlers();
};

/**
 * Processes DOM.
 */
SearchBox.addMethod('_process', function() {
    this._input = this._dom.select('input')[0];
    this._clearButton = this._dom.select('.button.searchClear')[0];
    this._searchButton = this._dom.select('.button.search')[0];
});

/**
 * Assigns handlers.
 */
SearchBox.addMethod('_assignHandlers', function() {
    this._dom.observe('submit', function(e) {
        Event.stop(e);    
    });

    this._input.observe("keypress", this._keyPressHandler.bindAsEventListener(this));

    this._clearButton.observe('click', this._clearHandler.bindAsEventListener(this));
    this._searchButton.observe('click', this._searchHandler.bindAsEventListener(this));
});

/**
 * Key pressed handler for input field.
 */
SearchBox.addMethod('_keyPressHandler', function(e) {
    if (this._enabled && Event.KEY_RETURN == e.keyCode) {
        this._doSearch();

        // Fix to prevent click event handling on clear button when enter key is pressed in the input field.
        Event.stop(e);
    }
});

/**
 * Updates visibility of clear button.
 */
SearchBox.addMethod('_updateClearButtonVisibility', function() {
    if (!this.getText().blank()) {
        this._clearButton.addClassName(layoutModule.UP_CLASS);
    } else {
        this._clearButton.removeClassName(layoutModule.UP_CLASS);
    }
});

/**
 * Click handler for clear button.
 */
SearchBox.addMethod('_clearHandler', function() {
    if (this._enabled) {
        this.setText("");
        this._doSearch();
    }
});

/**
 * Click handler for search button.
 */
SearchBox.addMethod('_searchHandler', function() {
    if (this._enabled) {
        this._doSearch();
    }
});

/**
 * Gets search box text.
 */
SearchBox.addMethod('getText', function() {
    return this._input.getValue();
});

/**
 * Sets search box text.
 */
SearchBox.addMethod('setText', function(text) {
    this._input.setValue(text);
    this._updateClearButtonVisibility();
});

/**
 * Enables search box.
 */
SearchBox.addMethod('enable', function() {
    buttonManager.enable(this._clearButton);
    buttonManager.enable(this._searchButton);
    this._input.writeAttribute(layoutModule.READONLY_ATTR_NAME, null);
    this._dom.writeAttribute(layoutModule.DISABLED_ATTR_NAME, null);
    
    this._enabled = true;
});

/**
 * Disables search box.
 */
SearchBox.addMethod('disable', function() {
    buttonManager.disable(this._clearButton);
    buttonManager.disable(this._searchButton);
    this._input.writeAttribute(layoutModule.READONLY_ATTR_NAME, layoutModule.READONLY_ATTR_NAME);
    this._dom.writeAttribute(layoutModule.DISABLED_ATTR_NAME, layoutModule.DISABLED_ATTR_NAME);

    this._enabled = false;
});

/**
 * Does search action.
 */
SearchBox.addMethod('_doSearch', function(text) {
    this._updateClearButtonVisibility();
    this.onSearch(this.getText());
});

/**
 * Is invoked when search action was performed (search button pressed, clear button pressed, enter key pressed in the
 * text input field). This method should be replaced in the instance of SearchBox class to handle search action.
 *
 * @param text the text in the search box.
 */
SearchBox.addMethod('onSearch', function(text) {
    // No-op.
});
