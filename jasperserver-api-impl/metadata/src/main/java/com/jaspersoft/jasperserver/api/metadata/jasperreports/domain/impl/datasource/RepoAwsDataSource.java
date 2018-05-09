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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;

/**
 * @author vsabadosh
 */
public class RepoAwsDataSource extends RepoJdbcDataSource {

    private String accessKey;
    private String secretKey;
    private String roleARN;
    private String region;
    private String dbName;
    private String dbInstanceIdentifier;
    private String dbService;

    public RepoAwsDataSource() {

    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRoleARN() {
        return roleARN;
    }

    public String getRegion() {
        return region;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setRoleARN(String roleARN) {
        this.roleARN = roleARN;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbInstanceIdentifier() {
        return dbInstanceIdentifier;
    }

    public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
        this.dbInstanceIdentifier = dbInstanceIdentifier;
    }

    public String getDbService() {
        return dbService;
    }

    public void setDbService(String dbService) {
        this.dbService = dbService;
    }

    protected Class getClientItf() {
        return AwsReportDataSource.class;
    }

    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

        AwsReportDataSource ds = (AwsReportDataSource) clientRes;
        ds.setAWSAccessKey(PasswordCipherer.getInstance().decodePassword(getAccessKey()));
        ds.setAWSSecretKey(PasswordCipherer.getInstance().decodePassword(getSecretKey()));
        ds.setRoleARN(getRoleARN());
        ds.setAWSRegion(getRegion());
        ds.setDbName(getDbName());
        ds.setDbService(getDbService());
        ds.setDbInstanceIdentifier(getDbInstanceIdentifier());
    }

    protected void copyFrom(Resource clientRes,
                            ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);
        AwsReportDataSource ds = (AwsReportDataSource) clientRes;
        setAccessKey(PasswordCipherer.getInstance().encodePassword(ds.getAWSAccessKey()));
        setSecretKey(PasswordCipherer.getInstance().encodePassword(ds.getAWSSecretKey()));
        setRoleARN(ds.getRoleARN());
        setRegion(ds.getAWSRegion());
        setDbName(ds.getDbName());
        setDbService(ds.getDbService());
        setDbInstanceIdentifier(ds.getDbInstanceIdentifier());
    }

}
