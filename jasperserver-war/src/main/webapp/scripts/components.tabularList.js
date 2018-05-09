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

/* global Class, $$, Truncator, */

Element.addMethods({
  isTextNode: function(element) {
      element = $(element);
      return element.childElements().length === 0 && element.innerHTML.strip().length > 0;
  }
});

var TabularList = Class.create({
    initialize: function(tableId) {
        if(!tableId){
            this.tableId = 'resultsTable';
        } else {
            this.tableId = tableId;
        }
    },

    truncText: function() {
        //find all the nodes in the table that have text
        var textNodes = $$("#" + this.tableId + " .name *").findAll(Element.isTextNode);
        new Truncator(textNodes);
        $$("td.name").each(function (td) {
            td.setAttribute("nowrap", "nowrap");
        });

        textNodes = $$("#" + this.tableId + " .description *").findAll(Element.isTextNode);
        new Truncator(textNodes);
        $$("td.description").each(function (td) {
            td.setAttribute("nowrap", "nowrap");
        });

        textNodes = $$("#" + this.tableId + " .path *").findAll(Element.isTextNode);
        new Truncator(textNodes);
        $$("td.path").each(function (td) {
            td.setAttribute("nowrap", "nowrap");
        });

        textNodes = $$("#" + this.tableId + " .modifiedDate *").findAll(Element.isTextNode);
        new Truncator(textNodes);
        $$("td.modifiedDate").each(function (td) {
            td.setAttribute("nowrap", "nowrap");
        });
    },

    untruncText: function() {
        //find all the nodes in the table that have text
        var textNodes = $$("#" + this.tableId + " .name *").findAll(Element.isTextNode);
        new Truncator().untruncate(textNodes);
        $$("td.name").each(function (td) {
            td.removeAttribute("nowrap");
        });

        textNodes = $$("#" + this.tableId + " .description *").findAll(Element.isTextNode);
        new Truncator().untruncate(textNodes);
        $$("td.description").each(function (td) {
            td.removeAttribute("nowrap");
        });

        textNodes = $$("#" + this.tableId + " .objectPath *").findAll(Element.isTextNode);
        new Truncator(textNodes, 30);
//        new Truncator().untruncate(textNodes);
        $$("td.path").each(function (td) {
            td.removeAttribute("nowrap");
        });

        textNodes = $$("#" + this.tableId + " .modifiedDate *").findAll(Element.isTextNode);
        new Truncator().untruncate(textNodes);
        $$("td.modifiedDate").each(function (td) {
            td.removeAttribute("nowrap");
        });
    },

    _isTextNode: function(node) {
        return node.childElements().length === 0 && node.innerHTML.strip().length > 0;
    }
});