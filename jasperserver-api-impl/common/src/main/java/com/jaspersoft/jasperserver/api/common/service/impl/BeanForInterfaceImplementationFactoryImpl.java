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

import java.util.Map;

import com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory;
import com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceImplementationFactory;

/**
 * @author swood
 *
 */
public class BeanForInterfaceImplementationFactoryImpl implements BeanForInterfaceImplementationFactory {

	private BeanForInterfaceFactory factory = null;
	private Map beanForInterfaceMappings = null;
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceImplementationFactory#getBean(java.lang.Class)
	 */
	public Object getBean(Class itfClass) {
		return factory.getBean(getBeanForInterfaceMappings(), itfClass);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceImplementationFactory#getBeanName(java.lang.Class)
	 */
	public String getBeanName(Class itfClass) {
		return factory.getBeanName(getBeanForInterfaceMappings(), itfClass);
	}

	/**
	 * @return Returns the factory.
	 */
	public BeanForInterfaceFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory The factory to set.
	 */
	public void setFactory(BeanForInterfaceFactory factory) {
		this.factory = factory;
	}

	/**
	 * @return Returns the beanForInterfaceMappings.
	 */
	public Map getBeanForInterfaceMappings() {
		return beanForInterfaceMappings;
	}

	/**
	 * @param beanForInterfaceMappings The beanForInterfaceMappings to set.
	 */
	public void setBeanForInterfaceMappings(Map beanForInterfaceMappings) {
		this.beanForInterfaceMappings = beanForInterfaceMappings;
	}

}
