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

    <t:insertTemplate template="/WEB-INF/jsp/templates/uploadTheme.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog uploadTheme overlay moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>    
    <t:putAttribute name="containerID" value="uploadTheme" />
    <t:putAttribute name="containerTitle"><spring:message code='dialog.uploadTheme.title'/></t:putAttribute>
    <t:putAttribute name="bodyContent">

            <label class="control input text" for="themeName" title="<spring:message code='dialog.uploadTheme.nameTitle'/>">
                <span class="wrap"><spring:message code='dialog.uploadTheme.nameLabel'/> (<spring:message code='required.field' javaScriptEscape="true"/>):</span>
                <input class="" id="themeName" type="text" value=""/>
                <span class="message warning">error message here</span>
            </label>
            <label class="control input file" for="themeZip" title="<spring:message code='dialog.uploadTheme.zipTitle'/>">
				<span class="wrap"><spring:message code='dialog.uploadTheme.zipLabel'/></span>
				<input class="" id="themeZip" name="themeZip" type="file" value=""/>
				<span class="message warning">error message here</span>
			</label>

    </t:putAttribute>
        <t:putAttribute name="footerContent">
         <button id="uploadThemeBtnUpload" class="button action primary up"><span class="wrap"><spring:message code="button.upload"/><span class="icon"></span></button>
         <button id="uploadThemeBtnCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
