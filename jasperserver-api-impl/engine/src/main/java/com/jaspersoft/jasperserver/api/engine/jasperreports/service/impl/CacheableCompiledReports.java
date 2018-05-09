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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCacheableItem;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CacheableCompiledReports implements RepositoryCacheableItem {

	private static final String CACHE_NAME = "JasperReport";

	private InternalReportCompiler compiler;
	
	public CacheableCompiledReports() {
	}
	
	public CacheableCompiledReports(InternalReportCompiler compiler) {
		this.compiler = compiler;
	}
	
	public InternalReportCompiler getCompiler() {
		return compiler;
	}

	public void setCompiler(InternalReportCompiler compiler) {
		this.compiler = compiler;
	}

	public String getCacheName() {
		return CACHE_NAME;
	}

	public byte[] getData(ExecutionContext context, FileResource resource) {
		return compiler.compileReport(context, resource);
	}

}
