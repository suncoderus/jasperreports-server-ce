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

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.jaspersoft.jasperserver.war.dto.DataTypeWrapper;
import com.jaspersoft.jasperserver.war.dto.QueryWrapper;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class QueryValidator implements Validator
{
	private RepositoryService repository;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class klass)
	{
		return QueryWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{
		QueryWrapper wrapper = (QueryWrapper) object;
		validateNameLabelDesc(wrapper, errors);
		validateQueryText(wrapper, errors);
	}

	public void validateNameLabelDesc(QueryWrapper wrapper, Errors errors)
	{
		Query query = wrapper.getQuery();
		if (query.getName() == null || query.getName().trim().length() == 0) {
			errors.rejectValue("query.name", "QueryValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(query.getName())) {
				errors.rejectValue("query.name", "QueryValidator.error.invalid.chars");
			}
			if (query.getName().length() > 100) {
				errors.rejectValue("query.name", "QueryValidator.error.too.long");
			}

			if (wrapper.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, query.getURIString())) {
					errors.rejectValue("query.name", "QueryValidator.error.duplicate");
				}
			}
		}

		if (query.getLabel() == null || query.getLabel().trim().length() == 0) {
			errors.rejectValue("query.label", "QueryValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(query.getLabel())) {
				errors.rejectValue("query.label", "QueryValidator.error.invalid.chars");
			}
			if (query.getLabel().length() > 100) {
				errors.rejectValue("query.label", "QueryValidator.error.too.long");
			}
		}

		if (query.getDescription() != null && query.getDescription().length() > 250) {
			errors.rejectValue("query.description", "QueryValidator.error.too.long");
		}
	}

	public void validateQueryText(QueryWrapper wrapper, Errors errors)
	{
		Query query = wrapper.getQuery();

		if (query.getSql() == null || query.getSql().trim().length() == 0) {
			errors.rejectValue("query.sql", "QueryValidator.error.not.empty");
		}
	}
}
