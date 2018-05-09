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
package com.jaspersoft.jasperserver.remote.settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DateTimeSettingsProvider implements SettingsProvider {

    private Pattern datePickerSettingsPattern;
    private Pattern timePickerSettingsPattern;
    private Integer datePickerSettingsPatternGroup;
    private Integer timePickerSettingsPatternGroup;
    private String datePickerDefaultLocale;
    private String datePickerSettingsPathTemplate;
    private String timePickerSettingsPathTemplate;
    private Map<String, String> datePickerPropertiesMapping;
    private Map<String, String> timePickerPropertiesMapping;
    private Boolean enableCache;
    private MessageSource messageSource;

    private Map<String, Map<String, Object>> settingsCache = new ConcurrentHashMap<String, Map<String, Object>>();

    public void setDatePickerSettingsPattern(Pattern datePickerSettingsPattern) {
        this.datePickerSettingsPattern = datePickerSettingsPattern;
    }

    public void setTimePickerSettingsPattern(Pattern timePickerSettingsPattern) {
        this.timePickerSettingsPattern = timePickerSettingsPattern;
    }

    public void setDatePickerSettingsPatternGroup(Integer datePickerSettingsPatternGroup) {
        this.datePickerSettingsPatternGroup = datePickerSettingsPatternGroup;
    }

    public void setTimePickerSettingsPatternGroup(Integer timePickerSettingsPatternGroup) {
        this.timePickerSettingsPatternGroup = timePickerSettingsPatternGroup;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }

    public void setDatePickerPropertiesMapping(Map<String, String> datePickerPropertiesMapping) {
        this.datePickerPropertiesMapping = datePickerPropertiesMapping;
    }

    public void setDatePickerSettingsPathTemplate(String datePickerSettingsPathTemplate) {
        this.datePickerSettingsPathTemplate = datePickerSettingsPathTemplate;
    }

    public void setTimePickerSettingsPathTemplate(String timePickerSettingsPathTemplate) {
        this.timePickerSettingsPathTemplate = timePickerSettingsPathTemplate;
    }

    public void setDatePickerDefaultLocale(String datePickerDefaultLocale) {
        this.datePickerDefaultLocale = datePickerDefaultLocale;
    }

    public void setTimePickerPropertiesMapping(Map<String, String> timePickerPropertiesMapping) {
        this.timePickerPropertiesMapping = timePickerPropertiesMapping;
    }

    @Override
    public Object getSettings() {
        String locale = getLocale();

        Map<String, Object> result = settingsCache.get(locale);
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put("datepicker", getDatePickerSettings(locale));
            result.put("timepicker", getTimePickerSettings(locale));

            if (enableCache) {
                settingsCache.put(locale, result);
            }
        }

        return result;
    }

    private String getLocale() {
        return LocaleContextHolder.getLocale().toString().replaceAll("_", "-");
    }

    /* DatePicker settings */

    private Map<String, Object> getPickerSettings(
            String locale, String settingsPathTemplate, Pattern settingsPattern,
            Integer patternGroup, Map<String, String> propsMapping) {

        String content = getSettingsContentOrDefault(settingsPathTemplate, locale);
        String settingsJSON = getSettingsFromFileContent(settingsPattern, content,
                patternGroup);
        Map<String, Object> result = convertToJSONObject(settingsJSON);
        customizeSettings(propsMapping, result);

        return result;
    }

    private Map<String, Object> getDatePickerSettings(String locale) {
        return getPickerSettings(locale, datePickerSettingsPathTemplate,
                datePickerSettingsPattern, datePickerSettingsPatternGroup, datePickerPropertiesMapping);
    }

	/* TimePicker settings */

    private Map<String, Object> getTimePickerSettings(String locale) {
        return getPickerSettings(locale, timePickerSettingsPathTemplate,
                timePickerSettingsPattern, timePickerSettingsPatternGroup, timePickerPropertiesMapping);
    }

    private InputStream getPathAsStream(String path) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        ServletContext servletContext = request.getSession().getServletContext();
        return servletContext.getResourceAsStream(path);
    }

    private String getSettingsContentOrDefault(String template, String locale) {
        String settingsFile = String.format(template, locale);
        String settingsContent = getSettingsContent(settingsFile);
        if (settingsContent == null) {
            settingsContent = getSettingsContent(String.format(template, datePickerDefaultLocale));
            if (settingsContent == null) {
                throw new RuntimeException("No default settings present");
            }
        }

        return settingsContent;
    }

    private String getSettingsContent(String path) {
        InputStream stream = getPathAsStream(path);
        if (stream == null) {
            return null;
        }

        try {
            return IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSettingsFromFileContent(Pattern pattern, String content, Integer patternGroup) {
        Matcher regexMatcher = pattern.matcher(content);
        if (regexMatcher.find()) {
            return regexMatcher.group(patternGroup);
        } else {
            throw new RuntimeException("Settings has incorrect format: " + content);
        }
    }

    private Map<String, Object> convertToJSONObject(String jsonAsString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() { });
        } catch (IOException e) {
            throw new RuntimeException("Error during parsing JSON. Invalid content" + jsonAsString);
        }
    }

    private void customizeSettings(Map<String, String> mapping, Map<String, Object> settings) {
        Locale locale = LocaleContextHolder.getLocale();

        for (Map.Entry<String, String> entry: mapping.entrySet()) {
            String propertyValue = messageSource.getMessage(entry.getValue(),
                    new Object[] {}, locale);
            settings.put(entry.getKey(), propertyValue);
        }
    }
}
