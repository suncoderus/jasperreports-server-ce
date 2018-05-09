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
import com.jaspersoft.jasperserver.api.JSException;

/**
 * Exception thrown when a operation that expects to find a resource in the
 * repository at a specified path fails to locate the resource.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 */
@JasperServerAPI
public class JSResourceNotFoundException extends JSException {

	/**
	 * Creates a resource not found exception.
	 * 
	 * @param message the exception message
	 * @see JSException#JSException(String)
	 */
	public JSResourceNotFoundException(String message) {
		super(message);
	}
	
	/**
	 * Creates a resource not found exception.
	 * 
	 * @param message the exception message
	 * @param args the arguments for the message, used when the message is
	 * interpreted as the key of a localized message
	 * @see JSException#JSException(String, Object[])
	 * @since 1.2.1
	 */
	public JSResourceNotFoundException(String message,  Object[] args) {
		super(message, args);
	}
}
