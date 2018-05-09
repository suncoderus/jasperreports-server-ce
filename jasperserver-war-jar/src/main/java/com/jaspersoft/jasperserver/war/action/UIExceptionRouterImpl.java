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
package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.JSShowOnlyErrorMessage;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;

import javax.annotation.Resource;

/**
 * Base implementation of {@link UIExceptionRouter}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class UIExceptionRouterImpl implements UIExceptionRouter {

    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    private UIExceptionProcessor getExceptionProcessor(Exception cause) {
        return genericTypeProcessorRegistry.getTypeProcessor(cause.getClass(), UIExceptionProcessor.class, false);
    }

    @Override
    @SuppressWarnings("unchecked") //genericTypeProcessorRegistry guarantee safety of casting.
    public JSShowOnlyErrorMessage getUIException(Exception cause) {
        final UIExceptionProcessor exceptionProcessor = getExceptionProcessor(cause);
        return exceptionProcessor != null ? exceptionProcessor.getUIException(cause) : null;
    }
}
