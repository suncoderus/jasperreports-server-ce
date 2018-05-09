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


<t:insertTemplate template="/WEB-INF/jsp/modules/addResource/dataSource/addDataSourceTemplate.jsp">
<t:putAttribute name="pageTitle">
    <c:choose>
        <c:when test="${dataResource.editMode}"><spring:message code="resource.datasource.jndi.page.title.edit"/></c:when>
        <c:otherwise><spring:message code="resource.datasource.jndi.page.title.add"/></c:otherwise>
    </c:choose>
</t:putAttribute>
<t:putAttribute name="bodyID" value="addResource_dataSource_federated"/>
<t:putAttribute name="dataSourceType" value="virtual"/>
<t:putAttribute name="testAvailable" value="${false}"/>
<t:putAttribute name="typeSpecificScripts">
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/components.dependent.dialog.js"></script>
    <script type="text/javascript" language="JavaScript" src="${pageContext.request.contextPath}/scripts/lib/mustache-0.4.0.js"></script>
</t:putAttribute>
<t:putAttribute name="typeSpecificContentAfterFolder">
    <fieldset class="group">
    <spring:bind path="dataResource.namedProperties[selectedSubDs]">
            <input id="selectedSubDs" type="hidden" name="${status.expression}" value="${status.value}" size="200" />
            <c:if test="${status.error}">
                <span class="message warning">${status.errorMessage}</span>
            </c:if>
        </spring:bind>
    </fieldset>
    <fieldset class="group">
    <div id="dataSourcesSelector" class="content" style="position:relative">
            <div id="subDsSelectionContainer" class="body twoColumn_equal pickWells">
                <div id="moveButtons" class="moveButtons">
                    <button id="right" disabled="true" class="button action square move right up" title="<spring:message code="resource.moveRight"/>"><span class="wrap"><spring:message code="resource.moveRight"/><span class="icon"></span></span></button>
                    <button id="left" disabled="true" class="button action square move left up" title="<spring:message code="resource.moveLeft"/>"><span class="wrap"><spring:message code="resource.moveLeft"/><span class="icon"></span></span></button>
                    <button id="toLeft" disabled="true" class="button action square move toLeft up" title="<spring:message code="resource.moveAllLeft"/>"><span class="wrap"><spring:message code="resource.moveAllLeft"/><span class="icon"></span></span></button>
                </div>


                <div id="availableDataSources" class="column secondary">
                    <label class="control" for="subDataSourcesTree">
                        <span class="wrap"><spring:message code="resource.dataSource.virtual.availableDataSources"/></span>
                    <br/>
                    <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                        <t:putAttribute name="containerClass" value="control groupBox"/>
                        <t:putAttribute name="bodyContent"><ul id="subDataSourcesTree"></ul></t:putAttribute>
                    </t:insertTemplate>
                    </label>
                </div>


                <div id="selectedDataSources" class="column primary">
                    <label class="control" for="selectedSubDataSourcesList">
                        <span class="wrap"><spring:message code="resource.dataSource.virtual.selectedDataSources"/></span>
                        <br/>
                        <%-- Selected resources list header --%>
                        <div class="content">
                            <ul id="selectedDataSourcesHeader" class="list tabular twoColumn header">
                                <li class="">
                                    <div class="wrap">
                                        <div class="column one">
                                            <p class="dataSourceName"><spring:message code='resource.dataSource.name'/></p>
                                        </div>
                                        <div class="column two">
                                            <p class="dataSourceID"><spring:message code='resource.dataSource.virtual.subDsId'/></p>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                            <div id="selectedSubDataSourcesListContainer">
                                <%-- Selected resources list body --%>
                                <ol id="selectedSubDataSourcesList" class=""></ol>
                            </div>
                        </div>
                    </label>
                </div>
            </div>
        </div>
    </fieldset>

</t:putAttribute>
    <t:putAttribute name="typeSpecificContentAfterBody">
        <%-- template for selected data sources list
        --%>
        <div class="hidden">
            <ul id="selectedDataSourcesTemplate" class="list tabular twoColumn">
                <li id="selectedDataSourcesTemplate:leaf" class="leaf">
                    {{#readOnly}}
                    <div class="wrap button disabled">
                        {{/readOnly}}
                        {{^readOnly}}
                        <div class="wrap button">
                            {{/readOnly}}

                            <div class="column one">
                                <p class="dataSourceName">{{dsName}}</p>
                            </div>
                            <div class="column two">
                                    <%-- TODO fix markup: would be better if input takes all available width --%>
                                {{#readOnly}}
                                <input type="text" size="30" class="dataSourceID noSubmit" value="{{dsId}}" readonly="true"/><%-- TODO maybe we'll need DIV of SPAN for read-only mode--%>
                                {{/readOnly}}
                                {{^readOnly}}
                                <input type="text" size="30" class="dataSourceID noSubmit" value="{{dsId}}"/><%-- TODO specify size with CSS --%>
                                {{/readOnly}}
                                <div class="wrap validatorMessageContainer">
                                    <span class="message warning"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

        <t:insertTemplate template="/WEB-INF/jsp/templates/dependencies.jsp">
            <t:putAttribute name="containerClass" value="hidden centered_vert centered_horz"/>

            <t:putAttribute name="bodyContent">
                <ul id="dependenciesList">
                </ul>
            </t:putAttribute>
        </t:insertTemplate>
    </t:putAttribute>
</t:insertTemplate>
