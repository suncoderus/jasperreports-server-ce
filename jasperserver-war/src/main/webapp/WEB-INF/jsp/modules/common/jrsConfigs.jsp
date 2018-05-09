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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz"%>
<%@ page import="com.jaspersoft.jasperserver.war.webHelp.WebHelpLookup" %>
<%@ page import="com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder" %>

<%--Global JRS State/Config object --%>

<script type="text/javascript">
    var JRS = {};

    var __jrsConfigs__ = {

        i18n: {},
        localContext: {},
        isIPad: "${isIPad}",
        contextPath: "${pageContext.request.contextPath}",
        runtimeContextPath: "${pageContext.request.contextPath}/runtime/${not empty runtimeHash ? runtimeHash : jsOptimizationProperties.runtimeHash}",
        organizationsFolderUri: "${not empty organizationsFolderUri ? organizationsFolderUri : commonProperties.organizationsFolderUri}",
        publicFolderUri: "${not empty publicFolderUri ? publicFolderUri : commonProperties.publicFolderUri}",
        tempFolderUri: "${not empty tempFolderUri ? tempFolderUri : commonProperties.tempFolderUri}",
        enableAccessibility: "${not empty enableAccessibility ? enableAccessibility : commonProperties.enableAccessibility}",
        organizationId: "${not empty organizationId ? organizationId : commonProperties.organizationId}",
        commonReportGeneratorsMetadata: ${not empty reportGenerators ? reportGenerators : '[]'},
        templatesFolderUri: '${not empty templatesFolderUri ? templatesFolderUri : templateProperties.templatesFolderUri}',
        defaultTemplateUri: '${not empty defaultTemplateUri ? defaultTemplateUri : templateProperties.defaultTemplateUri}',
        advNotSelected: "<spring:message code="ADH_162_NULL_SAVE_REPORT_SOURCE" javaScriptEscape="true"/>",
        templateNotSelected: "<spring:message code="ADH_162_NULL_SELECT_TEMPLATE_SOURCE" javaScriptEscape="true"/>",
        calendar: {

            userLocale: "${userLocale}",

            timepicker: {
                timeText: '<spring:message code="CAL_time" javaScriptEscape="true"/>',
                hourText: '<spring:message code="CAL_hour" javaScriptEscape="true"/>',
                minuteText: '<spring:message code="CAL_min" javaScriptEscape="true"/>',
                secondText: '<spring:message code="CAL_second" javaScriptEscape="true"/>',
                currentText: '<spring:message code="CAL_now" javaScriptEscape="true"/>',
                closeText: '<spring:message code="CAL_close" javaScriptEscape="true"/>',
                timeFormat: '<spring:message code="calendar.time.format" javaScriptEscape="true"/>',
                dateFormat: '<spring:message code="calendar.date.format" javaScriptEscape="true"/>',
                separator:'<spring:message code="calendar.datetime.separator" javaScriptEscape="true"/>'
            },

            i18n: {
                bundledCalendarTimeFormat: '<spring:message code="calendar.time.format" javaScriptEscape="true"/>',
                bundledCalendarFormat: '<spring:message code="calendar.date.format" javaScriptEscape="true"/>'
            }
        },

        webHelpModuleState: {
            contextMap: <%= WebHelpLookup.getInstance().getHelpContextMapAsJSON() %>,
            hostURL: '<%= WebHelpLookup.getInstance().getHostURL() %>',
            pagePrefix: '<%= WebHelpLookup.getInstance().getPagePrefix() %>'
        },

        urlContext: "${pageContext.request.contextPath}",
        defaultSearchText: "<spring:message code='SEARCH_BOX_DEFAULT_TEXT'/>",
        serverIsNotResponding: "<spring:message code='confirm.slow.server'/>"
    };

    // dirty hack to get path to current theme
    __jrsConfigs__.currentThemePath = "<spring:theme code='theme.css'/>".split("/").slice(0, -1).join("/");

    __jrsConfigs__.isProVersion = "${isProVersion}" === "true" ? true : false;

    __jrsConfigs__.userLocale = "${userLocale}";
    __jrsConfigs__.userTimezone = "<%= TimeZoneContextHolder.getTimeZone().getID() %>";

    __jrsConfigs__.avaliableLocales = [<c:forEach items="${userLocales}" var="locale" varStatus="sts">"${locale.code}"<c:if test="${!sts.last}">, </c:if></c:forEach>];
    __jrsConfigs__.avaliableLocalesFullName = [<c:forEach items="${userLocales}" var="locale" varStatus="sts">"<spring:message code="locale.option" arguments='${locale.code},${locale.description}'/>"<c:if test="${!sts.last}">, </c:if></c:forEach>];

    __jrsConfigs__.localeSettings = {
        locale: "${userLocale}",
        decimalSeparator: "${requestScope.decimalSeparatorForUserLocale}" || ".",
        groupingSeparator: "${requestScope.groupingSeparatorForUserLocale}" || ",",
        timeFormat: "<spring:message code="calendar.time.format" javaScriptEscape="true"/>",
        dateFormat: "<spring:message code="calendar.date.format" javaScriptEscape="true"/>",
        timestampSeparator: "<spring:message code="calendar.datetime.separator" javaScriptEscape="true"/>"
    };
    //Heartbeat

    __jrsConfigs__.heartbeatInitOptions = {
        baseUrl: "${pageContext.request.contextPath}",
        showDialog: false,
        sendClientInfo: false
    };

    <c:choose>
        <c:when test="${param['decorate'] == 'no' || param['viewAsDashboardFrame'] == 'true' || param['sessionDecorator'] == 'no' || sessionScope['sessionDecorator'] == 'no'}">
            __jrsConfigs__.initAdditionalUIComponents = false;
        </c:when>
        <c:otherwise>
            __jrsConfigs__.initAdditionalUIComponents = true;
        </c:otherwise>
    </c:choose>

    <%
     com.jaspersoft.jasperserver.war.common.HeartbeatBean heartbeat =
           (com.jaspersoft.jasperserver.war.common.HeartbeatBean) application.getAttribute("concreteHeartbeatBean");
    %>
    <authz:authorize ifAnyGranted="ROLE_ADMINISTRATOR">
    <%
        if (heartbeat != null && heartbeat.haveToAskForPermissionNow()) {
    %>
    __jrsConfigs__.heartbeatInitOptions.showDialog = true;
    <%
    }
    %>
    </authz:authorize>
    <authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR">
    <%
        if (heartbeat != null && heartbeat.isMakingCalls()
                && session != null && session.getAttribute("jsHeartbeatSentClientInfo") == null) {
            session.setAttribute("jsHeartbeatSentClientInfo", Boolean.TRUE);
    %>
    __jrsConfigs__.heartbeatInitOptions.sendClientInfo = true;
    <%
        }
    %>
    </authz:authorize>

    __jrsConfigs__.flowExecutionKey = "${flowExecutionKey}";

    if (window.localStorage && window.localStorage.previousPageHash){
        window.location.hash = window.localStorage.previousPageHash;
        delete window.localStorage.previousPageHash;
    }

    __jrsConfigs__.xssNonce = '${sessionScope.XSS_NONCE}';
</script>
