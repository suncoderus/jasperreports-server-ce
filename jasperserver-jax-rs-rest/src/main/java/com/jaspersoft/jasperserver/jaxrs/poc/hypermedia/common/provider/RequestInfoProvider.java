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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

/**
 * @author Igor.Nesterenko
 * @version $Id$
 */

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestInfoProvider {

    @Value("${deploy.base.url:}")
    private String deployBaseUrl;

    @Resource
    private HttpServletRequest request;

    public String getBaseUrl(){
        String result;

        if (deployBaseUrl != null  && !deployBaseUrl.isEmpty()){
            result = deployBaseUrl;
        }else {
            int serverPort = request.getServerPort();
            if (serverPort > 0)
                result = MessageFormat.format("{0}://{1}:{2}{3}",
                        request.getScheme(),
                        request.getServerName(),
                        String.valueOf(serverPort),
                        request.getContextPath());
            else
                result = MessageFormat.format("{0}://{1}{2}",
                        request.getScheme(),
                        request.getServerName(),
                        request.getContextPath());
        }

        if (result.charAt(result.length() - 1) != '/'){
            result += "/";
        }

        return result;
    }

    public int getLocalPort(){
        return request.getLocalPort();
    }

    public String getContextPath(){
        return request.getContextPath();
    }

    public boolean isSupportedDevice() {
        String userAgent = request.getHeader("user-agent");
        return userAgent.indexOf("iPad") == -1;
    }

}
