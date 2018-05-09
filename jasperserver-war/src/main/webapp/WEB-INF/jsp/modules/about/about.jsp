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

<%@ taglib prefix="spring" uri="/spring" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/aboutBox.jsp">
    <t:putAttribute name="containerClass" value="hidden centered_vert centered_horz"/>
    <t:putAttribute name="bodyContent">
        <p class="message"><spring:message code="dialog.aboutBox.productVersion"/>: <span class="emphasis"><spring:message code='JS_VERSION'/></span></p>
        <p class="message"><spring:message code="dialog.aboutBox.build"/>: <span class="emphasis"><spring:message code='OS_BUILD_DATE_STAMP'/>_<spring:message code='OS_BUILD_TIME_STAMP'/></span></p>
    </t:putAttribute>
</t:insertTemplate>
