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
package com.jaspersoft.jasperserver.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * This class is used to override HttpServeletRequest so that it can return TolerantHttpSession instead of HttpSession
 *
 * Created by nthapa on 7/12/13.
 */
public class TolerantRequest extends HttpServletRequestWrapper {

    public TolerantRequest(HttpServletRequest obj)
    {
        super(obj);
    }

    @Override
    public HttpSession getSession()
    {
       return getSession(true);

    }

    /*
    * Overrides the getSession method of HttpServletRequestWrapper
    *
    *return null if HttpSession is null else returns TolerantHttpSession
    *
     */
    @Override
    public HttpSession getSession(boolean create)
    {
        HttpSession session = super.getSession(create);
        if (session != null) {
            return new TolerantHttpSession(session);
        } else {
            return session;
        }

    }
}
