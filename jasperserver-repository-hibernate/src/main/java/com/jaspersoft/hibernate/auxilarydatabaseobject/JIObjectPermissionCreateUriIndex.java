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

package com.jaspersoft.hibernate.auxilarydatabaseobject;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.AbstractAuxiliaryDatabaseObject;

/**
 * Creates <i>uri_index</i> for <i>uri</i> column on JIObjectPermission table
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
public class JIObjectPermissionCreateUriIndex extends AbstractAuxiliaryDatabaseObject {
    @Override
    public String sqlCreateString(Dialect dialect, Mapping mapping, String s, String s1) throws HibernateException {
        if (dialect instanceof MySQLDialect) {
            // We need to explicitly set length for the uri_index index on MySQL database.
            // Its allow to increase size of uri to more then 255 symbols.
            // Otherwise we'll get "#1071 - Specified key was too long; max key length is 767 bytes" error message
            return "create index uri_index on JIObjectPermission (uri(255))";
        } else {
            return "create index uri_index on JIObjectPermission (uri)";
        }
    }

    @Override
    public String sqlDropString(Dialect dialect, String s, String s1) {
        return "DROP INDEX uri_index ON JIObjectPermission";
    }
}
