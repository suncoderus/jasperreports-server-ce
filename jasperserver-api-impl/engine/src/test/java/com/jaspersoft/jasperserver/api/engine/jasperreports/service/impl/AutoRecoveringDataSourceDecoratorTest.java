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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

public class AutoRecoveringDataSourceDecoratorTest {

    private static final boolean RECOVERABLE = true;
    private static final boolean NON_RECOVERABLE = false;
    private static final int NO_ERRORS = 0;
    private static final int ONE_ERROR = 1;
    private static final int TWO_ERRORS = 2;
    private static final boolean EXCEPTION_THROWN = true;
    private static final boolean EXCEPTION_NOT_THROWN = false;

    @Test
    public void noErrors() throws Exception {
        test(NO_ERRORS, RECOVERABLE, 0, 1, EXCEPTION_NOT_THROWN);
    }

    @Test
    public void oneRecoveryAttemptIfOneError() throws Exception {
        test(ONE_ERROR, RECOVERABLE, 1, 2, EXCEPTION_NOT_THROWN);
    }

    @Test
    public void oneRecoveryAttemptIfTwoErrors() throws Exception {
        test(TWO_ERRORS, RECOVERABLE, 1, 2, EXCEPTION_THROWN);
    }

    @Test
    public void noRetryIfNonRecoverable() throws Exception {
        test(ONE_ERROR, NON_RECOVERABLE, 1, 1, EXCEPTION_THROWN);
    }

    private void test(int numberOfErrorsToMake,
                      boolean recoverable,
                      int expectedNumberOfRecoveryCalls,
                      int expectedNumberOfTargetCalls,
                      boolean expectedException) throws Exception {
        DataSourceMock targetDs = new DataSourceMock(numberOfErrorsToMake);
        TestDataSourceDecorator testDs = new TestDataSourceDecorator(targetDs, recoverable);
        SQLException exceptionMade = null;
        try {
            testDs.getConnection();
        } catch (SQLException e) {
            exceptionMade = e;
        }
        Assert.assertEquals("recovery calls made", expectedNumberOfRecoveryCalls, testDs.recoveryCallsMade);
        Assert.assertEquals("getConnection calls made", expectedNumberOfTargetCalls, targetDs.callsMade);
        if (expectedException) {
            Assert.assertNotNull("Exception expected", exceptionMade);
        }
    }

    private static final class TestDataSourceDecorator extends AutoRecoveringDataSourceDecorator {

        int recoveryCallsMade;
        private final boolean recoveryResult;

        public TestDataSourceDecorator(DataSource target, boolean recoveryResult) {
            super(target);
            this.recoveryResult = recoveryResult;
        }

        @Override
        protected boolean recover(SQLException cause) {
            recoveryCallsMade++;
            return recoveryResult;
        }

    }

    private static final class DataSourceMock implements DataSource {

        private final int failFirst;
        int callsMade;

        public DataSourceMock(int failFirst) {
            this.failFirst = failFirst;
        }

        @Override
        public Connection getConnection() throws SQLException {
            handleCall();
            return null;
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            handleCall();
            return null;
        }

        private void handleCall() throws SQLException {
            callsMade++;
            if (failFirst >= callsMade) {
                throw new SQLException("test exception, ignore");
            }
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }
    }
}
