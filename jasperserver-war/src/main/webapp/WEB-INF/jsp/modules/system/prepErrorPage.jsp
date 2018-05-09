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
<%@ page import="com.jaspersoft.jasperserver.war.action.ErrorPageHandlerAction" %>
<%@ page import="com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page language="java" isErrorPage="true" %>
<% ApplicationContext applicationContext = StaticApplicationContext.getApplicationContext();
    ErrorPageHandlerAction errorPageHandler = (ErrorPageHandlerAction)applicationContext.getBean("errorPageHandlerAction");
    errorPageHandler.prepareErrorPage(request, response, exception);%>
<jsp:forward page="/WEB-INF/jsp/modules/system/errorPage.jsp" />
