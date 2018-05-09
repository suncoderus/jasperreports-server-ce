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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">
    //${sessionScope.XSS_NONCE} do not remove

    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        isEditMode: ${connectionWrapper.editMode},
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}"
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="OlapClientConnectionValidator.error.too.long.connectionLabel" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="OlapClientConnectionValidator.error.not.empty.connectionLabel" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="OlapClientConnectionValidator.error.too.long.connectionName" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="OlapClientConnectionValidator.error.not.empty.connectionName" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="OlapClientConnectionValidator.error.invalid.chars.connectionName" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="OlapClientConnectionValidator.error.too.long.connectionDescription" javaScriptEscape="true"/>';

    resource.messages["connectionStateFailed"] = '<spring:message code="resource.analysisConnection.connectionState.failed" javaScriptEscape="true"/>';
    resource.messages["connectionStatePassed"] = '<spring:message code="resource.analysisConnection.connectionState.passed" javaScriptEscape="true"/>';

    resource.messages["catalogIsEmpty"] = '<spring:message code="OlapClientConnectionValidator.error.not.empty.xmlaCatalog" javaScriptEscape="true"/>';
    resource.messages["dataSourceIsEmpty"] = '<spring:message code="OlapClientConnectionValidator.error.not.empty.xmlaDatasource" javaScriptEscape="true"/>';
    resource.messages["uriIsEmpty"] = '<spring:message code="OlapClientConnectionValidator.error.not.empty.xmlaConnectionUri" javaScriptEscape="true"/>';
    resource.messages["resource.SaveToFolder.Title"] = '<spring:message code="resource.SaveToFolder.Title" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.connectionType === "undefined") {
        __jrsConfigs__.connectionType = {};
    }

    __jrsConfigs__.connectionType.localContext = localContext;
    __jrsConfigs__.connectionType.resource = resource;

</script>
</js:out>