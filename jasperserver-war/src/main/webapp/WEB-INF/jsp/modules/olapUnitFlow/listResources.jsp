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

<%@ page language="java" contentType="text/html" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<!-- ${sessionScope.XSS_NONCE} do not remove -->

  <title><spring:message code="jsp.listResources.title"/></title>
  <meta name="pageHeading" content="<c:choose><c:when test='${wrapper.aloneEditMode}'><spring:message code="jsp.olapDS.pageHeading_edit"/></c:when><c:otherwise><spring:message code="jsp.olapDS.pageHeading_new"/></c:otherwise></c:choose>"/>
  <script>
            function jumpTo(pageTo){
//FIXME not used
            document.forms['fmCRExtRsrCh'].jumpToPage.value=pageTo;
            document.forms['fmCRExtRsrCh'].jumpButton.click();
            }
  </script>
  <script>
        	function editResource(resName){
				document.forms['fmCRExtRsrCh'].resourceName.value=resName;
				document.forms['fmCRExtRsrCh'].editResourceButton.click();
        	}
			function editControl(resName){
				document.forms['fmCRExtRsrCh'].resourceName.value=resName;
				document.forms['fmCRExtRsrCh'].editControlButton.click();
        	}        	
  </script>
</head>

<body>

<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr>
    <td>

<FORM name="fmCRExtRsrCh" action="" method="post">
                <table border="0" cellpading="0" cellspacing="0" align="center">
                    <tr>
                        <td><span class="fsection"><spring:message code="jsp.listResources.header"/></span></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <table width="100%" cellpadding="0" cellspacing="0">
                                <tr bgcolor="#c2c4b6" class="fheader">
                                    <td><strong><spring:message code="jsp.listResources.resourceName"/></strong></td>
                                    <td><strong><spring:message code="jsp.listResources.resourceType"/></strong></td>
                                    <td><spring:message code="jsp.listResources.stateAction"/></td>
                                </tr>
                                <tr>
                                    <td colspan="3">&nbsp;</td>
                                </tr>
                                <%-- Create an instance of FileResourceWrapper, for getting the supported file type names--%>
								<jsp:useBean id="fileResourceWrapper" class="com.jaspersoft.jasperserver.war.dto.FileResourceWrapper" />
								
								<%-- Create an instance of InputControlWrapper, for getting the supported control type names--%>
								<jsp:useBean id="inputControlWrapper" class="com.jaspersoft.jasperserver.war.dto.InputControlWrapper" />
                                
                                <%-- Show added resources present in the OlapUnit--%>
                                <c:forEach items="${wrapper.olapUnit.resources}" var="res">
                                	<%-- Show only those resources which arent marked suggested --%>
                                	<c:set var="suggested" value="false"/>
                                	<c:forEach items="${wrapper.suggestedResources}" var="resWrap">
                                		<c:if test="${resWrap.fileResource.name==res.localResource.name}">
                                			<c:set var="suggested" value="true"/>
                                		</c:if>
                                	</c:forEach>
                                	<c:if test="${!suggested}">
	                                    <tr>
	                                        <td><a href="javascript:editResource('${res.localResource.name}')">${res.localResource.name}</a></td>
	                                        <td><c:choose><c:when test='${res.localResource.fileType!=null}'>${fileResourceWrapper.allTypes[res.localResource.fileType]}</c:when><c:otherwise><spring:message code="jsp.listResources.resource"/></c:otherwise></c:choose></td>
	                                        <td>
	                                        	<input type="image" name="_eventId_RemoveResource" title="<spring:message code="jsp.listResources.button.removeResource"/>" alt="Remove"  src="images/delete1.gif" onclick="document.fmCRExtRsrCh.resourceName.value='${res.localResource.name}';">
	                                        </td>
	                                    </tr>
                                    </c:if>
                                </c:forEach>
								
								<%-- Show suggested resources, added or not --%>								                                                               
                               <c:if test="${wrapper.hasSuggestedResources}">
		                           <tr>
	                                    <td colspan="3" align="center"><strong><spring:message code="jsp.listResources.suggestedResources"/></strong></td>
	                               </tr>
                               </c:if>
                                <c:forEach items="${wrapper.suggestedResources}" var="resWrap">
                                	
	                                    <tr>
	                                        <td><a href="javascript:editResource('${resWrap.fileResource.name}')">${resWrap.fileResource.name}</a></td>
	                                        <td><c:choose><c:when test='${resWrap.fileResource.fileType!=null}'>${fileResourceWrapper.allTypes[resWrap.fileResource.fileType]}</c:when><c:otherwise><spring:message code="jsp.listResources.resource"/></c:otherwise></c:choose></td>
	                                        <td>
	                                        	<c:choose><c:when test="${resWrap.located}"><spring:message code="jsp.listResources.added"/></c:when>
	                                        	<c:otherwise><a href="javascript:editResource('${resWrap.fileResource.name}')"><spring:message code="jsp.listResources.addNow"/></a></c:otherwise></c:choose>
	                                        </td>
	                                    </tr>
                                    
                                </c:forEach>
                                
                                <%-- Show added controls --%>                       
                                <c:if test="${wrapper.hasNonSuggestedControls}">
		                           <tr>
	                                    <td colspan="3" align="center"><strong><spring:message code="jsp.listResources.addedControls"/></strong></td>
	                               </tr>
                               </c:if> 
                               <c:forEach items="${wrapper.olapUnit.inputControls}" var="control">
                                	<%-- Show only those controls which arent marked suggested --%>
                                	<c:set var="suggested" value="false"/>
                                	<c:forEach items="${wrapper.suggestedControls}" var="contWrap">
                                		<c:if test="${contWrap.inputControl.name==control.localResource.name}">
                                			<c:set var="suggested" value="true"/>
                                		</c:if>
                                	</c:forEach>
                                	<c:if test="${!suggested}">
										<tr>
											<c:if test="${control.local}">
												<td><strong><a href="javascript:editControl('${control.localResource.name}')">${control.localResource.name}</a></strong></td>
											</c:if>
								   			<c:if test="${!control.local}">
												   <td><strong><a href="javascript:editControl('${control.referenceURI}')">${control.referenceURI}</a></strong></td>
											</c:if>
											<td>${inputControlWrapper.supportedControlTypes[control.localResource.type]} <spring:message code="jsp.inputControl"/></td>
	                                        <td>
												<c:if test="${control.local}">
													<input type="image" name="_eventId_RemoveControl" title="<spring:message code="jsp.listResources.button.removeControl"/>" alt="<spring:message code="button.remove"/>"  src="images/delete1.gif" onclick="document.fmCRExtRsrCh.resourceName.value='${control.localResource.name}';">
								   				</c:if>
												<c:if test="${!control.local}">
													<input type="image" name="_eventId_RemoveControl" title="<spring:message code="jsp.listResources.button.removeControl"/>" alt="<spring:message code="button.remove"/>"  src="images/delete1.gif" onclick="document.fmCRExtRsrCh.resourceName.value='${control.referenceURI}';">
												</c:if>
											</td>
	                                    </tr>
                                    </c:if>
                                </c:forEach>
                               
                               <%-- Show suggested controls --%>
								<c:if test="${wrapper.hasSuggestedControls}">
		                           <tr>
	                                    <td colspan="3" align="left"><strong><spring:message code="jsp.listResources.suggestedControls"/></strong></td>
	                               </tr>
								</c:if>
								<c:forEach items="${wrapper.suggestedControls}" var="contWrap">
                               		    <tr>
	                                        <td><strong><a href="javascript:editControl('${contWrap.inputControl.name}')">${contWrap.inputControl.name}</a></strong></td>
	                                        <td><strong>${contWrap.supportedControlTypes[contWrap.inputControl.inputControlType]} <spring:message code="jsp.inputControl"/></strong></td>
	                                        <td>
	                                            <c:choose><c:when test="${contWrap.located}"><input type="image" name="_eventId_RemoveControl" title="<spring:message code="jsp.listResources.button.removeControl"/>" alt="<spring:message code="button.remove"/>"  src="images/delete1.gif" onclick="document.fmCRExtRsrCh.resourceName.value='${contWrap.inputControl.name}';"></c:when>
	                                            <c:otherwise><spring:message code="jsp.listResources.notAdded"/></c:otherwise></c:choose>
	                                        </td>
	                                    </tr>
                                </c:forEach>                                
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td>
                        	<spring:bind path="wrapper.validationMessage">
                        		<input type="hidden" name="${status.expression}">
                        		<c:if test="${status.error}"><span class="ferror">${status.errorMessage}</span></c:if>
                        	</spring:bind>
                        </td>
                   </tr>
                    <tr>
                        <td>
                            <input type="submit" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>" onclick=""/>&nbsp;
                            <input type="submit" class="fnormal" name="_eventId_Back" value="<spring:message code="button.back"/>">&nbsp;
                            <input type="submit" name="_eventId_AddResource" class="fnormal"  value="<spring:message code="jsp.listResources.button.addResource"/>">&nbsp;
                            <input type="submit" name="_eventId_AddControl" class="fnormal"  value="<spring:message code="jsp.listResources.button.addControl"/>">
                            <input type="submit" class="fnormal" name="_eventId_Next" value="<spring:message code="button.next"/>">
                        </td>
                    </tr>
                </table>
            <input type="hidden" name="resourceName">
            <input type="hidden" name="resourceType">
            <input type="hidden" name="jumpToPage">
            <input type="submit" style="visibility:hidden;" class="fnormal" value="EditControl" name="_eventId_EditControl" id="editControlButton">
            <input type="submit" style="visibility:hidden;" class="fnormal" value="EditResource" name="_eventId_EditResource" id="editResourceButton">
            <input type="submit" style="visibility:hidden;" class="fnormal" value="Jump" name="_eventId_Jump" id="jumpButton">
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
</FORM>

    </td>
  </tr>
</table>

</body>

</html>
