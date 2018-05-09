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

package com.jaspersoft.jasperserver.jaxrs.bundle;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;


/**
 *
 * @author Sergey.Prilukin, Zakhar.Tomcchenko
 * @version $Id
 */

public class ExposedResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
    private String basenamePrefix = "WEB-INF/bundles/";

    public Map<String, String> getAllMessagesForBaseName(String baseName, Locale locale) {
        Map<String, String> mergedProps = new HashMap<String, String>();
        List<String> filenames = calculateAllFilenames(basenamePrefix + baseName, locale);

        for (int j = filenames.size() - 1; j >= 0; j--) {
            PropertiesHolder propHolder = getProperties(filenames.get(j));
            if (propHolder.getProperties() != null) {
                mergedProps.putAll((Map) propHolder.getProperties());
            }
        }
        return mergedProps;
    }

    public String getBasenamePrefix() {
        return basenamePrefix;
    }

    public void setBasenamePrefix(String basenamePrefix) {
        this.basenamePrefix = basenamePrefix;
    }
}
