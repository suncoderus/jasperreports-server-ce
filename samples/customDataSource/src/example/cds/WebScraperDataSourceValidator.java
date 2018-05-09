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

package example.cds;

import java.util.Map;

import org.springframework.validation.Errors;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceValidator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

/**
 * @author bob
 *
 */
public class WebScraperDataSourceValidator implements CustomDataSourceValidator {

	/* check the values in the map; call rejectValue if tests don't pass
	 */
	public void validatePropertyValues(CustomReportDataSource ds, Errors errors) {
		String url = null;
		String path = null;
		Map props = ds.getPropertyMap();
		if (props != null) {
			path = (String) ds.getPropertyMap().get("path");
			url = (String) ds.getPropertyMap().get("url");
		}
		if (url == null || url.length() == 0) {
			reject(errors, "url");
		}
		if (path == null || path.length() == 0) {
			reject(errors, "path");
		}
	}

	// first arg is the path of the property which has the error
	// for custom DS's this will always be in the form "reportDataSource.propertyMap[yourPropName]"
	private void reject(Errors errors, String name) {
		errors.rejectValue("reportDataSource.propertyMap[" + name + "]", "webScraperDataSource." + name + ".required");
	}
}
