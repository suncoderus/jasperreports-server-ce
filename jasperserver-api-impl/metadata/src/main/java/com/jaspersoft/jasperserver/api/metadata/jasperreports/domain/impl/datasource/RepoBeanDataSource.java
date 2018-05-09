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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;

/**
 * @author swood
 *
 * @hibernate.joined-subclass table="BeanDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoBeanDataSource extends RepoDataSource implements RepoReportDataSource {

	String beanName;
	String beanMethod;

	/**
	 * 
	 */
	public RepoBeanDataSource() {
		super();
	}
	
	/**
	 * @hibernate.property
	 * 		column="beanMethod" type="string" length="100"
	 * 
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#getBeanMethod()
	 */
	public String getBeanMethod() {
		return beanMethod;
	}

	/**
	 * 	 * @hibernate.property
	 * 		column="beanName" type="string" length="100" not-null="true"
	 * 
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#getBeanName()
	 */
	public String getBeanName() {
		return beanName;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#setBeanMethod(java.lang.String)
	 */
	public void setBeanMethod(String beanMethod) {
		this.beanMethod = beanMethod;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#setBeanName(java.lang.String)
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	protected Class getClientItf() {
		return BeanReportDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		BeanReportDataSource ds = (BeanReportDataSource) clientRes;
		ds.setBeanName(getBeanName());
		ds.setBeanMethod(getBeanMethod());
	}
	
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		BeanReportDataSource ds = (BeanReportDataSource) clientRes;
		setBeanName(ds.getBeanName());
		setBeanMethod(ds.getBeanMethod());
	}

}
