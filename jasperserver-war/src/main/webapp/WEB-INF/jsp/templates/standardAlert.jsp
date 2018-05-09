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
    <t:insertTemplate template="/WEB-INF/jsp/templates/standardConfirm.jsp">
        <t:putAttribute name="bodyContent">
            
        </t:putAttribute>
    </t:insertTemplate>
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerElements" name="containerElements" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="OKButtonId" name="leftButtonId" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay standardAlert moveable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="standardAlert"/>
    <t:putAttribute name="containerTitle"><spring:message code='dialog.exception.title'/></t:putAttribute>
    <t:putAttribute name="containerElements">${containerElements}</t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
        ${bodyContent}
    </t:putAttribute>   
    <t:putAttribute name="footerContent">    
        <button id="${OKButtonId}" class="button action primary up"><span class="wrap"><spring:message code="button.close"/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
