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
package com.jaspersoft.jasperserver.api.logging.access.context.impl;

import com.jaspersoft.jasperserver.api.common.util.ExportRunMonitor;
import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.logging.access.context.AccessContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessContextImpl implements AccessContext {

    private LoggingContextProvider loggingContextProvider;
    private ResourceFactory clientClassFactory;
    private UserAuthorityService userAuthorityService;

    public void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        this.loggingContextProvider = loggingContextProvider;
    }

    public void setClientClassFactory(ResourceFactory clientClassFactory) {
        this.clientClassFactory = clientClassFactory;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    protected String getContextUsername() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationToken == null) {
            return null;
        }

        if (authenticationToken.getPrincipal() instanceof UserDetails) {
            UserDetails contextUserDetails = (UserDetails) authenticationToken.getPrincipal();
            return contextUserDetails.getUsername();
        } else if (authenticationToken.getPrincipal() instanceof String) {
            return (String) authenticationToken.getPrincipal();
        } else {
            return null;
        }
    }

    protected User getUserFromSecurityContext() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationToken == null) {
            return null;
        }

        if (authenticationToken.getPrincipal() instanceof User) {
            return (User)authenticationToken.getPrincipal();
        } else {
            return null;
        }
    }

    protected User getContextUser() {
        User user = getUserFromSecurityContext();
        if (user == null) {
            String username = getContextUsername();
            if (username == null) {
                return null;
            }
            user = userAuthorityService.getUser(null, username);
        }

        return user;
    }

    public void doInAccessContext(AccessContextCallback callback) {
        // Generating access events during export/import is pointless and should be disabled
        boolean noExportImport = !ExportRunMonitor.isExportRun() && !ImportRunMonitor.isImportRun();

        if (loggingContextProvider.isLoggingEnabled(AccessEvent.class) && callback != null && noExportImport) {
            User user = getContextUser();
            if (user != null) {
                AccessEvent accessEvent = (AccessEvent)clientClassFactory.newObject(AccessEvent.class);
                accessEvent.setEventDate(new Date());
                accessEvent.setUser(user);
                callback.fillAccessEvent(accessEvent);
                loggingContextProvider.getContext().logEvent(accessEvent);
            }
        }
    }
}