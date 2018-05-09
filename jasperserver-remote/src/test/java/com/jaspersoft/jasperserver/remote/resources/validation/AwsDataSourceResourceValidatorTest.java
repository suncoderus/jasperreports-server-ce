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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AwsReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class AwsDataSourceResourceValidatorTest {
    @InjectMocks
    private final AwsDataSourceResourceValidator validator = new AwsDataSourceResourceValidator();
    @Mock
    private JdbcDriverService jdbcDriverService;
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;
    @Spy
    private List<String> awsRegions = new LinkedList<String>();

    private AwsReportDataSource dataSource;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        reset(jdbcDriverService);
        reset(profileAttributesResolver);
        when(jdbcDriverService.isRegistered(anyString())).thenReturn(true);
        when(profileAttributesResolver.containsAttribute(anyString())).thenReturn(false);

        awsRegions.add("amazon");

        dataSource = new AwsReportDataSourceImpl();
        dataSource.setLabel("tets");

        dataSource.setAWSAccessKey("fas");
        dataSource.setAWSSecretKey("faf");
        dataSource.setAWSRegion(awsRegions.get(0));
        dataSource.setConnectionUrl("jdbc:ajfa");
        dataSource.setDbName("aajfa");
        dataSource.setDriverClass("ajfa");
        dataSource.setUsername("ajfa");
        dataSource.setTimezone("America/New_York");
    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_no_accessKey() throws Exception {
        dataSource.setAWSAccessKey(null);
        dataSource.setAWSSecretKey("secret_key");

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noSecretKey() throws Exception {
        dataSource.setAWSSecretKey(null);
        dataSource.setAWSAccessKey("Access_key");

        validator.validate(dataSource);
    }

    @Test
    public void testValidate_no_keys() throws Exception {
        dataSource.setAWSAccessKey(null);
        dataSource.setAWSSecretKey(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noConnection() throws Exception {
        dataSource.setConnectionUrl(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noDriver() throws Exception {
        dataSource.setDriverClass(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_unknownDriver() throws Exception {
        reset(jdbcDriverService);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noUsername() throws Exception {
        dataSource.setUsername(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noRegion() throws Exception {
        dataSource.setAWSRegion(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_nodbName() throws Exception {
        dataSource.setDbName(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_invalidTimezione() throws Exception {
        dataSource.setTimezone("#$%^&*(OL)");

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_invalidRegion() throws Exception {
        dataSource.setAWSRegion("#$%^&*(OL)");

        validator.validate(dataSource);
    }

    @Test
    public void testValidate_driverContainsAttribute() {
        reset(jdbcDriverService);
        when(profileAttributesResolver.containsAttribute(dataSource.getDriverClass())).thenReturn(true);

        validator.validate(dataSource);
    }

    @Test
    public void testValidate_connectionUrlContainsAttribute() {
        dataSource.setConnectionUrl("{attribute('name', 'category')}");
        when(profileAttributesResolver.containsAttribute(dataSource.getConnectionUrl())).thenReturn(true);

        validator.validate(dataSource);
    }

    @Test
    public void testValidate_awsRegionContainsAttribute() {
        dataSource.setAWSRegion("{attribute('name', 'category')}");
        when(profileAttributesResolver.containsAttribute(dataSource.getAWSRegion())).thenReturn(true);

        validator.validate(dataSource);
    }
}

