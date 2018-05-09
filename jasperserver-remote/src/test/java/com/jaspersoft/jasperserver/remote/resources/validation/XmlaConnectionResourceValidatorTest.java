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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.XMLAConnectionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class XmlaConnectionResourceValidatorTest {
    @InjectMocks
    private final XmlaConnectionResourceValidator validator = new XmlaConnectionResourceValidator();
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private XMLAConnection connection;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp(){
        connection = new XMLAConnectionImpl();
        connection.setLabel("aa");

        connection.setCatalog("a");
        connection.setDataSource("b");
        connection.setURI("c");
        connection.setUsername("d");
    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noCatalog() throws Exception {
        connection.setCatalog(null);
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyDataSource() throws Exception {
        connection.setDataSource("");
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noUri() throws Exception {
        connection.setURI(null);
        validator.validate(connection);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyUsername() throws Exception {
        connection.setUsername("");
        validator.validate(connection);
    }

}
