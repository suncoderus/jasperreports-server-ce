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

<%@ page language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/spring" prefix="spring" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle">
        <spring:message code='jsp.accessDenied.title'/>
    </t:putAttribute>
    <t:putAttribute name="bodyID" value="accessDenied"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
    <t:putAttribute name="bodyClass" value="oneColumn"/>

		<t:putAttribute name="bodyContent" >

			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    	<t:putAttribute name="containerClass" value="column decorated primary"/>
			    <t:putAttribute name="containerTitle">
                    <spring:message code='jsp.accessDenied.title'/>
			    </t:putAttribute>
			    <t:putAttribute name="bodyContent">

					<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
						<t:putAttribute name="containerClass" value="panel info nothingToDisplay centered_vert centered_horz"/>
					    <t:putAttribute name="bodyContent">
                            <p class="message"><spring:message code="jsexception.unable.to.complete.request"/></p>
					    </t:putAttribute>
					</t:insertTemplate>

			    </t:putAttribute>
			</t:insertTemplate>

	    </t:putAttribute>

</t:insertTemplate>
