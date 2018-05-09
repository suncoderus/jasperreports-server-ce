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
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class AttachmentProcessingHelper {
    /**
     * Use this helper method to set file data from binary stream.
     *
     * @param inputStream   - file binary data stream;
     * @param fileReference - resource reference to target file resource;
     * @param partName      - name of the file data part, used in exception messages;
     * @throws IllegalParameterValueException in case if file reference is null (it can happen if resource descriptor
     *                                        contains no description of this file resource)
     * @throws MandatoryParameterNotFoundException
     *                                        in case if ResourceReference isn't local resource.
     *                                        It's not allowed to set data to referenced file resource.
     */
    public static void setDataToFileResourceWithValidation(InputStream inputStream, ResourceReference fileReference, String partName)
            throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        if((fileReference == null || !fileReference.isLocal()
                || (fileReference.isLocal() && fileReference.getLocalResource() instanceof FileResource
                && (((FileResource) fileReference.getLocalResource()).isReference()
                || ((FileResource) fileReference.getLocalResource()).getData() != null)))
                && inputStream == null){

            // no file or file is reference or file data are not empty (already set) and no attachment. Do nothing.
            return;
        }
        if (fileReference == null) {
            throw new MandatoryParameterNotFoundException(partName);
        }
        if (!fileReference.isLocal() || (fileReference.isLocal() && fileReference.getLocalResource() instanceof FileResource
                && ((FileResource) fileReference.getLocalResource()).isReference())) {
            throw new IllegalParameterValueException(partName, partName + "Reference");
        }
        if (!(fileReference.getLocalResource() instanceof FileResource)) {
            throw new IllegalStateException(partName + " resource should be of type FileResource");
        }
        if (inputStream == null) {
            throw new MandatoryParameterNotFoundException("Multipart item " + partName);
        }
        final FileResource file = (FileResource) fileReference.getLocalResource();
        try {
            file.setData(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeException("Can't read attachment " + partName, e);
        }
    }

    public static void setDataToFileResourcesList(List<ResourceReference> references, Map<String, InputStream> parts, String partNamePrefix)
            throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        if (references != null) {
            for (int i = 0; i < references.size(); i++) {
                final ResourceReference currentReference = references.get(i);
                final String attachmentName = String.format(partNamePrefix + "[%d]", i);
                if (currentReference.isLocal() || parts.get(attachmentName) != null) {
                    setDataToFileResourceWithValidation(parts != null ? parts.get(attachmentName) : null, currentReference, attachmentName);
                }
            }
        }
    }
}
