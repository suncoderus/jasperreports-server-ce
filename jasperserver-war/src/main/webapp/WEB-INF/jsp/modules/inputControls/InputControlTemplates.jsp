<%@ taglib uri="/spring" prefix="spring"%>

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

<script id="bool" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <label class="control input checkBox" for="{{uuid}}" title="{{description}}">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <input  type="checkbox" {{#readOnly}}disabled{{/readOnly}} />
        </label>
    </div>
</script>

<script id="singleValueText" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <label class="control input text"  title="{{description}}">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <input class=""  type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
            <span class="warning">{{message}}</span>
        </label>
    </div>
</script>

<script id="singleValueNumber" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <label class="control input text"  title="{{description}}">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <input class=""  type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
            <span class="warning">{{message}}</span>
        </label>
    </div>
</script>

<script id="singleValueDate" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <label class="control picker"
               title="{{label}}. &#10;<spring:message code="DATE_IC_TOOLTIP" javaScriptEscape="true"/>">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <input class="date"  type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
            <div class="warning">{{message}}</div>
        </label>
    </div>
</script>

<script id="singleValueDatetime" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <label class="control picker"
               title="{{label}}. &#10;<spring:message code="DATE_IC_TOOLTIP" javaScriptEscape="true"/>">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <input class="date" type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
            <div class="warning">{{message}}</div>
        </label>
    </div>
</script>

<script id="singleValueTime" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <label class="control picker"
               title="{{label}}. &#10;<spring:message code="DATE_IC_TOOLTIP" javaScriptEscape="true"/>">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <input class="date" type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
            <div class="warning">{{message}}</div>
        </label>
    </div>
</script>

<script id="singleSelect" type="template/mustache">
    <div id="{{id}}" class="leaf visibleOverflow">
        <label class="control select"  title="{{description}}">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <div class="ssPlaceholder jr jr-mInput {{#readOnly}} jr-isDisabled{{/readOnly}}" {{#readOnly}}disabled="disabled"{{/readOnly}}></div>
            <span class="warning">{{message}}</span>
        </label>
    </div>
</script>

<script id="multiSelect" type="template/mustache">
    <div class="leaf visibleOverflow" id="{{id}}">
        <div class="control select multiple"  title="{{description}}">
            <span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
            <div class="msPlaceholder jr {{#readOnly}} jr-mInput jr-isDisabled {{/readOnly}}" {{#readOnly}}disabled="disabled"{{/readOnly}}></div>
            <span class="warning">{{message}}</span>
            <div class="resizeOverlay hidden"></div>
        </div>
    </div>
</script>

<script id="singleSelectRadio" type="template/mustache">
    <div id="{{id}}" class="leaf">
        <fieldset class="control select multiple radio" title="{{description}}">
            <legend>{{#mandatory}}* {{/mandatory}}{{label}}</legend>
            <ul class="list inputSet">
                {{#data}}
                <li>
                    <div class="control radio">
                        <input {{#selected}}checked="checked"{{/selected}} class="" id="{{uuid}}" type="radio" name="{{name}}" {{#readOnly}}disabled="disabled"{{/readOnly}} value="{{value}}"/>
                        <label class="wrap" for="{{uuid}}" title="{{description}}">
                            {{label}}&nbsp;
                        </label>
                    </div>
                </li>
                {{/data}}
            </ul>
        </fieldset>
        <span class="warning">{{message}}</span>
        <div class="resizeOverlay hidden"></div>
        <div class="jr-mSizer jr-mSizerVertical jr hidden"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>
    </div>
</script>

<script id="multiSelectCheckbox" type="template/mustache">
    <div id="{{id}}" class="leaf visibleOverflow">
        <fieldset class="control select checkbox" title="{{description}}">
            <legend>{{#mandatory}}* {{/mandatory}}{{label}}</legend>
            <ul class="list inputSet">
                {{#data}}
                    <li>
                        <div class="control checkBox">
                            <label class="wrap" for="{{uuid}}" title="{{description}}">
                                {{label}}&nbsp;
                            </label>
                            <input {{#selected}}checked="checked"{{/selected}} class="" id="{{uuid}}" type="checkbox" {{#readOnly}}disabled="disabled"{{/readOnly}} value="{{value}}"/>
                        </div>
                    </li>
                {{/data}}
            </ul>
	        <div class="{{#readOnly}}hidden{{/readOnly}}">
                <a href="#" name="multiSelectAll"><spring:message code="button.select.all" javaScriptEscape="true"/></a>
                <a href="#" name="multiSelectNone"><spring:message code="button.select.none" javaScriptEscape="true"/></a>
                <a href="#" name="multiSelectInverse"><spring:message code="button.select.inverse" javaScriptEscape="true"/></a>
            </div>
        </fieldset>
        <span class="warning">{{message}}</span>
        <div class="resizeOverlay hidden"></div>
        <div class="jr-mSizer jr-mSizerVertical jr hidden"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>
    </div>
</script>

<script id="reportOptions" type="template/mustache">
    <div>
        <label class="control select" for="reportOptionsSelect" title="{{title}}">
            <span class="wrap">{{label}}</span>
            <select id="reportOptionsSelect">
                {{#data}}
                    <option value="{{id}}" {{#selected}}selected="selected"{{/selected}}>{{label}}</option>
                {{/data}}
            </select>
        </label>
    </div>
</script>

<!-- ${sessionScope.XSS_NONCE} do not remove -->


