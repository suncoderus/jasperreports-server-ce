<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


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

<script id="addJasperReportNonSuggestedResourceTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one">
                {{#canChangeResources}}
                <a class="emphasis" href="#">{{name}}</a>
                {{/canChangeResources}}
                {{^canChangeResources}}
                {{name}}
                {{/canChangeResources}}
            </p>
            <p class="column two">{{fileType}}</p>
            <p class="column three">
                {{#canChangeResources}}
                <a class="launcher" href="#"><spring:message code="resource.report.remove"/></a>
                {{/canChangeResources}}
            </p>
        </div>
    </li>
</script>

<script id="addJasperReportSuggestedResourceTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one"><a class="emphasis" href="#">{{label}}</a></p>
            <p class="column two">{{fileType}}</p>
            <p class="column three">
                {{#located}}
                <spring:message code="jsp.listResources.added"/>
                {{/located}}
                {{^located}}
                <a class="launcher" href="#"><spring:message code="jsp.listResources.addNow"/></a>
                {{/located}}
            </p>
        </div>
    </li>
</script>

<script id="addJasperReportAddResourceTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one">
            {{#canChangeResources}}
                <a class="launcher" href="#"><spring:message code="resource.report.addResource"/></a>
            {{/canChangeResources}}
            </p>
            <p class="column two"></p>
            <p class="column three"></p>
        </div>
    </li>
</script>

<script id="addJasperReportNonSuggestedControlTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one">
                {{#canChangeResources}}
                    {{#local}}
                        <a class="emphasis" href="#">{{label}}</a>
                    {{/local}}
                    {{^local}}
                        <a class="emphasis" href="#">{{referenceURI}}</a>
                    {{/local}}
                {{/canChangeResources}}
                {{^canChangeResources}}
                    {{#local}}
                        {{label}}
                    {{/local}}
                    {{^local}}
                        {{referenceURI}}
                    {{/local}}
                {{/canChangeResources}}
            </p>
            <p class="column two">{{type}} <spring:message code="jsp.listResources.inputControl"/></p>
            <p class="column three">
                {{#canChangeResources}}
                    <a class="launcher" href="#"><spring:message code="resource.report.remove"/></a>
                {{/canChangeResources}}
            </p>
        </div>
    </li>
</script>

<script id="addJasperReportSuggestedControlTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one"><a class="emphasis" href="#">{{label}}</a></p>
            <p class="column two">{{type}} <spring:message code="jsp.listResources.inputControl"/></p>
            <p class="column three">
                {{#located}}
                    <a class="launcher" href="#"><spring:message code="resource.report.remove"/></a>
                {{/located}}
                {{^located}}
                    <spring:message code="jsp.listResources.notAdded"/>
                {{/located}}
            </p>
        </div>
    </li>
</script>

<script id="addJasperReportAddControlTemplate" type="text/mustache">
    <li class="leaf">
        {{#canChangeResources}}
            <div class="wrap">
                <b class="icon" title=""></b>
                <p class="column one">
                    <!-- NOTE: this link invokes #selectFile -->
                    <a class="launcher" href="#"><spring:message code="resource.report.addInputControl"/></a>
                </p>
                <p class="column two"></p>
                <p class="column three"></p>
            </div>
        {{/canChangeResources}}
    </li>
</script>


