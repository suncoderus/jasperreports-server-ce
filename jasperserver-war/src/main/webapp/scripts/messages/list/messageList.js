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

/* global toolbarButtonModule, layoutModule, matchAny, dynamicList, ajaxTargettedUpdate, alert, AjaxRequester,
 baseErrorHandler, localContext
*/

var messageListModule = {
    _list: null,
    _messageListId: "messageList",
    _flowExecutionKey: null,
    _systemConfirmMessages: [],

    toolbar: {
        _buttons: null,
        _id: "toolbar",

        initialize: function() {
            toolbarButtonModule.initialize({});
            this._buttons = document.body.select(layoutModule.TOOLBAR_CAPSULE_PATTERN);
            this._initEventHandlers();
        },

        refresh: function() {
            this._buttons.each(function(button) {
                toolbarButtonModule.setButtonState(button, messageListModule.hasSelectedMessages());
            }.bind(this));
        },

        _initEventHandlers: function() {
            $(this._id).observe('click', function(e) {
                var button = matchAny(e.element(), [layoutModule.BUTTON_PATTERN], true);

                var actionName = button.identify();
                messageListModule.doAction(actionName,
                        Object.toQueryString({"selectedIds": messageListModule.getSelectedIds()}));
            }.bindAsEventListener(this));
        }
    },

    filter: {
        _id: "messageFilter",

        initialize: function() {
            this._initEventHandlers();
        },

        _initEventHandlers: function() {
            $(this._id).observe('change', function(e) {
                var element = e.element();

                messageListModule.doAction('changeEventsType',
                        Object.toQueryString({"messageFilter": $(this._id).getValue()}));
            }.bindAsEventListener(this));
        }
    },

    initialize: function(options) {
        this._flowExecutionKey = options.flowExecutionKey;
        this._systemConfirmMessages = options.systemConfirmMessages;

        this._list = new dynamicList.List(this._messageListId, {
            listTemplateDomId: "list_fourColumn_type_message",
            itemTemplateDomId: "list_fourColumn_type_message:unread",
            multiSelect: true
        });

        var baseSelectItem = this._list.selectItem;
        this._list.selectItem = function(item) {
            if (!item.getValue().isHeader) {
                baseSelectItem.apply(this, arguments);
            }
        };

        this._initListEvents();

        this.toolbar.initialize();
        this.filter.initialize();
        this.doAction('getMessages', '');
    },

    _getMessageItems: function(messages) {
        var processItemTemplate = function(element) {
            var subject = element.select('.subject a')[0];
            subject.update(xssUtil.escape(this.getValue().subject));
            subject.writeAttribute('href', 'flow.html?_flowExecutionKey=' + messageListModule._flowExecutionKey + '&_eventId=viewMessage&id=' + this.getValue().id);

            var date = element.select('.date')[0];
            date.update(xssUtil.escape(this.getValue().date));
            date.writeAttribute('title', this.getValue().timestamp);

            var type = element.select('.type')[0];       
            type.update(xssUtil.escape(this.getValue().type));

            var component = element.select('.component')[0];
            component.update(xssUtil.escape(this.getValue().component));
            
            return element;
        };

        // Adding faked message element to the beginning for header.
        messages.unshift({isHeader: true});

        return messages.collect(function(message) {
            var templateDomId;
            if (message.isHeader) {
                templateDomId = "list_fourColumn_type_message:header";
            } else if(message.isRead) {
                templateDomId = "list_fourColumn_type_message:read";
            } else {
                templateDomId = "list_fourColumn_type_message:unread";
            }

            var messageItem = new dynamicList.ListItem({
                cssClassName: layoutModule.LEAF_CLASS,
                value: message,
                templateDomId: templateDomId
            });

            if (message.isHeader) {
                // Header item does not require template processing.
                messageItem.processTemplate = function(element){
                    return element;
                };
            } else {
                messageItem.processTemplate = processItemTemplate;
            }

            return messageItem;
        }.bind(this));
    },

    _initListEvents: function() {
        this._list.observe('item:selected', function(event) {
            this.refreshToolbar();
        }.bindAsEventListener(this));

        this._list.observe('item:unselected', function(event) {
            this.refreshToolbar();
        }.bindAsEventListener(this));
    },

    refreshToolbar: function() {
        messageListModule.toolbar.refresh();
    },

    hasSelectedMessages: function() {
        return this._list.getSelectedItems().length > 0;
    },

    getSelectedIds: function() {
        return this._list.getSelectedItems().collect(function(item) {
            return item.getValue().id;
        });
    },

    doAction: function(actionName, data) {
        var actionURL = 'flow.html?_flowExecutionKey=' + messageListModule._flowExecutionKey + '&_eventId=' + actionName;

        this._list.setItems([]);
        this.refreshToolbar();

        ajaxTargettedUpdate(actionURL, {
            postData: data,
            callback: function(response) {
                if (response.status == 'OK') {
                    this._list.setItems(this._getMessageItems(response.data));
                    this._list.show();
                } else {
                    alert(response.data.message);
                }
            }.bind(this),
            errorHandler: this._serverErrorHandler,
            mode: AjaxRequester.prototype.EVAL_JSON
        });
    },

    _serverErrorHandler: function(ajaxAgent) {
        if (ajaxAgent.getResponseHeader("LoginRequested")) {
            document.location = 'flow.html?_flowId=logEventFlow';
        }

        showErrorPopup = function() { window.alert('Server Error'); }; // jshint ignore: line
        var resolved = baseErrorHandler(ajaxAgent);

        if (!resolved && ajaxAgent.status == 500) {
            document.location = 'flow.html?_flowId=logEventFlow&_eventId=error';
            document.location = 'flow.html?_flowId=logEventFlow&_eventId=error';
        }
    }
};

if (typeof require === "undefined") {
    // prevent conflict with domReady plugin in RequireJS environment
    document.observe('dom:loaded', function() { messageListModule.initialize(localContext.initOptions);});
}
