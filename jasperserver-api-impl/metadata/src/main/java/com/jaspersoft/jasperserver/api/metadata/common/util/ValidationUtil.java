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
package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.validation.Errors;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JasperServerUtil.java 9052 2007-07-11 02:10:37Z melih $
 */
public class ValidationUtil 
{

	private static final Pattern PATTERN_NAME = Pattern.compile("(\\p{L}|\\p{N}|(\\_)|(\\.)|(\\-)|[;@])+");

	/*
	 * function to validate name
	 * allows only valid word characters and doesn't allow
	 * any space or any special characters for this field
	 * arguments string
	 * returns boolean
	 */
	public static boolean regExValidateName(String inp) throws PatternSyntaxException 
	{
		Matcher mat = PATTERN_NAME.matcher(inp);
		return mat.matches();
	}

	/**
	 * 
	 */
	public static void copyErrors(ValidationErrors errors, Errors uiErrors) 
	{
		if (errors != null && uiErrors != null)
		{
			for(Iterator it = errors.getErrors().iterator(); it.hasNext();)
			{
				ValidationError error = (ValidationError)it.next();
				uiErrors.rejectValue(error.getField(), error.getErrorCode(),
						error.getErrorArguments(), error.getDefaultMessage());
			}
		}
	}

}
