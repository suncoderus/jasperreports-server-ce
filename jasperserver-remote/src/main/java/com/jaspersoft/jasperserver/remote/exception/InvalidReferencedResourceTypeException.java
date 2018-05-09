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

import org.apache.commons.lang3.StringUtils;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class InvalidReferencedResourceTypeException extends RemoteException {
    private static final String ERROR_CODE  = "invalid.reference.type";
    public InvalidReferencedResourceTypeException(String currentType, String referenceAttributeName, String uri, String ... expectedTypes){
        super(buildMessage(currentType, referenceAttributeName, uri, expectedTypes));
        getErrorDescriptor().setErrorCode(ERROR_CODE).setParameters(currentType, referenceAttributeName, uri,
                StringUtils.join(expectedTypes, ", "));
    }

    private static String buildMessage(String currentType, String referenceAttributeName, String uri, String ... expectedTypes){
        return "Resource or type '" + currentType + "', referenced by an attribute '" + referenceAttributeName
                + "' (" + uri + "), doesn't match expected types: " + StringUtils.join(expectedTypes, ", ") + ".";
    }
}
