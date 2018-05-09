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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.data.cache.*;
import net.sf.jasperreports.data.cache.DataSnapshot;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateDataSnapshotService extends HibernateDaoImpl implements DataSnapshotPersistenceService {

	private final static Log log = LogFactory.getLog(HibernateDataSnapshotService.class);
	
	private DataSnapshotContentsPersistenceService contentsPersistenceService;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public boolean matchesVersion(ExecutionContext context, final long snapshotId,
			final int version) {
		return getHibernateTemplate().execute(new HibernateCallback<Boolean>() {
			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PersistentDataSnapshot.class);
				criteria.add(Restrictions.idEq(snapshotId));
				criteria.add(Restrictions.eq("version", version));
				criteria.setProjection(Projections.rowCount());
				Number count = (Number) criteria.uniqueResult();
				
				if (log.isDebugEnabled()) {
					log.debug("version check for " + snapshotId + " and " + version
							+ " returned " + count);
				}
				
				return count.intValue() > 0;
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public DataSnapshotPersistentMetadata loadDataSnapshotMetadata(
			ExecutionContext context, long snapshotId) {
		PersistentDataSnapshot persistentSnapshot = getHibernateTemplate().get(PersistentDataSnapshot.class, snapshotId);
		if (persistentSnapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("didn't find snapshot " + snapshotId);
			}
			return null;
		}
		
		DataSnapshotPersistentMetadata snapshotMeta = createSnapshotPersistentMetadata(persistentSnapshot);
		return snapshotMeta;
	}

	protected DataSnapshotPersistentMetadata createSnapshotPersistentMetadata(
			PersistentDataSnapshot persistentSnapshot) {
		DataCacheSnapshotMetadata metadata = createSnapshotMetadata(persistentSnapshot);
		DefaultDataSnapshotPersistentMetadata persistentMetadata = new DefaultDataSnapshotPersistentMetadata();
		persistentMetadata.setSnapshotMetadata(metadata);
		persistentMetadata.setVersion(persistentSnapshot.getVersion());
		persistentMetadata.setContentsId(persistentSnapshot.getContentsId());
		return persistentMetadata;
	}

	protected DataCacheSnapshotMetadata createSnapshotMetadata(
			PersistentDataSnapshot persistentSnapshot) {
		Map<String, Object> paramsCopy = copyParams(persistentSnapshot.getDataParameters());
		DefaultDataCacheSnapshotMetadata snapshotMeta = new DefaultDataCacheSnapshotMetadata(
				paramsCopy, persistentSnapshot.getSnapshotDate());
		return snapshotMeta;
	}

	protected Map<String, Object> copyParams(Map<String, Object> params) {
		return params == null ? new HashMap<String, Object>()
				: new HashMap<String, Object>(params);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public DataSnapshot loadDataSnapshot(ExecutionContext context, long contentsId) {
		// simply delegate
		return contentsPersistenceService.loadDataSnapshot(context, contentsId);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public DataCacheSnapshotData loadSnapshotData(ExecutionContext context,
			long snapshotId) {
		PersistentDataSnapshot persistentSnapshot = getHibernateTemplate().get(PersistentDataSnapshot.class, snapshotId);
		if (persistentSnapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("didn't find snapshot " + snapshotId);
			}
			return null;
		}
		
		DataCacheSnapshotMetadata snapshotMeta = createSnapshotMetadata(persistentSnapshot);
		DataContainer snapshotData = contentsPersistenceService.loadDataSnapshotData(
				context, persistentSnapshot.getContentsId());
		if (snapshotData == null) {
			if (log.isDebugEnabled()) {
				log.debug("didn't find snapshot data " + persistentSnapshot.getContentsId());
			}
			return null;
		}

		if (context != null &&
				DiagnosticSnapshotPropertyHelper.isDiagSnapshotSet(context.getAttributes())) {
			snapshotMeta.getParameters().put(
					DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT,
					String.valueOf(true));
		}

		DataCacheSnapshotData snapshot = new DataCacheSnapshotData();
		snapshot.setMetadata(snapshotMeta);
		snapshot.setSnapshotData(snapshotData);
		return snapshot;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public DataSnapshotSavedId saveDataSnapshot(final ExecutionContext context,
			final DataCacheSnapshot snapshot, final Long existingSnapshotId) {
		return getHibernateTemplate().execute(new HibernateCallback<DataSnapshotSavedId>() {
			public DataSnapshotSavedId doInHibernate(Session session) throws HibernateException, SQLException {
				PersistentDataSnapshot persistentSnapshot = null;
				if (existingSnapshotId != null) {
					persistentSnapshot = getHibernateTemplate().get(PersistentDataSnapshot.class, existingSnapshotId);
					if (persistentSnapshot == null) {
						log.info("Did not find snapshot " + existingSnapshotId + ", saving as new");
					}
				}
				
				boolean isNew = false;
				if (persistentSnapshot == null) {
					persistentSnapshot = new PersistentDataSnapshot();
					isNew = true;
				}
				
				if (!isNew) {
					// deleting existing data
					long currentContentsId = persistentSnapshot.getContentsId();
					contentsPersistenceService.deleteDataSnapshot(context, currentContentsId);
				}
				
				// saving the new snapshot
				long contentsId = contentsPersistenceService.saveDataSnapshot(context, snapshot.getSnapshot());
				persistentSnapshot.setContentsId(contentsId);
				
				DataCacheSnapshotMetadata snapshotMeta = snapshot.getMetadata();
				persistentSnapshot.setSnapshotDate(snapshotMeta.getSnapshotDate());
				
				Map<String, Object> paramsCopy = copyParams(snapshotMeta.getParameters());
				persistentSnapshot.setDataParameters(paramsCopy);
				
				if (isNew) {
					session.save(persistentSnapshot);
				}
				session.flush();
				
				long savedId = persistentSnapshot.getId();
				int savedVersion = persistentSnapshot.getVersion();
				if (log.isDebugEnabled()) {
					log.debug("saved data snapshot " + savedId + " at version " + savedVersion);
				}
				
				return new DataSnapshotSavedId(savedId, savedVersion, contentsId);
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public long saveDataSnapshot(final ExecutionContext context, final DataCacheSnapshotData snapshot) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				PersistentDataSnapshot persistentSnapshot = new PersistentDataSnapshot();

				// saving the new snapshot
				long contentsId = contentsPersistenceService.saveDataSnapshot(context, snapshot.getSnapshotData());
				persistentSnapshot.setContentsId(contentsId);

				DataCacheSnapshotMetadata snapshotMeta = snapshot.getMetadata();
				persistentSnapshot.setSnapshotDate(snapshotMeta.getSnapshotDate());

				Map<String, Object> paramsCopy = copyParams(snapshotMeta.getParameters());
				persistentSnapshot.setDataParameters(paramsCopy);

				session.save(persistentSnapshot);
				session.flush();

				long savedId = persistentSnapshot.getId();
				int savedVersion = persistentSnapshot.getVersion();
				if (log.isDebugEnabled()) {
					log.debug("saved data snapshot " + savedId + " at version " + savedVersion);
				}
				
				return savedId;
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteSnapshot(long snapshotId) {
    	if (log.isDebugEnabled()) {
    		log.debug("deleting snapshot " + snapshotId);
    	}
    	
		PersistentDataSnapshot persistentSnapshot = getHibernateTemplate().get(PersistentDataSnapshot.class, snapshotId);
		if (persistentSnapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("didn't find snapshot " + snapshotId + " for deletion ");
			}
			return;
		}
		
		long contentsId = persistentSnapshot.getContentsId();
		contentsPersistenceService.deleteDataSnapshot(null, contentsId);
		
		getHibernateTemplate().delete(persistentSnapshot);
	}

	public Long copyDataSnapshot(long snapshotId) {
    	if (log.isDebugEnabled()) {
    		log.debug("copying snapshot " + snapshotId);
    	}
    	
		final PersistentDataSnapshot persistentSnapshot = getHibernateTemplate().get(PersistentDataSnapshot.class, snapshotId);
		if (persistentSnapshot == null) {
			if (log.isDebugEnabled()) {
				log.debug("didn't find snapshot " + snapshotId + " for copying");
			}
			return null;
		}
		
		long contentsId = persistentSnapshot.getContentsId();
		final Long copyContentsId = contentsPersistenceService.copyDataSnapshot(contentsId);
		if (copyContentsId == null) {
			if (log.isDebugEnabled()) {
				log.debug("failed to copy snapshot contents for id " + contentsId);
			}
			return null;
		}
		
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				PersistentDataSnapshot copy = new PersistentDataSnapshot();
				copy.setSnapshotDate(persistentSnapshot.getSnapshotDate());
				copy.setContentsId(copyContentsId);
				copy.setDataParameters(copyParams(persistentSnapshot.getDataParameters()));
				
				session.save(copy);
				session.flush();
				
				long savedId = copy.getId();
				if (log.isDebugEnabled()) {
					log.debug("saved data snapshot copy " + savedId);
				}
				
				return savedId;
			}
		});
	}

	public DataSnapshotContentsPersistenceService getContentsPersistenceService() {
		return contentsPersistenceService;
	}

	public void setContentsPersistenceService(
			DataSnapshotContentsPersistenceService contentsPersistenceService) {
		this.contentsPersistenceService = contentsPersistenceService;
	}
	
}
