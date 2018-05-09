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
package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class MondrianXmlaDefinitionValidator extends GenericResourceValidator<MondrianXMLADefinition> {
    @javax.annotation.Resource(name = "concreteRepository")
    private RepositoryService repositoryService;
    @javax.annotation.Resource(name = "concreteTenantService")
    private TenantService tenantService;

    @Override
    protected void internalValidate(MondrianXMLADefinition resource, ValidationErrors errors) {
        if (!empty(resource.getCatalog())){
            validateUniqueCatalog(resource.getCatalog(), resource.getURIString(), errors);
        } else {
            addMandatoryParameterNotFoundError(errors, "catalog");
        }

        if (empty(resource.getMondrianConnection())){
            addMandatoryParameterNotFoundError(errors, "mondrianConnection");
        }
    }

    private void validateUniqueCatalog(String catalog, String uri, ValidationErrors errors) {
        Tenant tenant = tenantService.getTenantBasedOnRepositoryUri(null, uri);
        String tenantFolder = null;
        FilterCriteria criteria = FilterCriteria.createFilter(MondrianXMLADefinition.class);
        // does it belong to any org template?
        int templIndex = uri.indexOf(TenantService.ORG_TEMPLATE);
        if (templIndex >= 0) {
            // filter out all definitions from this template
            criteria.addFilterElement(FilterCriteria.createAncestorFolderFilter(
                    uri.substring(0, templIndex + TenantService.ORG_TEMPLATE.length())));
        } else {
            // does it belong to any organization?
            if (tenant != null) {
                // filter out all definition from this organization
                // note that it will include definitions fro sub organizations as well,
                // which we will filter out later
                tenantFolder = tenant.getTenantFolderUri();
                criteria.addFilterElement(FilterCriteria.createAncestorFolderFilter(tenantFolder));
            }
        }
        ResourceLookup[] lookups = repositoryService.findResource(null, criteria);
        catalog = catalog.toLowerCase();

        for (int i = 0; i < lookups.length; i++) {
            // if it is not a template, we need to skip all sub organization resources
            // from the validation scope
            if (templIndex < 0) {
                String uriTail = lookups[i].getURIString();
                if (tenantFolder != null) {
                    uriTail = uriTail.substring(tenantFolder.length());
                }
                // now, if uriTail contains "organizations", it means we got sub org resource
                if (uriTail.indexOf(TenantService.ORGANIZATIONS) >= 0) {
                    continue;
                }
            }
            Resource res = repositoryService.getResource(null, lookups[i].getURIString());
            MondrianXMLADefinition def = (MondrianXMLADefinition) res;
            if (def.getCatalog().toLowerCase().equals(catalog)
                    && !def.getURIString().equals(uri)) {
                addIllegalParameterValueError(errors,
                        ResourceMediaType.MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE + ".catalog",
                        catalog, "Another XML/A source already uses that catalog name");
            }
        }
    }
}
