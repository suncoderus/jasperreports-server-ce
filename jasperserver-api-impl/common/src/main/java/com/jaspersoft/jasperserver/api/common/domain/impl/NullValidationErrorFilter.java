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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;

/**
 * @author Paul Lysak
 *         Date: 05.10.12
 *         Time: 16:30
 */
public class NullValidationErrorFilter implements ValidationErrorFilter {
    private static final NullValidationErrorFilter instance = new NullValidationErrorFilter();

    public static final ValidationErrorFilter getInstance() {
        return instance;
    }

    public static boolean isNullFilter(ValidationErrorFilter filter) {
        return filter == null || filter instanceof NullValidationErrorFilter;
    }

    @Override
    public boolean matchError(ValidationError error) {
        return true;
    }

    @Override
    public boolean matchErrorCode(String errorCode) {
        return true;
    }

    @Override
    public boolean matchErrorField(String errorField) {
        return true;
    }
}
