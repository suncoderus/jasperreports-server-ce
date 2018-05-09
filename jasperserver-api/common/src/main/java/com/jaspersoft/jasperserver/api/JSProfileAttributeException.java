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

package com.jaspersoft.jasperserver.api;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * <p></p>
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
public class JSProfileAttributeException extends JSException {
    public static final String PROFILE_ATTRIBUTE_EXCEPTION_SUBSTITUTION_BASE =
            "profile.attribute.exception.substitution.base";
    public static final String PROFILE_ATTRIBUTE_EXCEPTION_SUBSTITUTION_NOT_FOUND =
            "profile.attribute.exception.substitution.not.found";
    public static final String PROFILE_ATTRIBUTE_EXCEPTION_SUBSTITUTION_CATEGORY_INVALID =
            "profile.attribute.exception.substitution.category.invalid";

    private ErrorDescriptor errorDescriptor;

    //We keep localizedMessage, because it is used for legacy popup error dialogs.
    // In ErrorDescriptor we use default message in English locale
    public JSProfileAttributeException(String localizedMessage, ErrorDescriptor errorDescriptor) {
        super(localizedMessage);

        this.errorDescriptor = errorDescriptor;
    }

    public JSProfileAttributeException(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor.getMessage() != null ? errorDescriptor.getMessage() :
                "Profile attribute substitutions error: " + errorDescriptor.toString());

        this.errorDescriptor = errorDescriptor;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }
}
