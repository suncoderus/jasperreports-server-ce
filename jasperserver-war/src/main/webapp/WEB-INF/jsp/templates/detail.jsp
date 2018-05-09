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
    shows information about JasperServer

Usage:
    <t:insertTemplate template="/WEB-INF/jsp/templates/detail.jsp">
        <t:putAttribute name="containerClass"></t:putAttribute>
        <t:putAttribute name="bodyContent"></t:putAttribute>
    </t:insertTemplate>

--%>

<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>


<t:useAttribute name="containerClass" id="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="bodyContent" id="bodyContent" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="containerID" id="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="containerTitle" id="containerTitle" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay detail centered_horz centered_vert moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="${not empty containerID ? containerID : 'detail'}"/>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="containerTitle">
        <c:if test="${not empty containerTitle}">${containerTitle}</c:if>
        <c:if test="${empty containerTitle}"><spring:message code="dialog.detail.title" javaScriptEscape="true"/></c:if>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" cascade="true">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="control groupBox"/>
            <t:putAttribute name="containerID" value="groupBox"/>
            <t:putAttribute name="bodyContent">${bodyContent}</t:putAttribute>
        </t:insertTemplate>
    </t:putAttribute>
        <t:putAttribute name="footerContent">
         <button id="close" class="button action primary up"><span class="wrap"><spring:message code="dialog.aboutBox.close" javaScriptEscape="true"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
