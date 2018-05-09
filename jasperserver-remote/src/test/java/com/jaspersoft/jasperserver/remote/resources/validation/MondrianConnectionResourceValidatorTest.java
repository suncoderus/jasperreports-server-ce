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
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.MondrianConnectionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class MondrianConnectionResourceValidatorTest {
    @InjectMocks
    private final MondrianConnectionResourceValidator validator = new MondrianConnectionResourceValidator();
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private MondrianConnection connection;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() {
        connection = new MondrianConnectionImpl();
        connection.setLabel("aa");
        connection.setDataSourceReference("/a");
        connection.setSchemaReference("/b");
    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noDataSource() throws Exception {
        connection.setDataSourceReference(null);
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyDataSource() throws Exception {
        connection.setDataSourceReference("");
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noSchema() throws Exception {
        connection.setSchemaReference(null);
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptySchema() throws Exception {
        connection.setSchemaReference("");
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noSchemaContent_newFile() throws Exception {
        final FileResourceImpl schema = new FileResourceImpl();
        schema.setURIString("/test/uri");
        connection.setSchema(schema);
        validator.validate(connection);
    }

    @Test
    public void testValidate_noSchemaContent_update_nothingHappens() throws Exception {
        final FileResourceImpl schema = new FileResourceImpl();
        schema.setURIString("/test/uri");
        schema.setVersion(10);
        connection.setSchema(schema);
        try {
            validator.validate(connection);
        } catch (Exception e) {
            fail();
        }
    }

}
