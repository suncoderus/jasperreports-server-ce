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

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListener;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListenerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class LocaleContextReportExecutionListenerFactory implements
		ReportExecutionListenerFactory {

	public ReportExecutionListener createListener(ReportUnitRequestBase request) {
		return new LocaleContextReportExecutionListener();
	}

}

class LocaleContextReportExecutionListener implements ReportExecutionListener {

	private Thread originalThread;
	private LocaleContext originalLocaleContext;

	public void init() {
		this.originalThread = Thread.currentThread();
		this.originalLocaleContext = LocaleContextHolder.getLocaleContext();
	}

	public void start() {
		boolean setContext = !Thread.currentThread().equals(originalThread);
		if (setContext) {
			LocaleContextHolder.setLocaleContext(originalLocaleContext);
		}
	}

	public void end(boolean success, long time) {
		end(success);
	}
	
	public void end(boolean success) {
		boolean setContext = !Thread.currentThread().equals(originalThread);
		if (setContext) {
			// clear the context
			LocaleContextHolder.resetLocaleContext();
		}
	}

}