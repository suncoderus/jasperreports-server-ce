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
package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.fill.StoreFactoryVirtualizer;
import net.sf.jasperreports.engine.fill.VirtualizerStoreFactory;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;

import com.jaspersoft.jasperserver.api.engine.common.domain.ReportResult;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SharedVirtualizerStoreFactory implements VirtualizerFactory, InitializingBean, Serializable {
	
	private static final Log log = LogFactory.getLog(SharedVirtualizerStoreFactory.class);
	
	public static final int DEFAULT_MAX_SIZE = 2000;

	private int maxSize = DEFAULT_MAX_SIZE;
	private VirtualizerStoreFactory storeFactory;

	private StoreFactoryVirtualizer virtualizer;

	@Override
	public void afterPropertiesSet() {
		virtualizer = createVirtualizer();
		
		if (log.isDebugEnabled()) {
			log.debug("created virtualizer " + virtualizer 
					+ " with max size " + maxSize + " and store factory " + storeFactory);
		}
	}

	protected StoreFactoryVirtualizer createVirtualizer() {
		return new StoreFactoryVirtualizer(maxSize, storeFactory);
	}
	
	@Override
	public JRVirtualizer getVirtualizer() {
		return virtualizer;
	}

	@Override
	public void setReadOnly(ReportResult report) {
		JRVirtualizationContext virtualizationContext = contextForReport(report, true);
		if (virtualizationContext != null) {
			if (log.isDebugEnabled()) {
				log.debug("setting virt context " + virtualizationContext + " as read only for " + report.getRequestId());
			}

			virtualizationContext.setReadOnly(true);
		}
	}

	@Override
	public void disposeReport(ReportResult report) {
		JRVirtualizationContext virtualizationContext = contextForReport(report, false);
		if (virtualizationContext != null) {
			if (log.isDebugEnabled()) {
				log.debug("disposing virt context " + virtualizationContext + " for " + report.getRequestId());
			}

			virtualizer.dispose(virtualizationContext);
		}
	}
	
	protected JRVirtualizationContext contextForReport(ReportResult report, boolean finalJasperPrint) {
		JRVirtualizationContext virtualizationContext = null;
		JasperPrintAccessor accessor = report.getJasperPrintAccessor();
		if (accessor != null) {
			JasperPrint jasperPrint = finalJasperPrint ? accessor.getFinalJasperPrint() : accessor.getJasperPrint();
			if (jasperPrint != null) {
				virtualizationContext = JRVirtualizationContext.getRegistered(jasperPrint);
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("found virt context " + virtualizationContext + " for report " + report.getRequestId());
		}

		return virtualizationContext;
	}

	public VirtualizerStoreFactory getStoreFactory() {
		return storeFactory;
	}

	public void setStoreFactory(VirtualizerStoreFactory storeFactory) {
		this.storeFactory = storeFactory;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

}
