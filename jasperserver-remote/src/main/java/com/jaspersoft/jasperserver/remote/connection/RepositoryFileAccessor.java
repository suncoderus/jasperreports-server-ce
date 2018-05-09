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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class RepositoryFileAccessor implements FileAccessor<ClientReference> {
    @Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;

    @Override
    public InputStream openStream(ClientReference location) {
        FileResourceData resourceData = getFileResourceData(location);
        return resourceData != null ? resourceData.getDataStream() : null;
    }

    @Override
    public boolean exist(ClientReference location) {
        return getFileResourceData(location) != null;
    }

    protected FileResourceData getFileResourceData(ClientReference location) {
        FileResourceData resourceData = null;
        if (location != null && location.getUri() != null) {
            final String uri = location.getUri();
            try {
                resourceData = repositoryService.getResourceData(null, uri);
            } catch (JSResourceNotFoundException e) {
                resourceData = repositoryService.getContentResourceData(null, uri);
            }
        }
        return resourceData;
    }
}
