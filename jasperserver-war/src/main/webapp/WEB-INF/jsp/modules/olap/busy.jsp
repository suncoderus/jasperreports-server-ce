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

<%@ page session="true" contentType="text/html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="/spring" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="jsp.olap.busy.title"/></t:putAttribute>
    <t:putAttribute name="headerContent">
        <meta http-equiv="refresh" content="1; URL='${requestSynchronizer.resultURI}'/>">
    </t:putAttribute>
    <t:putAttribute name="bodyID" value="serverError"/>
    <t:putAttribute name="bodyClass" value="oneColumn flow"/>
    <t:putAttribute name="bodyContent">
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			<t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="jsp.olap.busy.title"/></t:putAttribute>
		    <t:putAttribute name="bodyID" value="errorPageContent"/>
		    <t:putAttribute name="bodyContent">
				<div id="stepDisplay">
					<fieldset class="row instructions">
                        <h2 id="interjection" class="textAccent02"><spring:message code="jsp.olap.busy.title"/></h2>
						<h4 id="clarification">
                            <spring:message code="jsp.olap.busy.message" arguments="${requestSynchronizer.resultURI}"/>
						</h4>
                    </fieldset>
				</div>
			</t:putAttribute>
		</t:insertTemplate>
    </t:putAttribute>
</t:insertTemplate>