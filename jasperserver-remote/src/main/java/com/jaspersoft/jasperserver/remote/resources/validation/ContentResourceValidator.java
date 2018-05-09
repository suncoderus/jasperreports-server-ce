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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addIllegalParameterValueError;

/**
 * @author serhii.blazhyievskyi
 * @version $Id$
 */
@Component
public class ContentResourceValidator extends GenericResourceValidator<ContentResource> {
    @Resource(name = "repositoryService")
    private RepositoryService repository;
    @Resource(name = "repositoryServiceSecurityChecker")
    private RepositorySecurityChecker repositoryServiceSecurityChecker;
    @Value("#{configurationBean.maxFileSize}")
    private Long maxFileSize;


    public RepositoryService getRepository()
    {
        return repository;
    }

    public void setRepository(RepositoryService repository)
    {
        this.repository = repository;
    }

    public void setRepositoryServiceSecurityChecker(RepositorySecurityChecker repositoryServiceSecurityChecker) {
        this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    protected void internalValidate(ContentResource resource, ValidationErrors errors) {
        validateNaming(resource, errors);
        validateUpload(resource, errors);
    }

    private void validateNaming(ContentResource resource, ValidationErrors errors) {
        if (resource.getLabel() == null
                || resource.getLabel().trim().length() == 0) {
            addIllegalParameterValueError(errors, "label", "", "Enter a name for this resource");
        } else {
            if (resource.getLabel().length() > 100) {
                addIllegalParameterValueError(errors, "label", "",
                        "The resource label is too long. The maximum length is 100 characters.");
            } else if (!JasperServerUtil.regExValidateLabel(resource.getLabel())) {
                addIllegalParameterValueError(errors, "label", "",
                        "The resource label has invalid characters. You might have mistyped it.");
            }
        }

        if (resource.getDescription() != null && resource.getDescription().length() > 250)
            addIllegalParameterValueError(errors, "description", "",
                    "The description is too long. The maximum length is 250 characters.");
    }

    private void validateUpload(ContentResource resource, ValidationErrors errors) {
        if (maxFileSize >= 0 && resource.getData() != null && resource.getData().length > maxFileSize){
            addIllegalParameterValueError(errors, "data", "", "The file must be smaller than " + maxFileSize + " bytes");
        }
    }
}
