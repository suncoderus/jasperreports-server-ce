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
<%@ page import="com.jaspersoft.jasperserver.war.dto.StringOption" %>

<t:useAttribute id="pageTitle" name="pageTitle" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyID" name="bodyID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="dataSourceType" name="dataSourceType" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="typeSpecificScripts" name="typeSpecificScripts" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="typeSpecificContent" name="typeSpecificContent" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="typeSpecificContentAfterFolder" name="typeSpecificContentAfterFolder" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="typeSpecificContentAfterBody" name="typeSpecificContentAfterBody" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="testAvailable" name="testAvailable" classname="java.lang.Boolean" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle">${pageTitle}</t:putAttribute>
    <t:putAttribute name="bodyID" value="${bodyID}"/>
    <t:putAttribute name="bodyClass" value="oneColumn flow wizard ${lastSubflow?'oneStep':''}"/>
    <t:putAttribute name="moduleName" value="addDataSource.page"/>
    <t:putAttribute name="headerContent">
        <jsp:include page="addDataSourceStateTemp.jsp"/>
    </t:putAttribute>

    <t:putAttribute name="bodyContent">
        <form method="post" action="flow.html">

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="column decorated primary showingToolBar"/>
            <t:putAttribute name="containerTitle">
                <c:choose>
                    <c:when test="${dataResource.editMode}"><spring:message code='resource.dataSource.title.edit'/></c:when>
                    <c:otherwise><spring:message code='resource.dataSource.title.add'/></c:otherwise>
                </c:choose>
            </t:putAttribute>

            <t:putAttribute name="swipeScroll" value="${isIPad}"/>

            <t:putAttribute name="bodyContent">
                <div id="flowControls"></div>
                <div id="stepDisplay">
                <fieldset class="row instructions">
                <legend class="offLeft"><span><spring:message code='resource.dataSource.instructions'/></span>
                </legend>
                <h2 class="textAccent02"><spring:message code='resource.dataSource.instructions1'/></h2>
                <h4><spring:message code='resource.dataSource.instructions2'/></h4>
                </fieldset>

                <fieldset class="row inputs oneColumn">
                <legend class="offLeft"><span><spring:message code='resource.dataSource.inputs'/></span>
                </legend>

                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                    <t:putAttribute name="containerClass" value="column primary"/>
                    <t:putAttribute name="containerTitle"><spring:message
                            code='resource.dataSource.type'/></t:putAttribute>
                    <t:putAttribute name="headerContent">
                        <label class="control select inline" for="typeID" title="<spring:message code='resource.dataSource.dstype'/>">
                                    <span class="wrap offLeft"><spring:message
                                            code='resource.dataSource.dstype'/></span>
                            <select id="typeID" name="type" <c:if test='${dataResource.editMode}'>disabled="disabled"</c:if>>
                                <option value="bean" <c:if test='${dataSourceType=="bean"}'>selected="true"</c:if>><spring:message code='resource.dataSource.dstypeBean'/></option>
                                <option value="jdbc" <c:if test='${dataSourceType=="jdbc"}'>selected="true"</c:if>><spring:message code='resource.dataSource.dstypeJDBC'/></option>
                                <option value="jndi" <c:if test='${dataSourceType=="jndi"}'>selected="true"</c:if>><spring:message code='resource.dataSource.dstypeJNDI'/></option>
                                <option value="virtual" <c:if test='${dataSourceType=="virtual"}'>selected="true"</c:if>><spring:message code='resource.dataSource.dstypeVirtual'/></option>
                                <option value="aws" <c:if test='${dataSourceType=="aws"}'>selected="true"</c:if>><spring:message code='resource.dataSource.dstypeAws'/></option>
                                <c:forEach items="${allTypes}" var="type1">
                                    <option value="${type1.key}" <c:if test='${type1.key==dataSourceType}'>selected="true"</c:if>>${type1.value}</option>
                                </c:forEach>
                            </select>
                            <span class="message warning">error message here</span>
                        </label>

                    </t:putAttribute>

                    <t:putAttribute name="bodyContent">
                        <fieldset id="" class="group shortFields">
                        <legend class="offLeft"><span><spring:message
                            code='resource.dataSource.nameanddesc'/></span></legend>
                        <spring:bind path="dataResource.reportDataSource.label">
                            <label class="control input text <c:if test="${status.error}"> error </c:if>"
                                   class="required" for="labelID"
                                   title="<spring:message code='resource.dataSource.labelIDtitle'/>">
                                            <span class="wrap"><spring:message
                                                    code='resource.dataSource.name'/> (<spring:message
                                                    code='required.field'/>):</span>
                                <input class="" id="labelID" type="text" name="${status.expression}"
                                       value="${status.value}"/>
                                <!-- NOTE: This inline javascript here just for demonstration purposes; in production use AJAX to validate and create resourceID on keyup -->
                                            <span class="message warning">
                                                <c:if test="${status.error}">${status.errorMessage}</c:if>
                                            </span>
                            </label>
                        </spring:bind>

                        <spring:bind path="dataResource.reportDataSource.name">
                            <label class="control input text <c:if test="${status.error}"> error </c:if>"
                                   for="nameID"
                                   title="<spring:message code='resource.dataSource.nameIDtitle'/>">
                                            <span class="wrap"><spring:message code='resource.dataSource.resource'/>
                                                <c:choose>
                                                    <c:when test="${dataResource.editMode}"> (<spring:message code='dialog.value.readOnly'/>):</c:when>
                                                    <c:otherwise> (<spring:message code='required.field'/>):</c:otherwise>
                                                </c:choose>
                                            </span>
                                <input class="" id="nameID" type="text" name="${status.expression}"
                                       value="${status.value}" <c:if test="${dataResource.editMode}">readonly="readonly"</c:if>/>
                                            <span class="message warning">
                                                <c:if test="${status.error}">${status.errorMessage}</c:if>
                                            </span>
                            </label>
                        </spring:bind>

                        <spring:bind path="dataResource.reportDataSource.description">
                            <label class="control textArea <c:if test="${status.error}"> error </c:if>"
                                   for="descriptionID" title="<spring:message code='resource.dataSource.descriptionTitle'/>">
                                            <span class="wrap"><spring:message
                                                    code='resource.dataSource.description'/></span>
                                <textarea id="descriptionID" name="${status.expression}" type="text"><c:out
                                        value='${status.value}'/></textarea>
                                            <span class="message warning">
                                                <c:if test="${status.error}">${status.errorMessage}</c:if>
                                            </span>
                            </label>
                        </spring:bind>
                        </fieldset>
					<%-- CODE CUT --%>

                        ${typeSpecificContent}
                        <fieldset class="group">
                            <spring:bind path="dataResource.reportDataSource.parentFolder">
                                <label id="" class="control saveLocation browser<c:if test="${status.error}"> error</c:if>" for="folderUri" title="<spring:message code='form.saveLocationTitle'/>">
                                    <span class="wrap"><spring:message code="dialog.datasource.destination"/> (<spring:message code='required.field'/>):</span>
                                    <input id="folderUri" type="text" name="${status.expression}" value="${status.value}" <c:if test="${dataResource.editMode}">disabled="disabled"</c:if>/>
                                    <button id="browser_button" type="button" class="button action" <c:if test="${dataResource.editMode}">disabled="disabled"</c:if>><span class="wrap"><spring:message code="button.browse"/><span class="icon"></span></span></button>
                                    <c:if test="${status.error}">
                                        <span class="message warning">${status.errorMessage}</span>
                                    </c:if>
                                </label>
                            </spring:bind>
                        </fieldset>
                        ${typeSpecificContentAfterFolder}
                        <c:if test="${testAvailable}">
                            <fieldset class="group">
                                <span id="testDataSource" type="submit" class="button action up"
                                        name="_eventId_testDataSource"><span
                                        class="wrap"><spring:message code='button.testConnection'/></span>
                                </span>
                                <div class="message warning">
                                    <span >error message here</span>
                                    <a href="#" class="details"><spring:message code='button.details'/></a>
                                </div>
                            </fieldset>
                        </c:if>
                        <br/>
                    </t:putAttribute>
                </t:insertTemplate>
                </fieldset>
                    <!--/.row.inputs-->
                </div>
                <t:putAttribute name="footerContent">
                    <fieldset id="wizardNav">
                        <button id="previous" type="submit" class="button action up" name="_eventId_back"><span class="wrap"><spring:message
                                code='button.previous'/></span><span class="icon"></span></button>
                        <button id="next" type="submit" class="button action up" name="_eventId_save"><span class="wrap"><spring:message
                                code='button.next'/></span><span class="icon"></span></button>
                        <button id="done" type="submit" class="button primary action up"
                                name="_eventId_save"><span
                                class="wrap"><spring:message code='button.save'/></span><span
                                class="icon"></span></button>
                        <button id="dsCancel" type="submit" class="button action up" name="_eventId_cancel"><span
                                class="wrap"><spring:message
                                code='button.cancel'/></span><span class="icon"></span></button>
                    </fieldset>
                </t:putAttribute>
            </t:putAttribute>
        </t:insertTemplate>
            <div id="ajaxbuffer" style="display:none"></div>
            <input type="hidden" id="_flowExecutionKey" name="_flowExecutionKey" value="${flowExecutionKey}"/>
            <input type="hidden" name="_eventId_initAction" id="submitEvent"/>
        </form>


        <t:insertTemplate template="/WEB-INF/jsp/templates/selectFromRepository.jsp">
            <t:putAttribute name="containerClass">hidden</t:putAttribute>
            <t:putAttribute name="bodyContent">
                <ul id="addFileTreeRepoLocation"></ul>
            </t:putAttribute>
        </t:insertTemplate>

        ${typeSpecificContentAfterBody}
    </t:putAttribute>
</t:insertTemplate>
