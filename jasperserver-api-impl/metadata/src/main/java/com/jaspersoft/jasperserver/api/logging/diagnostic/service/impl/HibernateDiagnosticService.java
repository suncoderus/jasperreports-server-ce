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

import java.util.*;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.hibernate.jmx.StatisticsService;

/**
 * @author vsabadosh
 */
public class HibernateDiagnosticService implements Diagnostic {

    private StatisticsService hibernateStatistics;
    
    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_START_TIME, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getStartTime();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_STATISTICS_ENABLED, new DiagnosticCallback<Boolean>() {
                @Override
                public Boolean getDiagnosticAttributeValue() {
                    return hibernateStatistics.isStatisticsEnabled();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_DELETE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getEntityDeleteCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_INSERT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getEntityInsertCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_LOAD_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getEntityLoadCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_FETCH_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getEntityFetchCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_UPDATE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getEntityUpdateCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getQueryExecutionCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_HIT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getQueryCacheHitCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_MAX_TIME, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getQueryExecutionMaxTime();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_MIS_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getQueryCacheMissCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_PUT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getQueryCachePutCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_FLUSH_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getFlushCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_CONNECT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getConnectCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_HIT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getSecondLevelCacheHitCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_MISS_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getSecondLevelCacheMissCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_PUT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getSecondLevelCachePutCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SESSION_CLOSE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getSessionCloseCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SESSION_OPEN_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getSessionOpenCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_LOAD_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getCollectionLoadCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_FETCH_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getCollectionFetchCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_UPDATE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getCollectionUpdateCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_REMOVE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getCollectionRemoveCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_RECREATE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getCollectionRecreateCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_ROLE_NAMES, new DiagnosticCallback<List<String>>() {
                @Override
                public List<String> getDiagnosticAttributeValue() {
                    String[] roleNames = hibernateStatistics.getCollectionRoleNames();
                    if (roleNames != null && roleNames.length > 0) {
                        return Arrays.asList(roleNames);
                    } else {
                        return null;
                    }
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_NAMES, new DiagnosticCallback<List<String>>() {
                @Override
                public List<String> getDiagnosticAttributeValue() {
                    String[] entityNames = hibernateStatistics.getEntityNames();
                    if (entityNames != null && entityNames.length > 0) {
                        return Arrays.asList(entityNames);
                    } else {
                        return null;
                    }
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERIES, new DiagnosticCallback<List<String>>() {
                @Override
                public List<String> getDiagnosticAttributeValue() {
                    String[] entityNames = hibernateStatistics.getQueries();
                    if (entityNames != null && entityNames.length > 0) {
                        return Arrays.asList(entityNames);
                    } else {
                        return null;
                    }
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_REGION_NAMES, new DiagnosticCallback<List<String>>() {
                @Override
                public List<String> getDiagnosticAttributeValue() {
                    String[] entityNames = hibernateStatistics.getSecondLevelCacheRegionNames();
                    if (entityNames != null && entityNames.length > 0) {
                        return Arrays.asList(entityNames);
                    } else {
                        return null;
                    }
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_SUCCESSFUL_TRANSACTION_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getSuccessfulTransactionCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_TRANSACTION_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getTransactionCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_CLOSE_STATEMENT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getCloseStatementCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_PREPARE_STATEMENT_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getPrepareStatementCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_OPTIMISTIC_FAILURE_COUNT, new DiagnosticCallback<Long>() {
                @Override
                public Long getDiagnosticAttributeValue() {
                    return hibernateStatistics.getOptimisticFailureCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_MAX_TIME_QUERY_STRING, new DiagnosticCallback<String>() {
                @Override
                public String getDiagnosticAttributeValue() {
                    return hibernateStatistics.getQueryExecutionMaxTimeQueryString();
                }
            }).build();
    }

    public void setHibernateStatistics(StatisticsService hibernateStatistics) {
        this.hibernateStatistics = hibernateStatistics;
    }
}
