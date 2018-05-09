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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;


import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import org.springframework.validation.Errors;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ichan
 * Date: 2/12/14
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextDataSourceValidator extends XlsDataSourceValidator {

	public void validatePropertyValues(CustomReportDataSource ds, Errors errors) {
        super.validatePropertyValues(ds, errors);
		String fieldDelimiter = null;
		Map props = ds.getPropertyMap();
		if (props != null) {
			fieldDelimiter = (String) ds.getPropertyMap().get("fieldDelimiter");
		}

        if (fieldDelimiter == null || fieldDelimiter.length() == 0) {
            reject(errors, "fieldDelimiter", "Please enter field delimiter");
        } else if (fieldDelimiter.length() > 1) {
            reject(errors, "fieldDelimiter", "Invalid field delimiter:  field delimiter cannot have more than 1 character");
        }
	}

}
