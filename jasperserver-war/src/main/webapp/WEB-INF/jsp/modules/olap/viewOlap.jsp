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
<%@ page session="true" contentType="text/html" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/jasperserver.tld" prefix="js" %>
<%@ taglib uri="/spring" prefix="spring" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<%@ page errorPage="error.jsp" %>
<%--

  JPivot / WCF comes with its own "expression language", which simply
  is a path of properties. E.g. #{customer.address.name} is
  translated into:
    session.getAttribute("customer").getAddress().getName()
  WCF uses jakarta commons beanutils to do so, for an exact syntax
  see its documentation.

  With JSP 2.0 you should use <code>#{}</code> notation to define
  expressions for WCF attributes and <code>\${}</code> to define
  JSP EL expressions.

  No longer true? We have rtexprvalue = true on JPivot tags now

    JSP EL expressions can not be used with WCF tags currently, all
    tag attributes have their <code>rtexprvalue</code> set to false.
    There may be a twin library supporting JSP EL expressions in
    the future (similar to the twin libraries in JSTL, e.g. core
    and core_rt).

  Check out the WCF distribution which contains many examples on
  how to use the WCF tags (like tree, form, table etc).

--%>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
<t:putAttribute name="pageTitle"><spring:message code="jsp.viewOlap.title"/></t:putAttribute>
<t:putAttribute name="bodyID" value="analysisView"/>
<t:putAttribute name="bodyClass">oneColumn</t:putAttribute>
<t:putAttribute name="moduleName" value="olapView/olapViewMain"/>
<t:putAttribute name="headerContent">
    <meta http-equiv="Content-Type" content="text/html; charset=${requestScope['com.jaspersoft.ji.characterEncoding']}">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jpivot/table/mdxtable.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jpivot/navi/mdxnavi.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/wcf/form/xform.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/wcf/table/xtable.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/wcf/tree/xtree.css">

    <%-- set noMenu --%>
    <c:set var="withMenu" value="true"/>
    <c:if test="${sessionScope.drillthrough == 'x' && not requestScope.olapModel.showTableBelowCube}">
        <meta name="noMenu" content="true">
        <c:set var="withMenu" value="false"/>
    </c:if>

    <%-- drillthrough switches --%>
    <c:set var="belowCube" value="${requestScope.olapModel.showTableBelowCube}"/>
    <%-- account for saveVieAs --%>
    <c:if test="${sessionScope.belowCube}">
        <c:set var="belowCube" value="${sessionScope.belowCube}"/>
    </c:if>
    <c:set var="inDrillThrough" value="${sessionScope.inDrillThrough}"/>

    <%-- debug
    <c:out value="withMenu=${withMenu}"/>
    <br/><c:out value="belowCube=${belowCube}"/>
    <br/><c:out value="inDrillThrough=${inDrillThrough}"/>
    --%>

    <%-- drillthrough condition 1 --%>
    <c:set var="menuToolbarCube1" value="${withMenu == 'true' && belowCube != 'true' && inDrillThrough == 'false'}"/>
    <c:set var="menuToolbarCube2" value="${withMenu == 'true' && belowCube != 'true' && inDrillThrough == 'true'}"/>
    <c:set var="menuToolbarCube3" value="${withMenu == 'true' && belowCube == 'true' && inDrillThrough == 'false'}"/>
    <c:set var="menuToolbarCubeAll" value="${menuToolbarCube1 || menuToolbarCube2 || menuToolbarCube3}"/>

    <%-- drillthrough condition 2 --%>
    <c:set var="drillThroughTable1" value="${withMenu != 'true' && belowCube != 'true' && inDrillThrough == 'true'}"/>

    <%-- drillthrough condition 3 --%>
    <c:set var="drillThroughTable2" value="${withMenu == 'true' && belowCube == 'true' && inDrillThrough == 'true'}"/>

    <%-- drillthrough contitions, i.e., menu, toolbar, cube and drillthrough table --%>
    <c:set var="menuToolbarCube" value="${((menuToolbarCubeAll || drillThroughTable2) && not drillThroughTable1)}"/>
    <c:set var="drillthrough" value="${((drillThroughTable1 || drillThroughTable2) && not menuToolbarCubeAll)}"/>

    <js:out javaScriptEscape="true">
    <script>
        var viewURI = '${requestScope.name}';
        var olapPage = 'olap/viewOlap.html';
    </script>
    </js:out>

</t:putAttribute>
<t:putAttribute name="bodyContent">
<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
<t:putAttribute name="containerClass" value="column decorated primary"/>
<t:putAttribute name="containerTitle"><spring:message code="jsp.viewOlap.title"/></t:putAttribute>

<t:putAttribute name="bodyContent">

<%--<body bgcolor=white onunload=closeChildWin()>--%>
<%

    String location = (String) session.getAttribute("location");
    if (location != null) {
        String curViewName = (String) session.getAttribute("currentView");
        com.tonbeller.wcf.form.FormComponent frm = (com.tonbeller.wcf.form.FormComponent) session.getAttribute(curViewName + "/saveas");
        frm.setError("location", location);
        session.removeAttribute("location");
    }

    String viewName = (String) session.getAttribute("viewName");
    if (viewName != null) {
        String curViewName = (String) session.getAttribute("currentView");
        com.tonbeller.wcf.form.FormComponent frm = (com.tonbeller.wcf.form.FormComponent) session.getAttribute(curViewName + "/saveas");
        frm.setError("viewName", viewName);
        session.removeAttribute("viewName");
    }

    com.tonbeller.tbutils.res.Resources reso = com.tonbeller.tbutils.res.Resources.instance();
    String copyString = reso.getResourceBundle().getMessage("JAJ_000_jsp.jpivot.toolb.saveas.copy",
            null, reso.getLocaleContextHolderLocale());

    String popUpSaveAs = (String) session.getAttribute("save_access_denied");
    if (popUpSaveAs != null) {
        String curViewName = (String) session.getAttribute("currentView");
        com.tonbeller.wcf.form.FormComponent frm = (com.tonbeller.wcf.form.FormComponent) session.getAttribute(curViewName + "/saveas");
        frm.setVisible(true);
        session.removeAttribute("save_access_denied");
    }
%>

<form id="olapViewForm" action="<c:url value='viewOlap.html'/>" method="get">
    <wcf:scroller/>

    <!-- debug <input type="button" value="show dynamic source" onclick="showDynaSource()"> -->
        <%-- Needed for navigator --%>
    <wcf:renderParam name="linkParameters" value="name=${requestScope.name}"/>
        <%-- needed for drill through in mdxtable.xsl --%>
    <wcf:renderParam name="olapPage" value="olap/viewOlap.html"/>
        <%-- include query and title, so this jsp may be used with different queries --%>
        <%--<wcf:include id="include01" httpParam="query" prefix="/WEB-INF/queries/" suffix=".jsp"/>--%>
        <%-- store olapModel name --%>
    <input type="hidden" name="name" value="${requestScope.name}"/>
    <input type="hidden" name="decorate" value="${param.decorate}"/>
    <input type="hidden" name="ParentFolderUri" value="${param.ParentFolderUri}"/>
    <c:if test="${olapModel == null}">
        <jsp:forward page="empty.jsp"/>
    </c:if>
        <%-- used by drillReplace, see mdxtable.xsl --%>
    <wcf:renderParam name="viewUri" value="${requestScope.name}"/>
        <%-- define table, navigator and forms --%>
    <jp:table id="${requestScope.name}/table" query="#{olapModel}"/>
        <%--<jp:table id="breadcrumbs" query="#{query01}"/>--%>
    <jp:navigator id="${requestScope.name}/navi" query="#{olapModel}" visible="false"/>
    <wcf:form id="${requestScope.name}/mdxedit" xmlUri="/WEB-INF/jpivot/table/mdxedit.xml"
              model="#{olapModel}" visible="false" bookmarkable="true"/>
    <wcf:form id="${requestScope.name}/sortform" xmlUri="/WEB-INF/jpivot/table/sortform.xml"
              model="#(${requestScope.name}/table)" visible="false"/>
    <wcf:form id="${requestScope.name}/displayform" xmlUri="/WEB-INF/jpivot/table/displayform.xml"
              model="#(${requestScope.name}/table)" visible="false" bookmarkable="true"/>
    <jp:print id="${requestScope.name}/print"/>
    <wcf:form id="${requestScope.name}/printform" xmlUri="/WEB-INF/jpivot/print/printpropertiesform.xml"
              model="#(${requestScope.name}/print)" visible="false" bookmarkable="true"/>
    <jp:chart id="${requestScope.name}/chart" query="#{olapModel}" visible="false"/>
    <wcf:form id="${requestScope.name}/chartform" xmlUri="/WEB-INF/jpivot/chart/chartpropertiesform.xml"
              model="#(${requestScope.name}/chart)" visible="false" bookmarkable="true"/>
        <%-- for the drillthrough model, this needs to be named XXX.drilthroughtable --%>
    <wcf:form id="${requestScope.name}/saveas" xmlUri="/WEB-INF/jpivot/saveas/saveas.xml"
              model="#(${requestScope.name}/table)" visible="false"/>
    <wcf:table id="${requestScope.name}.drillthroughtable" model="#{drillThroughTableModel}" visible="false"
               selmode="none" editable="true"/>
        <%-- define a toolbar --%>
    <wcf:toolbar id="${requestScope.name}/toolbar" bundle="com.tonbeller.jpivot.toolbar.resources">
        <wcf:scriptbutton id="drillReplace"
                          model="#(${requestScope.name}/table.extensions.drillReplace.enabled)"
                          tooltip="JAJ_000_jsp.jpivot.toolb.navi.replace" radioGroup="navi" img="zoom"/>
        <wcf:scriptbutton id="sortConfigButton" tooltip="JAJ_000_jsp.jpivot.toolb.sort.hierarchy"
                          img="sortacross"
                          model="#(${requestScope.name}/table.extensions(sortRank).sortAcrossCubeHierarchy)"/>
        <wcf:scriptbutton id="nonEmpty" tooltip="JAJ_000_jsp.jpivot.toolb.non.empty" img="showempty"
                          model="#(${requestScope.name}/table.extensions.nonEmpty.buttonPressed)"/>
        <wcf:scriptbutton id="swapAxes" tooltip="JAJ_000_jsp.jpivot.toolb.swap.axes" img="swapaxes"
                          model="#(${requestScope.name}/table.extensions.swapAxes.buttonPressed)"/>
        <wcf:scriptbutton id="propertiesButton" tooltip="JAJ_000_jsp.jpivot.toolb.table.config"
                          img="displayoptions" model="#(${requestScope.name}/displayform.visible)"
                          radioGroup="config"/>
        <wcf:separator/>
        <wcf:scriptbutton id="chartButton01" tooltip="JAJ_000_jsp.jpivot.toolb.chart" img="chart-new"
                          model="#(${requestScope.name}/chart.visible)"/>
        <wcf:scriptbutton id="chartPropertiesButton01" tooltip="JAJ_000_jsp.jpivot.toolb.chart.config"
                          img="chartoptions" model="#(${requestScope.name}/chartform.visible)"
                          radioGroup="config"/>
        <wcf:separator/>
        <wcf:scriptbutton id="cubeNaviButton" tooltip="JAJ_000_jsp.jpivot.toolb.cube" img="cube-new"
                          model="#(${requestScope.name}/navi.visible)"/>
        <wcf:scriptbutton id="mdxEditButton" tooltip="JAJ_000_jsp.jpivot.toolb.mdx.edit" img="mdxquery"
                          model="#(${requestScope.name}/mdxedit.visible)"/>
        <wcf:separator/>
        <c:url var="printxlsURI" value="./Print">
            <c:param name="cube" value="01"/>
            <c:param name="type" value="XLS"/>
            <c:param name="view" value="${requestScope.name}"/>
            <c:param name="xsluri" value="/WEB-INF/jpivot/table/xls_mdxtable.xsl"/>
            <c:param name="xsluridt" value="/WEB-INF/wcf/xls_xtable.xsl"/>
            <c:param name="outputref" value="${requestScope.name}/table"/>
        </c:url>
        <wcf:imgbutton id="printxls" tooltip="JAJ_000_jsp.jpivot.toolb.excel" img="excel-new"
                       href="${printxlsURI}"/>
        <c:remove var="printxlsURI"/>
        <c:url var="printpdfURI" value="./Print">
            <c:param name="cube" value="01"/>
            <c:param name="type" value="PDF"/>
            <c:param name="view" value="${requestScope.name}"/>
            <c:param name="xsluri" value="/WEB-INF/jpivot/table/fo_mdxtable.xsl"/>
            <c:param name="xsluridt" value="/WEB-INF/wcf/fo_xtable.xsl"/>
            <c:param name="outputref" value="${requestScope.name}/table"/>
        </c:url>
        <wcf:imgbutton id="printpdf" tooltip="JAJ_000_jsp.jpivot.toolb.print" img="print-new"
                       href="${printpdfURI}"/>
        <c:remove var="printpdfURI"/>
        <wcf:scriptbutton id="printPropertiesButton01" tooltip="JAJ_000_jsp.jpivot.toolb.print.config"
                          img="outputopts" model="#(${requestScope.name}/printform.visible)"
                          radioGroup="config"/>
        <wcf:separator/>
        <c:url var="save" value="./Print">
            <c:param name="type" value="FILE"/>
            <c:param name="view" value="${requestScope.name}"/>
            <c:param name="xsluri" value="/WEB-INF/jpivot/table/fo_mdxtable.xsl"/>
            <c:param name="outputref" value="${requestScope.name}/table"/>
            <c:param name="name" value="${requestScope.name}"/>
            <c:param name="d" value="${requestScope.drillthrough}"/>
            <c:param name="label" value="${olapSession.olapUnit.label}"/>
            <c:param name="description" value="${olapSession.olapUnit.description}"/>
            <c:param name="parentFolder" value="${olapSession.olapUnit.parentFolder}"/>
        </c:url>
        <wcf:imgbutton id="save" tooltip="JAJ_000_jsp.jpivot.toolb.saveas.savett" img="save"
                       href="${save}"/>
        <c:remove var="save"/>
        <wcf:scriptbutton id="saveas" tooltip="JAJ_000_jsp.jpivot.toolb.saveas.saveastt" img="save-as"
                          model="#(${requestScope.name}/saveas.visible)" radioGroup="config"/>
    </wcf:toolbar>
        <%-- view table with vertical toolbar --%>
    <table border="0" width="100%" cellspacing="0" cellpadding="5">
        <tr>
            <th width="50" align="left" valign="top" rowspan="3">

                    <%-- render toolbar --%>
                <c:if test="${menuToolbarCube}">
                    <wcf:render ref="${requestScope.name}/toolbar"
                                xslUri="/WEB-INF/jpivot/toolbar/vtoolbar.xsl" xslCache="true"/>
                </c:if>

                    <%-- if there was an overflow, show error message --%>
                <c:if test="${requestScope.olapModel == null}">
                    <jsp:forward page="error.jsp"/>
                </c:if>
                <c:if test="${requestScope.olapModel.result.overflowOccured}">
                <p>
                    <strong style="color:red"><spring:message code="jsp.viewOlap.overflow"/></strong>

                <p>
                    </c:if>
            </th>
            <!-- olapModel name -->
            <br>
            <th align="left" valign="top" height="1">
    <span class="fsection" style="font-size: 12pt">
        <c:if test="${olapSession.olapUnit != null}">
                        ${olapSession.olapUnit.label}</span>
                <br/>
                </c:if>
            </th>
        </tr>
            <%-- navigator --%>
        <c:set var="naviVarName" value="${requestScope.name}/navi"/>
            <%-- mdx editor--%>
        <c:set var="mdxeditVarName" value="${requestScope.name}/mdxedit"/>
            <%-- chart --%>
        <c:set var="chartVarName" value="${requestScope.name}/chart"/>
            <%-- table row --%>
        <c:if test="${(sessionScope[naviVarName].visible == true || sessionScope[mdxeditVarName].visible == true || sessionScope[chartVarName].visible == true) && menuToolbarCube}">
        <tr>
            <td>
                </c:if>
                    <%-- render navigator --%>
                <c:if test="${sessionScope[naviVarName].visible && menuToolbarCube}">
                    <wcf:render ref="${requestScope.name}/navi"
                                xslUri="/WEB-INF/jpivot/navi/js-navigator.xsl" xslCache="true"/>
                </c:if>
                    <%-- edit mdx --%>
                <c:if test="${sessionScope[mdxeditVarName].visible && menuToolbarCube}">
                    <h3><spring:message code="jsp.viewOlap.mdx"/></h3>
                    <wcf:render ref="${requestScope.name}/mdxedit" xslUri="/WEB-INF/wcf/wcf.xsl"
                                xslCache="true"/>
                </c:if>
                    <%-- chart --%>
                <c:if test="${sessionScope[chartVarName].visible && menuToolbarCube}">
                    <h3><spring:message code="jsp.viewOlap.chart"/></h3>
                    <wcf:render ref="${requestScope.name}/chart" xslUri="/WEB-INF/jpivot/chart/chart.xsl"
                                xslCache="true"/>
                </c:if>
                    <%-- table row --%>
                <c:if test="${(sessionScope[naviVarName].visible == true || sessionScope[mdxeditVarName].visible == true) && menuToolbarCube}">
            </td>
        </tr>
        </c:if>
        <tr>
            <td align="left" valign="top">

                <!-- render the table -->
                <c:if test="${menuToolbarCube}">
                <p>
                        <wcf:render ref="${requestScope.name}/table"
                                    xslUri="/WEB-INF/jpivot/table/mdxtableEfficient.xsl" xslCache="true"/>

                <p>
                        <spring:message code="JAJ_000_jsp.viewOlap.slicer"/>:
                        <wcf:render ref="${requestScope.name}/table"
                                    xslUri="/WEB-INF/jpivot/table/mdxslicer.xsl" xslCache="true"/>

                <p>
                    </c:if>

                    <!-- drill through table -->
                    <c:if test="${drillthrough}">
                    <c:choose>
                    <c:when test="${requestScope.olapModel.showTableBelowCube}">
                    <wcf:render ref="${requestScope.name}.drillthroughtable" xslUri="/WEB-INF/wcf/wcf.xsl"
                                xslCache="true">
                        <wcf:renderParam name="isSamePage" value="true"/>
                    </wcf:render>
                    </c:when>
                    <c:otherwise>
                    <c:if test="${sessionScope.drillthrough == 'x' && not requestScope.olapModel.showTableBelowCube}">
                    <wcf:render ref="${requestScope.name}.drillthroughtable" xslUri="/WEB-INF/wcf/wcf.xsl"
                                xslCache="true">
                        <wcf:renderParam name="isSamePage" value="false"/>
                    </wcf:render>
                    </c:if>
                    </c:otherwise>
                    </c:choose>
                    </c:if>

                    <!-- render chart; moved before navigation table -->
            </td>
        </tr>
    </table>
    <c:if test="${menuToolbarCube}">
    <p>
        <a href="<c:url value='/flow.html'><c:param name='_flowId' value='searchFlow'/>
                                           <c:param name="mode" value='search'/>
                                           <c:param name="filterId" value='resourceTypeFilter'/>
                                           <c:param name="filterOption" value='resourceTypeFilter-view'/>
                                           <c:param name="searchText" value=''/>
                                           <c:param name="decorate" value='${param.decorate}'/>
                 </c:url>"><spring:message code="jsp.viewOlap.backToOlap"/></a>
        <br/>
        <a href="<c:url value='/flow.html'><c:param name='_flowId' value='searchFlow'/>
                                           <c:param name='curlnk' value='2'/>
                                           <c:param name='showFolder' value='${param.ParentFolderUri}'/>
                                           <c:param name="decorate" value='${param.decorate}'/>
                 </c:url>"><spring:message code="jsp.viewOlap.backToRepo"/></a>
        </c:if>
            <wcf:render ref="${requestScope.name}/displayform" xslUri="/WEB-INF/wcf/wcf.xsl"
                        xslCache="true"/>
            <wcf:render ref="${requestScope.name}/chartform" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
            <wcf:render ref="${requestScope.name}/printform" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
            <wcf:render ref="${requestScope.name}/saveas" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
</form>

<%
    /*
        // save this for future debugging.
        java.util.Enumeration en = session.getAttributeNames();
        out.println("<BR>");
        out.println("<BR>");

        while (en.hasMoreElements()) {
           String cur = (String)en.nextElement();
           out.println("name = " + cur + "--");
           out.println("<BR>value = " + session.getAttribute(cur) + "--");
           out.println("<BR><BR>");
        }
    */
%>
</t:putAttribute>
</t:insertTemplate>
</t:putAttribute>
</t:insertTemplate>
