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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.cache;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class CacheControlHelper {

    public static Integer ONE_DAY = 86400;


    public static Response.ResponseBuilder enableLocaleAwareStaticCache(Response.ResponseBuilder builder){
        CacheControl control = new CacheControl();
        control.setNoCache(false);
        control.setMaxAge(ONE_DAY);
        return builder.cacheControl(control).header(HttpHeaders.VARY, HttpHeaders.ACCEPT_LANGUAGE);
    }

}
