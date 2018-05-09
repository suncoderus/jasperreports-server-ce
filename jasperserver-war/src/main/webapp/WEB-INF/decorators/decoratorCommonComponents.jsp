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

<%-- Markup that is common to full-decorated pages --%>

<%@ include file="decoratorMinimalComponents.jsp" %>

<!-- logo -->
<div id="logo"></div>

<!-- about dialog -->
<jsp:include page="../jsp/modules/about/about.jsp"/>

<!-- global search box -->
<t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
    <t:putAttribute name="containerID" value="globalSearch"/>
    <t:putAttribute name="containerAttr" value="data-tab-index='1' data-component-type='search'"/>
    <t:putAttribute name="inputID" value="searchInput"/>
    <t:putAttribute name="accClass" value="stdnavinitialfocus"/> 
    <t:putAttribute name="inputTabindex" value="1"/>
</t:insertTemplate>

<c:if test="${isProVersion}">
    <c:if test='<%= ((NavigationActionModelSupport)application.getAttribute("concreteNavigationActionModelSupport")).isUsersExceeded() %>'>
        <t:insertTemplate template="/WEB-INF/jsp/templates/userCountExceeded.jsp">
            <t:putAttribute name="containerID" value="userCountExceeded"/>
            <t:putAttribute name="containerClass" value="userCountExceeded"/>
        </t:insertTemplate>
    </c:if>
</c:if>

	
<%--
***********************************************************************
authorization for logged in user
***********************************************************************
--%>
<ul id="metaLinks" class="horizontal" tabindex="3" role="menubar">
    <li id="userID" tabindex="-1" role="menuitem">
        <authz:authorize ifNotGranted="ROLE_ANONYMOUS">
            <span id="casted">
                <c:if test="<%= com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl.isUserSwitched() %>">
                    <c:set var="principalUserFullName" value="<%= ((com.jaspersoft.jasperserver.api.metadata.user.domain.User)
                          com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl.
                          getSourceAuthentication().getPrincipal()).getFullName() %>"/>
                    ${principalUserFullName}
                    <spring:message code="jsp.main.as"/>
                </c:if>
            </span>
            <authz:authentication property="principal.fullName"/>
        </authz:authorize>
    </li>
    <c:set var="isShowHelp" scope="page"><%= WebHelpLookup.getInstance().isShowHelpTrue() %></c:set>
	<c:if test="${isProVersion && isShowHelp}"><li id="help" tabindex="-1" role="menuitem"><a href="#" id="helpLink"><spring:message code="decorator.helpLink"/></a></li></c:if>
    <li id="main_logOut" tabindex="-1" class="last" role="menuitem"><a id="main_logOut_link" href="#" onclick="javascript:return false;"><spring:message code="menu.logout"/></a></li>
</ul>
