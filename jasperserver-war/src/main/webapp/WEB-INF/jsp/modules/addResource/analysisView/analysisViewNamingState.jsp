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
        uri: "${wrapper.parentFolder}",
        isEditMode: ${wrapper.editMode},
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}"
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="OlapUnitValidator.error.too.long.olapUnitLabel" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="OlapUnitValidator.error.not.empty.olapUnitLabel" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="OlapUnitValidator.error.too.long.olapUnitName" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="OlapUnitValidator.error.not.empty.olapUnitName" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="OlapUnitValidator.error.invalid.chars.olapUnitName" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="OlapUnitValidator.error.too.long.olapUnitDescription" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addOLAPView === "undefined") {
        __jrsConfigs__.addOLAPView = {};
    }

    __jrsConfigs__.addOLAPView.localContext = localContext;
    __jrsConfigs__.addOLAPView.resource = resource;

</script>
</js:out>