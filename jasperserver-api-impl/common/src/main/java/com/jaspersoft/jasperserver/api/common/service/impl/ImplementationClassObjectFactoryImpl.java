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
package com.jaspersoft.jasperserver.api.common.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory;
import com.jaspersoft.jasperserver.api.common.service.ObjectFactory;

/**
 * @author swood
 *
 */
public class ImplementationClassObjectFactoryImpl implements ImplementationObjectFactory, Serializable {
	
	private Map implementationClassMappings;
	
	private ObjectFactory objectFactory;

	public Map getImplementationClassMappings() {
		return implementationClassMappings;
	}

	public void setImplementationClassMappings(Map implementationClassMappings) {
		this.implementationClassMappings = implementationClassMappings;
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	
	public void setObjectFactory(ObjectFactory factoryImpl) {
		this.objectFactory = factoryImpl;
	}
	
	public Class getImplementationClass(Class _class) {
		return getObjectFactory().getImplementationClass(getImplementationClassMappings(), _class);
	}
	
	public Class getImplementationClass(String id) {
		return getObjectFactory().getImplementationClass(getImplementationClassMappings(), id);
	}
	
	public String getImplementationClassName(Class _class) {
		return getObjectFactory().getImplementationClassName(getImplementationClassMappings(), _class);
	}
	
	public String getImplementationClassName(String id) {
		return getObjectFactory().getImplementationClassName(getImplementationClassMappings(), id);
	}
	
	public Class getInterface(Class _class) {
		return getObjectFactory().getInterface(getImplementationClassMappings(), _class);
	}
	
	public String getInterfaceName(Class _class) {
		return getObjectFactory().getInterfaceName(getImplementationClassMappings(), _class);
	}
	
	public String getIdForClass(Class _class) {
		return getObjectFactory().getIdForClass(getImplementationClassMappings(), _class);
	}

	public Object newObject(Class _class) {
		return getObjectFactory().newObject(getImplementationClassMappings(), _class);
	}

	public Object newObject(String id) {
		return getObjectFactory().newObject(getImplementationClassMappings(), id);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory#getKeys()
	 */
	public List getKeys() {
		return getObjectFactory().getKeys(getImplementationClassMappings());
	}
}
