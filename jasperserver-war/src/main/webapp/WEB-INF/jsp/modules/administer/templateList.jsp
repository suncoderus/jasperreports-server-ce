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

<li class="leaf last">
    <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
        <t:putAttribute name="containerClass" value="panel pane settings"/>
        <t:putAttribute name="containerTitle"><spring:message code='<%= oLabelCode %>' /></t:putAttribute>
        <t:putAttribute name="bodyClass" value="twoColumn"/>
        <t:putAttribute name="bodyContent">
            <div class="column simple primary">
            <label class="control input text" for="input_<%= oName %>" title="<spring:message code='<%= oLabelCode %>' />">
                <span class="wrap"><spring:message code="<%= oDesc %>"/></span>
                <select id="input_<%= oName %>" name="">
                 <%
                    for (String e: oSelectOptions ) { %>
                        <option value="<%= e %>" <%= e.equals(oValue) ? "selected" : "" %> ><%= e %></option>
                 <% } %>
                </select>
                <span class="message warning" id="error_<%= oName %>">&nbsp;</span>
            </label>
        </div>
        <div class="column simple secondary">
            <fieldset class="actions">
                <button id="save" name="<%= oName %>" disabled="true" class="button action primary up"><span class="wrap"><spring:message code="button.change"/></span></button>
                <button id="cancel" name="<%= oName %>" value="<%= oValue %>" disabled="true" class="button action up"><span class="wrap"><spring:message code="button.cancel"/></span></button>
            </fieldset>
        </div>
        </t:putAttribute>
    </t:insertTemplate>
</li>
