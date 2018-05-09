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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;


/**
 * @author Lucian Chirita
 * @version $Id: HibernateRepositoryServiceImpl.java 11286 2007-12-17 17:00:30Z lucian $
 */
public abstract class BaseRepositorySecurityChecker implements RepositorySecurityChecker {
	
	private static final Log log = LogFactory.getLog(BaseRepositorySecurityChecker.class);
    
	/** Filters allResources.
	 *  Populates removableResources with the resources that can be deleted.
	 *  Populates editableResources with the resources that can be edited.
	 */  
    public void filterResources(List allResources, Map removableResources, Map editableResources) {
		Iterator iter = allResources.iterator();
		while (iter.hasNext()) {
			Resource res = (Resource) iter.next();
			filterResource(res, removableResources, editableResources); 		
		}		
	}
    
    /** Filters resource.
	 *  Adds resource to removableResources if it can be deleted.
	 *  Adds resource to editableResources if it can be edited.
	 */  
    public void filterResource(Resource resource, Map removableResources, Map editableResources) {
		if (isEditable(resource)) {
			editableResources.put(resource.getURIString(), "true");
			log.debug(resource.getURIString()
					+ ": "
					+ (editableResources.containsKey(resource.getURIString()) ? "EDIT"
							: "??edit??"));
		}
		if (isRemovable(resource)) {
			removableResources.put(resource.getURIString(), "true");
			log.debug(resource.getURIString()
					+ ": "
					+ (removableResources.containsKey(resource.getURIString()) ? "DELETE"
							: "??delete??"));
		}
	}

}
