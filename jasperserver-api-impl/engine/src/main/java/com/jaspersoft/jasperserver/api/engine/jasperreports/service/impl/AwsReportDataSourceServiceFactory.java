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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsDataSourceRecovery;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author vsabadosh
 */
public class AwsReportDataSourceServiceFactory extends JdbcReportDataSourceServiceFactory {

    private AwsDataSourceRecovery awsDataSourceRecovery;

    @Override
    public ReportDataSourceService createService(ReportDataSource reportDataSource) {
        if (!(reportDataSource instanceof AwsReportDataSource)) {
            throw new JSException("jsexception.invalid.aws.datasource", new Object[] {reportDataSource.getClass()});
        }
        AwsReportDataSource awsDataSource = (AwsReportDataSource) reportDataSource;

        DataSource dataSource = getPoolDataSource(awsDataSource.getDriverClass(), awsDataSource.getConnectionUrl(),
                awsDataSource.getUsername(), awsDataSource.getPassword());

        return new AwsDataSourceService(dataSource, getTimeZoneByDataSourceTimeZone(awsDataSource.getTimezone()),
                awsDataSource, awsDataSourceRecovery);
    }

    public void setAwsDataSourceRecovery(AwsDataSourceRecovery awsDataSourceRecovery) {
        this.awsDataSourceRecovery = awsDataSourceRecovery;
    }

}
