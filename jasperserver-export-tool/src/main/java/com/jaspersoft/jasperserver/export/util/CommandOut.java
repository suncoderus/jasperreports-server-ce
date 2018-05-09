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

package com.jaspersoft.jasperserver.export.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CommandOut {

	public static final String COMMAND_OUT_LOGGER = "com.jaspersoft.jasperserver.export.command";
	
	private static final CommandOut instance = new CommandOut(COMMAND_OUT_LOGGER);
	
	public static CommandOut getInstance() {
		return instance;
	}
	
	private final Log logger;

	
	protected CommandOut(String loggerName) {
		logger = LogFactory.getLog(loggerName);
	}

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Throwable e) {
        logger.warn(msg, e);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String msg, Throwable e) {
        logger.error(msg, e);
    }

    public void fatal(String msg) {
        logger.fatal(msg);
    }

    public void fatal(String msg, Throwable e) {
        logger.fatal(msg, e);
    }
	
}
