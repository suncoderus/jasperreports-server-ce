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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid;

import org.teiid.logging.MessageLevel;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class LogConfig {

    String logContext;
    String logLevel = Labels.NONE;

    public String getLogContext() {
        return logContext;
    }

    public void setLogContext(String logContext) {
        this.logContext = logContext;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public int getMessageLevel() {
        for (int i = MessageLevel.getValidLowerMessageLevel(); i <= MessageLevel.getValidUpperMessageLevel(); i++) {
            if (MessageLevel.getLabelForLevel(i).equalsIgnoreCase(logLevel)) return i;
        }
        return MessageLevel.NONE;
    }

    /**
     * Constants that define the types of the messages that are to be recorded
     * by the LogManager.
     */
    public static class Labels {
        public static final String CRITICAL     = "CRITICAL";
        public static final String ERROR        = "ERROR";
        public static final String WARNING      = "WARNING";
	    public static final String INFO         = "INFO";
	    public static final String DETAIL       = "DETAIL";
        public static final String TRACE        = "TRACE";
        public static final String NONE         = "NONE";
    }
}
