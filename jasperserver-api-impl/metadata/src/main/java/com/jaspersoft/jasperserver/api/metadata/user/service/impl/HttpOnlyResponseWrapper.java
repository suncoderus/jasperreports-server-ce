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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author vsabadosh
 * @version $Id:$
 */
public class HttpOnlyResponseWrapper extends HttpServletResponseWrapper {

    private static SimpleDateFormat cookieFormat = new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss zzz", Locale.ENGLISH);

    public HttpOnlyResponseWrapper(HttpServletResponse res) {
       super(res);
       cookieFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void addCookie(Cookie cookie) {
    StringBuffer header = new StringBuffer();
    if ((cookie.getName() != null) && (!cookie.getName().equals(""))) {
        header.append(cookie.getName());
    }
    if (cookie.getValue() != null) {
         // Empty values allowed for deleting cookie
         header.append("=" + cookie.getValue());
    }
    if (cookie.getVersion() == 1) {
        header.append(";Version=1");
        if (cookie.getComment() != null) {
            header.append(";Comment=\"" + cookie.getComment() + "\"");
        }
        if (cookie.getMaxAge() > -1) {
            header.append(";Max-Age=" + cookie.getMaxAge());
        }
    } else {
        if (cookie.getMaxAge() > -1) {
            Date now = new Date();
            now.setTime(now.getTime() + (1000L * cookie.getMaxAge()));
            header.append(";Expires=" + HttpOnlyResponseWrapper.cookieFormat.format(now));
        }
    }
    if (cookie.getDomain() != null) {
        header.append(";Domain=" + cookie.getDomain());
    }
    if (cookie.getPath() != null) {
        header.append(";Path=" + cookie.getPath());
    }
    if (cookie.getSecure()) {
        header.append(";Secure");
    }
    header.append(";HttpOnly");
    addHeader("Set-Cookie", header.toString());
    }

}