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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.export.Parameters;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ZipFileInputFactory implements ImportInputFactory, ZipFileInputManager {

	private String inputZipParameter;
	private PathProcessorFactory pathProcessorFactory;
	private String propertyPathProcessorId;

	public boolean matches(Parameters parameters) {
		return parameters.hasParameter(inputZipParameter);
	}
	
	public ImportInput createInput(Parameters parameters) {
		String zipFile = parameters.getParameterValue(inputZipParameter);
		if (zipFile == null) {
            // Adding non localized message cause import-export tool does not support localization.
			throw new JSException("No import ZIP file was specified.");
		}
		
		String processorId = pathProcessorFactory.getDefaultInputProcessor();
		PathProcessor processor = pathProcessorFactory.getProcessor(processorId);
		return new ZipFileInput(zipFile, processor, this);
	}

	public void updateInputProperties(ZipFileInput input, Properties properties) {
		String pathProcessorId = properties.getProperty(propertyPathProcessorId);
		if (pathProcessorId != null && !pathProcessorId.equals(pathProcessorFactory.getDefaultInputProcessor())) {
			PathProcessor processor = pathProcessorFactory.getProcessor(pathProcessorId);
			input.setPathProcessor(processor);
		}
	}

	public String getInputZipParameter() {
		return inputZipParameter;
	}

	public void setInputZipParameter(String inputDirParameter) {
		this.inputZipParameter = inputDirParameter;
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
