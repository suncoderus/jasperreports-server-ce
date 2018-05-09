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

<%-- JavaScript which is common to all pages and requires JSTL access --%>

<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove

    //common URL context
    var urlContext = "${pageContext.request.contextPath}";

    <%--default search text --%>
    var defaultSearchText = "<spring:message code='SEARCH_BOX_DEFAULT_TEXT'/>";
    var serverIsNotResponding = "<spring:message code='confirm.slow.server'/>";

    var commonReportGeneratorsMetadata = ${not empty reportGenerators ? reportGenerators : '[]'};
</script>

<%--section for navigation's json action model data - needs its own script tag with id --%>
<script type="text/json" id="navigationActionModel">
    <%=((NavigationActionModelSupport)application.getAttribute("concreteNavigationActionModelSupport")).getClientActionModelDocument("navigation", request)%>
</script>