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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import net.sf.jasperreports.data.cache.DataSnapshot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileBufferedDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotContentsPersistenceService;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotSerializer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateDataSnapshotContentsService extends HibernateDaoImpl implements DataSnapshotContentsPersistenceService {

	private static final Log log = LogFactory.getLog(HibernateDataSnapshotContentsService.class);
	
	private DataSnapshotSerializer snapshotSerializer;
	
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
	public DataSnapshot loadDataSnapshot(ExecutionContext context, final long id) {
		return getHibernateTemplate().execute(new HibernateCallback<DataSnapshot>() {
			public DataSnapshot doInHibernate(Session session) throws HibernateException, SQLException {
				if (log.isDebugEnabled()) {
					log.debug("loading data snapshot " + id);
				}
				
				Blob dataBlob = loadSnapshotDataBlob(id, session);
				if (dataBlob == null) {
					return null;
				}
				
				DataSnapshot snapshot;
				InputStream dataStream = dataBlob.getBinaryStream();
				try {
					snapshot = getSnapshotSerializer().readSnapshot(dataStream);
				} catch (IOException e) {
					throw new JSExceptionWrapper("Failed to read data snapshot", e);
				} finally {
					try {
						dataStream.close();
					} catch (IOException e) {
						log.warn("Failed to close blob stream for data snapshot " + id, e);
					}
				}
				return snapshot;
			}
		});
	}

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
	public DataContainer loadDataSnapshotData(ExecutionContext context, final long id) {
		return getHibernateTemplate().execute(new HibernateCallback<DataContainer>() {
			public DataContainer doInHibernate(Session session) throws HibernateException, SQLException {
				if (log.isDebugEnabled()) {
					log.debug("loading snapshot data " + id);
				}
				
				Blob dataBlob = loadSnapshotDataBlob(id, session);
				if (dataBlob == null) {
					return null;
				}
				
				FileBufferedDataContainer dataContainer;
				InputStream dataStream = dataBlob.getBinaryStream();
				try {
					dataContainer = new FileBufferedDataContainer();
					OutputStream dataOut = dataContainer.getOutputStream();
					try {
						DataContainerStreamUtil.pipeData(dataStream, dataOut);
					} finally {
						dataOut.close();// fail on close exception
					}
				} catch (IOException e) {
					throw new JSExceptionWrapper("Failed to read data snapshot", e);
				} finally {
					try {
						dataStream.close();
					} catch (IOException e) {
						log.warn("Failed to close blob stream for data snapshot " + id, e);
					}
				}
				return dataContainer;
			}
		});
	}

    @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
	public long saveDataSnapshot(ExecutionContext context, final DataSnapshot snapshot) {
		if (log.isDebugEnabled()) {
			log.debug("saving snapshot");
		}
		
		DataContainer dataContainer = new FileBufferedDataContainer();
		try {
			boolean closeOut = true;
			OutputStream out = dataContainer.getOutputStream();
			try {
				// write the snapshot to a temporary location
				getSnapshotSerializer().writeSnapshot(snapshot, out);
				closeOut = false;
				out.close();
			} catch (IOException e) {
				throw new JSExceptionWrapper("Failed to serialize data snapshot", e);
			} finally {
				if (closeOut) {
					try {
						out.close();
					} catch (IOException e) {
						log.warn("Failed to close data container stream for data snapshot");
					}
				}
			}

			return saveSnapshotData(dataContainer);
		} finally {
			dataContainer.dispose();
		}
	}

	public long saveDataSnapshot(ExecutionContext context, final DataContainer snapshotData) {
		if (log.isDebugEnabled()) {
			log.debug("saving snapshot data");
		}
		
		return saveSnapshotData(snapshotData);
	}

    @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
	public void deleteDataSnapshot(ExecutionContext context, final long id) {
		getHibernateTemplate().execute(new HibernateCallback<Void>() {
			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				if (log.isDebugEnabled()) {
					log.debug("deleting snapshot data " + id);
				}
				
				// deleting via a bulk query so that we don't need to load the entity (including the data)
				int count = session.getNamedQuery("JIDataSnapshotContentsDeleteId").setLong("id", id).executeUpdate();
				
				if (log.isDebugEnabled()) {
					log.debug("deleted " + count + " records for snapshot " + id);
				}
				
				if (count > 1) {
					// protect against any accidents
					throw new JSException("Deletion of data snapshot " + id + " removed " + count + " records");
				}
				
				return null;
			}
		});
	}

	public DataSnapshotSerializer getSnapshotSerializer() {
		return snapshotSerializer;
	}

	public void setSnapshotSerializer(DataSnapshotSerializer snapshotSerializer) {
		this.snapshotSerializer = snapshotSerializer;
	}

	protected Blob loadSnapshotDataBlob(final long id, Session session) {
		PersistentDataSnapshotContents persistentSnapshot = (PersistentDataSnapshotContents) session.get(
				PersistentDataSnapshotContents.class, id);
		if (persistentSnapshot == null) {
			log.info("Data snapshot " + id + " not found");
			return null;
		}
		
		Blob dataBlob = persistentSnapshot.getData();
		if (dataBlob == null) {
			log.warn("Data snapshot " + id + " is empty");
			return null;
		}
		
		return dataBlob;
	}

	protected long saveSnapshotData(final DataContainer snapshotData) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {
			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				PersistentDataSnapshotContents persistentSnapshot = new PersistentDataSnapshotContents();
				
				// create the blob
				Blob dataBlob;
				try {
					dataBlob = Hibernate.createBlob(snapshotData.getInputStream());
				} catch (IOException e) {
					throw new JSExceptionWrapper("Failed to create data snapshot blob", e);
				}
				
				persistentSnapshot.setData(dataBlob);
				
				if (log.isDebugEnabled()) {
					log.debug("saving data snapshot to the DB");
				}
				
				session.save(persistentSnapshot);
				session.flush();
				
				long savedId = persistentSnapshot.getId();
				
				if (log.isDebugEnabled()) {
					log.debug("saved data snapshot " + savedId);
				}
				
				return savedId;
			}
		});
	}

	public Long copyDataSnapshot(long id) {
		if (log.isDebugEnabled()) {
			log.debug("copying snapshot data " + id);
		}
		
		DataContainer snapshotData = loadDataSnapshotData(null, id);//FIXME direct copy without loading
		if (snapshotData == null) {
			if (log.isDebugEnabled()) {
				if (log.isDebugEnabled()) {
					log.debug("data for snapshot contents " + id + " not found for copy");
				}
				return null;
			}
		}
		
		// save as new
		return saveSnapshotData(snapshotData);
	}
	
}
