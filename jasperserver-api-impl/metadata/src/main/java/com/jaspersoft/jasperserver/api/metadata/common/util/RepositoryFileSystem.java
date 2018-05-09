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
package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.Collection;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileSystem;

/**
 * Implementation of Apache VFS FileSystem to access the JasperServer repository.
 * This implementation expects "internal" URIs that do not have to be transformed
 * for multi-tenancy.
 *
 * @author swood
 */
public class RepositoryFileSystem extends AbstractFileSystem implements FileSystem {
    private String scheme;

    protected RepositoryFileSystem(final String scheme, final FileName rootName, final FileSystemOptions fileSystemOptions) {
        super(rootName, null, fileSystemOptions);
        this.scheme = scheme;
    }

    /**
     * Finds a file in this file system.
     *
     * @param nameStr URI
     * @return VFS FileObject
     * @throws FileSystemException
     */
    @Override
    public FileObject resolveFile(final String nameStr) throws FileSystemException {
        // Resolve the name, and create the file
        final FileName workingFileName = new RepositoryFileName(scheme, nameStr, FileType.FILE);
        return resolveFile(workingFileName);
    }

    /**
     * Creates a VFS file object.
     *
     * @param name VFS FileName
     * @return VFS FileObject
     */
    protected FileObject createFile(final FileName name) {
        return new RepositoryFileObject(this, scheme, name);
    }

    /**
     * Returns the capabilities of this file system.
     * @param caps VFS capabilities collection
     */
    protected void addCapabilities(final Collection caps) {
        caps.addAll(RepositoryFileProvider.capabilities);
    }
}
