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

<meta name="viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, width=device-width">
<meta name="apple-mobile-web-app-capable" content="yes"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/runtime/${jsOptimizationProperties.runtimeHash}/themes/reset.css" type="text/css" media="screen">

<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/theme.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/typography.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/colors.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/jif.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/util.css" type="text/css" media="screen,print"/>
<!--
Containers
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/frame.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/containers.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/dialog.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/columns.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/decorated_column.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/panels.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/panes.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/menus.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/dialog_panels.css" type="text/css" media="screen,print"/>
<!--
Pages
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pages/one_column.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pages/two_columns.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pages/three_columns.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pages/row.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pages/flow.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pages/pickwells.css" type="text/css" media="screen,print"/>

<!--[if LT IE 9]>
<script type="text/javascript">
MAX_STYLESHEETS = 25;
linkurls = [
"${pageContext.request.contextPath}/themes/dev/components/common.css",
"${pageContext.request.contextPath}/themes/dev/components/layout_manager.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.css",
"${pageContext.request.contextPath}/themes/dev/buttons/action.css",
"${pageContext.request.contextPath}/themes/dev/buttons/action.join.css",
"${pageContext.request.contextPath}/themes/dev/buttons/action.jumbo.css",
"${pageContext.request.contextPath}/themes/dev/buttons/action.square.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.capsule.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.disclosure.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.minimize.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.options.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.picker.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.scheduled.css",
"${pageContext.request.contextPath}/themes/dev/buttons/button.search.css",
"${pageContext.request.contextPath}/themes/dev/buttons/move.css",
"${pageContext.request.contextPath}/themes/dev/lists/base.css",
"${pageContext.request.contextPath}/themes/dev/lists/buttonSet.css",
"${pageContext.request.contextPath}/themes/dev/lists/collapsible.css",
"${pageContext.request.contextPath}/themes/dev/lists/fields.css",
"${pageContext.request.contextPath}/themes/dev/lists/filters.css",
"${pageContext.request.contextPath}/themes/dev/lists/flat.css",
"${pageContext.request.contextPath}/themes/dev/lists/folders.css",
"${pageContext.request.contextPath}/themes/dev/lists/horizontal.css",
"${pageContext.request.contextPath}/themes/dev/lists/inputControls.css",
"${pageContext.request.contextPath}/themes/dev/lists/linkedResources.css",
"${pageContext.request.contextPath}/themes/dev/lists/location.css",
"${pageContext.request.contextPath}/themes/dev/lists/nameValue.css",
"${pageContext.request.contextPath}/themes/dev/lists/node.css",
"${pageContext.request.contextPath}/themes/dev/lists/palette.css",
"${pageContext.request.contextPath}/themes/dev/lists/tabular.css",
"${pageContext.request.contextPath}/themes/dev/lists/resources.css",
"${pageContext.request.contextPath}/themes/dev/lists/setLeft.css",
"${pageContext.request.contextPath}/themes/dev/lists/settings.css",
"${pageContext.request.contextPath}/themes/dev/lists/type.css",
"${pageContext.request.contextPath}/themes/dev/controls/base.css",
"${pageContext.request.contextPath}/themes/dev/controls/calendar.css",
"${pageContext.request.contextPath}/themes/dev/controls/combo.css",
"${pageContext.request.contextPath}/themes/dev/controls/groupBox.css",
"${pageContext.request.contextPath}/themes/dev/controls/paging.css",
"${pageContext.request.contextPath}/themes/dev/controls/path.css",
"${pageContext.request.contextPath}/themes/dev/controls/pickWells.css",
"${pageContext.request.contextPath}/themes/dev/controls/searchLockup.css",
"${pageContext.request.contextPath}/themes/dev/controls/tabSet.css",
"${pageContext.request.contextPath}/themes/dev/dialogSpecific/base.css",
"${pageContext.request.contextPath}/themes/dev/dataDisplays/base.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/addEditDomain.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/addReport.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/addReportControls.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/addResource.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/dashboardDesigner.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/dashboardViewer.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/data_chooser.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/demo.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/designer.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/designerCache.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/domainDesigner.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/home.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/licenseFailed.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/login.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/manage_users_and_roles.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/messageDetail.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/misc.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/nothingToDisplay.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/olap_settings.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/reportOptions.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/reportViewer.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/repository.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/savedValues.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/scheduler.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/serverError.css",
"${pageContext.request.contextPath}/themes/dev/pageSpecific/systemError.css",
"${pageContext.request.contextPath}/themes/default/samples.css",
"${pageContext.request.contextPath}/${scriptsFolder}/bower_components/jquery-ui/themes/jquery.ui.theme.css",
"${pageContext.request.contextPath}/${scriptsFolder}/bower_components/jquery-ui/themes/redmond/jquery-ui-1.10.4-custom.css",
"${pageContext.request.contextPath}/themes/dev/containers/bg.css",
"${pageContext.request.contextPath}/themes/dev/lists/bg.css",
"${pageContext.request.contextPath}/themes/dev/buttons/bg.css",
"${pageContext.request.contextPath}/themes/dev/components/jive.css"
];
var n = document.createStyleSheet();
for (var i=0;i<linkurls.length;i++) {
n.addImport(linkurls[i]);
if (i % MAX_STYLESHEETS == 0) n = document.createStyleSheet();
}
</script>
<![endif]-->

<!--
Components Layout
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/components/common.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/components/layout_manager.css" type="text/css" media="screen,print"/>
<!--
Buttons
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/action.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/action.join.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/action.jumbo.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/action.square.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.capsule.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.disclosure.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.minimize.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.options.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.picker.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.scheduled.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/button.search.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/move.css" type="text/css" media="screen,print"/>
<!--
Lists
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/base.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/buttonSet.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/collapsible.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/fields.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/filters.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/flat.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/folders.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/horizontal.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/inputControls.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/linkedResources.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/location.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/nameValue.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/node.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/palette.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/tabular.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/resources.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/setLeft.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/settings.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/type.css" type="text/css" media="screen,print"/>
<!--
Controls
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/base.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/calendar.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/combo.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/groupBox.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/paging.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/path.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/pickWells.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/searchLockup.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/controls/tabSet.css" type="text/css" media="screen,print"/>
<!--
Dialog specific
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/dialogSpecific/base.css" type="text/css" media="screen,print"/>
<!--
Data displays
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/dataDisplays/base.css" type="text/css" media="screen,print"/>
<!--
Page Specific
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/addEditDomain.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/addReport.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/addReportControls.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/addResource.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/dashboardDesigner.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/dashboardViewer.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/data_chooser.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/demo.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/designer.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/designerCache.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/domainDesigner.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/home.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/licenseFailed.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/login.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/manage_users_and_roles.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/messageDetail.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/misc.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/nothingToDisplay.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/olap_settings.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/reportOptions.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/reportViewer.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/repository.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/savedValues.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/scheduler.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/serverError.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/pageSpecific/systemError.css" type="text/css" media="screen,print"/>

<%--TODO: move to commmon themes approach--%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/${scriptsFolder}/bower_components/jquery-ui/themes/jquery.ui.theme.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/${scriptsFolder}/bower_components/jquery-ui/themes/redmond/jquery-ui-1.10.4-custom.css" type="text/css" media="screen">

<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/containers/bg.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/lists/bg.css" type="text/css" media="screen,print"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/themes/dev/buttons/bg.css" type="text/css" media="screen,print"/>
