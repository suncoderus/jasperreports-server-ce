/*
 * Copyright (C) 2005 - 2011 Jaspersoft Corporation. All rights reserved.
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
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

/* global console */

/**
 *  Usage of Input Controls
 */
define(function (require) {

    "use strict";

    require("commons.main");
    var $ = require("jquery"),
        BooleanControl = require("inputControl/ui/BooleanControl"),
        SingleValueTextControl = require("inputControl/ui/SingleValueTextControl"),
        SingleValueNumberControl = require("inputControl/ui/SingleValueNumberControl"),
        SingleValueDateControl = require("inputControl/ui/SingleValueDateControl"),
        SingleValueDatetimeControl = require("inputControl/ui/SingleValueDatetimeControl"),
        SingleValueTimeControl = require("inputControl/ui/SingleValueTimeControl"),
        SingleSelectControl = require("inputControl/ui/SingleSelectControl"),
        SingleSelectRadioControl = require("inputControl/ui/SingleSelectRadioControl"),
        MultiSelectControl = require("inputControl/ui/MultiSelectControl"),
        MultiSelectCheckboxControl = require("inputControl/ui/MultiSelectCheckboxControl");

    var booleanInputControl = new BooleanControl({
            type: "bool",
            uri: "/reports/coffeeBar",
            id: "coffeeBar",
            label: "Coffee Bar",
            mandatory: true,
            validators: [],
            visible: true
        }),
        singleValueText = new SingleValueTextControl({
            type:"singleValueText",
            uri:"/reports/name",
            id:"name",
            label:"Name",
            mandatory: false,
            validators:[],
            visible: true
        }),
        singleValueNumber = new SingleValueNumberControl({
            type:"singleValueNumber",
            uri:"/reports/order",
            id:"order",
            label:"Order",
            mandatory:"true",
            validators:[],
            visible: true
        }),
        singleValueDate = new SingleValueDateControl({
            type:"singleValueDate",
            uri:"/reports/birthDate",
            id:"birthDate",
            label:"Birth Date",
            mandatory:"true",
            validators:[],
            visible: true
        }),
        singleValueDatetime = new SingleValueDatetimeControl({
            type:"singleValueDate",
            uri:"/reports/test",
            id:"birthDate",
            label:"Finish at",
            validators:[],
            visible: true
        }),
        singleValueTime = new SingleValueTimeControl({
            type:"singleValueTime",
            uri:"/reports/test",
            id:"birthDate",
            label:"Start time",
            validators:[],
            visible: true
        }),
        singleSelect = new SingleSelectControl({
            type:"singleSelect",
            uri:"/reports/country",
            id:"country",
            label:"Country",
            validators:[],
            visible: true
        }),
        multiSelect = new MultiSelectControl({
            type:"multiSelect",
            uri:"/reports/state",
            id:"state",
            label:"State",
            validators:[],
            visible: true
        }),
        singleSelectRadio = new SingleSelectRadioControl({
            type:"singleSelectRadio",
            uri:"/reports/countryRadio",
            id:"countryRadio",
            label:"Country",
            validators:[],
            visible: true
        }),
        multipleCheckbox = new MultiSelectCheckboxControl({
            type:"multiSelectCheckbox",
            uri:"/reports/stateCheckbox",
            id:"stateCheckbox",
            label:"State",
            validators:[],
            visible: true
        });

    singleSelect.set({values: [
        {value: "DF", label: "Mexico | CA"},
        {value: "Guerrero", label: "Mexico | Guerrero", selected: "true"},
        {value: "Jalisco", label: "Mexico | Jalisco"},
        {value: "Veracruz", label: "Mexico | Veracruz"}
    ]});

    multiSelect.set({values: [
        {value:"DF", label:"Mexico | CA"},
        {value:"Guerrero", label:"Mexico | Guerrero", selected:"true"},
        {value:"Jalisco", label:"Mexico | Jalisco"},
        {value:"Sinaloa", label:"Mexico | Sinaloa", selected:"true"},
        {value:"Veracruz", label:"Mexico | Veracruz"}
    ]});

    singleSelectRadio.set({values: [
        {value:"Canada", label:"Canada"},
        {value:"Mexico", label:"Mexico"},
        {value:"USA", label:"USA", selected:"true"}
    ]});

    multipleCheckbox.set({values:[
        {value:"DF", label:"Mexico | CA"},
        {value:"Guerrero", label:"Mexico | Guerrero", selected:"true"},
        {value:"Jalisco", label:"Mexico | Jalisco"},
        {value:"Sinaloa", label:"Mexico | Sinaloa", selected:"true"},
        {value:"Veracruz", label:"Mexico | Veracruz"}
    ]});

    $("#booleanControl").append(booleanInputControl.getElem());
    $("#singleTextValueControl").append(singleValueText.getElem());
    $("#singleNumberValueControl").append(singleValueNumber.getElem());
    $("#singleValueDateControl").append(singleValueDate.getElem());
    $("#singleValueDateTimeControl").append(singleValueDatetime.getElem());
    $("#singleValueTimeControl").append(singleValueTime.getElem());
    $("#singleSelectControl").append(singleSelect.getElem());
    $("#multiSelectControl").append(multiSelect.getElem());
    $("#singleRadioControl").append(singleSelectRadio.getElem());
    $("#multipleCheckboxControl").append(multipleCheckbox.getElem());

    console.log("report IC usage");
});

