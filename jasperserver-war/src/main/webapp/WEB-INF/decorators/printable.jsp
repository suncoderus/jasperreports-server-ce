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
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>

<html>
    <head>
        <!-- ${sessionScope.XSS_NONCE} do not remove -->

        <title><decorator:title default="<spring:message code='decorators.printable.welcome'/>" /></title>
        <decorator:head />
    </head>

    <body onload="window.print(); initmenu();">
        <spring:message code='decorators.printable.printedOn'/> <%=new java.util.Date()%>.<br/>
        <hr noshade="noshade" size="1"/>
        <br/>
        <decorator:body />
    </body>
</html>


