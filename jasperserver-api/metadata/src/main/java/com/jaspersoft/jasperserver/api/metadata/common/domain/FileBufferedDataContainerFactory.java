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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

/**
 * @author Lucian Chirita
 *
 */
public class FileBufferedDataContainerFactory implements DataContainerFactory {

	private int memoryThreshold = FileBufferedDataContainer.DEFAULT_MEMORY_THRESHOLD;
	private int initialMemoryBuffer = FileBufferedDataContainer.DEFAULT_INITIAL_MEMORY_BUFFER;
	
	public DataContainer createDataContainer() {
		return new FileBufferedDataContainer(
				getMemoryThreshold(), getInitialMemoryBuffer());
	}

	public int getMemoryThreshold() {
		return memoryThreshold;
	}

	public void setMemoryThreshold(int memoryThreshold) {
		this.memoryThreshold = memoryThreshold;
	}

	public int getInitialMemoryBuffer() {
		return initialMemoryBuffer;
	}

	public void setInitialMemoryBuffer(int initialMemoryBuffer) {
		this.initialMemoryBuffer = initialMemoryBuffer;
	}

}
