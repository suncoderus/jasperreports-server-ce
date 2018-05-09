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

<%-- tempates for list (dynamicList.List component) --%>
<div id="listTemplateHolder" style="display:none">

<!-- default template for flat lists-->
<ul id="defaultListTemplate" class="list" tabindex="0" js-navtype="dynamiclist">
    <!-- default template for items of flat lists -->
    <li js-navtype="dynamiclist" id="dynamicListItemTemplate" class="leaf"><p class="wrap button "></p></li>

</ul>

<!-- Template used for filter path in search -->
<ul id="list_control_path" class="list control path" tabindex="0" js-navtype="dynamiclist">
    <!-- Template for label of the path
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="list_control_path:label" class="label"><p class="wrap button "></p></li>
    <!-- Template for items of the path
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="list_control_path:step" class="step"><p class="wrap button "></p></li>
</ul>

<!-- Template used for sorters in search -->
<!-- TODO FIXME: Keyboard support is temporarily disabled here due to a race condition in the sorter that CAN happen without the accessibility extensions,
using only the mouse, but happens really easily using the keyboard. -->
<ul id="tabSet_control_horizontal_responsive" class="list tabSet text control horizontal" tabindex="-1" js-navtype="none" js-stdnav="false">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabSet_control_horizontal_responsive:label" class="label"><p class="wrap"></p></li>
    <!-- Template for sort option
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabSet_control_horizontal_responsive:tab" class="tab"><p class="wrap button "></p></li>
</ul>

<!-- Template used for list of resources in search -->
<ul id="tabular_fourColumn_resources" class="list collapsible tabular resources fourColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * leaf - default item.
            * node - if has children resources (e.g. Report Options).
            * open - if resource has children and they showed
            * closed - if resource has children and they hidden
            * selected - if item is selected in the list
            * scheduled - if resource is scheduled
    -->
    <li js-navtype="dynamiclist" id="tabular_fourColumn_resources:leaf" class="resources">
        <div class="wrap button draggable">
            <div class="column one">
                <div class="scheduled icon button"></div>
                <div class="separator"></div>
                <div class="disclosure icon button"></div>
            </div>
            <div class="column two">
                <p class="resourceName"><a></a></p>
                <p class="resourceDescription"></p>
            </div>
            <div class="column three resourceType"></div>
            <div class="column four">
                <p class="createdDate"></p>
                <p class="modifiedDate"></p>
            </div>
        </div>
    </li>
</ul>

<!-- Template used for list of resources in search -->
<ul id="tabular_fourColumn_resources_sublist" class="list collapsible tabular resources fourColumn sublist" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * leaf - default item.
            * node - if has children resources (e.g. Report Options).
            * open - if resource has children and they showed
            * closed - if resource has children and they hidden
            * selected - if item is selected in the list
            * scheduled - if resource is scheduled
    -->
    <li js-navtype="dynamiclist" id="tabular_fourColumn_resources_sublist:leaf" class="resources">
        <div class="wrap button draggable">
            <div class="column one">
                <div class="scheduled icon button">&nbsp;</div>
                <div class="separator"></div>
                <div class="disclosure icon button">&nbsp;</div>
            </div>
            <div class="column two">
                <p class="resourceName"><a></a></p>
                <p class="resourceDescription"></p>
            </div>
            <div class="column three resourceType"></div>
            <div class="column four">
                <p class="createdDate"></p>
                <p class="modifiedDate"></p>
            </div>
        </div>
    </li>
</ul>

<!-- Template used for list of users in user management -->
<ol id="tabular_threeColumn" class="list tabular collapsible threeColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabular_threeColumn:leaf" class="leaf">
        <div class="wrap button ">
            <div class="column one">
                <p class="ID"><a></a></p>
            </div>
            <div class="column two">
                <p class="name"></p>
            </div>
            <div class="column three">
                <p class="organization"></p>
            </div>
        </div>
    </li>
</ol>

<!-- Template used for list of users in user management -->
<ol id="tabular_twoColumn" class="list tabular collapsible twoColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabular_twoColumn:leaf" class="leaf">
        <div class="wrap button ">
            <div class="column one">
                <p class="ID"><a></a></p>
            </div>
            <div class="column two">
                <p class="name"></p>
            </div>
        </div>
    </li>
</ol>

<!-- Template used for list of dependet reports in ad hoc designer -->
<ol id="tabular_oneColumn" class="list tabular oneColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabular_oneColumn:leaf" class="leaf">
        <div class="wrap">
            <div class="column one">
                <p class="uri"></p>
            </div>
        </div>
    </li>
</ol>

<!-- Template used for list of role in role management -->
<ol id="tabular_twoColumn:roles" class="list tabular twoColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabular_twoColumn:roles:leaf" class="leaf">
        <div class="wrap button ">
            <div class="column one">
                <p class="ID"></p>
            </div>
            <div class="column two">
                <p class="organization"></p>
            </div>
        </div>
    </li>
</ol>

<!-- Template used for list of role in role management -->
<ol id="tabular_oneColumn:roles" class="list tabular collapsible oneColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource item in the list
        States (possible class name changes) :
            * first - if item is first in the list
            * last - if item is last in the list
            * selected - if item is selected in the list
    -->
    <li js-navtype="dynamiclist" id="tabular_oneColumn:roles:leaf" class="leaf">
        <div class="wrap button ">
            <div class="column one">
                <p class="ID"></p>
            </div>
        </div>
    </li>
</ol>

<!-- Template used for list of resource children list in search (e.g Report Options)-->
<ol id="tabular_twoColumn_resources" class="list tabular resources twoColumn" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of resource child item in the list
        States (possible class name changes) :
            * selected - if item is selected in the list
            * scheduled - if resource is scheduled
    -->
    <li js-navtype="dynamiclist" id="tabular_twoColumn_resources:leaf" class="resources leaf">
        <div class="wrap button ">
            <div class="column one">
                <div class="scheduled icon button"></div>
            </div>
            <div class="column two">
                <p class="resourceName"><a></a></p>
                <p class="resourceDescription"></p>
            </div>
        </div>
    </li>
</ol>


<!-- Template used for list of permissions for resource-->
<ul id="tabular_twoColumn_setLeft" class="list setLeft tabular twoColumn" tabindex="0" js-navtype="dynamiclist">
<!--
    Template of permission item for user or role
    -->
    <li js-navtype="dynamiclist" id="tabular_twoColumn_setLeft:leaf" class="leaf">
        <div class="wrap"><b class="icon"></b>
            <p class="column one"><a class="launcher"></a></p>
            <p class="column two">
                <select>
                </select>
            </p>
        </div>
    </li>
    <li js-navtype="dynamiclist" id="tabular_twoColumn_setLeft:loading" class="leaf loading">
        <div class="wrap"><b class="icon"></b>
            <p class="column one"><a class="launcher"></a></p>
            <p class="column two">
            </p>
        </div>
    </li>
</ul>

<%--Template used for custom url prompt in dashboard--%>
<ul id="inputControls_option_list" class="list setLeft tabular twoColumn" tabindex="0" js-navtype="dynamiclist">
    <li js-navtype="dynamiclist" id="inputControls_option_list:header" class="leaf">
        <div class="wrap header">
            <b class="icon" title=""></b>
            <p class="column one">
                <span class="label"><spring:message code="ADH_774_INPUT_CONTROL"/></span>
            </p>
            <p class="column two">
                <span class="label"><spring:message code="ADH_775_URL_PARAMETER_NAME"/></span>
            </p>
        </div>
    </li>

    <%--for each control--%>
    <li js-navtype="dynamiclist" id="inputControls_option_list:control" class="leaf">
        <div class="wrap"><b class="icon" title=""></b>
            <div class="column one">
                <div class="control checkBox">
                    <label id="label_for_param_1" class="wrap" for="parameter_1"></label>
                    <input id="parameter_1" type="checkbox" value=""/>
                </div>
            </div>
            <div class="column two">
                <label id="label_for_param_1_value" class="control input text" for="parameter_1_value">
                    <input id="parameter_1_value" type="text" value="Country"/>
                </label>
            </div>
        </div>
    </li>
</ul>

<ul id="list_responsive_filters" class="list filters" tabindex="0" js-navtype="dynamiclist">
    <%-- used by search for items in the filter list --%>
    <li js-navtype="dynamiclist" id="list_responsive_filters:leaf" class="leaf">
        <p class="wrap button"><b class="icon"></b></p>
    </li>

    <%-- used by search for more/fewer disclosure item in filters list --%>
    <li js-navtype="dynamiclist" id="list_responsive_filters:node" class="node closed">
        <p class="wrap button separator"><b class="icon"></b>
            <a class="more launcher"><spring:message code="SEARCH_MORE" javaScriptEscape="true"/></a>
            <a class="fewer launcher"><spring:message code="SEARCH_FEWER" javaScriptEscape="true"/></a>
        </p>
    </li>

    <%-- used by search for more/fewer disclosure item in filters list --%>
    <li js-navtype="dynamiclist" id="list_responsive_filters:separator" class="leaf">
        <p class="wrap button"><b class="icon"></b></p>
    </li>
</ul>

<ul id="list_domain_bundles" tabindex="0" js-navtype="dynamiclist">
    <li js-navtype="dynamiclist" id="list_domain_bundles:leaf" class="leaf">
        <div class="wrap button "><b class="icon" title=""></b>
            <p class="column one"><a class="emphasis"></a></p>
            <p class="column two"><a class="launcher"></a> | <a class="launcher"></a></p>
        </div>
    </li>
</ul>

<ul id="list_fourColumn_type_message" class="list tabular type_messages fourColumn" tabindex="0" js-navtype="dynamiclist">
    <li js-navtype="dynamiclist" id="list_fourColumn_type_message:header" class="leaf">
        <div class="wrap header type_messages">
            <div class="column one">
                <p class="subject" title="<spring:message code="messages.messageList.message.subject.title" javaScriptEscape="true"/>"><spring:message code="messages.messageList.message.header.subject" javaScriptEscape="true"/></p>
            </div>
            <div class="column two">
                <p class="date" title="<spring:message code="messages.messageList.message.date.title" javaScriptEscape="true"/>"><spring:message code="messages.messageList.message.header.date" javaScriptEscape="true"/></p>
            </div>
            <div class="column three">
                <p class="type" title="<spring:message code="messages.messageList.message.type.title" javaScriptEscape="true"/>"><spring:message code="messages.messageList.message.header.type" javaScriptEscape="true"/></p>
            </div>
            <div class="column four">
                <p class="component" title="<spring:message code="messages.messageList.message.component.title" javaScriptEscape="true"/>"><spring:message code="messages.messageList.message.header.component" javaScriptEscape="true"/></p>
            </div>
        </div>
    </li>
    <li js-navtype="dynamiclist" id="list_fourColumn_type_message:unread" class="leaf">
        <div class="wrap button type_messages unread">
            <div class="column one">
                <p class="subject" title="<spring:message code="messages.messageList.message.subject.title" javaScriptEscape="true"/>"><a></a></p>
            </div>
            <div class="column two">
                <p class="date" title=""></p>
            </div>
            <div class="column three">
                <p class="type" title="<spring:message code="messages.messageList.message.type.title" javaScriptEscape="true"/>"></p>
            </div>
            <div class="column four">
                <p class="component" title="<spring:message code="messages.messageList.message.component.title" javaScriptEscape="true"/>"></p>
            </div>
        </div>
    </li>
    <li js-navtype="dynamiclist" id="list_fourColumn_type_message:read" class="leaf">
        <div class="wrap button type_messages">
            <div class="column one">
                <p class="subject" title="<spring:message code="messages.messageList.message.subject.title" javaScriptEscape="true"/>"><a></a></p>
            </div>
            <div class="column two">
                <p class="date" title=""></p>
            </div>
            <div class="column three">
                <p class="type" title="<spring:message code="messages.messageList.message.type.title" javaScriptEscape="true"/>"></p>
            </div>
            <div class="column four">
                <p class="component" title="<spring:message code="messages.messageList.message.component.title" javaScriptEscape="true"/>"></p>
            </div>
        </div>
    </li>
</ul>

<%-- List of assigned users in role management--%>
<ul id="list_responsive_fields" class="list fields" tabindex="0" js-navtype="dynamiclist">
    <li js-navtype="dynamiclist" id="list_responsive_fields:leaf" class="leaf"><a href="list.jsp#" class="wrap button "><b class="icon"></b></a></li>
</ul>

<ul id="list_type_attributes" class="list type_attributes" js-stdnav="false" tabindex="-1">
    <li js-navtype="dynamiclist" id="list_type_attributes:role" class="leaf"><a href="list.jsp#" class="wrap button launcher"></a><span class="separator">,</span></li>
    <li js-navtype="dynamiclist" id="list_type_attributes:role:last" class="leaf"><a href="list.jsp#" class="wrap button launcher"></a></li>
    <li js-navtype="dynamiclist" id="list_type_attributes:user" class="leaf"><a href="list.jsp#" class="wrap button launcher"></a>,&nbsp;</li>
    <li js-navtype="dynamiclist" id="list_type_attributes:user:last" class="leaf"><a href="list.jsp#" class="wrap button launcher"></a></li>
    <li js-navtype="dynamiclist" id="list_type_attributes:profileAttribute" class="leaf">,&nbsp;</li>
    <li js-navtype="dynamiclist" id="list_type_attributes:profileAttribute:last" class="leaf"></li>
</ul>

<ul id="list_domain_chooser_filter" class="list tabular filters twoColumn" tabindex="0" js-navtype="dynamiclist">
    <li js-navtype="dynamiclist" id="list_domain_chooser_filter:leaf" class="leaf">
        <div class="wrap readonly">
            <div class="column one condition">
                <span class="fieldName"></span>
                <span class="operation"></span>
                <span class="value"></span>
            </div>
            <div class="column two">
                <span class="lock"></span>
                    <span class="actions">
                        <a class="launcher button change"><spring:message code="page.preFilters.filter.change" javaScriptEscape="true"/></a> |
                        <a class="launcher button remove"><spring:message code="page.preFilters.filter.remove" javaScriptEscape="true"/></a>
                    </span>
            </div>
        </div>
    </li>
</ul>

<ul id="list_domain_designer_joins" class="list tabular fourColumn" tabindex="0" js-navtype="dynamiclist">
    <li js-navtype="dynamiclist" id="list_domain_designer_joins:leaf" class="leaf">
        <div class="wrap"><b class="icon"></b>
            <p class="column one"></p>
            <p class="column two"></p>
            <p class="column three">
                <select>
                    <option selected="selected" value="inner"><spring:message code="select.options.joins.inner" javaScriptEscape="true"/></option>
                    <option value="leftOuter"><spring:message code="select.options.joins.left" javaScriptEscape="true"/></option>
                    <option value="rightOuter"><spring:message code="select.options.joins.right" javaScriptEscape="true"/></option>
                    <option value="fullOuter"><spring:message code="select.options.joins.full" javaScriptEscape="true"/></option>
                </select>
            </p>
            <p class="column four"><a class="launcher button"><spring:message code="domain.designer.joins.remove" javaScriptEscape="true"/></a></p>
        </div>
    </li>
</ul>

<%-- Template for list of sort fields in adhoc sort dialog --%>
<ul id="list_fields_hideRoot_column_simple" class="list collapsible fields hideRoot column simple" tabindex="0" js-navtype="dynamiclist">
    <!--
        Template of sort fild
        States (possible class name changes) :
            * ascending - asc sorting by this filed
            * descending - desc sorting by this field
    -->
    <li js-navtype="dynamiclist" id="list_fields_hideRoot_column_simple:leaf" class="leaf ascending">
        <div class="wrap button draggable"><b class="icon button"></b></div>
    </li>
</ul>

</div>

<%-- tempates for complex hierarchical lists (dynamicTree.TreeSupport and dynamicTree.Tree components) --%>
<div id="treeTemplateHolder" style="display:none">

    <!-- FIXME: StdNav: implement dynamictree plugin and switch navtypes to that -->
    <ul id="list_responsive_collapsible" class="list collapsible" tabindex="0" js-navtype="dynamiclist">
        <%-- node icon and title--%>
        <li js-navtype="dynamiclist" id="list_responsive_collapsible:leaf" class="leaf">
            <p class="wrap button draggable"><b class="icon"></b></p>
        </li>

        <%-- tree loading indicator --%>
        <li js-navtype="dynamiclist" id="list_responsive_collapsible:loading" class="node loading">
            <p class="wrap button draggable"><b class="icon"></b></p>
        </li>
    </ul>

    <ul id="list_responsive_collapsible_folders" class="list collapsible folders" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_folders:folders" class="folders">
            <p class="wrap button draggable"><b class="icon"></b></p>
        </li>

    </ul>

    <ul id="list_responsive_collapsible_folders_adhocAvailableTrees" class="list collapsible folders" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_folders_adhocAvailableTrees:folders" class="folders">
            <p class="wrap button draggable">
                <b class="icon"></b>
                <b class="icon error hidden"></b>
            </p>
        </li>

    </ul>

    <%--todo: not sure if this is the right way. Leave for now: dashboard dev in progress--%>
    <ul id="list_responsive_collapsible_folders_specialContent" class="list collapsible folders" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_folders_specialContent:folders" class="node specialContent">
            <p class="wrap button "><b class="icon"></b></p>
        </li>

        <li js-navtype="dynamiclist" id="list_responsive_collapsible_folders_specialContent:leaf" class="leaf">
            <p class="wrap button draggable"><b class="icon"></b></p>
        </li>
    </ul>

    <%-- used in data chooser --%>
    <ul id="list_responsive_collapsible_fields" class="list collapsible fields" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_fields:fields" class="fields">
            <p class="wrap button draggable"><span class="icon button"></span></p>
        </li>
    </ul>

    <%-- used in domain designer  --%>
    <ul id="list_responsive_collapsible_type_tables" class="list collapsible type_tables" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_type_tables:tables" class="type_tables">
            <p class="wrap button draggable"><span class="icon button"></span></p>
        </li>

        <%-- button class is absent because this node should not respond on any event --%>
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_type_tables:fields" class="type_tables">
            <p class="wrap"><span class="icon noBubble"></span></p>
        </li>
    </ul>

    <%-- used in domain designer display view --%>
    <ul id="list_responsive_collapsible_type_sets" class="list collapsible type_sets column simple" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive_collapsible_type_sets:sets" class="set">
            <p class="wrap button draggable"><span class="icon button"></span></p>
        </li>
    </ul>

    <%-- used in data chooser display view --%>
    <ul id="list_responsive" class="list collapsible" tabindex="0" js-navtype="dynamiclist">
        <li js-navtype="dynamiclist" id="list_responsive:twoColumn">
            <div class="wrap button"><span class="icon button"></span><p class="column one"></p><p class="column two"></p></div>
        </li>
    </ul>

    <%-- input for inline edit (change of node title) --%>
    <input id="list_responsive_collapsible:input" type="text" value=""/>

</div>

