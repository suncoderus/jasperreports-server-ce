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
package com.jaspersoft.jasperserver.war.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaspersoft.jasperserver.api.common.properties.Log4jSettingsService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.jaspersoft.jasperserver.api.common.properties.Log4jPropertyChanger;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class LogSettingsController implements Controller {
    private Log4jSettingsService log4jSettingsService;

    protected PropertiesManagementService propertiesManagementService;

    public void setLog4jSettingsService(Log4jSettingsService log4jSettingsService) {
        this.log4jSettingsService = log4jSettingsService;
    }

    public void setPropertiesManagementService(PropertiesManagementService propertiesManagementService) {
        this.propertiesManagementService = propertiesManagementService;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String loggerArg = request.getParameter("logger");
        String levelArg = request.getParameter("level");

        // check if we're getting an update
        if (loggerArg != null && levelArg != null) {
            propertiesManagementService.setProperty(Log4jPropertyChanger.PROPERTY_PREFIX + loggerArg, levelArg);
        }

        ModelAndView mav = new ModelAndView("modules/administer/logSettings");
        mav.addObject("loggers", getLoggers());
        mav.addObject("loggerDesc", log4jSettingsService.getLoggerDescriptionsByLocale(LocaleContextHolder.getLocale()));
        return mav;
    }

    private Map<String, String> getLoggers() {
        Map<String, String> loggers = new HashMap<String, String>();
        loggers.putAll(log4jSettingsService.getLoggers());
        for (Map.Entry<String,String> entry : (Set<Map.Entry<String,String>>)(propertiesManagementService.entrySet())) {
            String key = entry.getKey();
            if (key.startsWith(Log4jPropertyChanger.PROPERTY_PREFIX)) {
                loggers.put(Log4jPropertyChanger.parseKey(key), entry.getValue());
            }
        }
        return loggers;
    }

}

