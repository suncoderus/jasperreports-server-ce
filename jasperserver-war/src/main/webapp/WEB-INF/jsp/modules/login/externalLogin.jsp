<%@ page import="com.jaspersoft.jasperserver.api.security.externalAuth.ExternalAuthProperties" %>
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
  <%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

   <%
       //force Ajax to reload the page during error handling in core.ajax.js
       response.setHeader("LoginRequested","true");

       //pass external login url to the page
       String externalLoginUrl = "login.html";
       if (request.getAttribute("externalAuthPropertiesBean") != null) {
           final ExternalAuthProperties externalAuthPropertiesBean = (ExternalAuthProperties)request.getAttribute("externalAuthPropertiesBean");
           final StringBuffer requestURL = request.getRequestURL();
           final String authProcessingUrl = requestURL.substring(0, requestURL.indexOf(request.getContextPath()) + request.getContextPath().length()) + externalAuthPropertiesBean.getAuthenticationProcessingUrl();
           externalLoginUrl = externalAuthPropertiesBean.getExternalLoginUrl() + "?" + externalAuthPropertiesBean.getServiceParameterName() + "=" + authProcessingUrl;
       }

       request.setAttribute("externalLoginUrl", externalLoginUrl);
   %>
  <%-- This allows us to avoid crossdomain redirects from ajax --%>
  <c:if test="${not empty externalLoginUrl}">
      <js:out javaScriptEscape="true">
      <script type="text/javascript">
          // ${sessionScope.XSS_NONCE} do not remove

          location='${externalLoginUrl}';
      </script>
      </js:out>
  </c:if>

