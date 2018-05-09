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

<%--
Overview:
    Tab control to be used to permit switching
Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
        <t:putAttribute name="containerID" value="[OPTIONAL]"/>
        <t:putAttribute name="containerClass" value="[OPTIONAL]"/>
        <t:putAttribute name="inputID" value="[OPTIONAL]"/>
        <t:putAttribute name="inputTabindex" value="[OPTIONAL]"/>
    </t:insertTemplate>

--%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerAttr" name="containerAttr" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="inputID" name="inputID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="accClass" name="accClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="inputTabindex" name="inputTabindex" classname="java.lang.String" ignore="true"/>

<%--/WEB-INF/jsp/templates/control_searchLockup.jsp revision A--%>
<c:choose>
<c:when test="${isIPad}">
<span id="${containerID}" class="control searchLockup ${containerClass}" ${containerAttr} ${accClass}>
    <label for="${inputID}" class="offLeft"><spring:message code="button.search" javaScriptEscape="true"/></label>
    <div class="wrap">
        <input type="text" class="${accClass}" id="${inputID}" tabindex="${inputTabindex}"/>
    </div>
    <b class="right"><a class="button searchClear"></a></b>
    <a class="button search up"></a>
</span>

</c:when>
<c:otherwise>
<span id="${containerID}" class="control searchLockup ${containerClass}" ${containerAttr}>
    <label for="${inputID}" class="offLeft"><spring:message code="button.search" javaScriptEscape="true"/></label>
    <div class="wrap">
        <input type="text" class="${accClass}" id="${inputID}" tabindex="${inputTabindex}"/>
    </div>
    <b class="right"><a class="button searchClear"></a></b>
    <a class="button search up"></a>
</span>
</c:otherwise>
</c:choose>

