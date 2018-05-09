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

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceValidator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import org.springframework.validation.Errors;

import java.util.Map;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class MongoDbDataSourceValidator implements CustomDataSourceValidator {
  @Override
  public void validatePropertyValues(CustomReportDataSource customReportDataSource, Errors errors) {
    Map<?, ?> propertyMap = customReportDataSource.getPropertyMap();
    String mongoURI = (String) propertyMap.get("mongoURI");
    if (mongoURI == null || mongoURI.length() == 0) {
      errors.rejectValue("reportDataSource.propertyMap[mongoURI]", "MongoDbDataSource.mongoURI.required");
    }
  }
}
