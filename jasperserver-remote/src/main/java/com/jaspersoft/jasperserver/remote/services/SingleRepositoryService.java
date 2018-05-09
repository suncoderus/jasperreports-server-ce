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

package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.VersionNotMatchException;

import java.io.InputStream;

/**
 * <p>Performs  operations with single resources in transaction</p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public interface SingleRepositoryService {
    Resource getResource(String uri);

    FileResourceData getFileResourceData(String uri);

    FileResourceData getFileResourceData(Resource resource);

    Resource createResource(Resource serverResource, String parentUri, boolean createFolders, boolean dryRun) throws RemoteException;

    Resource updateResource(Resource resource, boolean dryRun) throws ResourceNotFoundException, VersionNotMatchException;

    void deleteResource(String uri) throws IllegalParameterValueException, AccessDeniedException;

    String copyResource(String sourceUri, String destinationUri, boolean createFolders, boolean overwrite, String renameTo) throws ResourceNotFoundException, AccessDeniedException, ResourceAlreadyExistsException, IllegalParameterValueException;

    String moveResource(String sourceUri, String destinationUri, boolean createFolders, boolean overwrite, String renameTo) throws ResourceNotFoundException, AccessDeniedException, ResourceAlreadyExistsException, IllegalParameterValueException;

    Resource createFileResource(InputStream stream, String parentUri, String name, String label, String description, String type, boolean createFolders, boolean dryRun) throws RemoteException;

    Resource updateFileResource(InputStream stream, String parentUri, String name, String label, String description, String type, boolean dryRun) throws RemoteException;

    String getUniqueName(String parenUri, String name) throws MandatoryParameterNotFoundException;

    ClientResource saveOrUpdate(ClientResource clientResource, boolean overwrite, boolean createFolders, String clientType, boolean dryRun) throws RemoteException;
}
