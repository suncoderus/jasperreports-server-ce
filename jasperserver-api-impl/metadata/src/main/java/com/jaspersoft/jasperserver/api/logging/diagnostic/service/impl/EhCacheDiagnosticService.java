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
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.management.CacheStatistics;

import java.util.Map;

/**
 * Implementation of the EhCache Diagnostic service (Service which collecting statistics and configuration on specified cache).
 *
 * @author ogavavka, vsabadosh
 */
public class EhCacheDiagnosticService implements Diagnostic {

    private Ehcache cache;

    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
    final CacheConfiguration cacheConfig = cache.getCacheConfiguration();
        final CacheStatistics finalStatistics = getCacheStatistics(cache);
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_ACCURACY, new DiagnosticCallback<Integer>() {
                public Integer getDiagnosticAttributeValue() {
                    return finalStatistics.getStatisticsAccuracy();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_ACCURACYDESCR, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return finalStatistics.getStatisticsAccuracyDescription();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_OBJECTCOUNT, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getObjectCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEHIT_PERCENTAGE, new DiagnosticCallback<Double>() {
                public Double getDiagnosticAttributeValue() {
                    return finalStatistics.getCacheHitPercentage();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEHITS, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getCacheHits();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEMISS_PERCENTAGE, new DiagnosticCallback<Double>() {
                public Double getDiagnosticAttributeValue() {
                    return finalStatistics.getCacheMissPercentage();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_CACHEMISSES, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getCacheMisses();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_DISKSTORECOUNT, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getDiskStoreObjectCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITT_PERCENTAGE, new DiagnosticCallback<Double>() {
                public Double getDiagnosticAttributeValue() {
                    return finalStatistics.getOnDiskHitPercentage();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITS, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getOnDiskHits();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_DISKMISSES, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getOnDiskMisses();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_DISKHITS, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getOnDiskHits();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYSTORECOUNT, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getMemoryStoreObjectCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYHITT_PERCENTAGE, new DiagnosticCallback<Double>() {
                public Double getDiagnosticAttributeValue() {
                    return finalStatistics.getInMemoryHitPercentage();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYHITS, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getInMemoryHits();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_MEMORYMISSES, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getInMemoryMisses();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPSTORECOUNT, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getOffHeapStoreObjectCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPHITT_PERCENTAGE, new DiagnosticCallback<Double>() {
                public Double getDiagnosticAttributeValue() {
                    return finalStatistics.getOffHeapHitPercentage();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPHITS, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getOffHeapHits();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_OFFHEAPMISSES, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getOffHeapMisses();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_WRITEMAXQUEUE, new DiagnosticCallback<Integer>() {
                public Integer getDiagnosticAttributeValue() {
                    return finalStatistics.getWriterMaxQueueSize();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_STAT_WRITEQUEUELENGTH, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return finalStatistics.getWriterQueueLength();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_STATISTICS, new DiagnosticCallback<Boolean>() {
                public Boolean getDiagnosticAttributeValue() {
                    return cacheConfig.getStatistics();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_DISKSPOOL, new DiagnosticCallback<Integer>() {
                public Integer getDiagnosticAttributeValue() {
                    return cacheConfig.getDiskSpoolBufferSizeMB();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_DISKEXPIRYTHREAD, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getDiskExpiryThreadIntervalSeconds();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_LOGGING, new DiagnosticCallback<Boolean>() {
                public Boolean getDiagnosticAttributeValue() {
                    return cacheConfig.getLogging();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALDISK, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxBytesLocalDisk();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALHEAP, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxBytesLocalHeap();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MBYTE_LOCALOFFHEAP, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxBytesLocalOffHeap();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MELEMENTS_LOCALDISK, new DiagnosticCallback<Integer>() {
                public Integer getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxElementsOnDisk();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MELEMENTS_MEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxEntriesLocalHeap();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_TIME_IDLE, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getTimeToIdleSeconds();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_TIME_LIVE, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getTimeToLiveSeconds();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MEMORYSTORE_POLICY, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return cacheConfig.getMemoryStoreEvictionPolicy().toString();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MENTRYES_MEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxEntriesLocalDisk();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_MENTRYES_LOCALHEAP, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return cacheConfig.getMaxEntriesLocalHeap();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_DISKEPERSISTENT, new DiagnosticCallback<Boolean>() {
                public Boolean getDiagnosticAttributeValue() {
                    return cacheConfig.isDiskPersistent();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_ETERNAL, new DiagnosticCallback<Boolean>() {
                public Boolean getDiagnosticAttributeValue() {
                    return cacheConfig.isEternal();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_OVERFLOW_DISK, new DiagnosticCallback<Boolean>() {
                public Boolean getDiagnosticAttributeValue() {
                    return cacheConfig.isOverflowToDisk();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EHCACHE_CONF_OVERFLOW_OFFHEAP, new DiagnosticCallback<Boolean>() {
                public Boolean getDiagnosticAttributeValue() {
                    return cacheConfig.isOverflowToOffHeap();
                }
            }).build();
    }

    protected CacheStatistics getCacheStatistics(Ehcache cache) {
        return new CacheStatistics(cache);
    }

    public void setCache(Ehcache cache) {
        this.cache = cache;
    }

}
