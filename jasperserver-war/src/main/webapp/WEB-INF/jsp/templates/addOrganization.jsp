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
    Usage:permit organization to add a system created object to the repository.

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/addOrganization.jsp">
    </t:insertTemplate>

--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay addOrganization moveable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="addOrganization" />
    <t:putAttribute name="containerTitle">
        <spring:message code="MT_ADD_ORG_TITLE" javaScriptEscape="true"/>
    </t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
        <fieldset class="group first">
            <legend class="offLeft"><span><spring:message code="dialog.file.nameAndDescription" javaScriptEscape="true"/></span></legend>
            <label class="control input text" class="required" for="addOrgName" title="<spring:message code="MT_ORG_NAME_TEXT" javaScriptEscape="true"/>">
                <span class="wrap"><spring:message code="MT_ORG_NAME" javaScriptEscape="true"/>  (<spring:message code='required.field'/>):</span>
                <input class="" id="addOrgName" type="text" maxlength="100" value="" />
                <span class="message warning"></span>
            </label>
            <label class="control input text" for="addOrgID" title="<spring:message code="MT_ORG_ID_TEXT" javaScriptEscape="true"/>">
                <span class="wrap"><spring:message code="MT_ORG_ID" javaScriptEscape="true"/>  (<spring:message code='required.field'/>):</span>
                <input class="" id="addOrgID" type="text" maxlength="100" value="" />
                <span class="hint"><spring:message code="MT_ORG_ID_HINT" javaScriptEscape="true"/></span>
                <span class="message warning"></span>
            </label>
            <label class="control input text" class="required" for="addOrgAlias" title="<spring:message code="MT_ORG_ALIAS_TEXT" javaScriptEscape="true"/>">
                <span class="wrap"><spring:message code="MT_ORG_ALIAS" javaScriptEscape="true"/>  (<spring:message code='required.field'/>):</span>
                <input class="" id="addOrgAlias" type="text" maxlength="100" value=""/>
                <span class="message warning"></span>
            </label>
            <label class="control textArea" for="addOrgDesc" title="<spring:message code="MT_DESC_TEXT" javaScriptEscape="true"/>">
                <span class="wrap"><spring:message code="MT_DESCRIPTION" javaScriptEscape="true"/>:</span>
                <textarea id="addOrgDesc" type="text" maxlength="250"/> </textarea>
                <span class="message warning"></span>
            </label>
        </fieldset>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
            <button id="addOrgBtn" class="button action primary up">
                <span class="wrap"><spring:message code="MT_ADD_TO" javaScriptEscape="true"/></span><span class="icon"></span>
            </button>
            <button id="cancelOrgBtn" class="button action up">
                <span class="wrap"><spring:message code="dialog.file.cancel" javaScriptEscape="true"/></span><span class="icon"></span>
            </button>
    </t:putAttribute>
</t:insertTemplate>
