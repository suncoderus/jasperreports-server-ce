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
    Usage: dialog for selecting repository resource

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/repositorySelection.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="okButtonLabel" name="okButtonLabel" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay repositorySelectionDialog sizeable moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="repositorySelectionDialog" />
    <t:putAttribute name="containerTitle"><spring:message code="dialog.createReport.title"/></t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
			 <span class="wrap"><spring:message code="dialog.create.report.select.view"/></span>
            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="control groupBox"/>
                    <t:putAttribute name="bodyContent">
                        <ul class="responsive collapsible repositoryTree hideRoot"></ul>
                    </t:putAttribute>
            </t:insertTemplate>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
         <button class="button action primary up okButton"><span class="wrap">${okButtonLabel}</span><span class="icon"></span></button>
         <button class="button action up cancelButton"><span class="wrap"><spring:message code="button.cancel"/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
