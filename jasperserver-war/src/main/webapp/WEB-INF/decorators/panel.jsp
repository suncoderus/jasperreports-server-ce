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
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>

<p>
	<!-- ${sessionScope.XSS_NONCE} do not remove -->

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<th class="panelTitle">
				<decorator:title default="<spring:message code='decorators.panel.Unknown_panel'/>" />
			</th>
		</tr>
		<tr>
			<td class="panelBody">
				<decorator:body />
			</td>
		</tr>
	</table>
</p>


