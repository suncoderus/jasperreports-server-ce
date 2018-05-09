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
import java.util.Map;
import java.util.Set;

/**
 * @author Vasyl Spachynskyi
 * @version $Id: Id $
 * @since 27.01.2017
 */
public class CheckParametersExpressionContainerValidator
        implements ConstraintValidator<CheckParametersExpressionContainer, Map<String, ClientExpressionContainer>> {

    public static final String WHERE_EXPRESSION_PARAMETERS_NOT_VALID = "query.where.parameters.expression.not.valid";

    private Set<Class> acceptedExpressions;

    @Override
    public void initialize(CheckParametersExpressionContainer constraintAnnotation) {
        acceptedExpressions = new HashSet<Class>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(Map<String, ClientExpressionContainer> expressionMap, ConstraintValidatorContext context) {
        if (expressionMap == null) return true;

        for (Map.Entry<String, ClientExpressionContainer> item : expressionMap.entrySet()) {
            if (item == null) return false;

            if (item.getValue() == null) {
                return true;
            }

            if (item.getValue().getString() == null && item.getValue().getObject() == null) {
                return false;
            }

            if (item.getValue().getString() != null) {
                return true;
            }

            if (!acceptedExpressions.contains(item.getValue().getObject().getClass())) {
                return false;
            }
        }
        return true;
    }
}