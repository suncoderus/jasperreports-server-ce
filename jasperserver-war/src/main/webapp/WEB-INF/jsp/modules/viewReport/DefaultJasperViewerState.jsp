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

<%--
 This code formers proper script and passes it for evaluation on client side.
 It fixes all pagination issues, exporters issues in all browsers including IE.
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page import="net.sf.jasperreports.engine.export.*" %>
<%@ page import="net.sf.jasperreports.engine.*" %>
<%@ page import="com.jaspersoft.jasperserver.war.action.ExporterConfigurationBean" %>
<%@ page import="com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit" %>
<%@ page import="org.springframework.context.MessageSource" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.jaspersoft.jasperserver.war.action.GenericActionModelBuilder" %>
<%@ page import="org.springframework.web.util.JavaScriptUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.springframework.context.i18n.LocaleContextHolder" %>

<textarea class="hidden" style="display:none" name="_evalScript">
    <%-- formatting report refresh time --%>
    <spring:message code="date.format" var="dateFormat"/>
    <spring:message code="time.format" var="timeFormat"/>
    <fmt:formatDate type="both" value="${dataTimestamp}" timeZone="${sessionScope.userTimezone}" pattern="${dateFormat}" var="reportRefreshDate"/>
    <fmt:formatDate type="both" value="${dataTimestamp}" timeZone="${sessionScope.userTimezone}" pattern="${timeFormat}" var="reportRefreshTime"/>

    <js:out javaScriptEscape="true">
    Report.jasperPrintName = '${jasperPrintName}';
    Report.pageIndex = ${empty pageIndex ? 0 : pageIndex};
    Report.lastPageIndex = ${empty lastPageIndex ? "null" : lastPageIndex};
    Report.lastPartialPageIndex = ${empty lastPartialPageIndex ? "null" : lastPartialPageIndex};
    Report.pageTimestamp = ${empty pageTimestamp ? "null" : pageTimestamp};
    Report.snapshotSaveStatus = '${snapshotSaveStatus}';
    Report.flowExecutionKeyOutput = '${flowExecutionKey}';
    Report.emptyReport = ${emptyReport};
    Report.hasInputControls = ${hasInputControls};

    Report.dataTimestampMessage = '<spring:message code="jasper.report.view.data.snapshot.message" arguments="${reportRefreshDate},${reportRefreshTime}" javaScriptEscape="true"/>';

    </js:out>
</textarea>

<%
    //  TODO how about moving export menu items into actionModel-viewReport.xml? generatedOptions can be used there
    Boolean isEmptyReport = (Boolean) request.getAttribute("emptyReport");
    MessageSource messageSource = (MessageSource) request.getAttribute("messageSource");

    StringBuilder exportersList = new StringBuilder("[");
    boolean firstItem = true;
    if (!isEmptyReport) {
        Map<String, ExporterConfigurationBean> configuredExporters = (Map<String, ExporterConfigurationBean>) request.getAttribute("configuredExporters");
        for (Map.Entry<String, ExporterConfigurationBean> configuredExporter : configuredExporters.entrySet()) {
            if (!firstItem) {
                exportersList.append(",");
            } else {
                firstItem = false;
            }
            ExporterConfigurationBean exporter = configuredExporter.getValue();
            String exporterKey = configuredExporter.getKey();
            ReportUnit reportUnit = (ReportUnit) request.getAttribute("reportUnitObject");
            String exportFilename = null;
            if (exporter.getCurrentExporter() != null && reportUnit != null) {
                exportFilename = exporter.getCurrentExporter().getDownloadFilename(
                        request, reportUnit.getName());
            }

            String descriptionMessage = JavaScriptUtils.javaScriptEscape(
                    messageSource.getMessage(exporter.getDescriptionKey(), null, LocaleContextHolder.getLocale()));

            exportersList.append("{\"type\": \"simpleAction\",");
            exportersList.append("\"text\": \"" + descriptionMessage + "\",");
            exportersList.append("\"action\": \"Report.exportReport\",");
            exportersList.append("\"actionArgs\": [\"" + exporterKey + "\"");
            if (StringUtils.isNotEmpty(exportFilename)) {
                String url = request.getContextPath() + "/flow.html/flowFile/" + exportFilename;
                exportersList.append(", \"" + url + "\"");
            }
            exportersList.append("]}");
        }
    }
    exportersList.append("]");
    pageContext.setAttribute("exportersList", exportersList.toString());
%>

<script type="text/json" id="toolbarText">
<js:out escapeScript="false">
    {"toolbar_export":${exportersList}, <%= GenericActionModelBuilder.getEmbeddableActionModelDocument("viewReport")%>}
</js:out>
</script>
