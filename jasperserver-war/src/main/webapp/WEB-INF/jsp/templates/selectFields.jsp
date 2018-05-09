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
    shows information about JasperServer

Usage:
    <t:insertTemplate template="/WEB-INF/jsp/templates/selectFields.jsp">
    	<t:putAttribute name="bodyContent">
		    <t:putAttribute name="availableFields"></t:putAttribute>
        	<t:putAttribute name="selectedFields"></t:putAttribute>
    	</t:putAttribute>
    </t:insertTemplate>

--%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>


<t:useAttribute name="containerClass" id="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="availableFields" id="availableFields" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="selectedFields" id="selectedFields" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog selectFields overlay moveable sizeable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/> 
    <t:putAttribute name="containerID" value="selectFields"/>
    <t:putAttribute name="containerTitle"><spring:message code="ADH_RE_SELECT_FIELDS" javaScriptEscape="true"/></t:putAttribute>
    <t:putAttribute name="bodyClass" value="oneColumn"/>
    <t:putAttribute name="bodyContent" cascade="true">
        
		<div class="column simple">
            <div class="body twoColumn_equal pickWells">
            	<div id="moveButtons" class="moveButtons">
					<button id="right" class="button action square move right up" title="<spring:message code="ADH_RE_MOVE_RIGHT" javaScriptEscape="true"/>"><span class="wrap"><spring:message code="ADH_RE_MOVE_RIGHT" javaScriptEscape="true"/><span class="icon"></span></span></button>
					<button id="left" class="button action square move left up" title="<spring:message code="ADH_RE_MOVE_LEFT" javaScriptEscape="true"/>"><span class="wrap"><spring:message code="ADH_RE_MOVE_LEFT" javaScriptEscape="true"/><span class="icon"></span></span></button>
					<button id="toLeft" class="button action square move toLeft up" title="<spring:message code="ADH_RE_MOVE_ALL_LEFT" javaScriptEscape="true"/>"><span class="wrap"><spring:message code="DH_RE_MOVE_ALL_LEFT" javaScriptEscape="true"/><span class="icon"></span></span></button>
					<button id="toRight" class="button action square move toRight up" title="<spring:message code="ADH_RE_MOVE_ALL_RIGHT" javaScriptEscape="true"/>"><span class="wrap"><spring:message code="ADH_RE_MOVE_ALL_RIGHT" javaScriptEscape="true"/><span class="icon"></span></span></button>
				</div>
           		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
				    <t:putAttribute name="containerClass" value="column decorated secondary"/>
				    <t:putAttribute name="containerID" value="sourceTablesColumn"/>
				    <t:putAttribute name="containerTitle"><spring:message code="ADH_RE_AVAILABLE_FIELDS" javaScriptEscape="true"/></t:putAttribute>
		
				    <t:putAttribute name="bodyContent">
				    	 ${availableFields}
				    </t:putAttribute>

				</t:insertTemplate>
           		
           		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
				    <t:putAttribute name="containerClass" value="column decorated primary"/>
				    <t:putAttribute name="containerID" value="destTablesColumn"/>
				    <t:putAttribute name="containerTitle"><spring:message code="ADH_RE_SELECTED_FIELDS" javaScriptEscape="true"/></t:putAttribute>
		
				    <t:putAttribute name="bodyContent">
				     	${selectedFields}		
				    </t:putAttribute>
				</t:insertTemplate>
           </div>
       </div>
							   
    </t:putAttribute>
    
    <t:putAttribute name="footerContent">
         <button id="selectFieldsOk" class="button action primary up"><span class="wrap"><spring:message code="button.ok" javaScriptEscape="true"/><span class="icon"></span></button>
         <button id="selectFieldsCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel" javaScriptEscape="true"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
