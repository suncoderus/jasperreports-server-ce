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

import java.util.Map;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author lucian
 *
 */
public class SimplePathProcessorFactory implements PathProcessorFactory {

	private String defaultInputProcessor;
	private String defaultOutputProcessor;
	private Map processors;
	
	public String getDefaultInputProcessor() {
		return defaultInputProcessor;
	}

	public String getDefaultOutputProcessor() {
		return defaultOutputProcessor;
	}

	public PathProcessor getProcessor(String id) {
		if (!processors.containsKey(id)) {
			throw new JSException("No path processor found for id \"" + id + "\"");
		}
		
		return (PathProcessor) processors.get(id);
 	}

	public Map getProcessors() {
		return processors;
	}

	public void setProcessors(Map processors) {
		this.processors = processors;
	}

	public void setDefaultInputProcessor(String defaultInputProcessor) {
		this.defaultInputProcessor = defaultInputProcessor;
	}

	public void setDefaultOutputProcessor(String defaultOutputProcessor) {
		this.defaultOutputProcessor = defaultOutputProcessor;
	}

}
