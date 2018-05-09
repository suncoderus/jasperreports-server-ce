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

    <t:insertTemplate template="/WEB-INF/jsp/templates/reportGeneratorProperties.jsp">
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
    <t:putAttribute name="containerClass">panel dialog overlay moveable sizeable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/> 
    <t:putAttribute name="containerID" value="reportGeneratorProperties"/>
    <t:putAttribute name="containerTitle">${containerTitle}</t:putAttribute>
    <t:putAttribute name="bodyContent">
        <div id="advTreePanel" class="hidden">
            <span class="wrap"><spring:message code="dialog.create.report.select.view"/></span>

            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                <%--<t:putAttribute name="containerID" value="advTreePanel"/>--%>
                <t:putAttribute name="containerClass" value="control groupBox"/>
                <t:putAttribute name="bodyContent">
                    <ul class="responsive collapsible folders hideRoot" id="advLocationTree"></ul>
                </t:putAttribute>
            </t:insertTemplate>
        </div>
        <t:insertTemplate template="/WEB-INF/jsp/templates/generatorSelect.jsp">
            <t:putAttribute name="containerID" value="commonGeneratorSelect"/>
            <t:putAttribute name="containerClass" value="generatorSelect"/>
        </t:insertTemplate>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
        <button id="reportGeneratorPropertiesBtnOk" class="button action primary up"><span class="wrap"><spring:message code="button.ok"/></span><span class="icon"></span></button>
        <button id="reportGeneratorPropertiesBtnCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
