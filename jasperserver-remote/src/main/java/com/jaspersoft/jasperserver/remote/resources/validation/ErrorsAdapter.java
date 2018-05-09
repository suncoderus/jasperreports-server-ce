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

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addError;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ErrorsAdapter extends AbstractErrors {
    private final ValidationErrors errors;
    private final String objectName;

    public ErrorsAdapter(ValidationErrors errors, String objectName) {
        this.errors = errors;
        this.objectName = objectName;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
        addError(errors, errorCode, "", defaultMessage, errorArgs);
    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
        addError(errors, errorCode, field, defaultMessage, errorArgs);
    }

    @Override
    public void addAllErrors(Errors errors) {
        throw new NotImplementedException();
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        throw new NotImplementedException();
    }

    @Override
    public List<FieldError> getFieldErrors() {
        throw new NotImplementedException();
    }

    @Override
    public Object getFieldValue(String field) {
        throw new NotImplementedException();
    }
}
