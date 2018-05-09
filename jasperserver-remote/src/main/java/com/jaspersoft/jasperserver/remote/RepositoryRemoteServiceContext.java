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

package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.utils.RepositoryHelper;
import net.sf.jasperreports.engine.JRExporter;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public interface RepositoryRemoteServiceContext {

	RepositoryService getRepository();
	
	EngineService getEngine();
	
	ResourceHandlerRegistry getHandlerRegistry();
	
	String getMessage(String messageCode, Object[] args);

	ResourceDescriptor createResourceDescriptor(String referenceURI);
	
	ResourceDescriptor createResourceDescriptor(Resource resource);

	ResourceDescriptor createResourceDescriptor(Resource resource, Map options);

	RepositoryHelper getRepositoryHelper();

    String runReport(String requestXmlString);

    String list(String requestXmlString);

    List listResources(String uri);

    String get(String requestXmlString);

    String put(String requestXmlString);

    String delete(String requestXmlString);

    JRExporter getExporter(String type, Map exportParameters);

    String getContentType(String type);

    String move(String requestXmlString);

    String copy(String requestXmlString);

    Locale getLocale();

    

}
