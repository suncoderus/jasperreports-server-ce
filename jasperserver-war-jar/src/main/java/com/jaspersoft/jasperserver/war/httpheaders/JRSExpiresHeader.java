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

package com.jaspersoft.jasperserver.war.httpheaders;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeaderValueParser;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Stas Chubar
 */

public class JRSExpiresHeader implements Header {
    // All HTTP date/time stamps MUST be represented in Greenwich Mean Time (GMT), without exception.
    // See: http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.3.1
    public static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    public static final String HTTP_HEADER_DATE_FORMAT_PATTERN = "EEE, d MMM yyyy HH:mm:ss z";

    /**
     * DateFormat is unsafe so we will create one per thread and reuse it.
     */
    private static final ThreadLocal<DateFormat> threadLocalFormatter =
            new ThreadLocal<DateFormat>();

    private long maxAge = 0;

    public JRSExpiresHeader() {
    }

    public JRSExpiresHeader(long maxAge) {
        this.maxAge = maxAge;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public String getName() {
        return HttpHeaders.EXPIRES;
    }

    @Override
    public String getValue() {
        return getFormat().format(getDate());
    }

    public Date getDate() {
        return new Date(getCurrentDate().getTime() + maxAge * 1000);
    }

    @Override
    public HeaderElement[] getElements() throws ParseException {
        String value = getValue();
        if (value != null) {
            // result intentionally not cached, it's probably not used again
            return BasicHeaderValueParser.parseElements(value, null);
        } else {
            return new HeaderElement[0];
        }
    }

    Date getCurrentDate() {
        return new Date();
    }

    /**
     * We will use new instance per each thread
     * @return
     */
    private DateFormat getFormat() {
        DateFormat dateFormat  = threadLocalFormatter.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(HTTP_HEADER_DATE_FORMAT_PATTERN, Locale.ENGLISH);
            dateFormat.setTimeZone(GMT_ZONE);

            threadLocalFormatter.set(dateFormat);
        }
        return dateFormat;
    }
}
