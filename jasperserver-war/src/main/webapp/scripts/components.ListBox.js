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

/* global showCustomTooltip, hideCustomTooltip, Class, LinkButton */

function ListBox(id, listBoxModel) {
    this.id = id;

    this.listBoxModel = listBoxModel;

    this.options = [];
    this.selected = [];
    this.selectedIndex = -1;
    this.multiple = true;
    this.onclick = function (e) {/*do Nothing*/};
    this.ondblclick = function (e) {/*do Nothing*/};
    this.onselect = function (e) {/*do Nothing*/};
    this.ondeselect = function (e) {/*do Nothing*/};
    this._container = document.getElementById(id);
    this._table = null;
    this.selectionClass = 'optionSelected';
    this.optionClassName = '';
    this.tooltipDelay = 500;

    this._init();
    this._container.list = this;

    if  (this.listBoxModel) {
        this.refresh();
    }
}

ListBox.prototype.setSelectedClassName = function(className) {
    this.selectionClass = className;
};

ListBox.prototype.setOptionClassName = function(className) {
    this.optionClassName = className;
};

ListBox._LIST_TABLE_ID = 'ListBoxTable';
ListBox._LIST_TEMPLATE = '<table id="' + ListBox._LIST_TABLE_ID + '" style="width:100%;" cellspacing="0" cellpadding="0" border="0"><tbody></tbody></table>';
ListBox.cursorEvent;

ListBox.prototype._init = function () {
    this._container.innerHTML = this._getHtmlTemplate();
    this._table = document.getElementById(this._getListTableId());
    this._table.className = this.listClassName;

    var _this = this;
    this._table.onclick = function (e) {
        var event = window.event ? window.event : e;
    };

    this._table.onmousemove = function (e) {
        var event = window.event ? window.event : e;

        ListBox.cursorEvent = {clientX : event.clientX, clientY : event.clientY};
    }
};

ListBox.prototype._getHtmlTemplate = function () {
    var html = ListBox._LIST_TEMPLATE.replace(new RegExp(ListBox._LIST_TABLE_ID), this._getListTableId());

    return html;
};

ListBox.prototype._getListTableId = function () {
    return this.id + ListBox._LIST_TABLE_ID;
};

ListBox.prototype.addOption = function (value, html, styleClass, tooltip) {
    var row = this._table.insertRow(-1);
    var cell = row.insertCell(-1);

    cell.value = value;
    cell.cusotmTooltip = tooltip;
    cell.innerHTML = html;
    cell.noWrap = 'noWrap';
    if (styleClass) {
        cell.defaultClass = styleClass;
        cell.className = styleClass;
    } else {
        cell.defaultClass = this.optionClassName;
        cell.className = this.optionClassName;
    }

    cell.optionIndex = cell.parentNode.rowIndex;

    var _this = this;
    cell.ondblclick = function (e) {
        var event = window.event ? window.event : e;
        _this.ondblclick(event);
    };
    this._addSelectionSupport(cell);
    this._addTooltipSupport(cell);

    this.options.push(cell);
    return cell;
};

ListBox.prototype.removeOption = function (index) {
    if (index > -1 && index < this.options.length) {
        this.options.splice(index, 1);
        this._table.deleteRow(index);
        this._updateIndexes();
        if (this.selectedIndex == index) {
            this.selectedIndex = -1;
        }
        this._removeFromSelected(index);
    }
};

ListBox.prototype.clear = function () {
    var len = this._table.rows.length;
    for(var i = 0; i < len; i ++) {
        this._table.deleteRow(0);
    }
    this.options = [];
    this.selectedIndex = -1;
    this.selected = [];
};

ListBox.prototype._addSelectionSupport = function (option) {
    var _this = this;
    option.onclick = function (e) {
        var event = window.event ? window.event : e;

        // Changes for supporting delselection, not only selection
//        if (_this.selectedIndex == option.optionIndex) {
//            _this.onclick(event);
//            return;
//        }        \
        var callOnclick = true;
        if (event && event.ctrlKey) {
//            option.className = _this.selectionClass;
//            option.selected = true;
//            _this.selectedIndex = option.optionIndex;
//            _this.selected.push(option.optionIndex)
//            _this.onselect(option);

            if (!option.selected) {
                if (!_this.multiple) {
                    _this.deselectAll();
                    _this.selected = [];
                }
                option.className = _this.selectionClass;
                option.selected = true;
                _this.selectedIndex = option.optionIndex;
                _this.selected.push(option.optionIndex);
                _this.onselect(option);
            } else {
                option.className = option.defaultClass;
                option.selected = false;
                _this._removeFromSelected(option.optionIndex);
                if (_this.selected.length > 0) {
                    _this.selectedIndex = _this.selected[_this.selected.length - 1];
                } else {
                    _this.selectedIndex = -1;
                }
                _this.ondeselect(option);
            }
        } else {
            if (option.selected) {
                callOnclick = false;
            }
            _this.deselectAll();
            option.className = _this.selectionClass;
            option.selected = true;
            _this.selectedIndex = option.optionIndex;
            _this.selected = [];
            _this.selected.push(option.optionIndex);
            if (callOnclick) {
                _this.onselect(option);
            }
        }
        if (callOnclick) {
            _this.onclick(event);
        }
    }
};

ListBox.prototype.selectOption = function (index) {
    if (index > -1) {
        this.options[index].onclick();
    }
};

ListBox.prototype.selectOptionMulti = function (index) {
    if (index > -1) {
        this.options[index].onclick({ctrlKey : true});
    }
};

ListBox.prototype.deselectAll = function () {
    for(var i = 0; i < this.options.length; i ++) {
        if (this.options[i].selected) {
            this.options[i].selected = false;
            this._removeFromSelected(i);
            this.options[i].className = this.options[i].defaultClass;
        }
    }
};

ListBox.prototype._updateIndexes = function () {
    for(var i = 0; i < this.options.length; i ++) {
        this.options[i].optionIndex = this.options[i].parentNode.rowIndex;
    }
};

ListBox.prototype._addToSelected = function (index) {
    var isInSelected = false;
    for(var i = 0; i < this.selected.length; i ++) {
        if(this.selected[i] == index) {
            isInSelected = true;
        }
    }
    if(!isInSelected) {
        this.selected.push(index);
    }
};

ListBox.prototype._removeFromSelected = function (index) {
    for(var i = 0; i < this.selected.length; i ++) {
        if(this.selected[i] == index) {
            this.selected.splice(i, 1);
        }
    }
};

ListBox.prototype.getSelectedValues = function() {
    var values = [];
    for(var i = 0; i < this.selected.length; i++) {
        values.push(this.options[this.selected[i]].value);
    }

    return values;
};

ListBox.prototype.getSelected = function() {

    return this.selected;
};

ListBox.prototype.getModel = function() {

    return this.listBoxModel;
};

ListBox.prototype.setModel = function(listBoxModel) {

    this.listBoxModel = listBoxModel;
    this.refresh();
};

ListBox.prototype.refresh = function() {
    this.clear();

    if (this.listBoxModel) {

        for (var i = 0; i < this.listBoxModel.getOptionsCount(); i ++) {

            var value = this.listBoxModel.getValueAt(i);
            var valueHtml = this.listBoxModel.getValueHtmlAt(i);
            var className = this.listBoxModel.getClassNameAt(i);
            var tooltip = this.listBoxModel.getTooltipAt(i);

            this.addOption(value, valueHtml, className, tooltip);
        }
    }
};

ListBox.prototype.setTooltipDelay = function (tooltipDelay) {
    this.tooltipDelay = tooltipDelay;
};

ListBox.prototype._addTooltipSupport = function (option) {
    var _this = this;

    if (option.cusotmTooltip) {
        var timer;

        option.onmouseover = function (e) {
            var event = window.event ? window.event : e;

//            alert("evt.clientX " + event.clientX );
            function getShowCustomTooltip (event) {
                var evt = {clientX : event.clientX, clientY : event.clientY};

                return function() {

                    var e = (ListBox.cursorEvent) ? ListBox.cursorEvent : event;
                    showCustomTooltip(e, option.cusotmTooltip, null, null, null, 20);
                }
            }

            if (_this.tooltipDelay > -1) {

                timer = setTimeout(getShowCustomTooltip(event), _this.tooltipDelay);
            } else {

                getShowCustomTooltip(event)();
            }
        };

        option.onmouseout = function (e) {
            var event = window.event ? window.event : e;

            hideCustomTooltip(event, false);

            if (timer) {

                clearTimeout(timer);
            }
        }
    }
};

var ListBoxModel = Class.create({
    initialize: function(values) {

        this.values = values;
    },

    getOptionsCount: function() {

        return this.values.length;
    },

    getValueAt : function (index) {

        return this.values[index];
    },

    getValueHtmlAt : function (index) {

        return this.values[index];
    },

    getClassNameAt : function (index) {

        return '';
    },

    getTooltipAt : function (index) {

        return '';
    }
});

var PaginatedListBoxModel = Class.create(ListBoxModel, {

    initialize: function(values, firstResult, maxResult, totalResult) {

        this.values = values;
        this.firstResult = firstResult;
        this.maxResult = maxResult;
        this.totalResult = totalResult;
    },

    getOptionsCount: function() {

        return this.values.length;
    },

    getValues : function () {

        return this.values;
    },

    getValueAt : function (index) {

        return this.values[index];
    },

    getValueHtmlAt : function (index) {

        return this.values[index];
    },

    getClassNameAt : function (index) {

        return this.values[index];
    },

    getFirstResult : function () {

        return this.firstResult;
    },

    getMaxResult : function () {

        return this.maxResult;
    },

    getTotalResult : function () {

        return this.totalResult;
    },

    containsValue : function (value) {

        var contain = false;

        var len = this.getOptionsCount();
        for(var i = 0; i < len; i ++) {

            if (this.getValueAt(i) == value) {
                contain = true;
            }
        }

        return contain;
    },

    getValueIndex : function (value) {

        var contain = false;

        var len = this.getOptionsCount();
        for(var i = 0; i < len; i ++) {

            if (this.getValueAt(i) == value) {

                return i;
            }
        }

        return -1;
    }

});

PaginatedListBoxModel.MESSAGE_SOURCE = {};

var PaginatedListBox = Class.create({

    initialize: function(id, paginatedListBoxModel) {
        this.id = id;
        this.model = paginatedListBoxModel;
        this.navPanelHeight = 60;
        
        this.container = $(this.id);

        this.container.style.overflow = 'hidden';
        this.container.innerHTML = this._getHtmlTemplate();

        this.listBoxContainer = $(this._getListBoxId());
        this.navigationPanel = $(this._getListPageNavigationPanelId());
        this.status = $(this._getListStatusId());
        this.prevLink = new LinkButton(
                this._getPageLinkPrevId(),
                PaginatedListBox.PAGE_LINK_CLASS_NAME,
                PaginatedListBox.PAGE_LINK_HOVER_CLASS_NAME,
                PaginatedListBox.PAGE_LINK_DISABLED_CLASS_NAME);
        this.nextLink = new LinkButton(
                this._getPageLinkNextId(),
                PaginatedListBox.PAGE_LINK_CLASS_NAME,
                PaginatedListBox.PAGE_LINK_HOVER_CLASS_NAME,
                PaginatedListBox.PAGE_LINK_DISABLED_CLASS_NAME);

        this._setUpNavigation();

//        this.navigationPanel.show();
        this.navigationPanelHeight = this.navigationPanel.clientHeight;

        this.refresh();
        this.listBox = new ListBox(this._getListBoxId(), this.model);

        var inst = this;
        this.listBox.onclick = function (e) {
            inst.onclick(e);
        }
    },

    getList: function() {
        return this.listBox;
    },

    refresh: function() {
        if (this.getModel()) {
            this.listBox.setModel(this.getModel());
        }

        this.resize();
        if (this.getModel() && this.getModel().getMaxResult() < this.getModel().getTotalResult()) {

            this.status.innerHTML = this.getStatus();

            this.prevLink.setDisabled(this.getModel().getFirstResult() === 0);

            this.nextLink.setDisabled(this.getShowingResult() == this.getModel().getTotalResult());

            this.navigationPanel.show();
        } else {

            this.status.innerHTML = PaginatedListBox.SHOWING_TITLE + '&nbsp; 0';
            this.navigationPanel.hide();
        }
    },

    resize : function () {
        var h = this.getHeight();

        if (this.getModel() && this.getModel().getMaxResult() < this.getModel().getTotalResult()) {

            this.listBoxContainer.style.height = (this.getHeight() - this.navPanelHeight)  + 'px';
        } else {

            this.listBoxContainer.style.height = (h) + 'px';
        }
    },

    getModel: function () {
        return this.model;
    },

    setModel: function (model) {
        
        this.model = model;
        this.refresh();
    },

    getSelected : function() {

        return this.listBox.getSelected();
    },
        
    getSelectedValues: function() {
        return this.listBox.getSelectedValues();    
    },

    getWidth: function() {
        return this.container.clientWidth;
    },

    getHeight: function() {
        return this.container.clientHeight;
    },

    getStatus: function() {
        return PaginatedListBox.SHOWING_TITLE + '&nbsp;' + (this.getModel().getFirstResult() + 1) + '-' + this.getShowingResult() +
               '&nbsp;' + PaginatedListBox.ABOUT_TITLE + '&nbsp;' + this.getModel().getTotalResult();
    },

    getShowingResult: function() {
        return this.getModel().getFirstResult() +  this.getModel().getOptionsCount();
    },

    setSelectedClassName: function(className) {
        this.listBox.selectionClass = className;
    },

    setOptionClassName: function(className) {
        this.listBox.optionClassName = className;
    },

    reload: function() {
        var inst = this;

        this.onreload(inst.getModel().getFirstResult(), this.listBox.getSelected());
    },

    onreload: function(firstResult) {
        /* Do Nothing*/
    },

    onprev: function(firstResult) {
        /* Do Nothing*/
    },

    onnext: function(firstResult) {
        /* Do Nothing*/
    },

    onclick: function(e) {
        /* Do Nothing*/
    },

    _getHtmlTemplate: function() {
        var html = PaginatedListBox._PAGE_NAV_TEMPLATE
        html = html.replace(new RegExp(PaginatedListBox._LIST_BOX_ID), this._getListBoxId());
        html = html.replace(new RegExp(PaginatedListBox._PAGE_NAV_PANEL_ID), this._getListPageNavigationPanelId());
        html = html.replace(new RegExp(PaginatedListBox._LIST_STATUS_ID), this._getListStatusId());
        html = html.replace(new RegExp(PaginatedListBox._PAGE_LINK_PREV_ID), this._getPageLinkPrevId());
        html = html.replace(new RegExp(PaginatedListBox._PAGE_LINK_NEXT_ID), this._getPageLinkNextId());

        return html;
    },

    _getListBoxId: function() {
        return this.id + PaginatedListBox._LIST_BOX_ID;
    },

    _getListPageNavigationPanelId: function() {
        return this.id + PaginatedListBox._PAGE_NAV_PANEL_ID;
    },

    _getListStatusId: function() {
        return this.id + PaginatedListBox._LIST_STATUS_ID;
    },

    _getPageLinkPrevId: function() {
        return this.id + PaginatedListBox._PAGE_LINK_PREV_ID;
    },

    _getPageLinkNextId: function() {
        return this.id + PaginatedListBox._PAGE_LINK_NEXT_ID;
    },

    _setUpNavigation: function() {

        var inst = this;

        this.prevLink.getElement().innerHTML = PaginatedListBox.PAGE_LINK_PREV_TITLE;
        this.nextLink.getElement().innerHTML = PaginatedListBox.PAGE_LINK_NEXT_TITLE;

        this.prevLink.onclick = function () {
            inst.onprev(inst.getModel().getFirstResult() - inst.getModel().getMaxResult());
        };

        this.nextLink.onclick = function () {
            inst.onnext(inst.getShowingResult());
        };
    }

});

PaginatedListBox.SHOWING_TITLE = 'Showing';
PaginatedListBox.ABOUT_TITLE = 'of about';

PaginatedListBox.PAGE_LINK_CLASS_NAME = 'pageLink';
PaginatedListBox.PAGE_LINK_HOVER_CLASS_NAME = 'pageLinkHover';
PaginatedListBox.PAGE_LINK_DISABLED_CLASS_NAME = 'pageLinkDisabled';
PaginatedListBox.PAGE_LINK_PREV_TITLE = 'Prev';
PaginatedListBox.PAGE_LINK_NEXT_TITLE = 'Next';
PaginatedListBox._LIST_BOX_ID = 'ListBox';
PaginatedListBox._PAGE_NAV_PANEL_ID = 'ListBoxNavigationPanel';
PaginatedListBox._LIST_STATUS_ID = 'ListBoxStatus';
PaginatedListBox._PAGE_LINK_PREV_ID = 'ListBoxPrevPage';
PaginatedListBox._PAGE_LINK_NEXT_ID = 'ListBoxNextPage';
PaginatedListBox._PAGE_NAV_TEMPLATE = '<div id="' + PaginatedListBox._LIST_BOX_ID + '" style="height:100%;overflow-y:auto;"></div>' +
                                      '<div id="' + PaginatedListBox._PAGE_NAV_PANEL_ID + '" class="pagingZone" style="height:100%;">' +
                                      '<div id="' + PaginatedListBox._LIST_STATUS_ID + '">Showing 0</div> <br>' +
                                      '<a id="' + PaginatedListBox._PAGE_LINK_PREV_ID + '" href="#null" style="float:left;"></a>' +
                                      '<a id="' + PaginatedListBox._PAGE_LINK_NEXT_ID + '" href="#null" style="float:right;"></a>' +
                                      '<span style="clear:both;"></a>' +
                                      '</div>';

