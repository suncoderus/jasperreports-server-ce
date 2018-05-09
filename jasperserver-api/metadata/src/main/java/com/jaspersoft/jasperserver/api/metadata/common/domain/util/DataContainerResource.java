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

package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.io.InputStream;

import org.springframework.core.io.AbstractResource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

/**
 * @author Lucian Chirita
 *
 */
public class DataContainerResource extends AbstractResource {

	private static final String DESCRIPTION = "Contained data";

    private String fileName =null;
	
	private final DataContainer data;
	
	public DataContainerResource(DataContainer data) {
		this.data = data;
	}
	
	public String getDescription() {
		return DESCRIPTION;
	}

    public String getFilename() {
        if (fileName != null) return fileName;
		return super.getFilename();
	}

    public void setFilename(String fileName) {
        this.fileName = fileName;
    }

	public InputStream getInputStream() {
		return data.getInputStream();
	}
	
	public boolean exists() {
		return true;
	}

}
