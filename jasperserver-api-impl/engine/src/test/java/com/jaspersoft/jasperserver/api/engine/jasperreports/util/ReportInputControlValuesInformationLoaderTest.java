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

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
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
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link ReportInputControlValuesInformationLoader}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class ReportInputControlValuesInformationLoaderTest extends UnitilsJUnit4 {

    @Test
    public void ensureValuesInfoResolvedIfKeyPresentInMessageSource() throws Exception {
        MessageSource messageSource = mockMessageSource(map(entry("key", "value")));
        final ListOfValues listOfValues = mockListOfValues(getLOVI("$R{key}", "tttt"));
        ReportInputControlValuesInformation info =
                ReportInputControlValuesInformationLoader.getReportInputControlValuesInformation(listOfValues, null, messageSource);

        assertNotNull(info);
        assertEquals("value", info.getInputControlValueInformation("$R{key}").getPromptLabel());
    }

    @Test
    public void ensureValuesInfoResolvedIfKeyPresentInResourceBundle() throws Exception {
        ResourceBundle reportResourceBundle = new ResourceBundleMock(map(entry("key", "value")));
        final ListOfValues listOfValues = mockListOfValues(getLOVI("$R{key}", "tttt"));
        ReportInputControlValuesInformation info =
                ReportInputControlValuesInformationLoader.getReportInputControlValuesInformation(listOfValues, reportResourceBundle, null);

        assertNotNull(info);
        assertEquals("value", info.getInputControlValueInformation("$R{key}").getPromptLabel());
    }

    @Test
    public void ensureValuesInfoResolvedIfNoMessagesPresent() throws Exception {
        MessageSource messageSource = mockMessageSource(map(entry("key1", "messageSourceValue")));
        ResourceBundle reportResourceBundle = new ResourceBundleMock(map(entry("key1", "reportBundleValue")));
        final ListOfValues listOfValues = mockListOfValues(getLOVI("$R{key}", "tttt"));
        ReportInputControlValuesInformation info =
                ReportInputControlValuesInformationLoader.getReportInputControlValuesInformation(listOfValues, reportResourceBundle, messageSource);

        assertNotNull(info);
        assertEquals("$R{key}", info.getInputControlValueInformation("$R{key}").getPromptLabel());
    }

    private ListOfValues mockListOfValues(ListOfValuesItem... items) {
        final ListOfValuesImpl listOfValues = new ListOfValuesImpl();
        for (ListOfValuesItem item: items) {
            listOfValues.addValue(item);
        }

        return listOfValues;
    }

    private ListOfValuesItem getLOVI(String label, String value) {
        final ListOfValuesItemImpl listOfValuesItem = new ListOfValuesItemImpl();
        listOfValuesItem.setLabel(label);
        listOfValuesItem.setValue(value);
        return listOfValuesItem;
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

}
