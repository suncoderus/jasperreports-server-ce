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
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class ErrorDescriptorBuildingService {
    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    @Resource
    private SecureExceptionHandler secureExceptionHandler;

    public ErrorDescriptor buildErrorDescriptor(Throwable e){
        final ErrorDescriptorBuilder errorDescriptorBuilder = genericTypeProcessorRegistry
                .getTypeProcessor(e.getClass(), ErrorDescriptorBuilder.class, false);
        return errorDescriptorBuilder != null ? errorDescriptorBuilder.build(e) : secureExceptionHandler.handleException(e);
    }
}
