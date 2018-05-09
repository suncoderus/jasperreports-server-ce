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
package com.jaspersoft.jasperserver.war.validation;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.MondrianXmlaSourceWrapper;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;


/**
 * 
 * MondrianXmlaSourceValidator provides validation methods for the
 * mondrianXmlaSourceFlow
 *
 * @author jshih
 */
public class MondrianXmlaSourceValidator implements Validator
{
	private RepositoryService repository;
    private TenantService tenantService;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class klass) {
		return MondrianXmlaSourceWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object o, Errors errors) {
		MondrianXmlaSourceWrapper details = (MondrianXmlaSourceWrapper) o;
		validateNameLabelDesc(details, errors);
	}

	public void validateNameLabelDesc(MondrianXmlaSourceWrapper wrapper, Errors errors) {
		MondrianXMLADefinition mondrianXmlaDefinition =
			wrapper.getMondrianXmlaDefinition();
		if (mondrianXmlaDefinition.getName() == null ||
			mondrianXmlaDefinition.getName().trim().length() == 0) {
			errors.rejectValue("mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(mondrianXmlaDefinition.getName())) {
				errors.rejectValue(
						"mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.invalid.chars");
			}
			if (mondrianXmlaDefinition.getName().length() > 100) {
				errors.rejectValue(
						"mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.too.long");
			}

			if (wrapper.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, mondrianXmlaDefinition.getURIString())) {
					errors.rejectValue("mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.duplicate");
				}
			}
		}

		if (mondrianXmlaDefinition.getLabel() == null ||
			mondrianXmlaDefinition.getLabel().trim().length() == 0) {
			errors.rejectValue("mondrianXmlaDefinition.label", "MondrianXmlaSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(mondrianXmlaDefinition.getLabel())) {
				errors.rejectValue(
						"mondrianXmlaDefinition.label", "MondrianXmlaSourceValidator.error.invalid.chars");
			}
			if (mondrianXmlaDefinition.getLabel().length() > 100) {
				errors.rejectValue(
						"mondrianXmlaDefinition.label", "MondrianXmlaSourceValidator.error.too.long");
			}
		}

		if (mondrianXmlaDefinition.getDescription() != null &&
			mondrianXmlaDefinition.getDescription().length() > 100) {
			errors.rejectValue(
					"mondrianXmlaDefinition.description", "MondrianXmlaSourceValidator.error.too.long");
		}

		if (mondrianXmlaDefinition.getCatalog() == null ||
			mondrianXmlaDefinition.getCatalog().trim().length() == 0) {
			errors.rejectValue("mondrianXmlaDefinition.catalog", "MondrianXmlaSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(mondrianXmlaDefinition.getCatalog())) {
				errors.rejectValue(
						"mondrianXmlaDefinition.catalog", "MondrianXmlaSourceValidator.error.invalid.chars");
			}
			if (!validateUniqueCatalog(mondrianXmlaDefinition.getCatalog(),
						   mondrianXmlaDefinition.getURIString())) {
			    errors.rejectValue("mondrianXmlaDefinition.catalog", 
					       "MondrianXmlaSourceValidator.error.catalog.exists");
			}
		}
	}

    private boolean validateUniqueCatalog(String catalog, String uri) {
        Tenant tenant = tenantService.getTenantBasedOnRepositoryUri(null, uri);
        String tenantFolder = null;
        RepositoryService rep = getRepository();
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
        ResourceLookup[] lookups = rep.findResource(null, criteria);
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
            Resource res = rep.getResource(null, lookups[i].getURIString());
            MondrianXMLADefinition def = (MondrianXMLADefinition) res;
            if (def.getCatalog().toLowerCase().equals(catalog)
                    && !def.getURIString().equals(uri)) {
                return false;
            }
        }
        return true;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void validateResourceExists(MondrianXmlaSourceWrapper wrapper, Errors errors){      
        if(wrapper.getConnectionUri() == null || wrapper.getConnectionUri().trim().length()==0 ||
            !repository.resourceExists(null, wrapper.getConnectionUri())) {
            errors.rejectValue("connectionUri", "MondrianXmlaSourceValidator.error.not.empty");
        }
	}
    
}
