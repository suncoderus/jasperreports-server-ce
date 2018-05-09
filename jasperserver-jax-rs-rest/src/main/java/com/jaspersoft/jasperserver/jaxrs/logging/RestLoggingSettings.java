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
package com.jaspersoft.jasperserver.jaxrs.logging;

import java.util.logging.Level;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class RestLoggingSettings {
    private String loggerName;
    private Level level;
    private String verbosity;
    private Integer maxEntitySize;

    public String getLoggerName() {
        return loggerName;
    }

    public RestLoggingSettings setLoggerName(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    public RestLoggingSettings setLevel(Level level) {
        this.level = level;
        return this;
    }

    public String getVerbosity() {
        return verbosity;
    }

    public RestLoggingSettings setVerbosity(String verbosity) {
        this.verbosity = verbosity;
        return this;
    }

    public Integer getMaxEntitySize() {
        return maxEntitySize;
    }

    public RestLoggingSettings setMaxEntitySize(Integer maxEntitySize) {
        this.maxEntitySize = maxEntitySize;
        return this;
    }
}
