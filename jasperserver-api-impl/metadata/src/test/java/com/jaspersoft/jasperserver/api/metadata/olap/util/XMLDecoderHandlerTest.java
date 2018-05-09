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

package com.jaspersoft.jasperserver.api.metadata.olap.util;

import org.junit.Test;

import javax.xml.parsers.SAXParserFactory;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author askorodumov
 * @version $Id$
 */
public class XMLDecoderHandlerTest {

    @Test
    public void parse_withCustomParser_success() throws Exception {
        XMLDecoderHandler handler = new XMLDecoderHandler();
        InputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream("target/test-classes/View.options"));
            SAXParserFactory.newInstance().newSAXParser().parse(stream, handler);

            checkParsedObject(handler.getResult());
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @Test
    public void parse_withStandardXMLDecoder_success() throws Exception {
        InputStream stream = null;
        XMLDecoder decoder = null;
        try {
            stream = new BufferedInputStream(new FileInputStream("target/test-classes/View.options"));
            decoder = new XMLDecoder(stream);

            checkParsedObject(decoder.readObject());
        } finally {
            if (decoder != null) {
                decoder.close();
            }
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void checkParsedObject(Object object) {
        assertTrue(object instanceof HashMap);
        HashMap stateMap = (HashMap) object;

        Object o;
        o = stateMap.get("/organizations/organization_1/supermart/revenueAndProfit/RevenueByStoreView/printform");
        assertTrue(o instanceof HashMap);
        HashMap map = (HashMap) o;
        assertEquals(map.get("paperType"), "A4");
        assertEquals(map.get("paperTypeCustom"), Boolean.FALSE);
        assertEquals(map.get("pageHeight"), 29.7);

        o = stateMap.get("/organizations/organization_1/supermart/revenueAndProfit/RevenueByStoreView/table");
        assertTrue(o instanceof HashMap);
        map = (HashMap) o;
        assertEquals(map.get("slicerBuilder"), null);
        assertNotNull(map.get("rowAxisBuilder"));
        assertEquals(map.get("rowAxisBuilder").getClass(), TestClass.class);
        assertNotNull(map.get("axisStyle"));
        assertEquals(map.get("axisStyle").getClass(), TestClass.class);
        assertEquals(((TestClass) map.get("axisStyle")).isBooleanField1(), Boolean.TRUE);
        assertTrue(((TestClass) map.get("axisStyle")).isBooleanField2());

        o = stateMap.get("/organizations/organization_1/supermart/revenueAndProfit/RevenueByStoreView/displayform");
        assertTrue(o instanceof HashMap);
        map = (HashMap) o;
        assertEquals(map.get("extensions(sortRank).topBottomCount"), 10);

        o = stateMap.get("/organizations/organization_1/supermart/revenueAndProfit/RevenueByStoreView/toolbar");
        assertTrue(o instanceof HashMap);
        map = (HashMap) o;
        assertEquals(map.get(null), Boolean.TRUE);

        assertEquals(stateMap.get("drillThruSQL"), "    ");

        assertEquals(((TestClass) stateMap.get("tryConstructorWithSingleArgument")).getStringField1(), "argument value");

        assertEquals(((TestClass) stateMap.get("tryPutValueToMethod")).getStringField2(), "New value");

        assertEquals(((TestClass) stateMap.get("tryPutNullValueToMethod")).getStringField2(), null);

        // tryConstructorWithNullArgument
        assertEquals(((TestClass) stateMap.get("tryConstructorWithNullArgument")).getStringField1(), null);

        // tryPutVarargValueToMethod
        TestClass tested = (TestClass) stateMap.get("tryPutVarargValueToMethod");
        assertNotNull(tested);
        assertEquals(tested.isBooleanField2(), true);
        assertEquals(tested.getIntField(), 111);
        assertEquals(tested.getStringList().size(), 3);
        assertTrue(tested.getStringList().contains("string value 1"));
        assertTrue(tested.getStringList().contains("string value 2"));
        assertTrue(tested.getStringList().contains("string value 3"));

        // tryConstructorWithseparateArguments
        tested = (TestClass) stateMap.get("tryConstructorWithseparateArguments");
        assertNotNull(tested);
        assertEquals(tested.isBooleanField1(), true);
        assertEquals(tested.getIntField(), 111);
        assertEquals(tested.getStringField1(), "string value 4");

        // tryArrayListWithEvenObjects
        List list = (List) stateMap.get("tryArrayListWithEvenObjects");
        assertNotNull(list);
        assertEquals(list.size(), 3);
        int hash = System.identityHashCode(list.get(0));
        assertEquals(System.identityHashCode(list.get(1)), hash);
        assertEquals(System.identityHashCode(list.get(2)), hash);

        // tryArrayWithEvenObjects
        Object[] array = (Object[]) stateMap.get("tryArrayWithEvenObjects");
        assertNotNull(array);
        assertEquals(array.length, 3);
        hash = System.identityHashCode(array[0]);
        assertEquals(System.identityHashCode(array[1]), hash);
        assertEquals(System.identityHashCode(array[2]), hash);

        // tryArrayWithOddObjectsAndNull
        array = (Object[]) stateMap.get("tryArrayWithOddObjectsAndNull");
        assertNotNull(array);
        assertEquals(array.length, 3);
        assertEquals(((TestClass) array[0]).getStringField1(), "text value 1");
        assertEquals(array[1], null);
        assertEquals(((TestClass) array[2]).getStringField1(), "text value 2");

        // tryStaticFactoryMethod
        assertTrue(stateMap.get("tryStaticFactoryMethod") instanceof TestClassWithPrivateConstructor);

        // tryStaticMethod
        tested = (TestClass) stateMap.get("tryStaticMethod");
        assertNotNull(tested);
        assertEquals(tested.isBooleanField1(), true);
        assertEquals(tested.getIntField(), 22);

        // tryPrimitiveTypes
        o = stateMap.get("tryPrimitiveTypes");
        assertTrue(o instanceof HashMap);
        map = (HashMap) o;
        assertTrue(map.containsKey("null"));
        assertEquals(map.get("null"), null);
        assertEquals(map.get("string"), "string value");
        assertEquals(map.get("char"), 'R');
        assertEquals(map.get("byte"), (byte)111);
        assertEquals(map.get("short"), (short)1111);
        assertEquals(map.get("int"), 11111);
        assertEquals(map.get("long"), 1111111L);
        assertEquals(map.get("float"), 11.1111f);
        assertEquals(map.get("double"), 1111.1111);
        assertEquals(map.get("boolean"), true);

        int[] ints = (int[]) stateMap.get("tryArrayOfPrimitiveIntegers");
        assertNotNull(ints);
        assertEquals(ints.length, 4);
        assertEquals(ints[0], 1);
        assertEquals(ints[1], 1);
        assertEquals(ints[2], 2);
        assertEquals(ints[3], 2);
    }

    public static class TestClassWithPrivateConstructor {
        private TestClassWithPrivateConstructor() {
        }

        public static TestClassWithPrivateConstructor newInstance() {
            return new TestClassWithPrivateConstructor();
        }
    }

    public static class TestClass {
        private boolean booleanField1 = false;
        private boolean booleanField2 = false;
        private int intField = 0;
        private String stringField1 = "An empty value";
        private String stringField2 = "An empty value";
        private List<String> stringList = new ArrayList<String>();

        public TestClass() {
        }

        public TestClass(String stringField1) {
            this.stringField1 = stringField1;
        }

        public TestClass(boolean booleanField1, int intField) {
            this.booleanField1 = booleanField1;
            this.intField = intField;
        }

        public void setBoolTrue() {
            booleanField2 = true;
        }

        public boolean isBooleanField1() {
            return booleanField1;
        }

        public void setBooleanField1(boolean booleanField1) {
            this.booleanField1 = booleanField1;
        }

        public boolean isBooleanField2() {
            return booleanField2;
        }

        public void setBooleanField2(boolean booleanField2) {
            this.booleanField2 = booleanField2;
        }

        public String getStringField1() {
            return stringField1;
        }

        public void setStringField1(String stringField1) {
            this.stringField1 = stringField1;
        }

        public String getStringField2() {
            return stringField2;
        }

        public void setStringField2(String stringField2) {
            this.stringField2 = stringField2;
        }

        public void setBoolInt(boolean booleanField2, int intField) {
            this.booleanField2 = booleanField2;
            this.intField = intField;
        }

        public void addStringToList(String s) {
            stringList.add(s);
        }

        public List<String> getStringList() {
            return stringList;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }
    }
}
