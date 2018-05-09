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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves input control label from resource bundles or from message source
 * in the following precedence: report-specific bundles, server-wide bundles.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class InputControlLabelResolver {
    private static Pattern LABEL_KEY_PATTERN = Pattern.compile("^\\$R\\{(.+)\\}(.*)$");

    /**
     * Resolves the input control label by specified message key.
     *
     * @param key                 the message key.
     * @param reportMessageSource the report message source.
     * @param serverMessageSource the server message source.
     * @return resolved label.
     */
    public static String resolve(String key, ResourceBundle reportMessageSource, MessageSource serverMessageSource) {
        return resolve(key, new ResourceBundleLabelResolverAdapter(reportMessageSource),
                new MessageSourceLabelResolverAdapter(serverMessageSource));
    }

    /**
     * Resolves the input control label by specified message key.
     *
     * @param key                 the message key.
     * @param reportMessageSource the report message source.
     * @param serverMessageSource the server message source.
     * @return resolved label.
     */
    public static String resolve(String key, MessageSource reportMessageSource, MessageSource serverMessageSource) {
        return resolve(key, new MessageSourceLabelResolverAdapter(reportMessageSource),
                new MessageSourceLabelResolverAdapter(serverMessageSource));
    }

    private static String resolve(String key, LabelResolver reportLabelResolver, LabelResolver serverLabelResolver) {
        if (key == null) {
            return null;
        }

        String label = key;

        Matcher m = LABEL_KEY_PATTERN.matcher(key);
        if (m.matches()) {
            // matches $R{<supermart.store.store_sqft.label>} is Between
            String labelKey = m.group(1);
            // matches $R{supermart.store.store_sqft.label}< is Between>
            String labelPostfix = m.group(2);

            boolean messageFound = false;
            try {
                label = reportLabelResolver.getLabel(labelKey);
                messageFound = true;
            } catch (Exception ex) { /* No message. Ok, not a prob... */ }

            if (!messageFound) {
                try {
                    label = serverLabelResolver.getLabel(labelKey);
                    messageFound = true;
                } catch (Exception ex) { /* No message. Ok, not a prob... */ }
            }

            if (messageFound) {
                label += labelPostfix;
            }
        }

        return label;
    }

    static interface LabelResolver {
        String getLabel(String key);
    }

    static class MessageSourceLabelResolverAdapter implements LabelResolver {
        private MessageSource messageSource;

        MessageSourceLabelResolverAdapter(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @Override
        public String getLabel(String key) {
            if (messageSource != null) {
                return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
            } else {
                throw new NoSuchMessageException(key);
            }
        }
    }

    static class ResourceBundleLabelResolverAdapter implements LabelResolver {
        private ResourceBundle resourceBundle;

        ResourceBundleLabelResolverAdapter(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }

        @Override
        public String getLabel(String key) {
            if (resourceBundle != null) {
                return resourceBundle.getString(key);
            } else {
                throw new MissingResourceException(
                    String.format("Can't find resource for bundle %s, key %s", this.getClass().getName(), key),
                    this.getClass().getName(), key);
            }
        }
    }
}
