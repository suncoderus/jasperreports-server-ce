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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.util.InternalRepositoryFileProvider;
import mondrian.spi.CatalogLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Mondrian support: define the CatalogLocator URI for the JasperServer repository
 * used Apache VFS
 *
 * @author swood
 */
public class RepositoryCatalogLocator implements CatalogLocator {
    private static final Log log = LogFactory.getLog(RepositoryCatalogLocator.class);
    
    public static final String SCHEME_DELIMITER = ":";
    public static final String SCHEME_URL_SEPARATOR = 
                            SCHEME_DELIMITER + Folder.SEPARATOR;

    private String scheme;

    public RepositoryCatalogLocator() {
        this(InternalRepositoryFileProvider.INTERNAL_REPOSITORY_SCHEME);
    }

    public RepositoryCatalogLocator(String scheme) {
        this.scheme = scheme;
    }

    public String locate(String catalogName) {

        if (catalogName == null || catalogName.trim().length() == 0) {
            throw new JSException("No catalog name given for RepositoryCatalogLocator");
        }

        catalogName = catalogName.trim();

        log.debug("Got: " + catalogName);

        /*
         * Can get:
         *      "not my scheme":/abc
         *      abc
         *      /abc
         *      scheme:/abc
         * want to return:
         *      scheme:/abc
         */
        if (catalogName.startsWith(scheme + SCHEME_URL_SEPARATOR)) {
            return catalogName;
        }

        int schemeSeparatorPos = catalogName.indexOf(SCHEME_DELIMITER);


        if (schemeSeparatorPos >= 0) {
            // rip off the existing scheme
            catalogName = catalogName.substring(schemeSeparatorPos + 1);
        }
        if (!catalogName.startsWith(Folder.SEPARATOR)) {
            catalogName = Folder.SEPARATOR + catalogName;
        }
        catalogName = scheme + SCHEME_DELIMITER + catalogName;

        log.debug("returned: " + catalogName);

        return catalogName;
    }
}
