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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Paul Lysak
 * @version $Id$
 */
public class VirtualDataSourceValidatorTest {
    VirtualDataSourceValidator validator = new VirtualDataSourceValidator();
    {
//        validator.
    }

    @Test
    public void validateSubDsIdOk() {
        ValidationErrors errors = new ValidationErrorsImpl();
        validator.validateSubDsId("subDatasource", "subDsId", errors);
        Assert.assertTrue("Should be no validation errors", errors.getErrors().isEmpty());
        Assert.assertFalse("Errors flag should be false", errors.isError());
    }

    @Test
    public void validateSubDsIdInvalidDotFail() {
        ValidationErrors errors = new ValidationErrorsImpl();
        validator.validateSubDsId("sub.Datasource", "subDsId", errors);
        Assert.assertEquals("Expected one validation error", 1, errors.getErrors().size());
        Assert.assertTrue("Errors flag should be true", errors.isError());
        Assert.assertEquals("Error code", "error.invalid.chars", ((ValidationErrorImpl)errors.getErrors().get(0)).getErrorCode());
    }

    @Test
    public void validateSubDsIdInvalidStartWithUnderscoreFail() {
        ValidationErrors errors = new ValidationErrorsImpl();
        validator.validateSubDsId("_subDatasource", "subDsId", errors);
        Assert.assertEquals("Expected one validation error", 1, errors.getErrors().size());
        Assert.assertTrue("Errors flag should be true", errors.isError());
        Assert.assertEquals("Error code", "error.first.letter.required", ((ValidationErrorImpl)errors.getErrors().get(0)).getErrorCode());
    }

}
