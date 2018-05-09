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

<%-- Markup that is common both to full-decorated and minimally-decorated pages --%>

<%@ include file="../jsp/modules/common/jsEdition.jsp" %>

<%--
***********************************************************************
templates (for cloning) shared by all/most JasperServer pages
***********************************************************************
--%>
<%@ include file="../jsp/templates/menu.jsp" %>
<%@ include file="../jsp/templates/list.jsp" %>
<%@ include file="../jsp/modules/common/tooltip.jsp" %>
<%@ include file="../jsp/modules/common/dnd.jsp" %>

<%--
***********************************************************************
components (not for cloning) shared by all/most JasperServer pages
***********************************************************************
--%>
<!-- save as dialog -->
<tiles:insertTemplate template="/WEB-INF/jsp/templates/saveAs.jsp">
     <tiles:putAttribute name="containerClass" value="hidden centered_vert centered_horz"/>
     <tiles:putAttribute name="bodyContent" >
        <ul class="responsive collapsible folders hideRoot" id="saveAsFoldersTree"></ul>
     </tiles:putAttribute>
</tiles:insertTemplate>


<c:if test="${isProVersion}">
    <t:insertTemplate template="/WEB-INF/jsp/templates/repositorySelection.jsp">
        <t:putAttribute name="containerClass" value="hidden centered_vert centered_horz"/>
        <t:putAttribute name="okButtonLabel"><spring:message code="button.ok"/></t:putAttribute>
    </t:insertTemplate>
    <form id="reportGeneratorForm" action="<c:url value="${contextPath}/reportGenerator.html"/>" method="get" style="display:none">
        <input type="hidden" name="action" value="displayTempReportUnit"/>
        <input type="hidden" name="advUri" value=""/>
        <input type="hidden" name="template" value=""/>
        <input type="hidden" name="generator" value=""/>
        <input type="hidden" name="exportFormat" value="html"/>
    </form>
    <t:insertTemplate template="/WEB-INF/jsp/templates/reportGeneratorProperties.jsp">
        <t:putAttribute name="containerClass" value="hidden"/>
        <t:putAttribute name="containerTitle"><spring:message code="dialog.createReport.title"/></t:putAttribute>
    </t:insertTemplate>
</c:if>

<!-- dimmer - for modal dialogs -->
<div id="pageDimmer" class="dimmer hidden"></div>

<!-- loading cue -->
<tiles:insertTemplate template="/WEB-INF/jsp/templates/loading.jsp">
    <tiles:putAttribute name="containerID" value="loading"/>
    <tiles:putAttribute name="containerClass" value="hidden"/>
</tiles:insertTemplate>

<!-- ajax alert popup dialog -->
<t:insertTemplate template="/WEB-INF/jsp/templates/standardAlert.jsp">
    <t:putAttribute name="containerClass" value="sizeable hidden"/>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="bodyContent">
        <div id="errorPopupContents"></div>
    </t:putAttribute>
</t:insertTemplate>


<%--
Hidden iframe for ajax downloads
--%>
<iframe id="ajax-download-iframe" style="visibility:hidden"></iframe>

<%--
Hidden iframe for ajax uploads
--%>
<iframe id="ajax-upload-iframe" style="visibility:hidden" src="about:blank" ></iframe>

<%--heat beat info--%>
<jsp:include page="../jsp/modules/heartbeat/heartbeat.jsp"/>

<%-- about dialog --%>
<jsp:include page="../jsp/modules/about/about.jsp"/>

	
