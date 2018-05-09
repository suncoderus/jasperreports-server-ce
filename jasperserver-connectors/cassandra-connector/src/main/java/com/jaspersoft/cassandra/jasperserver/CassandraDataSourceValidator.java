/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.cassandra.jasperserver;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceValidator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import org.springframework.validation.Errors;

import java.util.Map;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class CassandraDataSourceValidator implements CustomDataSourceValidator {

	@Override
	public void validatePropertyValues(CustomReportDataSource customReportDataSource, Errors errors) {
		Map<?, ?> propertyMap = customReportDataSource.getPropertyMap();
//		if (jdbcURL == null || jdbcURL.length() == 0) {
//			errors.rejectValue("reportDataSource.propertyMap[jdbcURL]", "HiveDataSource.jdbcURL.required");
//		}
	}
}
