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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 24.01.2017
 */
public class CheckExpressionContainerValidator
        implements ConstraintValidator<CheckExpressionContainer, ClientExpressionContainer> {

    public static final String WHERE_EXPRESSION_NOT_VALID = "query.where.expression.not.valid";
    public static final String AGGREGATION_EXPRESSION_NOT_VALID = "query.aggregation.expression.not.valid";
    public static final String CALCULATED_FIELD_EXPRESSION_NOT_VALID = "query.calculated.field.expression.not.valid";

    private Set<Class> acceptedExpressions;

    @Override
    public void initialize(CheckExpressionContainer constraintAnnotation) {
        acceptedExpressions = new HashSet<Class>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(ClientExpressionContainer value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.getString() == null && value.getObject() == null) {
            return false;
        }
        if (value.getString() != null) return true;
        return acceptedExpressions.contains(value.getObject().getClass());
    }


}
