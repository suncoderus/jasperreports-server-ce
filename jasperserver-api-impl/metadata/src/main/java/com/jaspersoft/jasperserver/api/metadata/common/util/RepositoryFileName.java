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
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileName;

/**
 * Implementation of Apache VFS FileName to access the JasperServer repository
 *
 * @author swood
 */
public class RepositoryFileName extends AbstractFileName {

    protected RepositoryFileName(final String scheme, final String path, final FileType type) {
        /*
         * We could get "repo:/a/resource", "/a/resource", "a/resource"
         *
         * Want to put in "/a/resource" as the path
         */
        //super(scheme, path, type);
        super(scheme,
                   (path == null)
                        ? "/"           // the root
                        : (
                            (path.startsWith("/")
                            ? path      // "/a/resource"
                            : (
                                (path.startsWith(scheme + ":/")  // "repo:/a/resource"
                                ? path.substring(scheme.length() + 1)
                                : "/" + path    // "a/resource"
                              )
                          )
                           )),
                   type);
    }

    /**
     * Factory method for creating name instances.
     *
     * @param path 
     * @param type
     * @return
     */
    public FileName createName(final String path, FileType type) {
        return new RepositoryFileName(getScheme(), path, type);
    }

    /**
     * Builds the root URI for this file name.
     * @param buffer 
     * @param addPassword
     */
    protected void appendRootUri(final StringBuffer buffer, boolean addPassword) {
        buffer.append(getScheme());
        buffer.append(":");
    }
}
