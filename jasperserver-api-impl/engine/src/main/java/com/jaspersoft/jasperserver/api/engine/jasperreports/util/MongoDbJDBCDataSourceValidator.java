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
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.springframework.validation.Errors;

import java.io.File;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ichan
 * Date: 2/12/14
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MongoDbJDBCDataSourceValidator implements CustomDataSourceValidator {
    private ProfileAttributesResolver profileAttributesResolver;

	public void validatePropertyValues(CustomReportDataSource ds, Errors errors) {
		String filePath = null;
        String portNumber = null;

		Map props = ds.getPropertyMap();
		if (props != null) {
			filePath = (String) ds.getPropertyMap().get("fileName");
            portNumber = (String) ds.getPropertyMap().get("portNumber");
		}
		if (filePath == null || filePath.length() == 0) {
            // file path can be null if data file resources is linked to the data source or auto generated
		} else {
            if (filePath.toLowerCase().startsWith("ftp:")) {
                int idx1 = filePath.indexOf(":", 6);
                int idx2 = filePath.indexOf("@", 6);
                int idx3 = filePath.indexOf("/", idx1);
                if ((idx1 <= 0) || (idx2 <= 0) || (idx3 <= 0) || (idx3 == (idx2 + 1))) {
                    // Please follow FTP syntax for FTP path: ftp://[USERNAME]:[PASSWORD]@[HOST]:[PORT]/[PATH]/[FILENAME]
                    reject(errors, "fileName", "invalid.ftp.syntax");
                }
            } else if  (filePath.toLowerCase().startsWith("repo:")) {
            } else if  (filePath.toLowerCase().startsWith("http:")) {
            } else if  (filePath.toLowerCase().startsWith("https:")) {
            } else {
                try {
                    File f = new File(filePath);
                    if (!(f.exists() && !f.isDirectory())) {
                        reject(errors, "fileName", "invalid.server.file.system.path");
                    }
                } catch (Exception ex) {
                    reject(errors, "fileName", "invalid.server.file.system.path");
                }
            }
        }
        if (!profileAttributesResolver.containsAttribute(portNumber)) {
            try {
                Integer.parseInt(portNumber);
            } catch (Exception ex) {
                reject(errors, "portNumber", "invalid.port.number");
            }
        }
	}

	// first arg is the path of the property which has the error
	// for custom DS's this will always be in the form "reportDataSource.propertyMap[yourPropName]"
	protected void reject(Errors errors, String name, String reason) {
		if (errors != null) errors.rejectValue("textDataSource.propertyMap[" + name + "]",  reason);
        else System.out.println("textDataSource.propertyMap[" + name + "] - " + reason);
	}

    public void setProfileAttributesResolver(ProfileAttributesResolver profileAttributesResolver) {
        this.profileAttributesResolver = profileAttributesResolver;
    }
}
