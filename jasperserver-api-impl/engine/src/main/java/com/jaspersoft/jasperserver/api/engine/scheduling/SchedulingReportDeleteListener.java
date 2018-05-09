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

import com.jaspersoft.jasperserver.api.common.util.UpgradeRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryExtendListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceCopiedEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SchedulingReportDeleteListener extends RepositoryEventListenerSupport implements RepositoryExtendListener {

	private ReportSchedulingInternalService schedulingService;
	
	public void onResourceDelete(Class resourceItf, String resourceURI) {
		if (ReportUnit.class.isAssignableFrom(resourceItf)) {
            if (!UpgradeRunMonitor.isUpgradeRun()) {
                schedulingService.removeReportUnitJobs(resourceURI);
            }
		}
	}

	public ReportSchedulingInternalService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(
			ReportSchedulingInternalService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public void folderMoved(FolderMoveEvent folderMove) {
		// NOOP
	}

	public void resourceMoved(ResourceMoveEvent resourceMove) {
		if (ReportUnit.class.isAssignableFrom(resourceMove.getResourceType())) {
			schedulingService.updateReportUnitURI(
					resourceMove.getOldResourceURI(), resourceMove.getNewResourceURI());
		}
	}

	public void resourceCopied(ResourceCopiedEvent event) {
		// NOP
	}

}
