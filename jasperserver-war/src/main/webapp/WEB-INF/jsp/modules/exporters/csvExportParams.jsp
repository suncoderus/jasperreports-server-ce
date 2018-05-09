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
<html>
<head>
<!-- ${sessionScope.XSS_NONCE} do not remove -->

  <title><spring:message code='jsp.csvExportParams.title'/></title>
  <meta name="pageHeading" content="<spring:message code='jsp.csvExportParams.pageHeading'/>"/>
  
</head>

<body>
<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr>
    <td>

<form name="csvExportParametersForm" method="post" action="">
<table border="0" cellpadding="1" cellspacing="0" align="center">
<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
  <tr>
    <td>&nbsp;</td>
    <td><span class="fsection"><spring:message code='jsp.csvExportParams.title'/></span></td>
  </tr>
  <tr>
    <td colspan="2">&nbsp;</td>
  </tr>
	<spring:bind path="csvExportParams.fieldDelimiter">
	<tr>
		<td align="right"><spring:message code="jsp.csvExportParams.field.delimiter"/>&nbsp;</td>
		<td align="left">
			<input maxlength="1" name="${status.expression}" type="text" class="fnormal" size="25" value="${status.value}"/>
			<c:if test="${status.error}"><BR><span class="ferror">${status.errorMessage}</span></c:if>
		</td>
	</tr>
   </spring:bind>
  <tr>
    <td colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
		<input type="submit" name="_eventId_submit" value='<spring:message code="button.submit"/>' class="fnormal"/>&nbsp;
		<input type="button" name="btnclose" value='<spring:message code="button.close"/>' class="fnormal" onclick="javascript:window.close()"/>
    </td>
  </tr>

</table>
</form>

    </td>
  </tr>
</table>
</body>

</html>
