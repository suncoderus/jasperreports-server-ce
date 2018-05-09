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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

/**
 * This service stores and retrieves thumbnail records from the repository using Hibernate
 *
 * @author Grant Bacon <gbacon@jaspersoft.com>
 * @version
 */
public interface ReportThumbnailService {

    /**
     * Save report thumbnail in database relating it to the user and report it belongs to
     *
     * @param thumbnailStream
     * @param user
     * @param resource
     */
    public void saveReportThumbnail(ByteArrayOutputStream thumbnailStream, User user, Resource resource);

    /**
     * Obtain a persisted thumbnail
     *
     * @param user User who requested thumbnail
     * @param resource Client resource object of which to obtain a thumbnail
     * @return image data
     * @throws JSException
     */
    public ByteArrayInputStream getReportThumbnail(User user, Resource resource);


    /**
     * Obtain a persisted thumbnail
     *
     * @param user User who requested thumbnail
     * @param reportUri Location of the resource within the repository
     * @return image data
     * @throws JSException
     */
    public ByteArrayInputStream getReportThumbnail(User user, String reportUri);


    /**
     * Obtain the default thumbnail
     *
     * @return default thumbnail image data
     */
    public ByteArrayInputStream getDefaultThumbnail();

}
