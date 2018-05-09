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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="isAuthorized" value="false"/>
<c:choose>
    <c:when test="${isProVersion}">
        <authz:authorize ifAllGranted="ROLE_SUPERUSER">
            <c:set var="isAuthorized" value="true"/>
        </authz:authorize>
    </c:when>
    <c:otherwise>
        <authz:authorize ifAllGranted="ROLE_ADMINISTRATOR">
            <c:set var="isAuthorized" value="true"/>
        </authz:authorize>
    </c:otherwise>
</c:choose>

<t:insertTemplate template="/WEB-INF/jsp/modules/addResource/dataSource/addDataSourceTemplate.jsp">
<t:putAttribute name="pageTitle">
    <c:choose>
        <c:when test="${dataResource.editMode}"><spring:message code="resource.datasource.jndi.page.title.edit"/></c:when>
        <c:otherwise><spring:message code="resource.datasource.jndi.page.title.add"/></c:otherwise>
    </c:choose>
</t:putAttribute>
<t:putAttribute name="bodyID" value="addResource_dataSource_aws"/>
<t:putAttribute name="dataSourceType" value="aws"/>
<t:putAttribute name="testAvailable" value="${true}"/>
<t:putAttribute name="typeSpecificScripts">
</t:putAttribute>
<t:putAttribute name="typeSpecificContent">
    <fieldset class="group mediumFields">
        <spring:bind path="dataResource.reportDataSource.timezone">
            <label class="control select" for="timeZone" title="<spring:message code='resource.analysisConnection.timeZone'/>">
                <span class="wrap"><spring:message code='resource.dataSource.timezone'/></span>
                <select name="${status.expression}" class="fnormal timeZone" id="">
                    <option value="" <c:if test="${selectedTimezone == null}">selected</c:if>><spring:message code="jsp.jdbcPropsForm.timezone.default.option"/></option>
                    <c:forEach var="timezone" items="${timezones}">
                        <option value="${timezone.code}"
                                <c:if test="${selectedTimezone == timezone.code}">selected</c:if>>
                            <spring:message code="timezone.option"
                                            arguments="${timezone.code},${timezone.description}" />
                        </option>
                    </c:forEach>
                </select>
                <span class="message warning">${status.errorMessage}</span>
                <span class="message hint"><spring:message code='resource.dataSource.hint3'/></span>
            </label>
        </spring:bind>
    </fieldset>
</t:putAttribute>
<t:putAttribute name="typeSpecificContentAfterFolder">
    <fieldset class="group">
        <legend><span><spring:message code="resource.dataSource.aws.settings.title"/></span></legend>

        <c:set  var="disableAwsDefaults" value="${not isEc2Instance or suppressEc2CredentialsWarnings}" />
        <c:set var="showUserDefinedSettings" value="${not empty dataResource.reportDataSource.AWSAccessKey or disableAwsDefaults}" />

        <ul class="list inputSet">
            <li class="leaf">
                <div class="control radio">
                    <label class="wrap" for="awsUseDefault" title="<spring:message code="resource.dataSource.aws.option.useDefault.title"/>">
                        <spring:message code="resource.dataSource.aws.option.useDefault"/>
                    </label>
                    <input id="awsUseDefault" type="radio" name="awsSettings" value="0" ${showUserDefinedSettings ? "" : "checked"} ${disableAwsDefaults ? "disabled='disabled'" : ''} >
                </div>
            </li>
            <li class="leaf">
                <div class="control radio">
                    <label class="wrap" for="awsUserDefined" title="<spring:message code="resource.dataSource.aws.option.userDefined.title"/>">
                        <spring:message code="resource.dataSource.aws.option.url" var="awsUrl" />
                        <spring:message code="resource.dataSource.aws.option.userDefined" arguments="${awsUrl}" />
                    </label>
                    <input id="awsUserDefined" type="radio" name="awsSettings" value="1" ${showUserDefinedSettings ? "checked" : ""}>
                </div>
            </li>
        </ul>
        <fieldset class="group shortFields" id="aws_settings" >
            <input type="hidden" name="datasourceUri" id="datasourceUri" value="${not dataResource.editMode ? "" : dataResource.reportDataSource.URIString}" />

            <spring:bind path="dataResource.reportDataSource.aWSAccessKey">
                <label class="control input text ${status.error ? 'error': ''}" for="awsAccessKey" title="<spring:message code="resource.dataSource.aws.setting.accessKey.title"/>">
                    <span class="wrap" disabled="disabled"><spring:message code="resource.dataSource.aws.setting.accessKey"/>:</span>
                    <input name="${status.expression}" id="awsAccessKey" type="text" value="${status.value}" ${showUserDefinedSettings ? "" : "readonly='true'"}>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                </label>
            </spring:bind>
            <spring:bind path="dataResource.reportDataSource.aWSSecretKey">

                <label class="control input password ${status.error ? 'error': ''}" for="awsSecretKey" title="<spring:message code="resource.dataSource.aws.setting.secretKey.title"/>">
                    <span class="wrap" disabled="disabled"><spring:message code="resource.dataSource.aws.setting.secretKey"/>:</span>
                    <input name="${status.expression}" id="awsSecretKey" type="password" value="${not dataResource.editMode ? null : passwordSubstitution}" ${showUserDefinedSettings ? "" : "readonly='true'"} autocomplete="off">
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                </label>
            </spring:bind>
            <spring:bind path="dataResource.reportDataSource.roleARN">
                <label class="control input text ${status.error ? 'error': ''}" for="arn" title="<spring:message code="resource.dataSource.aws.setting.arn.title" />">
                    <span class="wrap" disabled="disabled"><spring:message code="resource.dataSource.aws.setting.arn" />:</span>
                    <input name="${status.expression}" id="arn" type="text" value="${status.value}" ${showUserDefinedSettings ? "" : "readonly='true'"}>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                    <span class="message hint"><spring:message code="resource.dataSource.aws.optional.arn" /></span>
                </label>
            </spring:bind>
        </fieldset>
    </fieldset>

    <fieldset class="group" id="aws_region">
        <legend><span><spring:message code="resource.dataSource.aws.tree"/></span></legend>
            <spring:bind path="dataResource.reportDataSource.aWSRegion">
                <label class="control select ${status.error ? 'error': ''}" for="region" title="<spring:message code="resource.dataSource.aws.setting.region.title" />">
                    <span class="wrap"><spring:message code="resource.dataSource.aws.setting.region" />:</span>
                    <select name="${status.expression}" class="fnormal" id="region">
                        <c:forEach var="region" items="${awsRegions}">
                            <option value="${region}"
                                    <c:if test="${status.value == region}">selected</c:if>>
                                <spring:message code="${region}" />
                            </option>
                        </c:forEach>
                    </select>
                    <span class="message warning">${status.errorMessage}</span>
                </label>
            </spring:bind>
        <button id="refresh_tree_button" class="button action"><span class="wrap">
            <spring:message code="resource.dataSource.aws.updateTree"/>
            <span class="icon"></span></span>
        </button>
    </fieldset>
    <fieldset class="group" id="aws_dataSourceList">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="control groupBox"/>
            <t:putAttribute name="bodyContent">
                <ul id="awsDataSourceTree"></ul>
            </t:putAttribute>
        </t:insertTemplate>
        
    </fieldset>
    <fieldset class="group shortFields">
        <spring:bind path="dataResource.reportDataSource.username">
            <label class="control input text required<c:if test="${status.error}"> error </c:if>" for="userNameID" title="<spring:message code='resource.analysisConnection.userName'/>">
                <span class="wrap"><spring:message code='resource.dataSource.jdbc.username'/> (<spring:message code='required.field'/>):</span>
                <input id="userNameID" type="text" name="${status.expression}" value="${status.value}"/>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                <span class="message hint"></span>
            </label>
        </spring:bind>

        <spring:bind path="dataResource.reportDataSource.password">
            <label class="control input password required<c:if test="${status.error}"> error </c:if>" for="passwordID"
                   title="<spring:message code='resource.analysisConnection.password'/>">
                <span class="wrap"><spring:message code='resource.dataSource.jdbc.password'/>:</span>
                <input id="passwordID" type="password"  name="${status.expression}" value="${not dataResource.editMode ? null : passwordSubstitution}" />
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                <span class="message hint"></span>
            </label>
        </spring:bind>

        <spring:bind path="dataResource.reportDataSource.dbName">
            <label class="control input text required<c:if test="${status.error}"> error </c:if>" for="dbName"
                   title="<spring:message code='resource.dataSource.aws.database.name.title'/>">
                <span class="wrap"><spring:message code='resource.dataSource.aws.database.name'/> (<spring:message code='required.field'/>):</span>
                <input name="${status.expression}" id="dbName" type="text" value="${status.value}"/>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
            </label>
        </spring:bind>
    </fieldset>
    <fieldset class="group">
        <spring:bind path="dataResource.reportDataSource.driverClass">
            <label class="control input text required<c:if test="${status.error}"> error </c:if>" for="driverID"
                   title="<spring:message code='resource.analysisConnection.driver'/>">
                <span class="wrap"><spring:message code='resource.dataSource.jdbc.driver'/> (<spring:message code='required.field'/>):</span>
                <input name="${status.expression}" id="driverID" type="text" value="${status.value}"/>
                <c:if test="${isAuthorized and empty tenantId}">
                    <button id="addDriverBtn" type="button" class="button action up">
                        <span class="wrap"><spring:message code='resource.dataSource.jdbc.upload.addDriverButton'/></span>
                    </button>
                </c:if>
                <span class="message hint" style="margin-top:0"><spring:message code='resource.dataSource.jdbc.hint1'/></span>
                <span class="message warning">
                    <c:if test="${status.error}">${status.errorMessage}</c:if>
                </span>
            </label>
        </spring:bind>

        <spring:bind path="dataResource.reportDataSource.connectionUrl">
            <label class="control input text required<c:if test="${status.error}"> error </c:if>" for="urlID" title="<spring:message code='resource.analysisConnection.requiredURL'/>">
                <span class="wrap"><spring:message code='resource.dataSource.jdbc.url'/> (<spring:message code='required.field'/>):</span>
                <input id="urlID" type="text" name="${status.expression}" value="${status.value}"/>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                <span class="message hint"><spring:message code='resource.dataSource.jdbc.hint2'/></span>
            </label>
        </spring:bind>

        <spring:bind path="dataResource.reportDataSource.dbInstanceIdentifier">
            <input type="hidden" id="dbInstanceIdentifier" name="${status.expression}" value="${status.value}" />
        </spring:bind>
        <spring:bind path="dataResource.reportDataSource.dbService">
            <input type="hidden" id="dbService" name="${status.expression}" value="${status.value}" />
        </spring:bind>
    </fieldset>

    <script id="fileUploadTemplate" type="template/mustache">
        <li class="leaf">
            <input class="" id="{{fileId}}" type="file" name="{{fileId}}" value="" size="60" accept="jar/jar" />
            <span class="message warning">error message here</span>
        </li>
    </script>

    <script id="uploadDriverDialogTemplate" type="text/mustache">
        <t:insertTemplate template="/WEB-INF/jsp/templates/select.jsp">
            <t:putAttribute name="containerClass">hidden</t:putAttribute>
            <t:putAttribute name="containerTitle"><spring:message code='resource.dataSource.jdbc.selectDriverTitle'/></t:putAttribute>
            <t:putAttribute name="bodyContent">
                <ul id="fileInputsList" class="" title="Locate local file"></ul>
                <div id="errorMessage" class="">
                    <span class="message warning">error message here</span>
                </div>
                <div id="warningMessage" class="hidden">
                    <span class="message textAccent">error message here</span>
                </div>
            </t:putAttribute>
        </t:insertTemplate>
    </script>

</t:putAttribute>
</t:insertTemplate>
