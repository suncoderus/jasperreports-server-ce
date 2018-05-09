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

package com.jaspersoft.jasperserver.war.ftpserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * @author asokolnikov
 */
public class JSFtpLet extends DefaultFtplet {

    public static final String SECURITY_CONTEXT = "securityContext";

    private static Log log = LogFactory.getLog(JSFtpLet.class);

    @Override
    public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute(SECURITY_CONTEXT, securityContext);

        printState(session, "onLogin");
        return super.onLogin(session, request);
    }

    @Override
    public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {

        SecurityContext securityContext = ((SecurityContext) session.getAttribute(SECURITY_CONTEXT));
        if (securityContext == null) {
            SecurityContextHolder.clearContext();
        } else {
            SecurityContextHolder.setContext(securityContext);
        }

        String cmd = request.getCommand();
        if (request.getArgument() != null) {
            cmd += " " + request.getArgument();
        }
        printState(session, cmd);
        return super.beforeCommand(session, request);
    }

    private void printState(FtpSession session, String command) {
        String thread = Thread.currentThread().toString();
        Object principle = null;
        try {
            principle = ((SecurityContext) session.getAttribute(SECURITY_CONTEXT)).getAuthentication().getPrincipal();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        String pr = (principle == null) ? "null" : principle.toString();
        log.trace("**** Thread : " + thread + ", principle : " + pr + ", command : " + command);
    }
}
