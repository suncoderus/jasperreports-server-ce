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

package com.jaspersoft.jasperserver.api.metadata.security;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Iterator;

/**
 * A {@link org.springframework.security.acls.model.Acl} ACLs based method argument voter that works with
 * collections and arrays of objects. 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BasicMethodCollectionArgumentVoter extends
		BasicMethodArgumentVoter {

	private static final Log log = LogFactory.getLog(BasicMethodCollectionArgumentVoter.class);
	
	@Override
	protected boolean accessPermitted(Authentication authentication, Object secureObject) {
		boolean permitted = true;
		for (Iterator it = getObjectsIterator(secureObject); it.hasNext();) {
			Object object = (Object) it.next();
			if (!super.accessPermitted(authentication, object)) {
				permitted = false;
				break;
			}
		}
		return permitted;
	}
	
	protected Iterator getObjectsIterator(Object secureObject) {
		Iterator<?> iterator;
		if (secureObject instanceof Collection) {
			iterator = ((Collection) secureObject).iterator();
		} else if (secureObject != null && secureObject.getClass().isArray()) {
			iterator = new ArrayIterator(secureObject);
		} else {
			iterator = new SingletonIterator(secureObject);
		}
		return iterator;
	}
	
}
