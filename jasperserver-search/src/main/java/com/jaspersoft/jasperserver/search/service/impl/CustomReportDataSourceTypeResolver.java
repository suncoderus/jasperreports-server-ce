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
package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.search.service.ResourceTypeResolver;

/**
 * Created by stas on 3/7/14.
 */
public class CustomReportDataSourceTypeResolver implements ResourceTypeResolver {

    @javax.annotation.Resource
    protected RepositoryService repositoryService;

    @Override
    public String getResourceType(Resource resource) {
        if (resource == null) { return null; }

        if (resource instanceof CustomReportDataSource) {
            return getResourceType((CustomReportDataSource) resource);

        } else if (resource.getResourceType().equals(CustomReportDataSource.class.getCanonicalName())) {
            return getResourceType(resource.getURIString());

        } else {
            return resource.getResourceType();
        }
    }

    private String getResourceType(String uri) {
        if (uri == null) { return null; }

        CustomReportDataSource dataSource = (CustomReportDataSource) repositoryService.getResource(null, uri);
        return (dataSource == null) ? null : getResourceType(dataSource);
    }

    protected String getResourceType(CustomReportDataSource dataSource) {
        return dataSource.getDataSourceName();
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
}
