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

  <title><spring:message code="jsp.olapDS.jdbcPropsForm.title"/></title>
<c:if test='${dataResource.subflowMode}'>
  <meta name="pageHeading" content="<c:choose><c:when test='${dataResource.subEditMode}'><spring:message code="jsp.olapDS.pageHeading_edit"/></c:when><c:otherwise><spring:message code="jsp.olapDS.pageHeading_new"/></c:otherwise></c:choose>"/>
</c:if>
  <meta http-equiv="Content-Type" content="text/html; charset=${requestScope['com.jaspersoft.ji.characterEncoding']}">
  <script>
    function jumpTo(pageTo){
//FIXME not used
    document.forms['fmCRValidConf'].jumpToPage.value=pageTo;
    document.forms['fmCRValidConf'].jumpButton.click();
    }
  </script>
</head>

<body>

<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr>
    <td>

<form name="fmCRValidConf" action="" method="post">
            <table border="0" cellpadding="1" cellspacing="0" align="center">
                <tr>
                    <td>&nbsp;</td>
                    <td><span class="fsection"><c:if test='${dataResource.subflowMode}'></c:if><spring:message code="jsp.olapDS.jdbcPropsForm.title"/></span></td>
                </tr>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
                <spring:bind path="dataResource.olapDataSource.name">
                    <tr>
                        <td align="right">* <spring:message code="label.name"/>&nbsp;</td>
                        <td><input maxlength="100" name="${status.expression}" type="text" class="fnormal" size="40" <c:if test='${dataResource.aloneEditMode}'>disabled="true"</c:if> value="${status.value}"></td>
                    </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.olapDataSource.label">
                    <tr align="center">
                        <td align="right">* <spring:message code="label.label"/>&nbsp;</td>
                        <td align="left"><input maxlength="100" name="${status.expression}" type="text" class="fnormal" size="40" value="${status.value}"></td>
                    </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.olapDataSource.description">
                    <tr align="center">
                        <td align="right"><spring:message code="label.description"/>&nbsp;</td>
                        <td align="left"><input maxlength="100" name="${status.expression}" type="text" class="fnormal" size="40" value="${status.value}"></td>
                    </tr>
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
                <spring:bind path="dataResource.olapDataSource.driverClass">
                <tr>
                    <td align="right">* <spring:message code="jsp.olapDS.jdbcPropsForm.driver"/>&nbsp;</td>
                    <td><input maxlength="100" name="${status.expression}" type="text" class="fnormal" size="40" value="${status.value}">&nbsp;<spring:message code="JS_sample.jdbc.driver"/></td>
                </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.olapDataSource.connectionUrl">
                <tr>
                    <td align="right">* <spring:message code="jsp.olapDS.jdbcPropsForm.url"/>&nbsp;</td>
                    <td><input maxlength="100" name="${status.expression}" type="text" class="fnormal" size="40" value="${status.value}">&nbsp;<spring:message code="JS_sample.jdbc.url"/></td>
                </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.olapDataSource.username">
                <tr>
                    <td align="right">* <spring:message code="jsp.olapDS.jdbcPropsForm.username"/>&nbsp;</td>
                    <td><input maxlength="100" name="${status.expression}" type="text" class="fnormal" size="40" value="${status.value}"></td>
                </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.olapDataSource.password">
                <tr>
                    <td align="right">* <spring:message code="jsp.olapDS.jdbcPropsForm.password"/>&nbsp;</td>
                    <td><input maxlength="100" name="${status.expression}" type="password" class="fnormal" size="40" value="${status.value}"></td>
                </tr>
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
                    <td align="left">
                      <input type="submit" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>"/>&nbsp;
                    <c:if test="${!dataResource.aloneEditMode}">
                        <input type="submit" class="fnormal" name="_eventId_Back" value="<spring:message code="button.back"/>">&nbsp;
                    </c:if>
                    <c:choose>
                        <c:when test='${dataResource.subflowMode || dataResource.mode==0}'>
                            <input type="submit" class="fnormal" name="_eventId_Next" value="<spring:message code="button.next"/>">
                        </c:when>
                        <c:otherwise>
                            <input type="submit" class="fnormal" name="_eventId_Save" value="<spring:message code="button.save"/>">
                        </c:otherwise>
                    </c:choose>
                    </td>
                </tr>
            </table>
            <input type="hidden" name="jumpToPage">
            <input type="submit" class="fnormal" style="visibility:hidden;" value="" name="_eventId_Jump" id="jumpButton">
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
</FORM>

    </td>
  </tr>
</table>

</body>

</html>
