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

package com.jaspersoft.jasperserver.export.io;

import java.util.Properties;
import java.util.zip.Deflater;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.export.Parameters;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ZipFileOutputFactory implements ExportOutputFactory {

	private String zipFileParameter;
	private int compressionLevel = Deflater.DEFAULT_COMPRESSION;
	private PathProcessorFactory pathProcessorFactory;
	private String propertyPathProcessorId;

	public boolean matches(Parameters parameters) {
		return parameters.hasParameter(zipFileParameter);
	}
	
	public ExportOutput createOutput(Parameters parameters) {
		String zipFile = parameters.getParameterValue(getZipFileParameter());
		if (zipFile == null) {
			throw new JSException("jsexception.no.export.zip.file.specified");
		}
		
		String processorId = pathProcessorFactory.getDefaultOutputProcessor();
		PathProcessor processor = pathProcessorFactory.getProcessor(processorId);
		Properties properties = new Properties();
		properties.setProperty(propertyPathProcessorId, processorId);
		return new ZipFileOutput(zipFile, getCompressionLevel(), processor, properties);
	}

	public String getZipFileParameter() {
		return zipFileParameter;
	}

	public void setZipFileParameter(String exportFolderParameter) {
		this.zipFileParameter = exportFolderParameter;
	}

	public int getCompressionLevel() {
		return compressionLevel;
	}

	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public PathProcessorFactory getPathProcessorFactory() {
		return pathProcessorFactory;
	}

	public void setPathProcessorFactory(PathProcessorFactory pathProcessorFactory) {
		this.pathProcessorFactory = pathProcessorFactory;
	}

	public String getPropertyPathProcessorId() {
		return propertyPathProcessorId;
	}

	public void setPropertyPathProcessorId(String propertyPathProcessorId) {
		this.propertyPathProcessorId = propertyPathProcessorId;
	}

}
