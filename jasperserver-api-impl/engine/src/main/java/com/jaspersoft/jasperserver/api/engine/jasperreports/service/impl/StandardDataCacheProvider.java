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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DefaultDataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotPersistentMetadata;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import net.sf.jasperreports.data.cache.DataCacheHandler;
import net.sf.jasperreports.data.cache.DataSnapshot;
import net.sf.jasperreports.data.cache.PopulatedSnapshotCacheHandler;
import net.sf.jasperreports.engine.ReportContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class StandardDataCacheProvider implements DataCacheProvider {
	
	private static final Log log = LogFactory.getLog(StandardDataCacheProvider.class);
	
	private ReportLoadingService reportLoadingService;
	private DataSnapshotService dataSnapshotService;
	private String dataSnapshotProviderParameter = "com.jaspersoft.jasperserver.data.snapshot.provider";//default value	

	public DataCacheSnapshot setReportExecutionCache(ExecutionContext context, 
			ReportUnitRequestBase request, ReportUnit reportUnit, Map<String, Object> dataParameters) {
		boolean doRecordDataSnapshot =
				dataSnapshotService.isSnapshotRecordingEnabled() && request.isRecordDataSnapshot();
		boolean isDiagnosticExport = dataSnapshotService.isDiagnosticSnapshotExportEnabled(reportUnit.getURIString());

		if (!isDiagnosticExport && !(request.isUseDataSnapshot() || doRecordDataSnapshot)) {
			// nothing to do
			if (log.isDebugEnabled()) {
				log.debug("no data caching");
			}
			return null;
		}

		ReportContext reportContext = request.getReportContext();
		if (reportContext == null) {
			if (log.isDebugEnabled()) {
				log.debug("no report context");
			}
			return null;
		}
		
		ReportDataCacheProvider cacheProvider = (ReportDataCacheProvider) reportContext.getParameterValue(
				getDataSnapshotProviderParameter());
		if (cacheProvider == null) {
			cacheProvider = createReportCacheProvider();
			reportContext.setParameterValue(dataSnapshotProviderParameter, cacheProvider);
		}
		
		DataCacheHandler cacheHandler = null;
		
		// check if we have a populated snapshot
		DataCacheSnapshot dataSnapshot = loadDataSnapshot(context, request,
				reportUnit, dataParameters, cacheProvider);
		if (dataSnapshot != null) {
			if (log.isDebugEnabled()) {
				log.debug("found data snapshot");
			}
			
			// use a cache handler that provides the snapshot
			cacheHandler = new PopulatedSnapshotCacheHandler(dataSnapshot.getSnapshot());
		} else if (isDiagnosticExport || doRecordDataSnapshot) {
			if (log.isDebugEnabled()) {
				log.debug("creating recording cache handler");
			}
			
			// create a recording cache handler
			cacheHandler = cacheProvider.createRecordHandler(dataParameters);
		}
		
		if (cacheHandler != null) {
			// populate the cache handler if we have one
			reportContext.setParameterValue(DataCacheHandler.PARAMETER_DATA_CACHE_HANDLER, cacheHandler);
		}
		
		return dataSnapshot;
	}
	
	public void handleInvalidSnapshot(ExecutionContext context, 
			ReportUnitRequestBase request, ReportUnit reportUnit, Map<String, Object> dataParameters) {
		ReportContext reportContext = request.getReportContext();
		if (reportContext == null) {
			if (log.isDebugEnabled()) {
				log.debug("no report context");
			}
			
			return;
		}
		
		DataCacheHandler cacheHandler = (DataCacheHandler) reportContext.getParameterValue(
				DataCacheHandler.PARAMETER_DATA_CACHE_HANDLER);
		if (cacheHandler != null && cacheHandler.isSnapshotPopulated()) {
			if (log.isDebugEnabled()) {
				log.debug("removing populated data snapshot");
			}
			
			// removing the populated data cache
			reportContext.setParameterValue(DataCacheHandler.PARAMETER_DATA_CACHE_HANDLER, null);
		}
		
		ReportDataCacheProvider cacheProvider = (ReportDataCacheProvider) reportContext.getParameterValue(
				getDataSnapshotProviderParameter());
		// the cache provider should already exist
		if (cacheProvider != null) {
			DataCacheSnapshot snapshot = cacheProvider.getSnapshot();
			if (snapshot != null && snapshot.isSaved()) {
				// clear the invalid snapshot
				cacheProvider.setSnapshot(null);
				// an invalid saved snapshot is treated like an non existing snapshot
				cacheProvider.setHadSavedSnapshot(false);
			}

			boolean doRecordDataSnapshot =
					dataSnapshotService.isSnapshotRecordingEnabled() && request.isRecordDataSnapshot();
			boolean isDiagnosticExport = dataSnapshotService.isDiagnosticSnapshotExportEnabled(reportUnit.getURIString());

			// create a recording cache handler
			if (isDiagnosticExport || doRecordDataSnapshot) {
				if (log.isDebugEnabled()) {
					log.debug("creating recording cache handler");
				}
				
				DataCacheHandler recordingHandler = cacheProvider.createRecordHandler(dataParameters);
				reportContext.setParameterValue(DataCacheHandler.PARAMETER_DATA_CACHE_HANDLER, recordingHandler);
			}
		}
	}

	protected ReportDataCacheProvider createReportCacheProvider() {
		return new ReportDataCacheProvider();
	}

	protected DataCacheSnapshot loadDataSnapshot(ExecutionContext context,
			ReportUnitRequestBase request, ReportUnit reportUnit,
			Map<String, Object> dataParameters,
			ReportDataCacheProvider cacheProvider) {
		if (reportUnit.getDataSnapshotId() != null) {
			// if the report has a snapshot Id, set the flag in the report provider
			cacheProvider.setHadSavedSnapshot(true);
		}
		
		if (!request.isUseDataSnapshot()) {
			if (log.isDebugEnabled()) {
				log.debug("not using data snapshot");
			}
			return null;
		}
		
		DataCacheSnapshot dataSnapshot = cacheProvider.getSnapshot();
		if (dataSnapshot != null) {
			if (!parametersMatch(dataParameters, dataSnapshot.getMetadata().getParameters())) {
				if (log.isDebugEnabled()) {
					log.debug("current snapshot doesn't match the parameters");
				}
				
				cacheProvider.clearSnapshot();
				dataSnapshot = null;
			}
		}
		
		if (dataSnapshot == null) {
			// try to load the persistent snapshot
			dataSnapshot = useSavedDataSnapshot(context, reportUnit, dataParameters, cacheProvider);
		}
		
		return dataSnapshot;
	}

	protected DataCacheSnapshot useSavedDataSnapshot(ExecutionContext context, ReportUnit reportUnit, 
			Map<String, Object> dataParameters, ReportDataCacheProvider cacheProvider) {
		DataSnapshotPersistentMetadata persistentSnapshot = loadSavedDataSnapshot(context, reportUnit);
		if (persistentSnapshot == null) {
			// this means no or out of date snapshot, reset the flag
			cacheProvider.setHadSavedSnapshot(false);
			return null;
		}
		
		if (!parametersMatch(dataParameters, persistentSnapshot.getSnapshotMetadata().getParameters())) {
			// the cached/saved snapshot is the latest version but doesn't match the parameters
			if (log.isDebugEnabled()) {
				log.debug("cached data snapshot " + reportUnit.getDataSnapshotId() 
						+ " with version " + persistentSnapshot.getVersion() 
						+ " doesn't match the parameters");
			}
			return null;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("found matching saved data snapshot " + reportUnit.getDataSnapshotId() 
					+ " at version " + persistentSnapshot.getVersion()); 
		}
		
		DataSnapshot snapshotContents = dataSnapshotService.getSnapshotContents(context, persistentSnapshot.getContentsId());
		if (snapshotContents == null) {
			if (log.isDebugEnabled()) {
				log.debug("data snapshot contents " + persistentSnapshot.getContentsId() 
						+ " not found for " + reportUnit.getURIString());
			}
			return null;
		}
		
		DefaultDataCacheSnapshot dataSnapshot = new DefaultDataCacheSnapshot(
				snapshotContents, persistentSnapshot.getSnapshotMetadata());
		cacheProvider.setSavedDataSnapshot(dataSnapshot);
		return dataSnapshot;
	}

	public DataSnapshotPersistentMetadata loadSavedDataSnapshot(ExecutionContext context, ReportUnit reportUnit) {
		Long snapshotId = reportUnit.getDataSnapshotId();
		if (snapshotId == null) {
			return null;
		}

		// Need to load metadata to see if it has isDiagSnapshot set,
		// postpone snapshotPersistentEnabled check until after metadata.
		// try to load the persistent snapshot
		if (log.isDebugEnabled()) {
			log.debug("loading saved data snapshot " + snapshotId + " for " + reportUnit.getURIString());
		}
		
		DataSnapshotPersistentMetadata persistentSnapshot = dataSnapshotService.getSnapshotMetadata(context, snapshotId);
		if (persistentSnapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("data snapshot " + snapshotId + " not found for " + reportUnit.getURIString());
			}
			return null;
		}

		boolean isDiagSnapshot = DiagnosticSnapshotPropertyHelper.isDiagSnapshotSet(persistentSnapshot.getSnapshotMetadata().getParameters());

		if (!dataSnapshotService.isSnapshotPersistenceEnabledForReportUri(reportUnit.getURI().replaceAll("repo:", ""))
				&& !isDiagSnapshot) {
			// if snapshot persistence is disabled, don't use saved snapshots
			if (log.isDebugEnabled()) {
				log.debug("snapshot persistence is disabled, not using snapshot " + snapshotId
						+ " for report " + reportUnit.getURIString());
			}
			persistentSnapshot = null;
			return null;
		}
		
		
		Date snapshotDate = persistentSnapshot.getSnapshotMetadata().getSnapshotDate();
		Resource dataSource = getReportLoadingService().getFinalResource(context, reportUnit.getDataSource(), Resource.class);
		Date dataSourceTimestamp = dataSource == null ? null : dataSource.getUpdateDate();
		if (dataSourceTimestamp != null && !snapshotDate.after(dataSourceTimestamp)) {
			// snapshot is older than data source, considering invalid
			if (log.isDebugEnabled()) {
				log.debug("data snapshot " + snapshotId + " of " + reportUnit.getURIString()
						+ " is older (" + snapshotDate + ") than data source (" + dataSourceTimestamp + ")");
			}
			return null;
		}
		
		return persistentSnapshot;
	}
	
	public DataCacheSnapshot getDataSnapshot(ExecutionContext context,
			ReportContext reportContext) {
		ReportDataCacheProvider cacheProvider = (ReportDataCacheProvider) 
				reportContext.getParameterValue(getDataSnapshotProviderParameter());
		if (cacheProvider == null) {
			if (log.isDebugEnabled()) {
				log.debug("no cache provider found");
			}
			
			return null;
		}
		return cacheProvider.getSnapshot();
	}
	
	public boolean parametersMatch(Map<String, Object> dataParameters, 
			Map<String, Object> snapshotParams) {
		boolean match = true;
		for (Map.Entry<String, Object> dataParamsEntry : dataParameters.entrySet()) {
			String key = dataParamsEntry.getKey();
			Object value = dataParamsEntry.getValue();
			Object snapshotValue = snapshotParams == null ? null : snapshotParams.get(key);
			
			if (!valuesEquals(value, snapshotValue)) {
				match = false;
				break;
			}
		}
		return match;
	}

	private boolean valuesEquals(Object paramsValue, Object snapshotValue) {
		if (paramsValue == null) {
			return snapshotValue == null;
		}
		if(snapshotValue == null) {
			return false;
		}

		if ((snapshotValue instanceof Collection<?>) &&
				(paramsValue instanceof Collection<?>)) {
			return CollectionUtils.isEqualCollection(
					(Collection<?>) paramsValue, (Collection<?>) snapshotValue);
		}

		return snapshotValue.equals(paramsValue);
	}

	public void snapshotSaved(ExecutionContext context,
			ReportContext reportContext, DataCacheSnapshot snapshot,
			Long savedSnapshotId) {
		// the only thing we need to do is mark the snapshot as saved
		if (!snapshot.isSaved()) {
			ReportDataCacheProvider cacheProvider = (ReportDataCacheProvider) 
					reportContext.getParameterValue(getDataSnapshotProviderParameter());
			if (cacheProvider != null) {
				cacheProvider.setSavedDataSnapshot(snapshot);
				cacheProvider.setHadSavedSnapshot(true);
			}
		}
	}

	public SnapshotSaveStatus getSnapshotSaveStatus(ReportContext reportContext) {
		ReportDataCacheProvider cacheProvider = reportContext == null ? null 
				: (ReportDataCacheProvider) reportContext.getParameterValue(getDataSnapshotProviderParameter());
		SnapshotSaveStatus status;
		if (cacheProvider == null) {
			// should not happen, so let's be conservative
			status = SnapshotSaveStatus.NO_CHANGE;
		} else {
			DataCacheSnapshot snapshot = cacheProvider.getSnapshot();
			boolean hadSavedSnapshot = cacheProvider.hadSavedSnapshot();
			if (!dataSnapshotService.isSnapshotPersistenceEnabledForReportUri((String) reportContext.getParameterValue("reportUnit"))
					|| snapshot == null || !snapshot.getSnapshot().isPersistable()) {
				// if we can't save a snapshot, check if we had a saved snapshot that we need to delete
				status = hadSavedSnapshot ? SnapshotSaveStatus.UPDATED : SnapshotSaveStatus.NO_CHANGE;
			} else if (snapshot.isSaved()) {
				// already saved snapshot
				status = SnapshotSaveStatus.NO_CHANGE;
			} else {
				// we have a new snapshot to save, check if there is an old one
				status = hadSavedSnapshot ? SnapshotSaveStatus.UPDATED : SnapshotSaveStatus.NEW;
			}
		}
		return status;
	}
	
	public String getDataSnapshotProviderParameter() {
		return dataSnapshotProviderParameter;
	}

	public void setDataSnapshotProviderParameter(String dataSnapshotProviderParameter) {
		this.dataSnapshotProviderParameter = dataSnapshotProviderParameter;
	}

	public ReportLoadingService getReportLoadingService() {
		return reportLoadingService;
	}

	public void setReportLoadingService(ReportLoadingService reportLoadingService) {
		this.reportLoadingService = reportLoadingService;
	}

	public DataSnapshotService getDataSnapshotService() {
		return dataSnapshotService;
	}

	public void setDataSnapshotService(DataSnapshotService dataSnapshotService) {
		this.dataSnapshotService = dataSnapshotService;
	}

}
