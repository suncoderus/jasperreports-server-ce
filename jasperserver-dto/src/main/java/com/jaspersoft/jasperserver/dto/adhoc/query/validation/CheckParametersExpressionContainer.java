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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import static com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckParametersExpressionContainerValidator.WHERE_EXPRESSION_PARAMETERS_NOT_VALID;

/**
 * @author Vasyl Spachynskyi
 * @version $Id: Id $
 * @since 27.01.2017
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CheckParametersExpressionContainerValidator.class })
@ReportAsSingleViolation
public @interface CheckParametersExpressionContainer {
    String errorCode = WHERE_EXPRESSION_PARAMETERS_NOT_VALID;

    String message() default errorCode;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends ClientExpression>[] value();
}
