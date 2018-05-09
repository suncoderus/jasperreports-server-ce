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
        <c:when test="${dataResource.editMode}"><spring:message code="resource.datasource.jdbc.page.title.edit"/></c:when>
        <c:otherwise><spring:message code="resource.datasource.jdbc.page.title.add"/></c:otherwise>
    </c:choose>
</t:putAttribute>
<t:putAttribute name="bodyID" value="addResource_dataSource_JDBC"/>
<t:putAttribute name="dataSourceType" value="${type}"/>
<t:putAttribute name="testAvailable" value="${testAvailable}"/>
<t:putAttribute name="typeSpecificContent">
        <fieldset class="group shortFields">
            <legend class="offLeft"><span><spring:message
                    code='resource.dataSource.accessProp'/></span></legend>

            <c:forEach var="prop" items="${dataResource.customProperties}">
                <spring:bind path="dataResource.reportDataSource.propertyMap[${prop.name}]">
                    <label class="control input saveLocation text <c:if test="${status.error}"> error </c:if>"
                           class="required" for=""
                           title="">
                        <span class="wrap"><spring:message code="${prop.label}"/>  <c:if
                                test="${prop.mandatory != null}">(<spring:message
                                code='required.field'/>)</c:if></span>
                        <c:choose>
                            <c:when test="${prop.type eq 'boolean'}">
                                <input type="hidden" name="_${status.expression}"
                                       value="visible"/>
                                <input name="${status.expression}" type="checkbox"
                                       <c:if test="${status.value eq 'on' || status.value eq 'true'}">checked="true"</c:if>
                                       value="true">
                            </c:when>

                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${prop.displayHeight != null}">
                                        <textarea name="${status.expression}"
                                                  rows="${prop.displayHeight}"
                                                  cols="<c:choose><c:when test="${prop.displayWidth != null}">${prop.displayWidth}</c:when><c:otherwise>40</c:otherwise></c:choose>"
                                                  class="fnormal"><c:out
                                                value='${status.value}'/></textarea>
                                    </c:when>
                                    <c:otherwise>
                                        <input name="${status.expression}"
                                               type="<c:choose><c:when test="${prop.name eq 'password'}">password</c:when><c:otherwise>text</c:otherwise></c:choose>"
                                               class="fnormal"
                                               size="<c:choose><c:when test="${prop.displayWidth != null}">${prop.displayWidth}</c:when><c:otherwise>40</c:otherwise></c:choose>"
                                               value="${(prop.name eq 'password') ? (not dataResource.editMode ? null : passwordSubstitution) : status.value}">
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>

               <span class="message warning">
                    <c:if test="${status.error}">${status.errorMessage}</c:if>
                </span>
                    </label>
                </spring:bind>
            </c:forEach>
        </fieldset>
    </t:putAttribute>
</t:insertTemplate>