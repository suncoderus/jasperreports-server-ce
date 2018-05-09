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
package com.jaspersoft.jasperserver.api.engine.scheduling;

import org.springframework.beans.factory.FactoryBean;

import net.sf.jasperreports.engine.JRPrintHyperlink;

import com.jaspersoft.jasperserver.api.engine.common.service.impl.WebDeploymentInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.BaseReportExecutionHyperlinkProducerFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportExecutionHyperlinkProducerFactory extends BaseReportExecutionHyperlinkProducerFactory implements FactoryBean {
	
	private WebDeploymentInformation deploymentInformation;
	
	public Object getObject() {
		return new HyperlinkProducer();
	}

	public Class getObjectType() {
		return HyperlinkProducer.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	protected String getServerURLPrefix() {
		return getDeploymentInformation().getDeploymentURI();
	}
	
	public class HyperlinkProducer extends BaseHyperlinkProducer {
		@Override
		protected void appendHyperlinkStart(JRPrintHyperlink hyperlink, StringBuffer sb) {
			sb.append(getServerURLPrefix());
		}
		
		@Override
		protected void appendAdditionalParameters(JRPrintHyperlink hyperlink, URLParameters urlParams) {
			// nothing to add
		}
	}

	public WebDeploymentInformation getDeploymentInformation() {
		return deploymentInformation;
	}

	public void setDeploymentInformation(
			WebDeploymentInformation deploymentInformation) {
		this.deploymentInformation = deploymentInformation;
	}
	
}
