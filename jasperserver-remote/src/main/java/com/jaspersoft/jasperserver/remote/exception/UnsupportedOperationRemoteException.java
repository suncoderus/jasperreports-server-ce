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
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class UnsupportedOperationRemoteException extends RemoteException {
    public static final String ERROR_CODE_UNSUPPORTED_OPERATION = "operation.not.supported";
    public UnsupportedOperationRemoteException() {
        super();
        getErrorDescriptor().setErrorCode(ERROR_CODE_UNSUPPORTED_OPERATION);
    }
    public UnsupportedOperationRemoteException(String operation) {
        super("Operation [" + operation + "] not supported");
        ErrorDescriptor errorDescriptor = getErrorDescriptor();
        errorDescriptor.setErrorCode(ERROR_CODE_UNSUPPORTED_OPERATION);
        errorDescriptor.setParameters(operation);
    }
}
