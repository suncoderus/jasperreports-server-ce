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
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.PartialMock;
import net.sf.ehcache.management.CacheStatistics;

import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Tests for {@link EhCacheDiagnosticService}
 *
 * @author vsabadosh
 */
public class EhCacheDiagnosticServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private PartialMock<EhCacheDiagnosticService> ehCacheDiagnosticService;

    private Mock<CacheStatistics> finalStatistics;
    private Mock<CacheConfiguration> cacheConfig;

    private int statisticsAccuracy = 5;
    private String statisticsAccuracyDescription = "StatisticsAccuracyDescription";
    private Long objectCount = 6L;
    private Double cacheHitPercentage = 7.0;
    private Long cacheHits = 8L;
    private Double cacheMissPercentage = 9.0;
    private Long cacheMisses = 10L;
    private Long diskStoreObjectCount = 11L;
    private Double onDiskHitPercentage = 12.0;
    private Long onDiskHits = 13L;
    private Long onDiskMisses = 14L;
    private Long memoryStoreObjectCount = 15L;
    private Double inMemoryHitPercentage = 16.0;
    private Long inMemoryHits = 17L;
    private Long inMemoryMisses = 18L;
    private Long offHeapStoreObjectCount = 19L;
    private Double OffHeapHitPercentage = 20.0;
    private Long OffHeapHits = 21L;
    private Long offHeapMisses = 22L;
    private Integer writeMaxQueueSize = 23;
    private Long writeQueueLength = 24L;

    private Integer conf_DiskSpoolBufferSizeMB = 25;
    private Long conf_DiskExpiryThreadIntervalSeconds = 26L;
    private Boolean conf_LoggingEnabled = false;
    private Long conf_MaxBytesLocalDisk = 27L;
    private Long conf_MaxBytesLocalHeap = 28L;
    private Long conf_MaxBytesLocalOffHeap = 29L;
    private Integer conf_MaxElementsOnDisk = 30;
    private Long conf_TimeToIdleSeconds = 32L;
    private Long conf_TimeToLiveSeconds = 33L;
    private Long conf_MaxEntriesLocalDisk = 34L;
    private Long conf_MaxEntriesLocalHeap = 35L;
    private Boolean diskPersistent = true;
    private Boolean eternal = false;
    private Boolean overflowToDisk = true;
    private Boolean overflowToOffHeap = false;

    @Before
    public void setUp() {
        finalStatistics = MockUnitils.createMock(CacheStatistics.class);

        finalStatistics.returns(statisticsAccuracy).getStatisticsAccuracy();
        finalStatistics.returns(statisticsAccuracyDescription).getStatisticsAccuracyDescription();
        finalStatistics.returns(objectCount).getObjectCount();
        finalStatistics.returns(cacheHitPercentage).getCacheHitPercentage();
        finalStatistics.returns(cacheHits).getCacheHits();
        finalStatistics.returns(cacheMissPercentage).getCacheMissPercentage();
        finalStatistics.returns(cacheMisses).getCacheMisses();
        finalStatistics.returns(diskStoreObjectCount).getDiskStoreObjectCount();
        finalStatistics.returns(onDiskHitPercentage).getOnDiskHitPercentage();
        finalStatistics.returns(onDiskHits).getOnDiskHits();
        finalStatistics.returns(onDiskMisses).getOnDiskMisses();
        finalStatistics.returns(memoryStoreObjectCount).getMemoryStoreObjectCount();
        finalStatistics.returns(inMemoryHitPercentage).getInMemoryHitPercentage();
        finalStatistics.returns(inMemoryHits).getInMemoryHits();
        finalStatistics.returns(inMemoryMisses).getInMemoryMisses();
        finalStatistics.returns(offHeapStoreObjectCount).getOffHeapStoreObjectCount();
        finalStatistics.returns(OffHeapHitPercentage).getOffHeapHitPercentage();
        finalStatistics.returns(OffHeapHits).getOffHeapHits();
        finalStatistics.returns(offHeapMisses).getOffHeapMisses();
        finalStatistics.returns(writeMaxQueueSize).getWriterMaxQueueSize();
        finalStatistics.returns(writeQueueLength).getWriterQueueLength();

        cacheConfig = MockUnitils.createMock(CacheConfiguration.class);

        cacheConfig.returns(conf_DiskSpoolBufferSizeMB).getDiskSpoolBufferSizeMB();
        cacheConfig.returns(conf_DiskExpiryThreadIntervalSeconds).getDiskExpiryThreadIntervalSeconds();
        cacheConfig.returns(conf_LoggingEnabled).getLogging();
        cacheConfig.returns(conf_MaxBytesLocalDisk).getMaxBytesLocalDisk();
        cacheConfig.returns(conf_MaxBytesLocalHeap).getMaxBytesLocalHeap();
        cacheConfig.returns(conf_MaxBytesLocalOffHeap).getMaxBytesLocalOffHeap();
        cacheConfig.returns(conf_MaxElementsOnDisk).getMaxElementsOnDisk();
        cacheConfig.returns(conf_MaxEntriesLocalHeap).getMaxEntriesLocalHeap();
        cacheConfig.returns(conf_TimeToIdleSeconds).getTimeToIdleSeconds();
        cacheConfig.returns(conf_TimeToLiveSeconds).getTimeToLiveSeconds();
        cacheConfig.returns(conf_MaxEntriesLocalDisk).getMaxEntriesLocalDisk();
        cacheConfig.returns(conf_MaxEntriesLocalHeap).getMaxEntriesLocalHeap();
        cacheConfig.returns(diskPersistent).isDiskPersistent();
        cacheConfig.returns(eternal).isEternal();
        cacheConfig.returns(overflowToDisk).isOverflowToDisk();
        cacheConfig.returns(overflowToOffHeap).isOverflowToOffHeap();

        Mock<Ehcache> cache = MockUnitils.createMock(Ehcache.class);

        cache.returns(cacheConfig).getCacheConfiguration();
        ehCacheDiagnosticService.getMock().setCache(cache.getMock());
        ehCacheDiagnosticService.returns(finalStatistics).getCacheStatistics(cache.getMock());
    }

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = ehCacheDiagnosticService.getMock().getDiagnosticData();

        //Test total size of diagnostic attributes collected from SessionRegistryDiagnosticService
        assertEquals(39, resultDiagnosticData.size());

        //Test actual values of diagnostic attributes
        assertEquals(statisticsAccuracy, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_ACCURACY, null, null)).getDiagnosticAttributeValue());
        assertEquals(statisticsAccuracyDescription, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_ACCURACYDESCR, null, null)).getDiagnosticAttributeValue());
        assertEquals(objectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OBJECTCOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEHIT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheMissPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEMISS_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(cacheMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(diskStoreObjectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKSTORECOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(onDiskHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(onDiskHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(onDiskMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_DISKMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(memoryStoreObjectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYSTORECOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(inMemoryHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYHITT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(inMemoryHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(inMemoryMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(offHeapStoreObjectCount, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPSTORECOUNT, null, null)).getDiagnosticAttributeValue());
        assertEquals(OffHeapHitPercentage, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPHITT_PERCENTAGE, null, null)).getDiagnosticAttributeValue());
        assertEquals(OffHeapHits, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPHITS, null, null)).getDiagnosticAttributeValue());
        assertEquals(offHeapMisses, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPMISSES, null, null)).getDiagnosticAttributeValue());
        assertEquals(writeMaxQueueSize, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_WRITEMAXQUEUE, null, null)).getDiagnosticAttributeValue());
        assertEquals(writeQueueLength, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_STAT_WRITEQUEUELENGTH, null, null)).getDiagnosticAttributeValue());

        assertEquals(conf_DiskSpoolBufferSizeMB, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_DISKSPOOL, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_DiskExpiryThreadIntervalSeconds, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_DISKEXPIRYTHREAD, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_LoggingEnabled, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_LOGGING, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxBytesLocalDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALDISK, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxBytesLocalHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALHEAP, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxBytesLocalOffHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALOFFHEAP, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxElementsOnDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MELEMENTS_LOCALDISK, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxEntriesLocalHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MELEMENTS_MEMORY, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_TimeToIdleSeconds, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_TIME_IDLE, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_TimeToLiveSeconds, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_TIME_LIVE, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxEntriesLocalDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MENTRYES_MEMORY, null, null)).getDiagnosticAttributeValue());
        assertEquals(conf_MaxEntriesLocalHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_MENTRYES_LOCALHEAP, null, null)).getDiagnosticAttributeValue());
        assertEquals(diskPersistent, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_DISKEPERSISTENT, null, null)).getDiagnosticAttributeValue());
        assertEquals(eternal, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_ETERNAL, null, null)).getDiagnosticAttributeValue());
        assertEquals(overflowToDisk, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_OVERFLOW_DISK, null, null)).getDiagnosticAttributeValue());
        assertEquals(overflowToOffHeap, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.EHCACHE_CONF_OVERFLOW_OFFHEAP, null, null)).getDiagnosticAttributeValue());
    }

}
