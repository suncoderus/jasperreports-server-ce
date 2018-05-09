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

<script type="text/javascript">
    //${sessionScope.XSS_NONCE} do not remove

    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        <js:out javaScriptEscape="true">
        uri: '${control.inputControl.parentFolder}',
        isEditMode: ${control.editMode},
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}"
        </js:out>
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="InputControlsFlowValidator.error.too.long.inputControl.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="InputControlsFlowValidator.error.not.empty.inputControl.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="InputControlsFlowValidator.error.too.long.inputControl.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="InputControlsFlowValidator.error.not.empty.inputControl.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="InputControlsFlowValidator.error.invalid.chars.inputControl.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="InputControlsFlowValidator.error.too.long.inputControl.description" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addInputControl === "undefined") {
        __jrsConfigs__.addInputControl = {};
    }

    __jrsConfigs__.addInputControl.localContext = localContext;
    __jrsConfigs__.addInputControl.resource = resource;

</script>