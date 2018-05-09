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

package com.jaspersoft.hibernate;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.BlobType;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ComparableBlob;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

public class ByteWrappingBlobType extends BlobType {
	
	public static Logger log = Logger.getLogger(ByteWrappingBlobType.class);
	
	public static final String MAP_BLOBS_TO_BINARY_TYPE = "mapBlobsToBinaryType";

	public void set(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		// we are using the useInputStreamToInsertBlob() as a proxy for mapping blobs to binaries
		String mapProp = session.getFactory().getDialect().getDefaultProperties().getProperty(MAP_BLOBS_TO_BINARY_TYPE);
		final boolean mapBlobToStreams = String.valueOf(true).equals(mapProp);
		
		// if we are setting a NULL value, it's important that we know what the real DB type is
		// if we are mapping to a binary type, then use that type to set null
		if (value == null) {
			st.setNull(index, mapBlobToStreams ? Types.BINARY : Types.BLOB);
		} else {
			Blob blob = (Blob) value;
			if (mapBlobToStreams) {
				st.setBinaryStream( index, blob.getBinaryStream(), (int) blob.length() );
			}
			else {
				super.set(st, value, index, session);
			}
		}
	}

	/**
	 * on calling get(), let's just call getObject() and see what happens, then turn it
	 * into a blob if it isn't one.
	 */
	public Object get(ResultSet rs, String name) throws HibernateException,	SQLException {
		Object value = rs.getObject(name);
		if (rs.wasNull()) {
			return null;
		}
		if (value instanceof Blob) {
			log.debug("getting blob for " + name);
			return DataContainerStreamUtil.createComparableBlob((Blob) value);
		} else if (value instanceof byte[]) {
			log.debug("getting byte[" + ((byte[])value).length + "] for " + name);
			return DataContainerStreamUtil.createComparableBlob((byte[]) value);
		} else if (value instanceof InputStream) {
			try {
				log.debug("getting input stream for " + name);
				return DataContainerStreamUtil.createComparableBlob((InputStream) value);
			} catch (Exception e) {
				throw new HibernateException("exception creating blob from input stream", e);
			}
		} else {
			throw new HibernateException("I don't know how to map the type " + value.getClass().getName() + " to a blob");
		}
	}

	// super implementation uses ==, which doesn't work because we recreate the blob
	// we have created a ComparableBlob class which lets us make sure that the byte array is the same
	public boolean isEqual(Object x, Object y, EntityMode entityMode) {
		if (x != null && y != null) {
			return x.equals(y);
		}
		return super.isEqual(x, y, entityMode);
	}

	
	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
		byte[] bytes = (byte[]) cached;
		return (bytes == null) ? null : new ComparableBlob(Arrays.copyOf(bytes, bytes.length));
	}

	public Serializable disassemble(Object value, SessionImplementor session, Object owner)	throws HibernateException {
		if (value instanceof ComparableBlob) {
			ComparableBlob cblob = (ComparableBlob) value;
			return Arrays.copyOf(cblob.getBytes(), cblob.getBytes().length);
		} else if (value == null) {
			return null;
		} else {
            try {
                Blob blob = (Blob) value;
                byte[] data = new byte[(int)blob.length()];
                blob.getBinaryStream().read(data);
                return data;
            } catch (Exception e) {
                return super.disassemble(value, session, owner);
            }
		}
	}

}
