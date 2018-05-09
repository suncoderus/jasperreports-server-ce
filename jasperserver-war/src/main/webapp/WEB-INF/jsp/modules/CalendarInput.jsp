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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/spring" prefix="spring" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<% request.setAttribute("id", ((String) request.getAttribute("name")).replace(".", "_"));%>

<input type="text" name="${name}" id="${id}" value="${value}"
       onmousedown="cancelEventBubbling(event)"
       <c:if test="${not empty onchange}">onchange="${onchange}"</c:if>
       <c:if test="${readOnly}">disabled="disabled"</c:if>
        />
<c:if test="${not readOnly}">

    <script type="text/javascript">
        // ${sessionScope.XSS_NONCE} do not remove

        jQuery('#${id}').${(hasDate == true) ? 'date' : ''}${(hasTime == true) ? 'time' : ''}picker({
            <js:out javaScriptEscape="true">
            dateFormat:'${datePattern}',
            timeFormat:'${timePattern}',
            showSecond:'${showSecond}',
            </js:out>
            showOn:"button",
            buttonText:"",
            changeYear:true,
            changeMonth:true,
            showButtonPanel:true,
            onChangeMonthYear:null,
            beforeShow:jQuery.datepicker.movePickerRelativelyToTriggerIcon
        }).next().addClass('button').addClass('picker');
        // Prototype.js compatibility
        jQuery('#${id}')[0].getValue = function () {
            return jQuery(this).val()
        }
    </script>
</c:if>
