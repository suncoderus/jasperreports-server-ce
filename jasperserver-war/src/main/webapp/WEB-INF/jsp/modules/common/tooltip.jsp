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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="jsTooltip"/>
    <t:putAttribute name="containerClass" value="panel info tooltip hidden"/>
    <t:putAttribute name="bodyContent">
        <p class="message"></p>
    </t:putAttribute>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="orgTooltip"/>
    <t:putAttribute name="containerClass" value="panel info tooltip hidden"/>
    <t:putAttribute name="bodyContent">
        <p class="message label"><spring:message code="tooltip.organization"/></p>
        <p class="message"></p>
    </t:putAttribute>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="fullNameTooltip"/>
    <t:putAttribute name="containerClass" value="panel info tooltip hidden"/>
    <t:putAttribute name="bodyContent">
        <p class="message label"><spring:message code="tooltip.fullName"/></p>
        <p class="message"></p>
    </t:putAttribute>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="fullNameAndOrgTooltip"/>
    <t:putAttribute name="containerClass" value="panel info tooltip hidden"/>
    <t:putAttribute name="bodyContent">
        <p class="message label"><spring:message code="tooltip.fullName"/></p>
        <p class="message"></p>
        <p class="message label"><spring:message code="tooltip.organization"/></p>
        <p class="message"></p>
    </t:putAttribute>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerID" value="resourceNameTooltip"/>
    <t:putAttribute name="containerClass" value="panel info tooltip hidden"/>
    <t:putAttribute name="bodyContent">
        <p class="message label"><spring:message code="repository.resource.hint.name" javaScriptEscape="true"/>: </p>
        <p class="message"></p>
        <p class="message label"><spring:message code="repository.resource.hint.path" javaScriptEscape="true"/>: </p>
        <p class="message"></p>
    </t:putAttribute>
</t:insertTemplate>
