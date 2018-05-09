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

/**
 * @author fpang
 * @since May 6, 2016
 * @version $Id$
 *
 */

package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;

/**
 * @author fpang, schubar
 *
 */
public class ParameterMapValidator implements ConstraintValidator<ParameterMap, Map<String, ClientExpressionContainer>> {
    public static Pattern NOT_W_PATTERN = Pattern.compile("[^\\p{L}\\p{N}_]");
    public static Pattern NOT_A_TO_Z_OR_UNDERSCORE_PATTERN = Pattern.compile("[^\\p{L}_]");

    @Override
    public void initialize(ParameterMap constraintAnnotation) {
    }

    @Override
    public boolean isValid(Map<String, ClientExpressionContainer> targetParameterMap, ConstraintValidatorContext context) {
        if (targetParameterMap != null) {
            for (String paramEl : targetParameterMap.keySet()) {
                if (paramEl == null
                    || paramEl.contains(".") // This check is explicitly defined because <dot> has special meaning in Data Engine and impl of ExpressionEvaluator
                    || NOT_A_TO_Z_OR_UNDERSCORE_PATTERN.matcher(paramEl.substring(0, 1)).matches()
                    || NOT_W_PATTERN.matcher(paramEl.substring(1)).matches()) {
                    return false;
                }
            }
        }
        return true;
    }
}
