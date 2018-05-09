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

<%@ taglib prefix="spring" uri="/spring" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">
    // ${sessionScope.XSS_NONCE} don't remove
    __jrsConfigs__.isEncryptionOn=${isEncryptionOn};

    __jrsConfigs__.loginState = {
        showLocaleMessage: '<spring:message code="jsp.Login.link.showLocale" javaScriptEscape="true"/>',
        hideLocaleMessage: '<spring:message code="jsp.Login.link.hideLocale" javaScriptEscape="true"/>',
        allowUserPasswordChange: ${allowUserPasswordChange},
        changePasswordMessage: '<spring:message code="jsp.Login.link.changePassword" javaScriptEscape="true"/>',
        cancelPasswordMessage: '<spring:message code="jsp.Login.link.cancelPassword" javaScriptEscape="true"/>',
        passwordExpirationInDays: ${passwordExpirationInDays},
        nonEmptyPasswordMessage: '<spring:message code="jsp.Login.link.nonEmptyPassword" javaScriptEscape="true"/>',
        passwordNotMatchMessage: '<spring:message code="jsp.Login.link.passwordNotMatch" javaScriptEscape="true"/>'
    };

</script>
</js:out>
       
