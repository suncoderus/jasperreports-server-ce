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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

public class AbstractTextDataSourceDefinitionTest {

    private Map<Object, Class<?>> getInput2ExpectedMap() {
        Map<Object, Class<?>> testData = new HashMap<Object, Class<?>>();

        testData.put(null, null);
        testData.put(new Object(), null);
        testData.put("", null);
        testData.put(".", null);
        testData.put(" ", null);

        testData.put("-", null);
        testData.put("+", null);

        testData.put("a", null);

        testData.put("0", Integer.class);
        testData.put("+0", Integer.class);
        testData.put("-0", Integer.class);

        testData.put("2147483647", Integer.class); // max int
        testData.put("+2147483647", Integer.class); // max int
        testData.put("-2147483647", Integer.class); // max int negative
        testData.put("2147483648", Long.class); // max int + 1

        testData.put("-2147483648", Integer.class); // min int
        testData.put("-2147483649", Long.class); // min int - 1

        testData.put("9223372036854775807", Long.class); // max long 
        testData.put("+9223372036854775807", Long.class); // max long
        testData.put("-9223372036854775807", Long.class); // max long negative
        testData.put("-9223372036854775808", Long.class); // min long

        testData.put("5.5", Double.class);
        testData.put("+5.5", Double.class);
        testData.put("-5.5", Double.class);

        testData.put(new Double(0), Double.class);
        testData.put(new Double(5), Double.class);
        testData.put(new Double(-5), Double.class);

        testData.put("0.0", Double.class);
        testData.put("+0.0", Double.class);
        testData.put("-0.0", Double.class);
        testData.put(".0", Double.class);
        testData.put("+.0", Double.class);
        testData.put("-0.0", Double.class);
        testData.put("5.0", Double.class);
        testData.put("+5.0", Double.class);
        testData.put("-5.0", Double.class);

        testData.put("9223372036854775808", BigInteger.class); // max long + 1
        testData.put("-9223372036854775809", BigInteger.class); // min long - 1
        testData.put(Integer.valueOf(5), Integer.class);

        testData.put(new Float(5), Float.class);
        testData.put(new Float(-5), Float.class);
        testData.put(new Byte((byte) -5), Byte.class);

        return testData;
    }

    @Test
    public void testGetNumericType() {
        Map<Object, Class<?>> input2ExpectedMap = getInput2ExpectedMap();

        for (Map.Entry<Object, Class<?>> testCaseData : input2ExpectedMap.entrySet()) {

            Class<?> actual = new TestableAbstractTextDataSourceDefinition().getNumericType(testCaseData.getKey(), null);

            String testCaseDescription = "input:" + String.valueOf(testCaseData.getKey());
            Assert.assertEquals(testCaseDescription, testCaseData.getValue(), actual);
        }
    }

    @SuppressWarnings("serial")
    private static final class TestableAbstractTextDataSourceDefinition extends AbstractTextDataSourceDefinition {

        @Override
        public Map<String, Object> customizePropertyValueMap(CustomReportDataSource customReportDataSource, Map<String, Object> propertyValueMap) {
            // do nothing
            return null;
        }

        @Override
        public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customReportDataSource) throws Exception {
            // do nothing
            return null;
        }
    }
}
