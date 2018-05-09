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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/spring" prefix="spring"%>
<%@taglib uri="/WEB-INF/jasperserver.tld" prefix="js" %>

<html>
<head>
    <!-- ${sessionScope.XSS_NONCE} do not remove -->

    <title><spring:message code='jsp.listOlapViews.title'/></title>
  <meta name="pageHeading" content="<spring:message code='jsp.listOlapViews.pageHeading'/>"/>
  </head>

<body>

<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr>
    <td>

<span class="fsection"><spring:message code='jsp.listOlapViews.section'/></span>
<br/>
<br/>
<form name="fmLstOlaps" method="post" action="../">
<table border="0" width="100%" cellpadding="0" cellspacing="0">
	<!-- dummy inputs, to keep visual consistency with Reports -->
	<input type="submit" name="_eventId_selectOlapView" id="selectOlapView" value="" style="visibility:hidden;"/>
	<input type="hidden" name="olapUnit"/>
	<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
  <tr bgcolor="#c2c4b6" class="fheader">
    <td class="paddedcell" width="20%"><spring:message code='jsp.listOlapViews.olap_view_name'/></td>
    <td class="paddedcell" width="40%"><spring:message code='jsp.listOlapViews.description'/></td>
    <td class="paddedcell" width="10%"><spring:message code='jsp.listOlapViews.date'/></td>
    <td class="paddedcell" width="10%"><spring:message code='RM_CREATE_FOLDER_PARENT_FOLDER'/></td>
  </tr>
<js:paginator items="${olapUnits}" page="${currentPage}" formName="fmLstOlaps">
<c:forEach var="olapUnit" items="${paginatedItems}" varStatus="itStatus">
  <tr height="18" <c:if test="${itStatus.count % 2 == 0}">class="list_alternate"</c:if>>
    <td><a href="javascript:disableLink('<c:url value="/olap/viewOlap.html"><c:param name="name" value="${olapUnit.URIString}"/><c:param name="new" value="true"/></c:url>');" class="disLink" onclick="this.disabled='true'">
		${olapUnit.label}</a></td>
    <td class="paddedcell">${olapUnit.description}</td>
    <td class="paddedcell" nowrap><js:formatDate value="${olapUnit.creationDate}"/></td>
    <td class="paddedcell" nowrap>   
     <a href="listOlapViews.jsp#" onclick="gotoFolderExplorer('${olapUnit.parentFolder}')">
      ${olapUnit.parentFolder}
     </a> 
    </td>
  </tr>
</c:forEach>
	<tr>
		<td class="paddedcell" colspan="4">
			<js:paginatorLinks/>
		</td>
	</tr>
</js:paginator>
</table>
</form>

    </td>
  </tr>
</table>

<script type="text/javascript"> 
function disableLink(url) { 
	//document.write("<style type='text/css'>.disLink{display:none;}</style>"); 
	location.href = url; 
} 
// -->
</script> 
</body>

</html>
