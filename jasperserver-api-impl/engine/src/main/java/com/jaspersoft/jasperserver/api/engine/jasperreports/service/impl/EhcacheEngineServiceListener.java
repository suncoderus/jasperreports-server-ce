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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.OrderedMap;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;

public class EhcacheEngineServiceListener implements PostUpdateEventListener,  BeanFactoryAware {

	BeanFactory factory = null;
	protected String ehcacheEngineServiceName;

	public String getEhcacheEngineServiceName() {
		return ehcacheEngineServiceName;
	}

	public void setEhcacheEngineServiceName(String name) {
		this.ehcacheEngineServiceName = name;
	}

	public EhcacheEngineService getEhcacheEngineService() {
		return (EhcacheEngineService)factory.getBean(ehcacheEngineServiceName);
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		Object e = event.getEntity();
		if (e instanceof RepoDataSource) {
			getEhcacheEngineService().clear();
		}
	}

	@Override
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		this.factory = factory;
	}

}
