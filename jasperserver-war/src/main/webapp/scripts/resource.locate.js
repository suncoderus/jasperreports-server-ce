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

/* global _, resource, picker*/

var resourceLocator = {
    CONTENT_REPOSITORY : 'CONTENT_REPOSITORY',
    LOCAL : 'LOCAL',
    NONE : 'NONE',
    FILE_SYSTEM : 'FILE_SYSTEM',
    LOCATE_EVENT : 'resource:locate',
    ALLOWED_FILE_RESOURCE_EXTENSIONS: [
        "css", //css
        "ttf", //font
        "jpg", "jpeg", "gif", "bmp", "png", //img
        "jar", //jar
        "jrxml", //jrxml
        "properties", //properties
        "jrtx", //jrtx
        "xml", "agxml", //xml
        "docx", "doc", "ppt", "pptx", "xls", "xlsx", "ods", "odt", "odp", "pdf", "rtf", "html" //contentResource
    ],

    /*
     * Initializes Resource Locator common logic.
     * @param options Object with following properties:
     *     options = {
     *          fileUploadInput : 'filePath',
     *          resourceInput : 'resourceUri',
     *          browseButton : 'browser_button',
     *          newResourceLink : 'link_id',
     *          treeId : 'resourceBrowserTreeId',
     *          providerId : 'treeProviderId',
     *          dialogTitle : 'Select Resource From Repository'
     *      }
     *
     */
    initialize: function(options) {
		var get = function(element) {
			return _.isObject(element) ? element : $(element);
		};

        this.resourceUri = get(options.resourceInput);
        this.browseButton = get(options.browseButton);
        this.filePath = get(options.fileUploadInput);
        this.fakeFilePath = get(options.fakeFileUploadInput);
        this.fakeFileInput = get(options.fakeFileUploadInputText);
        this.newResourceLink = get(options.newResourceLink);

        try {
            this._initFileSelector(options);
        } catch(e) {
            /* bypass logging to console */
        } finally {
            this._initEvents(options);
        }

        return this;
    },

    _initEvents: function(options) {
        jQuery(document).on("click", "#CONTENT_REPOSITORY, #FILE_SYSTEM, #NONE, #LOCAL", this._clickHandler);
        if (options.providerId === "fileResourceTreeDataProvider") {
            jQuery("#next").on("click", resourceLocator._nextClickHandler);
            jQuery("#filePath").on("change", resourceLocator._uploadChangeHandler);
        }
    },

    _nextClickHandler: function(event) {
        if (jQuery("#fileUpload").hasClass("error")) {
            event.preventDefault();
        }
    },

    _uploadChangeHandler: function(event) {
        jQuery("#fileUpload").removeClass("error");

        var validFileName = true;

        var fileName = jQuery("#filePath")[0].value;
        if (fileName) {
            var match = jQuery("#filePath")[0].value.match(/.*\.([^\.]+)$/);
            if (match) {
                var extension = match[1];
                if (_.indexOf(resourceLocator.ALLOWED_FILE_RESOURCE_EXTENSIONS, extension) < 0) {
                    validFileName = false;
                }
            } else {
                validFileName = false;
            }
        } else {
            validFileName = false;
        }

        if (!validFileName) {
            var message =
                resource.messages['resource.report.unsupportedFileType.error'] +
                    " " +resourceLocator.ALLOWED_FILE_RESOURCE_EXTENSIONS.join(", ");

            jQuery("#fileUpload").addClass("error").find("span.warning")
                .html(message);
        }
    },

    _clickHandler : function(event) {
        resourceLocator._updateResourceSelectorState(event.target.id);
    },

    _updateResourceSelectorState : function(id) {
        // Update File upload component state.
        resource.switchButtonState(this.filePath, id === this.FILE_SYSTEM);
        resource.switchButtonState(this.fakeFilePath, id === this.FILE_SYSTEM);
        resource.switchButtonState(this.fakeFileInput, id === this.FILE_SYSTEM);
        // Update Resource selector component state: button + text input.
        resource.switchButtonState(this.browseButton, id === this.CONTENT_REPOSITORY);
        resource.switchDisableState(this.resourceUri, id !== this.CONTENT_REPOSITORY);
        // Update Create new resource link state.
        var classes = id === this.LOCAL ? ['disabled', 'launcher'] : ['launcher', 'disabled'];
        this._switchElementClasses(this.newResourceLink, classes);
    },

    _initFileSelector : function(options) {
        this.fileSelector = new picker.FileSelector(_.extend({}, options, {
			uriTextboxId: this.resourceUri,
			browseButtonId: this.browseButton,
			title: options.dialogTitle
		}));
    },

	remove : function(options) {
		this.fileSelector.remove();
	},

	///////////////////////////
    // Utility methods.
    ///////////////////////////
    _switchElementClasses : function(element, classes) {
        element && classes && element.removeClassName(classes[0]).addClassName(classes[1]);
    }
};
