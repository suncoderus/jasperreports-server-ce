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
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationResultImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.OlapUnitImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class OlapUnitValidatorTest {
    @InjectMocks
    private final OlapUnitValidator validator = new OlapUnitValidator();
    @Mock
    private OlapConnectionService olapConnectionService;
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private OlapUnit unit;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(olapConnectionService.validate(any(ExecutionContext.class), any(OlapUnit.class))).thenReturn(new ValidationResultImpl());
    }

    @BeforeMethod
    public void setUp(){
        unit = new OlapUnitImpl();
        unit.setLabel("label");
        unit.setOlapClientConnectionReference("/a");
        unit.setMdxQuery("SELECT\n" +
                "   { [Measures].[Store Sales] } ON COLUMNS,\n" +
                "   { [Date].[2002], [Date].[2003] } ON ROWS\n" +
                "FROM Sales\n" +
                "WHERE ( [Store].[USA].[CA] )");


    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(unit);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noQuery() throws Exception {
        unit.setMdxQuery(null);
        validator.validate(unit);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyQuery() throws Exception {
        unit.setMdxQuery("");
        validator.validate(unit);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noConnection() throws Exception {
        unit.setOlapClientConnectionReference(null);
        validator.validate(unit);
    }

}
