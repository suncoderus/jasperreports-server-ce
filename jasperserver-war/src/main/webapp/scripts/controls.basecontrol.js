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
 * @author: afomin, inesterenko
 * @version: $Id$
 */

/* global JRS, _ */

JRS.Controls = (function (jQuery, _, Controls) {

    //module:
    //
    //  controls.basecontrol
    //
    //summary:
    //
    //BaseControl - prototype for all input controls
    //
    //dependencies:
    //
    //
    //  jQuery          - v1.7.1
    //  _,              - underscore.js 1.3.1
    //  Controls,       - controls.core module

    function getSelectedValues(values){
        if (_.isArray(values)) {
            var filteredValues = _.filter(values,
                function (val) {
                    return val.selected;
                });
            return _.map(filteredValues, function (opt) {
                //TODO: decouple from controls.options data structure
                if (opt.id || opt.id === ""){
                    return opt;
                }
                return opt.value;
            });
        } else {
            return values;
        }
    }

    return _.extend(Controls, {

        //Base object for all input controls types
        BaseControl:Controls.Base.extend({

            /* Id of the control. */
            id : null,

            /* Store jQuery object presentation of each control. */
            elem : null,

            /* Validation message:
             In case of incorrect value it will contain error message from server.
             In case of correct value it will be null. */
            error : null,

            // Store template's chunks functions
            templateChunks:{},

            constructor:function (args) {
                this.initialize.apply(this, arguments);
            },

            // Return jQuery object

            getElem:function () {
                return this.elem;
            },

            //Save reference to control's DOM as jQuery object
            setElem:function (element) {
                this.elem = element;
            },

            //bind custom events in inherited objects
            bindCustomEventListeners:function () {
            },

            isSingleSelect:function () {
                return !_.isArray(this.get('selection'));
            },

            isSingleValue : function(){
                return !_.isArray(this.get('values'));
            },

            //get template function for chunk of template
            getTemplateSection:function (section) {
                if (!this.templateChunks[this.type+"_"+section]) {
                    this.templateChunks[this.type+"_"+section] = Controls.TemplateEngine.createTemplateSection(section, this.type);
                }
                return this.templateChunks[this.type+"_"+section];
            },

            initialize:function (args) {
                this.baseRender(args);
                this.isVisible(args) && this.bindCustomEventListeners();
            },

            fireControlSelectionChangeEvent:function (evt) {
                jQuery(document).trigger(Controls.CHANGE_CONTROL, this, evt)
            },

            // Render the control from main template
            baseRender:function (controlStructure) {
                controlStructure && _.extend(this, controlStructure);

                var template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    var element = jQuery(template(this));
                    this.setElem(element);
                }
            },

            // Refresh values of the control.
            update:function (controlData) {
            },

            // Returns selected values from the control.
            get:function (attribute) {
               return this[attribute];
            },

            // Check for validation
            isValid:function () {
                return this.error === null ||
                    (this.error && this.error instanceof String && this.error.length === 0);
            },

            //Check whether control is visible
            //Also returns true if visible property was not set at all
            isVisible: function(args) {
                return !args || args.visible === undefined || args.visible === true;
            },

            //render message
            updateWarningMessage:function () {
                var message = this.error != null ? this.error : "";
                this.getElem() && this.getElem().find(".warning").text(message);
            },

            // Recreate dom element for current control
            refresh:function () {
                var values = this.get('values');
                var selection = this.get('selection');
                if (this.isSingleValue()) {
                    values = selection;
                } else {
                    values = Controls.BaseControl.merge(values, selection);
                }
                this.values = values;
                this.initialize(this);
                this.isVisible(this) && this.update(values);
            },

            // Store attributes and do some actions according changed attribute
            set:function (attributes, preventNotification) {

                _.extend(this, attributes);

                if (attributes['values'] !== undefined) {
                    this.isVisible(this) && this.update(attributes['values']);
                    if (this.isSingleValue()){
                        this["selection"] = attributes['values'];
                    }else{
                        if (this.isSingleSelect()){
                            this["selection"] = getSelectedValues(attributes['values'])[0];
                        }else{
                            this["selection"] = getSelectedValues(attributes['values']);
                        }
                    }
                }

                if (attributes["selection"]!== undefined){
                    if ( this.isSingleValue()){
                        this["values"] = attributes["selection"];
                    }
                    !preventNotification && this.fireControlSelectionChangeEvent();
                }

                if (attributes['disabled'] !== undefined && !this.get("readOnly")) {
                    var disabled = attributes['disabled'];
                    if (!preventNotification) {
                        disabled ? this.disable() : this.enable();
                    }
                }

                if (attributes["error"] !== undefined){
                    this.updateWarningMessage();
                }
            },

            find:function (attributes) {
                if (attributes !== undefined && attributes && typeof attributes == "object") {
                    var key = _.keys(attributes)[0];
                    var value = _.values(attributes)[0];
                    return _.find(this.get('values'), function (val) {
                        return val[key] === value
                    });
                }
            },

            enable:function () {
                this.getElem().find("input").prop('disabled', false);
                this.getElem().find("select").prop('disabled', false);
            },

            disable:function () {
                this.getElem().find("input").prop('disabled', true);
                this.getElem().find("select").prop('disabled', true);
            }

        },{
            //merge  values  with selection
            merge:function (values, selections) {

                if(_.isNull(values) || _.isUndefined(values)){
                    return selections;
                }

                if (_.isNull(selections) || _.isUndefined(selections)){
                    return values;
                }

                if (_.isArray(selections)) {
                    return _.map(values, function (option) {
                        var selectedValue = _.find(selections, function (selection) {
                            return selection === option.value;
                        });
                        if (selectedValue !== undefined) {
                            option.selected = true;
                        } else {
                            delete option.selected;
                        }
                        return option
                    });
                }else {
                    return this.merge(values, [selections]);
                }
            }
            }),

        CHANGE_CONTROL: "changed:control"

    });

})(
    jQuery,
    _,
    JRS.Controls
);
