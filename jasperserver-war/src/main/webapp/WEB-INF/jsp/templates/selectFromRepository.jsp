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
    Usage:permit user to add a file to the repository

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/selectFromRepository.jsp">
    </t:insertTemplate>

--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:useAttribute id="containerTitle" name="containerTitle" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog selectFromRepository overlay moveable sizeable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/> 
    <t:putAttribute name="containerID" value="selectFromRepository"/>
    <t:putAttribute name="containerTitle">${containerTitle}</t:putAttribute>
    <t:putAttribute name="bodyContent">
                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                    <t:putAttribute name="containerClass" value="control groupBox fillParent"/>
                    <t:putAttribute name="swipeScroll" value="${isIPad}"/>
                    <t:putAttribute name="bodyContent">${bodyContent}</t:putAttribute>
                </t:insertTemplate>               
    </t:putAttribute>
    <t:putAttribute name="footerContent">
        <button id="selectFromRepoBtnSelect" class="button action primary up"><span class="wrap"><spring:message code="button.select"/></span><span class="icon"></span></button>
        <button id="selectFromRepoBtnCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
