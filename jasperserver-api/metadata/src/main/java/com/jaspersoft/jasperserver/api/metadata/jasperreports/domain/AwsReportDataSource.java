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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * Represents a persistent JasperServer repository data source which corresponds with a
 * Amazon data source which uses JDBC.
 * @author vsabadosh
 */
@JasperServerAPI
public interface AwsReportDataSource extends JdbcReportDataSource {

    String getAWSAccessKey();
    
    String getAWSSecretKey();
    
    String getRoleARN();
    
    String getAWSRegion();

    String getDbName();
    
    String getDbInstanceIdentifier();
    
    String getDbService();

    void setAWSAccessKey(String accessKey);

    void setAWSSecretKey(String secretKey);

    void setRoleARN(String roleARN);

    void setAWSRegion(String region);

    void setDbName(String dbName);
    
    void setDbInstanceIdentifier(String dbInstanceIdentifier);
    
    void setDbService(String dbService);
}
