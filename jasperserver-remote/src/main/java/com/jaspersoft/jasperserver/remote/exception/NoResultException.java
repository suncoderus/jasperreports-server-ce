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
package com.jaspersoft.jasperserver.remote.exception;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * @author: Zakhar.Tomchenco
 */
public class NoResultException extends AccessDeniedException {
    public final String ERROR_CODE_NO_RESULT = "no.result.available";

    public NoResultException(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor);
        this.getErrorDescriptor().setErrorCode(ERROR_CODE_NO_RESULT);
    }

    public NoResultException(String message) {
        super(message);
        this.getErrorDescriptor().setErrorCode(ERROR_CODE_NO_RESULT);
    }
}
