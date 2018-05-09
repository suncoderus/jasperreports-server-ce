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
<%@ page import="com.jaspersoft.jasperserver.war.common.HeartbeatBean" %>

<%@ page contentType="text/html" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove

    // Initialization of repository search init object.
    var heartbeatInitOptions = {
        baseUrl: "${pageContext.request.contextPath}",
        showDialog: false,
        sendClientInfo: false
    };

<%
    HeartbeatBean heartbeat = (HeartbeatBean)application.getAttribute("concreteHeartbeatBean");
%>
<authz:authorize ifAnyGranted="ROLE_ADMINISTRATOR">
    <%
        if (heartbeat != null && heartbeat.haveToAskForPermissionNow()) {
    %>
    heartbeatInitOptions.showDialog = true;
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
    heartbeatInitOptions.sendClientInfo = true;
    <%
        }
    %>
</authz:authorize>
</script>
</js:out>