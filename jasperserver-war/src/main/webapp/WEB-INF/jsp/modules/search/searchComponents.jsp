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

<t:insertTemplate template="/WEB-INF/jsp/templates/addFolder.jsp">
    <t:putAttribute name="containerClass" value="centered_vert centered_horz hidden"/>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/generateResource.jsp">
     <t:putAttribute name="containerClass" value="hidden centered_vert centered_horz"/>
     <t:putAttribute name="bodyContent" >
        <ul class="responsive collapsible folders hideRoot" id="generateResourceFoldersTree"></ul>
     </t:putAttribute>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/uploadTheme.jsp">
    <t:putAttribute name="containerClass" value="centered_vert centered_horz hidden"/>
</t:insertTemplate>

<t:insertTemplate template="/WEB-INF/jsp/templates/dependencies.jsp">
    <t:putAttribute name="containerClass" value="hidden centered_vert centered_horz"/>

    <t:putAttribute name="bodyContent">
        <ul id="dependenciesList">
        </ul>
    </t:putAttribute>
</t:insertTemplate>

<div id="dndResourceTemplate" >
    <div class="dragging hidden">
    </div>
</div>

<div class="hidden">
    <t:insertTemplate template="/WEB-INF/jsp/templates/propertiesResource.jsp">
        <t:putAttribute name="containerClass">centered_vert centered_horz hidden</t:putAttribute>
    </t:insertTemplate>
</div>
<div class="hidden">
    <t:insertTemplate template="/WEB-INF/jsp/templates/permissions.jsp">
        <t:putAttribute name="containerClass">centered_vert centered_horz hidden</t:putAttribute>
        <t:putAttribute name="bodyContent">
            <ul id="permissionsList"></ul>
        </t:putAttribute>
    </t:insertTemplate>
</div>

<div class="hidden">
    <form id="redirectForm" action="flow.html" method="post" onsubmit="return false;" style="display:none;">
        <input id="_flowExecutionKey" name="_flowExecutionKey" value="${flowExecutionKey}" type="text" />
        <input id="_eventId" name="_eventId" value="redirect" type="text" />
        <input id="flowParams" name="flowParams" value="" type="text" />
    </form>
</div>
