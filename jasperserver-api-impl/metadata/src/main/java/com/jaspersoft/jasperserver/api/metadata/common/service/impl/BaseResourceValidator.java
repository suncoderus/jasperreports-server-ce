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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.NullValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseResourceValidator {
	
	private RepositoryService repository;

	protected boolean validateNameString(String nameString, String nameField, ValidationErrors errors) {
        if (StringUtils.isEmpty(nameString))
        {
            errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.not.empty",
                    null, null, nameField));
            return false;
        }
        if (nameString.length() > 100)
        {
            errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.too.long",
                    new Object[]{new Integer(100)}, null, nameField));
            return false;
        }
        else if (!ValidationUtil.regExValidateName(nameString))
        {
            errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.invalid.chars",
                    null, null, nameField));
            return false;
        }
        return true;
    }

    protected void validateName(Resource resource, ValidationErrorFilter filter, ValidationErrors errors)
    {
		String nameField = getFieldPrefix() + "name";
		if (filter == null || filter.matchErrorField(nameField))
		{
            if(!validateNameString(resource.getName(), nameField, errors)) {
                return;
            }
            //need explicitly enabled filter to validate duplicate names
            if (!NullValidationErrorFilter.isNullFilter(filter) && filter.matchErrorCode(getErrorMessagePrefix() + "error.duplicate"))
            {
                if (getRepositoryService().repositoryPathExists(null, resource.getURIString()))
                {
                    errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.duplicate",
                            null, null, nameField));
                }
            }
        }
	}
	
	protected void validateLabel(Resource resource, ValidationErrorFilter filter, ValidationErrors errors)
	{
		String labelField = getFieldPrefix() + "label";
		if (filter == null || filter.matchErrorField(labelField))
		{
			if (resource.getLabel() == null || resource.getLabel().trim().length() == 0) 
			{
				errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.not.empty", 
						null, null, labelField));
			}
			else 
			{
				if (resource.getLabel().length() > 100) 
				{
					errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.too.long", 
							new Object[]{new Integer(100)}, null, labelField));
				}
			}
		}
	}
	
	protected void validateDescription(Resource resource, ValidationErrorFilter filter, ValidationErrors errors)
	{
		String descriptionField = getFieldPrefix() + "description";
		if (filter == null || filter.matchErrorField(descriptionField))
		{
			if (resource.getDescription() != null && resource.getDescription().length() > 250)
			{
				errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.too.long", 
						new Object[]{new Integer(250)}, null, descriptionField));
			}
		}
	}
	
	protected abstract String getErrorMessagePrefix();
	
	protected abstract String getFieldPrefix();

	public RepositoryService getRepositoryService() {
		return repository;
	}

	public void setRepositoryService(RepositoryService repository) {
		this.repository = repository;
	}

	
}
