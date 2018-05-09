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

import java.util.Date;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListener;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportExecutionListenerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportExecutionAuditListenerFactory implements ReportExecutionListenerFactory {

	private LoggingContextProvider loggingContextProvider;
	private AuditContext auditContext;
	
	public ReportExecutionListener createListener(ReportUnitRequestBase request) {
		if (!request.isCreateAuditEvent()) {
			return null;
		}
		
		return new ReportExecutionAuditListener(request.getStartTime());
	}
	
	protected class ReportExecutionAuditListener implements ReportExecutionListener {
		private final long startTime;
		private Thread originalThread;

		public ReportExecutionAuditListener(long startTime) {
			this.startTime = startTime;
		}
		
		public void init() {
			this.originalThread = Thread.currentThread();
		}

		public void start() {
			getAuditContext().doInAuditContext(
					new AuditContext.AuditContextCallback() {
						public void execute() {
							getAuditContext().createAuditEvent("runReport");
						}
					});
		}

		public void end(boolean success) {
			end(success,-1);
		}		
		public void end(boolean success, final long size) {
			if (success) {
				getAuditContext().doInAuditContext("runReport",
						new AuditContext.AuditContextCallbackWithEvent() {
							public void execute(AuditEvent auditEvent) {
								getAuditContext().addPropertyToAuditEvent("reportExecutionStartTime", new Date(startTime), auditEvent);
								getAuditContext().addPropertyToAuditEvent("reportExecutionTime", System.currentTimeMillis() - startTime, auditEvent);
								if (size>=0) {
									getAuditContext().addPropertyToAuditEvent("reportMemorySize", size, auditEvent);
								}
								getAuditContext().closeAuditEvent(auditEvent);
							}
						});
			}
			
			if (!Thread.currentThread().equals(originalThread)) {// TODO lucianc put in finally
				// the execution ran on a different thread, flush the events
				loggingContextProvider.flushContext();
			}
		}

	}

	public LoggingContextProvider getLoggingContextProvider() {
		return loggingContextProvider;
	}

	public void setLoggingContextProvider(
			LoggingContextProvider loggingContextProvider) {
		this.loggingContextProvider = loggingContextProvider;
	}

	public AuditContext getAuditContext() {
		return auditContext;
	}

	public void setAuditContext(AuditContext auditContext) {
		this.auditContext = auditContext;
	}

}
