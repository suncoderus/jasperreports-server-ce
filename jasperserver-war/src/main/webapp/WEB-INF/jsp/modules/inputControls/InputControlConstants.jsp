<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

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

<%--Common IC constants--%>
<%@ page import="com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler"%>
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove

    if (typeof ControlsBase === "undefined") {
        ControlsBase = {};
    }
    ControlsBase.NULL_SUBSTITUTION_VALUE = "<%= InputControlHandler.NULL_SUBSTITUTION_VALUE %>";
    ControlsBase.NULL_SUBSTITUTION_LABEL = "<%= InputControlHandler.NULL_SUBSTITUTION_LABEL %>";
    ControlsBase.NOTHING_SUBSTITUTION_VALUE = "<%= InputControlHandler.NOTHING_SUBSTITUTION_VALUE %>";
</script>

<c:if test="${isPro}">
    <script type="text/javascript">
            ControlsBase.DEFAULT_OPTION_TEXT = '<spring:message code="report.options.dialog.default.name" javaScriptEscape="true"/>';
            ControlsBase.CONTROL_DEFAULT_OPTION_TEXT = '<spring:message code="report.options.select.empty.label" javaScriptEscape="true"/>';
            ControlsBase.CONTROL_OPTIONS_TEXT = '<spring:message code="report.options.select.label" javaScriptEscape="true"/>';
    </script>
</c:if>