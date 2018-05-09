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

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * A Spring bean post processor that registers a repository event listener
 * with a listener registrar.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @see RepositoryEventListenerRegistry
 * @since 2.0.0
 */
@JasperServerAPI
public class ResourceEventListenerProcessor implements BeanPostProcessor {

	private RepositoryEventListenerRegistry registry;
	private String listenerBeanName;

	/**
	 * Registers the bean with the registrar if the processed bean name matches
	 * the configured listener bean name.
	 * 
	 * @see #setListenerBeanName(String)
	 */
	public Object postProcessBeforeInitialization(Object bean, String name) {
		if (name != null && name.equals(getListenerBeanName())) {
			getRegistry().registerListener((RepositoryEventListener) bean);
		}
		return bean;
	}
	
	/**
	 * No action.
	 */
	public Object postProcessAfterInitialization(Object bean, String name) {
		return bean;
	}

	/**
	 * Returns the name of the bean that will be registered as a repository
	 * listener with the registrar.
	 * 
	 * @return the listener bean name
	 */
	public String getListenerBeanName() {
		return listenerBeanName;
	}

	/**
	 * Specifies the name of the bean that should be registered as a repository
	 * listener with the registrar.
	 * 
	 * @param listenerBeanName the name of the repository listener bean
	 */
	public void setListenerBeanName(String listenerBeanName) {
		this.listenerBeanName = listenerBeanName;
	}

	/**
	 * Returns the repository listener registrar which will be used to register
	 * the listener.
	 * 
	 * @return the repository listener registrar
	 */
	public RepositoryEventListenerRegistry getRegistry() {
		return registry;
	}

	/**
	 * Sets the repository listener registrar that will be used to register
	 * the listener.
	 * 
	 * @param registry the repository listener registrar
	 */
	public void setRegistry(RepositoryEventListenerRegistry registry) {
		this.registry = registry;
	}

}
