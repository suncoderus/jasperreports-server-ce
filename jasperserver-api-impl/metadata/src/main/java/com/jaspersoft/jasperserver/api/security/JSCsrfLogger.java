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
package com.jaspersoft.jasperserver.api.security;

import org.apache.log4j.Logger;
import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.log.ILogger;
import org.owasp.csrfguard.log.LogLevel;

/**
 * Wrap log4j for CsrfGuard
 *
 * @author Anton Fomin
 * @version $Id$
 */
public class JSCsrfLogger implements ILogger {

    private Logger log = Logger.getLogger(CsrfGuard.class);

    public void log(String msg) {
        log.info(msg);
    }

    public void log(LogLevel level, String msg) {
        if (LogLevel.Error.equals(level)) {
            log.error(msg);
        } else if (LogLevel.Fatal.equals(level)) {
            log.fatal(msg);
        } else if (LogLevel.Debug.equals(level)) {
            log.debug(msg);
        } else if (LogLevel.Trace.equals(level)) {
            log.trace(msg);
        } else if (LogLevel.Warning.equals(level)) {
            log.warn(msg);
        } else if (LogLevel.Info.equals(level)) {
            log.info(msg);
        } else {
            log.info(msg);
        }
    }

    public void log(Exception exception) {
        log.error(String.valueOf(exception));
    }

    public void log(LogLevel level, Exception exception) {
        log(level, String.valueOf(exception));
    }
}
