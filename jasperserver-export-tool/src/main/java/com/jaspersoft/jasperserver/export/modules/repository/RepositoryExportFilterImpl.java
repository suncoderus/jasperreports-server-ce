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

package com.jaspersoft.jasperserver.export.modules.repository;

import java.util.Set;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ClientResourceResolver;
import com.jaspersoft.jasperserver.export.Parameters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryExportFilterImpl implements RepositoryExportFilter {
	private ClientResourceResolver clientResourceResolver;

	@Override
	public boolean toExportContents(Folder folder) {
		return true;
	}

	@Override
	public boolean excludeFolder(String uri, Parameters exportParameters) {
		return false;
	}

	@Override
	public boolean isToExportResource(String type, Set<String> resourceTypes) {
		if (resourceTypes.isEmpty()) return true;

		Set<Class<? extends Resource>> serverClasses =  clientResourceResolver.getResourceTypes(resourceTypes);

		for (Class<? extends Resource> aClass : serverClasses) {
			if (aClass.getName().equalsIgnoreCase(type)) return true;
		}
		return false;
	}

	public void setClientResourceResolver(ClientResourceResolver clientResourceResolver) {
		this.clientResourceResolver = clientResourceResolver;
	}

	public int getClientResourceTypesSize() {
		return clientResourceResolver.getClientResourceTypeToServerResourceTypes().size();
	}
}
