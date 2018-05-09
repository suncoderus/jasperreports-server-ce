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
package com.jaspersoft.jasperserver.remote.resources;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ClientTypeHelperTest {

    public static final String NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION = "nameFromXmlRootElementAnnotation";
    public static final String NAME_FROM_XML_TYPE_ANNOTATION = "nameFromXmlTypeAnnotation";

    @Test
    public void extractClientType_XmlRootElementAnnotation() {
        assertEquals(NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION, ClientTypeHelper.extractClientType(TestClientObjectWithXmlRootAnnotation.class));
    }

    @Test
    public void extractClientType_XmlTypeAnnotation() {
        assertEquals(NAME_FROM_XML_TYPE_ANNOTATION, ClientTypeHelper.extractClientType(TestClientObjectWithXmlTypeAnnotation.class));
    }

    @Test
    public void extractClientType_NoAnnotation() {
        assertEquals("testClientObjectWithoutAnnotation", ClientTypeHelper.extractClientType(TestClientObjectWithoutAnnotation.class));
    }

    @Test
    public void getClientClass_clientTypeFromXmlRootAnnotation(){
        final ClientTypeHelper<TestClientObjectWithXmlRootAnnotation> clientTypeHelper =
                new ClientTypeHelper<TestClientObjectWithXmlRootAnnotation>(
                        new TestToClientConverterExtension<TestClientObjectWithXmlRootAnnotation>() {});
        assertEquals(clientTypeHelper.getClientResourceType(), NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION);
    }

    @Test
    public void getClientClass_clientTypeFromXmlTypeAnnotation(){
        final ClientTypeHelper<TestClientObjectWithXmlTypeAnnotation> clientTypeHelper =
                new ClientTypeHelper<TestClientObjectWithXmlTypeAnnotation>(
                        new TestToClientConverterExtension<TestClientObjectWithXmlTypeAnnotation>() {});
        assertEquals(clientTypeHelper.getClientResourceType(), NAME_FROM_XML_TYPE_ANNOTATION);
    }

    @Test
    public void getClientClass_clientTypeFromClassName(){
        final ClientTypeHelper<TestClientObjectWithoutAnnotation> clientTypeHelper =
                new ClientTypeHelper<TestClientObjectWithoutAnnotation>(
                        new TestToClientConverterExtension<TestClientObjectWithoutAnnotation>() {});
        assertEquals(clientTypeHelper.getClientResourceType(), "testClientObjectWithoutAnnotation");
    }

    @XmlRootElement(name = NAME_FROM_XML_ROOT_ELEMENT_ANNOTATION)
    private static class TestClientObjectWithXmlRootAnnotation {
    }

    @XmlType(name = NAME_FROM_XML_TYPE_ANNOTATION)
    private static class TestClientObjectWithXmlTypeAnnotation {
    }

    private static class TestClientObjectWithoutAnnotation {
    }

    private static class TestToClientConverterExtension<T> implements ToClientConverter<Object, T, Object>{
        @Override
        public T toClient(Object serverObject, Object options) {
            return null;
        }

        @Override
        public String getClientResourceType() {
            return null;
        }
    }
}
