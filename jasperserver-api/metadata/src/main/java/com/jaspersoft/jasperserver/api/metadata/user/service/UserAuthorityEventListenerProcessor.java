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

package com.jaspersoft.jasperserver.api.metadata.user.service;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class UserAuthorityEventListenerProcessor implements BeanPostProcessor {

	private UserAuthorityEventListenerRegistry registry;
	private String listenerBeanName;

	public Object postProcessBeforeInitialization(Object bean, String name) {
		if (name != null && name.equals(getListenerBeanName())) {
			getRegistry().registerListener((UserAuthorityEventListener) bean);
		}
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String name) {
		return bean;
	}

	public String getListenerBeanName() {
		return listenerBeanName;
	}

	public void setListenerBeanName(String listenerBeanName) {
		this.listenerBeanName = listenerBeanName;
	}

	public UserAuthorityEventListenerRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(UserAuthorityEventListenerRegistry registry) {
		this.registry = registry;
	}

}
