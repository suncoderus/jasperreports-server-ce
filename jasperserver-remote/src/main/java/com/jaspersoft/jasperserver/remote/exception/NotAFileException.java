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
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class NotAFileException extends RemoteException {
    public final String ERROR_NOT_A_FILE = "not.a.file";

    public NotAFileException(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor);
        this.getErrorDescriptor().setErrorCode(ERROR_NOT_A_FILE);
    }

    public NotAFileException(String uri) {
        super("Resource "+ uri+" is not a file");
        this.getErrorDescriptor().setErrorCode(ERROR_NOT_A_FILE);
        this.getErrorDescriptor().setParameters(uri);
    }

}
