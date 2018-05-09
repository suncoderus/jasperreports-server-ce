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

/* global JRS, require, updateYearMonth, ControlsBase, _ */

JRS.Controls = (function (jQuery, _, Controls) {

//module:
//
//  controls.components
//
//summary:
//
//  Provide default input controls types
//
//main types:
//
//  Bool, SingleValue, SingleValueDate, SingleSelect, MultiSelect, MultiSelectCheckbox, SingleSelectRadio - default
//  input controls
//
//  ReportOptions - special control, not connected with cascade
//
//dependencies:
//
//  jQuery          - jquery v1.7.1
//  _,         - underscore.js 1.3.1
//  Controls   - controls.core module
    var dateUtil;
    require(["common/util/parse/date"],
        function(dateUtilModule){
            dateUtil =dateUtilModule;
    });

    var SingleSelect,
        MultiSelect,
        CacheableDataProvider,
        DataProviderWithLabelHash,
        SearcheableDataProvider,
        resizableMultiSelectTrait,
        DateAndTimePicker;

    require([
        "components/singleSelect/view/SingleSelect",
        "components/multiSelect/view/MultiSelect",
        "components/singleSelect/dataprovider/CacheableDataProvider",
        "components/multiSelect/dataprovider/DataProviderWithLabelHash",
        "components/singleSelect/dataprovider/SearcheableDataProvider",
        "components/multiSelect/mixin/resizableMultiSelectTrait",
        "components/dateAndTime/DateAndTimePicker"

    ], function (SingleSelectModule,
                 MultiSelectModule,
                 CacheableDataProviderModule,
                 DataProviderWithLabelHashModule,
                 SearcheableDataProviderModule,
                 resizableMultiSelectMixinModule,
                 DateAndTimePickerModule

    ) {
        SingleSelect = SingleSelectModule;
        MultiSelect = MultiSelectModule;
        CacheableDataProvider = CacheableDataProviderModule;
        DataProviderWithLabelHash = DataProviderWithLabelHashModule;
        SearcheableDataProvider = SearcheableDataProviderModule;
        resizableMultiSelectTrait = resizableMultiSelectMixinModule;
        DateAndTimePicker = DateAndTimePickerModule;
    });

    function changeDateFunc(dateText) {
        this.set({selection:dateText});
    }

    function changeMonthYear(year, month, inst) {
        var newDate = inst.input.datepicker("getDate");
        if (newDate && !(newDate.getFullYear() === year && newDate.getMonth() === month - 1)) {
            newDate = updateYearMonth(newDate, year, month - 1);
            inst.input.datepicker("setDate", newDate);
            changeDateFunc.call(this, inst.input.val());
        }
    }

    function getNormalizedDatetimeValue(rawValue) {
        var normalizedValue = rawValue.toUpperCase().replace(/([\s]+$|^[\s]+)/g, "");
        return normalizedValue.replace(/[\s]*(\+|\-)[\s]*/g, "$1");
    }

    function extractSelection(controlData, single) {
        if (!controlData) {
            return null;
        }

        var selection = {};

        for (var i = 0; i < controlData.length; i++) {
            var item = controlData[i];
            if (item.selected) {
                selection[i] = item.value;

                if (single) {
                    break;
                }
            }
        }

        return selection;
    }

    return _.extend(Controls, {

        Bool:Controls.BaseControl.extend({

            update:function (controlData) {
                var container = this.getElem();
                var input = container.find('input');
                if (controlData == "true") {
                    input.prop('checked', true);
                } else {
                    input.removeAttr('checked');
                }
            },

            bindCustomEventListeners:function () {
                this.getElem().on('change', 'input', _.bind(function (evt) {
                    var value = evt.target.getValue() == null ? "false" : "true";
                    this.set({selection:value});
                }, this));
            }
        }),

        SingleValueText:Controls.BaseControl.extend({

            update:function (controlData) {
                var value = controlData == ControlsBase.NULL_SUBSTITUTION_VALUE ? ControlsBase.NULL_SUBSTITUTION_LABEL : controlData;
                var container = this.getElem();
                if (container) {
                    container.find('input').attr('value', value);
                    container.find('input').val(value);
                }
            },

            bindCustomEventListeners:function () {
                this.getElem().on('change', 'input', _.bind(function (evt) {
                    var inputValue = evt.target.value;
                    var value = inputValue == ControlsBase.NULL_SUBSTITUTION_LABEL ? ControlsBase.NULL_SUBSTITUTION_VALUE : inputValue;
                    this.set({selection:value});
                }, this));

            }
        }),

        SingleValueNumber:Controls.BaseControl.extend({

            update:function (controlData) {
                this.getElem().find('input').attr('value', controlData);
                this.getElem().find('input').val(controlData);
            },

            bindCustomEventListeners:function () {
                this.getElem().on('change', 'input', _.bind(function (evt) {
                    this.set({selection:evt.target.value});
                }, this));
            }
        }),

        SingleValueDate:Controls.BaseControl.extend({

            initialize:function (args) {
                this.baseRender(args);
                args.visible && this.setupCalendar();
            },

            get: function(attribute){
                var localizedDate = Controls.BaseControl.prototype.get.call(this, attribute);
                return dateUtil.localizedDateToIsoDate(localizedDate);
            },

            set: function(attributes, preventNotification){
                if(attributes.values){
                    attributes.values = dateUtil.isoDateToLocalizedDate(attributes.values);
                }
                Controls.BaseControl.prototype.set.call(this, attributes, preventNotification);
            },

            setupCalendar:function () {
                var input = this.getElem().find('input');

                this.picker = new DateAndTimePicker({
                    el: input,
                    showOn: "button",
                    dateFormat: JRS.i18n["bundledCalendarFormat"],
                    disabled: input[0].disabled,
                    onSelect: _.bind(changeDateFunc, this)
                });

                input.change(_.bind(function (evt) {
                    //prevent triggering of global control change event
                    evt.stopPropagation();

                    //remove all spaces and convert to upper case
                    jQuery(evt.target).val(getNormalizedDatetimeValue(evt.target.value));

                    changeDateFunc.call(this, jQuery(evt.target).val());
                }, this));

                input.after('&nbsp;');
                var button = this.getElem().find('button');
                button.wrap(ControlsBase.CALENDAR_ICON_SPAN).empty();
            },

            update:function (controlData) {
                var container = this.getElem();
                container.find('input').attr('value', controlData);
                container.find('input').val(controlData);
            }

        }),

        SingleValueDatetime:Controls.BaseControl.extend({

            initialize:function (args) {
                this.baseRender(args);
                args.visible && this.setupCalendar();
            },

            getInput: function() {
                var $el = this.getElem();

                return $el.find('input');
            },

            destroyCalendar: function() {
                var $input = this.getInput();
                $input.off();

                this.picker && this.picker.remove();
            },

            setupCalendar:function () {
                var $el = this.getElem(),
                    $input = this.getInput();

                this.destroyCalendar();

                this.picker = new DateAndTimePicker({
                    el: $input[0],
                    showOn: "button",
                    dateFormat: JRS.i18n["bundledCalendarFormat"],
                    timeFormat: JRS.i18n["bundledCalendarTimeFormat"],
                    disabled: $input[0].disabled,
                    onSelect: _.bind(changeDateFunc, this)
                });

                $input.change(_.bind(function (evt) {
                    //prevent triggering of global control change event
                    evt.stopPropagation();

                    //remove all spaces an  d convert to upper case
                    jQuery(evt.target).val(getNormalizedDatetimeValue(evt.target.value));

                    changeDateFunc.call(this, jQuery(evt.target).val());
                }, this));

                $input.after('&nbsp;');

                var button = $el.find('button');

                button.wrap(ControlsBase.CALENDAR_ICON_SPAN).empty();
            },

            get: function(attribute){
                var localizedDateTime = Controls.BaseControl.prototype.get.call(this, attribute);
                return dateUtil.localizedTimestampToIsoTimestamp(localizedDateTime);
            },

            set: function(attributes, preventNotification) {
                if(attributes.values){
                    attributes.values = dateUtil.isoTimestampToLocalizedTimestamp(attributes.values);
                }

                if (!attributes.values && !attributes.selection) {
                    this.setupCalendar();
                }

                Controls.BaseControl.prototype.set.call(this, attributes, preventNotification);
            },
            update:function (controlData) {
                var container = this.getElem();
                container.find('input').attr('value', controlData);
                container.find('input').val(controlData);
            }
        }),

        SingleValueTime:Controls.BaseControl.extend({

            initialize:function (args) {
                this.baseRender(args);
                args.visible && this.setupCalendar();
            },

            getInput: function() {
                var $el = this.getElem();

                return $el.find('input');
            },

            destroyCalendar: function() {
                var $input = this.getInput();
                $input.off();

                this.picker && this.picker.remove();
            },

            setupCalendar:function () {
                var $el = this.getElem(),
                    $input = this.getInput();

                this.destroyCalendar();

                this.picker = new DateAndTimePicker({
                    el: $input[0],
                    showOn: "button",
                    timeFormat: JRS.i18n["bundledCalendarTimeFormat"],
                    disabled: $input[0].disabled,
                    onClose: _.bind(changeDateFunc, this)
                });

                $input.change(_.bind(function (evt) {
                    //prevent triggering of global control change event
                    evt.stopPropagation();

                    //remove all spaces and convert to upper case
                    jQuery(evt.target).val(getNormalizedDatetimeValue(evt.target.value));

                    changeDateFunc.call(this, jQuery(evt.target).val());
                }, this));

                $input.after('&nbsp;');
                var button = $el.find('button');
                button.wrap(ControlsBase.CALENDAR_ICON_SPAN).empty();
            },

            get: function(attribute){
                var localizedTime = Controls.BaseControl.prototype.get.call(this, attribute);
                return dateUtil.localizedTimeToIsoTime(localizedTime);
            },

            set: function(attributes, preventNotification){
                if (attributes.values) {
                    attributes.values = dateUtil.isoTimeToLocalizedTime(attributes.values);
                }

                if (!attributes.values && !attributes.selection) {
                    this.setupCalendar();
                }

                Controls.BaseControl.prototype.set.call(this, attributes, preventNotification);
            },
            update:function (controlData) {
                var container = this.getElem();
                container.find('input').attr('value', controlData);
                container.find('input').val(controlData);
            }
        }),

        SingleSelect:Controls.BaseControl.extend({

            baseRender:function (controlStructure) {
                if (!this.singleSelect) {
                    this.dataProvider = new CacheableDataProvider();
                    this.singleSelect = new SingleSelect({
                        getData: this.dataProvider.getData
                    }).setDisabled(controlStructure.readOnly);
                }

                controlStructure && _.extend(this, controlStructure);

                var template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    this.singleSelect.undelegateEvents();
                    var element = jQuery(template(this));
                    this.singleSelect.render().renderData();
                    element.find(".ssPlaceholder").append(this.singleSelect.el);
                    this.setElem(element);
                    this.singleSelect.delegateEvents();
                }
            },

            update:function (controlData) {
                this.dataProvider.setData(controlData);

                var that = this;
                this.singleSelect.fetch(function() {
                    that.singleSelect.setValue(extractSelection(controlData, true), {silent: true});
                });
            },

            bindCustomEventListeners:function () {
                this.singleSelect.off("selection:change").on("selection:change", function (selection) {
                    this.set({selection:selection});
                }, this);
            }
        }),

        MultiSelect:Controls.BaseControl.extend({

            baseRender:function (controlStructure) {
                var self = this;

                if (!this.multiSelect) {

                    this.dataProvider = new DataProviderWithLabelHash();

                    this.multiSelect = new MultiSelect({
                        getData: new SearcheableDataProvider({
                            getData: this.dataProvider.getData
                        }).getData,
                        selectedListOptions: {
                            formatLabel: function(value) {
                                return self.dataProvider.getDataLabelHash()[value];
                            }
                        },
                        resizable : true
                    });

                    this.multiSelect.setDisabled(controlStructure.readOnly);

                    this._resize = _.debounce(_.bind(this.multiSelect.resize, this.multiSelect), 500);
                }

                controlStructure && _.extend(this, controlStructure);

                var template = Controls.TemplateEngine.createTemplate(this.type);

                if (template) {
                    this.multiSelect.undelegateEvents();
                    var element = jQuery(template(this));
                    this.multiSelect.render().renderData();
                    element.find(".msPlaceholder").append(this.multiSelect.el);
                    this.setElem(element);
                    this.multiSelect.delegateEvents();
                }
            },

            update:function (controlData) {
                this.dataProvider.setData(controlData);

                var that = this;
                this.multiSelect.fetch(function() {
                    var selection = extractSelection(controlData);

                    that.multiSelect.setValue(selection, {silent: true});
                    that._resize();
                });
            },

            bindCustomEventListeners:function () {
                this.multiSelect.off("selection:change").on("selection:change", function (selection) {
                    this.set({selection:selection});
                }, this);
            },

            get:function (attribute) {
                var result = this[attribute];
                if (attribute == 'selection' && _.isEmpty(result)) {
                    return [ControlsBase.NOTHING_SUBSTITUTION_VALUE];
                } else {
                    return result;
                }
            },

            makeResizable:function () {

                var $sizer = this.multiSelect.$el.find(".jr-mSizer");

                //according to IC specifics, sizer should be after alert message
                if ($sizer.length){
                    $sizer.detach().insertAfter(this.getElem().find('.resizeOverlay'));
                }
            }
        }),

        SingleSelectRadio:Controls.BaseControl.extend({

            update:function (controlData) {

                var start = (new Date()).getTime();

                var list = this.getElem().find('ul')[0];

                var data = _.map(controlData, function (val) {
                    var result = _.clone(val);
                    result.readOnly = this.readOnly;
                    result.name = this.getOptionName();
                    result.uuid = _.uniqueId(this.id);
                    return result;
                }, this);

                var template = this.getTemplateSection('data');

                Controls.Utils.setInnerHtml(list, template, {data:data});
                list.innerHTML += "&nbsp;"; //workaround for IE scrollbar

                //TODO move to decorator
                if (jQuery(list).find('li').length < 5 && this.getElem()[0].clientHeight < 125) {
                    this.getElem().find('.jr-mSizer').addClass('hidden');
                    this.getElem().find('.inputSet').removeClass('sizable').attr('style', false);
                } else {
                    if (this.resizable) {
                        this.getElem().find('.jr-mSizer').removeClass('hidden');
                        this.getElem().find('.inputSet').addClass('sizable');
                    }
                }
            },

            getOptionName:function () {
                return this.id + "_option";
            },

            bindCustomEventListeners:function () {
                this.getElem().on('change', 'input', _.bind(function (evt) {
                    var selection = evt.target.value;
                    // for better performance in IE7
                    var that = this;
                    setTimeout(function () {
                        that.set({selection:selection})
                    });
                }, this));
            },

            //TODO move to decorator
            makeResizable:function () {
                this.resizable = true;
                var list = this.getElem().find('ul');
                var sizer = this.getElem().find('.jr-mSizer').removeClass('hidden');
                sizer.addClass("ui-resizable-s");
                list.resizable({
                    handles:{
                        's': sizer
                    }
                });
            }

        }),

        MultiSelectCheckbox:Controls.BaseControl.extend({

            update:function (controlData) {

                var data = _.map(controlData, function (val) {
                    var result = _.clone(val);
                    result.readOnly = this.readOnly;
                    result.uuid = _.uniqueId(this.id);
                    return result;
                }, this);

                var list = this.getElem().find('ul')[0];

                var template = this.getTemplateSection('data');

                Controls.Utils.setInnerHtml(list, template, {data:data});

                //TODO move to decorator
                if (jQuery(list).find('li').length < 5 && this.getElem()[0].clientHeight < 125) {
                    this.getElem().find('.jr-mSizer').addClass('hidden');
                    this.getElem().find('.inputSet').removeClass('sizable').attr('style', false);
                } else {
                    if (this.resizable) {
                        this.getElem().find('.jr-mSizer').removeClass('hidden');
                        this.getElem().find('.inputSet').addClass('sizable');
                    }
                }
            },

            getSelection:function () {
                var boxes = this.getElem().find(":checkbox").filter(":checked");

                return _.map(boxes, function (box) {
                    return jQuery(box).val();
                });
            },

            bindCustomEventListeners:function () {
                this.getElem().on('change', 'input', _.bind(function (evt) {
                    var selection = this.getSelection();
                    // for better performance in IE7
                    var that = this;
                    setTimeout(function () {
                        that.set({selection:selection})
                    });
                }, this));

                this.getElem().on('click','a',_.bind(function(evt) {

                    var options = this.getElem().find('input');

                    var name = jQuery(evt.target)[0].name;
                    if (name === "multiSelectAll" || name === "multiSelectNone") {
                        _.each(options,  function(opt){
                            opt.checked = name === "multiSelectAll";
                        });
                    } else if (name === "multiSelectInverse") {
                        _.each(options,  function(opt){
                            opt.checked = !opt.checked;
                        });
                    }

                    this.getElem().find('input').change(); // trigger the cascading request

                }, this));
            },

            get:function (attribute) {
                var result = this[attribute];
                if (attribute == 'selection' && _.isEmpty(result)) {
                    return [ControlsBase.NOTHING_SUBSTITUTION_VALUE];
                } else {
                    return result;
                }
            },

            //TODO move to decorator
            makeResizable:function () {
                this.resizable = true;
                var list = this.getElem().find('ul');
                var sizer = this.getElem().find('.jr-mSizer').removeClass('hidden');
                sizer.addClass("ui-resizable-s");
                list.resizable({
                    handles:{
                        's': sizer
                    }
                });
            }

        })
    });

})(
    jQuery,
    _,
    JRS.Controls
);
