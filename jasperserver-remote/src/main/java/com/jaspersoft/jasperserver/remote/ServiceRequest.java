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

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.OperationResult;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * A service request is used mainly by the methos put of some handlers, which need to access
 * the resource and optionally sub resources on the request, and populate the operation
 * result accordingly.
 *
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public interface ServiceRequest {
	
	ResourceDescriptor getRequestDescriptor();
	
	String getRequestArgument(String name);
	
	OperationResult getResult();

}
