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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<!-- ${sessionScope.XSS_NONCE} do not remove -->

<c:if test='${dataResource.subflowMode}'>
    <meta name="pageHeading" content="<c:choose><c:when test='${dataResource.subEditMode}'><spring:message code="jsp.olapDS.pageHeading_edit"/></c:when><c:otherwise><spring:message code="jsp.olapDS.pageHeading_new"/></c:otherwise></c:choose>"/>
</c:if>
  <title><spring:message code="jsp.olapDS.chooseType.title"/></title>
  <script>
    function jumpTo(pageTo){
//FIXME not used
    document.forms['fmCRChTyp'].jumpToPage.value=pageTo;
    document.forms['fmCRChTyp'].jumpButton.click();
    }
  </script>
</head>

<body>

<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr>
    <td>

<FORM name="fmCRChTyp" action="" method="post">
            <table cellpading="1" cellspacing="0" border="0" align="center">
                <tr>
                    <td>&nbsp;</td>
                    <td><span class="fsection"><c:if test='${dataResource.subflowMode}'></c:if><spring:message code="jsp.olapDS.chooseType.title"/></span></td>
                </tr>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><spring:message code="jsp.olapDS.chooseType.selectType"/></td>
                </tr>
                <spring:bind path="dataResource.type">
                	<c:forEach items="${dataResource.allTypes}" var="type" varStatus="counter">
		                <tr>
		                    <td colspan="2">&nbsp;</td>
		                </tr>
                	    <tr>
                          <td>&nbsp;</td>
	                      <td><input type="radio" id="radio${counter.index}" onClick="document.forms['fmCRChTyp']._eventId_Next.click();" name="${status.expression}" <c:if test='${status.value==type || status.value==null}'>checked="true"</c:if>
	                        value="${type}" />&nbsp;<a href="javascript:document.forms['fmCRChTyp'].radio${counter.index}.click();" >${type}</a></td>
	                    </tr>
                	</c:forEach>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>
                        <input type="submit" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>">&nbsp;
                        <c:if test='${dataResource.mode!=1 && dataResource.mode!=2}'>
                            <input type="submit" class="fnormal" name="_eventId_Back" value="<spring:message code="button.back"/>">&nbsp;
                        </c:if>
                        <input type="submit" class="fnormal" name="_eventId_Next" value="<spring:message code="button.next"/>">
                    </td>
                </tr>
            </table>
            <input type="hidden" name="jumpToPage">
            <input type="submit" class="fnormal" style="visibility:hidden;" value="" name="_eventId_Jump" id="jumpButton">
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
</FORM>

    </td>
  </tr>
</table>

</body>

</html>
