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
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.hibernate.jmx.StatisticsService;
import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.junit.Test;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link HibernateDiagnosticService}
 *
 * @author vsabadosh
 */
public class HibernateDiagnosticServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private HibernateDiagnosticService hibernateDiagnosticService;

    @InjectInto(property = "hibernateStatistics")
    private Mock<StatisticsService> hibernateStatisticsMock;
    private long startTime = 100000;
    private boolean statisticsEnabled = true;
    private long entityDeleteCount = 3;
    private long entityInsertCount = 4;
    private long entityLoadCount = 5;
    private long entityFetchCount = 6;
    private long entityUpdateCount = 7;
    private long queryExecutionCount = 8;
    private long queryCacheHitCount = 9;
    private long queryExecutionMaxTime = 10;
    private long queryCacheMissCount = 11;
    private long queryCachePutCount = 12;
    private long flushCount = 13;
    private long connectCount = 14;
    private long secondLevelCacheHitCount = 15;
    private long secondLevelCacheMissCount = 16;
    private long secondLevelCachePutCount = 17;
    private long sessionCloseCount = 18;
    private long sessionOpenCount = 19;
    private long collectionLoadCount = 20;
    private long collectionFetchCount = 21;
    private long collectionUpdateCount = 22;
    private long collectionRemoveCount = 23;
    private long collectionRecreateCount = 24;
    private String[] roleNames;
    private String[] entityNames;
    private String[] queries;
    private String[] secondLevelCacheRegionNames;
    private long successfulTransactionCount = 25;
    private long transactionCount = 26;
    private long closeStatementCount = 27;
    private long prepareStatementCount = 28;
    private long optimisticFailureCount = 29;
    private String queryExecutionMaxTimeQueryString = "Execution max time query string";

    @Before
    public void setUp() {
        hibernateStatisticsMock.returns(startTime).getStartTime();
        hibernateStatisticsMock.returns(statisticsEnabled).isStatisticsEnabled();
        hibernateStatisticsMock.returns(entityDeleteCount).getEntityDeleteCount();
        hibernateStatisticsMock.returns(entityInsertCount).getEntityInsertCount();
        hibernateStatisticsMock.returns(entityLoadCount).getEntityLoadCount();
        hibernateStatisticsMock.returns(entityFetchCount).getEntityFetchCount();
        hibernateStatisticsMock.returns(entityUpdateCount).getEntityUpdateCount();
        hibernateStatisticsMock.returns(queryExecutionCount).getQueryExecutionCount();
        hibernateStatisticsMock.returns(queryCacheHitCount).getQueryCacheHitCount();
        hibernateStatisticsMock.returns(queryExecutionMaxTime).getQueryExecutionMaxTime();
        hibernateStatisticsMock.returns(queryCacheMissCount).getQueryCacheMissCount();
        hibernateStatisticsMock.returns(queryCachePutCount).getQueryCachePutCount();
        hibernateStatisticsMock.returns(flushCount).getFlushCount();
        hibernateStatisticsMock.returns(connectCount).getConnectCount();
        hibernateStatisticsMock.returns(secondLevelCacheHitCount).getSecondLevelCacheHitCount();
        hibernateStatisticsMock.returns(secondLevelCacheMissCount).getSecondLevelCacheMissCount();
        hibernateStatisticsMock.returns(secondLevelCachePutCount).getSecondLevelCachePutCount();
        hibernateStatisticsMock.returns(sessionCloseCount).getSessionCloseCount();
        hibernateStatisticsMock.returns(sessionOpenCount).getSessionOpenCount();
        hibernateStatisticsMock.returns(collectionLoadCount).getCollectionLoadCount();
        hibernateStatisticsMock.returns(collectionFetchCount).getCollectionFetchCount();
        hibernateStatisticsMock.returns(collectionUpdateCount).getCollectionUpdateCount();
        hibernateStatisticsMock.returns(collectionRemoveCount).getCollectionRemoveCount();
        hibernateStatisticsMock.returns(collectionRecreateCount).getCollectionRecreateCount();
        roleNames = new String[2];
        roleNames[0] = "Role1";
        roleNames[1] = "Role2";
        hibernateStatisticsMock.returns(roleNames).getCollectionRoleNames();
        entityNames = new String[2];
        entityNames[0] = "Entity1";
        entityNames[1] = "Entity2";
        hibernateStatisticsMock.returns(entityNames).getEntityNames();
        queries = new String[3];
        queries[0] = "Query 1";
        queries[1] = "Query 2";
        queries[2] = "Query 3";
        hibernateStatisticsMock.returns(queries).getQueries();
        secondLevelCacheRegionNames = new String[4];
        secondLevelCacheRegionNames[0] = "Region 1";
        secondLevelCacheRegionNames[1] = "Region 2";
        secondLevelCacheRegionNames[2] = "Region 3";
        secondLevelCacheRegionNames[3] = "Region 4";
        hibernateStatisticsMock.returns(secondLevelCacheRegionNames).getSecondLevelCacheRegionNames();
        hibernateStatisticsMock.returns(successfulTransactionCount).getSuccessfulTransactionCount();
        hibernateStatisticsMock.returns(transactionCount).getTransactionCount();
        hibernateStatisticsMock.returns(closeStatementCount).getCloseStatementCount();
        hibernateStatisticsMock.returns(prepareStatementCount).getPrepareStatementCount();
        hibernateStatisticsMock.returns(optimisticFailureCount).getOptimisticFailureCount();
        hibernateStatisticsMock.returns(queryExecutionMaxTimeQueryString).getQueryExecutionMaxTimeQueryString();
    }

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = hibernateDiagnosticService.getDiagnosticData();
        //Test total size of diagnostic attributes
        assertEquals(34, resultDiagnosticData.size());

        //Test actual values of target diagnostic attributes
        assertEquals(startTime, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_START_TIME, null, null)).getDiagnosticAttributeValue());
        assertEquals(statisticsEnabled, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_STATISTICS_ENABLED, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityDeleteCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_DELETE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityInsertCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_INSERT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityLoadCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_LOAD_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityFetchCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_FETCH_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(entityUpdateCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_UPDATE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryExecutionCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryCacheHitCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_HIT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryExecutionMaxTime, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_MAX_TIME, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryCacheMissCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_MIS_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryCachePutCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_CACHE_PUT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(flushCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_FLUSH_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(connectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_CONNECT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(secondLevelCacheHitCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_HIT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(secondLevelCacheMissCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_MISS_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(secondLevelCachePutCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_PUT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(sessionCloseCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SESSION_CLOSE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(sessionOpenCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SESSION_OPEN_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionLoadCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_LOAD_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionFetchCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_FETCH_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionUpdateCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_UPDATE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionRemoveCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_REMOVE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(collectionRecreateCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_RECREATE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(2, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_ROLE_NAMES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Role1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_COLLECTION_ROLE_NAMES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(2, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_NAMES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Entity1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_ENTITY_NAMES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(3, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERIES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Query 1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERIES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(4, ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_REGION_NAMES, null, null)).getDiagnosticAttributeValue()).size());
        assertEquals("Region 1", ((List)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SECOND_LEVEL_CACHE_REGION_NAMES, null, null)).getDiagnosticAttributeValue()).get(0));
        assertEquals(successfulTransactionCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_SUCCESSFUL_TRANSACTION_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(transactionCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_TRANSACTION_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(closeStatementCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_CLOSE_STATEMENT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(prepareStatementCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_PREPARE_STATEMENT_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(optimisticFailureCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_OPTIMISTIC_FAILURE_COUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(queryExecutionMaxTimeQueryString, resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.HIBERNATE_QUERY_EXECUTION_MAX_TIME_QUERY_STRING, null, null)).getDiagnosticAttributeValue());
    }
}
