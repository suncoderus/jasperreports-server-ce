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
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericResourceValidatorTest {
    @InjectMocks
    private GenericResourceValidator<Resource> validator = mock(GenericResourceValidator.class);
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private Resource resource = new ResourceLookupImpl();

    @BeforeClass
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void init() {
        reset(profileAttributesResolver);
        resource.setURIString("/test/uri");
        resource.setLabel("testa");
        resource.setDescription("description");
        reset(validator);
        doCallRealMethod().when(validator).validate(any(Resource.class));
        doCallRealMethod().when(validator).validate(any(Resource.class), eq(false));
    }

    @Test
    public void validate() {
        validator.validate(resource);
        verify(validator).internalValidate(same(resource), any(ValidationErrors.class));
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validate_exception() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ValidationErrors validationErrors = (ValidationErrors) invocation.getArguments()[1];
                validationErrors.add(new ValidationErrorImpl());
                return null;
            }
        }).when(validator).internalValidate(same(resource), any(ValidationErrors.class));
        validator.validate(resource);
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validate_labelContainsAttributePlaceholder_exception() throws Exception {
        when(profileAttributesResolver.containsAttribute(resource.getLabel())).thenReturn(true);
        validator.validate(resource);
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validate_labelIsNull_exception() throws Exception {
        validator.validate(new ResourceLookupImpl());
    }

    @Test
    public void validate_labelIsNull_slipRepoFieldsValidation_success() throws Exception {
        Exception exception = null;
        try {
            validator.validate(new ResourceLookupImpl(), true);
        } catch (Exception e){
            exception = e;
        }
        assertNull(exception);
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validate_descriptionContainsAttributePlaceholder_exception() throws Exception {
        when(profileAttributesResolver.containsAttribute(resource.getDescription())).thenReturn(true);
        validator.validate(resource);
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validate_nameContainsAttributePlaceholder_exception() throws Exception {
        when(profileAttributesResolver.containsAttribute(resource.getName())).thenReturn(true);
        validator.validate(resource);
    }
}
