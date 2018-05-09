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

/* global Report, viewer, Controls, ControlsBase, toolbarButtonModule, matchAny, JRS, isIPad, isSupportsTouch,
 TouchController, layoutModule, centerElement, dialogs, ajax, buttonManager, _, alert, addDataToForm
 */

Report.toolbarActionMap = {
    'back' : "Report.goBack",
    'ICDialog' : "Controls.show",
    'export': "doNothing",
    'undo': "Report.undo",
    'redo': "Report.redo",
    'undoAll': "Report.undoAll"
};
/*
    Init & reportRefreshed methods.
 */
jQuery.extend(Report, {
    initialize: function() {
        this.commonInit();

        Report.exportForm = $(Report.EXPORT_ACTION_FORM);
        this.nothingToDisplay = $('nothingToDisplay');

        if ($(Report.DATA_REFRESH_BUTTON)) {
            $(Report.DATA_REFRESH_BUTTON).observe('mouseup', function() {
                viewer.jive && viewer.jive.hide();
                if ((typeof Controls !== "undefined")) {
                    var selectedData = Controls.viewModel.get('selection');
                    var controlsUri = ControlsBase.buildSelectedDataUri(selectedData);
                    this.refreshReport({freshData: true}, null, controlsUri ? '&' + controlsUri : '');
                } else {
                    this.refreshReport({freshData: true}, null, '');
                }
            }.bindAsEventListener(this));
        }

        if ($(Report.ASYNC_CANCEL_BUTTON)) {
            $(Report.ASYNC_CANCEL_BUTTON).observe('mouseup', function() {
                this.cancelReportAsyncExecution(true);
            }.bindAsEventListener(this));
        }

        /**
         *  Init toolbar
         */
        if (window.toolbarButtonModule) {
            toolbarButtonModule.ACTION_MODEL_TAG = Report.TOOLBAR_SUBMENU;
            toolbarButtonModule.initialize(Report.toolbarActionMap);
        }

        if ($(Report.PAGINATION_PAGE_CURRENT)) { // observe page navigation input
            $(Report.PAGINATION_PAGE_CURRENT).observe('change', function(e) {
                this.goToPage($(Report.PAGINATION_PAGE_CURRENT).getValue());
                e.stop();
            }.bindAsEventListener(this));


            // observe page navigation buttons
            $(this.PAGINATION_CONTAINER).observe('click', function(e) {
                var elem = e.element();
                e.stop();

                for (var pattern in this.pageActions) {
                    if (matchAny(elem, [pattern], true)) {
                        this.navigateToReportPage(this.pageActions[pattern]);
                        return;
                    }
                }
            }.bindAsEventListener(this));
        }

        if (document.location.href.indexOf("noReturn=true") > 0) {
            buttonManager.disable($('back'))
        }

        if ((typeof Controls !== "undefined")) {
            Controls.initialize.bind(Controls)(Report.refreshReport);
        } else {
            Report.refreshReport();
        }
    },
    reportRefreshed: function(silentUpdate) {
//        Report.snapshotSaveStatus = null;
        var fr,r;

        /*
         * Adding following line to force Google chrome to display ajax content
         * injected into #reportContainer. Bug fix 23988.
         */
        if(typeof JRS !== 'undefined' && JRS.fid){
            fr = jQuery('#'+JRS.fid, window.parent.document);
            r = fr.contents().find('#reportContainer');
            fr.removeClass('hidden').show();

            if(!isIPad()) fr.parent().parent().css('background-image','none');
            var h = r.height();
            var w = r.width();
            if(isSupportsTouch()){
                new TouchController(r.get(0),fr.get(0).parentNode.parentNode,{
                    use2Fingers:true,
                    showBorders:true,
                    noInit3d:true
                });

                r.children('div').bind({
                    'touchstart':function(){
                        fr.parent().parent().siblings().removeClass('over');
                        fr.parent().parent().addClass('over');
                    }
                })
            }
            r.show();

            // bloody hack to remember initial size of report frame
            window.reportFrameHeight = window.reportFrameHeight || fr.parent().height();
            window.reportFrameWidth = window.reportFrameWidth || fr.parent().width();
        } else {
            var ic = $(ControlsBase.INPUT_CONTROLS_FORM);
            if(isSupportsTouch()){
                if(this.touchController){
                    this.touchController.reset();
                } else {
                    var scrollWrapper =  jQuery('#reportContainer').parent().parent();
                    var contentWidth = jQuery('#reportContainer > div').width();
                    (scrollWrapper.width() < contentWidth) && (scrollWrapper.css('width', contentWidth + 'px'));
                    this.touchController = new TouchController(scrollWrapper[0],scrollWrapper.parent()[0],{
                        noInit3d:true,
                        scrollbars: true
                    });
                }
                var bm = jQuery('#' + ControlsBase.INPUT_CONTROLS_FORM + ' > button.minimize');
                if(ic && bm && bm.is(":visible")){
                    var m = ic.hasClassName('minimized');
                    layoutModule.maximize(ic);
                    m && layoutModule.minimize(ic);
                }
            }
            jQuery('#reportContainer').show().height();
        }
        jQuery('#reportContainer').parents(".body").slice(0,1).scrollTop(0);
        /*
         Hide nothing to display html
         */
        if (Report.nothingToDisplay) {
            Report.nothingToDisplay.addClassName(layoutModule.HIDDEN_CLASS);
            jQuery("#" + Report.DATA_REFRESH_BUTTON).removeAttr(layoutModule.DISABLED_ATTR_NAME);
        }

        var paginationState = jQuery("#paginationIndexHolder");
        var emptyReportState = jQuery("#emptyReportMessageHolder");

        if (paginationState.length > 0) {
            var lastPageIndex = paginationState.attr("data-lastPageIndex");
            jQuery("#emptyReportID").addClass('hidden');

            if (lastPageIndex === "0") {
                jQuery("#pagination").addClass('hidden');
            } else {
                jQuery("#pagination").removeClass('hidden');
            }
        }
        if (emptyReportState && emptyReportState.length > 0) {
            var emptyReportMessage = emptyReportState.html();
            jQuery("#pagination").addClass('hidden');
            jQuery(jQuery("#emptyReportID p.message")[1]).html(emptyReportMessage);
            jQuery("#emptyReportID").removeClass('hidden');
            centerElement(jQuery("#emptyReportID"), {horz: true, vert: true});
        }

        Report.refreshPagination(silentUpdate);
        Report.refreshExporters();
        Report.refreshSave();
        Report.refreshAsyncCancel();

        if (this.dataTimestampMessage && $(Report.DATA_TIMESTAMP_SPAN)) {
            $(Report.DATA_TIMESTAMP_SPAN).update(this.dataTimestampMessage);
        }

        jQuery('#innerPagination').css('margin', 0);

        dialogs.popup.hideShared($(ajax.LOADING_ID), Report.REPORT_COMPONENT_ID);
    }
});
/*
    UI updates after report is updated or canceled
*/
jQuery.extend(Report, {
    refreshExporters: function() {
        buttonManager[this.lastPageIndex != null ? "enable" : "disable"]($('export'));
    },
    refreshSave: function() {/* overridden in Pro */},
    refreshPagination: function(silentUpdate) {
        var currentPageIndex = viewer.reportInstance.currentpage;
        var lastPage = viewer.reportInstance.status.totalPages;

        if (this.emptyReport || (lastPage != null && lastPage <= 0)) {
            buttonManager.disable($(Report.PAGINATION_PAGE_FIRST));
            buttonManager.disable($(Report.PAGINATION_PAGE_PREV));
            buttonManager.disable($(Report.PAGINATION_PAGE_CURRENT));
            buttonManager.disable($(Report.PAGINATION_PAGE_NEXT));
            buttonManager.disable($(Report.PAGINATION_PAGE_LAST));
            if ($(Report.PAGINATION_PAGE_CURRENT)) {
                $(Report.PAGINATION_PAGE_CURRENT).setValue("");
                $(Report.PAGINATION_PAGE_TOTAL).update("");
            }
            return;
        }

        if (currentPageIndex > 0) {
            buttonManager.enable($(Report.PAGINATION_PAGE_FIRST));
            buttonManager.enable($(Report.PAGINATION_PAGE_PREV));
        } else {
            buttonManager.disable($(Report.PAGINATION_PAGE_FIRST));
            buttonManager.disable($(Report.PAGINATION_PAGE_PREV));
        }

        if (lastPage == null) {
            if (this.lastPartialPageIndex == null || currentPageIndex < this.lastPartialPageIndex) {
                buttonManager.enable($(Report.PAGINATION_PAGE_NEXT));
            } else {
                buttonManager.disable($(Report.PAGINATION_PAGE_NEXT));
            }
            buttonManager.disable($(Report.PAGINATION_PAGE_LAST));
        } else if ((currentPageIndex+1) < lastPage) {
            buttonManager.enable($(Report.PAGINATION_PAGE_NEXT));
            buttonManager.enable($(Report.PAGINATION_PAGE_LAST));
        } else {
            buttonManager.disable($(Report.PAGINATION_PAGE_NEXT));
            buttonManager.disable($(Report.PAGINATION_PAGE_LAST));
        }

        buttonManager.enable($(Report.PAGINATION_PAGE_CURRENT));
        if ($(Report.PAGINATION_PAGE_CURRENT)) {
            if (!silentUpdate) {
                $(Report.PAGINATION_PAGE_CURRENT).setValue(currentPageIndex + 1);
            }

            if (lastPage == null) {
                $(Report.PAGINATION_PAGE_TOTAL).update("");
            } else {
                $(Report.PAGINATION_PAGE_TOTAL).update(this.getMessage("jasper.report.view.page.of") + lastPage);
            }
        }

        this.pageActions = {
            'button#page_first': 0,
            'button#page_prev': currentPageIndex - 1,
            'button#page_next': currentPageIndex + 1
        };

        if (viewer.reportInstance.status.totalPages != null) {
            this.pageActions['button#page_last'] = lastPage - 1;
        }
    },
    refreshAsyncCancel: function(canceled) {
        var meta = viewer.reportInstance.status;

        if ($(Report.ASYNC_CANCEL_BUTTON)) {
            if(canceled) {
                $('asyncIndicator').addClassName(layoutModule.HIDDEN_CLASS);
                buttonManager.disable(Report.ASYNC_CANCEL_BUTTON);
                $(Report.DATA_REFRESH_BUTTON).removeClassName(layoutModule.HIDDEN_CLASS);
            } else {
                if(meta.jasperPrintName && meta.reportStatus == 'running' && meta.totalPages == null) {
                    buttonManager.enable(Report.ASYNC_CANCEL_BUTTON);
                    $('asyncIndicator').removeClassName(layoutModule.HIDDEN_CLASS);
                    $(Report.DATA_REFRESH_BUTTON).addClassName(layoutModule.HIDDEN_CLASS);
                } else {
                    $('asyncIndicator').addClassName(layoutModule.HIDDEN_CLASS);
                    buttonManager.disable(Report.ASYNC_CANCEL_BUTTON);
                    $(Report.DATA_REFRESH_BUTTON).removeClassName(layoutModule.HIDDEN_CLASS);
                }
            }
        }
    }
});
/*
    Methods bound to toolbar buttons
*/
jQuery.extend(Report, {
    exportReport: function(type, formAction) {

        var
            exportForm = Report.exportForm,
            savedActionValue,
            genericParams,
            postData,
            cleanupForm;

        jQuery(exportForm).empty();

        exportForm.method = "post";
        exportForm.target='_blank';

        savedActionValue = exportForm.action;
        cleanupForm = function () {
            exportForm.target = '_self';
            exportForm.action = savedActionValue;
        };

        genericParams = {
            _eventId: "export",
            _flowExecutionKey: Report.reportExecutionKey(),
            output: "" + type
        };

        var selection = Controls.getLastSelection();
        postData = _.extend(genericParams, selection);

        addDataToForm(exportForm, postData);

        if (formAction) {
            exportForm.action = formAction;
        }

        exportForm.submit();

        setTimeout(cleanupForm, 500);
    },
    goBack: function() {
        if (!Report.confirmExit()) {
            return;
        }

        // disable back button after first click
        buttonManager.disable($('back'));

        window.history.back();
    },
    open: function() {
        alert("Not implemented yet: open report")
    },
    save: function() {
        alert("Report saving is not available in community edition");
    },
    saveAs: function() {
        alert("Report saving is not available in community edition");
    },
    undo: function() {
        viewer.reportInstance.undo();
    },
    redo: function() {
        viewer.reportInstance.redo();
    },
    undoAll: function() {
        viewer.reportInstance.undoAll();
    },
    cancelReportAsyncExecution: function(bAsync) {
        if (!viewer.reportInstance.status.jasperPrintName) {
            return;
        }

        buttonManager.disable($(Report.ASYNC_CANCEL_BUTTON));

        viewer.reportInstance.cancelExecution(bAsync).then(function(jsonResponse, textStatus, jqXHR) {
            var status = jsonResponse.result.status;
            /*
             If report execution finished do nothing.
             */
            if (viewer.reportInstance.status.reportStatus == 'finished' || viewer.reportInstance.status.totalPages != null) {
                return;
            }

            if (jsonResponse.result.lastPartialPageIndex) {
                Report.lastPartialPageIndex = jsonResponse.result.lastPartialPageIndex;
            }

            if (status == "error") {
                alert("Report execution error: " + jsonResponse.result.errorMessage);
                return;
            }

            if (status == "canceled") {
                Report.refreshAsyncCancel(true);
                dialogs.systemConfirm.show(Report.getMessage("jasper.report.view.report.canceled"), 5000);
                return;
            } else {
                if (jsonResponse.result.lastPageIndex) {
                    Report.lastPageIndex = jsonResponse.result.lastPageIndex;
                    viewer.reportInstance.status.totalPages = Report.lastPageIndex + 1;
                }

                if (jsonResponse.result.snapshotSaveStatus) {
                    Report.snapshotSaveStatus = jsonResponse.result.snapshotSaveStatus;
                }

                viewer.reportInstance.status.reportStatus = status;

                Report.refreshPagination(true);
                Report.refreshExporters();
                Report.refreshSave();
                Report.refreshAsyncCancel();
            }
        }, function(jqXHR) {
            if(jqXHR.getResponseHeader("lastPartialPageIndex")) {
                Report.lastPartialPageIndex = jqXHR.getResponseHeader("lastPartialPageIndex");
            }
            Report.refreshPagination(true);
        });
    }
});





/*
 Report.cancelCheckPageUpdated = function() {
    if (this.checkPageUpdatedTimeoutId != null) {
        window.clearTimeout(this.checkPageUpdatedTimeoutId);
        this.checkPageUpdatedTimeoutId = null;
    }
 }

 Report.reportPageRefreshed = function(silentUpdate) {

 }

 Report.initUpdateCheckDelay = function() {
    Report.updateDelay = 1000;
    Report.updateDelayIncrement = 0;
 }

 Report.reportDone = function() {
    return !this.reportRunning || this.lastPageIndex != null;
 }

 Report.reportSuccess = function() {
    return this.lastPageIndex != null;
 }

 Report.pageUpdateCheckRequired = function() {
    // check for updates if the report is not finished
    return !this.reportDone() && this.jasperPrintName;
 }

 Report.scheduleCheckPageUpdated = function() {
     if (this.pageUpdateCheckRequired()) {
        var self = this;
        var pageUpdatedCall = function() {
            self.checkPageUpdated();
        };

        var delay = Report.updateDelay;
        if (delay > 5000) {
            delay = 5000;
        } else {
            Report.updateDelay += Report.updateDelayIncrement;
            Report.updateDelayIncrement += 1000;
        }

        this.checkPageUpdatedTimeoutId = window.setTimeout(pageUpdatedCall, delay);
     }
 }

 Report.checkPageUpdated = function() {
     this.cancelCheckPageUpdated();

     if (!this.pageUpdateCheckRequired()) {
        // final page, total page count known -> no update check required
        return;
     }

     var params = {
        jasperPrintName: this.jasperPrintName
     };

     if (this.pageTimestamp != null) {
        // the page is not final, check for updates
        params.pageIndex = this.pageIndex;
        params.pageTimestamp = this.pageTimestamp;
     }

     var url = 'viewReportPageUpdateCheck.html?' + Object.toQueryString(params);

     var options = {
        mode: AjaxRequester.prototype.EVAL_JSON,
        silent: true,//silent
        errorHandler: function(ajaxAgent) {
            return Report.jsonReportStatusErrorHandler(ajaxAgent, function(ajaxAgent) {
                // don't do anything, but schedule a new update refresh
                Report.scheduleCheckPageUpdated();
                return true;
            });
        },
        callback: function(jsonResponse) {
            Report.jsonReportStatusCallback(jsonResponse);

            if (jsonResponse.result.pageModified) {
                // refresh the current page
                Report.navigateToReportPage(Report.pageIndex, true, true);
            }
        }
     };

     ajaxTargettedUpdate(url, options);
 };

Report.rerunReport = function(urlParams, options, reportParameters) {
    var paramsString = '';

    if (reportParameters) {
        paramsString = Object.toQueryString(reportParameters);
    }

    if (Report.areInputControlsEnabled()) {
        var selectedData = Controls.viewModel.get('selection');
        var controlsUri = ControlsBase.buildSelectedDataUri(selectedData);
        this.refreshReport(urlParams, options, paramsString + (controlsUri ? '&' + controlsUri : ''));
    } else {
        this.refreshReport(urlParams, options, paramsString);
    }
};

Report.isNothingToDisplayShown = function() {
    if (Report.nothingToDisplay) {
        return !Report.nothingToDisplay.hasClassName(layoutModule.HIDDEN_CLASS);
    }
    return false;
};

Report.jsonReportStatusErrorHandler = function(ajaxAgent, nonReportErrorHandler) {
    if (!isValidJsonResponse(ajaxAgent)) {
        // see if it's a report execution error
        if (ajaxAgent.getResponseHeader("reportError")) {
            Report.refreshAsyncCancel(true);

            if (ajaxAgent.getResponseHeader("lastPartialPageIndex")) {
                Report.lastPartialPageIndex = ajaxAgent.getResponseHeader("lastPartialPageIndex");
                Report.refreshPartialLastPage();
            }

            // report execution error, show the popup
            var handled = baseErrorHandler(ajaxAgent);
            if (!handled) {
                // this shouldn't normally happen
                alert("Unexpected response");
            }
            return true;
        } else {
            // other type of error
            return nonReportErrorHandler(ajaxAgent);
        }
    }
    return false;
}

Report.jsonReportStatusCallback = function(jsonResponse) {
    if (viewer.reportInstance.status.reportStatus == 'finished' || viewer.reportInstance.status.totalPages != null) {
        // report completion was already processed, nothing to do
        return;
    }

     if (jsonResponse.result.lastPartialPageIndex) {
        Report.lastPartialPageIndex = jsonResponse.result.lastPartialPageIndex;
        Report.refreshPartialLastPage();
     }

     var status = jsonResponse.result.status;

     if (status == "error") {
        alert("Report execution error: " + jsonResponse.result.errorMessage);
        return;
     }

     if (status == "canceled") {
        Report.refreshAsyncCancel(true);
        dialogs.systemConfirm.show(Report.getMessage("jasper.report.view.report.canceled"), 5000);
        return;
     } else {
        if (jsonResponse.result.lastPageIndex) {
        Report.lastPageIndex = jsonResponse.result.lastPageIndex;
     }

     if (jsonResponse.result.snapshotSaveStatus) {
        Report.snapshotSaveStatus = jsonResponse.result.snapshotSaveStatus;
     }

     Report.refreshPagination(true);
     Report.refreshExporters();
     Report.refreshSave();
     Report.refreshAsyncCancel();
}


 var url = 'viewReportAsyncCancel.html?' + Object.toQueryString({jasperPrintName: Report.jasperPrintName});
 var options = {
 mode: AjaxRequester.prototype.EVAL_JSON,
 errorHandler: function(ajaxAgent) {
 return Report.jsonReportStatusErrorHandler(ajaxAgent, function(ajaxAgent) {
 baseErrorHandler(ajaxAgent);
 return true;
 });
 },
 callback: function(jsonResponse) {
 Report.jsonReportStatusCallback(jsonResponse);

 // it's possible that the report had finished before clicking cancel.
 // in that case, if we have an incomplete page refresh it
 if (jsonResponse.result.status == "finished" && Report.pageTimestamp != null) {
 Report.navigateToReportPage(Report.pageIndex, true, true);
 }
 }
 };

 ajaxTargettedUpdate(url, options);

 */

