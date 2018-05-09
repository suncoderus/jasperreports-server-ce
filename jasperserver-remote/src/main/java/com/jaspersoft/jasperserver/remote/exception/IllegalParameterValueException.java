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
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class IllegalParameterValueException extends RemoteException {
    public static final String ERROR_CODE  = "illegal.parameter.value.error";

    public IllegalParameterValueException(String parameterName, String parameterValue) {
        this(parameterName, parameterValue, null);
    }

    public IllegalParameterValueException(String parameterName, String parameterValue, Exception cause) {
        this("Value of parameter " + (parameterName != null ? "'" + parameterName + "'" : "") + " invalid", cause,
                parameterName, parameterValue);
    }

    public IllegalParameterValueException(String message, String... parameters) {
        this(message, null, parameters);
    }

    public IllegalParameterValueException(String message, Exception cause, String... parameters) {
        super(message, cause);
        setErrorDescriptor(new ErrorDescriptor().setMessage(message).setErrorCode(ERROR_CODE).setParameters(parameters));
    }

    public IllegalParameterValueException(ErrorDescriptor errorDescriptor){
        super(errorDescriptor);
    }
}
