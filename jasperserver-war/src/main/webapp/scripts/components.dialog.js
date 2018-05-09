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
 * @author: inesterenko
 * @version: $Id$
 */

/* global jaspersoft, _, Backbone, dialogs */

define(function(require){

    "use strict";

    var  $ = require("jquery"),
        _ = require("underscore"),
        Backbone = require("backbone"),
        templateEngine = require("components.templateengine"),
        dialogs = require("components.dialogs");

    var Dialog = Backbone.View.extend({
        rendered : false,
        contentSelector : ".body",
        events: {
            "click .cancel":"hide"
        },

        initialize : function(options){
            this.templateId = options && options.templateId || this.templateId;
            this.contentSelector = options && options.contentSelector || this.contentSelector;
            if (!this.templateId) {
                throw "Dialog template is not provided";
            }
            _.bindAll(this);
            this.options = _.extend({}, options);
        },

        render : function(parentContainer){
            this.undelegateEvents();
            this.$el = $(templateEngine.getTemplateText(this.templateId)).closest("div");
            this.el = this.$el[0];
            $(parentContainer ? parentContainer : document.body).append(this.$el);
            this.delegateEvents();

            this.rendered = true;
            return this;
        },

        hide: function(event){
            dialogs.popup.hide(this.el);
            event && event.stopPropagation();
        },

        show: function(parentContainer){
            if (!this.rendered) {
                this.render(parentContainer);
            }
            dialogs.popup.show(this.el, this.options.modal);
        },

        setContent: function(content) {
            this.$el.find(this.contentSelector).html($(content));
        },
        /*
         * Replace current dialog messages with given ones
         *
         * @param messages Messages array. Each element of the array will be wrapped with <p>
         * @private
         */
        _updateMessage : function(messages) {
            messages = _.isString(messages) ? [messages] : messages;
            var messageWrapper = document.createDocumentFragment();
            _.each(messages || [], function(message) {
                messageWrapper.appendChild($("<p/>", {
                    "text" : message,
                    "class" : "message"
                })[0]);
            }, this);
            this.$el.find(".body").html(messageWrapper);
        }

    });

    /*
     * Simple Confirm Dialog, that is created from template (see jsp/templates/standardConfirm.jsp).
     * You could specify message to be displayed at creation time or during show method invocation.
     *
     * Also, custom OK button handler could be specified during Dialog creation or
     * it could be overridden during show method invocation.
     *
     * @type {*}
     */
    var ConfirmDialog = Dialog.extend({
        templateId : "standardConfirmTemplate",
        events: {
            "click button.cancel":"hide",
            "click button.ok":"onOk"
        },

        initialize : function(options) {
            Dialog.prototype.initialize.call(this, options);
            _.extend(this, _.defaults(options || {}, {
                messages : "",
                ok : (function() {})
            }));
        },
        /*
         * Show dialog with the given message and callback for OK action
         *
         * @param options {messages : [""], ok : function(){}};
         */
        show : function(options) {
            Dialog.prototype.show.call(this);
            this._updateMessage(options.messages || this.messages);
            options.ok && (this.ok = options.ok);
        },

        onOk : function() {
            this.hide();
            this.ok();
        }
    });

    //keep that old-school for backwarcompatibility
    jaspersoft || (jaspersoft = {components:{}}); // jshint ignore: line
    jaspersoft.components || (jaspersoft.components = {});

    jaspersoft.components.Dialog = Dialog;
    jaspersoft.components.ConfirmDialog = ConfirmDialog;
    
    return Dialog;

});
