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
package com.jaspersoft.jasperserver.api.security.encryption;

import com.jaspersoft.jasperserver.api.security.SecurityConfiguration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * When encryption is turned on, the decrypted value is placed in the request attribute
 * by the same name because the original value in the request parameter is ummutable.
 *
 * @author nmacaraeg
 */
public class EncryptionRequestUtils {

    /**
     * If encryption is turned on, then the decrypted value should be in the attribute by the same name;
     * otherwise, use the original parameter value. Note that we are pulling a string list from the attribute
     * and, for now, the assumption is that the first index in the list is the decrypted value we want.
     *
     * @param request a HttpServletRequest.
     * @param  keyName the key name for the parameter or attribute.
     * @return Either the decrypted value or the original parameter value.
     */
    public static String getValue(ServletRequest request, String keyName) {
        String originalParameterValue = request.getParameter(keyName);
        if (SecurityConfiguration.isEncryptionOn())  {
            List<String> decryptedValues = (List<String>)request.getAttribute(EncryptionFilter.DECRYPTED_PREFIX + keyName);

            return (decryptedValues != null && decryptedValues.size() > 0) ? decryptedValues.get(0) : originalParameterValue;
        }

        return originalParameterValue;
    }

}
