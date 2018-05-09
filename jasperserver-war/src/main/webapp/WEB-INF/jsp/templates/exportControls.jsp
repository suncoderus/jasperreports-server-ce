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

Usage:

	<t:insertTemplate template="/WEB-INF/jsp/templates/exportControls.jsp">
	     //some selectore to controls container
	    <t:putAttribute name="containerID" value=""/>
	     //detalization of displayed functionality, could be 'short' or 'extended'
	     // 'short' provide functions for resource export only
	     // 'extended'  adds user/roles and events export function
	    <t:putAttribute name="detalization" value=""/>
	</t:insertTemplate>
	
--%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="typeID" name="typeID" classname="java.lang.String" ignore="true"/>

<%--Jasper's components templates--%>
<jsp:include page="componentsTemplates.jsp"/>

<script type="text/javascript">

    // ${sessionScope.XSS_NONCE} do not remove

    if (typeof __jrsConfigs__.Export === "undefined") {
        __jrsConfigs__.Export = {};
    }

    __jrsConfigs__.Export.i18n = {
        "file.name.empty": '<spring:message code="export.file.name.empty" javaScriptEscape="true"/>',
        "file.name.too.long": '<spring:message code="export.file.name.too.long" javaScriptEscape="true"/>',
        "file.name.not.valid": '<spring:message code="export.file.name.not.valid" javaScriptEscape="true"/>',
        "export.select.users":'<spring:message code="export.select.users" javaScriptEscape="true"/>',
        "export.select.roles":'<spring:message code="export.select.roles" javaScriptEscape="true"/>',
        "error.invalid.response" : '<spring:message code="error.invalid.response" javaScriptEscape="true"/>',
        "error.invalid.request" : '<spring:message code="error.invalid.request" javaScriptEscape="true"/>',
        "error.timeout" : '<spring:message code="error.timeout" javaScriptEscape="true"/>',
        "export.session.expired" : '<spring:message code="export.session.expired" javaScriptEscape="true"/>',
        "export.server.not.avaliable" : '<spring:message code="export.server.not.avaliable" javaScriptEscape="true"/>'
    };

    __jrsConfigs__.Export.initParams = {
        container: "${containerID}",
        type : "${typeID}",
        modal: !"${containerID}"
    };
</script>

<%--Export's Templates--%>
<jsp:include page="exportTemplates.jsp" />
