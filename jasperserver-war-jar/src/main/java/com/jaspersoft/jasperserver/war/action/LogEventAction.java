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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.util.DateUtils;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import com.jaspersoft.jasperserver.api.engine.common.service.LoggingService;

/**
 * Log event action class.
 */
public class LogEventAction extends FormAction {
    // Attributes.
    private static final String ATTRIBUTE_AJAX_RESPONSE_MODEL = "ajaxResponseModel";
    private static final String ATTRIBUTE_MESSAGE_FILTER = "messageFilter";
    private static final String ATTRIBUTE_MESSAGE = "message";

    // Prameters.
    private static final String PARAMETER_SELECTED_IDS = "selectedIds";
    private static final String PARAMETER_ID = "id";

    // Message filter.
    enum MessageFilter {
        ALL,
        UNREAD
    }

    // Services.
    private LoggingService loggingService;
    private MessageSource messages;
    private ConfigurationBean configurationBean;

    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    public Event messages(RequestContext context) throws Exception {
        try {
            List events;
            String messageFilterName = (String) context.getConversationScope().get(ATTRIBUTE_MESSAGE_FILTER);
            if (MessageFilter.ALL.name().equals(messageFilterName))
                events = loggingService.getUserEvents(null);
            else if (MessageFilter.UNREAD.name().equals(messageFilterName))
                events = loggingService.getUnreadEvents(null);
            else
                events = loggingService.getUserEvents(null);

            context.getRequestScope().put(ATTRIBUTE_AJAX_RESPONSE_MODEL,
                    createJSONResponse(toJsonMessages(events, false), true));
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", e.toString());
            context.getRequestScope().put(ATTRIBUTE_AJAX_RESPONSE_MODEL,
                    createJSONResponse(jsonObject, true));
        }

        return success();
    }

    public JSONObject createJSONResponse(Object json, boolean success) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("status", success ? "OK" : "ERROR");
        jsonObject.put("data", json);

        return jsonObject;
    }

    private JSONArray toJsonMessages(List<LogEvent> events, boolean detailed) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (LogEvent logEvent : events) {
            jsonArray.put(toJsonMessage(logEvent, detailed));
        }

        return jsonArray;
    }

    private JSONObject toJsonMessage(LogEvent logEvent, boolean detailed) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(PARAMETER_ID, logEvent.getId());
        jsonObject.put("date", getFormattedDate(logEvent.getOccurrenceDate()));
        jsonObject.put("timestamp", new SimpleDateFormat(configurationBean.getTimestampFormat()).format(logEvent.getOccurrenceDate()));
        jsonObject.put("subject", messages.getMessage(logEvent.getMessageCode(), null,
                LocaleContextHolder.getLocale()));
        jsonObject.put("type", messages.getMessage("eventType." + logEvent.getType() + ".label", null,
                LocaleContextHolder.getLocale()));
        jsonObject.put("component", messages.getMessage("event.component." + logEvent.getComponent(), null, 
                LocaleContextHolder.getLocale()));
        if (detailed) {
            jsonObject.put(ATTRIBUTE_MESSAGE, logEvent.getText());
        }
        jsonObject.put("isRead", logEvent.getState() == LogEvent.STATE_READ);

        return jsonObject;
    }

    private String getFormattedDate(Date date) {
        String formattedDate = new SimpleDateFormat(configurationBean.getDateFormat()).format(date);

        if (DateUtils.isToday(date)) {
            formattedDate = messages.getMessage("event.date.today", null, formattedDate,
                    LocaleContextHolder.getLocale());
        } else if (DateUtils.isYesterday(date)) {
            formattedDate = messages.getMessage("event.date.yesterday", null, formattedDate,
                    LocaleContextHolder.getLocale());
        } else if (DateUtils.isThisYear(date)) {
            formattedDate = new SimpleDateFormat(configurationBean.getCurrentYearDateFormat()).format(date);
        }

        return formattedDate;
    }

    public Event changeEventsType(RequestContext context) throws Exception {
        String messageFilterName = context.getRequestParameters().get(ATTRIBUTE_MESSAGE_FILTER);
        context.getConversationScope().put(ATTRIBUTE_MESSAGE_FILTER, messageFilterName);

        return success();
    }


    private long[] getIds(RequestContext context) {
        if (context.getRequestParameters().contains(PARAMETER_SELECTED_IDS)) {
            Long[] idArray = (Long[]) context.getRequestParameters().getArray(PARAMETER_SELECTED_IDS, Long.class);

            long[] ids = new long[idArray.length];
            for (int i = 0; i < idArray.length; i++) {
                ids[i] = idArray[i];
            }

            return ids;
        }
        
        return null;
    }

    public Event delete(RequestContext context) throws Exception {
        long[] ids = getIds(context);
        if (ids != null) {
            loggingService.delete(null, ids);
        }

        return success();
    }

    public Event markAsRead(RequestContext context) throws Exception {
        long[] ids = getIds(context);
        if (ids != null) {
            for (long id : ids) {
                LogEvent event = loggingService.getLogEvent(null, id);
                if (event.getState() != LogEvent.STATE_READ) {
                    event.setState(LogEvent.STATE_READ);
                    loggingService.update(event);
                }
            }
        }

        return success();
    }


    public Event markAsUnread(RequestContext context) throws Exception {
        long[] ids = getIds(context);
        if (ids != null) {
            for (long id : ids) {
                LogEvent event = loggingService.getLogEvent(null, id);
                if (event.getState() != LogEvent.STATE_UNREAD) {
                    event.setState(LogEvent.STATE_UNREAD);
                    loggingService.update(event);
                }
            }
        }

        return success();
    }

    public Event setupMessageDetail(RequestContext context) throws Exception {
        String id = context.getRequestParameters().get(PARAMETER_ID);
        try {
            LogEvent event = loggingService.getLogEvent(null, Long.parseLong(id));
            if (event.getState() == LogEvent.STATE_UNREAD) {
                event.setState(LogEvent.STATE_READ);
                loggingService.update(event);
            }

            context.getRequestScope().put(ATTRIBUTE_MESSAGE, toJsonMessage(event, true));

            return success();
        } catch (NumberFormatException e) {
            return error();
        }
    }
}
