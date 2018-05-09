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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addIllegalParameterValueError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addMandatoryParameterNotFoundError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class GenericResourceValidator<ResourceType extends Resource> implements ResourceValidator<ResourceType> {
    @javax.annotation.Resource
    private ProfileAttributesResolver profileAttributesResolver;

    @Override
    public void validate(ResourceType resource, boolean skipRepoFieldsValidation) throws JSValidationException {
        final ValidationErrorsImpl validationErrors = new ValidationErrorsImpl();
        if(!skipRepoFieldsValidation) {
            genericValidate(resource, validationErrors);
        }
        internalValidate(resource, validationErrors);
        if (validationErrors.isError()) {
            throw new JSValidationException(validationErrors);
        }
    }

    @Override
    public void validate(ResourceType resource) throws JSValidationException {
        validate(resource, false);
    }

    private void genericValidate(Resource resource, ValidationErrors errors) {
        if (empty(resource.getLabel())) {
            addMandatoryParameterNotFoundError(errors, "label");
        } else {
            if (resource.getLabel().length() > 100) {
                addIllegalParameterValueError(errors, "label", resource.getLabel(), "The label must not be longer than 100 characters");
            }
            if (profileAttributesResolver.containsAttribute(resource.getLabel())) {
                addIllegalParameterValueError(errors, "label", resource.getLabel(), "Attribute placeholder is not allowed");
            }
        }

        if (!empty(resource.getDescription())) {
            if (resource.getDescription().length() > 250) {
                addIllegalParameterValueError(errors, "description", resource.getLabel(), "The description must not be longer than 250 characters");
            }
            if (profileAttributesResolver.containsAttribute(resource.getDescription())) {
                addIllegalParameterValueError(errors, "description", resource.getLabel(), "Attribute placeholder is not allowed");
            }

        }

        if (!empty(resource.getName()) && profileAttributesResolver.containsAttribute(resource.getName())) {
            addIllegalParameterValueError(errors, "name", resource.getName(), "Attribute placeholder is not allowed");
        }
    }

    protected abstract void internalValidate(ResourceType resource, ValidationErrors errors);
}
