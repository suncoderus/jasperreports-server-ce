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

package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.util.StreamCompression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SwapFileVirtualizerFactory extends AbstractIndividualVirtualizerFactory {

	private static final Log log = LogFactory.getLog(SwapFileVirtualizerFactory.class);
	
	public static final int DEFAULT_MAX_SIZE = 200;
	public static final int DEFAULT_BLOCK_SIZE = 4096;
	public static final int DEFAULT_MIN_BLOCK_GROW_COUNT = 100;
	
	private int maxSize;
	private String tempDirectory;
	private int blockSize;
	private int minBlockGrowCount;
	private StreamCompression compression;

	public SwapFileVirtualizerFactory() {
		// default values
		maxSize = DEFAULT_MAX_SIZE;
		tempDirectory = System.getProperty("java.io.tmpdir");
		blockSize = DEFAULT_MIN_BLOCK_GROW_COUNT;
		minBlockGrowCount = DEFAULT_MIN_BLOCK_GROW_COUNT;
	}
	
	public JRVirtualizer getVirtualizer() {
		JRSwapFile swapFile = getSwapFile();
		JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(maxSize, swapFile, true, compression);
		
		if (log.isDebugEnabled()) {
			log.debug("Created swap file virtualizer " + virtualizer);
		}
		
		return virtualizer;
	}

	protected JRSwapFile getSwapFile() {
		JRSwapFile swapFile = new JRSwapFile(tempDirectory, blockSize, minBlockGrowCount);
		return swapFile;
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public String getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public int getMinBlockGrowCount() {
		return minBlockGrowCount;
	}

	public void setMinBlockGrowCount(int minBlockGrowCount) {
		this.minBlockGrowCount = minBlockGrowCount;
	}

	public StreamCompression getCompression() {
		return compression;
	}

	public void setCompression(StreamCompression compression) {
		this.compression = compression;
	}

}
