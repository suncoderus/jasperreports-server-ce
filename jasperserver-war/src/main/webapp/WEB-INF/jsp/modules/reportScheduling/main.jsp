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
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
	<t:putAttribute name="pageTitle"><spring:message code="report.scheduling.list.title"/></t:putAttribute>
	<t:putAttribute name="bodyID" value="scheduler_jobSummary"/>
	<t:putAttribute name="bodyClass" value="oneColumn scheduler_jobSummary"/>

	<c:choose>
		<c:when test="${isPro}">
			<t:putAttribute name="moduleName" value="schedulerPro/schedulerMain"/>
		</c:when>
		<c:otherwise>
			<t:putAttribute name="moduleName" value="scheduler/schedulerMain"/>
		</c:otherwise>
	</c:choose>

	<t:putAttribute name="headerContent">

		<jsp:include page="../inputControls/commonInputControlsImports.jsp" />

		<js:out javaScriptEscape="true">
		<script type="text/javascript">
			__jrsConfigs__.usersTimeZone = "${timezone}";
			__jrsConfigs__.enableSaveToHostFS = "${enableSaveToHostFS}";
			__jrsConfigs__.reportJobEditorDefaults = JSON.parse('${reportJobDefaults}');
			__jrsConfigs__.availableReportJobOutputFormats = JSON.parse('${availableReportJobOutputFormats}');
			__jrsConfigs__.availableDashboardJobOutputFormats = JSON.parse('${availableDashboardJobOutputFormats}');
			__jrsConfigs__.VALUE_SUBSTITUTION = "<spring:message code="input.password.substitution"/>";

			__jrsConfigs__.timeZones = [];
            <c:forEach items="${userTimezones}" var="timezone">
                __jrsConfigs__.timeZones.push({value: "${timezone.code}", title: "<spring:message code="timezone.option" arguments='${timezone.code},${timezone.description}'/>"});
            </c:forEach>
		</script>
		</js:out>
	</t:putAttribute>

	<t:putAttribute name="bodyContent" >

		<div id="saveValues" class="panel dialog saveValues overlay moveable centered_horz centered_vert hidden" >
			<div  class="content hasFooter " >
				<div  class="header mover" >
					<div class="title">
						Save Values
					</div>
				</div>
				<div  class="body  " >
					<label class="control input text" accesskey="o" for="savedValuesName" title="This is the displayed name of the resource. It can be changed at any time.">
						<span class="wrap">The Name for the Saved Values (required):</span>
						<input class="" id="savedValuesName" type="text" value=""/>
						<span class="message warning">error message here</span>
					</label>
				</div>
				<div  class="footer " >
					<button id="saveAsBtnSave" class="button action primary up"><span class="wrap">Save<span class="icon"></span></button>
					<button id="saveAsBtnCancel" class="button action up"><span class="wrap">Cancel<span class="icon"></span></button>
				</div>
			</div>
		</div>

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
					   title="{{label}}. &#10;If your parameter supports relative date expressions, you can enter expressions like &apos;WEEK+1&apos; in this input control">
					<span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
					<input class="date"  type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
					<div class="warning">{{message}}</div>
				</label>
			</div>
		</script>

		<script id="singleValueDatetime" type="template/mustache">
			<div id="{{id}}" class="leaf">
				<label class="control picker"
					   title="{{label}}. &#10;If your parameter supports relative date expressions, you can enter expressions like &apos;WEEK+1&apos; in this input control">
					<span class="wrap">{{#mandatory}}* {{/mandatory}}{{label}}</span>
					<input class="date" type="text" value="" {{#readOnly}}disabled{{/readOnly}} />
					<div class="warning">{{message}}</div>
				</label>
			</div>
		</script>

		<script id="singleValueTime" type="template/mustache">
			<div id="{{id}}" class="leaf">
				<label class="control picker"
					   title="{{label}}. &#10;If your parameter supports relative date expressions, you can enter expressions like &apos;WEEK+1&apos; in this input control">
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
					<div class="ssPlaceholder" {{#readOnly}}disabled="disabled"{{/readOnly}}></div>
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
				<div class="sizer vertical hidden"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>
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
						<a href="#" name="multiSelectAll">All</a>
						<a href="#" name="multiSelectNone">None</a>
						<a href="#" name="multiSelectInverse">Inverse</a>
					</div>
				</fieldset>
				<span class="warning">{{message}}</span>
				<div class="resizeOverlay hidden"></div>
				<div class="sizer vertical hidden"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>
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

	</t:putAttribute>

</t:insertTemplate>
