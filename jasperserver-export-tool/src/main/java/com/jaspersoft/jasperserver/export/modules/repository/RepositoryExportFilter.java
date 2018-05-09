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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.export.Parameters;

import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public interface RepositoryExportFilter {

	boolean toExportContents(Folder folder);

	/**
	 *  Filter folders by export parameters.
	 *
	 *  @param uri - folder uri
	 *  @param exportParameters - export parameters
	 *
	 *  @return Return <tt>true</tt> if is it accessible folder.
	* */
	boolean excludeFolder(String uri, Parameters exportParameters);

	/**
	 *  Filter resources by it accessible resource type groups.
	 *
	 *  @param type - resource type whose will be tested
	 *  @param resourceTypes - accessible resource type groups
	 *
	 *  @return Return <tt>true</tt> if is it accessible resource type.
	 */
	boolean isToExportResource(String type, Set<String> resourceTypes);
}
