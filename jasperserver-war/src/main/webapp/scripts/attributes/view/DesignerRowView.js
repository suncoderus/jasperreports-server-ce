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
    var _ = require("underscore"),
        $ = require("jquery"),
        RowView = require("attributes/view/RowView"),
        DeleteConfirm = require("serverSettingsCommon/behaviors/DeleteConfirmBehavior"),
        confirmDialogTypesEnum = require("serverSettingsCommon/enum/confirmDialogTypesEnum"),
        permissionMasksEnum = require("attributes/enum/permissionMasksEnum"),
        rowTemplatesFactory = require("attributes/factory/rowTemplatesFactory");

    var SECURE_VALUE_SUBSTITUTION = "~secure~",
        SLIDE_UP_ROW_HEIGHT = "28px",
        ROW_HEIGHT = "100%";

    var DesignerRowView = RowView.extend({
        tagName: "div",
        className: "table-row",

        ui: {
            passwordInput: "input[type='password']"
        },

        events: _.extend({}, RowView.prototype.events, {
            "click .edit": "toggleActive",
            "click .cancel": "cancel",
            "click .ok": "runValidation",
            "click @ui.passwordInput": "toggleValue",
            "blur @ui.passwordInput": "toggleValue"
        }),

        modelEvents: {
            "change": "_onModelChange"
        },

        behaviors: _.extend(RowView.prototype.behaviors, {
            DeleteConfirm: {
                behaviorClass: DeleteConfirm
            }
        }),

        computeds: _.extend(RowView.prototype.computeds, {
            readOnly: {
                deps: ["inherited", "permissionMask"],
                get: function(inherited, permissionMask) {
                    var isReadOnly = permissionMask === permissionMasksEnum.READ_ONLY;

                    return isReadOnly || (inherited && isReadOnly);
                }
            },
            disableRemove: {
                deps: ["inherited", "permissionMask"],
                get: function(inherited, permissionMask) {
                    return inherited || permissionMask === permissionMasksEnum.READ_ONLY;
                }
            }
        }),

        initialize: function(options) {
            this.editMode = false;

            this._onViewInitialize && this._onViewInitialize();

            RowView.prototype.initialize.apply(this, arguments);
        },

        render: function() {
            RowView.prototype.render.apply(this, arguments);

            this._onViewRender && this._onViewRender();

            return this;
        },

        getTemplate: function() {
            var showRowViewTemplate = !this.editMode && this.model.get("name");

            return _.template(rowTemplatesFactory({editMode: !showRowViewTemplate}));
        },

        toggleActive: function() {
            var dfd = new $.Deferred(),
                self = this;

            this.toggleMode().done(function() {
                self.trigger("active", self.editMode, dfd);
            });

            return dfd;
        },

        toggleMode: function() {
            var dfd = new $.Deferred();

            this.editMode = !this.editMode;

            this.editMode ? this.slideDown(dfd) : this.slideUp(dfd);

            return dfd;
        },

        toggleIfModelIsValid: function() {
            if (this.validateModel()) {

                this.model.setState("confirmedState");

                this.model.isNew() && this.confirmState();

                this.modelChanged = null;

                return this.toggleActive();
            }

            return new $.Deferred();
        },

        slideDown: function(dfd) {
            this.render().hide().slideDown({
                done: function() {
                    dfd.resolve();
                }
            });

            return this;
        },

        slideUp: function(dfd, height) {
            height = height || SLIDE_UP_ROW_HEIGHT;

            var self = this;

            this.$el.animate({height: height}, {
                done: function() {
                    self.render().$el.css({height: ROW_HEIGHT}).show();
                    dfd.resolve();
                }
            });

            return this;
        },

        hide: function() {
            return this.$el.hide();
        },

        show: function() {
            return this.$el.show();
        },

        cancel: function() {
            this.model.set(this.model.getState("confirmedState"));
            this.toggleActive();
        },

        runValidation: function(event, options) {
            this.validateModel(options) && this.triggerConfirmOpening(options);
        },

        triggerConfirmOpening: function(options) {
            options = options || {};

            var dfd = new $.Deferred(),
                self = this;

            var openPermissionConfirmOrValidate = function() {
                self.permissionConfirmShouldBeShown
                    ? self._openPermissionConfirm && _.bind(self._openPermissionConfirm, self, options)()
                    : self.triggerModelValidation(options);
            };

            if (!this.model.isNew()) {
                if (this.modelChanged) {
                    this.modelChanged.name
                        ? this.trigger("open:confirm", confirmDialogTypesEnum.NAME_CONFIRM, {dfd: dfd})
                        : dfd.resolve();

                    dfd.done(function() {
                        self.modelChanged._embedded
                            ? openPermissionConfirmOrValidate()
                            : self.triggerModelValidation(options);
                    });
                } else {
                    this.toggleActive();
                }
            } else {
                openPermissionConfirmOrValidate();
            }
        },

        triggerModelValidation: function(options) {
            this.model.trimAttrs(["name", "value", "description"]);
            this.trigger("validate", options);
        },

        validateModel: function(options) {
            options = options || {};

            var modelIsValid = this.model.isValid(true);

            !modelIsValid && options.dfd && options.dfd.reject();

            return modelIsValid;
        },

        confirmState: function(confirm) {
            this.model.confirmState(confirm);
        },

        isStateConfirmed: function() {
            return this.model.isStateConfirmed();
        },

        isInherited: function() {
            return this.model.getState("originalState").inherited;
        },

        getChangedProperties: function(property) {
            if (this.modelChanged) {
                return property ? this.modelChanged[property] : this.modelChanged;
            }
        },

        toggleValue: function(e) {
            if (this.editMode) {
                var $input = $(e.target),
                    inputValue = $input.val(),
                    isValueSecured = inputValue === SECURE_VALUE_SUBSTITUTION,
                    isOkButton = $(e.relatedTarget).hasClass("ok"),
                    isClick = e.type === "click";

                if (inputValue) {
                    if (isClick && isValueSecured) {
                        $input.val("");
                        this.model.resetField("value");
                    }

                    if (!isClick && !isOkButton) {
                        $input.val(SECURE_VALUE_SUBSTITUTION);
                    }
                }
            }
        },

        invokeFiltration: function() {
            var model = this.model;

            if (model.hasChanged("secure") || model.hasChanged("_embedded") && !this._isPermissionLimited()) {
                return !this.editMode && model.hasChanged("inherited");
            }
        },

        validateIfSecure: function() {
            var nameChanged = this.getChangedProperties("name");

            this.model.validateIfSecure = (this.model.validateSecureValue() && nameChanged) || false;
        },

        _onSaveSuccess: function() {
            this.modelChanged = null;

            this.model.setId();

            this.model.get("secure") && this.model.resetField("value");

            this.model.setState();
            this.model.setState("confirmedState");
        },

        _onModelChange: function(model) {
            this.modelChanged = this.modelChanged || {};

            var changedAttributes = model.changedAttributes(),
                originalModelAttributes = this.model.getState(),
                isInherited = this.isInherited(),
                modelIsNew = model.isNew(),
                modelIsChanged;

            _.each(changedAttributes, function(val, prop) {
                if (prop !== "inherited") {
                    var originalAttribute = originalModelAttributes[prop],
                        equal = !_.isObject(val)
                            ? originalAttribute === val
                            : _.isEqual(originalAttribute, val);

                    if (!equal) {
                        this.modelChanged[prop] = val;
                    } else {
                        delete this.modelChanged[prop];
                    }
                }
            }, this);

            modelIsChanged = !_.isEmpty(this.modelChanged);

            isInherited && model.set("inherited", !modelIsChanged);

            !this.editMode && changedAttributes.secure && model.setState("confirmedState");

            this.trigger("changed", modelIsChanged || modelIsNew);

            this.model.validate(this.modelChanged);
        }
    });

    return DesignerRowView;
});