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

import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link InputControlLabelResolver}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class InputControlLabelResolverTest extends UnitilsJUnit4 {

    @Test
    public void ensureThatMessageFromReportSourceIsResolved() throws Exception {
        final String resourceKey = "$R{field}";
        final String key = "field";
        final String label = "Field";

        final Map<String, String> stringMap = map(entry(key, label));
        ResourceBundle resourceBundle = new ResourceBundleMock(stringMap);
        MessageSource messageSource = mockMessageSource(stringMap);

        String value1 = InputControlLabelResolver.resolve(resourceKey, resourceBundle, null);
        String value2 = InputControlLabelResolver.resolve(resourceKey, messageSource, null);
        assertEquals(label, value1);
        assertEquals(label, value2);
    }

    @Test
    public void ensureThatMessageFromApplicationSourceIsResolved() throws Exception {
        final String resourceKey = "$R{field}";
        final String key = "field";
        final String label = "Field";

        MessageSource messageSource = mockMessageSource(map(entry(key, label)));
        String value1 = InputControlLabelResolver.resolve(resourceKey, (ResourceBundle)null, messageSource);
        String value2 = InputControlLabelResolver.resolve(resourceKey, (MessageSource)null, messageSource);
        assertEquals(label, value1);
        assertEquals(label, value2);
    }

    @Test
    public void ensureThatMessageKeyIsKeptIfNoMessagesExists() throws Exception {
        final String key = "$R{field}";

        MessageSource messageSource = mockMessageSource(map());
        ResourceBundle resourceBundle = new ResourceBundleMock(map());

        String value1 = InputControlLabelResolver.resolve(key, (ResourceBundle)null, null);
        String value2 = InputControlLabelResolver.resolve(key, (MessageSource)null, null);
        String value3 = InputControlLabelResolver.resolve(key, (ResourceBundle) null, messageSource);
        String value4 = InputControlLabelResolver.resolve(key, (MessageSource)null, messageSource);
        String value5 = InputControlLabelResolver.resolve(key, resourceBundle, null);
        String value6 = InputControlLabelResolver.resolve(key, messageSource, null);
        String value7 = InputControlLabelResolver.resolve(key, messageSource, messageSource);
        String value8 = InputControlLabelResolver.resolve(key, resourceBundle, messageSource);
        assertEquals(key, value1);
        assertEquals(key, value2);
        assertEquals(key, value3);
        assertEquals(key, value4);
        assertEquals(key, value5);
        assertEquals(key, value6);
        assertEquals(key, value7);
        assertEquals(key, value8);
    }

    @Test
    public void ensureThatMessageFromReportsHasPriorityOverMessagesFromApplication() throws Exception {
        final String key = "field";
        final String reportLabel = "reportField";
        final String applicationLabel = "Field";
        final String resourceKey = "$R{field}";

        final Map<String, String> reportStrings = map(entry(key, reportLabel));
        MessageSource reportMessageSource = mockMessageSource(reportStrings);
        ResourceBundle reportResourceBundle = new ResourceBundleMock(reportStrings);
        MessageSource applicationMessageSource = mockMessageSource(map(entry(key, applicationLabel)));

        String value1 = InputControlLabelResolver.resolve(resourceKey, reportMessageSource, applicationMessageSource);
        String value2 = InputControlLabelResolver.resolve(resourceKey, reportResourceBundle, applicationMessageSource);
        assertEquals(reportLabel, value1);
        assertEquals(reportLabel, value2);
    }

    @Test
    public void ensureThatCompositeMessagesResolvedProperly() throws Exception {
        final String key = "field";
        final String reportLabel = "reportField";
        final String reportLabelresolved = "reportField test";
        final String resourceKey = "$R{field} test";

        final Map<String, String> reportStrings = map(entry(key, reportLabel));
        MessageSource reportMessageSource = mockMessageSource(reportStrings);
        ResourceBundle reportResourceBundle = new ResourceBundleMock(reportStrings);

        String value1 = InputControlLabelResolver.resolve(resourceKey, reportMessageSource, null);
        String value2 = InputControlLabelResolver.resolve(resourceKey, reportResourceBundle, null);
        assertEquals(reportLabelresolved, value1);
        assertEquals(reportLabelresolved, value2);
    }

    @Test
    public void ensureThatCompositeMessagesResolvedProperlyWithEmptyBundles() throws Exception {
        final String resourceKey = "$R{field} test";

        final Map<String, String> reportStrings = map();
        MessageSource reportMessageSource = mockMessageSource(reportStrings);
        ResourceBundle reportResourceBundle = new ResourceBundleMock(reportStrings);

        String value1 = InputControlLabelResolver.resolve(resourceKey, reportMessageSource, null);
        String value2 = InputControlLabelResolver.resolve(resourceKey, reportResourceBundle, null);
        assertEquals(resourceKey, value1);
        assertEquals(resourceKey, value2);
    }

    private MessageSource mockMessageSource(final Map<String, String> strings) {
        Mock<MessageSource> messageSourceMock = MockUnitils.createMock(MessageSource.class);
        messageSourceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                String key = (String) proxyInvocation.getArguments().get(0);
                if (key == null) {
                    throw new NullPointerException();
                }

                if (!strings.containsKey(key)) {
                    throw new NoSuchMessageException(key);
                }

                return strings.get(key);
            }
        }).getMessage(null, null, (Locale)null);

        return messageSourceMock.getMock();
    }

    static class ResourceBundleMock extends ResourceBundle {
        private Map<String, String> strings;

        ResourceBundleMock(Map<String, String> strings) {
            this.strings = strings;
        }

        @Override
        protected Object handleGetObject(String key) {
            return strings.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return null;
        }
    }

    private Map.Entry<String, String> entry(String key, String value) {
        return new AbstractMap.SimpleEntry<java.lang.String, java.lang.String>(key, value);
    }

    private Map<String, String> map(Map.Entry<String, String>... strings) {
        Map<String, String> stringsMap = new HashMap<String, String>(strings.length);
        for (Map.Entry<String, String> entry: strings) {
            stringsMap.put(entry.getKey(), entry.getValue());
        }

        return stringsMap;
    }
}
