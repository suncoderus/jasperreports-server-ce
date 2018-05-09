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

package com.jaspersoft.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQLDialect;

import com.jaspersoft.hibernate.ByteWrappingBlobType;

/**
 * This dialect maps blobs to binary streams for PostgreSQL.
 * This is necessary because PostgreSQL requires blob access to happen within a transaction.
 * That means we have to turn defaultAutoCommit off which has some weird side effects.
 * Using the "bytea" type should work better.
 * 
 * There are a few aspects to implementing this:
 * - Generate the "bytea" type for columns with Hibernate type "blob"
 * - Set the "mapBlobsToBinaryType" flag, which will cause ByteWrappingBlobType.set() 
 *   to use ResultSet.setBinaryStream() instead of ResultSet.setBlob()
 * - Use ByteWrappingBlobType (a subclass of BlobType) instead of "blob" in the Hibernate mapping files.
 *   This overrides BlobType.get() to call ResultSet.getObject() instead of getBlob, and then turns
 *   whatever it gets into a Blob if it isn't one already.
 *   
 *   I'm not sure why we have to use blobs, and they're only used in 4 places. However, I do know that
 *   Oracle has a brain-dead binary type (LONG RAW) that we should stay away from, and if we've already
 *   tested with blobs on other databases, then just let it be.
 */
public class PostgresqlNoBlobDialect extends PostgreSQLDialect {

    public PostgresqlNoBlobDialect() {
        super();
        // use "bytea" to map blob types
        registerColumnType( Types.BLOB, "bytea" );
        // turn on blob mapping
        getDefaultProperties().setProperty(ByteWrappingBlobType.MAP_BLOBS_TO_BINARY_TYPE, String.valueOf(true));
    }

}