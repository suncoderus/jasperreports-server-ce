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
package com.jaspersoft.jasperserver.war;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.LocaleUtils;
import org.springframework.web.servlet.LocaleResolver;

import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JSSessionLocaleResolver implements LocaleResolver
{
	public Locale resolveLocale(HttpServletRequest request)
	{
		String sessionAttribute = JasperServerConstImpl.getUserLocaleSessionAttr();

        Locale locale =null;
        try {
            locale= (Locale)request.getSession().getAttribute(sessionAttribute);
        }
        catch(Exception e){}
        finally {


            if (locale == null) {
                //Try to get locale from cookies for login page, see bug #30500
                locale = getLocaleFromCookies(request);
            }

            if (locale == null) {
                locale = request.getLocale();
            }
        }
        return locale;
	}

    private Locale getLocaleFromCookies(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JasperServerConstImpl.getUserLocaleSessionAttr())) {
                    if (cookie.getValue() != null && cookie.getValue().length() > 0) {
                        return LocaleUtils.toLocale(cookie.getValue());
                    }

                    break;
                }
            }
        }

        return null;
    }

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale)
	{
		String sessionAttribute = JasperServerConstImpl.getUserLocaleSessionAttr();
		request.getSession().setAttribute(sessionAttribute, locale);
	}
}
