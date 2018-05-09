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

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshotData;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DefaultDataCacheSnapshotMetadata;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceModuleConfiguration;

/**
 * @author tkavanagh
 * @version $Id$
 */

public class ReportUnitBean extends ResourceBean {
	
	private static final String DATA_SNAPSHOT_SERVICE_BEAN = "dataSnapshotService";

	/*
	 * The following come from the ReportUnit interface 
	 */
	private ResourceReferenceBean dataSource;
	private ResourceReferenceBean query;
	private ResourceReferenceBean[] inputControls;
	private ResourceReferenceBean mainReport;
	private ResourceReferenceBean[] resources;
	private String inputControlRenderingView;
	private String reportRenderingView;
    private boolean alwaysPromptControls;
    private byte controlsLayout = ReportUnit.LAYOUT_SEPARATE_PAGE;
	
	private DataSnapshotMetadataBean dataSnapshotMetadata;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		ReportUnit ru = (ReportUnit) res;
		setDataSource(exportHandler.handleReference(ru.getDataSource()));
		setQuery(exportHandler.handleReference(ru.getQuery()));
		setInputControls(handleReferences(ru.getInputControls(), exportHandler));
		setMainReport(exportHandler.handleReference(ru.getMainReport()));
		setResources(handleReferences(ru.getResources(), exportHandler));
		setInputControlRenderingView(ru.getInputControlRenderingView());
		setReportRenderingView(ru.getReportRenderingView());
		setAlwaysPromptControls(ru.isAlwaysPromptControls());
		setControlsLayout(ru.getControlsLayout());

		copyDataSnapshotFrom(exportHandler, ru);
	}

	protected void copyDataSnapshotFrom(ResourceExportHandler exportHandler,
			ReportUnit ru) {
		Long snapshotId = ru.getDataSnapshotId();
		if (snapshotId != null) {
			DataSnapshotService snapshotService = getSnapshotService(
					exportHandler.getConfiguration());
			
			if (!snapshotService.isSnapshotPersistenceEnabledForReportUri(ru.getPath())) {
				if (log.isDebugEnabled()) {
					log.debug("data snapshot persistence disabled, ignoring snapshot " + snapshotId
							+ " of report " + ru.getURIString());
				}

				return;
			}
			
			DataCacheSnapshotData dataSnapshot = snapshotService.loadSnapshotData(
					exportHandler.getExecutionContext(), snapshotId);
			if (dataSnapshot != null) {
				if (log.isDebugEnabled()) {
					log.debug("exporting data snapshot " + snapshotId 
							+ " for report " + ru.getURIString());
				}
				
				dataSnapshotMetadata = new DataSnapshotMetadataBean();
				dataSnapshotMetadata.copyFrom(dataSnapshot.getMetadata(), ru.getURIString(), exportHandler);
				
				String fileName = snapshotDataFileName(ru);
				exportHandler.handleData(ru, fileName, dataSnapshot.getSnapshotData().getInputStream());
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Did not find data snapshot " + snapshotId 
							+ " for report " + ru.getURIString());
				}
			}
		}
	}

	protected String snapshotDataFileName(ReportUnit ru) {
		return ru.getName() + "_data_snapshot.dump";
	}

	protected DataSnapshotService getSnapshotService(
			ResourceModuleConfiguration configuration) {
		DataSnapshotService snapshotService = configuration.getApplicationContext().getBean(
				DATA_SNAPSHOT_SERVICE_BEAN, DataSnapshotService.class);
		return snapshotService;
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		ReportUnit ru = (ReportUnit) res;

        if (getDataSource() != null) {
            Resource processedDS = importHandler.getHandledResource(getDataSource().getExternalURI());
            ru.setDataSource(processedDS != null
                    ? new ResourceReference(processedDS)
                    : importHandler.handleReference(getDataSource()));
        }

        ru.setQuery(importHandler.handleReference(getQuery()));
		ru.setInputControls(handleReferences(ru, getInputControls(), importHandler));
		ru.setMainReport(importHandler.handleReference(getMainReport()));
        List rs = handleReferences(ru, getResources(), importHandler);
        if (ru.getResources() == null) {
            ru.setResources(rs);
        } else if (rs != null) {
            ru.getResources().addAll(rs);
        }
        ru.setInputControlRenderingView(getInputControlRenderingView());
		ru.setReportRenderingView(getReportRenderingView());
		ru.setAlwaysPromptControls(isAlwaysPromptControls());
		ru.setControlsLayout(getControlsLayout());
		
		copyDataSnapshotTo(importHandler, ru);
	}

	protected void copyDataSnapshotTo(ResourceImportHandler importHandler,
			ReportUnit ru) {
		if (dataSnapshotMetadata == null) {
			// clearing any existing data snapshot
			ru.setDataSnapshotId(null);
		} else {
			DataSnapshotService snapshotService = getSnapshotService(
					importHandler.getConfiguration());
			boolean isDiagSnapshot = isDiagSnapshotAttributeSet(importHandler);

			if (!isDiagSnapshot && !snapshotService.isSnapshotPersistenceEnabled()) {
				if (log.isDebugEnabled()) {
					log.debug("data snapshot persistence disabled, not importing snapshot for report " 
							+ ru.getURIString());
				}
				
				// reset any existing snapshots
				ru.setDataSnapshotId(null);
				return;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("importing data snapshot for report " + ru.getURIString());
			}
			
			Map<String, Object> parametersMap = dataSnapshotMetadata.getParametersMap(ru, importHandler);
			if (isDiagSnapshot)
			{
				parametersMap.put(DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT, Boolean.TRUE.toString());
			}
			DefaultDataCacheSnapshotMetadata metadata = new DefaultDataCacheSnapshotMetadata(
					parametersMap, dataSnapshotMetadata.getSnapshotDate());
			
			byte[] snapshotBytes = importHandler.handleData(this, snapshotDataFileName(ru), null);
			DataContainer snapshotData = new MemoryDataContainer(snapshotBytes);
			
			DataCacheSnapshotData dataSnapshot = new DataCacheSnapshotData();
			dataSnapshot.setMetadata(metadata);
			dataSnapshot.setSnapshotData(snapshotData);
			
			// saving as a new data snapshot
			// TODO lucianc too early to save here, the report unit might not get saved
			long snapshotId = snapshotService.saveDataSnapshot(
					importHandler.getExecutionContext(), dataSnapshot);
			ru.setDataSnapshotId(snapshotId);

			if (log.isDebugEnabled()) {
				log.debug("saved data snapshot " + snapshotId + " for report " + ru.getURIString());
			}
		}
	}

	/**
	 * @param attributes
	 * @return
	 */
	private boolean isDiagSnapshotAttributeSet(ResourceImportHandler importHandler) 
	{
    	return Boolean.valueOf((String)importHandler.getImportContext().getAttributes().getAttribute(DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT));
	}

	public ResourceReferenceBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(ResourceReferenceBean dataSource) {
		this.dataSource = dataSource;
	}
	
	public ResourceReferenceBean getQuery() {
		return query;
	}

	public void setQuery(ResourceReferenceBean query) {
		this.query = query;
	}	
	
	public ResourceReferenceBean[] getInputControls() {
		return inputControls;
	}

	public void setInputControls(ResourceReferenceBean[] inputControls) {
		this.inputControls = inputControls;
	}

	public ResourceReferenceBean getMainReport() {
		return mainReport;
	}

	public void setMainReport(ResourceReferenceBean mainReport) {
		this.mainReport = mainReport;
	}

	public ResourceReferenceBean[] getResources() {
		return resources;
	}

	public void setResources(ResourceReferenceBean[] resources) {
		this.resources = resources;
	}

	public boolean isAlwaysPromptControls() {
		return alwaysPromptControls;
	}

	public void setAlwaysPromptControls(boolean alwaysPromptControls) {
		this.alwaysPromptControls = alwaysPromptControls;
	}

	public byte getControlsLayout() {
		return controlsLayout;
	}

	public void setControlsLayout(byte controlsLayout) {
		this.controlsLayout = controlsLayout;
	}

	public String getInputControlRenderingView() {
		return inputControlRenderingView;
	}

	public void setInputControlRenderingView(String inputControlRenderingView) {
		this.inputControlRenderingView = inputControlRenderingView;
	}

	public String getReportRenderingView() {
		return reportRenderingView;
	}

	public void setReportRenderingView(String reportRenderingView) {
		this.reportRenderingView = reportRenderingView;
	}

	public DataSnapshotMetadataBean getDataSnapshotMetadata() {
		return dataSnapshotMetadata;
	}

	public void setDataSnapshotMetadata(
			DataSnapshotMetadataBean dataSnapshotMetadata) {
		this.dataSnapshotMetadata = dataSnapshotMetadata;
	}
}
