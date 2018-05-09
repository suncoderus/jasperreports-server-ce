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

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="/spring" prefix="spring"%>

<jsp:include page="setScriptOptimizationProps.jsp"/>

<script type="text/javascript" src="${scriptsUri}/bower_components/requirejs/require.js"></script>
<script type="text/javascript" src="${scriptsUri}/require.config.js"></script>
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove

    require.config({
        baseUrl: "${scriptsUri}"
    });
</script>

<%-- Preload  jQuery since some plugins assume it's global --%>
<script type="text/javascript" src="${scriptsUri}/bower_components/jquery/dist/jquery.js"></script>
<%-- Load CSRF protection asap, right after laoding jquery. --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/runtime/${jsOptimizationProperties.runtimeHash}/JavaScriptServlet"></script>

<c:if test="${optimizeJavascript == true}">
    <%--
        Prototypejs is excluded from uglifing since it has troubles with uglifying.
        So in case if js optimization is enabled - load it as usual javascript file from not optimized sources.
    --%>
    <script type="text/javascript" src="${notOptimizedScriptsUri}/bower_components/prototype/dist/prototype.js"></script>

    <%-- Hack to skip loading of prototype from optimized folder since it's already loaded--%>
    <script type="text/javascript">
        define("prototype", $);
    </script>
</c:if>

