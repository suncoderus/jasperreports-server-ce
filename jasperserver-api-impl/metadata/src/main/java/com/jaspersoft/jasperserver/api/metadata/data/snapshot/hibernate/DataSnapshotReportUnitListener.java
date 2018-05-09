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
package com.jaspersoft.jasperserver.api.metadata.data.snapshot.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.EntityEntry;
import org.hibernate.event.SaveOrUpdateEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateSaveOrUpdateListener;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotPersistenceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.RepoReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DataSnapshotReportUnitListener implements HibernateDeleteListener, HibernateSaveOrUpdateListener, ApplicationContextAware {

	private static final Log log = LogFactory.getLog(DataSnapshotReportUnitListener.class);
	
	private ApplicationContext applicationContext;
	private String dataSnapshotServiceBean;
	
	public void onDelete(Object o) {
		if (o instanceof RepoReportUnit) {
			RepoReportUnit reportUnit = (RepoReportUnit) o;
			Long snapshotId = reportUnit.getDataSnapshotId();
			if (snapshotId != null) {
				// delete the snapshot on report unit deletion
				if (log.isDebugEnabled()) {
					log.debug("deleting snapshot " + snapshotId + " for report " + reportUnit.getResourceURI());
				}
				
				dataSnapshotService().deleteSnapshot(snapshotId);
			}
		}
	}

	public void beforeSaveOrUpdate(SaveOrUpdateEvent event) {
		// NOP
	}

	public void afterSaveOrUpdate(SaveOrUpdateEvent event) {
		Object entity = event.getEntity();
		EntityEntry entityEntry = event.getEntry();
		if (entity instanceof RepoReportUnit && entityEntry != null && entityEntry.isExistsInDatabase()) {
			// delete the snapshot when a new one is set in a report unit
			RepoReportUnit reportUnit = (RepoReportUnit) entity;
			Long currentSnapshotId = reportUnit.getDataSnapshotId();
			Long oldSnapshotId = (Long) entityEntry.getLoadedValue("dataSnapshotId");
			if (oldSnapshotId != null && (currentSnapshotId == null || !oldSnapshotId.equals(currentSnapshotId))) {
				// delete the snapshot on report unit deletion
				if (log.isDebugEnabled()) {
					log.debug("deleting snapshot " + currentSnapshotId 
							+ " for report " + reportUnit.getResourceURI()
							+ ", replaced by " + currentSnapshotId);
				}
				
				dataSnapshotService().deleteSnapshot(oldSnapshotId);
			}
		}
	}

	protected DataSnapshotPersistenceService dataSnapshotService() {
		return applicationContext.getBean(dataSnapshotServiceBean, DataSnapshotPersistenceService.class);
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
