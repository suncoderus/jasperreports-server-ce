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
public class ResourceAlreadyExistsException extends RemoteException{
    public ResourceAlreadyExistsException(String resourceName){
        this("Resource " + (resourceName != null ? "'" + resourceName + "'" : "") +" already exists", resourceName);
    }
    
    public ResourceAlreadyExistsException(String message, String ... parameters){
        super(new ErrorDescriptor().setMessage(message).setErrorCode("resource.already.exists").setParameters(parameters));
    }

    public ResourceAlreadyExistsException(ErrorDescriptor errorDescriptor){
            super(errorDescriptor);
    }
}
