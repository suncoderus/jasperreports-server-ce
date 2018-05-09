<%--
  ~ Copyright © 2005 - 2018 TIBCO Software Inc.
  ~ http://www.jaspersoft.com.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <https://www.gnu.org/licenses/>.
  --%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
    <%-- For LAYOUT_IN_PAGE = 4 always start with shown input controls (can't be hidden). --%>
    <c:when test="${hasInputControls and reportControlsLayout == 4}">
        <c:set var="bodyClass" value="twoColumn"/>
    </c:when>
    <c:otherwise>
        <c:set var="bodyClass" value="oneColumn"/>
    </c:otherwise>
</c:choose>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="${reportUnitObject.label}"/>
    <t:putAttribute name="bodyID" value="reportViewer"/>
    <t:putAttribute name="bodyClass" value="${bodyClass}"/>
    <%--<t:putAttribute name="pageClass" value="${bodyClass}"/>--%>
    <t:putAttribute name="moduleName" value="reportViewer/reportViewerMain"/>
    <t:putAttribute name="headerContent">
        <style>
            .novis {visibility:hidden}

            div#reportPartsController {
                position: absolute;
                right: 22px;
                top: 3px;
            }

            div#reportPartsContainer {
                color: #0872A4;
                float: left;
            }

            div#reportPartsContainer div.reportPart {
                cursor:pointer;
                background-color: #efefef;
                border-bottom:transparent;
                border-color:#c5c5c5;
                border-style: solid;
                border-width:1px;
                display: inline-block;
                height:19px;
                margin: 0 2px;
                min-width:100px;
                position:relative;
                top:1px;
                white-space:nowrap;
                zoom:1;
            }

            div#reportPartsContainer div.reportPart.active {
                border:1px solid #c5c5c5;
                border-top:1px solid #c5c5c5;
                border-bottom:transparent;
                height:22px;
                top:1px;
            }

            div#reportPartsContainer span {
                display: block;
                height: 100%;
                line-height: 19px;
                min-width: 100px;
                padding: 0 8px;
                text-align: center;
                width: auto;
            }

            div#reportPartsContainer div.reportPart.active span {
                background:#fff;
                border-bottom:1px solid #ffffff;
                font-weight: bold;
            }

            div#reportPartsNavigation {
                float: left;
            }
        </style>
        <jsp:include page="../inputControls/InputControlConstants.jsp" />
        <%@ include file="ViewReportState.jsp" %>
    </t:putAttribute>

    <t:putAttribute name="bodyContent" >

        <!-- ========== INPUT CONTROLS ON-PAGE FORM: BEGIN =========== -->
        <c:if test="${hasInputControls and (reportControlsLayout == 2 or reportControlsLayout == 4)}">
            <div id="inputControlsForm" class="column secondary decorated sizeable <c:if test="${not empty requestScope.reportOptionsList}">showingSubHeader</c:if>" role="navigation">
                <div class="sizer horizontal"></div>
                <button class="button minimize" type="button"></button>
                <div class="content hasFooter">
                    <div class="header ">
                        <div class="title"><spring:message code="button.controls"/></div>
                        <c:if test="${isPro}">
                            <div class="sub header"></div>
                        </c:if>
                    </div>

                    <div class="body">
                          <js:parametersForm reportName="${requestScope.reportUnit}" renderJsp="${controlsDisplayForm}" />
                    </div>
                    <div class="footer ">
                        <button id="apply" class="button action primary up"><span class="wrap"><spring:message code="button.apply" javaScriptEscape="true"/><span class="icon"></span></span></button>
                        <c:if test="${reportControlsLayout == 2}">
                            <button id="ok" class="button action up"><span class="wrap"><spring:message code="button.ok" javaScriptEscape="true"/><span class="icon"></span></button>
                        </c:if>
                        <button id="reset" class="button action up"><span class="wrap"><spring:message code="button.reset" javaScriptEscape="true"/><span class="icon"></span></button>
                        <c:if test="${reportControlsLayout == 2}">
                          <button id="cancel" class="button action up"><span class="wrap"><spring:message code="button.cancel" javaScriptEscape="true"/><span class="icon"></span></span></button>
                        </c:if>
                        <c:if test="${isPro}">
                            <button id="save" class="button action up" ${isReportFolderReadOnly ? 'disabled="disabled"':''}><span class="wrap"><spring:message code="button.save" javaScriptEscape="true"/><span class="icon"></span></span></button>
                            <button id="remove" class="button action up hidden" ${isReportFolderReadOnly ? 'disabled="disabled"':''}><span class="wrap"><spring:message code="button.remove" javaScriptEscape="true"/><span class="icon"></span></span></button>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- ========== INPUT CONTROLS ON-PAGE FORM: END =========== -->

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="column decorated primary showingToolBar"/>
            <t:putAttribute name="containerID" value="reportViewFrame"/>
            <t:putAttribute name="containerTitle"><%--<spring:message code="report.view.containerTitle"/> - --%>${reportUnitObject.label}</t:putAttribute>

            <!-- ========== TOOLBAR =========== -->
            <t:putAttribute name="headerContent">

                <div id="dataTimestampMessage"></div>
                <ul id="asyncIndicator" class="list buttonSet hidden">
                    <li class="leaf"><button id="asyncCancel" class="button capsule text up" disabled="disabled"><span class="wrap"><spring:message code="button.cancel.loading"/><span class="icon"></span></span></button></li>
                </ul>
                <ul class="list buttonSet">
                    <li class="leaf" role="application"><button id="dataRefreshButton" type="submit" aria-label="<spring:message code="jasper.report.view.button.data.refresh" javaScriptEscape="true"/>" data-title="true" class="button capsule up"><span class="wrap"><span class="icon"></span></span></button></li>
                </ul>

                <div id="reportPartsController" style="display: none">
                    <div id="reportPartsContainer">
                    </div>
                    <div id="reportPartsNavigation" class="control search">
                        <button id="part_prev" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_PREVIOUS"/>" data-title="true" class="button action square move searchPrevious up" disabled="disabled">
                            <span class="wrap"><span class="icon"></span></span>
                        </button>
                        <button id="part_next" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_NEXT"/>"  data-title="true" class="button action square move searchNext up" disabled="disabled">
                            <span class="wrap"><span class="icon"></span></span>
                        </button>
                    </div>
                </div>

                <!-- ========== VIEWER TOOLBAR =========== -->
                <div id="viewerToolbar" class="toolbar">

                    <%--
                        LAYOUT_POPUP_SCREEN = 1;
                        LAYOUT_SEPARATE_PAGE = 2;
                        LAYOUT_TOP_OF_PAGE = 3;
                        LAYOUT_IN_PAGE = 4;
                     --%>

                    <%-- See whether to show input controls trigger button in toolbar. --%>
                    <%-- Button can be shown for all layouts except LAYOUT_IN_PAGE = 4. --%>
                    <c:choose>
                        <c:when test="${hasInputControls and (reportControlsLayout == 1 or reportControlsLayout == 2 or reportControlsLayout == 3)}">
                            <c:set var="controlsHidden" value=""/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="controlsHidden" value="hidden"/>
                        </c:otherwise>
                    </c:choose>

                    <%-- Show input controls trigger button unpressed for first time, then figure out in javascript. --%>
                    <%-- Button can be pressed only for LAYOUT_TOP_OF_PAGE = 3. --%>
                    <c:set var="controlUp" value="up"/>

                    <%-- Set this style ony for LAYOUT_TOP_OF_PAGE = 3. --%>
                    <%-- Not sure whether it is still actual. --%>
                    <c:choose>
                        <c:when test="${hasInputControls and reportControlsLayout == 3}">
                            <c:set var="controlToggle" value="toggle"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="controlToggle" value=""/>
                        </c:otherwise>
                    </c:choose>

                    <!-- ========== LEFT BUTTON SET =========== -->
                    <ul class="list buttonSet">
                        <c:if test="${param.decorate != 'no'}">
                            <ul class="list buttonSet">
                                <li class="leaf" role="application"><button id="back" class="button capsule text up"><span class="wrap"><spring:message code="button.back"/><span class="icon"></span></span></button></li>
                            </ul>
                        </c:if>
                        <li class="node">
                            <ul class="list buttonSet">
                                <c:if test="${isPro}">
                                    <li class="leaf"><button id="fileOptions" class="button capsule mutton up first" aria-label="<spring:message code="button.save"/>"  data-title="true" disabled="true"><span class="wrap"><span class="icon"></span><span class="indicator"></span></span></button></li>
                                </c:if>
                                <c:if test="${!isPro}">
                                    <li class="leaf"><button id="fileOptions" class="button capsule mutton up first" aria-label="<spring:message code="button.save"/> - <spring:message code="feature.pro.only"/>"  data-title="true" disabled="true"><span class="wrap"><span class="icon"></span><span class="indicator"></span></span></button></li>
                                </c:if>
                                <li class="leaf"><button id="export" class="button capsule mutton up last" disabled="disabled" aria-label="<spring:message code="button.export"/>"  data-title="true"><span class="wrap"><span class="icon"></span><span class="indicator"></span></span></button></li>
                            </ul>
                        </li>
                        <li class="node">
                            <ul class="list buttonSet">
                                <li class="leaf"><button id="undo" class="button capsule up first" disabled="disabled" aria-label="<spring:message code="button.undo"/>" data-title="true"><span class="wrap"><span class="icon"></span></span></button>
                                </li>
                                <li class="leaf"><button id="redo" class="button capsule up middle" disabled="disabled" aria-label="<spring:message code="button.redo"/>" data-title="true"><span class="wrap"><span class="icon"></span></span></button></li>
                                <li class="leaf"><button id="undoAll" class="button capsule up last" disabled="disabled" aria-label="<spring:message code="button.undoAll"/>" data-title="true"><span class="wrap"><span class="icon"></span></span></button></li>
                            </ul>
                        </li>
                        <li class="node conditional first">
                            <ul class="list buttonSet">
                                <li id="controls" class="leaf ${controlsHidden}">
                                    <button id="ICDialog" class="button capsule ${controlToggle} ${controlUp}" aria-label="<spring:message code="button.controls"/>" data-title="true">
                                        <span class="wrap">
                                            <span class="icon"></span>
                                        </span>
                                    </button>
                                    <span class="divider"></span>
                                </li>
                            </ul>
                        </li>
                        <li class="node conditional second">
                            <ul class="list buttonSet">
                                <li class="leaf">
                                    <button id="bookmarksDialog" style="display: none;" class="button capsule up" aria-label="<spring:message code="button.bookmarks"/>" data-title="true">
                                        <span class="wrap">
                                            <span class="icon"></span>
                                        </span>
                                    </button>
                                </li>
                            </ul>
                        </li>
                    </ul>
                    <!-- ========== END LEFT BUTTON SET =========== -->

                    <!-- ========== RIGHT BUTTON SET =========== -->
                    <div id="reportToolsRight" class="control toolsRight">
                        <!-- ========== ZOOM =========== -->
                        <div id="reportZoom" class="control zoom">
                            <span class="divider first"></span>
                            <button id="zoom_out" aria-label="<spring:message code="button.zoomOut"/>" data-title="true" class="button action square move zoomOut up"><span class="wrap"><span class="icon"></span></span></button>
                            <button id="zoom_in" aria-label="<spring:message code="button.zoomIn"/>" data-title="true" class="button action square move zoomIn up"><span class="wrap"><span class="icon"></span></span></button>
                            <label for="zoom_value" class="control input textPlus inline">
                                <input id="zoom_value" type="text" value="100%" name="zoom_value" aria-label="<spring:message code="button.zoomOptions"/>">
                                <button id="zoom_value_button" class="button disclosure" aria-label="<spring:message code="button.zoomOptions"/>" data-title="true">
                                    <span class="icon"></span>
                                </button>
                            </label>
                            <!-- <span class="divider last"></span> -->
                        </div>
                        <script type="application/json" id="reportZoomText">
                            [
                                {"key": "0.1", "value": "10%"}, {"key": "0.25", "value": "25%"}, {"key": "0.5", "value": "50%"}, {"key": "0.75", "value": "75%"},
                                {"key": "1", "value": "100%"}, {"key": "1.25", "value": "125%"}, {"key": "2", "value": "200%"}, {"key": "4", "value": "400%"},
                                {"key": "8", "value": "800%"}, {"key": "16", "value": "1600%"}, {"key": "24", "value": "2400%"}, {"key": "32", "value": "3200%"},
                                {"key": "64", "value": "6400%"},
                                {"key": "fit_actual", "value": "<spring:message code="button.zoomOptions.option.actualSize" htmlEscape="true"/>"},
                                {"key": "fit_width", "value": "<spring:message code="button.zoomOptions.option.fitWidth" htmlEscape="true"/>"},
                                {"key": "fit_height", "value": "<spring:message code="button.zoomOptions.option.fitHeight" htmlEscape="true"/>"},
                                {"key": "fit_page", "value": "<spring:message code="button.zoomOptions.option.fitPage" htmlEscape="true"/>"}
                            ]
                        </script>

                        <!-- ========== REPORT SEARCH =========== -->
                        <div id="reportSearch" class="control search">
                            <span class="divider first"></span>
                            <label for="search_report" class="control input textPlus inline">
                                <input id="search_report" type="text" placeholder="<spring:message code="button.searchReportPlaceholder"/>" aria-label="<spring:message code="button.searchReportTitle"/>" name="search_report">
                                <button id="search_report_button" class="button search" aria-label="<spring:message code="button.searchReportTitle"/>" data-title="true">
                                    <span class="icon"></span>
                                </button>
                                <button id="search_options" class="button disclosure" aria-label="<spring:message code="button.searchOptions"/>" data-title="true">
                                    <span class="icon"></span>
                                </button>
                            </label>
                            <button id="search_previous" aria-label="<spring:message code="button.searchPrevious"/>" data-title="true" class="button action square move searchPrevious up" disabled="disabled"><span class="wrap"><span class="icon"></span></span></button>
                            <button id="search_next" aria-label="<spring:message code="button.searchNext"/>" data-title="true" class="button action square move searchNext up" disabled="disabled"><span class="wrap"><span class="icon"></span></span></button>
                            <span class="divider last"></span>
                        </div>
                        <script type="application/json" id="reportSearchText">
                            [
                                {"key": "caseSensitive", "value": "<spring:message code="button.searchOptions.option.caseSensitive" javaScriptEscape="true"/>"},
                                {"key": "wholeWordsOnly", "value": "<spring:message code="button.searchOptions.option.wholeWordsOnly" javaScriptEscape="true"/>"}
                            ]
                        </script>

                        <!-- ========== PAGINATION =========== -->
                        <div id="pagination" class="control paging" role="application">
                            <button id="page_first" type="submit" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_FIRST" javaScriptEscape="true"/>" data-title="true" class="button action square move toLeft up" disabled="disabled"><span class="wrap"><span class="icon"></span></span></button>
                            <button id="page_prev" type="submit" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_PREVIOUS" javaScriptEscape="true"/>" data-title="true" class="button action square move left up" disabled="disabled"><span class="wrap"><span class="icon"></span></span></button>
                            <label class="control input text inline" for="page_current" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_CURRENT_PAGE" javaScriptEscape="true"/>">
                                <span class="wrap"><spring:message code="jasper.report.view.page.intro"/></span>
                                <input class="" id="page_current" type="text" name="currentPage" value=""/>
                                <span class="wrap" id="page_total">&nbsp;</span>
                            </label>
                            <button id="page_next" type="submit" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_NEXT" javaScriptEscape="true"/>" data-title="true" class="button action square move right up" disabled="disabled"><span class="wrap"><span class="icon"></span></span></button>
                            <button id="page_last" type="submit" aria-label="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_LAST" javaScriptEscape="true"/>" data-title="true" class="button action square move toRight up" disabled="disabled"><span class="wrap"><span class="icon"></span></span></button>
                        </div>
                    </div>
                    <!-- ========== END RIGHT BUTTON SET =========== -->

                </div>
                <!-- ========== END VIEWER TOOLBAR =========== -->

                <%--ajax buffer--%>
                <div id="ajaxbuffer" style="display: none;" ></div>

            </t:putAttribute>
            <!-- ========== END TOOLBAR =========== -->

            <!-- ========== REPORT CONTAINER =========== -->
            <t:putAttribute name="swipeScroll" value="${isIPad}"/>
            <t:putAttribute name="bodyContent">

                <!-- ========== INPUT CONTROLS TOP-OF-PAGE FORM: BEGIN =========== -->
                <c:if test="${hasInputControls == true and reportControlsLayout == 3}">
                    <div class="topOfPage" id="inputControlsForm">
                        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                            <t:putAttribute name="containerClass" value="panel pane inputControls ${controlUp == 'up' ? 'hidden' : ''}"/>
                            <t:putAttribute name="headerClass" value="${not empty requestScope.reportOptionsList ? '' : 'hidden'}"/>
                            <t:putAttribute name="headerContent">
                            <c:if test="${isPro}">
                                <div class="sub header"></div>
                            </c:if>
                            </t:putAttribute>

                            <t:putAttribute name="bodyContent">
                                <js:parametersForm reportName="${requestScope.reportUnit}" renderJsp="${controlsDisplayForm}" />
                            </t:putAttribute>

                            <t:putAttribute name="footerContent">
                                <button id="apply" class="button action primary up"><span class="wrap"><spring:message code="button.apply" javaScriptEscape="true"/><span class="icon"></span></span></button>
                                <button id="reset" class="button action up"><span class="wrap"><spring:message code="button.reset" javaScriptEscape="true"/><span class="icon"></span></span></button>
                                <c:if test="${isPro}">
                                    <button id="save" class="button action up" ${isReportFolderReadOnly ? 'disabled="disabled"':''}><span class="wrap"><spring:message code="button.save" javaScriptEscape="true"/><span class="icon"></span></span></button>
                                    <button id="remove" class="button action up hidden" ${isReportFolderReadOnly ? 'disabled="disabled"':''}><span class="wrap"><spring:message code="button.remove" javaScriptEscape="true"/><span class="icon"></span></span></button>
                                </c:if>
                            </t:putAttribute>
                        </t:insertTemplate>
                    </div>
                </c:if>
                <!-- ========== INPUT CONTROLS TOP-OF-PAGE FORM: END =========== -->

                <div style="position: relative">
                    <t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
                        <t:putAttribute name="containerAttributes" value="style='position:absolute;width:700px;height:120px;'" />
                        <t:putAttribute name="bodyContent">
                            <p class="message"><spring:message code="report.view.needInput.warning"/></p>
                        </t:putAttribute>
                    </t:insertTemplate>
                </div>

                <t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
                    <t:putAttribute name="containerID" value="emptyReportID" />
                    <t:putAttribute name="bodyContent">
                        <p class="message"><b>${reportUnitObject.label}</b></p>
                        <p class="message">
                            <%-- currently empty report message is set on server side - see ViewReportAction.runReport --%>
                        </p>
                    </t:putAttribute>
                </t:insertTemplate>

                <%--c:if test="${empty param.frame}">
                    <center>
                </c:if--%>
                <div id="reportContainer" class="" style="position:relative;"></div>
                <%--c:if test="${empty param.frame}">
                    </center>
                </c:if--%>

            </t:putAttribute>

        </t:insertTemplate>

        <!-- ========== INPUT CONTROLS DIALOG =========== -->
        <c:if test="${hasInputControls and reportControlsLayout == 1}">
            <t:insertTemplate template="/WEB-INF/jsp/templates/inputControls.jsp">
                <t:putAttribute name="containerTitle"><spring:message code="button.controls"/></t:putAttribute>
                <t:putAttribute name="containerClass" value="sizeable hidden ${not empty requestScope.reportOptionsList ? 'showingSubHeader' : ''}"/>
                <t:putAttribute name="hasReportOptions" value="false"/>
                <t:putAttribute name="bodyContent">
                    <js:parametersForm reportName="${requestScope.reportUnit}" renderJsp="${controlsDisplayForm}" />
                </t:putAttribute>
            </t:insertTemplate>
        </c:if>

        <!-- ========== SAVE REPORT OPTIONS DIALOG =========== -->
        <c:if test="${isPro}">
            <t:insertTemplate template="/WEB-INF/jsp/templates/saveValues.jsp">
                <t:putAttribute name="containerClass" value="hidden"/>
            </t:insertTemplate>
        </c:if>

        <%-- This form is used for submit actions --%>
        <form id="exportActionForm" action="<c:url value="flow.html"/>" method="post">
            <input type="hidden" name="_flowExecutionKey" value=""/>
            <input type="hidden" name="_eventId" value=""/>
            <input type="hidden" name="output"/>
        </form>

    </t:putAttribute>

</t:insertTemplate>
