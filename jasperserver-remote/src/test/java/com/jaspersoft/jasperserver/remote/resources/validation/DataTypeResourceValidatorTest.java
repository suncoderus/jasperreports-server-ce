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
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
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
public class DataTypeResourceValidatorTest {
    @InjectMocks
    private final DataTypeResourceValidator validator = new DataTypeResourceValidator();
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private DataType type;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() {
        type = new DataTypeImpl();
        type.setLabel("label");
    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(type);
    }


    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_maxLessThanMin() throws Exception {
        type.setMaxValue(new Integer(0));
        type.setMinValue(new Integer(10));
        validator.validate(type);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_type_notSpecified() throws Exception {
        type.setDataTypeType((byte) 0);

        validator.validate(type);
    }

    @Test
    public void testValidate_regexp_valid() throws Exception {
        type.setRegularExpr("[a-z]+");

        validator.validate(type);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_regexp_invalid() throws Exception {
        type.setRegularExpr("[a-z");

        validator.validate(type);
    }

    @Test
    public void testValidate_maxLength_valid() throws Exception {
        type.setMaxLength(1);

        validator.validate(type);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_maxLength_invalid() throws Exception {
        type.setMaxLength(0);

        validator.validate(type);
    }
}
