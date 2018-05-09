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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- Login Help Dialog -->
<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass" value="panel dialog overlay moveable centered_horz centered_vert hidden"/>
    <t:putAttribute name="containerID" value="helpLoggingIn"/>
    <t:putAttribute name="containerTitle"><spring:message code='LOGIN_HELP'/></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
        <p class="message"><spring:message code='LOGIN_SIGN_IN_AS'/>:</p>
        <ul class="decorated">
            <c:if test="${isProVersion}">
                <li><span class="emphasis">superuser</span> <spring:message code='LOGIN_SUPERUSER_USER'/></li>
            </c:if>
            <li><span class="emphasis">jasperadmin</span> <spring:message code='LOGIN_ADMIN_USER'/></li>
            <li><span class="emphasis">joeuser/joeuser</span> <spring:message code='LOGIN_JOEUSER'/></li>
            <c:if test="${isProVersion}">
                <li><span class="emphasis">demo/demo</span> <spring:message code='LOGIN_TO_VIEW_DEMO'/></li>
            </c:if>
        </ul>
        <p class="message"><spring:message code='CONTACT_ADMIN'/></p>
    </t:putAttribute>

    <t:putAttribute name="footerContent">
        <button type="submit" class="button action primary up"><span class="wrap"><spring:message code='button.ok'/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>