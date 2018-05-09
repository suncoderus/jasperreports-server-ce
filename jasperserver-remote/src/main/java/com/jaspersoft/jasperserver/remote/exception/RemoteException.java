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

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class RemoteException extends RuntimeException {

    private ErrorDescriptor errorDescriptor;

    public RemoteException() {
        super();
        this.errorDescriptor = new ErrorDescriptor();
    }

    public RemoteException(String message) {
        super(message);
        this.errorDescriptor = new ErrorDescriptor().setMessage(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
        this.errorDescriptor = new ErrorDescriptor().setMessage(message);
    }

    public RemoteException(Throwable cause, SecureExceptionHandler exceptionHandler) {
        super(cause);
        this.errorDescriptor = exceptionHandler.handleException(cause);
    }

    public RemoteException(ErrorDescriptor errorDescriptor){
        super(errorDescriptor.getMessage());
        this.errorDescriptor = errorDescriptor;
    }

    public RemoteException(ErrorDescriptor errorDescriptor, Throwable e){
        super(errorDescriptor.getMessage(), e);
        this.errorDescriptor = errorDescriptor;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public void setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
    }

    public Boolean isUnexpected(){
        return getErrorDescriptor() != null && ErrorDescriptor.ERROR_CODE_UNEXPECTED_ERROR.equals(getErrorDescriptor().getErrorCode());
    }
}
