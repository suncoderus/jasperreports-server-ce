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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileProvider;

/**
 * Implementation of Apache VFS FileProvider to access the JasperServer repository.
 * Deals with "internal" URIs that do not need to be transformed for multi-tenancy
 *
 * @author swood
 */
public class RepositoryFileProvider extends AbstractFileProvider {

    public static final String REPOSITORY_SCHEME = Resource.URI_PROTOCOL;

    protected final static Collection capabilities = Collections.unmodifiableCollection(Arrays.asList(new Capability[]
    {
        Capability.READ_CONTENT,
        Capability.URI
    }));

    public RepositoryFileProvider() {
        super();
        setFileNameParser(new RepositoryFileNameParser(getScheme()));
    }

    public FileObject findFile(final FileObject baseFile,
                                            final String uri,
                                            final FileSystemOptions fileSystemOptions)
        throws FileSystemException {

        final FileName rootName = new RepositoryFileName(getScheme(), null, FileType.FOLDER);
        FileSystem fs = findFileSystem(rootName.getRoot(), fileSystemOptions);

        if (fs == null) {
            fs = new RepositoryFileSystem(getScheme(), rootName, fileSystemOptions);
            addFileSystem(rootName.getRoot(), fs);
        }

        FileObject result = fs.resolveFile(uri);
        return result;
    }

    public FileSystemConfigBuilder getConfigBuilder() {
        return org.apache.commons.vfs.provider.res.ResourceFileSystemConfigBuilder.getInstance();
    }

    public Collection getCapabilities() {
        return capabilities;
    }

    public String getScheme() {
        return REPOSITORY_SCHEME;
    }

}
