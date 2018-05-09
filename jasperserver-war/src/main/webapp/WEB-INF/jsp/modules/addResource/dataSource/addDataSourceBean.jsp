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
        <c:when test="${dataResource.editMode}"><spring:message code="resource.datasource.bean.page.title.edit"/></c:when>
        <c:otherwise><spring:message code="resource.datasource.bean.page.title.add"/></c:otherwise>
    </c:choose>
</t:putAttribute>
<t:putAttribute name="bodyID" value="addResource_dataSource_Bean"/>
<t:putAttribute name="dataSourceType" value="bean"/>
<t:putAttribute name="testAvailable" value="${true}"/>
<t:putAttribute name="typeSpecificContent">
        <fieldset class="group shortFields">
            <legend class="offLeft"><span><spring:message code='resource.dataSource.accessProp'/></span></legend>
            <spring:bind path="dataResource.reportDataSource.beanName">
                <label class="control input text <c:if test="${status.error}"> error </c:if>" class="required" for="beanNameID"
                       title="<spring:message code='resource.analysisConnection.driver'/>">
                    <span class="wrap"><spring:message code='resource.dataSource.bean.name'/> (<spring:message code='required.field'/>):</span>
                    <input class="" id="beanNameID" type="text" name="${status.expression}" value="${status.value}" />
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                    <span class="message hint"><spring:message code='resource.dataSource.bean.hint1'/></span>
                </label>
            </spring:bind>

            <spring:bind path="dataResource.reportDataSource.beanMethod">
                <label class="control input text <c:if test="${status.error}"> error </c:if>" class="required" for="beanMethodID"
                       title="<spring:message code='resource.analysisConnection.driver'/>">
                    <span class="wrap"><spring:message code='resource.dataSource.bean.method'/> (<spring:message code='required.field'/>):</span>
                    <input class="" id="beanMethodID" type="text" name="${status.expression}" value="${status.value}" />
                    <span class="message warning">
                        <c:if test="${status.error}">${status.errorMessage}</c:if>
                    </span>
                    <span class="message hint"><spring:message code='resource.dataSource.bean.hint2'/></span>
                </label>
            </spring:bind>

        </fieldset>
    </t:putAttribute>
</t:insertTemplate>
