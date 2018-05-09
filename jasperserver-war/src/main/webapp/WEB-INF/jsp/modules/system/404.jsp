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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>

<c:set var="isAuthorized" value="false"/>
<c:set var="anonymousClass" value="anonymous"/>
<authz:authorize ifNotGranted="ROLE_ANONYMOUS">
    <c:set var="isAuthorized" value="true"/>
    <c:set var="anonymousClass" value=""/>
</authz:authorize>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="error.404.title"/></t:putAttribute>
    <t:putAttribute name="pageClass">${anonymousClass}</t:putAttribute>
    <t:putAttribute name="moduleName" value="system/systemErrorMain"/>

    <t:putAttribute name="headerContent">
        <c:if test="${isAuthorized == false}">
            <meta content="true" name="noMenu">
        </c:if>
    </t:putAttribute>
    <t:putAttribute name="bodyID" value="systemError"/>
    <t:putAttribute name="bodyClass">oneColumn</t:putAttribute>
    <t:putAttribute name="bodyContent">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="column decorated primary"/>
            <t:putAttribute name="containerTitle"><spring:message code="error.404.title"/></t:putAttribute>
            <t:putAttribute name="bodyID" value="errorPageContent"/>
            <t:putAttribute name="bodyContent">
                <t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
                    <t:putAttribute name="bodyContent">
                        <p class="message"><spring:message code="error.404.message"/></p>
                    </t:putAttribute>
                </t:insertTemplate>
            </t:putAttribute>
        </t:insertTemplate>
    </t:putAttribute>
</t:insertTemplate>
