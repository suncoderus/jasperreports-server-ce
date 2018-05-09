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

package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BeanDataSourceBean extends ResourceBean {

	private String beanName;
	private String beanMethod;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		BeanReportDataSource ds = (BeanReportDataSource) res;
		setBeanName(ds.getBeanName());
		setBeanMethod(ds.getBeanMethod());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		BeanReportDataSource ds = (BeanReportDataSource) res;
		ds.setBeanName(getBeanName());
		ds.setBeanMethod(getBeanMethod());
	}

	public String getBeanMethod() {
		return beanMethod;
	}
	
	public void setBeanMethod(String beanMethod) {
		this.beanMethod = beanMethod;
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
}
