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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory;

/**
 * @author swood
 *
 */
public class BeanForInterfaceFactoryImpl implements BeanForInterfaceFactory, ApplicationContextAware {
	private static final Log log = LogFactory.getLog(BeanForInterfaceFactoryImpl.class);
	
	private ApplicationContext ctx;
    
    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }
	
	private final Comparator itfComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			Class itf1 = (Class) o1;
			Class itf2 = (Class) o2;
			
			if (itf1.equals(itf2)) {
				return 0;
			} else if (itf2.isAssignableFrom(itf1)) {
				return -1;
			} else if (itf1.isAssignableFrom(itf2)) {
				return 1;
			} else {
				return itf1.getName().compareTo(itf2.getName());
			}
		}
	};

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory#getBean(java.util.Map, java.lang.Class)
	 */
	public String getBeanName(Map interfaceToBeanMappings, Class itfClass) {
		if (interfaceToBeanMappings == null) {
			return null;
		}
		
		//TODO cache
		try {
			SortedSet interfaces = new TreeSet(itfComparator);

			for (Iterator it = interfaceToBeanMappings.keySet().iterator(); it.hasNext();) {
				String itfName = (String) it.next();
				Class itf = Class.forName(itfName, true, Thread.currentThread().getContextClassLoader());
				if (itf.isAssignableFrom(itfClass)) {
					interfaces.add(itf);
				}
			}

			if (!interfaces.isEmpty()) {
				Class itf = (Class) interfaces.iterator().next();
				return (String) interfaceToBeanMappings.get(itf.getName());
			}
			return null;
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory#getBeanName(java.util.Map, java.lang.Class)
	 */
	public Object getBean(Map classToBeanMappings, Class _class) {
		
		String beanName = getBeanName(classToBeanMappings, _class);
		
		/*
		 * TODO Leave this to the caller to handle?
		 */
		if (beanName == null) {
			throw new JSException("jsexception.bean.name.not.found.for.interface", new Object[] {_class.getName()});
		}
		
		Object bean = ctx.getBean(beanName);
		
		/*
		 * TODO Leave this to the caller to handle?
		 */
		if (bean == null) {
			throw new JSException("jsexception.bean.no.name", new Object[] {beanName});
		}
		
		return bean;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory#hasMapping(java.util.Map, java.lang.Class)
	 */
	public boolean hasMapping(Map classToBeanMappings, Class _class) {
		if (classToBeanMappings == null) {
			return false;
		}
		
		//TODO cache
		try {
			boolean found = false;
			for (Iterator it = classToBeanMappings.keySet().iterator(); it.hasNext();) {
				String itfName = (String) it.next();
				Class itf = Class.forName(itfName, true, Thread.currentThread().getContextClassLoader());
				if (itf.isAssignableFrom(_class)) {
					found = true;
					break;
				}
			}
			return found;
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

}
