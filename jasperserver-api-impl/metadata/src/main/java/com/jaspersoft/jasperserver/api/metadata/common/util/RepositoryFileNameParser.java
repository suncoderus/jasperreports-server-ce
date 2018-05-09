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

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileNameParser;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.provider.VfsComponentContext;

/**
 * Implementation of Apache VFS FileNameParser to access the JasperServer repository
 *
 * @author swood
 */
public class RepositoryFileNameParser extends AbstractFileNameParser {

    private String scheme;

    public RepositoryFileNameParser(String scheme) {
        this.scheme = scheme;
    }

    public FileName parseUri(final VfsComponentContext context, final FileName base, final String filename) throws FileSystemException {
        final StringBuffer name = new StringBuffer();

        // Extract the scheme
        final String foundScheme = UriParser.extractScheme(filename, name);

        if (!scheme.equals(foundScheme)) {
            throw new FileSystemException("invalid scheme: " + foundScheme);
        }

        // Decode and normalise the path
        UriParser.canonicalizePath(name, 0, name.length(), this);
        UriParser.fixSeparators(name);
        FileType fileType = UriParser.normalisePath(name);
        final String path = name.toString();

        return new RepositoryFileName(scheme, path, fileType);
    }

}
