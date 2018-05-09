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
    // ${sessionScope.XSS_NONCE} do not remove
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        <js:out javaScriptEscape="true">
        isEditMode: ${listOfValuesDTO.editMode},
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}"
        </js:out>
    };


    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="ListOfValuesValidator.error.too.long.listOfValues.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="ListOfValuesValidator.error.not.empty.listOfValues.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="ListOfValuesValidator.error.too.long.listOfValues.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="ListOfValuesValidator.error.not.empty.listOfValues.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="ListOfValuesValidator.error.invalid.chars.listOfValues.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="ListOfValuesValidator.error.too.long.listOfValues.description" javaScriptEscape="true"/>';
    resource.messages["itemNameIsEmpty"] = '<spring:message code="ListOfValuesValidator.error.not.empty.newLabel" javaScriptEscape="true"/>';
    resource.messages["itemValueIsEmpty"] = '<spring:message code="ListOfValuesValidator.error.not.empty.newValue" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addListOfValues === "undefined") {
        __jrsConfigs__.addListOfValues = {};
    }

    __jrsConfigs__.addListOfValues.localContext = localContext;
    __jrsConfigs__.addListOfValues.resource = resource;
</script>