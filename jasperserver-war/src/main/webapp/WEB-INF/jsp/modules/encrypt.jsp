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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="com.jaspersoft.jasperserver.api.security.SecurityConfiguration" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="moduleName" value="encrypt/encryptMain"/>
    <t:putAttribute name="pageTitle" value="Jaspersoft Encryption Tool"/>
	    <t:putAttribute name="headerContent">

        <script>
        	var isEncryptionOn = <%= SecurityConfiguration.isEncryptionOn() %>;
        </script>
    </t:putAttribute>
    <t:putAttribute name="bodyID" value="encrypt"/>
    <t:putAttribute name="bodyClass" value="oneColumn"/>

		<t:putAttribute name="bodyContent" >

			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    	<t:putAttribute name="containerClass" value="column decorated primary"/>
			    <t:putAttribute name="containerTitle">Encryption Tool</t:putAttribute>
			    <t:putAttribute name="bodyContent">

					<form name="encryptForm" method="post" action="">
					<table align="center">

					<tr><td width="20">&nbsp;&nbsp;</td><td align="right">&nbsp;</td><td>&nbsp;&nbsp;</td><td>&nbsp;</td></tr>

					<tr>
					<td>&nbsp;&nbsp;</td><td width="150"><nobr>1. Enter text to encrypt:</nobr></td><td>&nbsp;&nbsp;</td>
					<td><input id="text1" name="text1" type="text" size="50" value="joeuser"></td>
					</tr>
					<tr>
					<td>&nbsp;&nbsp;</td><td><nobr>2. Copy encrypted text:</nobr></td><td>&nbsp;&nbsp;</td>
					<td><input id="text2" name="text2" type="text" size="150" value="" disabled></td>
					</tr>

					<tr><td>&nbsp;&nbsp;</td><td>&nbsp;</td><td>&nbsp;&nbsp;</td><td>&nbsp;</td></tr>

					<tr><td>&nbsp;&nbsp;</td><td>&nbsp;</td><td>&nbsp;&nbsp;</td><td>
					<button type="button" id="submitButton" class="button action primary up">
					<span class="wrap">Encrypt</span>
					<span class="icon"></span>
					</button>
					<button type="button" id="clearButton" class="button action primary up">
					<span class="wrap">Clear</span>
					<span class="icon"></span>
					</button>
					</td>
					</tr>

					</table>
					</form>

			    </t:putAttribute>
			</t:insertTemplate>

	    </t:putAttribute>

</t:insertTemplate>
