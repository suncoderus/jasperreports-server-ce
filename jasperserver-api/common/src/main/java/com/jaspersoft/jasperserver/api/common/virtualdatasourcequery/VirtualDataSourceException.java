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
package com.jaspersoft.jasperserver.api.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.sql.SQLException;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id
 */
@JasperServerAPI
public class VirtualDataSourceException extends SQLException {

    String virtualDataSourceID;
    Set<String> schemas;

	public VirtualDataSourceException(Throwable cause)
	{
		super(cause);
	}

	public VirtualDataSourceException(String message)
	{
		super(message);
	}

	public VirtualDataSourceException(String message, Throwable cause)
	{
		super(message, cause);
	}

    public String getVirtualDataSourceID() {
        return virtualDataSourceID;
    }

    public void setVirtualDataSourceID(String virtualDataSourceID) {
        this.virtualDataSourceID = virtualDataSourceID;
    }

    public Set<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(Set<String> schemas) {
        this.schemas = schemas;
    }
}
