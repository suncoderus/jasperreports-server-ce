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
 * @author: ztomchenco
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var jQuery = require("jquery"),
        Backbone = require("backbone"),
        mainTemplate = require("text!tenantImportExport/export/template/authorityPickerTemplate.htm"),
        optionTemplate = require("text!tenantImportExport/export/template/authorityPickerOptionsTemplate.htm"),
        _ = require("underscore");

    function isBeetween(a, b, c) {
        return c <= Math.max(a, b) && c >= Math.min(a, b)
    }

    return Backbone.View.extend({

        events:{
            "click .button.search":"search",
            "click .button.searchClear":"clear",
            "click .button-select-all": "_selectAllHandler",
            "keydown input[type=text]" : "keyHandler",
            "mousedown li":"selectionStarted",
            "mouseenter li":"mouseEntered",
            "mouseleave li":"mouseLeave",
            "mouseup ul":"selectionFinished",
            "mouseenter ul": "stopScroll",
            "mouseleave ul":"startScroll"
        },

        cursor:-1,
        baseCursor:-1,
        selecting:false,
        scrollSpeed: 30,

        initialize:function (options) {
            this.model.on("change", _.bind(this.renderList, this));
            this.model.on("error:server", _.bind(function () {
                this.trigger("error:server", arguments);
            }, this));

            this.upHandler = _.bind(function (evt) {
                this.stopScroll(evt);
                this.selecting = false;
            }, this);

            this.el = false;
			this.initOptions = options;
        },

        render:function () {
            if (!this.mainTemplate) this.mainTemplate = _.template(mainTemplate);
            if (!this.optionsTemplate) this.optionsTemplate =  _.template(optionTemplate);

            if (!this.el) {
                this.$el = jQuery("<div/>").html(this.mainTemplate(this.initOptions)).children();
                this.el = this.$el[0];
                this.subEl = this.$el.find(".authorityPicker ul");
                this.delegateEvents();
            }

            this.model.context || this.model.setContext();

            return this;
        },

        renderList:function () {
            this.subEl.html(this.optionsTemplate(this.model.attributes)).find("li").each(function (index, element) {
                jQuery(element).attr("index", index);
            });

            this.cursor = -1;
            this.baseCursor = -1;
            this.scrollSpeed = this.subEl.find("li:eq(0)").height();
            this.subEl.scrollTop(0);

            this.trigger("change:selection",[]);

            return this;
        },

        getSelected:function () {
            return _.reduce(this.subEl.find(".selected"), function (memo, el) {
                memo.push(_.pluck(jQuery(el).find("span"), "innerHTML").join("|"))
                return memo;
            }, []);
        },

        search:function () {
            var searchString = this.$el.find("input[type=text]").val();
            this.model.setContext({searchString:searchString});
            if (searchString) {
                this.$el.find(".button.searchClear").addClass("up");
            } else {
                this.$el.find(".button.searchClear").removeClass("up");
            }
        },

        _selectAllHandler: function(){
            this.selectAll();
        },

        clear:function () {
            this.$el.find("input[type=text]").val("");
            this.search();
        },

        keyHandler: function(evt){
            if (evt.which === 13){
                this.search();
            }
        },

        setDisabled:function (value) {
            var $selectAll = this.$el.find(".button-select-all").children();

            if (value) {
                this.subEl.addClass("disabled");
                $selectAll.addClass("disabled");
                this.undelegateEvents();
            } else {
                this.subEl.removeClass("disabled");
                $selectAll.removeClass("disabled");
                this.delegateEvents();
            }
            this.$el.find('input[type=text]').attr("disabled", value);
        },

        highlightSet:function(what){
            this.subEl.find("li").each(function(index, element) {
                element = jQuery(element);
                element.toggleClass("highlighted", _.contains(what, _.pluck(element.find("span"), "innerHTML").join("|")));
            });
        },

        selectAll:function (silent) {
            this.subEl.find("li").each(function (index, element) {
                jQuery(element).addClass("selected");
            });
            silent || this.trigger("change:selection", this.getSelected());
        },

        selectNone:function (silent) {
            this.subEl.find("li").each(function (index, element) {
                jQuery(element).removeClass("selected");
            });
            silent || this.trigger("change:selection", this.getSelected());
        },

        selectInverse:function (silent) {
            this.subEl.find("li").each(function (index, element) {
                jQuery(element).toggleClass("selected");
            });
            silent || this.trigger("change:selection", this.getSelected());
        },

        selectItem: function(n, silent){
            this.subEl.find("li[index="+n+"]").addClass("selected");
            silent || this.trigger("change:selection", this.getSelected());
        },

        unSelectItem: function(n, silent){
            this.subEl.find("li[index="+n+"]").removeClass("selected");
            silent || this.trigger("change:selection", this.getSelected());
        },

        selectRange:function (a, b, silent) {
            var begin = Math.min(a, b);
            var end = Math.max(a, b);

            this.subEl.find("li:lt(" + (end + 1) + ")").each(function (index, element) {
                if (index >= begin) {
                    jQuery(element).addClass("selected");
                }
            });
            silent || this.trigger("change:selection", this.getSelected());
        },

        selectionStarted:function (evt) {
            this.selecting = true;

            if (!(evt.ctrlKey || evt.metaKey)) {
                this.selectNone(true);
            }

            var index = +jQuery(evt.target).parents("li").toggleClass("selected").attr("index");

            if (evt.shiftKey) {
                if (this.cursor != -1) {
                    this.selectRange(this.cursor, index, true);
                }
            } else {
                this.cursor = index;
                this.baseCursor = this.cursor;
            }
        },

        mouseEntered:function (evt) {
            if (this.selecting) {
                this.cursor = +jQuery(evt.target).parents("li").addClass("selected").attr("index");
            }
        },

        mouseLeave:function (evt) {
            if (this.selecting) {
                var previous = jQuery(evt.target).parents("li");
                var next = jQuery(evt.relatedTarget).parents("li");
                if (isBeetween(this.cursor, this.baseCursor, +next.attr("index"))) {
                    previous.removeClass("selected");
                }
            }
        },

        selectionFinished:function (evt) {
            this.selecting = false;
            this.trigger("change:selection", this.getSelected());
        },

        startScroll: function(evt) {
            if (this.selecting){
                if (jQuery(evt.relatedTarget).hasClass("upper")){
                    this.direction = -1;
                } else if (jQuery(evt.relatedTarget).hasClass("lower")){
                    this.direction = 1;
                } else {
                    this.selecting = false;
                    return;
                }

                jQuery(document.body).on("mouseup", this.upHandler);

                this.scrollTimer = setInterval(_.bind(this.scrollList, this), 200);
            }

        },

        stopScroll:function (evt) {
            if (this.selecting) {
                clearInterval(this.scrollTimer);
                this.direction = 0;
                jQuery(document.body).off("mouseup", this.upHandler);
            }
        },

        scrollList: function(){
            this.subEl.scrollTop(+this.subEl.scrollTop() + this.scrollSpeed*this.direction);

            if (isBeetween(this.cursor, this.baseCursor, this.cursor + this.direction)){
                this.unSelectItem(this.cursor, true);
            }  else{
                this.selectItem(this.cursor + this.direction, true);
            }

            this.cursor = this.cursor + this.direction;
        }
    })
});