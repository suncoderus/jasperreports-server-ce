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
package com.jaspersoft.jasperserver.api.security.validators;

import com.jaspersoft.jasperserver.api.security.params.SpringParams;
import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.ServletRequest;

/**
 * DEPRECATED!
 * Validate Spring Framework parameters/values.
 */
public class SpringValidator// extends ValidatorImpl
{
    private static final Logger LOG = Logger.getLogger(SpringValidator.class);
    private final String context = this.getClass().getName();

    /**
     * @inheritDoc
     */
    public boolean validate(ServletRequest request) throws ValidationException, IntrusionException
    {
        boolean status = true;
        String validParamName = null;
        String validParamValue = null;

        StringBuilder paramName = new StringBuilder();
        StringBuilder paramValue = new StringBuilder();
        for (SpringParams p : SpringParams.values())
        {
            paramName.append(p.getParamName());
            if (paramName.length() > 0)
            {
                String param = request.getParameter(paramName.toString());
                if (param != null && param.length() > 0)
                {
                    paramValue.append(param);
                    //status = validate(paramValue.toString(), p.getValueValidationKey(), p.getMaxLength(), p.getAllowNull(), context);
                    LOG.debug("SAFE = [" + paramName + "] [" + paramValue + "]");
                }
            }

            // reset
            paramName.setLength(0);
            paramValue.setLength(0);
        }

        return status;
    }

}
