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
<%@ taglib prefix="spring" uri="/spring" %>
<%@ page isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>

<head>
  <!-- ${sessionScope.XSS_NONCE} do not remove -->

  <title><spring:message code='jsp.loginError.title'/></title>
  <LINK href="/stylesheets/styles.css" type="text/css" rel="stylesheet">
  <LINK href="/stylesheets/base.css" type="text/css" rel="stylesheet">
  <meta name="pageHeading" content="<spring:message code='jsp.loginError.pageHeading'/>"/>
  <meta name="noMenu" content="true">
</head>

<body>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<form name="fmErrorPage">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
<c:choose>
    <c:when test="${exception!=null}">
  <tr><td>${exception}</td></tr>
    </c:when>
    <c:otherwise>
  <tr><td align="center" class="ferror"><spring:message code='jsp.loginError.invalidCredentials1'/><br/><spring:message code='jsp.loginError.invalidCredentials2'/></td></tr>
    </c:otherwise>
</c:choose>
</table>
</form>

</body>

</html>


