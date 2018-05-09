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
package com.jaspersoft.jasperserver.api.security.csrf;

import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * CrossDomainCommunicationFilter validates that the ajax requests from visualize are coming from a white listed domain or from JRS domain.
 * Server whitelist is only used for an unauthenticated session.  Upon authentication, tenant and user profile attrib. lists are used.
 *
 *  CrossDomainCommunicationFilter compares the remote and local domain header values in the request.  It also matches the
 *  remote header value to the whitelist regex. If the remote domains doesn't match, the requests are potentially coming
 *  from an attack riding the user's session: 401 error is returned.
 *
 * @author dlitvak
 * @version $Id$
 */
public class CrossDomainCommunicationFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(CrossDomainCommunicationFilter.class);
    private static final String SESS_XDM_WHITELIST = "SESS_XDM_WHITELIST";

    public static final String XDM_LOCAL = "XDM_LOCAL";
    public static final String X_REMOTE_DOMAIN = "X-Remote-Domain";
    public static final String X_LOCAL_DOMAIN = "X-Local-Domain";

    private DomainWhitelistProvider whitelistProvider;

    public void setWhitelistProvider(DomainWhitelistProvider xdmWhitelistProvider) {
        this.whitelistProvider = xdmWhitelistProvider;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String remoteDomain = httpRequest.getHeader(X_REMOTE_DOMAIN);
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authenticationToken.getPrincipal();

        // remoteDomain != null - a visualize/easyXDM request
        if (remoteDomain != null && authenticationToken.isAuthenticated() && principal instanceof UserDetails
               && !((UserDetails) principal).getUsername().equalsIgnoreCase("anonymousUser")) {
            Pattern whitelistPattern;
            HttpSession session = httpRequest.getSession();

            // user is authenticated. Get her whitelist
            Object authWhitelist = session.getAttribute(SESS_XDM_WHITELIST);
            if (authWhitelist != null)
                whitelistPattern = (Pattern) authWhitelist;
            else {
                whitelistPattern = whitelistProvider.getWhitelistPattern(principal);
                session.setAttribute(SESS_XDM_WHITELIST, whitelistPattern);
            }

            if (logger.isDebugEnabled())
                logger.debug("Whitelist for user " + ((MetadataUserDetails) principal).getUsername() + ": " + whitelistPattern);

            Pattern localPattern = (Pattern) session.getAttribute(XDM_LOCAL);
            if (localPattern == null) {
                final String localDomain = httpRequest.getHeader(X_LOCAL_DOMAIN);
                localPattern = Pattern.compile("^" + localDomain + "$", Pattern.CASE_INSENSITIVE);
                session.setAttribute(XDM_LOCAL, localPattern);
            }

            // allow at least the JasperReports Server domain (as seen by xdm.remote.page.js), if the whitelist does not match
            if (!localPattern.matcher(remoteDomain).matches() &&
                    (whitelistPattern != null && !whitelistPattern.matcher(remoteDomain).matches())) {

                logger.error("Potential cross-domain attack. Request origin domain: " + remoteDomain + "; local pattern: " + localPattern);

                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Domain " + remoteDomain + " forbidden.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
