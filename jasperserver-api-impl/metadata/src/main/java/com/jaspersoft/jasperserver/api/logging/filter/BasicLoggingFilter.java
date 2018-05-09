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
package com.jaspersoft.jasperserver.api.logging.filter;

import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public abstract class BasicLoggingFilter implements Filter {

    private static final Log log = LogFactory.getLog(BasicLoggingFilter.class);

    private LoggingContextProvider loggingContextProvider;
    private Exception originalException = null;

    public void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        this.loggingContextProvider = loggingContextProvider;
    }

    protected abstract void beforeChain(ServletRequest request) throws ServletException;

    protected abstract void logException(ServletRequest request, Exception filterException) throws ServletException;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            beforeChain(request);
            chain.doFilter(request, response);
        } catch (IOException e) {
            originalException = e;
            throw e;
        } catch (ServletException e) {
            originalException = e;
            throw e;
        } catch (RuntimeException e) {
            originalException = e;
            throw e;
        } finally {
            try {
                logException(request, originalException);
                originalException = null;
                loggingContextProvider.flushContext();
            } catch (Exception e) {
                log.error("Exception during flushing audit context", e);
            }
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}
