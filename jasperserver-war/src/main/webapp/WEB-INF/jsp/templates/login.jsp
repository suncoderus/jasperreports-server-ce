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
    Login dialog. Used only on log in page.

Usage:
    <t:insertTemplate template="/WEB-INF/jsp/templates/login.jsp">
        <t:putAttribute name="jsEdition" value="[REQUIRED]"/>
        <t:putAttribute name="allowUserPasswordChange" value="[REQUIRED]"/>
        <t:putAttribute name="errorMessages">
            [OPTIONAL]
        </t:putAttribute>
        <t:putAttribute name="localeOptions">
            [REQUIRED]
        </t:putAttribute>
        <t:putAttribute name="timezoneOptions">
            [REQUIRED]
        </t:putAttribute>
    </t:insertTemplate>
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<t:useAttribute name="jsEdition" id="jsEdition" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="allowUserPasswordChange" id="allowUserPasswordChange" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="showPasswordChange" id="showPasswordChange" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="warningMessages" id="warningMessages" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="errorMessages" id="errorMessages" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="localeOptions" id="localeOptions" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="timezoneOptions" id="timezoneOptions" classname="java.lang.String" ignore="false"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog inlay login ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="login"/>
    <t:putAttribute name="containerTitle"><spring:message code='jsp.Login.title'/></t:putAttribute>
    <t:putAttribute name="bodyContent">
        <div class="devices"></div>
        <div class="inputSection">
            ${warningMessages}
            ${errorMessages}

            <fieldset>
                <legend class="offLeft"><span><spring:message code='jsp.Login.section'/></span></legend>
                <c:if test="${jsEdition != 'community'}">
                    <label class="control input text hidden" accesskey="o" for="orgId">
                        <span class="wrap"></span><spring:message code='MT_ORGANIZATION'/>:</span>
                        <input id="orgId" name="orgId" type="text" autocapitalize="off"/>
                        <span class="message warning"></span>
                    </label>
                </c:if>
                <label class="control input text" for="j_username" id="usernamelabel">
                    <span class="wrap"><spring:message code='jsp.Login.username'/>:</span>
                </label>
                <input class="stdnavinitialfocus" id="j_username" name="j_username" type="text" autocapitalize="off" autofocus="autofocus" aria-labelledby="usernamelabel"/>
                <span class="message warning"></span>
                <label class="control input password" for="j_password_pseudo" id="passwordlabel">
                    <span class="wrap">
                        <c:choose>
                            <c:when test="${showPasswordChange eq 'true'}"><spring:message code='jsp.Login.password.old'/>:</c:when>
                            <c:otherwise><spring:message code='jsp.Login.password'/>:</c:otherwise>
                        </c:choose>
                    </span>
                </label>
                <input class="" id="j_password_pseudo" name="j_password_pseudo" type="password" maxlength="47" aria-labelledby="passwordlabel" role="application"/>
                <input class="" id="j_password" name="j_password" type="hidden" aria-hidden="true"/>
                <span class="message warning"></span>
            </fieldset>
            <fieldset>
                <legend><a id="showHideLocaleAndTimezone" tabindex="0"><spring:message code="jsp.Login.link.showLocale"/></a></legend>
                <div id="localeAndTimeZone" class="hidden">
                    <label class="control select" for="userLocale" id="userLocaleLabel">
                        <span class="wrap"><spring:message code='jsp.Login.locale'/>:</span>
                    </label>
                    <select id="userLocale" name="userLocale" aria-labelledby="userLocaleLabel" role="listbox">
                        ${localeOptions}
                    </select>
                    <label class="control select" for="userTimezone" id="userTimezoneLabel">
                        <spring:message code='jsp.Login.timezone'/>:</span>
                    </label>
                    <select id="userTimezone" name="userTimezone" aria-labelledby="userTimezoneLabel" role="listbox">
                        ${timezoneOptions}
                    </select>
                </div>
            </fieldset>
            <c:if test="${allowUserPasswordChange eq 'true'}">
                <script type="text/javascript" language="JavaScript">
                    var doesAllowUserPasswordChange = true;
                </script>
                <fieldset>
                    <legend><a id="showHideChangePassword" tabindex="0">
                        <c:choose>
                            <c:when test="${showPasswordChange eq 'true'}"><spring:message code="jsp.Login.link.cancelPassword"/></c:when>
                            <c:otherwise><spring:message code="jsp.Login.link.changePassword"/></c:otherwise>
                        </c:choose>
                    </a></legend>
                    <div id="changePassword" <c:if test="${not showPasswordChange eq 'true'}">class="hidden"</c:if>>
                        <label class="control input password" for="j_newpassword1">
                            <span class="wrap"><spring:message code='jsp.Login.link.newPassword'/>:</span>
                            <span class="message warning"></span>
                            <input id="j_newpassword1_pseudo" name="j_newpassword1_pseudo" type="password"/>
                            <input id="j_newpassword1" name="j_newpassword1" type="hidden"/>
                        </label>
                        <label class="control input password" for="j_newpassword2">
                            <span class="wrap"><spring:message code='jsp.Login.link.repeatNewPassword'/>:</span>
                            <span class="message warning"></span>
                            <input id="j_newpassword2_pseudo" name="j_newpassword2_pseudo" type="password"/>
                            <input id="j_newpassword2" name="j_newpassword2" type="hidden"/>
                        </label>
                        <input type="hidden" name="passwordExpiredDays"/>
                    </div>
                </fieldset>
            </c:if>
        </div>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
        <div class="inputSection">
            <button type="button" id="submitButton" class="button action primary up" disabled="disabled">
                <span class="wrap"><spring:message code='jsp.Login.button.login'/></span>
                <span class="icon"></span>
            </button>
            <h2><a id="needHelp" tabindex="0"><spring:message code='LOGIN_NEED_HELP_LINK'/></a></h2>
            <c:if test="${isProVersion and isEC2}">
                <div id="amazonLogo" class="row"></div>
            </c:if>
            <!--
            <div class="cosmetic left"></div>
            <div class="cosmetic right"></div>
            -->
        </div>
    </t:putAttribute>
</t:insertTemplate>

