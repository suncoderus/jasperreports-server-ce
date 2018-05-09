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
<%@ page contentType="text/html" %>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="/WEB-INF/jasperserver.tld" prefix="js" %>

<%@ page import="com.jaspersoft.jasperserver.search.model.SearchActionModelSupport" %>

<%@ include file="../common/jsEdition.jsp" %>

<%-- Setting bodyID, page title, primary container title, nothing to display message. --%>
<c:set var="bodyId" value="repoBrowse"/>
<c:set var="pageTitle"><spring:message code='SEARCH_BROWSE_REPOSITORY' javaScriptEscape='true'/></c:set>
<c:set var="primaryContainerTitle"><spring:message code='SEARCH_SEARCH' javaScriptEscape='true'/></c:set>
<c:set var="nothingToDisplayMsg"><spring:message code="repository.nothingToDisplay.browse" javaScriptEscape="true"/></c:set>
<c:set var="pageClass">twoColumn</c:set>

<c:choose>
    <c:when test="${mode == 'search'}">
        <c:set var="bodyId" value="repoSearch"/>
        <c:set var="pageTitle"><spring:message code='SEARCH_SEARCH_REPOSITORY' javaScriptEscape='true'/></c:set>
        <c:set var="primaryContainerTitle"><spring:message code='SEARCH_TITLE' javaScriptEscape='true'/></c:set>
        <c:set var="nothingToDisplayMsg"><spring:message code="repository.nothingToDisplay.search" javaScriptEscape="true"/></c:set>
        <c:set var="pageClass">twoColumn</c:set>
    </c:when>
    <c:when test="${mode == 'library'}">
        <c:set var="bodyId" value="repoLibrary"/>
        <c:set var="pageTitle"><spring:message code='SEARCH_LIBRARY' javaScriptEscape='true'/></c:set>
        <c:set var="primaryContainerTitle"><spring:message code='SEARCH_LIBRARY' javaScriptEscape='true'/></c:set>
        <c:set var="nothingToDisplayMsg"><spring:message code="repository.nothingToDisplay.library" javaScriptEscape="true"/></c:set>
        <c:set var="pageClass">oneColumn</c:set>
    </c:when>
    <c:otherwise>
        <c:set var="bodyId" value="repoBrowse"/>
        <c:set var="pageTitle"><spring:message code='SEARCH_BROWSE_REPOSITORY' javaScriptEscape='true'/></c:set>
        <c:set var="primaryContainerTitle"><spring:message code='SEARCH_TITLE' javaScriptEscape='true'/></c:set>
        <c:set var="nothingToDisplayMsg"><spring:message code="repository.nothingToDisplay.browse" javaScriptEscape="true"/></c:set>
        <c:set var="pageClass">twoColumn</c:set>
    </c:otherwise>
</c:choose>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="${pageTitle}"/>
    <t:putAttribute name="bodyID" value="${bodyId}"/>
    <t:putAttribute name="bodyClass" value="${pageClass}"/>
    <t:putAttribute name="moduleName" value="repository/repositoryMain"/>
    <t:putAttribute name="headerContent" >

        <jsp:include page="resultsState.jsp"/>

        <%--get action model data for search menus--%>
        <script type="text/json" id="searchActionModel">
            <%= SearchActionModelSupport.getInstance((String) request.getAttribute("mode")).getClientActionModelDocument(request) %>
        </script>

    </t:putAttribute>
    <t:putAttribute name="bodyContent">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value="results"/>
            <t:putAttribute name="containerClass" value="column decorated primary"/>
            <t:putAttribute name="containerTitle" value="${primaryContainerTitle}"/>
            <t:putAttribute name="headerContent">
                    <t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
				        <t:putAttribute name="containerID" value="secondarySearchBox"/>
                        <t:putAttribute name="containerAttr">
                            <c:if test="${mode == 'search'}">data-tab-index="3" data-component-type="search"</c:if>
                        </t:putAttribute>
				        <t:putAttribute name="inputID" value="secondarySearchInput"/>
				    </t:insertTemplate>

                <!--
                <form id="secondarySearchBox" class="searchLockup">
                    <label for="secondarySearchInput" class="offLeft"><spring:message code="button.search" javaScriptEscape="true"/></label>
                    <input class="" id="secondarySearchInput"/>
                    <b class="right"><button class="button searchClear"></button></b>
                    <button class="button search up"></button>
                </form>
                -->

                <ul id="sortMode" class=""></ul>
            </t:putAttribute>
            <t:putAttribute name="subHeaderContent">
                <div class="toolbar">
                    <ul class="list buttonSet">
                    	<li class="node open">
                   			 <ul class="list buttonSet">
		                        <li class="leaf"><button id="run" class="button capsule text up first"><span class="wrap"><spring:message code="RM_BUTTON_RUN" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
		                        <li class="leaf"><button id="edit" class="button capsule text up middle"><span class="wrap"><spring:message code="RM_BUTTON_WIZARD" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
		                        <li class="leaf"><button id="open" class="button capsule text up last"><span class="wrap"><spring:message code="RM_BUTTON_OPEN" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
<!--
							</ul>
						</li>
                    	<li class="node open">
                   			 <ul class="list buttonSet">
-->
		                        <li class="leaf"><button id="copy" class="button capsule text up first"><span class="wrap"><spring:message code="RM_BUTTON_COPY_RESOURCE" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
		                        <li class="leaf"><button id="cut" class="button capsule text up middle"><span class="wrap"><spring:message code="RM_BUTTON_MOVE_RESOURCE" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
		                        <li class="leaf"><button id="paste" class="button capsule text up last"><span class="wrap"><spring:message code="RM_BUTTON_COPY_HERE" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
<!--
							</ul>
						</li>
                    	<li class="node open">
                   			 <ul class="list buttonSet">
-->
		                        <li class="leaf"><button id="remove" class="button capsule text up"><span class="wrap"><spring:message code="SEARCH_BULK_DELETE" javaScriptEscape="true"/></span><span class="icon"></span></button></li>
							</ul>
						</li>
                    </ul>
                </div>
                <div class="sub header hidden">
                    <ul id="filterPath" class=""></ul>
                </div>
                <ul class="list collapsible tabular resources fourColumn header" id="resultsListHeader">
                    <li class="resources first leaf scheduled" id="resultsListHeader_item1">
                        <div class="wrap draggable">
                            <div class="column one">
                                <div class="scheduled icon button"></div>
                                <div class="separator"></div>
                                <div class="disclosure icon button"></div>
                            </div>
                            <div class="column two">
                                <h3 class="resourceName"><spring:message code="repository.resource.header.name" javaScriptEscape="true"/></h3>
                                <p class="resourceDescription"><spring:message code="repository.resource.header.description" javaScriptEscape="true"/></p>
                            </div>
                            <div class="column three resourceType"><spring:message code="repository.resource.header.type" javaScriptEscape="true"/></div>
                            <div class="column four">
                                <p class="createdDate"><spring:message code="repository.resource.header.createdDate" javaScriptEscape="true"/></p>
                                <p class="modifiedDate"><spring:message code="repository.resource.header.modifiedDate" javaScriptEscape="true"/></p>
                            </div>
                        </div>
                    </li>
                </ul>
            </t:putAttribute>
            <t:putAttribute name="bodyID" value="resultsContainer"/>
            <t:putAttribute name="bodyAttributes">tabindex="-1"</t:putAttribute>
			<!-- Include swipeScroll here -->
            <t:putAttribute name="bodyContent">
                <ol id="resultsList" class="" tabIndex="0" data-tab-index="4" data-component-type="list" js-navtype="dynamiclist"></ol>
                <t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
                    <t:putAttribute name="bodyContent">
                        <p class="message">${nothingToDisplayMsg}</p>
                    </t:putAttribute>
                </t:insertTemplate>
            </t:putAttribute>
        </t:insertTemplate>

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value="searchFilters"/>
            <t:putAttribute name="containerClass" value="column decorated secondary sizeable ${mode == 'browse' ? 'hidden' : ''}"/>
            <t:putAttribute name="containerElements">
                <div class="sizer horizontal"></div>
                <button class="button minimize"></button>
            </t:putAttribute>
            <t:putAttribute name="containerTitle"><spring:message code="SEARCH_FILTERS" javaScriptEscape="true"/></t:putAttribute>
            <%--
            <t:putAttribute name="swipeScroll" value="${isIPad}"/>
             --%>
            <t:putAttribute name="bodyID" value="filtersPanelContent"/>
            <t:putAttribute name="bodyContent">
           		<div id="filtersPanel"></div>
            </t:putAttribute>
        </t:insertTemplate>

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value="folders"/>
            <t:putAttribute name="containerClass" value="column decorated secondary sizeable ${mode == 'search' ? 'hidden' : ''}"/>
                <t:putAttribute name="containerElements">
                    <div class="sizer horizontal"></div>
                    <button class="button minimize"></button>
                </t:putAttribute>
            <t:putAttribute name="containerTitle"><spring:message code="SEARCH_FOLDERS" javaScriptEscape="true"/></t:putAttribute>
            <t:putAttribute name="swipeScroll" value="${isIPad}"/>
            <t:putAttribute name="bodyID">foldersPodContent</t:putAttribute>
            <t:putAttribute name="bodyContent">
                <ul id="foldersTree" style="position:absolute;" class="list responsive collapsible folders" ${mode == "browse" ? "data-tab-index='3' data-component-type='tree'" : ""}></ul>
                <div id="ajaxbuffer" style="display:none"></div>
            </t:putAttribute>
        </t:insertTemplate>

        <jsp:include page="searchComponents.jsp"/>


        <t:insertTemplate template="/WEB-INF/jsp/templates/standardConfirm.jsp">
            <t:putAttribute name="containerClass">hidden</t:putAttribute>
            <t:putAttribute name="leftButtonId" value="deleteResourceOK"/>
            <t:putAttribute name="rightButtonId" value="deleteResourceCancel"/>
        </t:insertTemplate>
    </t:putAttribute>
</t:insertTemplate>
