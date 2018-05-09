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


<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} do not remove
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        isEditMode: <c:choose>
                        <c:when test='${masterFlow == "reportUnit" && masterFlowStep != "query"}'>${dataResource.subEditMode}</c:when>
                        <c:otherwise>${query.editMode}</c:otherwise>
                    </c:choose>
        <c:choose>
            <c:when test='${dataResource.dataSourceUri != null}'>
        ,
        <js:out javaScriptEscape="true">
        dataSourceUri: "${dataResource.dataSourceUri}"
        </js:out>
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test='${dataResource.dataSourceJson != null}'>
        ,
        <js:out javaScriptEscape="true">
        dataSourceJson: JSON.parse('${dataResource.dataSourceJson}')
        </js:out>
            </c:when>
        </c:choose>

    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["resource.Query.Title"] = '<spring:message code="resource.DataSource.Title" javaScriptEscape="true"/>';
    resource.messages["dataSourceInvalid"] = '<spring:message code="resource.query.linkDataSource2" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addQuery === "undefined") {
        __jrsConfigs__.addQuery = {};
    }

    __jrsConfigs__.addQuery.localContext = localContext;
    __jrsConfigs__.addQuery.resource = resource;

    <c:choose>
    <c:when test='${masterFlow == "reportUnit" && masterFlowStep != "query"}'>
    __jrsConfigs__.addQuery.buttonFlowControls = true;
    </c:when>
    <c:otherwise>
    __jrsConfigs__.addQuery.buttonFlowControls = false;
    </c:otherwise>
    </c:choose>
</script>