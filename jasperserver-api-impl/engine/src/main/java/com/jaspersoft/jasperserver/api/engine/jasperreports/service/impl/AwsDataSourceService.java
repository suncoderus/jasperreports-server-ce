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

import com.jaspersoft.jasperserver.api.JSAwsDataSourceRecoveryException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsDataSourceRecovery;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.sql.DataSource;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;
import java.net.ConnectException;

/**
 * @author vsabadosh
 */
public class AwsDataSourceService extends JdbcDataSourceService {

    private static final Log log = LogFactory
            .getLog(AwsDataSourceService.class);
    //SQL State that starts with "08" means problem with  connection
    //see http://publib.boulder.ibm.com/infocenter/idshelp/v111/index.jsp?topic=/com.ibm.sqls.doc/sqls548.htm for more
    //details
    public static final String SQL_STATE_CLASS = "08";
    private AwsReportDataSource awsReportDataSource;
    private AwsDataSourceRecovery awsDataSourceRecovery;

    public AwsDataSourceService(DataSource dataSource, TimeZone timezone, AwsReportDataSource awsReportDataSource,
            AwsDataSourceRecovery awsDataSourceRecovery) {
        super(dataSource, timezone);
        this.awsReportDataSource = awsReportDataSource;
        this.awsDataSourceRecovery = awsDataSourceRecovery;
    }

    protected Connection createConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new JSException(getErrorMessage("jsexception.error.creating.connection"), e);
        }
    }

    @Override
    public boolean testConnection() throws SQLException {
        awsDataSourceRecovery.createAwsDSSecurityGroup(awsReportDataSource);
        try {
            return super.testConnection();
        } catch (SQLException ex) {
            if (isConnectionRefusedEx(ex)) {
                throw new JSException(getErrorMessage("aws.exception.datasource.recovery.timeout"), ex);
            } else {
                throw ex;
            }
        }
    }

    /**
     * @return Returns the dataSource.
     */
    public DataSource getDataSource() {
        return new AwsDataSourceWrapper(super.getDataSource());
    }

    private class AwsDataSourceWrapper extends PoolingDataSource {
        private DataSource dataSource;

        AwsDataSourceWrapper(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            try {
                Connection c = this.dataSource.getConnection();
                if (log.isDebugEnabled()) {
                    log.debug("Create Connection successful at com.jaspersoft.jasperserver.api.engine.jasperreports." +
                            "service.impl.AwsDataSourceService.createConnection");
                }
                return c;
            } catch (SQLException e) {
                //Amazon Security recovery block in case of ConnectionException or SocketTimeoutException
                //The condition is complicated just for double checking. In theory checking SQL_STATE_CLASS is enough.
                if (isConnectionRefusedEx(e)) {
                    awsDataSourceRecovery.createAwsDSSecurityGroup(awsReportDataSource);
                    try {
                        return this.dataSource.getConnection();
                    } catch (SQLException ex) {
                        if (isConnectionRefusedEx(ex)) {
                            throw new JSAwsDataSourceRecoveryException(e.getMessage() + "\n" +
                                    getErrorMessage("aws.exception.datasource.recovery.timeout"));
                        } else {
                            log.error("Error creating connection.", e);
                            throw new JSException(getErrorMessage("jsexception.error.creating.connection"), e);
                        }
                    }
                } else {
                    log.error("Error creating connection.", e);
                    throw new JSException(getErrorMessage("jsexception.error.creating.connection"), e);
                }
            }
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException {
            return this.dataSource.isWrapperFor(aClass);
        }

        @Override
        public <T> T unwrap(Class<T> tClass) throws SQLException {
            return this.dataSource.unwrap(tClass);
        }
    }

    private boolean isConnectionRefusedEx(SQLException ex) {
        Throwable throwable = ExceptionUtils.getRootCause(ex);
        return throwable instanceof ConnectException || throwable instanceof SocketTimeoutException ||
                ex.getSQLState() != null && ex.getSQLState().startsWith(SQL_STATE_CLASS);
    }

    private String getErrorMessage(String errorCode) {
        return awsDataSourceRecovery.getMessageSource().getMessage(errorCode, null, LocaleContextHolder.getLocale());
    }

}
