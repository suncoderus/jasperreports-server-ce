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
package com.jaspersoft.jasperserver.api.metadata.olap.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * Management API for OLAP connections
 *
 * @author sbirney
 * @version $Id$
 */
@JasperServerAPI
public interface OlapManagementService {

    /**
     * Flush all Mondrian cache entries in this server
     */
    public void flushOlapCache();

    /**
     * Flush the Mondrian cache for the Mondrian Connection in the repository
     * referred to by the URI
     *
     * @param uri
     */
    public void flushConnection(String uri);

    /**
     * Flush the Mondrian cache for the Mondrian Connection in the repository
     * referred to by the given Resource
     *
     * @param resource
     */
    public void flushConnection(Resource resource);

    /**
     * Flush the Mondrian cache for the Mondrian Connection in the repository
     * referred to by the given set of parameters
     *
     * @param parameters
     */
    public void flushConnection(MondrianConnectionSchemaParameters parameters);

    /**
     * Notification to all servers of a change in the given Mondrian Connection,
     * leading to flushing
     *
     * @param context
     * @param resource the Mondrian Connection that changed
     */
    public void notifySchemaChange(ExecutionContext context, Resource resource);

    /**
     * Notification to all servers of a use of the given Mondrian Connection
     *
     * @param context
     * @param resource the Mondrian Connection that was used
     */
    public void notifySchemaUse(ExecutionContext context, Resource resource);

    /**
     * Flush Mondrian connection if the given resource is related to a
     * Mondrian connection.
     *
     * @param resource Resource possibly related to a Mondrian Connection
     */
    public void flushIfRelatedToMondrianConnection(Resource resource);
}
