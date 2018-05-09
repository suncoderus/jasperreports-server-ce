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

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;

import com.jaspersoft.jasperserver.api.engine.common.domain.ReportResult;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class AbstractIndividualVirtualizerFactory implements VirtualizerFactory, Serializable {
	
	private static final Log log = LogFactory.getLog(AbstractIndividualVirtualizerFactory.class); 
	
	@Override
	public void setReadOnly(ReportResult report) {
		JRVirtualizer virtualizer = report.getVirtualizer();
		if (virtualizer != null) {
			if (log.isDebugEnabled()) {
				log.debug("setting virtualizer " + virtualizer + " as read only for " + report.getRequestId());
			}
			
			((JRAbstractLRUVirtualizer) virtualizer).setReadOnly(true);
		}
	}

	@Override
	public void disposeReport(ReportResult report) {
		JRVirtualizer virtualizer = report.getVirtualizer();
		if (virtualizer != null) {
			if (log.isDebugEnabled()) {
				log.debug("disposing virtualizer " + virtualizer + " for " + report.getRequestId());
			}
			
			virtualizer.cleanup();
		}
	}

}
