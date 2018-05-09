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

<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        isEditMode: ${dataType.editMode},
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}"
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="DataTypeValidator.error.too.long.dataType.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="DataTypeValidator.error.not.empty.dataType.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="DataTypeValidator.error.too.long.dataType.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="DataTypeValidator.error.not.empty.dataType.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="DataTypeValidator.error.invalid.chars.dataType.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="DataTypeValidator.error.too.long.dataType.description" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addDataType === "undefined") {
        __jrsConfigs__.addDataType = {};
    }

    __jrsConfigs__.addDataType.type = ${dataType.dataType.dataTypeType};
    __jrsConfigs__.addDataType.localContext = localContext;
    __jrsConfigs__.addDataType.resource = resource;
</script>
</js:out>