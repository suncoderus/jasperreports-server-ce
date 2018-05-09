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
  --%><%--

    IMPORTANT: Do not remove the comments between the tags, they need to prevent unnecessary spaces in the output.

--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%--

--%><c:choose><%--
	--%><c:when test="${control.inputControl.mandatory
			and (control.inputControl.inputControlType == 2
				or control.inputControl.inputControlType == 3
				or control.inputControl.inputControlType == 4
				or control.inputControl.inputControlType == 6
				or control.inputControl.inputControlType == 7
				or control.inputControl.inputControlType == 8
				or control.inputControl.inputControlType == 9
				or control.inputControl.inputControlType == 10
				or control.inputControl.inputControlType == 11
				)}"><%--
        --%><spring:message code="jsp.defaultParametersForm.star.input.label" argumentSeparator="\\\\" arguments="${controlLabel}"/><%--
    --%></c:when><%--
	--%><c:otherwise><%--
		--%>${controlLabel}<%--
	--%></c:otherwise><%--
--%></c:choose>