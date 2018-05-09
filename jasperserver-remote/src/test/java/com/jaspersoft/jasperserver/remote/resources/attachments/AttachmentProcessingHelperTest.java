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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class AttachmentProcessingHelperTest {
    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void setDataToFileResourceWithValidation_noPart_exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final FileResourceImpl localResource = new FileResourceImpl();
        final ResourceReference fileReference = new ResourceReference(localResource);
        AttachmentProcessingHelper.setDataToFileResourceWithValidation(null, fileReference, "someName");
    }

    @Test
    public void setDataToFileResourceWithValidation_partIsNull_resourceIsNull_nothingHappens() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        Exception exception = null;
        try {
            AttachmentProcessingHelper.setDataToFileResourceWithValidation(null, null, "someName");
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void setDataToFileResourceWithValidation_partIsNull_resourceDataNotNull_nothingHappens() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        Exception exception = null;
        try {
            final FileResourceImpl fileResource = new FileResourceImpl();
            fileResource.setData("testData".getBytes());
            AttachmentProcessingHelper.setDataToFileResourceWithValidation(null, new ResourceReference(fileResource), "someName");
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void setDataToFileResourceWithValidation_partIsNull_resourceIsReference_nothingHappens() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        Exception exception = null;
        try {
            AttachmentProcessingHelper.setDataToFileResourceWithValidation(null, new ResourceReference("/some/uri"), "someName");
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void setDataToFileResourceWithValidation_partIsNull_resourceIsFileReference_nothingHappens() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        Exception exception = null;
        try {
            FileResource fileResource = new FileResourceImpl();
            fileResource.setReferenceURI("/test/uri");
            AttachmentProcessingHelper.setDataToFileResourceWithValidation(null, new ResourceReference(fileResource), "someName");
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void setDataToFileResourceWithValidation_withPart_resourceIsNull_Exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        AttachmentProcessingHelper.setDataToFileResourceWithValidation(mock(InputStream.class), null, "someName");
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void setDataToFileResourceWithValidation_withPart_resourceIsReference_Exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        AttachmentProcessingHelper.setDataToFileResourceWithValidation(mock(InputStream.class), new ResourceReference("/test/uri"), "someName");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void setDataToFileResourceWithValidation_withPart_resourceIsNotFile_Exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        AttachmentProcessingHelper.setDataToFileResourceWithValidation(mock(InputStream.class), new ResourceReference(new ContentResourceImpl()), "someName");
    }

    @Test
    public void setDataToFileResourceWithValidation_withPart_setData() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        byte[] expectedByteArray = new byte[]{1, 2, 3};
        final FileResourceImpl fileResource = new FileResourceImpl();
        AttachmentProcessingHelper.setDataToFileResourceWithValidation(new ByteArrayInputStream(expectedByteArray), new ResourceReference(fileResource), "someName");
        assertEquals(fileResource.getData(), expectedByteArray);
    }

    @Test
    public void setDataToFileResourcesList() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        byte[] expectedByteArray0 = new byte[]{1, 2, 3};
        byte[] expectedByteArray1 = new byte[]{4, 5, 6};
        byte[] expectedByteArray3 = new byte[]{7, 8, 9};
        List<ResourceReference> references = new ArrayList<ResourceReference>();
        references.add(new ResourceReference(new FileResourceImpl()));
        references.add(new ResourceReference(new FileResourceImpl()));
        references.add(new ResourceReference("/some/uri"));
        references.add(new ResourceReference(new FileResourceImpl()));
        final String prefix = "somePrefix";
        final HashMap<String, InputStream> parts = new HashMap<String, InputStream>();
        parts.put(prefix + "[0]", new ByteArrayInputStream(expectedByteArray0));
        parts.put(prefix + "[1]", new ByteArrayInputStream(expectedByteArray1));
        parts.put(prefix + "[3]", new ByteArrayInputStream(expectedByteArray3));
        AttachmentProcessingHelper.setDataToFileResourcesList(references, parts, prefix);
        assertEquals(references.size(), 4);
        assertTrue(references.get(0).getLocalResource() instanceof FileResource);
        assertEquals(((FileResource) references.get(0).getLocalResource()).getData(), expectedByteArray0);
        assertTrue(references.get(1).getLocalResource() instanceof FileResource);
        assertEquals(((FileResource) references.get(1).getLocalResource()).getData(), expectedByteArray1);
        assertFalse(references.get(2).isLocal());
        assertTrue(references.get(3).getLocalResource() instanceof FileResource);
        assertEquals(((FileResource) references.get(3).getLocalResource()).getData(), expectedByteArray3);
    }

    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void setDataToFileResourcesList_partsIsNull_exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        List<ResourceReference> references = new ArrayList<ResourceReference>();
        references.add(new ResourceReference(new FileResourceImpl()));
        AttachmentProcessingHelper.setDataToFileResourcesList(references, null, "somePrefix");
    }

    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void setDataToFileResourcesList_partsIsEmpty_exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        List<ResourceReference> references = new ArrayList<ResourceReference>();
        references.add(new ResourceReference(new FileResourceImpl()));
        AttachmentProcessingHelper.setDataToFileResourcesList(references, new HashMap<String, InputStream>(), "somePrefix");
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void setDataToFileResourcesList_partExists_resourceIsReference_exception() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        List<ResourceReference> references = new ArrayList<ResourceReference>();
        references.add(new ResourceReference("/some/uri"));
        final String prefix = "somePrefix";
        final HashMap<String, InputStream> parts = new HashMap<String, InputStream>();
        parts.put(prefix + "[0]", mock(InputStream.class));
        AttachmentProcessingHelper.setDataToFileResourcesList(references, parts, prefix);
    }
}
