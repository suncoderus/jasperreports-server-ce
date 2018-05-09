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


<js:jasperviewer
    imageServlet="${pageContext.request.contextPath}/reportimage?jrprint=${jasperPrintName}&"
    resourceServlet="${pageContext.request.contextPath}/reportresource?"
    page='${requestScope.page}'
    jasperPrintAttribute="${jasperPrintName}"
    innerPagination="${showPagination}"
    ignorePageMargins="${ignorePageMargins}"
    renderJsp="${requestScope.reportDisplayForm}"
    reportContext="${rptCtx}"/>
