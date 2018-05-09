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
package com.jaspersoft.jasperserver.api.metadata.data.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import net.sf.jasperreports.data.cache.DataSnapshot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JavaDataSnapshotSerializer implements DataSnapshotSerializer {

	private final static Log log = LogFactory.getLog(JavaDataSnapshotSerializer.class);
	
	public void writeSnapshot(DataSnapshot snapshot, OutputStream out) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("serializing data snapshot of type " + snapshot.getClass().getName());
		}
		
		ObjectOutputStream objectOut = new ObjectOutputStream(out);
		objectOut.writeObject(snapshot);
	}

	public DataSnapshot readSnapshot(InputStream in) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("deserializing data snapshot");
		}
		
		ObjectInputStream objectInput = new ObjectInputStream(in);
		DataSnapshot snapshot;
		try {
			snapshot = (DataSnapshot) objectInput.readObject();
		} catch (ClassNotFoundException e) {
			throw new JSExceptionWrapper("Failed to deserialize data snapshot", e);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("deserialized data snapshot of type " + snapshot.getClass().getName());
		}
		
		return snapshot;
	}

}
