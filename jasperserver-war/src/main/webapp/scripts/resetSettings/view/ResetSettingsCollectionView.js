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
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function(require) {
    var $ = require("jquery"),
        _ = require("underscore"),
        Backbone = require("backbone"),
        i18n = require("bundle!EditSettingsBundle"),
        BaseTable = require("common/component/baseTable/BaseTable"),
        collectionViewTemplate = require("text!resetSettings/templates/collectionViewTemplate.htm"),
        OptionContainer = require("common/component/base/OptionContainer"),
        AlertDialog = require("common/component/dialog/AlertDialog"),
        Notification = require("common/component/notification/Notification"),
        confirmDialogTypesEnum = require("serverSettingsCommon/enum/confirmDialogTypesEnum"),
        confirmationDialogFactory = require("resetSettings/factory/confirmationDialogFactory");

    require("css!attributes.css");

    var ResetSettingsCollectionView = BaseTable.extend({
        template: _.template(collectionViewTemplate),

        templateHelpers: function() {
            return {
                i18n: i18n
            };
        },

        childEvents: {
            "open:confirm": "_openConfirm"
        },

        initialize: function(options) {
            BaseTable.prototype.initialize.apply(this, arguments);

            this.notification = new Notification();
            this.alertDialog = new AlertDialog();
            this.model = new Backbone.Model();
            this.changedViews = [];

            !_.isEmpty(options.buttons) && options.buttonsContainer && this._initButtons(options);

            this._initConfirmationDialogs();
            this.tooltip && this._initTooltipEvents();
            this._initEvents();
        },

        fetchData: function() {
            return this.collection.fetch({reset: true, headers: {Accept: "application/attributes.collection.hal+json"}});
        },

        saveChildren: function() {
            var self = this,
                allModels = [];

            _.each(self.changedViews, function(view) {
                allModels.push(view.model);
            }, self);

            allModels.length
            && self.collection.save(allModels)
                .done(_.bind(self._successAjaxCallback, self))
                .fail(_.bind(self._errorAjaxCallback, self));
        },

        revertChanges: function() {
            var length = this.changedViews.length;

            for (var i = length - 1; i >= 0; i--) {
                this._revertViewRemoval(this.changedViews[i]);
            }

            this._resetChangedList();
        },

        containsUnsavedItems: function() {
            return !!this.changedViews.length;
        },

        remove: function() {
            $(window).off("beforeunload", this._onPageLeave);

            this.confirmationDialog && this.confirmationDialog.remove();
            this.notification && this.notification.remove();
            this.alertDialog && this.alertDialog.remove();

            BaseTable.prototype.remove.apply(this, arguments);
        },

        _initEvents: function() {
            $(window).on("beforeunload", _.bind(this._onPageLeave, this));
        },

        _initTooltipEvents: function() {
            this.listenTo(this, "childview:mouseover", this._onChildViewMouseOver);
            this.listenTo(this, "childview:mouseout", this._onChildViewMouseOut);
        },

        _initConfirmationDialogs: function() {
            this.confirmationDialogs = {};

            _.each(confirmDialogTypesEnum, function(type) {
                this.confirmationDialogs[type] = confirmationDialogFactory(type);
            }, this);

            this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.DELETE_CONFIRM], "button:yes", this._onDeleteConfirm);
            this.listenTo(this.confirmationDialogs[confirmDialogTypesEnum.CANCEL_CONFIRM], "button:yes", this.revertChanges);

            this._initPermissionConfirmEvents && this._initPermissionConfirmEvents();
        },

        _successAjaxCallback: function(data) {
            this.notification.show({message: i18n["editSettings.notification.message.saved"], type: "success"});

            this._resetChangedList();
        },

        _errorAjaxCallback: function(response) {
            var msg;

            switch (response.status) {
                case 401:
                    msg = i18n["editSettings.error.message.not.authenticated"];
                    break;
                default:
                    msg = i18n["editSettings.error.message.unknown.error"];
                    break;
            }

            this.alertDialog.setMessage(msg);
            this.alertDialog.open();
        },

        _findChildrenByModel: function(model) {
            model = _.isString(model) ? this._findModelsWhere({name: model}) : model;

            return model && this.children.findByModel(model);
        },

        _resetChangedList: function() {
            this.changedViews.length = 0;

            this.toggleButtons();
        },

        _revertViewRemoval: function(view) {
            this._deleteViewFromChangedList(view);
            this.collection.add(view.model, {at: view.indexAt});
        },

        _openConfirm: function(childView, type) {
            this.model.set("changedChildView", childView);

            var confirmDialog = this.confirmationDialogs[type],
                openConfirm = type !== confirmDialogTypesEnum.CANCEL_CONFIRM ? true : this.containsUnsavedItems();

            type === confirmDialogTypesEnum.DELETE_CONFIRM
                && confirmDialog.setContent(_.template(i18n["editSettings.confirm.delete.dialog.text"], {
                    name: childView.model.get("name")
                }));

            openConfirm && confirmDialog.open();
        },

        _saveChildViewToChangedList: function(childView, modelChanged) {
            var index = _.indexOf(this.changedViews, childView);

            index !== -1
                ? (!modelChanged && this._deleteViewFromChangedList(childView, index))
                : modelChanged && this.changedViews.push(childView);

            this.toggleButtons();
        },

        _deleteViewFromChangedList: function(childView, index) {
            index = index || _.indexOf(this.changedViews, childView);

            index !== -1 && this.changedViews.splice(index, 1);
        },

        _removeModel: function(model) {
            this.collection.remove(model);
        },

        _onPageLeave: function(e) {
            if (this.containsUnsavedItems()) {
                (e || window.event).returnValue = i18n["editSettings.dialog.unsaved.changes"];
                return i18n["editSettings.dialog.unsaved.changes"];
            }
        },

        _onDeleteConfirm: function() {
            var childView = this.model.get("changedChildView"),
                model = childView.model;

            childView.indexAt = this.collection.indexOf(model);

            this._saveChildViewToChangedList(childView, true);

            this._removeModel(model);
        },

        _onChildViewMouseOver: function(view, model, e) {
            var $parentTableColumn = $(e.target).closest(".table-column"),
                prop;

            $parentTableColumn.hasClass("name") && (prop = "name");
            $parentTableColumn.hasClass("value") && (prop = "value");

            this.tooltip.show(_.pick(model.toJSON(), prop));
        },

        _onChildViewMouseOut: function(view, model, e) {
            this.tooltip.hide();
        }
    });

    return ResetSettingsCollectionView;
});