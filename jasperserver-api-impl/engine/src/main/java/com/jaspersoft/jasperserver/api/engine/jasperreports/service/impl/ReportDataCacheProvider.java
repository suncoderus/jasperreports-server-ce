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
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DefaultDataCacheSnapshot;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.SavedDataCacheSnapshotDecorator;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DefaultDataCacheSnapshotMetadata;

import net.sf.jasperreports.data.cache.ColumnDataCacheHandler;
import net.sf.jasperreports.data.cache.DataCacheHandler;
import net.sf.jasperreports.data.cache.DataRecorder;
import net.sf.jasperreports.data.cache.DataSnapshot;
import net.sf.jasperreports.data.cache.DatasetRecorder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportDataCacheProvider {
	
	private volatile DataCacheSnapshot snapshot;
	private boolean hadSavedSnapshot;

	public DataCacheSnapshot getSnapshot() {
		return snapshot;
	}
	
	public boolean hadSavedSnapshot() {
		return hadSavedSnapshot;
	}

	public void setSnapshot(DataCacheSnapshot snapshot) {
		this.snapshot = snapshot;
	}

	public void setSavedDataSnapshot(DataCacheSnapshot snapshot) {
		SavedDataCacheSnapshotDecorator persistenceSnapshot = new SavedDataCacheSnapshotDecorator(snapshot);
		setSnapshot(persistenceSnapshot);
	}

	public void setHadSavedSnapshot(boolean hadSavedSnapshot) {
		this.hadSavedSnapshot = hadSavedSnapshot;
	}
	
	public void clearSnapshot() {
		setSnapshot(null);
	}

	public DataCacheHandler createRecordHandler(Map<String, Object> parameters) {
		ColumnDataCacheHandler delegateHandler = new ColumnDataCacheHandler();
		return new RecordHandler(parameters, delegateHandler);
	}
	
	protected void setRecorderDataSnapshot(DataCacheSnapshot snapshot) {
		setSnapshot(snapshot);
	}

	protected class RecordHandler implements DataCacheHandler {

		private final Map<String, Object> parameters;
		private final DataCacheHandler delegate;
		private Date snasphotDate;

		public RecordHandler(Map<String, Object> parameters, DataCacheHandler delegate) {
			this.parameters = parameters;
			this.delegate = delegate;
			
			// for now initializing here should be fine
			// maybe we should move it to the first createDataRecorder?
			this.snasphotDate = new Date();
		}
		
		public boolean isRecordingEnabled() {
			return delegate.isRecordingEnabled();
		}

		public DataRecorder createDataRecorder() {
			DataRecorder dataRecorder = delegate.createDataRecorder();
			return new Recorder(dataRecorder);
		}

		public boolean isSnapshotPopulated() {
			return false;
		}

		public DataSnapshot getDataSnapshot() {
			return null;
		}

		protected void wrapDataSnapshot() {
			DataSnapshot dataSnapshot = delegate.getDataSnapshot();
			DefaultDataCacheSnapshotMetadata metadata = 
					new DefaultDataCacheSnapshotMetadata(parameters, snasphotDate);
			DataCacheSnapshot snapshot = new DefaultDataCacheSnapshot(dataSnapshot, metadata);
			setRecorderDataSnapshot(snapshot);
		}
		
		protected class Recorder implements DataRecorder {
			final DataRecorder delegate;
			
			public Recorder(DataRecorder delegate) {
				this.delegate = delegate;
			}

			public DatasetRecorder createRecorder() {
				return delegate.createRecorder();
			}

			public void addRecordResult(Object key, Object recorded) {
				delegate.addRecordResult(key, recorded);
			}

			public void setSnapshotPopulated() {
				delegate.setSnapshotPopulated();
				
				if (isEnabled()) {
					wrapDataSnapshot();
				}
			}

			public void disableRecording() {
				delegate.disableRecording();				
			}

			public void disablePersistence() {
				delegate.disablePersistence();
			}

			public boolean isEnabled() {
				return isRecordingEnabled();
			}
		}
	}

}
