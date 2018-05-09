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
    <t:insertTemplate template="/WEB-INF/jsp/templates/aboutBox.jsp">
        <t:putAttribute name="containerClass"></t:putAttribute>
        <t:putAttribute name="bodyContent"></t:putAttribute>
    </t:insertTemplate>

--%>

<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>


<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="containerClass" id="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="bodyContent" id="bodyContent" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog aboutBox overlay moveable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID">${containerID != null ? containerID : "aboutBox"}</t:putAttribute>
    <t:putAttribute name="containerTitle"><spring:message code='dialog.aboutBox.title'/></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent" cascade="true">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="control groupBox fillParent"/>
            <t:putAttribute name="bodyContent">${bodyContent}</t:putAttribute>
        </t:insertTemplate>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
        <button class="button action primary up"><span class="wrap"><spring:message code='dialog.aboutBox.close'/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
