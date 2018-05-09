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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.data.cache.*;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import net.sf.jasperreports.data.cache.DataSnapshot;
import net.sf.jasperreports.engine.ReportContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@Component
public class StandardDataSnapshotService implements DataSnapshotService {

	private static final Log log = LogFactory.getLog(StandardDataSnapshotService.class);
	
	private boolean snapshotRecordingEnabled;
	private boolean snapshotPersistenceEnabled;
	
	private DataSnapshotPersistenceService persistenceService;
	private DataSnapshotCachingService cachingService;
	private DataCacheProvider dataCacheProvider;
	private RepositoryService repository;
	private RepositoryService unsecureRepository;
    @Autowired(required = false)
    private DiagnosticParametersWatcher diagnosticParametersWatcher;

	public DataSnapshotPersistentMetadata getSnapshotMetadata(ExecutionContext context, long snapshotId) {
		// look in the cache for the metadata
		DataSnapshotPersistentMetadata metadata = cachingService.getSnapshotMetadata(context, snapshotId);
		if (metadata == null) {
			if (log.isDebugEnabled()) {
				log.debug("no cached snapshot " + snapshotId);
			}
		} else {
			// verify the version with the latest persistent version
			int cachedVersion = metadata.getVersion();
			if (persistenceService.matchesVersion(context, snapshotId, cachedVersion)) {
				if (log.isDebugEnabled()) {
					log.debug("found cached snapshot " + snapshotId + " at version " + cachedVersion);
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("invalidating cached snapshot " + snapshotId + " with version " + cachedVersion);
				}
				
				// purge the old entry from the cache
				cachingService.invalidateSnapshot(context, snapshotId);
				metadata = null;
			}
		}
		
		if (metadata == null) {
			if (log.isDebugEnabled()) {
				log.debug("loading data snapshot metadata " + snapshotId);
			}
			
			// load from the persistent storage
			metadata = persistenceService.loadDataSnapshotMetadata(context, snapshotId);
			
			if (metadata == null) {
				// this shouldn't normally happen, but treating as a non blocking error
				log.info("saved data snapshot metadata " + snapshotId + " not found");
				return null;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("loaded snapshot metadata " + snapshotId 
						+ " at version " + metadata.getVersion());
			}
			
			// put it in the cache
			cachingService.putSnapshotMetadata(context, snapshotId, metadata);
		}
		
		return metadata;
	}

	public DataSnapshot getSnapshotContents(ExecutionContext context, long contentsId) {
		DataSnapshot snapshotContents = cachingService.getSnapshotContents(context, contentsId);
		if (snapshotContents == null) {
			if (log.isDebugEnabled()) {
				log.debug("loading data snapshot contents " + contentsId);
			}
			
			snapshotContents = persistenceService.loadDataSnapshot(context, contentsId);
			
			if (snapshotContents == null) {
				// this shouldn't normally happen, but treating as a non blocking error
				log.info("saved data snapshot contents " + contentsId + " not found");
				return null;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("loaded data snapshot contents " + contentsId);
			}
			
			cachingService.putSnapshotContents(context, contentsId, snapshotContents);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("found cached snapshot contents " + contentsId);
			}
		}
		return snapshotContents;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public long saveDataSnapshot(ExecutionContext context,
			DataCacheSnapshot snapshot, Long existingSnapshotId) {
		// saving
    	DataSnapshotSavedId saved = persistenceService.saveDataSnapshot(context, snapshot, existingSnapshotId);
		
		// also adding to the cache
		cachingService.put(context, saved, snapshot);
		
		return saved.getSnapshotId();
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteSnapshot(ExecutionContext context, long snapshotId) {
    	persistenceService.deleteSnapshot(snapshotId);
    	
    	// remove from the cache
    	cachingService.invalidateSnapshot(context, snapshotId);
    }
	
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
	public boolean saveAutoReportDataSnapshot(ExecutionContext context, ReportContext reportContext, ReportUnit reportUnit) {
    	// this method should only be called for initial data snapshots saves, not updates
    	DataCacheProvider.SnapshotSaveStatus saveStatus = dataCacheProvider.getSnapshotSaveStatus(reportContext);
    	if (saveStatus != DataCacheProvider.SnapshotSaveStatus.NEW) {
    		throw new JSException("Data snapshot is not new");
    	}
    	
		ReportUnit repositoryRU = loadRepositoryReportUnit(context, reportUnit);
		boolean dirtyReportUnit = applyReportUnitChanges(context,
				reportContext, repositoryRU, false, reportUnit.getURIString());

		if (!dirtyReportUnit) {
			// nothing to save in the report unit
			return false;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("saving report unit " + repositoryRU.getPath());
		}

		getUnsecureRepository().saveResource(context, repositoryRU);
		return true;
	}
	
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
	public boolean saveReportDataSnapshot(ExecutionContext context, ReportContext reportContext, ReportUnit reportUnit) {
		ReportUnit repositoryRU = loadRepositoryReportUnit(context, reportUnit);
		boolean dirtyReportUnit = applyReportUnitChanges(context,
				reportContext, repositoryRU, false, reportUnit.getURIString());

		if (!dirtyReportUnit) {
			// nothing to save in the report unit
			return false;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("saving report unit " + repositoryRU.getPath());
		}

		getRepository().saveResource(context, repositoryRU);
		return true;
	}

	protected boolean applyReportUnitChanges(ExecutionContext context,
			ReportContext reportContext, ReportUnit reportUnit, boolean copyDataSnapshot,
			String originalReportUri) {
		boolean dirtyReportUnit = false;
		
		Long currentSnapshotId = reportUnit.getDataSnapshotId();
		Long existingSnapshotId = copyDataSnapshot ? null : reportUnit.getDataSnapshotId();
		DataSnapshotPersistResult result = persistDataSnapshot(context, reportContext,
				existingSnapshotId);
		Long savedSnapshotId = result.getSavedSnapshotId();
		if (currentSnapshotId == null
				? savedSnapshotId != null
				: (savedSnapshotId == null || !currentSnapshotId.equals(savedSnapshotId))) {
			// data snapshot Id has changed
			reportUnit.setDataSnapshotId(savedSnapshotId);
			dirtyReportUnit = true;
		}
		
		return dirtyReportUnit;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
	public DataSnapshotPersistResult persistDataSnapshot(ExecutionContext context, ReportContext reportContext, 
			Long existingSnapshotId) {
		// if not copying, only saving data snapshot when it has modified
		DataCacheSnapshot snapshot = dataCacheProvider.getDataSnapshot(context, reportContext);
		
		if (!isSnapshotPersistenceEnabledForReportUri((String) reportContext.getParameterValue("reportUnit"))
				|| (snapshot != null && !snapshot.getSnapshot().isPersistable())) {
			if (log.isDebugEnabled()) {
				log.debug("data snapshot not persistable");
			}
			// do not save anything
			snapshot = null;
		}
		
		boolean changed;
		Long savedSnapshotId;
		if (snapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("no data snapshot to save");
			}
			
			savedSnapshotId = null;
			
			if (existingSnapshotId == null) {
				changed = false;
			} else {
				// deleting current snapshot
				if (log.isDebugEnabled()) {
					log.debug("deleting data snapshot " + existingSnapshotId);
				}
				
				deleteSnapshot(context, existingSnapshotId);
				changed = true;
			}
		} else {
			if (snapshot.isSaved() && existingSnapshotId != null) {
				// the snapshot didn't change, nothing to save
				if (log.isDebugEnabled()) {
					log.debug("snapshot " + existingSnapshotId + " has not changed");
				}
				
				savedSnapshotId = existingSnapshotId;
				changed = false;
			} else {
				if (log.isDebugEnabled()) {
					log.debug("saving data snapshot with current id " + existingSnapshotId);
				}
				
				savedSnapshotId = saveDataSnapshot(context, snapshot, existingSnapshotId);
				changed = true;
				
				dataCacheProvider.snapshotSaved(context, reportContext, snapshot, savedSnapshotId);
				
				if (log.isDebugEnabled()) {
					log.debug("saved data snapshot " + savedSnapshotId);
				}
			}
		}

		return new DataSnapshotPersistResult(changed, savedSnapshotId);
	}

	protected ReportUnit loadRepositoryReportUnit(ExecutionContext context,
			ReportUnit reportUnit) {
		// reload the report unit because the view report flow modifies it
		ReportUnit repositoryRU = (ReportUnit) getRepository().getResource(context, 
				reportUnit.getURIString(), ReportUnit.class);
		if (repositoryRU == null) {
			throw new JSException("Report unit not found at " + reportUnit.getURIString());
		}
		
		// check if the report got modified since loading
		if (repositoryRU.getVersion() != reportUnit.getVersion()) {
			throw new JSException("Report unit has been modified after it was loaded");
		}
		return repositoryRU;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public String saveReportDataSnapshotCopy(ExecutionContext context, ReportContext reportContext, ReportUnit reportUnit,
    		String folderUri, String label, boolean overwrite)
    		throws CopyDestinationExistsException {
    	String copyName = makeResourceName(label);
    	String copyURI = RepositoryUtils.concatenatePath(folderUri, copyName);
    	
		// fetching as ResourceLookup so that we don't fully load the resource
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createResourceURIFilter(copyURI));
		List<ResourceLookup> resources = getRepository().loadResourcesList(context, criteria);
    	if (resources != null && !resources.isEmpty()) {
    		ResourceLookup resource = resources.get(0);
    		// first check that the target is a report unit
    		if (!ReportUnit.class.getName().equals(resource.getResourceType())) {
    			throw new CopyDestinationNotReportException(copyURI);
    		}
    		
    		if (!overwrite) {
    			throw new CopyDestinationExistsException(copyURI);
    		}
    		
    		if (log.isDebugEnabled()) {
    			log.debug("deleting existing resource " + copyURI);
    		}
    		getRepository().deleteResource(context, copyURI);
    	}
    	
		ReportUnit reportUnitCopy = getRepository().makeResourceCopy(context, 
				reportUnit.getURIString(), reportUnit.getVersion(), 
				ReportUnit.class, copyURI);
		
		if (reportUnitCopy == null) {
			throw new JSException("Report unit not found at " + reportUnit.getURIString());
		}
    	
		reportUnitCopy.setLabel(label);
		
    	applyReportUnitChanges(context, reportContext, reportUnitCopy, true, reportUnit.getURIString());
    	
    	if (log.isDebugEnabled()) {
    		log.debug("saving report unit copy of " + reportUnit.getURIString() + " at " + copyURI);
    	}
    	
    	getRepository().saveResource(context, reportUnitCopy);
    	return copyURI;
    }
    
	protected static final Pattern PATTERN_RESOURCE_NAME_REPLACE = Pattern.compile("[^\\p{L}\\p{N}_.-]");
	
	protected String makeResourceName(String label) {
		return PATTERN_RESOURCE_NAME_REPLACE.matcher(label).replaceAll("_");
	}

	public DataCacheSnapshotData loadSnapshotData(ExecutionContext context, Long snapshotId) {
		// simply delegate
		return persistenceService.loadSnapshotData(context, snapshotId);
	}

	public long saveDataSnapshot(ExecutionContext context, DataCacheSnapshotData dataSnapshot) {
		// simply delegate
		return persistenceService.saveDataSnapshot(context, dataSnapshot);
	}

	public Long copyDataSnapshot(Long snapshotId) {
		// simply delegate
		return persistenceService.copyDataSnapshot(snapshotId);
	}
    
	public DataSnapshotPersistenceService getPersistenceService() {
		return persistenceService;
	}

    @SuppressWarnings("unused") // Used by Spring
	public void setPersistenceService(
			DataSnapshotPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public DataSnapshotCachingService getCachingService() {
		return cachingService;
	}

    @SuppressWarnings("unused") // Used by Spring
	public void setCachingService(DataSnapshotCachingService cachingService) {
		this.cachingService = cachingService;
	}

	public DataCacheProvider getDataCacheProvider() {
		return dataCacheProvider;
	}

	public void setDataCacheProvider(DataCacheProvider dataCacheProvider) {
		this.dataCacheProvider = dataCacheProvider;
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public RepositoryService getUnsecureRepository() {
		return unsecureRepository;
	}

    @SuppressWarnings("unused") // Used by Spring
	public void setUnsecureRepository(RepositoryService unsecureRepository) {
		this.unsecureRepository = unsecureRepository;
	}

	public boolean isSnapshotRecordingEnabled() {
		return snapshotRecordingEnabled;
	}

    @SuppressWarnings("unused") // Used by Spring
	public void setSnapshotRecordingEnabled(boolean snapshotRecordingEnabled) {
		this.snapshotRecordingEnabled = snapshotRecordingEnabled;
	}

	@Override
	public boolean isSnapshotRecordingEnabledForReportUri(String reportUri) {
		return isDiagnosticSnapshotExportEnabled(reportUri) && isCreatingSnapshot() || snapshotRecordingEnabled;
	}

	@Override
	public boolean isDiagnosticSnapshotExportEnabled(String reportUri) {
		return diagnosticParametersWatcher != null
				&& diagnosticParametersWatcher.isSnapshotExportEnabled(reportUri);
	}

	public boolean isSnapshotPersistenceEnabled() {
		return snapshotPersistenceEnabled;
	}

    public boolean isSnapshotPersistenceEnabledForReportUri(String reportUri) {
        return isCurrentReportUriBeingWatchedByDiagnostics(reportUri) && isCreatingSnapshot() || snapshotPersistenceEnabled;
    }

    @SuppressWarnings("unused") // Used by Spring
	public void setSnapshotPersistenceEnabled(boolean snapshotPersistenceEnabled) {
		this.snapshotPersistenceEnabled = snapshotPersistenceEnabled;
	}

    @SuppressWarnings("unused") // Used by Spring
    public void setDiagnosticParametersWatcher(DiagnosticParametersWatcher diagnosticParametersWatcher) {
        this.diagnosticParametersWatcher = diagnosticParametersWatcher;
    }

    private boolean isCurrentReportUriBeingWatchedByDiagnostics(String reportUri) {
        return diagnosticParametersWatcher != null
				&& diagnosticParametersWatcher.isResourceUriWatched(reportUri);
    }

    private boolean isCreatingSnapshot() {
        return true;
    }
}
