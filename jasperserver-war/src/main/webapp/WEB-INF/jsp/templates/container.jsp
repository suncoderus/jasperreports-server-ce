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

Usage:

	<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
	    <t:putAttribute name="containerID" value=""/>
	    <t:putAttribute name="containerClass" value=""/>
	    <t:putAttribute name="containerAttributes">to include name=value pairs in the container tag</t:putAttribute>
	    <t:putAttribute name="containerElements">to include markup within the container but outside the content division</t:putAttribute>
	    <t:putAttribute name="containerTitle"></t:putAttribute>

        <t:putAttribute name="contentID" value=""/>
        <t:putAttribute name="contentClass" value=""/>
        <t:putAttribute name="contentAttributes">to include name=value pairs in the header tag</t:putAttribute>
            [OPTIONAL]
	    <t:putAttribute name="headerID" value=""/>
	    <t:putAttribute name="headerClass" value=""/>
	    <t:putAttribute name="headerAttributes">to include name=value pairs in the header tag</t:putAttribute>
	    <t:putAttribute name="headerContent">
			[OPTIONAL]
	    </t:putAttribute>
	    <t:putAttribute name="bodyID" value=""/>
	    <t:putAttribute name="bodyClass" value=""/>
	    <t:putAttribute name="bodyAttributes">to include name=value pairs in the body tag</t:putAttribute>
	    <t:putAttribute name="bodyContent">
			[REQUIRED]
	    </t:putAttribute>
	    <t:putAttribute name="footerID" value=""/>
	    <t:putAttribute name="footerClass" value=""/>
	    <t:putAttribute name="footerAttributes">to include name=value pairs in the footer tag</t:putAttribute>
	    <t:putAttribute name="footerContent">
	    	[OPTIONAL]
	    </t:putAttribute>
	</t:insertTemplate>
	
NOTES:
1. All attribute tags are optional
2. All attribute tags may be included in either form:
   - <t:putAttribute name="attributeName" value="values"/>
   - <t:putAttribute name="attributeName"> values </t:putAttribute>
3. All attribute tags MUST be closed 
4. Attribute tag order does NOT matter

  	
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ page import="java.util.Arrays" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerAttributes" name="containerAttributes" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerElements" name="containerElements" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerTitle" name="containerTitle" classname="java.lang.String" ignore="true"/>

<t:useAttribute id="contentID" name="contentID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="contentClass" name="contentClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="contentAttributes" name="contentAttributes" classname="java.lang.String" ignore="true"/>

<t:useAttribute id="headerID" name="headerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="headerClass" name="headerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="headerAttributes" name="headerAttributes" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="headerControls" name="headerControls" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="headerContent" name="headerContent" classname="java.lang.String" ignore="true"/>

<t:useAttribute id="subHeaderClass" name="subHeaderClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="subHeaderAttributes" name="subHeaderAttributes" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="subHeaderContent" name="subHeaderContent" classname="java.lang.String" ignore="true"/>

<t:useAttribute id="bodyID" name="bodyID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyClass" name="bodyClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyAttributes" name="bodyAttributes" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="swipeScroll" name="swipeScroll" classname="java.lang.Boolean" ignore="true"/>
<t:useAttribute id="swipeScrollAll" name="swipeScrollAll" classname="java.lang.Boolean" ignore="true"/>

<t:useAttribute id="footerID" name="footerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="footerClass" name="footerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="footerAttributes" name="footerAttributes" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="footerContent" name="footerContent" classname="java.lang.String" ignore="true"/>

<c:set var="swipeScrollClass" value=""/>

<c:if test="${swipeScroll}">
    <c:set var="swipeScrollClass" value="swipeScroll"/>
</c:if>

<div <c:if test="${containerID != null}">id="${containerID}"</c:if> class="${containerClass}" ${containerAttributes}>

    <!-- ${sessionScope.XSS_NONCE} do not remove -->

    <c:if test="${param['nui'] == '1'}">
	    <t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
	</c:if>
	${containerElements}
    <c:if test="${footerContent != null}">
        <div <c:if test="${contentID != null}">id="${contentID}"</c:if> class="content hasFooter ${contentClass}" ${contentAttributes}>
    </c:if>
    <c:if test="${footerContent == null}">
        <div <c:if test="${contentID != null}">id="${contentID}"</c:if> class="content ${contentClass}" ${contentAttributes}>
    </c:if>
		<div <c:if test="${headerID != null}">id="${headerID}"</c:if> class="header ${headerClass}" ${headerAttributes}>
            <div class="title">
                ${containerTitle}
			</div>
            ${headerContent}
            <%--
            <c:choose>
                <c:when test="${(fn:contains(containerClass, 'secondary') || fn:contains(containerClass, 'tertiary')) && !fn:contains(containerClass, 'showingSubHeader') }">
                <div class="columnheader" style="position:absolute;top:0;right:0;padding-left:8px;z-index:9;">
                    ${headerContent}
                    <span class="minimize ui-icon ui-icon-minusthick" style="margin:4px 4px 4px 0;border:solid 1px #ccc;display:inline-block;cursor:pointer;"></span>
                </div>
                </c:when>
                <c:otherwise>
                    ${headerContent}
                </c:otherwise>
            </c:choose>
            --%>
		</div>
        <c:if test="${subHeaderContent != null}">
        <div class="subheader ${subHeaderClass} ${subHeaderAttributes}">
            ${subHeaderContent}
        </div>
        </c:if>
		<div <c:if test="${bodyID != null}">id="${bodyID}"</c:if> class="body ${bodyClass} ${swipeScrollClass}" ${bodyAttributes}>
            <c:if test="${swipeScroll}"><div class="scrollWrapper"></c:if>
			    ${bodyContent}
            <c:if test="${swipeScroll}"></div></c:if>
        </div>
        <c:if test="${footerContent != null}">
		<div <c:if test="${footerID != null}">id="${footerID}"</c:if> class="footer ${footerClass}" ${footerAttributes}>
			${footerContent}
		</div>
        </c:if>
	</div>
</div>
