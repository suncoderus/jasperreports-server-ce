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
    Usage:permit user to add a system created object to the repository.

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/manageDataSource.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay manageDataSource moveable sizeable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>    
    <t:putAttribute name="containerID" value="manageDataSource" />
    <t:putAttribute name="containerTitle"><spring:message code="domain.designer.tables.manage.datasource.dialog.title"/></t:putAttribute>
    <t:putAttribute name="bodyContent">
            <label class="control input text" accesskey="o" for="dataSourceAlias" title="<spring:message code="domain.designer.tables.manage.datasource.alias.title"/>">
                <span class="wrap"><spring:message code="dialog.file.name"/> (<spring:message code='required.field'/>):</span>
                <input class="" id="dataSourceAlias" type="text" value=""/>
                <span class="message warning">error message here</span>
            </label>
			
            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            	<t:putAttribute name="containerClass" value="control groupBox"/>
                <t:putAttribute name="bodyContent">${bodyContent}</t:putAttribute>
            </t:insertTemplate>
			
			<a id="selectSchemas" class="launcher"><spring:message code="domain.designer.tables.manage.datasource.selectSchemas"/></a>

    </t:putAttribute>
    <t:putAttribute name="footerContent">
         <button id="ok" class="button action primary up"><span class="wrap"><spring:message code="button.ok"/></span><span class="icon"></span></button>
         <button id="cancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
