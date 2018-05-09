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
package com.jaspersoft.jasperserver.api.common.properties;

import com.jaspersoft.jasperserver.api.common.util.diagnostic.LoggerLevelChangeInitiator;
import com.jaspersoft.jasperserver.api.common.util.diagnostic.LoggerLevelChanger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Log4j changer manages log4j configuration.
 * it assumes a "log4j." prefix for all logger names.
 * @author udavidovich
 * @version $Id$
 */
@Component
public class Log4jPropertyChanger extends PropertyChangerAdapter {
	public final static String PROPERTY_PREFIX = "log4j.";
    private static final Log log = LogFactory.getLog(Log4jPropertyChanger.class);

    private Log4jSettingsService log4jSettingsService;

    @Autowired(required = false)
    @Qualifier("logVerbosityManager")
    private LoggerLevelChanger loggerLevelChanger;

    public void setLog4jSettingsService(Log4jSettingsService log4jSettingsService) {
        this.log4jSettingsService = log4jSettingsService;
    }

    @Override
	public void setProperty(String key, String value) {
        log.debug("setting log4j property: " + key + " - " + value);
        key=parseKey(key);
        Logger log = Logger.getLogger(key);
        Level level = Level.toLevel(value);
        if (loggerLevelChanger == null) {
            // CE version
            log.setLevel(level);
        } else {
            // PRO version
            loggerLevelChanger.setLevel(log, level, LoggerLevelChangeInitiator.SERVER_SETTINGS);
        }
	}

    @Override
	public String getProperty(String key) {
        key=parseKey(key);
        Logger log = Logger.getLogger(key);
        return log.getEffectiveLevel().toString();
        //effective level can be inherited from a parent
	}

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> propertiesMap = new LinkedHashMap<String, String>();
        for (String key : log4jSettingsService.getLoggers().keySet()) {
            propertiesMap.put(PROPERTY_PREFIX+key, log4jSettingsService.getLoggers().get(key));
        }
        return propertiesMap;
    }

    public static String parseKey(String key) {
		assert (key.startsWith(PROPERTY_PREFIX));
		return key.substring(PROPERTY_PREFIX.length());
	}
}

