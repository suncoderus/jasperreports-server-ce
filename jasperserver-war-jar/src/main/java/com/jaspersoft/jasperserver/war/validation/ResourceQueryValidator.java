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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.dto.ResourceReferenceDTO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ResourceQueryValidator implements Validator {

    private RepositoryService repository;

    private RepositorySecurityChecker repositoryServiceSecurityChecker;

    public RepositoryService getRepository() {
        return repository;
    }

    public void setRepository(RepositoryService repository) {
        this.repository = repository;
    }

    public RepositorySecurityChecker getRepositoryServiceSecurityChecker() {
        return repositoryServiceSecurityChecker;
    }

    public void setRepositoryServiceSecurityChecker(RepositorySecurityChecker repositoryServiceSecurityChecker) {
        this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
    }

    public boolean supports(Class clazz) {
        return ResourceReferenceDTO.class.isAssignableFrom(clazz);
    }

    public void validate(Object o, Errors errors) {
        // TODO Auto-generated method stub
    }

    public void validateQueryExists(ResourceReferenceDTO dto, Errors errors) {
        if (dto.getSource() != null && dto.getSource().equals(JasperServerConstImpl.getFieldChoiceRepo())
                && (dto.getReferenceURI() == null || dto.getReferenceURI().trim().length() == 0 ||
                    !repository.resourceExists(null, dto.getReferenceURI()))) {
            errors.rejectValue("referenceURI", "ResourceQueryValidator.error.no");
        } else if (dto.getSource() != null && dto.getSource().equals(JasperServerConstImpl.getFieldChoiceLocal())) {
            if (dto.getLocalResource() == null || dto.getLocalResource().getName() == null || ((Query)dto.getLocalResource()).getSql() == null) {
                errors.rejectValue("source", "ResourceQueryValidator.error.no");
            }
        }
    }

}
