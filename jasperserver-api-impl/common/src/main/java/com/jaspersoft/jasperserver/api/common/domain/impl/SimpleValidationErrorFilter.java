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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ValidationErrorsImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class SimpleValidationErrorFilter implements ValidationErrorFilter, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Set codesToInclude;
	private final Set codesToExclude;
	private final Set fieldsToInclude;
	private final Set fieldsToExclude;
	
	public SimpleValidationErrorFilter() 
	{
		codesToInclude = new HashSet();
		codesToExclude = new HashSet();
		fieldsToInclude = new HashSet();
		fieldsToExclude = new HashSet();
	}

	public boolean matchError(ValidationError error) 
	{
		return matchErrorCode(error.getErrorCode());
	}

	public boolean matchErrorCode(String code) 
	{
		return 
			(codesToInclude.isEmpty() || codesToInclude.contains(code))
			&& (codesToExclude.isEmpty() || !codesToExclude.contains(code));
			
	}

	public boolean matchErrorField(String field) 
	{
		return 
			(fieldsToInclude.isEmpty() || fieldsToInclude.contains(field))
			&& (codesToExclude.isEmpty() || !fieldsToExclude.contains(field));
			
	}

	public void addErrorCodeToInclude(String code) 
	{
		codesToInclude.add(code);
		codesToExclude.remove(code);
	}
	
	public void addErrorCodeToExclude(String code) 
	{
		codesToExclude.add(code);
		codesToInclude.remove(code);
	}
	
	public void addErrorFieldToInclude(String field) 
	{
		fieldsToInclude.add(field);
		fieldsToExclude.remove(field);
	}
	
	public void addErrorFieldToExclude(String field) 
	{
		fieldsToExclude.add(field);
		fieldsToInclude.remove(field);
	}
	
}
