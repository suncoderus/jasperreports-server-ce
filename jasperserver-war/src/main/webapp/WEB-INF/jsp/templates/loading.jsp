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

<%--
Overview:
    Standard Confirm. Provide user an opportunity to cancel an irrevocable action.

Usage:
    <t:insertTemplate template="/WEB-INF/jsp/templates/loading.jsp">
        <t:putAttribute name="containerClass" value="cancellable">
    </t:insertTemplate>
    
    Use cancellable option only when load can be cancelled.
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>



<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="${not empty containerID ? containerID : 'loading'}"/>
    <t:putAttribute name="containerClass">panel dialog loading overlay moveable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerTitle"><spring:message code='jsp.wait'/></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
        <p class="message" role="alert" aria-live="assertive"><spring:message code='jsp.loading'/></p>
        <button id="cancel" class="button action up" aria-label="<spring:message code="button.cancel"/> "><span class="wrap"><spring:message code="button.cancel"/></span><span class="icon"></span></button>
    </t:putAttribute>   
</t:insertTemplate>
