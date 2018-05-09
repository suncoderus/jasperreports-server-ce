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
package com.jaspersoft.jasperserver.api.logging.service.impl;

import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;
import com.jaspersoft.jasperserver.api.logging.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class LoggingFacade implements LoggingService {

    @Resource(name="${bean.routingMap}")
    private Map<Class<? extends LoggableEvent>, LoggingService> routingMap;

    public void saveEvent(LoggableEvent loggableEvent) {
        for (Class<? extends LoggableEvent> clazz: routingMap.keySet()) {
            if (clazz.isAssignableFrom(loggableEvent.getClass())) {
                LoggingService loggingService = routingMap.get(clazz);
                if (loggingService != null) {
                    loggingService.saveEvent(loggableEvent);
                }
            }
        }
    }

    private Map<Class<? extends LoggableEvent>, List<LoggableEvent>> geSplittedEventsMap(List<LoggableEvent> events) {
        Map<Class<? extends LoggableEvent>, List<LoggableEvent>> eventsByTypeMap =
                new HashMap<Class<? extends LoggableEvent>, List<LoggableEvent>>();

        for (LoggableEvent loggableEvent : events) {
            for (Class<? extends LoggableEvent> clazz: routingMap.keySet()) {
                if (clazz.isAssignableFrom(loggableEvent.getClass())) {
                    if (!eventsByTypeMap.containsKey(clazz)) {
                        eventsByTypeMap.put(clazz, new ArrayList<LoggableEvent>());
                    }

                    eventsByTypeMap.get(clazz).add(loggableEvent);
                }
            }
        }

        return eventsByTypeMap;
    }

    public void saveEvents(List<LoggableEvent> loggableEvents) {
        Map<Class<? extends LoggableEvent>, List<LoggableEvent>> eventsByTypeMap = geSplittedEventsMap(loggableEvents);
        for (Class<? extends LoggableEvent> clazz: eventsByTypeMap.keySet()) {
            LoggingService loggingService = routingMap.get(clazz);
            if (loggingService != null) {
                loggingService.saveEvents(eventsByTypeMap.get(clazz));
            }
        }
    }
}
