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
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
 <decorator:usePage id="main" />
 <html>
 <head>
 <title>Raz's Site -
    <decorator:title default="<spring:message code='decorators.metadata.welcome'/>" />
 </title>
 <decorator:head />
 </head>

 <body>
 <!-- ${sessionScope.XSS_NONCE} do not remove -->
 <h1><decorator:title default="<spring:message code='decorators.metadata.welcome'/>" /></h1>
 <h3>
 <a href="mailto:<decorator:getProperty property="meta.author"
            default="staff@example.com" />">
 <decorator:getProperty property="meta.author"
              default="<spring:message code='decorators.metadata.metaAuthor'/>" />
 </a></h3><hr />
 <decorator:body />
 <p><small>
    (<a href="?printable=true">printable version</a>)
    </small>
 </p>
 </body>
 </html>


