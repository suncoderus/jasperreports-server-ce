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

<script id="attributesMain" type="template/mustache">
    <div id="noAttributes"><p class="message"><spring:message code="jsp.attributes.no.attributes"/></p></div>

   <div class="attributesTable">
    <ol class="list tabular">
        <li class="leaf">
            <div class="wrap header">
                <div class="column attrName">
                    <spring:message code="jsp.attributes.attribute.name"/>
                </div>
                <div class="column attrValue">
                    <spring:message code="jsp.attributes.attribute.value"/>
                </div>
                <div class="column attrAction">

                </div>
            </div>
        </li>
      </ol>
      <ol class="list tabular items"> </ol>
      <ol class="list tabular">
        <li id="newAttribute" class="leaf">
            <div class="wrap">
                <div class="column attrName textArea">
                    <textarea rows="1"></textarea>
                    <span class="message warning"></span>
                </div>
                <div class="column attrValue textArea">
                    <textarea rows="1"></textarea>
                    <span class="message warning"></span>
                </div>
                <div class="column attrAction">
                    <a class="launcher"><spring:message code="resource.lov.add"/> </a>
                </div>
            </div>
        </li>
    </ol>
 </div>
</script>

<script id="attributesItem" type="template/mustache">
        <div class="wrap">
            <div class="column attrName textArea">
                <textarea rows="1" readonly>{{name}}</textarea>
                <span class="message warning"></span>
            </div>
            <div class="column attrValue textArea">
                <textarea rows="1" readonly>{{value}}</textarea>
                <span class="message warning"></span>
            </div>
            <div class="column attrAction">
                <a class="launcher"><spring:message code="resource.lov.remove"/></a>
            </div>
        </div>
</script>

<script id="attributesSecureItem" type="template/mustache">
        <div class="wrap">
            <div class="column attrName textArea">
                <textarea rows="1" readonly>{{name}}</textarea>
                <span class="message warning"></span>
            </div>
            <div class="column attrValue input">
                <input type="password" readonly value="{{value}}"/>
                <span class="message warning"></span>
            </div>
            <div class="column attrAction">
                <a class="launcher"><spring:message code="resource.lov.remove"/></a>
            </div>
        </div>
</script>