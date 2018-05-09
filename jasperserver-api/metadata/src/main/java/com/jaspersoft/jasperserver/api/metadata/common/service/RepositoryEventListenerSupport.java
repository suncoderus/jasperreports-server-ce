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

package com.jaspersoft.jasperserver.api.metadata.common.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * Base implementation of a repository repository listener.
 * 
 * <p>
 * This class contains empty methods for all repository events.
 * It can be extended by listener classes that override the methods for the
 * events in which they are interested.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.2.1
 */
@JasperServerAPI
public class RepositoryEventListenerSupport implements RepositoryEventListener {

	/**
	 * Empty implementation.
	 */
	public void onFolderDelete(String folderURI) {
		//empty
	}

	/**
	 * Empty implementation.
	 */
	public void onResourceDelete(Class resourceItf, String resourceURI) {
		//empty
	}

}
