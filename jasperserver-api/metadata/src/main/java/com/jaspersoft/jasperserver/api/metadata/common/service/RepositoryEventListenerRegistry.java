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
 * Registrar of repository event listeners.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 * @see RepositoryEventListener
 * @see ResourceEventListenerProcessor
 */
@JasperServerAPI
public interface RepositoryEventListenerRegistry {
	
	/**
	 * Registers a repository event listener to be notified of event that
	 * occur in the repository.
	 * 
	 * @param listener the listener to register with the repository
	 */
	void registerListener(RepositoryEventListener listener);
	
	/**
	 * Removes a previously registered repository event lister.
	 * 
	 * @param listener the listener to be removed
	 */
	void deregisterListener(RepositoryEventListener listener);		

}
