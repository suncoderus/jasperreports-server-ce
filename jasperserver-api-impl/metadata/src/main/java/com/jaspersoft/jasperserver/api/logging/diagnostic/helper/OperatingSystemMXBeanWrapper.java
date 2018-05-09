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
package com.jaspersoft.jasperserver.api.logging.diagnostic.helper;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author ogavavka
 */
public class OperatingSystemMXBeanWrapper{
    OperatingSystemMXBean osBean= ManagementFactory.getOperatingSystemMXBean();
    enum JVMTypes {
        sun, ibm, other
    }
    JVMTypes jvmType;
    public OperatingSystemMXBeanWrapper() {
        jvmType =JVMTypes.other;
        if (osBean.getClass().getName().equals("com.sun.management.OperatingSystem"))
            jvmType = JVMTypes.sun;
        if (osBean.getClass().getName().equals("com.ibm.lang.management.OperatingSystem"))
            jvmType = JVMTypes.ibm;
    }
    public String getArch() {


        return osBean.getArch();
    }
    public int getAvailableProcessors() {
        return osBean.getAvailableProcessors();
    }

    public String getName() {
        return osBean.getName();
    }

    public String getVersion() {
        return osBean.getVersion();
    }

    public double getSystemLoadAverage() {
        return osBean.getSystemLoadAverage();
    }
    public long getTotalPhysicalMemorySize() {
        Long physicalMemorySize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            physicalMemorySize = ((com.sun.management.OperatingSystemMXBean)osBean).getTotalPhysicalMemorySize();
        } else {
            physicalMemorySize = getInfo("TotalPhysicalMemorySize");
        }
        return physicalMemorySize;
    }
    public long getTotalSwapSpaceSize() {
        Long swapSpaceSize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            swapSpaceSize = ((com.sun.management.OperatingSystemMXBean)osBean).getTotalSwapSpaceSize();
        } else {
            swapSpaceSize = getInfo("TotalSwapSpaceSize");
        }
        return swapSpaceSize;
    }
    public long getFreeSwapSpaceSize() {
        Long freeSwapSpaceSize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            freeSwapSpaceSize = ((com.sun.management.OperatingSystemMXBean)osBean).getFreeSwapSpaceSize();
        } else {
            freeSwapSpaceSize = getInfo("FreeSwapSpaceSize");
        }
        return freeSwapSpaceSize;
    }
    public long getFreePhysicalMemorySize() {
        Long freePhysicalMemorySize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            freePhysicalMemorySize = ((com.sun.management.OperatingSystemMXBean)osBean).getFreePhysicalMemorySize();
        } else {
            freePhysicalMemorySize = getInfo("FreePhysicalMemorySize");
        }
        return freePhysicalMemorySize;
    }
    public long getProcessCpuTime() {
        Long processCpuTime = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            processCpuTime = ((com.sun.management.OperatingSystemMXBean)osBean).getProcessCpuTime();
        } else {
            processCpuTime = getInfo("ProcessCpuTime");
        }
        return processCpuTime;
    }
    public long getCommittedVirtualMemorySize() {
        Long committedVirtualMemorySize = -1L;
        if (jvmType.equals(JVMTypes.sun)) {
            committedVirtualMemorySize = ((com.sun.management.OperatingSystemMXBean)osBean).getCommittedVirtualMemorySize();
        } else {
            committedVirtualMemorySize = getInfo("CommittedVirtualMemorySize");
        }
        return committedVirtualMemorySize;
    }

    private Long getInfo(String attr) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            Object attribute = mBeanServer.getAttribute(new ObjectName("java.lang","type","OperatingSystem"), attr);
            if (attribute instanceof Long) {
                Long value = (Long) attribute;
                return value;
            } else if (attribute instanceof Double) {
                double value = (Double) attribute;
                Long value1 = (long) value;
                return value1;
            }

        } catch (MBeanException e) {

        } catch (AttributeNotFoundException e) {

        } catch (InstanceNotFoundException e) {

        } catch (ReflectionException e) {

        } catch (MalformedObjectNameException e) {

        }
        return -1L;
    }
}
