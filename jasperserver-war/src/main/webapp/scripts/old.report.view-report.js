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

var closeToRepository = false;

function exportReport(type, formAction)
{
    document.viewReportForm.target='_blank';
    var timeoutForm = "document.viewReportForm.target='_self';";
    if (formAction)
    {
        timeoutForm += "document.viewReportForm.action='" + document.viewReportForm.action + "';";
        document.viewReportForm.action = formAction;
    }
    setTimeout(timeoutForm, 500);
    document.viewReportForm.output.value="" + type;
    document.viewReportForm._eventId_export.click();
}

function setBlankFormTarget()
{
    document.viewReportForm.target='_blank';
}

function setSelfFormTarget()
{
    document.viewReportForm.target='_self';
}

function backToInputControlsPage()
{
    document.viewReportForm._eventId_back.click();
}

function closeViewReport()
{
    document.viewReportForm._eventId_close.click();
}

function showInputControlsDialog(auto)
{
    closeToRepository = Boolean(auto);
    showDiv('inputControlsContainer');
    if (typeof firstInputControlName != 'undefined' && firstInputControlName) {
        var inputOrSelect = $(firstInputControlName);
        if (inputOrSelect) {
            inputOrSelect.focus();
        }
    }
}

function inputControlsDialogCancel()
{
    if (closeToRepository)
    {
        closeViewReport();
    }
    else
    {
        cancelInputControlsDialog();
    }
}

function cancelInputControlsDialog()
{
    hideInputControlsDialog();
    var url = "flow.html?_flowExecutionKey=" + inputControlsFlowExecutionKey()
        + "&_eventId=revertToSaved"
        + "&decorate=no";
    ajaxTargettedUpdate(
        url,
        {
            fillLocation:"inputControlsContainer",
            callback:"inputValuesUpdated();",
            errorHandler:baseErrorHandler
        });
}

function hideInputControlsDialog()
{
    hideDiv('inputControlsContainer');
}

function setInputValues(postAction)
{
    var postCall = postAction ? ("inputValuesUpdated(); if (!hasInputValuesErrors()) { " + postAction + " };") : null;
    var extraParams = {"_eventId": "setInputValues", "decorate" : "no"};
    ajaxTargettedFormSubmit(
        "inputControlsFrm",
        "flow.html",
        {
            extraPostData: extraParams,
            fillLocation:"inputControlsContainer",
            callback: postCall,
            errorHandler: baseErrorHandler
        });
}

function inputValuesUpdated()
{
    evaluateScripts("inputControlsContainer");
}

function hasInputValuesErrors()
{
    var errorsEl = document.getElementById("_inputValuesErrors");
    return errorsEl.value == "true";
}

refreshReportErrorHandler = baseErrorHandler;

function refreshReport()
{
    var url = "flow.html?_flowExecutionKey=" + reportExecutionKey()
        + "&_eventId=refreshReport&decorate=no";
    //ajaxTargettedUpdate(url, "reportContainer", "reportContained", "reportRefreshed()", refreshReportErrorHandler);
    ajaxTargettedUpdate(
        url,
        {
            fillLocation:"reportContainer",
            toLocation:"reportContained",
            callback:"reportRefreshed();",
            errorHandler:refreshReportErrorHandler
        });
}

function reportExecutionKey()
{
    var flowExecutionKey;
    if (document.inputControlsFrm)
    {
        flowExecutionKey = inputControlsFlowExecutionKey();
    }
    else
    {
        flowExecutionKey = document.viewReportForm._flowExecutionKey.value;
    }
    return flowExecutionKey;
}

function reportRefreshed()
{
    closeToRepository = false;
    copyReportFlowExecutionKey();
}

function copyReportFlowExecutionKey()
{
    if (document.inputControlsFrm)
    {
        document.inputControlsFrm._flowExecutionKey.value = viewReportFlowExecutionKey();
    }
}

function copyControlsFlowExecutionKey()
{
    if (document.viewReportForm)
    {
        document.viewReportForm._flowExecutionKey.value = inputControlsFlowExecutionKey();
    }
}

function submitInputValues()
{
    setInputValues("refreshReport();hideInputControlsDialog();");
}

function applyInputValues()
{
    setInputValues("refreshReport()");
}

function viewReportFlowExecutionKey()
{
    return document.viewReportForm._flowExecutionKey.value;
}

function inputControlsFlowExecutionKey()
{
    if (document.inputControlsFrm._flowExecutionKey.toString().indexOf('NodeList') != -1) {
        return document.inputControlsFrm._flowExecutionKey[0].value;
    } else {
        return document.inputControlsFrm._flowExecutionKey.value;
    }
}

function navigateToReportPage(page)
{
    var url = "flow.html?_flowExecutionKey=" + viewReportFlowExecutionKey()
        + "&_eventId=navigate&pageIndex=" + page
        + "&decorate=no";
    //ajaxTargettedUpdate(url, "reportContainer", "reportContained", "copyReportFlowExecutionKey()", baseErrorHandler);
    ajaxTargettedUpdate(
        url,
        {
            fillLocation:"reportContainer",
            toLocation:"reportContained",
            callback:"copyReportFlowExecutionKey();",
            errorHandler:baseErrorHandler
        });
}

function navigateToDashboardReportPage(page)
{
    var url = "flow.html?_flowExecutionKey=" + viewReportFlowExecutionKey()
        + "&_eventId=navigate&pageIndex=" + page
        + "&decorate=no"
        + "&viewAsDashboardFrame=true";
    //ajaxTargettedUpdate(url, "reportContainer", "reportContained", "copyReportFlowExecutionKey()", baseErrorHandler);
    ajaxTargettedUpdate(
        url,
        {
            fillLocation:"reportContainer",
            toLocation:"reportContained",
            callback:"copyReportFlowExecutionKey();",
            errorHandler:baseErrorHandler
        });
}

function inputControlsDialogReset()
{
    var url = "flow.html?_flowExecutionKey=" + inputControlsFlowExecutionKey()
        + "&_eventId=resetToDefaults"
        + "&decorate=no";
    //ajaxTargettedUpdate(url, "inputControlsContainer", null, "inputValuesUpdated()", baseErrorHandler);
    ajaxTargettedUpdate(
        url,
        {
            fillLocation:"inputControlsContainer",
            callback:"inputValuesUpdated()",
            errorHandler:baseErrorHandler
        });
}

function submitTopInputValues()
{
    setInputValues("refreshReport();");
}

function isTopControlsPanelShown()
{
    return document.getElementById("topInputControlsPanel").style.display != "none";
}

function hideTopControlsPanel()
{
    document.getElementById("topInputControlsPanel").style.display = "none";
    document.getElementById("topControlsHideImg").style.display = "none";
    document.getElementById("topControlsShowImg").style.display = "inline";
    document.getElementById("topControlsHideSpan").style.display = "none";
    document.getElementById("topControlsShowSpan").style.display = "inline";
}

function showTopControlsPanel()
{
    document.getElementById("topInputControlsPanel").style.display = "";
    document.getElementById("topControlsShowImg").style.display = "none";
    document.getElementById("topControlsHideImg").style.display = "inline";
    document.getElementById("topControlsShowSpan").style.display = "none";
    document.getElementById("topControlsHideSpan").style.display = "inline";
}

function toggleTopControlsPanel()
{
    if (isTopControlsPanelShown())
    {
        var url = "flow.html?_flowExecutionKey=" + inputControlsFlowExecutionKey()
            + "&_eventId=toggleTopControls"
            + "&decorate=no";
        //ajaxTargettedUpdate(url, "inputControlsContainer", null, "inputValuesUpdated();hideTopControlsPanel();copyControlsFlowExecutionKey();", baseErrorHandler);
        ajaxTargettedUpdate(
            url,
            {
                fillLocation:"inputControlsContainer",
                callback:"inputValuesUpdated();hideTopControlsPanel();copyControlsFlowExecutionKey();",
                errorHandler:baseErrorHandler
            });
    }
    else
    {
        var url = "flow.html?_flowExecutionKey=" + inputControlsFlowExecutionKey()
            + "&_eventId=toggleTopControls"
            + "&decorate=no";
        //ajaxTargettedUpdate(url, "inputControlsContainer", null, "inputValuesUpdated();showTopControlsPanel();copyControlsFlowExecutionKey();", baseErrorHandler);
        ajaxTargettedUpdate(
            url,
            {
                fillLocation:"inputControlsContainer",
                callback:"inputValuesUpdated();showTopControlsPanel();copyControlsFlowExecutionKey();",
                errorHandler:baseErrorHandler
            });
    }
}

function checkCurrentPage(evt) {
    var evt  = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode == 13) && (node.name=="currentPage")) {
      goToPage(node.value);
      return false;
    }
}

function goToPage(page) {
    if(parseInt(page))
    {
      document.getElementById("checkErrorsRow").style.display = "none";
        navigateToReportPage(parseInt(page)-1);
    }
    else
    {
      document.getElementById("checkErrorsRow").style.display = "block";
    }
}
