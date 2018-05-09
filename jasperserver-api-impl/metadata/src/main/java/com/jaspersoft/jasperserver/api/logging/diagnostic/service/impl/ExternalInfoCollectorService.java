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
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.OperatingSystemMXBeanWrapper;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;

import java.io.File;
import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ogavavka, vsabadosh
 */
public class ExternalInfoCollectorService implements Diagnostic {

    private MemoryMXBean memoryBean;
    private OperatingSystemMXBeanWrapper osBean;
    private RuntimeMXBean runtimeBean;
    private ClassLoadingMXBean classLoadingBean;

    public ExternalInfoCollectorService() {
        memoryBean = ManagementFactory.getMemoryMXBean();
        osBean = new OperatingSystemMXBeanWrapper();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        classLoadingBean = ManagementFactory.getClassLoadingMXBean();
    }

    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_AVAILABLEPROCESSORS, new DiagnosticCallback<Integer>() {
                public Integer getDiagnosticAttributeValue() {
                    return Runtime.getRuntime().availableProcessors();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_FREEMEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return Runtime.getRuntime().freeMemory();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_MAXMEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return Runtime.getRuntime().maxMemory();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_TOTALMEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return Runtime.getRuntime().totalMemory();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_ENVIRONMENT, new DiagnosticCallback<Map<String, String>>() {
                public Map<String, String> getDiagnosticAttributeValue() {
                    Map<String, String> map = new HashMap<String, String>();
                    map.putAll(System.getenv());
                    return map;
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_PROPERTIES, new DiagnosticCallback<Map<String, String>>() {
                public Map<String, String> getDiagnosticAttributeValue() {
                    HashMap<String, String> result = new HashMap<String, String>();
                    for (String prop : System.getProperties().stringPropertyNames()) {
                        result.put(prop, System.getProperty(prop));
                    }
                    return result;
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_JVMHEAPMEMORY, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return memoryBean.getHeapMemoryUsage().toString();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_JVMNONHEAPMEMORY, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return memoryBean.getNonHeapMemoryUsage().toString();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSNAME, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return osBean.getName();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSARCH , new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return osBean.getArch();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSVERSION, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return osBean.getVersion();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSSYSTEMLOADAVERAGE, new DiagnosticCallback<Double>() {
                public Double getDiagnosticAttributeValue() {
                    return osBean.getSystemLoadAverage();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSTOTALPHYSICALMEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return osBean.getTotalPhysicalMemorySize();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSTOTALSWAPSPACESIZE, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return osBean.getTotalSwapSpaceSize();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSFREESWAPSPACESIZE, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return osBean.getFreeSwapSpaceSize();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSFREEPHYSICALMEMORY, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return osBean.getFreePhysicalMemorySize();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSPROCESSORCPUTIME, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return osBean.getProcessCpuTime();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_OSCOMMITEDVIRTUALMEMORYSIZE, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return osBean.getCommittedVirtualMemorySize();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_CLASSLOADERTOTALLOADEDCLASSESCOUNT, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return classLoadingBean.getTotalLoadedClassCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_CLASSLOADERUNLOADEDCLASSCOUNT, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return classLoadingBean.getUnloadedClassCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_CLASSLOADERLOADEDCLASSCOUNT, new DiagnosticCallback<Integer>() {
                public Integer getDiagnosticAttributeValue() {
                    return classLoadingBean.getLoadedClassCount();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMEBOOTCLASSPATH, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getBootClassPath();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMECLASSPATH, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getClassPath();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMELIBRARYPATH, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getLibraryPath();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMEUPTIME, new DiagnosticCallback<Long>() {
                public Long getDiagnosticAttributeValue() {
                    return runtimeBean.getUptime();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMEVMNAME, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getVmName();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMEVMVENDOR, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getVmVendor();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMEVMVERSION, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getVmVersion();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_RUNTIMEINPUTARGUMENTS, new DiagnosticCallback<String>() {
                public String getDiagnosticAttributeValue() {
                    return runtimeBean.getInputArguments().toString();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.EXT_DISKSFREESPACE, new DiagnosticCallback<HashMap<String,Long>>() {
                public HashMap<String,Long> getDiagnosticAttributeValue() {
                    File[] nodes = File.listRoots();
                    HashMap<String,Long> result = new HashMap<String, Long>();
                    for(File node: nodes) {
                        result.put(node.toString(), node.getFreeSpace());
                    }
                    return result;
                }
         }).build();
    }
}

