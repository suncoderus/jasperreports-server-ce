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
package com.jaspersoft.jasperserver.remote.resources.attachments;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.MondrianConnectionImpl;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MondrianConnectionAttachmentsProcessorTest {
    private MondrianConnectionAttachmentsProcessor processor = new MondrianConnectionAttachmentsProcessor();
    @Test
    public void processAttachments_withSchemaPart_setData() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final MondrianConnectionImpl serverObject = new MondrianConnectionImpl();
        serverObject.setSchemaReference("/test/reference");
        final FileResourceImpl localResource = new FileResourceImpl();
        serverObject.getSchema().setLocalResource(localResource);
        final Map<String, InputStream> parts = new HashMap<String, InputStream>();
        byte[] expectedByteArray = new byte[]{1,2,3};
        parts.put("schema", new ByteArrayInputStream(expectedByteArray));
        final Resource result = processor.processAttachments(serverObject, parts);
        assertTrue(result instanceof MondrianConnection);
        MondrianConnection resultConnection = (MondrianConnection) result;
        assertNotNull(resultConnection.getSchema());
        assertTrue(resultConnection.getSchema().getLocalResource() instanceof FileResource);
        assertEquals(((FileResource) resultConnection.getSchema().getLocalResource()).getData(), expectedByteArray);
    }
}
