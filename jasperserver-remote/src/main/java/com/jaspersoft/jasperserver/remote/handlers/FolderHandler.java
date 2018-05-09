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
package com.jaspersoft.jasperserver.remote.handlers;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.UniversalValidationErrorFilter;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id$
 */
@Service
public class FolderHandler extends AbstractResourceHandler {

    private static final Log log = LogFactory.getLog(FolderHandler.class);

    public Class getResourceType() {
        return Folder.class;
    }

    @Override
    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {
        descriptor.setWsType(ResourceDescriptor.TYPE_FOLDER);
        descriptor.setHasData(false);
    }

    @Override
    public SearchFilter getSearchFilter(String uri, String queryString, String wsType, boolean recursive, int maxItems, int startIndex) {
        return new SearchFilter() {
            @Override
            public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
                criteria.add(Restrictions.eq("hidden", false));
            }
        };
    }

    protected void validate(RepositoryService repository, Folder folder) throws ServiceException
    {
            ValidationErrorFilter filter = folder.isNew()? UniversalValidationErrorFilter.getInstance() : null;
            ValidationErrors errors = repository.validateFolder(null, folder, filter);

            if (errors.isError()) {
                throw new ServiceException(ServiceException.FORBIDDEN, errors.toString());
            }

    }


}
