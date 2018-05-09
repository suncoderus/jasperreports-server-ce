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

package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A changed resource maybe connected to a Mondrian Connection. If so, flush the Mondrian
 * cache related to the old values of the Mondrian connection
 *
 * @author swood
 */
public class OlapConnectionHibernatePostUpdateListener implements PostUpdateEventListener, BeanFactoryAware {

    private OlapManagementService olapManagementService;
    private String olapManagementServiceBean;
    private ResourceFactory resourceFactory;
	private BeanFactory beanFactory;

    public void onPostUpdate(PostUpdateEvent event) {
        Object o = event.getEntity();
        if (o instanceof RepoFileResource) {
            RepoFileResource fileResource = (RepoFileResource) o;
            if (FileResource.TYPE_XML.equals(fileResource.getFileType())) {
                Resource possibleMondrianConnectionRelatedResource = (Resource) ((RepoResource) o).toClient(getResourceFactory());
                getOlapManagementService().notifySchemaChange(null, possibleMondrianConnectionRelatedResource);
            }
        }
    }

    public OlapManagementService getOlapManagementService() {
        if (olapManagementService == null) {

            olapManagementService = (OlapManagementService) beanFactory.getBean(olapManagementServiceBean);
        }
        return olapManagementService;
    }
    
    

    public String getOlapManagementServiceBean() {
		return olapManagementServiceBean;
	}

	public void setOlapManagementServiceBean(String olapManagementServiceBean) {
		this.olapManagementServiceBean = olapManagementServiceBean;
	}

	public void setOlapManagementService(OlapManagementService olapManagementService) {
        this.olapManagementService = olapManagementService;
    }

    public ResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
