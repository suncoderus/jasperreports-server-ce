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
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Robert Matei (robert.matei@geminisols.ro)
 * @version $Id$
 */
public class DefaultCalendarFormatProvider implements CalendarFormatProvider, Serializable {

    private boolean lenientFormats;

    public String getCalendarDatePattern() {
        String pattern = getDateFormatPattern();
        pattern = pattern.replaceAll("(?i)m+", "%m");
        pattern = pattern.replaceAll("(?i)d+", "%d");
        pattern = pattern.replaceAll("(?i)y+", "%Y");
        return pattern;
    }

    public String getCalendarDatetimePattern() {
        String pattern = getDatetimeFormatPattern();
        pattern = pattern.replaceAll("d+", "%d");
        pattern = pattern.replaceAll("M+", "%m");
        pattern = pattern.replaceAll("y+", "%Y");
        pattern = pattern.replaceAll("H+", "%H");
        pattern = pattern.replaceAll("h+", "%I");
        pattern = pattern.replaceAll("a+", "%p");
        pattern = pattern.replaceAll("(?<=^|[^%])m+", "%M");
        return pattern;
    }

    public String getCalendarTimePattern() {
        return getCalendarDatetimePattern().replace(getCalendarDatePattern(), "");
    }

    public DateFormat getDateFormat() {
        String pattern = getDateFormatPattern();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(isLenientFormats());
        return dateFormat;
    }

    @Override
    public String getDatePattern() {
        return getDateFormatPattern();
    }

    public DateFormat getDatetimeFormat() {
        String pattern = getDatetimePattern();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(isLenientFormats());
        return dateFormat;
    }

    @Override
    public String getDatetimePattern() {
        return getDatetimeFormatPattern();
    }

    public DateFormat getTimeFormat() {
        String pattern = getTimeFormatPattern();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(isLenientFormats());
        return dateFormat;
    }

    public String getTimePattern() {
        return getTimeFormatPattern();
    }

    protected String getDatetimeFormatPattern() {
        SimpleDateFormat defaultFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, LocaleContextHolder.getLocale());
        String pattern = (defaultFormat).toPattern();
        pattern = pattern.replaceAll("d+", "dd");
        pattern = pattern.replaceAll("M+", "MM");
        pattern = pattern.replaceAll("y+", "yyyy");
        pattern = pattern.replaceAll("H+", "HH");
        pattern = pattern.replaceAll("h+", "hh");
        pattern = pattern.replaceAll("k+", "HH");
        pattern = pattern.replaceAll("K+", "hh");
        pattern = pattern.replaceAll("m+", "mm");
        pattern = pattern.replaceAll("m+", "ss");
        return pattern;
    }

    protected String getTimeFormatPattern() {
        SimpleDateFormat defaultFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, LocaleContextHolder.getLocale());
        String pattern = (defaultFormat).toPattern();
        pattern = pattern.replaceAll("H+", "HH");
        pattern = pattern.replaceAll("h+", "hh");
        pattern = pattern.replaceAll("k+", "HH");
        pattern = pattern.replaceAll("K+", "hh");
        pattern = pattern.replaceAll("m+", "mm");
        pattern = pattern.replaceAll("m+", "ss");
        return pattern;
    }

    protected String getDateFormatPattern() {
        SimpleDateFormat defaultFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, LocaleContextHolder.getLocale());
        String pattern = (defaultFormat).toPattern();
        pattern = pattern.replaceAll("(?i)d+", "dd");
        pattern = pattern.replaceAll("(?i)m+", "MM");
        pattern = pattern.replaceAll("(?i)y+", "yyyy");
        return pattern;
    }

    public boolean isLenientFormats() {
        return lenientFormats;
    }

    public void setLenientFormats(boolean lenientFormats) {
        this.lenientFormats = lenientFormats;
    }
}
