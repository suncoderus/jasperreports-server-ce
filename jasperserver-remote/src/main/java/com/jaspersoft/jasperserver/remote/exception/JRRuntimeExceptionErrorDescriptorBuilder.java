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
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptorBuilder;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketingService;
import net.sf.jasperreports.engine.JRRuntimeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Component
public class JRRuntimeExceptionErrorDescriptorBuilder implements ErrorDescriptorBuilder<JRRuntimeException> {

    @Resource
    private SecureExceptionHandler secureExceptionHandler;

    @Override
    public ErrorDescriptor build(JRRuntimeException e) {
        final String messageKey = e.getMessageKey();
        if(BucketingService.EXCEPTION_MESSAGE_KEY_BUCKET_MEASURE_LIMIT.equals(messageKey)){
            return new ErrorDescriptor().setErrorCode("crosstab.bucket.measure.limit").setMessage(e.getMessage());
        }
        return secureExceptionHandler.handleException(e);
    }
}
