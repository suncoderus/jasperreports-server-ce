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
package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.common.properties.Log4jPropertyChanger;
import com.jaspersoft.jasperserver.api.common.properties.Log4jSettingsService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages and formats the Diagnostic data related to Log Settings configured via
 * the web UI
 *
 * @author vsabadosh
 * @since 5.0
 */
public class LogSettingsDiagnosticService implements Diagnostic {
    private PropertiesManagementService propertiesManagementService;
    private Log4jSettingsService log4jSettingsService;
    private MessageSource messageSource;

    public void setPropertiesManagementService(PropertiesManagementService propertiesManagementService) {
        this.propertiesManagementService = propertiesManagementService;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setLog4jSettingsService(Log4jSettingsService log4jSettingsService) {
        this.log4jSettingsService = log4jSettingsService;
    }

    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
    return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.LOG_SETTINGS, new DiagnosticCallback<Map<String, String>>() {
                @Override
                public Map<String, String> getDiagnosticAttributeValue() {
                    //Merge log settings with values from Global Properties List
                    Map<String, String> loggers = log4jSettingsService.getLoggers();
                    for (Map.Entry<String,String> entry : (Set<Map.Entry<String,String>>)(propertiesManagementService.entrySet())) {
                        String key = entry.getKey();
                        if (key.startsWith(Log4jPropertyChanger.PROPERTY_PREFIX)) {
                            loggers.put(Log4jPropertyChanger.parseKey(key), entry.getValue());
                        }
                    }
                    return generateLogSettingsWithDescription(loggers);
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.GLOBAL_PROPERTIES_LIST, new DiagnosticCallback<Map<String, String>>() {
                @Override
                public Map<String, String> getDiagnosticAttributeValue() {
                    //Merge log settings with values from Global Properties List
                    Map<String, String> globalPropertiesList = new HashMap<String, String>();
                    for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) (propertiesManagementService.entrySet())) {
                        //TODO: Maybe need to add descriptions for it too.
                        globalPropertiesList.put(entry.getKey(), entry.getValue());
                    }
                    return globalPropertiesList;
                }
            }).build();
    }

    /**
     * Returns map of log setting values which combine description and log level.
     */
    private Map<String, String> generateLogSettingsWithDescription(Map<String, String> loggers) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> updatedLoggersWithDesc = new HashMap<String, String>();

        for (String loggerKey : loggers.keySet()) {
            String description = messageSource.getMessage(loggerKey, null, "", locale);
            if (description == null || description.isEmpty()) {
                description = "";
            } else {
                description = " (" + description + ")";
            }
            updatedLoggersWithDesc.put(loggerKey, loggers.get(loggerKey) + description);
        }
        return updatedLoggersWithDesc;
    }

}
