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
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove
    if (typeof inputControl === "undefined") {
        inputControl = {};
    }

    if (typeof inputControl.messages === "undefined") {
        inputControl.messages = {};
    }

    inputControl.messages['InputControlLocate.Title']= "<spring:message code='resource.InputControlLocate.Title' javaScriptEscape='true'/>"

    if (typeof __jrsConfigs__.addJasperReport === "undefined") {
        __jrsConfigs__.addJasperReport = {};
    }

    __jrsConfigs__.addJasperReport.inputControl = inputControl;

</script>