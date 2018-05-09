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
    <t:insertTemplate template="/WEB-INF/jsp/templates/sortDialog.jsp">
        <t:putAttribute name="bodyContent">
		    <t:putAttribute name="availableFields"></t:putAttribute>
        	<t:putAttribute name="selectedFields"></t:putAttribute>
    	</t:putAttribute>
    </t:insertTemplate>

--%>

<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>


<t:useAttribute name="containerClass" id="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="bodyContent" id="bodyContent" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="availableFields" id="availableFields" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="selectedFields" id="selectedFields" classname="java.lang.String" ignore="true"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog sortDialog overlay moveable sizeable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="containerID" value="sortDialog"/>
    <t:putAttribute name="containerTitle"><spring:message code="dialog.sortDialog.title" javaScriptEscape="true"/></t:putAttribute>
    <t:putAttribute name="bodyClass" value="oneColumn"/>
    <t:putAttribute name="bodyContent" cascade="true">
        
		<div class="column simple">
            <div class="body twoColumn_equal pickWells">
            	<div class="moveButtons">
					<button id="sortDialogAddToSort" class="button action square move right up" title='<spring:message code="dialog.sortDialog.Add" javaScriptEscape="true"/>'><span class="wrap"><spring:message code="dialog.sortDialog.moveRightButton" javaScriptEscape="true"/><span class="icon"></span></span></button>
					<button id="sortDialogRemoveFromSort" class="button action square move left up" title='<spring:message code="dialog.sortDialog.Remove" javaScriptEscape="true"/>'><span class="wrap"><spring:message code="dialog.sortDialog.moveLeftButton" javaScriptEscape="true"/><span class="icon"></span></span></button>
				</div>	
           		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
				    <t:putAttribute name="containerClass" value="column decorated secondary"/>
				    <t:putAttribute name="containerID" value="availableFields"/>
				    <t:putAttribute name="containerTitle"><spring:message code="dialog.sortDialog.availableFields" javaScriptEscape="true"/></t:putAttribute>
                    <t:putAttribute name="swipeScroll" value="${isIPad}"/>

				    <t:putAttribute name="bodyContent">
				    	 ${availableFields}
				    </t:putAttribute>

				</t:insertTemplate>
           		
           		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
				    <t:putAttribute name="containerClass" value="column decorated primary"/>
				    <t:putAttribute name="containerID" value="sortOn"/>
				    <t:putAttribute name="containerTitle"><spring:message code="dialog.sortDialog.sortOn" javaScriptEscape="true"/></t:putAttribute>
                    <%--<t:putAttribute name="swipeScroll" value="${isIPad}"/>--%>
				    <t:putAttribute name="bodyContent">
				     	${selectedFields}
					<div class="moveButtons">
						<button id="sortDialogToTop" class="button action square move toTop up" title='<spring:message code="dialog.sortDialog.moveToTopButton" javaScriptEscape="true"/>'><span class="wrap"><spring:message code="dialog.sortDialog.moveToTopButton" javaScriptEscape="true"/><span class="icon"></span></span></button>
						<button id="sortDialogUpward" class="button action square move upward up" title='<spring:message code="dialog.sortDialog.moveUpButton" javaScriptEscape="true"/>'><span class="wrap"><spring:message code="dialog.sortDialog.moveUpButton" javaScriptEscape="true"/><span class="icon"></span></span></button>
						<button id="sortDialogDownward" class="button action square move downward up" title='<spring:message code="dialog.sortDialog.moveDownButton" javaScriptEscape="true"/>'><span class="wrap"><spring:message code="dialog.sortDialog.moveDownButton" javaScriptEscape="true"/><span class="icon"></span></span></button>
						<button  id="sortDialogToBottom" class="button action square move toBottom up" title='<spring:message code="dialog.sortDialog.moveToBottomButton" javaScriptEscape="true"/>'><span class="wrap"><spring:message code="dialog.sortDialog.moveToBottomButton" javaScriptEscape="true"/><span class="icon"></span></span></button>
					</div>		
				    </t:putAttribute>
				</t:insertTemplate>
           </div>
       </div>
							   
    </t:putAttribute>
    
    <t:putAttribute name="footerContent">
         <button id="sortDialogOk" class="button action primary up"><span class="wrap"><spring:message code="button.ok" javaScriptEscape="true"/><span class="icon"></span></button>
         <button id="sortDialogCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel" javaScriptEscape="true"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
