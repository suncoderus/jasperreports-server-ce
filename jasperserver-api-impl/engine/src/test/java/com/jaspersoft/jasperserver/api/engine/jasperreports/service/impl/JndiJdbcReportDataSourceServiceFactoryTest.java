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

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;
import com.jaspersoft.jasperserver.api.metadata.common.util.JndiFallbackResolver;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link JndiJdbcReportDataSourceServiceFactory}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class JndiJdbcReportDataSourceServiceFactoryTest extends UnitilsJUnit4 {

    @TestedObject
    private JndiJdbcReportDataSourceServiceFactory jndiJdbcReportDataSourceServiceFactory;

    @InjectIntoByType
    private Mock<PooledJdbcDataSourceFactory> pooledJdbcDataSourceFactory;

    @InjectIntoByType
    private Mock<JndiFallbackResolver> jndiFallbackResolver;

    @Test
    public void ensureDataSourceTimeZoneIsAppliedToDataSourceService() {
        final String dsTimeZone = "GMT+5";

        setUp();

        //Actual test method call
        JdbcDataSourceService rds = (JdbcDataSourceService) jndiJdbcReportDataSourceServiceFactory.createService(getDataSourceService(dsTimeZone));

        //Check expectations
        Map<String, ?> paramsMap = new HashMap<String, Object>();
        rds.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getTimeZone(dsTimeZone), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));
    }

    @Test
    public void ensureDefaultTimeZoneIsAppliedToDataSourceServiceIfTZFromDataSourceIsEmptyOrNull() {
        final String emptyTZ = "";
        final String nullTZ = null;

        setUp();

        //Actual test method call
        JdbcDataSourceService emptyTZService = (JdbcDataSourceService) jndiJdbcReportDataSourceServiceFactory.createService(getDataSourceService(emptyTZ));
        JdbcDataSourceService nullTZService = (JdbcDataSourceService) jndiJdbcReportDataSourceServiceFactory.createService(getDataSourceService(nullTZ));

        Map<String, ?> paramsMap = new HashMap<String, Object>();

        //Check expectations
        emptyTZService.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getDefault(), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));

        nullTZService.setReportParameterValues(paramsMap);
        assertEquals(TimeZone.getDefault(), paramsMap.get(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE));
    }

    private void setUp() {
        Mock<PooledDataSource> pooledDataSourceMock = MockUnitils.createMock(PooledDataSource.class);
        Mock<DataSource> dataSourceMock = MockUnitils.createMock(DataSource.class);
        pooledDataSourceMock.returns(dataSourceMock).getDataSource();
        pooledJdbcDataSourceFactory.returns(pooledDataSourceMock).createPooledDataSource(null, null, null, null, true, false);
    }

    private JndiJdbcReportDataSource getDataSourceService(String dsTimeZone) {
        Mock<JndiJdbcReportDataSource> serviceMock = MockUnitils.createMock(JndiJdbcReportDataSource.class);
        serviceMock.returns(dsTimeZone).getTimezone();
        return serviceMock.getMock();
    }

}
