/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import mondrian.olap.MondrianProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author vsabadosh
 */
public class OlapSettingDiagnosticService implements Diagnostic {

    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.OLAP_SETTINGS, new DiagnosticCallback<Map<String, Object>>() {
                public Map<String, Object> getDiagnosticAttributeValue() {
                    Map<String, Object> olapSettings = new HashMap<String, Object>();
                    olapSettings.put(MondrianProperties.instance().DisableCaching.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().DisableCaching.get(), "JAM_005_DISABLE_MEMORY_CACHE"));
                    olapSettings.put(MondrianProperties.instance().GenerateFormattedSql.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().GenerateFormattedSql.get(), " JAM_006_GENERATE_FORMATTED_SQL"));
                    olapSettings.put(MondrianProperties.instance().QueryLimit.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().QueryLimit.get(), "JAM_009_QUERY_LIMIT"));
                    olapSettings.put(MondrianProperties.instance().ResultLimit.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().ResultLimit.get(), "JAM_010_RESULT_LIMIT"));
                    olapSettings.put(MondrianProperties.instance().MaxEvalDepth.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().MaxEvalDepth.get(), "JAM_022_EVALUATE_MAX_EVAL_DEPTH"));
                    olapSettings.put(MondrianProperties.instance().ExpCompilerClass.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().ExpCompilerClass.get(), "JAM_052_EXP_CALC_CLASS"));
                    olapSettings.put(MondrianProperties.instance().CaseSensitive.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().CaseSensitive.get(), "JAM_024_CASE_SENSITIVE"));
                    olapSettings.put(MondrianProperties.instance().CompareSiblingsByOrderKey.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().CompareSiblingsByOrderKey.get(), "JAM_023_COMPARE_SIBLINGS_BY_ORDER_KEY"));
                    olapSettings.put(MondrianProperties.instance().QueryTimeout.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().QueryTimeout.get(), "JAM_025_QUERY_TIMEOUT"));
                    olapSettings.put(MondrianProperties.instance().HighCardChunkSize.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().HighCardChunkSize.get(), "JAM_055_HIGH_CARD_CHUNK_SIZE"));
                    olapSettings.put(MondrianProperties.instance().SparseSegmentDensityThreshold.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().SparseSegmentDensityThreshold.get(),"JAM_012_SPARSE_DENSITY_THRESHOLD"));
                    olapSettings.put(MondrianProperties.instance().SparseSegmentCountThreshold.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().SparseSegmentCountThreshold.get(), "JAM_013_SPARSE_COUNT_THRESHOLD"));
                    olapSettings.put(MondrianProperties.instance().IgnoreInvalidMembers.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().IgnoreInvalidMembers.get(), "JAM_025_IGNORE_INVALID_MEMBERS"));
                    olapSettings.put(MondrianProperties.instance().IgnoreInvalidMembersDuringQuery.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().IgnoreInvalidMembersDuringQuery.get(), "JAM_026_IGNORE_INVALID_MEMBERS_DURING_QUERY"));
                    olapSettings.put(MondrianProperties.instance().NullMemberRepresentation.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().NullMemberRepresentation.get(), "JAM_027_NULL_MEMBER_REPRESENTATION"));
                    olapSettings.put(MondrianProperties.instance().IterationLimit.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().IterationLimit.get(), "JAM_028_ITERATION_LIMIT"));
                    olapSettings.put(MondrianProperties.instance().CrossJoinOptimizerSize.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().CrossJoinOptimizerSize.get(), "JAM_029_OPTIMIZER_SIZE"));
                    olapSettings.put(MondrianProperties.instance().IgnoreMeasureForNonJoiningDimension.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().IgnoreMeasureForNonJoiningDimension.get(), "JAM_033_IGNORE_MEASURE_FOR_NON_JOINING_PREFIX"));
                    olapSettings.put(MondrianProperties.instance().NeedDimensionPrefix.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().NeedDimensionPrefix.get(), "JAM_032_NEED_DIMENSION_PREFIX"));
                    olapSettings.put(MondrianProperties.instance().NullDenominatorProducesNull.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().NullDenominatorProducesNull.get(), "JAM_031_NULL_OR_ZERO_DENOMINATOR_PRODUCES_NULL"));
                    olapSettings.put(MondrianProperties.instance().UseAggregates.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().UseAggregates.get(), "JAM_014_ENABLE_AGGREGATES"));
                    olapSettings.put(MondrianProperties.instance().ChooseAggregateByVolume.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().ChooseAggregateByVolume.get(), "JAM_015_CHOOSE_AGGREGATES_BY_VOL"));
                    olapSettings.put(MondrianProperties.instance().OptimizePredicates.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().OptimizePredicates.get(), "JAM_016_AGGREGATES_OPTIMIZE_PREDICATES"));
                    olapSettings.put(MondrianProperties.instance().AggregateRules.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().AggregateRules.get(), "JAM_017_AGGREGATES_RULES"));
                    olapSettings.put(MondrianProperties.instance().AggregateRuleTag.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().AggregateRuleTag.get(), "JAM_018_AGGREGATES_RULE_TAG"));
                    olapSettings.put(MondrianProperties.instance().GenerateAggregateSql.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().GenerateAggregateSql.get(), "JAM_019_AGGREGATES_GENERATE_SQL"));
                    olapSettings.put(MondrianProperties.instance().JdbcFactoryClass.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().JdbcFactoryClass.get(), "JAM_020_AGGREGATES_JDBC_FACTORY_CLASS"));
                    olapSettings.put(MondrianProperties.instance().EnableExpCache.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableExpCache.get(), "JAM_035_EXP_CACHE_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().EnableRolapCubeMemberCache.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableRolapCubeMemberCache.get(), "JAM_036_ENABLE_CUBE_MEMBER_CACHE"));
                    olapSettings.put(MondrianProperties.instance().MaxConstraints.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().MaxConstraints.get(), "JAM_054_MAX_CONSTRAINTS"));
                    olapSettings.put(MondrianProperties.instance().EnableNativeCrossJoin.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableNativeCrossJoin.get(), "JAM_037_NATIVE_CROSS_JOIN_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().EnableNativeTopCount.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableNativeTopCount.get(), "JAM_038_NATIVE_TOP_COUNT_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().EnableNativeFilter.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableNativeFilter.get(), "JAM_039_NATIVE_FILTER_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().EnableNativeNonEmpty.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableNativeNonEmpty.get(), "JAM_040_NATIVE_NON_EMPTY_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().ExpandNonNative.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().ExpandNonNative.get(), "JAM_053_EXPAND_NON_NATIVE"));
                    olapSettings.put(MondrianProperties.instance().AlertNativeEvaluationUnsupported.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().AlertNativeEvaluationUnsupported.get(), "JAM_048_NATIVE_UNSUPPORTED_ALERT"));
                    olapSettings.put(MondrianProperties.instance().EnableGroupingSets.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableGroupingSets.get(), "JAM_041_GROUPING_SETS_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().MaxRows.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().MaxRows.get(), "JAM_004_XMLA_MAX_DRILL_THROUGH"));
                    olapSettings.put(MondrianProperties.instance().EnableTotalCount.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableTotalCount.get(), "JAM_042_XMLA_DRILL_THROUGH_TOTAL_COUNT_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().MemoryMonitor.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().MemoryMonitor.get(), "JAM_045_MEMORY_MONITOR_ENABLE"));
                    olapSettings.put(MondrianProperties.instance().MemoryMonitorThreshold.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().MemoryMonitorThreshold.get(), "JAM_046_MEMORY_MONITOR_THRESHOLD"));
                    olapSettings.put(MondrianProperties.instance().MemoryMonitorClass.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().MemoryMonitorClass.get(), "JAM_047_MEMORY_MONITOR_CLASS"));
                    olapSettings.put(MondrianProperties.instance().CellBatchSize.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().CellBatchSize.get(), "JAM_063_CELL_BATCH_SIZE"));
                    olapSettings.put(MondrianProperties.instance().DisableLocalSegmentCache.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().DisableLocalSegmentCache.get(), "JAM_062_DISABLE_LOCAL_SEGMENT_CACHE"));
                    olapSettings.put(MondrianProperties.instance().EnableDrillThrough.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableDrillThrough.get(), "JAM_070_ENABLE_DRILL_THROUGH"));
                    olapSettings.put(MondrianProperties.instance().EnableInMemoryRollup.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().EnableInMemoryRollup.get(), "JAM_064_ENABLE_IN_MEMORY_ROLLUP"));
                    olapSettings.put(MondrianProperties.instance().ExecutionHistorySize.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().ExecutionHistorySize.get(), "JAM_058_MONITOR_EXECUTION_HISTORY_SIZE"));
                    olapSettings.put(MondrianProperties.instance().FilterChildlessSnowflakeMembers.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().FilterChildlessSnowflakeMembers.get(), "JAM_065_FILTER_CHILD_LESS_SNOW_FLAKE_MEMBERS"));
                    olapSettings.put(MondrianProperties.instance().RolapConnectionShepherdNbThreads.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().RolapConnectionShepherdNbThreads.get(), "JAM_066_MAX_QUERY_THREADS"));
                    olapSettings.put(MondrianProperties.instance().RolapConnectionShepherdThreadPollingInterval.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().RolapConnectionShepherdThreadPollingInterval.get(), "JAM_067_SHEPHERD_THREAD_POLLING_INTERVAL"));
                    olapSettings.put(MondrianProperties.instance().SegmentCache.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().SegmentCache.get(), "JAM_059_SEGMENT_CACHE"));
                    olapSettings.put(MondrianProperties.instance().SegmentCacheManagerNumberCacheThreads.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().SegmentCacheManagerNumberCacheThreads.get(), "JAM_060_MAX_CACHE_THREADS"));
                    olapSettings.put(MondrianProperties.instance().SegmentCacheManagerNumberSqlThreads.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().SegmentCacheManagerNumberSqlThreads.get(), "JAM_061_MAX_SQL_THREADS"));
                    olapSettings.put(MondrianProperties.instance().StatisticsProviders.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().StatisticsProviders.get(), "JAM_069_STATISTICS_PROVIDER"));
                    olapSettings.put(MondrianProperties.instance().XmlaSchemaRefreshInterval.getPath(),
                            generateValueWithDescription(MondrianProperties.instance().XmlaSchemaRefreshInterval.get(), "JAM_068_SCHEMA_REFRESH_INTERVAL"));

                    return olapSettings;
                }
            }).build();
    }

    private String generateValueWithDescription(Object olapValue, String messagePath) {
        Locale locale = LocaleContextHolder.getLocale();
        String description = messageSource.getMessage(messagePath, null, "", locale);
        if (description == null || description.isEmpty()) {
            description = "";
        } else {
            description = " (" + description + ")";
        }
        return olapValue+description;
    }


}
