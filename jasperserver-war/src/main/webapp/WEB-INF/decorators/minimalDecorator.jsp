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

<%@ page contentType="text/html" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/spring" prefix="spring" %>
<%@ taglib uri="/WEB-INF/jasperserver.tld" prefix="js" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.jaspersoft.jasperserver.war.webHelp.WebHelpLookup" %>
<%@ page import="com.jaspersoft.jasperserver.api.engine.common.service.impl.NavigationActionModelSupport" %>

<html>
<head>
    <title>Jaspersoft: <decorator:title/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=${requestScope['com.jaspersoft.ji.characterEncoding']}">
    <%--<meta http-equiv="X-UA-Compatible" content="IE=8"/>--%>
    <link rel="shortcut icon" href="favicon.ico"/>

    <%@ include file="decoratorMinimalImports.jsp" %>

    <decorator:head/>
</head>

<body id="<decorator:getProperty property='body.id'/>" class="noDecoration <decorator:getProperty property='body.class'/>">

<%@ include file="decoratorMinimalComponents.jsp" %>

<div id="frame">

    <c:if test="${pageProperties['meta.noMenu']==null && param['decorate'] != 'no'}">
        <div id="mainNavigation" class="menuRoot menu horizontal primaryNav" navtype="menu">
            <ul id="navigationOptions" data-tab-index="2" data-component-type="navigation" role="menubar">
                <li id="main_home" class="leaf" role="menuitem"><p class="wrap button"><span class="icon"></span><spring:message code="menu.home"/></p></li>
                <c:if test='<%= !((NavigationActionModelSupport)application.getAttribute("concreteNavigationActionModelSupport")).banUserRole() %>'>
                    <li id="main_library" class="leaf" role="menuitem"><p class="wrap button"><span class="icon"></span><spring:message code="menu.library"/></p></li>
                </c:if>
            </ul>
        </div>
    </c:if>

    <!-- START decorated page content-->
    <decorator:body/>
    <!-- END decorated page content -->
</div>

<div id="systemMessageConsole" style="display:none;">
    <p id="systemMessage"></p>
</div>
    <%--JavaScript which is common to all pages and requires JSTL access--%>
    <%@ include file="../jsp/modules/commonJSTLScripts.jsp" %>
</body>
</html>
