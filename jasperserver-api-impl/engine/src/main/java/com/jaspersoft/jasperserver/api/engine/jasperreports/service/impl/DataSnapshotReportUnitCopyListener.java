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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.BaseRepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceCopiedEvent;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DataSnapshotReportUnitCopyListener extends BaseRepositoryListener implements ApplicationContextAware {

	private static final Log log = LogFactory.getLog(DataSnapshotReportUnitCopyListener.class);
	
	private ApplicationContext applicationContext;
	private String dataSnapshotServiceBean;

	@Override
	public void resourceCopied(ResourceCopiedEvent event) {
		Resource resource = event.getResource();
		if (resource instanceof ReportUnit) {
			ReportUnit report = (ReportUnit) resource;
			copyDataSnapshot(event.getSourceUri(), report);
		}
	}

	private void copyDataSnapshot(String sourceUri, ReportUnit report) {
		Long sourceSnapshotId = report.getDataSnapshotId();
		if (sourceSnapshotId != null) {
			DataSnapshotService dataSnapshotService = dataSnapshotService();
			if (dataSnapshotService.isSnapshotPersistenceEnabled()) {
				Long snapshotCopyId = dataSnapshotService.copyDataSnapshot(sourceSnapshotId);
				if (log.isDebugEnabled()) {
					log.debug("created snapshot copy " + snapshotCopyId + " for " + report.getURIString()
							+ ", source " + sourceSnapshotId + " of " + sourceUri);
				}
				report.setDataSnapshotId(snapshotCopyId);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("data snapshot persistence disabled, not saving a snapshot for " 
							+ report.getURIString() + ", source " + sourceSnapshotId + " of " + sourceUri);
				}
				
				// clear the report snapshot
				report.setDataSnapshotId(null);
			}
		}
	}

	protected DataSnapshotService dataSnapshotService() {
		return applicationContext.getBean(dataSnapshotServiceBean, DataSnapshotService.class);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getDataSnapshotServiceBean() {
		return dataSnapshotServiceBean;
	}

	public void setDataSnapshotServiceBean(String dataSnapshotServiceBean) {
		this.dataSnapshotServiceBean = dataSnapshotServiceBean;
	}
	
}
