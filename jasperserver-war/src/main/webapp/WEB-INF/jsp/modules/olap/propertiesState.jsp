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
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} don't remove

    if (typeof Administer === "undefined") {
        Administer = {};
    }

    if (typeof Administer._messages === "undefined") {
        Administer._messages = {};
    }

    Administer.urlContext = "${pageContext.request.contextPath}";
    Administer._messages["jsp.olap.flush.done"]  = '<spring:message code="jsp.olap.flush.done" javaScriptEscape="true"/>';

    __jrsConfigs__.Administer = Administer;


    // Analysis and Designer Options messages
    Administer._messages["JAM_018_ERROR"]  = '<spring:message code="JAM_018_ERROR" javaScriptEscape="true"/>';
    Administer._messages["JAM_019_WHOLE_NUMBER_ERROR"]  = '<spring:message code="JAM_019_WHOLE_NUMBER_ERROR" javaScriptEscape="true"/>';
    Administer._messages["JAM_020_RATIO_NUMBER_ERROR"]  = '<spring:message code="JAM_020_RATIO_NUMBER_ERROR" javaScriptEscape="true"/>';
    Administer._messages["JAM_048_ZERO_OR_GREATER"]  = '<spring:message code="JAM_048_ZERO_OR_GREATER" javaScriptEscape="true"/>';
    Administer._messages["JAM_049_ONE_OR_GREATER"]  = '<spring:message code="JAM_049_ONE_OR_GREATER" javaScriptEscape="true"/>';
    Administer._messages["JAM_050_ONE_TO_99"]  = '<spring:message code="JAM_050_ONE_TO_99" javaScriptEscape="true"/>';
    Administer._messages["JAM_051_INVALID_CLASS"]  = '<spring:message code="JAM_051_INVALID_CLASS" javaScriptEscape="true"/>';
    Administer._messages["JAM_056_UPDATED"]  = '<spring:message code="JAM_056_UPDATED" javaScriptEscape="true"/>';
    Administer._messages["JAM_057_DONE"]  = '<spring:message code="JAM_057_DONE" javaScriptEscape="true"/>';
</script>
</js:out>
