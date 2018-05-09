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
package com.jaspersoft.jasperserver.api.logging.context.impl;

import com.jaspersoft.jasperserver.api.logging.service.LoggingService;
import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContext;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public abstract class BasicLoggingContextProvider implements LoggingContextProvider, Serializable
{
    private static final Log log = LogFactory.getLog(BasicLoggingContextProvider.class);

    private transient LoggingService loggingService;
    private transient Map<Class<? extends LoggableEvent>, Boolean> enabledLoggingTypesMap;

    private final ThreadLocal<LoggingContext> threadLocalContext = new ThreadLocal<LoggingContext>() {
        @Override
        protected synchronized LoggingContext initialValue() {
            return createLoggingContext();
        }
    };

    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public void setEnabledLoggingTypesMap(Map<Class<? extends LoggableEvent>, Boolean> enabledLoggingTypesMap) {
        this.enabledLoggingTypesMap = enabledLoggingTypesMap;
    }

    protected LoggingContext createLoggingContext() {
        LoggingContextImpl context = new LoggingContextImpl();
        context.setEnabledLoggingTypesMap(enabledLoggingTypesMap);
        return context;
    }

    public LoggingContext getContext() {
        return threadLocalContext.get();
    }

    protected abstract void onFlushContext();
    
    public void flushContext() {
        LoggingContext loggingContext = threadLocalContext.get();
        if (!loggingContext.getAllEvents().isEmpty()) {
            try {
                try {
                    onFlushContext();
                } finally {
                    loggingService.saveEvents(loggingContext.getAllEvents());
                }
            } finally {
                loggingContext.clearAllEvents();
            }
        }
    }

    public <T extends LoggableEvent> boolean isLoggingEnabled(Class<T> clazz) {
        boolean isEnabled = false;

        for (Class<? extends LoggableEvent> baseClass: enabledLoggingTypesMap.keySet()) {
            if (baseClass.isAssignableFrom(clazz) && enabledLoggingTypesMap.get(baseClass)) {
                isEnabled = true;
                break;
            }
        }

        return isEnabled;
    }
}
