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

package com.jaspersoft.jasperserver.api.metadata.jasperreports.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * A Data Source Service which can test its connection.
 */
@JasperServerAPI
public interface ConnectionTestingDataSourceService {

    /**
     * Tests connection.
     * Implementors can indicate failed test by throwing an exception or returning <code>false</code>.
     * @return <code>true</code> if connection was tested successfully.
     * @throws Exception
     */
    boolean testConnection() throws Exception;
}
