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

define(function(require) {
    var _ = require("underscore"),
        $ = require("jquery"),
        BackboneValidation = require("backbone.validation"),
        i18n = require("bundle!AttributeBundle"),
        i18nMessage = require("common/util/i18nMessage").extend({bundle: i18n}),
        Epoxy = require("backbone.epoxy"),
        permissionMasksEnum = require("attributes/enum/permissionMasksEnum");


    var MAX_LENGTH = 255,
        MAX_VALUE_LENGTH = 2000;

    /**
     * Construtor. Same parameters as Backbone.Model
     * @class  Attribute
     * @classdesc This is a Backbone model, which represents user attribute. Intended to use inside Attributes collection.
     * @extends Backbone.Model
     * @see Attributes
     */
    var AttributeModel = Epoxy.Model.extend({
        defaults: {
            id: undefined,
            name: undefined,
            value: "",
            description: "",
            inherited: false,
            permissionMask: 1,
            secure: false
        },

        constructor: function() {
            this._initModelWithPermissionDefaults && this._initModelWithPermissionDefaults();

            Epoxy.Model.apply(this, arguments);
        },

        initialize: function() {
            if (!this.get("id")) {
                // By default id is generated from the attribute name.
                this.setId();
            }

            this.validateSameNames = false;

            this.setState("originalState");
            this.setState("confirmedState");
        },

        /**
         * This method returns url, which points in attribute, represented by this Attribute instance
         * @instance
         * @memberof Attribute
         * @return url of representing resource
         */
        url: function() {
            // duplicated on purpose - overrides some strange behaviour of FF
            var safeId = encodeURIComponent(this.id).replace(/'/g, "%27");
            return this.collection.url(this.isNew() ? "" : safeId);
        },

        validation: {
            name: [
                {
                    required: true,
                    msg: new i18nMessage("attributes.error.attribute.name.empty")
                },
                {
                    maxLength: MAX_LENGTH,
                    msg: new i18nMessage("attributes.error.attribute.name.too.long", MAX_LENGTH)
                },
                {
                    doesNotContainSymbols: "\\\\/",
                    msg: new i18nMessage("attributes.error.attribute.name.invalid")
                },
                {
                    fn: function() {
                        if (this.attr) {
                            var msg,
                                existOnCurrentLevel,
                                attribute,
                                attributesLength = this.attr.length;

                            for (var i = 0; i < attributesLength; i++) {
                                attribute = this.attr[i];

                                _.defaults(attribute, this.defaults);

                                existOnCurrentLevel = this.holder === attribute.holder && this.get("inherited") === attribute.inherited;

                                if (existOnCurrentLevel) {
                                    // attribute already exist on this level
                                    msg = "attributes.error.attribute.name.already.exist";
                                    break;
                                } else if (attribute.inherited) {
                                    // attribute can not be overridden
                                    if (attribute.permissionMask === permissionMasksEnum.READ_ONLY) {
                                        msg = "attributes.error.attribute.name.already.exist.at.higher.level";
                                        break;
                                    }
                                }

                            }

                            this.attr = null;
                            this.holder = null;

                            return msg && new i18nMessage(msg);
                        }
                    }
                },
                {
                    fn: function() {
                        if (this.validateIfSecure) {
                            return new i18nMessage("attributes.error.attribute.secure.renaming.not.allowed");
                        }
                    }
                }
            ],
            value: [
                {
                    maxLength: MAX_VALUE_LENGTH,
                    msg: new i18nMessage("attributes.error.attribute.value.too.long", MAX_VALUE_LENGTH)
                },
                {
                    fn: function() {
                        if (this.validateIfSecure) {
                            this.validateIfSecure = false;
                            // return space just to highlight value field without any message
                            return " ";
                        }
                    }
                }
            ],
            description: [
                {
                    maxLength: MAX_LENGTH,
                    msg: new i18nMessage("attributes.error.attribute.description.too.long", MAX_LENGTH)
                }
            ]
        },

        setId: function() {
            var name = this.get("name"),
                id = this.get("id");

            name !== id && this.set("id", name);
        },

        toggleSameNamesValidation: function() {
            this.validateSameNames = !this.validateSameNames;
        },

        resetField: function(prop) {
            this.set(prop, this.defaults[prop]);
        },

        reset: function(property, state) {
            var attribute = {};
            state = this.getState(state);

            attribute[property] = state[property];

            this.set(property ? attribute : state);

            return this;
        },

        isRenamed: function() {
            return this.get("name") !== this.get("id");
        },

        isOriginallyInherited: function() {
            return this.originalState.inherited;
        },

        isOverridden: function() {
            return !this.compareAttribute("inherited");
        },

        compareAttribute: function(attr) {
            return this.originalState[attr] === this.confirmedState[attr];
        },

        setState: function(state, attributes) {
            attributes = attributes || this.attributes;
            this[state || "originalState"] = _.clone(attributes);
        },

        getState: function(state) {
            return this[state || "originalState"];
        },

        trimAttrs: function(attrs, options) {
            _.each(attrs, function(attr) {
                var attrValue = this.get(attr);
                this.set(attr, $.trim(attrValue), options);
            }, this);
        },

        toJSON: function(options) {
            options = options || {};

            var attributes = Epoxy.Model.prototype.toJSON.apply(this, arguments);

            return (options.omitValue && this.validateSecureValue() && _.omit(attributes, "value")) || attributes;
        },

        confirmState: function(confirm) {
            this.stateConfirmed = confirm || true;
        },

        isStateConfirmed: function() {
            return this.stateConfirmed;
        },

        validateSecureValue: function() {
            var isEmptyValue = _.isEmpty(this.get("value")),
                isSecure = this.get("secure"),
                isNew = this.isNew() || this.isOverridden();

            return !isNew && isSecure && isEmptyValue;
        }
    });

    _.extend(AttributeModel.prototype, BackboneValidation.mixin);

    return AttributeModel;
});

