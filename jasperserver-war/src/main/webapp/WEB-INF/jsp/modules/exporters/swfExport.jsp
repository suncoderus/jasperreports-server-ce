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

<%@ page import="
		com.jaspersoft.jasperserver.war.dto.StringOption,
		com.jaspersoft.jasperserver.war.common.UserLocale
		" %>

<%@ taglib prefix="spring" uri="/spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<!-- ${sessionScope.XSS_NONCE} do not remove -->

  <title><spring:message code='jsp.exportAsFlash.pageTitle'/></title>
  <meta name="noMenu" content="true">
  <meta name="pageHeading" content="<spring:message code='jsp.exportAsFlash.pageTitle'/>"/>

<%
  response.setHeader("LoginRequested","true");
  session.removeAttribute("js_uname");
  session.removeAttribute("js_upassword");
%>

</head>

<body id="swfExport">
	<div class="swfWrapper">
		<object class="swfExport" >
			<param name="wmode" value="transparent">
		  	<param name="movie" value="jasperreports-flash-5.0.1.swf"/>
            <param name="FlashVars" value="jrpxml=jrpxml?jrprint=${jasperPrintName}">
		  	<embed src="jasperreports-flash-5.0.1.swf" FlashVars="jrpxml=jrpxml?jrprint=${jasperPrintName}" width="100%" height="100%" wmode="transparent"></embed>
		</object>
	</div>
</body>

</html>
