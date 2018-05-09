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

package com.jaspersoft.jasperserver.remote;

/**
 * The resource action resolver interface. This class is used to decide if a resource
 * can be deleted or created. 
 *
 * @author Yuriy Plakosh
 * @version $Id$
 * @since 3.7.0.1
 */
public interface ResourceActionResolver {

    /**
     * Checks if the resource with the specified URI can be deleted.
     *
     * @param resourceUri the URI of the resource.
     *
     * @return <code>true</code> if the resource can be deleted, <code>false</code> otherwise.
     */
    public boolean isResourceDeletable(String resourceUri);

    /**
     * Checks if the resource can be created in the destination folder.
     *
     * @param destinationFolder the URI of the destination folder.
     *
     * @return <code>true</code> if the resource can be created, <code>false</code> otherwise.
     */
    public boolean canCreateResource(String destinationFolder);
}
