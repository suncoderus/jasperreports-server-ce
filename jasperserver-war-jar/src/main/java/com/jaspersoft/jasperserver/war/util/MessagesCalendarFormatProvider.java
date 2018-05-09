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
package com.jaspersoft.jasperserver.war.util;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class MessagesCalendarFormatProvider implements CalendarFormatProvider, Serializable {

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;
    private String datePatternKey;
    private String calendarDatePatternKey;
    private String datetimePatternKey;
    private String timePatternKey;
    private String calendarDatetimeSeparatorKey;
    private String calendarTimePatternKey;
    private boolean lenientFormats;

    public String getCalendarDatePattern() {
        return messages.getMessage(getCalendarDatePatternKey(), null, getLocale());
    }

    public String getCalendarTimePattern() {
        return messages.getMessage(getCalendarTimePatternKey(), null, "hh:mm", getLocale());
    }

    public String getCalendarDatetimePattern() {
        return new StringBuilder().
                append(getCalendarDatePattern()).
                append(messages.getMessage(getCalendarDatetimeSeparatorKey(), null, " ", getLocale())).
                append(getCalendarTimePattern()).
                toString();
    }

    public DateFormat getDateFormat() {
        String pattern = getDatePattern();
        return createFormat(pattern);
    }

    @Override
    public String getDatePattern() {
        return messages.getMessage(getDatePatternKey(), null, getLocale());
    }

    protected SimpleDateFormat createFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(isLenientFormats());
        return format;
    }

    public DateFormat getDatetimeFormat() {
        String pattern = getDatetimePattern();
        return createFormat(pattern);
    }

    @Override
    public String getDatetimePattern() {
        return messages.getMessage(getDatetimePatternKey(), null, getLocale());
    }

    public DateFormat getTimeFormat() {
        String pattern = messages.getMessage(getTimePatternKey(), null, getLocale());
        return createFormat(pattern);
    }

    @Override
    public String getTimePattern() {
        return messages.getMessage(getTimePatternKey(), null, getLocale());
    }

    public String getCalendarDatePatternKey() {
        return calendarDatePatternKey;
    }

    public void setCalendarDatePatternKey(String calendarDatePatternKey) {
        this.calendarDatePatternKey = calendarDatePatternKey;
    }

    public String getCalendarDatetimeSeparatorKey() {
        return calendarDatetimeSeparatorKey;
    }

    public void setCalendarDatetimeSeparatorKey(String calendarDatetimeSeparatorKey) {
        this.calendarDatetimeSeparatorKey = calendarDatetimeSeparatorKey;
    }

    public String getDatePatternKey() {
        return datePatternKey;
    }

    public void setDatePatternKey(String datePatternKey) {
        this.datePatternKey = datePatternKey;
    }

    public String getDatetimePatternKey() {
        return datetimePatternKey;
    }

    public void setDatetimePatternKey(String datetimePatternKey) {
        this.datetimePatternKey = datetimePatternKey;
    }

    public String getTimePatternKey() {
        return timePatternKey;
    }

    public void setTimePatternKey(String timePatternKey) {
        this.timePatternKey = timePatternKey;
    }

    public MessageSource getMessages() {
        return messages;
    }

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    protected Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public boolean isLenientFormats() {
        return lenientFormats;
    }

    public void setLenientFormats(boolean lenientFormats) {
        this.lenientFormats = lenientFormats;
    }

    public String getCalendarTimePatternKey() {
        return calendarTimePatternKey;
    }

    public void setCalendarTimePatternKey(String calendarTimePatternKey) {
        this.calendarTimePatternKey = calendarTimePatternKey;
    }
}
