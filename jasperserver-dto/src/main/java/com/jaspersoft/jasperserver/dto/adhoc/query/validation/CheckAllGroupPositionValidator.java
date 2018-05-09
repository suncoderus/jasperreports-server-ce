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

package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.common.ValidationErrorDescriptorBuilder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import java.util.List;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 15.02.2017
 */
public class CheckAllGroupPositionValidator implements ConstraintValidator<CheckAllGroupPosition, List<ClientQueryGroup>>,
        ValidationErrorDescriptorBuilder {
    @Override
    public void initialize(CheckAllGroupPosition constraintAnnotation) {}

    @Override
    public boolean isValid(List<ClientQueryGroup> groups, ConstraintValidatorContext context) {
        if (groups.size() > 1) {
            if (isAllGroup(groups.get(0))) {
                return true;
            }

            for (ClientQueryGroup g : groups) {
                if (isAllGroup(g)) return false;
            }
        }
        return true;
    }

    @Override
    public ErrorDescriptor build(ConstraintViolation violation) {
        return new ErrorDescriptor()
                .setErrorCode(CheckAllGroupPosition.errorCode)
                .setMessage("AllGroup should be first")
                .setParameters(violation.getPropertyPath().toString());
    }

    private boolean isAllGroup(ClientQueryGroup group) {
        return group instanceof ClientQueryGroup.ClientAllGroup;
    }
}
