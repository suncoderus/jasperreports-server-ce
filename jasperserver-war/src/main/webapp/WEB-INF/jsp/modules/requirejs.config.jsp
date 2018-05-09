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
<%@ page contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Map"%>
<%
    Map<String, String> modulePaths = (Map<String, String>)request.getAttribute("modulePaths");
%>
requirejs.config({
    paths: {
<%
    Set<String> keys = modulePaths.keySet();
    int i=0, ln = keys.size();
    for (String key: keys) {
%>      "<%=key%>": "<%=modulePaths.get(key)%>"<c:if test="<%=i<ln-1%>">,</c:if>
<%
        i++;
    }
%>  }
});
