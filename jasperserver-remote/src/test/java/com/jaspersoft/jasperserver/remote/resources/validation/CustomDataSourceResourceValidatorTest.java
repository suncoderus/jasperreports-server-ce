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
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.CustomReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class CustomDataSourceResourceValidatorTest {
    @InjectMocks
    private final CustomDataSourceResourceValidator validator = new CustomDataSourceResourceValidator();
    @Mock
    private CustomReportDataSourceServiceFactory customDataSourceFactory;
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private CustomReportDataSource dataSource;

    @BeforeClass
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        dataSource = new CustomReportDataSourceImpl();
        dataSource.setLabel("tets");

        dataSource.setServiceClass("fas");
        dataSource.setDataSourceName("fas2");

        Map properties = new HashMap();
        properties.put("key", "value");
        dataSource.setPropertyMap(properties);
    }

    @Test
    public void testValidate() throws Exception {
        when(customDataSourceFactory.getDefinition(dataSource)).thenReturn(mock(CustomDataSourceDefinition.class));

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_no_definition() throws Exception {
        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_no_identity() throws Exception {
        dataSource.setServiceClass(null);
        dataSource.setDataSourceName(null);

        validator.validate(dataSource);
    }

    @Test
    public void testValidate_take_class_from_Definition() throws Exception {
        String className = "test";
        CustomDataSourceDefinition definition = new CustomDataSourceDefinition();
        definition.setServiceClassName(className);
        when(customDataSourceFactory.getDefinition(dataSource)).thenReturn(definition);

        dataSource.setServiceClass(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_take_class_from_Definition_nvalid() throws Exception {
        CustomDataSourceDefinition definition = new CustomDataSourceDefinition();
        when(customDataSourceFactory.getDefinition(dataSource)).thenReturn(definition);

        dataSource.setServiceClass(null);

        validator.validate(dataSource);
    }

}
