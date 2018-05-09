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
<%@ page import="com.jaspersoft.jasperserver.war.dto.StringOption"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>

<%@ include file="../../common/jsEdition.jsp" %>

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
            <c:when test="${dataResource.editMode}"><spring:message code="resource.datasource.jdbc.page.title.edit"/></c:when>
            <c:otherwise><spring:message code="resource.datasource.jdbc.page.title.add"/></c:otherwise>
        </c:choose>
    </t:putAttribute>
    <t:putAttribute name="bodyID" value="addResource_dataSource_JDBC"/>
    <t:putAttribute name="dataSourceType" value="jdbc"/>
    <t:putAttribute name="testAvailable" value="${true}"/>
    <t:putAttribute name="typeSpecificContent">
        <fieldset  class="group shortFields">
            <legend class="offLeft"><span><spring:message code='resource.dataSource.accessProp'/></span></legend>

            <label class="control select" for="driverSelector" title="<spring:message code='resource.dataSource.jdbc.selectDriverTitle'/>">
                <span class="wrap"><spring:message code='resource.dataSource.jdbc.driver'/>:</span>
                <select name="driverSelector" id="driverSelector"></select>
                <c:if test="${isAuthorized and empty tenantId}">
                    <button id="addDriverBtn" type="button" class="button action up">
                    	<span class="wrap"><spring:message code='resource.dataSource.jdbc.upload.addDriverButton'/></span>
                    </button>
                </c:if>
            </label>

            <div id="jdbcFields"></div>

            <div id="jdbcDataSourceFields">
                <spring:bind path="dataResource.reportDataSource.driverClass">
                   <label class="hidden control input text <c:if test="${status.error}"> error </c:if>" class="required" for="driverID"
                           title="<spring:message code='resource.analysisConnection.driver'/>">
                        <span class="wrap"><spring:message code='resource.dataSource.jdbc.driver'/> (<spring:message code='required.field'/>):</span>
                        <input class="" name="${status.expression}" id="driverID" type="text" value="${status.value}"/>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                        <span class="message hint"><spring:message code='resource.dataSource.jdbc.hint1'/></span>
                    </label>
                </spring:bind>

                <spring:bind path="dataResource.reportDataSource.connectionUrl">
                    <label id="jdbcURL" class="control input text <c:if test="${status.error}"> error </c:if>" class="required" for="urlID" title="<spring:message code='resource.analysisConnection.requiredURL'/>">
                        <span class="wrap"><spring:message code='resource.dataSource.jdbc.url'/> (<spring:message code='required.field'/>):</span>
                        <input class="" id="urlID" type="text" name="${status.expression}" value="${status.value}"/>
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                        <span class="message hint"><spring:message code='resource.dataSource.jdbc.hint2'/></span>
                    </label>
                </spring:bind>
            </div>

            <div id="credientialsFields">
	            <spring:bind path="dataResource.reportDataSource.username">
	                <label class="control input text <c:if test="${status.error}"> error </c:if>" class="required" for="userNameID" title="<spring:message code='resource.analysisConnection.userName'/>">
	                    <span class="wrap"><spring:message code='resource.dataSource.jdbc.username'/> (<spring:message code='required.field'/>):</span>
	                    <input class="" id="userNameID" type="text" name="${status.expression}" value="${status.value}"/>
	                <span class="message warning">
	                    <c:if test="${status.error}">${status.errorMessage}</c:if>
	                </span>
	                    <span class="message hint"></span>
	                </label>
	            </spring:bind>
	
	            <spring:bind path="dataResource.reportDataSource.password">
	                <label class="control input password <c:if test="${status.error}"> error </c:if>" class="required" for="passwordID"
	                       title="<spring:message code='resource.analysisConnection.password'/>">
	                    <span class="wrap"><spring:message code='resource.dataSource.jdbc.password'/>:</span>
	                    <input class="" id="passwordID" type="password"  name="${status.expression}" value="${not dataResource.editMode ? null : passwordSubstitution}" />
	                <span class="message warning">
	                    <c:if test="${status.error}">${status.errorMessage}</c:if>
	                </span>
	                    <span class="message hint"></span>
	                </label>
	            </spring:bind>
			</div>
			<div id="timezoneField">
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
            </div>
        </fieldset>

        <script id="jdbcFieldTemplate" type="template/mustache">
            <label class="control input text {{#error}} error {{/error}} required" for="{{name}}" title="{{title}}">
                <span class="wrap">{{label}}</span>
                <input id="{{name}}" type="text" value=""/>
                <span class="message warning"></span>
                <span class="message hint"></span>
            </label>
        </script>

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
