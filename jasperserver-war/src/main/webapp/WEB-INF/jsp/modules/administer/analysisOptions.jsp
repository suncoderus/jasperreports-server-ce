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

<%@ page import="mondrian.olap.MondrianProperties" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>


<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="menu.mondrian.properties"/></t:putAttribute>
    <t:putAttribute name="bodyID" value="analysisOptions"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="administer/administerAnalysisOptionsMain"/>

    <t:putAttribute name="headerContent">
        <%@ include file="administerState.jsp" %>
	</t:putAttribute>
    <t:putAttribute name="bodyContent">
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value="settings"/>
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="menu.mondrian.properties"/></t:putAttribute>
		    <t:putAttribute name="headerContent">
				<button id="flushOLAPCache" class="button capsule text up"><span class="wrap"><spring:message code="menu.flush.olap.cache"/></span></button>
		    </t:putAttribute>
		    <t:putAttribute name="bodyClass" value=""/>
		    <t:putAttribute name="bodyContent">

                <%
                  ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
                  String oName;
                  String oDesc;
                  String oLabelCode;
                  Object oValue;
                  String[] oSelectOptions;
                %>

				<ol class="list settings">
					<li class="node">
						<div class="wrap">
							<h2 class="title settingsGroup"><spring:message code="JAM_HEADER_GENERAL_BEHAVIOR"/></h2>
						</div>
						<ol class="list settings">
                            <%
                              request.setAttribute("oName", MondrianProperties.instance().DisableCaching.getPath());
                              request.setAttribute("oLabelCode", "JAM_005_DISABLE_MEMORY_CACHE");
                              request.setAttribute("oValue", MondrianProperties.instance().DisableCaching.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().EnableDrillThrough.getPath());
                                request.setAttribute("oLabelCode", "JAM_070_ENABLE_DRILL_THROUGH");
                                request.setAttribute("oValue", MondrianProperties.instance().EnableDrillThrough.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().GenerateFormattedSql.getPath());
                              request.setAttribute("oLabelCode", "JAM_006_GENERATE_FORMATTED_SQL");
                              request.setAttribute("oValue", MondrianProperties.instance().GenerateFormattedSql.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().QueryLimit.getPath());
                              request.setAttribute("oLabelCode", "JAM_009_QUERY_LIMIT");
                              request.setAttribute("oValue", MondrianProperties.instance().QueryLimit.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().ResultLimit.getPath());
                              request.setAttribute("oLabelCode", "JAM_010_RESULT_LIMIT");
                              request.setAttribute("oValue", MondrianProperties.instance().ResultLimit.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().RolapConnectionShepherdNbThreads.getPath());
                                request.setAttribute("oLabelCode", "JAM_066_MAX_QUERY_THREADS");
                                request.setAttribute("oValue", MondrianProperties.instance().RolapConnectionShepherdNbThreads.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().RolapConnectionShepherdThreadPollingInterval.getPath());
                                request.setAttribute("oLabelCode", "JAM_067_SHEPHERD_THREAD_POLLING_INTERVAL");
                                request.setAttribute("oValue", MondrianProperties.instance().RolapConnectionShepherdThreadPollingInterval.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().MaxEvalDepth.getPath());
                                request.setAttribute("oLabelCode", "JAM_022_EVALUATE_MAX_EVAL_DEPTH");
                                request.setAttribute("oValue", MondrianProperties.instance().MaxEvalDepth.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().ExpCompilerClass.getPath());
                              request.setAttribute("oLabelCode", "JAM_052_EXP_CALC_CLASS");
                              request.setAttribute("oValue", MondrianProperties.instance().ExpCompilerClass.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().CaseSensitive.getPath());
                              request.setAttribute("oLabelCode", "JAM_024_CASE_SENSITIVE");
                              request.setAttribute("oValue", MondrianProperties.instance().CaseSensitive.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().CellBatchSize.getPath());
                                request.setAttribute("oLabelCode", "JAM_063_CELL_BATCH_SIZE");
                                request.setAttribute("oValue", MondrianProperties.instance().CellBatchSize.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().CompareSiblingsByOrderKey.getPath());
                              request.setAttribute("oLabelCode", "JAM_023_COMPARE_SIBLINGS_BY_ORDER_KEY");
                              request.setAttribute("oValue", MondrianProperties.instance().CompareSiblingsByOrderKey.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().QueryTimeout.getPath());
                              request.setAttribute("oLabelCode", "JAM_025_QUERY_TIMEOUT");
                              request.setAttribute("oValue", MondrianProperties.instance().QueryTimeout.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().HighCardChunkSize.getPath());
                              request.setAttribute("oLabelCode", "JAM_055_HIGH_CARD_CHUNK_SIZE");
                              request.setAttribute("oValue", MondrianProperties.instance().HighCardChunkSize.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().SparseSegmentDensityThreshold.getPath());
                              request.setAttribute("oLabelCode", "JAM_012_SPARSE_DENSITY_THRESHOLD");
                              request.setAttribute("oValue", MondrianProperties.instance().SparseSegmentDensityThreshold.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().SparseSegmentCountThreshold.getPath());
                              request.setAttribute("oLabelCode", "JAM_013_SPARSE_COUNT_THRESHOLD");
                              request.setAttribute("oValue", MondrianProperties.instance().SparseSegmentCountThreshold.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().IgnoreInvalidMembers.getPath());
                              request.setAttribute("oLabelCode", "JAM_025_IGNORE_INVALID_MEMBERS");
                              request.setAttribute("oValue", MondrianProperties.instance().IgnoreInvalidMembers.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().IgnoreInvalidMembersDuringQuery.getPath());
                              request.setAttribute("oLabelCode", "JAM_026_IGNORE_INVALID_MEMBERS_DURING_QUERY");
                              request.setAttribute("oValue", MondrianProperties.instance().IgnoreInvalidMembersDuringQuery.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                                request.setAttribute("oName", MondrianProperties.instance().NullMemberRepresentation.getPath());
                                request.setAttribute("oLabelCode", "JAM_027_NULL_MEMBER_REPRESENTATION");
                                request.setAttribute("oValue", MondrianProperties.instance().NullMemberRepresentation.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().IterationLimit.getPath());
                              request.setAttribute("oLabelCode", "JAM_028_ITERATION_LIMIT");
                              request.setAttribute("oValue", MondrianProperties.instance().IterationLimit.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().CrossJoinOptimizerSize.getPath());
                              request.setAttribute("oLabelCode", "JAM_029_OPTIMIZER_SIZE");
                              request.setAttribute("oValue", MondrianProperties.instance().CrossJoinOptimizerSize.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().EnableInMemoryRollup.getPath());
                                request.setAttribute("oLabelCode", "JAM_064_ENABLE_IN_MEMORY_ROLLUP");
                                request.setAttribute("oValue", MondrianProperties.instance().EnableInMemoryRollup.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().IgnoreMeasureForNonJoiningDimension.getPath());
                              request.setAttribute("oLabelCode", "JAM_033_IGNORE_MEASURE_FOR_NON_JOINING_PREFIX");
                              request.setAttribute("oValue", MondrianProperties.instance().IgnoreMeasureForNonJoiningDimension.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                                request.setAttribute("oName", MondrianProperties.instance().NeedDimensionPrefix.getPath());
                                request.setAttribute("oLabelCode", "JAM_032_NEED_DIMENSION_PREFIX");
                                request.setAttribute("oValue", MondrianProperties.instance().NeedDimensionPrefix.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().NullDenominatorProducesNull.getPath());
                              request.setAttribute("oLabelCode", "JAM_031_NULL_OR_ZERO_DENOMINATOR_PRODUCES_NULL");
                              request.setAttribute("oValue", MondrianProperties.instance().NullDenominatorProducesNull.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().StatisticsProviders.getPath());
                                request.setAttribute("oLabelCode", "JAM_069_STATISTICS_PROVIDER");
                                request.setAttribute("oValue", MondrianProperties.instance().StatisticsProviders.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                              oName = MondrianProperties.instance().SolveOrderMode.getPath();
                              oDesc = oName;
                              oLabelCode = "JAM_030_SOLVE_ORDER_MODE";
                              oValue = MondrianProperties.instance().SolveOrderMode.get();

                              String[] solvedModes = {"ABSOLUTE", "SCOPED"};
                              oSelectOptions = solvedModes;
                            %>
                            <%@ include file="templateList.jsp" %>

                        </ol>
                    </li>

                    <li class="node">
                        <div class="wrap">
                            <h2 class="title settingsGroup"><spring:message code="JAM_014_AGGREGATE_SECTION"/></h2>
                        </div>


                        <ol class="list settings">

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().UseAggregates.getPath());
                              request.setAttribute("oLabelCode", "JAM_014_ENABLE_AGGREGATES");
                              request.setAttribute("oValue", MondrianProperties.instance().UseAggregates.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().ReadAggregates.getPath());
                                request.setAttribute("oLabelCode", "JAM_072_READ_AGGREGATE_TABLES");
                                request.setAttribute("oValue", MondrianProperties.instance().ReadAggregates.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().ChooseAggregateByVolume.getPath());
                              request.setAttribute("oLabelCode", "JAM_015_CHOOSE_AGGREGATES_BY_VOL");
                              request.setAttribute("oValue", MondrianProperties.instance().ChooseAggregateByVolume.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().OptimizePredicates.getPath());
                              request.setAttribute("oLabelCode", "JAM_016_AGGREGATES_OPTIMIZE_PREDICATES");
                              request.setAttribute("oValue", MondrianProperties.instance().OptimizePredicates.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().AggregateRules.getPath());
                              request.setAttribute("oLabelCode", "JAM_017_AGGREGATES_RULES");
                              request.setAttribute("oValue", MondrianProperties.instance().AggregateRules.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().AggregateRuleTag.getPath());
                              request.setAttribute("oLabelCode", "JAM_018_AGGREGATES_RULE_TAG");
                              request.setAttribute("oValue", MondrianProperties.instance().AggregateRuleTag.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().GenerateAggregateSql.getPath());
                              request.setAttribute("oLabelCode", "JAM_019_AGGREGATES_GENERATE_SQL");
                              request.setAttribute("oValue", MondrianProperties.instance().GenerateAggregateSql.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().JdbcFactoryClass.getPath());
                              request.setAttribute("oLabelCode", "JAM_020_AGGREGATES_JDBC_FACTORY_CLASS");
                              request.setAttribute("oValue", MondrianProperties.instance().JdbcFactoryClass.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                        </ol>
                    </li>

                    <li class="node">
                        <div class="wrap">
                            <h2 class="title settingsGroup"><spring:message code="JAM_034_CACHING_SECTION"/></h2>
                        </div>
                        <ol class="list settings">

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableExpCache.getPath());
                              request.setAttribute("oLabelCode", "JAM_035_EXP_CACHE_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableExpCache.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().EnableRolapCubeMemberCache.getPath());
                                request.setAttribute("oLabelCode", "JAM_036_ENABLE_CUBE_MEMBER_CACHE");
                                request.setAttribute("oValue", MondrianProperties.instance().EnableRolapCubeMemberCache.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().SegmentCache.getPath());
                              request.setAttribute("oLabelCode", "JAM_059_SEGMENT_CACHE");
                              request.setAttribute("oValue", MondrianProperties.instance().SegmentCache.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().SegmentCacheManagerNumberCacheThreads.getPath());
                                request.setAttribute("oLabelCode", "JAM_060_MAX_CACHE_THREADS");
                                request.setAttribute("oValue", MondrianProperties.instance().SegmentCacheManagerNumberCacheThreads.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().SegmentCacheManagerNumberSqlThreads.getPath());
                                request.setAttribute("oLabelCode", "JAM_061_MAX_SQL_THREADS");
                                request.setAttribute("oValue", MondrianProperties.instance().SegmentCacheManagerNumberSqlThreads.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().DisableLocalSegmentCache.getPath());
                                request.setAttribute("oLabelCode", "JAM_062_DISABLE_LOCAL_SEGMENT_CACHE");
                                request.setAttribute("oValue", MondrianProperties.instance().DisableLocalSegmentCache.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().MaxConstraints.getPath());
                              request.setAttribute("oLabelCode", "JAM_054_MAX_CONSTRAINTS");
                              request.setAttribute("oValue", MondrianProperties.instance().MaxConstraints.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableNativeCrossJoin.getPath());
                              request.setAttribute("oLabelCode", "JAM_037_NATIVE_CROSS_JOIN_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableNativeCrossJoin.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableNativeTopCount.getPath());
                              request.setAttribute("oLabelCode", "JAM_038_NATIVE_TOP_COUNT_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableNativeTopCount.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableNativeFilter.getPath());
                              request.setAttribute("oLabelCode", "JAM_039_NATIVE_FILTER_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableNativeFilter.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableNativeNonEmpty.getPath());
                              request.setAttribute("oLabelCode", "JAM_040_NATIVE_NON_EMPTY_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableNativeNonEmpty.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().ExpandNonNative.getPath());
                              request.setAttribute("oLabelCode", "JAM_053_EXPAND_NON_NATIVE");
                              request.setAttribute("oValue", MondrianProperties.instance().ExpandNonNative.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().FilterChildlessSnowflakeMembers.getPath());
                                request.setAttribute("oLabelCode", "JAM_065_FILTER_CHILD_LESS_SNOW_FLAKE_MEMBERS");
                                request.setAttribute("oValue", MondrianProperties.instance().FilterChildlessSnowflakeMembers.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                              oName = MondrianProperties.instance().AlertNativeEvaluationUnsupported.getPath();
                              oDesc = oName;
                              oLabelCode = "JAM_048_NATIVE_UNSUPPORTED_ALERT";
                              oValue = MondrianProperties.instance().AlertNativeEvaluationUnsupported.get();

                              String[] options = {"OFF", "WARN", "ERROR"};
                              oSelectOptions = options;
                            %>
                            <%@ include file="templateList.jsp" %>


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableGroupingSets.getPath());
                              request.setAttribute("oLabelCode", "JAM_041_GROUPING_SETS_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableGroupingSets.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                        </ol>
                    </li>

                    <li class="node">
                        <div class="wrap">
                            <h2 class="title settingsGroup"><spring:message code="JAM_043_XMLA_SECTION"/></h2>
                        </div>
                        <ol class="list settings">


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().MaxRows.getPath());
                              request.setAttribute("oLabelCode", "JAM_004_XMLA_MAX_DRILL_THROUGH");
                              request.setAttribute("oValue", MondrianProperties.instance().MaxRows.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().EnableTotalCount.getPath());
                              request.setAttribute("oLabelCode", "JAM_042_XMLA_DRILL_THROUGH_TOTAL_COUNT_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().EnableTotalCount.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", MondrianProperties.instance().XmlaSchemaRefreshInterval.getPath());
                                request.setAttribute("oLabelCode", "JAM_068_SCHEMA_REFRESH_INTERVAL");
                                request.setAttribute("oValue", MondrianProperties.instance().XmlaSchemaRefreshInterval.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                                        </ol>
                                              </li>
                                              <li class="node">
                                                  <div class="wrap">
                                                      <h2 class="title settingsGroup"><spring:message code="JAM_044_MEMORY_MONITOR_SECTION"/></h2>
                                                  </div>
                                                  <ol class="list settings">


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().MemoryMonitor.getPath());
                              request.setAttribute("oLabelCode", "JAM_045_MEMORY_MONITOR_ENABLE");
                              request.setAttribute("oValue", MondrianProperties.instance().MemoryMonitor.get());
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />


                            <%
                              request.setAttribute("oName", MondrianProperties.instance().MemoryMonitorThreshold.getPath());
                              request.setAttribute("oLabelCode", "JAM_046_MEMORY_MONITOR_THRESHOLD");
                              request.setAttribute("oValue", MondrianProperties.instance().MemoryMonitorThreshold.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().ExecutionHistorySize.getPath());
                              request.setAttribute("oLabelCode", "JAM_058_MONITOR_EXECUTION_HISTORY_SIZE");
                              request.setAttribute("oValue", MondrianProperties.instance().ExecutionHistorySize.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                              request.setAttribute("oName", MondrianProperties.instance().MemoryMonitorClass.getPath());
                              request.setAttribute("oLabelCode", "JAM_047_MEMORY_MONITOR_CLASS");
                              request.setAttribute("oValue", MondrianProperties.instance().MemoryMonitorClass.get());
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />


						</ol>
					</li>
				</ol>

		    </t:putAttribute>
		    <t:putAttribute name="footerContent">
		    </t:putAttribute>
		</t:insertTemplate>

		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerID" value="serverSettingsMenu"/>
		    <t:putAttribute name="containerClass" value="column decorated secondary sizeable"/>
            <t:putAttribute name="containerElements">
                <div class="sizer horizontal"></div>
                <button class="button minimize"></button>
            </t:putAttribute>
		    <t:putAttribute name="containerTitle"><spring:message code="menu.settings"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value=""/>
		    <t:putAttribute name="bodyContent">
		    	<!--
		    	   NOTE: these objects serve as navigation links, load respective pages
		    	 -->
                <ul class="list responsive filters">
                    <li class="leaf"><p class="wrap button" id="navLogSettings"><b class="icon"></b><spring:message code="menu.log.Settings"/></p></li>
                    <c:if test="${isProVersion}">
                        <li class="leaf"><p class="wrap button" id="logCollectors"><b class="icon"></b><spring:message code="logCollectors.title"/></p></li>
                    </c:if>
                    <c:choose>
                        <c:when test="${isProVersion}">
                            <c:set var="analysisOptionsId" value="navAnalysisOptions"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="analysisOptionsId" value="navAnalysisOptionsCE"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${isProVersion}">
                        <li class="leaf"><p class="wrap button" id="navDesignerOptions"><b class="icon"></b><spring:message code="menu.adhoc.options"/></p></li>
                        <li class="leaf"><p class="wrap button" id="navDesignerCache"><b class="icon"></b><spring:message code="menu.adhoc.cache"/></p></li>
                    </c:if>
                    <li class="leaf selected"><p class="wrap button" id="${analysisOptionsId}"><b class="icon"></b><spring:message code="menu.mondrian.properties"/></p></li>
                    <li class="leaf"><p class="wrap button" id="navAwsSettings"><b class="icon"></b><spring:message code="menu.aws.settings"/></p></li>
                    <li class="leaf"><p class="wrap button" id="navCustomAttributes"><b class="icon"></b><spring:message code="menu.server.attributes"/></p></li>
                    <li class="leaf"><p class="wrap button" id="navResetSettings"><b class="icon"></b><spring:message code="menu.edit.settings"/></p></li>
                    <li class="leaf"><p class="wrap button" id="navImport"><b class="icon"></b><spring:message code="import.import"/></p></li>
                    <li class="leaf"><p class="wrap button" id="navExport"><b class="icon"></b><spring:message code="export.export"/></p></li>
                    <li class="leaf" disabled="disabled"><p class="wrap separator" href="#"><b class="icon"></b></p></li>
                </ul>
            </t:putAttribute>
		    <t:putAttribute name="footerContent">
		    </t:putAttribute>
		</t:insertTemplate>
     </t:putAttribute>

</t:insertTemplate>
