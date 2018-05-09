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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">

    //${sessionScope.XSS_NONCE} do not remove

    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        isEditMode: ${dataResource.editMode},
        type: "${type}",
        parentFolderUri: "${parentFolder}",
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}",
        dependentResources: JSON.parse('${not empty dataResource.namedProperties.dependentResources ? dataResource.namedProperties.dependentResources : "[]" }'),
        jdbcDrivers: JSON.parse('${jdbcDriversJSON}'),
        dynamicUrlPartPattern: JSON.parse('${dynamicUrlPartPattern}'),
        validationPatterns: JSON.parse('${validationPatterns}')
    };

    localContext.flowExecutionKey = "${flowExecutionKey}";

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="ReportDataSourceValidator.error.too.long.reportDataSource.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="ReportDataSourceValidator.error.too.long.reportDataSource.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.reportDataSource.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="ReportDataSourceValidator.error.too.long.reportDataSource.description" javaScriptEscape="true"/>';
    resource.messages["wordInvalidChars"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.wordCharsOnly" javaScriptEscape="true"/>';
    resource.messages["shouldStartWithLetter"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.shouldStartWithLetter" javaScriptEscape="true"/>';

    resource.messages["driverIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.driverClass" javaScriptEscape="true"/>';
    resource.messages["driverNotInstalled"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.driverNotInstalled" javaScriptEscape="true"/>';
    resource.messages["urlIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.connectionUrl" javaScriptEscape="true"/>';
    resource.messages["dbNameIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.dbNameIsEmpty" javaScriptEscape="true"/>';
    resource.messages["parentFolderIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.parentFolderIsEmpty" javaScriptEscape="true"/>';
    resource.messages["userNameIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.username" javaScriptEscape="true"/>';
    resource.messages["serviceIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.jndiName" javaScriptEscape="true"/>';
    resource.messages["beanNameIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.beanName" javaScriptEscape="true"/>';
    resource.messages["beanMethodIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.beanMethod" javaScriptEscape="true"/>';

    resource.messages["awsAccessKeyIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.awsAccessKey" javaScriptEscape="true"/>';
    resource.messages["awsSecretKeyIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.awsSecretKey" javaScriptEscape="true"/>';
    resource.messages["regionIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.region" javaScriptEscape="true"/>';

    resource.messages["driverHasInvalidChar"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.reportDataSource.driverClass" javaScriptEscape="true"/>';
    resource.messages["urlHasInvalidChar"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.reportDataSource.connectionUrl" javaScriptEscape="true"/>';

    resource.messages["resource.Add.Files.Title"]='<spring:message code="resource.Add.Files.Title" javaScriptEscape="true"/>';

    resource.messages["subDatasourcesNeeded"] = '<spring:message code="ReportDataSourceValidator.error.sub.datasources.needed" javaScriptEscape="true"/>';
    resource.messages["subDatasourcesIdDuplicates"] = '<spring:message code="ReportDataSourceValidator.error.sub.datasources.id.duplicates" javaScriptEscape="true"/>';
    resource.messages["dependenciesTopMessage"] = '<spring:message code="resource.dataSource.virtual.dependencies.top.message" javaScriptEscape="true"/>';
    resource.messages["dependenciesBottomMessage"] = '<spring:message code="resource.dataSource.virtual.dependencies.bottom.message" javaScriptEscape="true"/>';

    resource.messages["button.upload"] = '<spring:message code="button.upload" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.required.field"] = '<spring:message code="required.field" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.dbHost"] = '<spring:message code="resource.dataSource.jdbc.dbHost" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.dbPort"] = '<spring:message code="resource.dataSource.jdbc.dbPort" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.dbName"] = '<spring:message code="resource.dataSource.jdbc.dbName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.sName"] = '<spring:message code="resource.dataSource.jdbc.sName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.securityToken"] = '<spring:message code="resource.dataSource.jdbc.securityToken" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.serverAddress"] = '<spring:message code="resource.dataSource.jdbc.serverAddress" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.projectId"] = '<spring:message code="resource.dataSource.jdbc.projectId" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.oAuthServiceAcctEmail"] = '<spring:message code="resource.dataSource.jdbc.oAuthServiceAcctEmail" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.oAuthPvtKeyPath"] = '<spring:message code="resource.dataSource.jdbc.oAuthPvtKeyPath" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.stmtCallLimit"] = '<spring:message code="resource.dataSource.jdbc.stmtCallLimit" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.authMech"] = '<spring:message code="resource.dataSource.jdbc.authMech" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.defaultKeySpace"] = '<spring:message code="resource.dataSource.jdbc.defaultKeySpace" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.informixServerName"] = '<spring:message code="resource.dataSource.jdbc.informixServerName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.urlID"] = '<spring:message code="resource.dataSource.jdbc.url" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.schemaName"] = '<spring:message code="resource.dataSource.jdbc.schemaName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.driverType"] = '<spring:message code="resource.dataSource.jdbc.driverType" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.fieldCantBeEmpty"] = '<spring:message code="resource.dataSource.jdbc.fieldCantBeEmpty" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.invalidField"] = '<spring:message code="resource.dataSource.jdbc.invalidField" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.requiredTitle"] = '<spring:message code="resource.dataSource.jdbc.requiredTitle" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.driverMissing"] = '<spring:message code="resource.dataSource.jdbc.driverMissing" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.otherDriver"] = '<spring:message code="resource.dataSource.jdbc.otherDriver" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.driverUploadSuccess"] = '<spring:message code="resource.dataSource.jdbc.upload.driverUploadSuccess" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.driverUploadFail"] = '<spring:message code="resource.dataSource.jdbc.upload.driverUploadFail" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.overwriteWarning"] = '<spring:message code="resource.dataSource.jdbc.upload.overwriteWarning" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.wrongExtension"] = '<spring:message code="resource.dataSource.jdbc.upload.wrongExtension" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.addDriverButton"] = '<spring:message code="resource.dataSource.jdbc.upload.addDriverButton" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.editDriverButton"] = '<spring:message code="resource.dataSource.jdbc.upload.editDriverButton" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addDataSource === "undefined") {
        __jrsConfigs__.addDataSource = {};
    }

    __jrsConfigs__.addDataSource.localContext = localContext;
    __jrsConfigs__.addDataSource.resource = resource;

</script>
</js:out>