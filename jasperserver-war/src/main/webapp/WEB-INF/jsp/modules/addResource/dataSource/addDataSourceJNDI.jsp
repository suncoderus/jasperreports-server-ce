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


<t:insertTemplate template="/WEB-INF/jsp/modules/addResource/dataSource/addDataSourceTemplate.jsp">
<t:putAttribute name="pageTitle">
    <c:choose>
        <c:when test="${dataResource.editMode}"><spring:message code="resource.datasource.jndi.page.title.edit"/></c:when>
        <c:otherwise><spring:message code="resource.datasource.jndi.page.title.add"/></c:otherwise>
    </c:choose>
</t:putAttribute>
<t:putAttribute name="bodyID" value="addResource_dataSource_JNDI"/>
<t:putAttribute name="dataSourceType" value="jndi"/>
<t:putAttribute name="testAvailable" value="${true}"/>
<t:putAttribute name="typeSpecificContent">
    <fieldset class="group mediumFields">
        <legend class="offLeft"><span><spring:message
                code='resource.dataSource.accessProp'/></span></legend>
        <spring:bind path="dataResource.reportDataSource.jndiName">
            <label class="control input text <c:if test="${status.error}"> error </c:if>"
                   class="required" for="serviceID"
                   title="<spring:message code='resource.analysisConnection.driver'/>">
                <span class="wrap"><spring:message
                        code='resource.dataSource.jndi.serviceName'/> (<spring:message
                        code='required.field'/>):</span>
                <input class="" id="serviceID" type="text" name="${status.expression}"
                       value="${status.value}"/>
                <span class="message warning">
                    <c:if test="${status.error}">${status.errorMessage}</c:if>
                </span>
                <span class="message hint"><spring:message
                        code='resource.dataSource.jndi.hint1'/></span>
            </label>
        </spring:bind>

        <spring:bind path="dataResource.reportDataSource.timezone">
            <label class="control select" for="timeZone" title="<spring:message code='resource.analysisConnection.timeZone'/>">
                <span class="wrap"><spring:message
                        code='resource.dataSource.timezone'/></span>
                <select name="${status.expression}" class="fnormal timeZone" id="timeZone">
                    <option value=""
                            <c:if test="${selectedTimezone == null}">selected</c:if>>
                        <spring:message
                                code="jsp.jdbcPropsForm.timezone.default.option"/></option>
                    <c:forEach var="timezone" items="${timezones}">
                        <option value="${timezone.code}"
                                <c:if test="${selectedTimezone == timezone.code}">selected</c:if>>
                            <spring:message code="timezone.option"
                                            arguments="${timezone.code},${timezone.description}" />
                        </option>
                    </c:forEach>
                </select>
                <span class="message warning">${status.errorMessage}</span>
                <span class="message hint"><spring:message
                        code='resource.dataSource.hint3'/></span>
            </label>
        </spring:bind>
    </fieldset>
    </t:putAttribute>
</t:insertTemplate>
