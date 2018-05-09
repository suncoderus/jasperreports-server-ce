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
    Displays editable and non-editable object properties.

Usage:
    <t:insertTemplate template="/WEB-INF/jsp/templates/propertiesResource.jsp">
        <t:putAttribute name="containerClass">[OPTIONAL]</t:putAttribute>
    </t:insertTemplate>
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="/spring" prefix="spring"%>


<t:useAttribute name="containerClass" id="containerClass" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="propertiesResource" />
    <t:putAttribute name="containerClass">panel dialog overlay moveable centered_horz centered_vert properties resource ${containerClass}</t:putAttribute>    
    <t:putAttribute name="headerClass" value="mover"/> 
    <t:putAttribute name="containerTitle"><spring:message code="DIALOG_PROPERTIES_RESOURCE_TITLE"/></t:putAttribute>
    <t:putAttribute name="bodyContent">
        <fieldset>
            <legend class="offLeft"><span><spring:message code="DIALOG_PROPERTIES_RESOURCE_COLUMN_PRIMARY_LEGEND"/></span></legend>
            <label class="control input text" accesskey="o" for="displayName" title="<spring:message code='resource.visiblename.tooltip'/>">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_DISPLAY_NAME"/>:</span>
                <input id="displayName" name="displayName" type="text" value="Accounts Report"/>
                <span class="message warning">error message here</span>
            </label>
            <label class="control textArea" for="description">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_DESCRIPTION"/>:</span>
                <textarea id="description" name="description" type="text"/>All Accounts Report</textarea>
                <span class="message warning">error message here</span>
            </label>
            <label class="control input text" for="path">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_PATH"/>:</span>
                <input id="path" type="text" name="path" value="/" readonly="readonly"/>
                <span class="message warning">error message here</span>
            </label>

            <legend class="offLeft"><span><spring:message code="DIALOG_PROPERTIES_RESOURCE_COLUMN_SECONDARY_LEGEND"/></span></legend>
            <label class="control input text" accesskey="o" for="resourceID">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_RESOURCE_ID"/>:</span>
                <div id="resourceID" name="resourceID" class="resourceLabelID"></div>
                <!--input id="resourceID" name="resourceID" type="text" readonly="readonly" value="AllAccounts"/-->
                <span class="message warning">error message here</span>
            </label>
            <label class="control input text" accesskey="o" for="type">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_TYPE"/>:</span>
                <input id="type" name="type" type="text" readonly="readonly" value="Report"/>
                <span class="message warning">error message here</span>
            </label>
            <label class="control input text" accesskey="o" for="createdDate">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_CREATED_DATE"/>:</span>
                <input id="createdDate" name="createdDate" type="text" readonly="readonly" value="3/11/10 3:55 PM"/>
                <span class="message warning">error message here</span>
            </label>
            <label class="control input text" accesskey="o" for="userAccess">
                <span class="wrap"><spring:message code="DIALOG_PROPERTIES_RESOURCE_USER_ACCESS"/>:</span>
                <input id="userAccess" name="userAccess" type="text" readonly="readonly" value="Modify, Delete"/>
                <span class="message warning">error message here</span>
            </label>
        </fieldset>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
    	<button class="button action primary up submit"><span class="wrap"><spring:message code="button.submit"/><span class="icon"></span></button>
        <button class="button action up cancel"><span class="wrap"><spring:message code="button.cancel"/><span class="icon"></span></button>
        <button class="button action primary up ok"><span class="wrap"><spring:message code="button.ok"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
