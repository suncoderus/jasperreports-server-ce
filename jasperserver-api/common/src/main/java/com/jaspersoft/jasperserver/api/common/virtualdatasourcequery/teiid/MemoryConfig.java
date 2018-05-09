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
package com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
@JasperServerAPI
public interface MemoryConfig {

    /*
     * return the disk directory for caching
     * @return disk storage directory
     */
    public String getDiskDirectory();

    /**
	 * returns whether it is using disk storage
	 * @return a boolean for using disk storage
	 */
    public boolean isUseDisk();

    /**
     * Get the nominal batch size target
     * Usage of extremely large VM sizes and or datasets requires additional considerations. Teiid has a non-negligible
     * amount of overhead per batch/table page on the order of 100-200 bytes. Depending on the data types involved each
     * full batch/table page will represent a variable number of rows (a power of two multiple above or below the
     * processor batch size). If you are dealing with datasets with billions of rows and you run into OutOfMemory
     * issues, consider increasing the processor-batch-size to force the allocation of larger batches and table pages
     * @return  the processor batch size
     */
    public int getProcessorBatchSize();

     /**
     * Get the batch size to use when reading data from a connector.
     * @return Batch size (# of rows)
     */
//    public int getConnectorBatchSize();

    /*
     * get the max open files for caching in cache directory
     * @return the max open files
     */
    public int getMaxOpenFiles();

     /*
     * get the max file size (in MB) (default 2048MB)
     * @return the max file size
     */
    public long getMaxFileSize();

    /*
     * setting determines the total size in kilobytes of batches that can be used by one active plan may be in addition
     * to the memory held based on max-reserve-kb. Typical minimum memory required by Teiid when all the active plans
     * are active is #active-plans*max-processing-kb. The default value of -1 will auto-calculate a typical max based
     * upon the max heap available to the VM and max active plans. The auto-calculated value assumes a 64bit
     * architecture and will limit processing batch usage to 10% of memory beyond the first 300 megabytes (which are
     * assumed for use by the AS and other Teiid purposes)
     * @return the max processing Kb
     */
    public int getMaxProcessingKb();

    /*
     * setting determines the total size in kilobytes of batches that can be held by the BufferManager in memory.
     * This number does not account for persistent batches held by soft (such as index pages) or weak references.
     * The default value of -1 will auto-calculate a typical max based upon the max heap available to the VM.
     * The auto-calculated value assumes a 64bit architecture and will limit buffer usage to 50% of the first
     * gigabyte of memory beyond the first 300 megabytes (which are assumed for use by the AS and other Teiid purposes)
     * and 75% of the memory beyond that.
     * @return the max reserve Kb
     */
    public int getMaxReserveKb();

    /**
     * get the max amount of buffer space for caching (file storage) in MB
     * For table page and result batches the buffer manager will we a limited number of files that are dedicated to
     * a particular storage size. However, as mentioned in the installation, creation of Teiid lob values (for example
     * through SQL/XML) will typically create one file per lob once the lob exceeds the allowable in memory size of
     * 8KB. In heavy usage scenarios, consider pointing the buffer directory on a partition that is routinely
     * defragmented. By default Teiid will use up to 50GB of disk space. This is tracked in terms of the number of
     * bytes written by Teiid. For large data sets, you may need to increase the max-buffer-space setting.
     * @return the max buffer space
     */
    public long getMaxBufferSpace();

    /*
     * returns whether persisted inline lobs in file storage
     * @return inline lobs
     */
    public boolean isInlineLobs();

     /*
     * get the memory space for caching (in MB)
     * set the value to -1 for using approximately 25% of what's set aside for the reserved (default -1)
     * @return the memory buffer space
     */
    public long getMemoryBufferSpace();

    /*
     * get the max storage size for object (in byte)
     * This represents the individual batch page size. If the processor-batch-size is increased and/or you are dealing
     * with extremely wide result sets (several hundred columns), then the default setting of 8MB for the
     * max-storage-object-size may be too low. The inline-lobs also account in this size if batch contains them.
     * The sizing for max-storage-object-size is in terms of serialized size, which will be much closer to the raw
     * data size than the Java memory footprint estimation used for max-reserved-kb. max-storage-object-size should
     * not be set too large relative to memory-buffer-space since it will reduce the performance of the memory buffer.
     * The memory buffer supports only 1 concurrent writer for each max-storage-object-size of the memory-buffer-space.
     * @return the max storage size for object
     */
    public int getMaxStorageObjectSize();

    /*
     * returns whether it uses direct byte buffer (off heap = true) or heap byte buffer (off heap = false)
     * Take advantage of the BufferManager memory buffer to access system memory without allocating it to the heap.
     * Setting memory-buffer-off-heap to "true" will allocate the Teiid memory buffer off heap. Depending on whether
     * your installation is dedicated to Teiid and the amount of system memory available, this may be preferable to
     * on-heap allocation. The primary benefit is additional memory usage for Teiid without additional garbage
     * collection tuning. This becomes especially important in situations where more than 32GB of memory is desired
     * for the VM. Note that when using off-heap allocation, the memory must still be available to the java process and
     * that setting the value of memory-buffer-space too high may cause the VM to swap rather than reside in memory.
     * With large off-heap buffer sizes (greater than several gigabytes) you may also need to adjust VM settings.
     * @return whether memory buffer off heap value
     */
    public boolean isMemoryBufferOffHeap();

}
