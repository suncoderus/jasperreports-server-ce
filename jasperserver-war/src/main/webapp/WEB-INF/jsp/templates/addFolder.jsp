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

    <t:insertTemplate template="/WEB-INF/jsp/templates/addFolder.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="addFolder" />
    <t:putAttribute name="containerClass">panel dialog overlay addFolder moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerTitle"><spring:message code="dialog.addFolder.title" javaScriptEscape="true"/></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
            <label class="control input text" accesskey="o" for="addFolderInputName" title="<spring:message code="dialog.file.name.title" javaScriptEscape="true"/>">
                <spring:message code="dialog.file.name" javaScriptEscape="true"/> (<spring:message code='required.field' javaScriptEscape="true"/>):
                <input class="" id="addFolderInputName" type="text" value="" tabindex="1"/>
                <span class="message warning">error message here</span>
            </label>
            <label class="control textArea" for="addFolderInputDescription">
                <spring:message code="dialog.file.description" javaScriptEscape="true"/>:
                <textarea id="addFolderInputDescription" type="text" tabindex="2"/></textarea>
                <span class="message warning">error message here</span>
            </label>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
            <button id="addFolderBtnAdd" class="button action primary up" tabindex="3"><span class="wrap"><spring:message code="dialog.addFolder.add" javaScriptEscape="true"/></span><span class="icon"></span></button>
            <button id="addFolderBtnCancel" class="button action up" tabindex="4"><span class="wrap"><spring:message code="dialog.file.cancel" javaScriptEscape="true"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
