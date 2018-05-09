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

/* global JRS, _, Report, require */

JRS.Controls = (function (jQuery, _, Controls) {

    //module:
    //
    //  controls.viewmodel
    //
    //main types:
    //
    // ViewModel      - initialize, draw, update controls, no any communication with server
    //
    //dependencies:
    //
    //
    //  jQuery          - v1.7.1
    //  _,              - underscore.js 1.3.1
    //  Controls,       - controls.components

    var isIE = false;
    require(["common/util/browserDetection"], function(browserDetection) {
        isIE = browserDetection.isIE();
    });

    // ViewModel initialize, updates and adds controls to the DOM
    var ViewModel = Controls.Base.extend({

        constructor:function (args) {
            this.containerSelector = (args && args.containerSelector) || Controls.ViewModel.DEFAULT_CONTAINER;
            this.controls = {};

            //save reference to initialized manager object
            Controls.getViewModel = _.bind(function () {
                return this;
            }, this);

            _.bindAll(this);

            this.registerEvents();
        },

        registerEvents : function() {
            Controls.ignore("changed:control", this.onControlChange);
            Controls.listen({
                "changed:control":this.onControlChange
            });
        },

        /* Listens for controls change and triggers controls update */
        onControlChange:function (event, control) {
            var selectedData, slaveControlIds;
            var controlInCascade = control.slaveDependencies && control.slaveDependencies.length;
            if (controlInCascade) {
                //optimize changes in cascade
                slaveControlIds = this._getAllSlaveControlIds(control.id);
                var parentControlIds = this._getParentControlIds(slaveControlIds);
                var controlsIds = _.union(slaveControlIds, parentControlIds);
                selectedData = this.get("selection");
                if (!_.isEmpty(controlsIds)) {
                    //leave only changed selection
                    _.each(selectedData, function (value, key) {
                        if (_.indexOf(controlsIds, key) === -1) {
                            delete selectedData[key];
                        }
                    });
                }
            } else {
                selectedData = this.get("selection");
            }
            jQuery(document).trigger(Controls.ViewModel.CHANGE_SELECTION, [selectedData, slaveControlIds, controlInCascade]);
            if (!controlInCascade){
                //TODO: update state before sending it to subscribers
                jQuery(document).trigger(Controls.ViewModel.CHANGE_VALUES, [this.state]);

            }
        },

        /**
         * Returns ids of all immediate and transitive slave controls for given control
         * @param controlId id of given master control
         */
        _getAllSlaveControlIds:function (controlId) {
            var controlIds = [];

            var control = this.findControl({id:controlId});

            if (control && !_.isEmpty(control.slaveDependencies)) {
                _.each(control.slaveDependencies, function (slaveControlId) {
                    controlIds.push(slaveControlId);
                    controlIds.push.apply(controlIds, this._getAllSlaveControlIds(slaveControlId));
                }, this);
            }
            return _.uniq(controlIds);
        },

        /**
         * Returns ids of parent controls for given control ids
         * @param controlIds
         */
        _getParentControlIds:function (controlIds) {
            var parentControlIds = [];

            _.each(controlIds, function (controlId) {
                var control = this.findControl({id:controlId});
                parentControlIds.push.apply(parentControlIds, control.masterDependencies);
            }, this);

            return _.uniq(parentControlIds);
        }

    }, {

        //Static props
        DEFAULT_CONTAINER:'#inputControlsContainer',
        CHANGE_VALUES:"viewmodel:values:changed",
        CHANGE_SELECTION:"viewmodel:selection:changed",
        CHANGE_ORDER:"viewmodel:order:changed",

        //check for changes between two objects
        isSelectionChanged: function (previous,next) {
            var previousControlIds = previous ? _(previous).keys() : [];
            var nextControlIds = next ? _(next).keys() : [];
            var controlsIdUnion = _.union(previousControlIds, nextControlIds);
            if (controlsIdUnion.length === previousControlIds.length){
                var previousFlattenValues = previous ? _.flatten(_(previous).values()) : [];
                var nextFlattenValues = next ? _.flatten(_(next).values()) : [];
                return  _.difference(previousFlattenValues, nextFlattenValues).length > 0 ||
                        _.difference(nextFlattenValues, previousFlattenValues).length > 0;
            }
            return true;
        }
    });

    var Model = {

        //Get array of all instantiated controls
        getControls:function () {
            return this.controls;
        },

        // Creates instances of all controls. Not adding them to DOM
        instantiate:function (jsonStructure) {
            // recreate controls object
            this.controls = {};

            // Iterate over each control in passed json
            _.each(jsonStructure, this.createControl, this);

        },

        // Create control and add it to controls but do not add to the DOM
        createControl:function (jsonControl) {
            // Find instantiated control by it's type
            var Control = function (type) {
                for (var property in Controls) {
                    if (type.toLowerCase() === property.toLowerCase()) {
                        return Controls[property];
                    }
                }
            }(jsonControl.type);
            // Is such control implementation present?
            if (Control) {
                this.controls[jsonControl.id] = new Control(jsonControl);
            } else {
                throw new Error("Can not find implementation of the control type: " + jsonControl.type);
            }
            return this.controls[jsonControl.id];
        },

        // Remove control from state
        removeControl:function (controlId) {
            delete this.controls[controlId];
        },

        // reorders controls in state
        reorderControl: function(oldIndex, newIndex){
            if (this.structure.length == 1) {
                // nothing to reorder
                return;
            }

            var shiftDirection = oldIndex < newIndex ? 1 : -1;
            var start = oldIndex < newIndex ? Math.min(oldIndex, newIndex) : Math.max(oldIndex, newIndex);
            var end = oldIndex < newIndex ? Math.max(oldIndex, newIndex) : Math.min(oldIndex, newIndex);
            var t;

            for (var i = start; i!=end; i = i + shiftDirection){
                t = this.structure[i];
                this.structure[i] = this.structure[i + shiftDirection];
                this.structure[i + shiftDirection] = t;
            }
            jQuery(document).trigger(Controls.ViewModel.CHANGE_ORDER, [this.structure]);
        },

        // Invoke specific actions according specified attributes
        set:function (attributes, preventNotification) {
            if(!attributes) {
                return; //may happen for non-existing controls
            }

            _.extend(this, attributes);

            if (attributes["structure"] !== undefined) {
                var structure = attributes["structure"];
                this.instantiate(structure);
                this.draw(structure);
            }
            if (attributes["state"] !== undefined) {
                var state = attributes["state"];
                this.update(state);
                !preventNotification && jQuery(document).trigger(Controls.ViewModel.CHANGE_VALUES, [attributes["state"]]);
            }
        },

        get : function(attribute){
            if (!attribute){
                return;
            }
            if (attribute === "selection"){
                var result = {};
                _.each(this.getControls(), function (control) {
                    var selectedData = control.get("selection");
                    // Should be array of values, so check it.
                    // Also check if value is undefined then do not pass it to the result array.
                    if (selectedData instanceof Array) {
                        result[control.id] = selectedData;
                    } else if (!_.isUndefined(selectedData)) {
                        result[control.id] = [selectedData];
                    }

                });
                return result;
            }
            return this[attribute];
        },

        // Find control by specified attribute
        findControl : function(attribute){
            var key = _.keys(attribute)[0];
            var value = _.values(attribute)[0];
            return _.find(this.getControls(), function(control){
                return control[key] === value;
            });
        },

        // Returns specified properties from all controls
        pluck:function (property) {
            return _.pluck(this.getControls(), property);
        }
    };

    var View = {

        // Draw structure of all controls. Put them to already selected container
        draw:function (jsonStructure) {

            var container = this.getContainer();
            container.empty();

            if (isIE) {
                // https://jira.tibco.com/browse/JS-32279 - Scroll jumps up on any click inside control aria (item select, title, tab)
                // Solution is to skip stdnav handling on root container of this component.
                container.attr("js-stdnav", "false");
                container.attr("js-navtype", "none");
            }

            // Iterate over each control in passed json
            _.each(jsonStructure, function (jsonControl) {
                if (jsonControl.visible) {
                    var control = this.findControl({id:jsonControl.id});
                    control.makeResizable && control.makeResizable();
                    container.append(control.getElem());
                }
            }, this);

            if (window.Report && window.Report.icReorderEnabled) {
                var horizontalLayout = window.Controls.layouts.LAYOUT_TOP_OF_PAGE == Report.reportControlsLayout;
                if(!(/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent))) {
                     jQuery(container).sortable({
                         delay: 200,
                         placeholder: horizontalLayout ? "verticalPlaceholder" : "horizontalPlaceholder",
                         axis: horizontalLayout ? "x" : "y",
                         items: "> .leaf",
                         cancel: "input,textarea,button,select,option,.list.inputSet,.jr-mSizer",
                         start: function(event, ui) {
                             ui.item.data('oldIndex', ui.item.index());
                         },
                         stop: _.bind(function(event, ui) {
                             var oldIndex = ui.item.data('oldIndex');
                             var newIndex = ui.item.index();
                             oldIndex != newIndex && this.reorderControl(oldIndex, newIndex);
                         }, this)});
                }
            }
        },

        // Fill content of all controls
        update:function (state) {
            _.each(state, function (controlState, controlId) {
                var control = this.findControl({id:controlId});
                control.set({
                    "values":controlState.values,
                    "error" : controlState.error
                });
            }, this);
        },

        // Getter for IC container.
        // Fetch DOM object only once.
        getContainer:function () {
            if (this.container && this.container.length == 1) {
                return this.container;
            } else {
                this.container = jQuery(this.containerSelector);
                return this.container;
            }
        },

        // Clear all warning messages
        removeValidationMessages:function () {
            _.each(this.getControls(), function (control) {
                control.set({error:null});
            });
        },

        // check valid
        areAllControlsValid:function () {
            return _.isEmpty(_.filter(this.getControls(), function (control) {
                return !control.isValid();
            }));
        },

         // Setter for IC container.
        setContainer:function (containerSelector) {
            this.container = jQuery(containerSelector);
        },

        // Reload container, used in case when DOM has been changed.
        reloadContainer:function () {
            this.container = jQuery(this.containerSelector);
            return this.container;
        },

        // Prevent user action on control
        disable:function () {
            _.each(this.getControls(), function (control) {
                control.set({ disabled:true});
            });
        },

        // Allow user action on control
        enable:function () {
            _.each(this.getControls(), function (control) {
                control.set({ disabled:false});
            });
        }
    };

    _.extend(ViewModel.prototype, Model, View);

    return _.extend(Controls, {
        ViewModel: ViewModel
});

})(
    jQuery,
    _,
    JRS.Controls
);
