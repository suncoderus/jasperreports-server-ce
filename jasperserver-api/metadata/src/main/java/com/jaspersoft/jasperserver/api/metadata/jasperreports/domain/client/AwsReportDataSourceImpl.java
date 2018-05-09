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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;

/**
 * @author vsabadosh
 */
public class AwsReportDataSourceImpl extends JdbcReportDataSourceImpl implements AwsReportDataSource {

    private String accessKey;
    private String secretKey;
    private String roleARN;
    private String region;
    private String dbName;
    private String dbInstanceIdentifier;
    private String dbService;

    public String getAWSAccessKey() {
        return accessKey;
    }

    public String getAWSSecretKey() {
        return secretKey;
    }

    public String getRoleARN() {
        return roleARN;
    }

    public String getAWSRegion() {
        return region;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbInstanceIdentifier() {
        return dbInstanceIdentifier;
    }

    public String getDbService() {
        return dbService;
    }

    public void setAWSAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setAWSSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setRoleARN(String roleARN) {
        this.roleARN = roleARN;
    }

    public void setAWSRegion(String region) {
       this.region = region;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
        this.dbInstanceIdentifier = dbInstanceIdentifier;
    }

    public void setDbService(String dbService) {
        this.dbService = dbService;
    }

    protected Class getImplementingItf() {
        return AwsReportDataSource.class;
    }

}
