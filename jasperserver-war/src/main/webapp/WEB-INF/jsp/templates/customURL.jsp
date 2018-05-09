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
    Usage:permit user to add a file to the repository

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/customURL.jsp">
    	<t:putAttribute name="bodyContent">
    	
    	</t:putAttribute>   
    </t:insertTemplate>

--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="selectFileButtonId" name="selectFileButtonId" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="cancelButtonId" name="cancelButtonId" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog customURL overlay sizeable centered_vert centered_horz moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="customURL"/>
    <t:putAttribute name="containerTitle"><spring:message code='ADH_771c_CUSTOM_URL_LABEL'/></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
   	<t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="bodyContent">
		<label class="control input text" for="url" title="<spring:message code='ADH_773_CUSTOM_URL_INFO'/>">
			<span class="wrap"><spring:message code='ADH_773_CUSTOM_URL_INFO'/></span>
			<input class="" id="customURLInput" type="text" value=""/>
			<span class="message warning"><spring:message code='ADH_107_GENERIC_ERROR'/></span>
		</label>

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value=""/>
            <t:putAttribute name="containerClass" value="control groupBox fillParent"/>
            <t:putAttribute name="bodyContent">${bodyContent}</t:putAttribute>
        </t:insertTemplate>

    </t:putAttribute>
    <t:putAttribute name="footerContent">
        <button id="customURLOk" class="button action primary up"><span class="wrap"><spring:message code="button.ok"/></span><span class="icon"></span></button>
        <button id="customURLCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/></span><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
