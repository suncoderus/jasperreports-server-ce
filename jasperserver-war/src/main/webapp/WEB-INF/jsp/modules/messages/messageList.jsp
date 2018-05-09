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
<%@ taglib uri="/spring" prefix="spring"%>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="messages.messageList.page.title"/></t:putAttribute>
    <t:putAttribute name="bodyID" value="messages"/>
    <t:putAttribute name="bodyClass" value="oneColumn"/>
    <t:putAttribute name="moduleName" value="messages/list/messageListMain"/>

    <t:putAttribute name="headerContent" >

        <script type="text/javascript">

            __jrsConfigs__.messagesListInitOptions = {
                flowExecutionKey: "${flowExecutionKey}",
                systemConfirmMessages: {
                    "markAsRead": "<spring:message code="messages.messageList.systemConfirm.markAsRead"/>",
                    "markAsUnread": "<spring:message code="messages.messageList.systemConfirm.markAsUnread"/>",
                    "delete": "<spring:message code="messages.messageList.systemConfirm.delete"/>"
                }
            };

        </script>

    </t:putAttribute>
    <t:putAttribute name="bodyContent" >

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="column decorated primary showingToolBar"/>
            <t:putAttribute name="containerTitle"><spring:message code="messages.messageList.header.title"/>:</t:putAttribute>
            <t:putAttribute name="headerContent">
                <label class="control select inline" for="messageFilter" title="<spring:message code="messages.messageList.filter.title"/>">
                    <span class="wrap offLeft"><spring:message code="messages.messageList.filter.label"/></span>
                    <select id="messageFilter" name="messageFilter">
                        <option selected="selected" value="ALL" <c:if test="${messageFilter == 'ALL'}">selected</c:if>><spring:message code="messages.messageList.filter.all"/></option>
                        <option value="UNREAD" <c:if test="${messageFilter == 'UNREAD'}">selected</c:if>><spring:message code="messages.messageList.filter.unread"/></option>
                    </select>
                    <span class="message warning"></span>
                </label>

                <div id="toolbar" class="toolbar">
                    <ul class="list buttonSet">
						<li class="node open">
							<ul class="list buttonSet">
								<li class="leaf"><button id="markAsRead" class="button capsule text up first"><span class="wrap"><spring:message code="messages.messageList.toolbar.button.markAsRead"/></span><span class="icon"></span></button></li>
								<li class="leaf"><button id="markAsUnread" class="button capsule text up last"><span class="wrap"><spring:message code="messages.messageList.toolbar.button.markAsUnread"/></span><span class="icon"></span></button></li>
								<li class="leaf"><button id="delete" class="button capsule text up"><span class="wrap"><spring:message code="messages.messageList.toolbar.button.delete"/></span></button></li>
							</ul>
						</li>
                    </ul>
                </div>
            </t:putAttribute>
            <t:putAttribute name="bodyContent">
                <ol id="messageList" class=""></ol>
            </t:putAttribute>
        </t:insertTemplate>
    </t:putAttribute>
</t:insertTemplate>
